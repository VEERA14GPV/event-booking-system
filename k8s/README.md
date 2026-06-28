# Event Booking System — Kubernetes Deployment Guide

## Architecture

```
Internet
   │
   ▼ :80 / :443
┌─────────────────────────────────────────────────────────┐
│  NGINX Ingress Controller (LoadBalancer Service)        │
│                                                         │
│  booking.example.com/           → frontend:80  (SPA)   │
│  booking.example.com/auth/**    → backend:8082          │
│  booking.example.com/events/**  → backend:8082          │
│  booking.example.com/shows/**   → backend:8082          │
│  booking.example.com/seats/**   → backend:8082          │
│  booking.example.com/bookings/** → backend:8082         │
│  booking.example.com/payments/** → backend:8082         │
│  booking.example.com/socket/**  → backend:8082 (WS)    │
└─────────────────────────────────────────────────────────┘
          │                        │
          ▼                        ▼
  ┌──────────────┐        ┌──────────────────┐
  │  frontend    │        │     backend       │
  │  Deployment  │        │    Deployment     │
  │  (2 replicas)│        │   (1 replica*)    │
  │  nginx SPA   │        │  Spring Boot 3    │
  └──────────────┘        └──────────────────┘
                                   │
          ┌────────────────────────┼──────────────────┐
          ▼                        ▼                   ▼
  ┌──────────────┐        ┌──────────────┐   ┌──────────────┐
  │    mysql     │        │    redis     │   │elasticsearch │
  │ StatefulSet  │        │ StatefulSet  │   │ StatefulSet  │
  │  (10Gi PVC)  │        │  (5Gi PVC)  │   │ (15Gi PVC)   │
  └──────────────┘        └──────────────┘   └──────────────┘
```

> *WebSocket (STOMP/SockJS) uses an **in-memory** broker. Multiple backend replicas
> would break real-time seat updates because messages broadcast on pod A are not
> visible to clients connected to pod B. Scale to 2+ replicas only after adding a
> Redis/RabbitMQ STOMP broker relay.*

---

## Prerequisites

| Tool | Minimum Version | Install |
|------|----------------|---------|
| Docker | 24+ | https://docs.docker.com/get-docker/ |
| kubectl | 1.28+ | https://kubernetes.io/docs/tasks/tools/ |
| Kubernetes cluster | 1.28+ | minikube / kind / EKS / GKE / AKS |
| NGINX Ingress Controller | 1.10+ | See below |

### Install NGINX Ingress Controller

```bash
# Cloud (EKS, GKE, AKS, DigitalOcean)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.11.0/deploy/static/provider/cloud/deploy.yaml

# minikube
minikube addons enable ingress

# kind
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.11.0/deploy/static/provider/kind/deploy.yaml
```

---

## Quick Start

### 1. Set up secrets (REQUIRED before any deployment)

```bash
# Generate a secure JWT secret
JWT_SECRET=$(openssl rand -base64 48 | tr -d '\n')

# Edit the Secret files — replace placeholder base64 values:
# k8s/secrets/mysql-secret.yaml
# k8s/secrets/app-secret.yaml

# Encode your values:
echo -n "your-db-password"  | base64   # → mysql-password
echo -n "$JWT_SECRET"        | base64   # → jwt-secret
echo -n "rzp_live_XXXX"      | base64   # → razorpay-key-id
echo -n "your-razorpay-sec"  | base64   # → razorpay-key-secret

# Apply secrets first (not in deploy.sh to avoid overwriting)
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets/ --namespace=event-booking
```

### 2. Build and deploy

```bash
chmod +x k8s/deploy.sh
./k8s/deploy.sh docker.io/yourname 1.0.0
```

### 3. Access the application

```bash
# Get the Ingress IP
kubectl get ingress event-booking-ingress -n event-booking

# For minikube:
minikube tunnel          # in a separate terminal
# Then visit http://localhost (update /etc/hosts if using a domain)

# For cloud clusters, the EXTERNAL-IP appears after ~2 minutes:
kubectl get svc -n ingress-nginx
```

---

## Step-by-Step Manual Deployment

### Step 1 — Build Docker images

```bash
# Backend
docker build \
  --platform linux/amd64 \
  -t docker.io/yourname/event-booking-backend:1.0.0 \
  .

# Frontend (K8s build — uses nginx-k8s.conf, no API proxy)
docker build \
  --platform linux/amd64 \
  --build-arg NGINX_CONF=nginx-k8s.conf \
  --build-arg VITE_API_BASE_URL="" \
  --build-arg VITE_WS_URL="/socket" \
  -t docker.io/yourname/event-booking-frontend:1.0.0 \
  ./frontend
```

### Step 2 — Push to registry

```bash
docker push docker.io/yourname/event-booking-backend:1.0.0
docker push docker.io/yourname/event-booking-frontend:1.0.0
```

### Step 3 — Update image references in manifests

```bash
# Replace YOUR_REGISTRY placeholder in Deployment manifests:
sed -i 's|YOUR_REGISTRY/event-booking-backend:latest|docker.io/yourname/event-booking-backend:1.0.0|' \
  k8s/backend/backend-deployment.yaml

sed -i 's|YOUR_REGISTRY/event-booking-frontend:latest|docker.io/yourname/event-booking-frontend:1.0.0|' \
  k8s/frontend/frontend-deployment.yaml
```

### Step 4 — Apply manifests in order

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets/           -n event-booking
kubectl apply -f k8s/configmaps/        -n event-booking
kubectl apply -f k8s/mysql/             -n event-booking
kubectl apply -f k8s/redis/             -n event-booking
kubectl apply -f k8s/elasticsearch/     -n event-booking

# Wait for databases to be ready before starting the app
kubectl rollout status statefulset/mysql         -n event-booking --timeout=300s
kubectl rollout status statefulset/redis         -n event-booking --timeout=120s
kubectl rollout status statefulset/elasticsearch -n event-booking --timeout=300s

kubectl apply -f k8s/backend/           -n event-booking
kubectl apply -f k8s/frontend/          -n event-booking
kubectl apply -f k8s/ingress/           -n event-booking
```

---

## Verify the Deployment

```bash
# All pods should be Running
kubectl get pods -n event-booking

# Expected output:
# NAME                        READY   STATUS    RESTARTS
# backend-xxxxxxxxx-xxxxx     1/1     Running   0
# elasticsearch-0             1/1     Running   0
# frontend-xxxxxxxxx-xxxxx    1/1     Running   0
# frontend-xxxxxxxxx-yyyyy    1/1     Running   0
# mysql-0                     1/1     Running   0
# redis-0                     1/1     Running   0

# Check services
kubectl get svc -n event-booking

# Check Ingress
kubectl get ingress -n event-booking

# Check HPA
kubectl get hpa -n event-booking

# Backend health
kubectl exec -n event-booking deploy/backend -- \
  curl -sf http://localhost:8082/actuator/health | python3 -m json.tool

# Backend readiness
kubectl exec -n event-booking deploy/backend -- \
  curl -sf http://localhost:8082/actuator/health/readiness

# Test an API endpoint through the Ingress
curl -s http://booking.example.com/events | python3 -m json.tool
```

---

## Update / Rolling Deployment

```bash
# Build and push new image
docker build --platform linux/amd64 -t docker.io/yourname/event-booking-backend:1.1.0 .
docker push docker.io/yourname/event-booking-backend:1.1.0

# Rolling update — zero downtime (maxUnavailable: 0)
kubectl set image deployment/backend backend=docker.io/yourname/event-booking-backend:1.1.0 \
  -n event-booking

# Watch the rollout
kubectl rollout status deployment/backend -n event-booking

# Roll back if something goes wrong
kubectl rollout undo deployment/backend -n event-booking
```

---

## Troubleshooting

### Pod stuck in Pending

```bash
kubectl describe pod <pod-name> -n event-booking
# Look for: Insufficient CPU/memory → increase node size or lower resource requests
# Look for: 0/N nodes available → check node taints, tolerations, affinity
```

### Pod stuck in CrashLoopBackOff

```bash
# View logs from the crashing container
kubectl logs <pod-name> -n event-booking --previous

# Describe to see the last exit code and reason
kubectl describe pod <pod-name> -n event-booking

# For the backend, common causes:
# 1. Cannot connect to MySQL  → check DB_HOST, DB_PASSWORD env vars
# 2. Cannot connect to Redis  → check SPRING_DATA_REDIS_HOST
# 3. JWT_SECRET not set       → check app-secret Secret
# 4. OOM killed               → increase memory limit in backend-deployment.yaml
```

### Backend cannot reach MySQL

```bash
# Verify MySQL pod is Running
kubectl get pods -n event-booking -l app=mysql

# Check MySQL logs
kubectl logs statefulset/mysql -n event-booking

# Test connectivity from backend pod
kubectl exec -n event-booking deploy/backend -- \
  sh -c "nc -zv mysql 3306 && echo 'OK' || echo 'FAILED'"

# Verify Secret is correctly base64-decoded
kubectl get secret mysql-secret -n event-booking -o jsonpath='{.data.mysql-password}' | base64 -d
```

### WebSocket not connecting

```bash
# 1. Check backend pod logs for WebSocket CONNECT frames
kubectl logs deploy/backend -n event-booking | grep -i "websocket\|socket\|stomp"

# 2. Verify the /socket path reaches the backend through Ingress
curl -v -H "Upgrade: websocket" -H "Connection: Upgrade" \
  http://booking.example.com/socket/info

# 3. Check nginx-ingress controller logs
kubectl logs -n ingress-nginx deploy/ingress-nginx-controller | tail -50

# 4. Confirm proxy-read-timeout is set on the Ingress
kubectl get ingress event-booking-ingress -n event-booking -o yaml | \
  grep -A5 annotations

# 5. If SockJS falls back to XHR polling (not WS), it still works but real-time
#    updates will have ~1s polling latency instead of true push. This is normal
#    in environments that strip WebSocket upgrade headers.
```

### Ingress not getting an external IP

```bash
# For cloud providers, wait ~2 minutes for the cloud LB to provision
kubectl get svc -n ingress-nginx -w

# For minikube, run in a separate terminal:
minikube tunnel

# For kind, verify kind-specific deploy was used:
# kubectl apply -f https://...ingress-nginx/deploy/static/provider/kind/deploy.yaml
```

### Elasticsearch pod not starting (vm.max_map_count)

```bash
# The init container sets this — check if it succeeded
kubectl describe pod elasticsearch-0 -n event-booking | grep -A5 "init-container\|Init Containers"

# On the worker node directly (requires SSH to node):
sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" >> /etc/sysctl.conf

# Verify after pod restart
kubectl exec statefulset/elasticsearch -n event-booking -- \
  sysctl vm.max_map_count
```

### Out of memory / OOM killed

```bash
# Check if pod was OOM killed
kubectl describe pod <pod-name> -n event-booking | grep -i "OOM\|Killed\|LastState"

# Increase backend memory limit in backend-deployment.yaml:
#   limits.memory: "2Gi"
# Then apply:
kubectl apply -f k8s/backend/backend-deployment.yaml -n event-booking
```

### Check resource usage

```bash
# Requires metrics-server (usually pre-installed on cloud clusters)
kubectl top pods -n event-booking
kubectl top nodes
```

---

## TLS / HTTPS with cert-manager

```bash
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml

# Create a ClusterIssuer for Let's Encrypt
cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: you@example.com
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
      - http01:
          ingress:
            class: nginx
EOF

# Add to ingress.yaml:
#   annotations:
#     cert-manager.io/cluster-issuer: "letsencrypt-prod"
#   tls:
#     - hosts: [booking.example.com]
#       secretName: event-booking-tls
```

---

## Scaling

```bash
# Frontend can scale freely (stateless)
kubectl scale deployment/frontend --replicas=4 -n event-booking

# Backend: keep at 1 until Redis STOMP broker relay is added
# (see backend-deployment.yaml for explanation)
kubectl scale deployment/backend  --replicas=1 -n event-booking

# Trigger HPA manually (for load testing)
kubectl autoscale deployment/backend --cpu-percent=60 --min=1 --max=5 \
  -n event-booking

# Check HPA status
kubectl get hpa -n event-booking -w
```

---

## Teardown

```bash
# Delete all resources in the namespace
kubectl delete namespace event-booking

# This also deletes PVCs — your MySQL/Redis/ES data will be LOST.
# To preserve data, delete other resources first, keep the StatefulSets,
# then manually delete the namespace after backing up data.
```

---

## Environment Variables Reference

| Variable | Source | Default | Description |
|----------|--------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | ConfigMap | `prod` | Activates application-prod.properties |
| `DB_HOST` | ConfigMap | `mysql` | MySQL Service DNS name |
| `DB_PORT` | ConfigMap | `3306` | MySQL port |
| `DB_USER` | ConfigMap | `booking_user` | MySQL username |
| `DB_PASSWORD` | Secret | — | MySQL password |
| `SPRING_DATA_REDIS_HOST` | ConfigMap | `redis` | Redis Service DNS name |
| `SPRING_DATA_REDIS_PORT` | ConfigMap | `6379` | Redis port |
| `SPRING_ELASTICSEARCH_URIS` | ConfigMap | `http://elasticsearch:9200` | ES cluster URI |
| `JWT_SECRET` | Secret | — | JWT signing key (min 32 chars) |
| `JWT_EXPIRATION` | ConfigMap | `86400000` | Token expiry in ms (24h) |
| `RAZORPAY_KEY_ID` | Secret | — | Razorpay API key |
| `RAZORPAY_KEY_SECRET` | Secret | — | Razorpay API secret |

---

## File Reference

```
k8s/
├── namespace.yaml                        Namespace: event-booking
├── secrets/
│   ├── mysql-secret.yaml                 DB credentials (base64)
│   └── app-secret.yaml                   JWT + Razorpay keys (base64)
├── configmaps/
│   └── app-configmap.yaml               Non-sensitive config (Spring env vars)
├── mysql/
│   ├── mysql-statefulset.yaml            MySQL 8 with 10Gi PVC
│   └── mysql-service.yaml               Headless ClusterIP :3306
├── redis/
│   ├── redis-statefulset.yaml            Redis 7 with 5Gi PVC + AOF
│   └── redis-service.yaml               ClusterIP :6379
├── elasticsearch/
│   ├── elasticsearch-statefulset.yaml    ES 8.17 with 15Gi PVC
│   └── elasticsearch-service.yaml       ClusterIP :9200/:9300
├── backend/
│   ├── backend-deployment.yaml           Spring Boot, 1 replica, probes, HPA-ready
│   ├── backend-service.yaml              ClusterIP :8082
│   └── backend-hpa.yaml                 HPA: CPU 60%, Mem 70%, min=1 max=5
├── frontend/
│   ├── frontend-deployment.yaml          nginx SPA, 2 replicas
│   └── frontend-service.yaml             ClusterIP :80
├── ingress/
│   └── ingress.yaml                      NGINX Ingress with WebSocket annotations
├── deploy.sh                             Automated build + push + deploy script
└── README.md                             This file
```

---

## Production Checklist

- [ ] Replace ALL placeholder base64 values in `secrets/`
- [ ] Change `booking.example.com` to your real domain in `ingress.yaml`
- [ ] Replace `YOUR_REGISTRY` in Deployment manifests with your actual image registry
- [ ] Set up cert-manager for TLS (HTTPS)
- [ ] Restrict Ingress CORS to your domain (already set in annotations)
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` in prod (in application-prod.properties ✅)
- [ ] Run a database migration tool (Flyway/Liquibase) before first deploy
- [ ] Add Redis STOMP broker relay before scaling backend beyond 1 replica
- [ ] Install metrics-server for HPA to function (`kubectl top pods`)
- [ ] Set up cluster monitoring (Prometheus + Grafana)
- [ ] Configure log aggregation (ELK, Loki+Grafana, Datadog)
- [ ] Set up PVC backup policy (Velero or cloud-native snapshots)
- [ ] Never commit `secrets/` to version control — use Sealed Secrets or External Secrets
