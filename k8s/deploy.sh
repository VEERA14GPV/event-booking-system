#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# deploy.sh — Build, push, and deploy the Event Booking System to Kubernetes.
#
# Usage:
#   ./k8s/deploy.sh [REGISTRY] [TAG]
#
# Examples:
#   ./k8s/deploy.sh docker.io/myuser 1.2.0
#   ./k8s/deploy.sh ghcr.io/myorg   latest
#   ./k8s/deploy.sh 123456789.dkr.ecr.us-east-1.amazonaws.com latest
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

# ── Configurable defaults ─────────────────────────────────────────────────────
REGISTRY="${1:-docker.io/yourname}"
TAG="${2:-latest}"
NAMESPACE="event-booking"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

BACKEND_IMAGE="${REGISTRY}/event-booking-backend:${TAG}"
FRONTEND_IMAGE="${REGISTRY}/event-booking-frontend:${TAG}"

# ── Colour helpers ────────────────────────────────────────────────────────────
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
info()    { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

# ─────────────────────────────────────────────────────────────────────────────
# STEP 1 — Pre-flight checks
# ─────────────────────────────────────────────────────────────────────────────
info "Pre-flight checks…"
command -v docker  >/dev/null 2>&1 || error "docker is not installed."
command -v kubectl >/dev/null 2>&1 || error "kubectl is not installed."
kubectl cluster-info >/dev/null 2>&1  || error "kubectl cannot reach the cluster. Check your kubeconfig."

# ─────────────────────────────────────────────────────────────────────────────
# STEP 2 — Build Docker images
# ─────────────────────────────────────────────────────────────────────────────
info "Building backend image: ${BACKEND_IMAGE}"
docker build \
  --platform linux/amd64 \
  -t "${BACKEND_IMAGE}" \
  "${PROJECT_ROOT}"

info "Building frontend image: ${FRONTEND_IMAGE}"
docker build \
  --platform linux/amd64 \
  --build-arg NGINX_CONF=nginx-k8s.conf \
  --build-arg VITE_API_BASE_URL="" \
  --build-arg VITE_WS_URL="/socket" \
  -t "${FRONTEND_IMAGE}" \
  "${PROJECT_ROOT}/frontend"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 3 — Push to registry
# ─────────────────────────────────────────────────────────────────────────────
info "Pushing ${BACKEND_IMAGE}…"
docker push "${BACKEND_IMAGE}"

info "Pushing ${FRONTEND_IMAGE}…"
docker push "${FRONTEND_IMAGE}"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 4 — Patch image references in manifests and apply
# ─────────────────────────────────────────────────────────────────────────────
info "Applying K8s manifests…"

# Namespace first
kubectl apply -f "${SCRIPT_DIR}/namespace.yaml"

# Secrets (skip if already exist — do not overwrite real secrets)
kubectl apply -f "${SCRIPT_DIR}/secrets/"        --namespace="${NAMESPACE}"

# ConfigMaps
kubectl apply -f "${SCRIPT_DIR}/configmaps/"     --namespace="${NAMESPACE}"

# Infrastructure (order: MySQL → Redis → ES — backend waits for all three)
kubectl apply -f "${SCRIPT_DIR}/mysql/"          --namespace="${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/redis/"          --namespace="${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/elasticsearch/"  --namespace="${NAMESPACE}"

# Patch the image in backend and frontend Deployments
kubectl set image deployment/backend  backend="${BACKEND_IMAGE}"  \
  --namespace="${NAMESPACE}" 2>/dev/null || true
kubectl set image deployment/frontend frontend="${FRONTEND_IMAGE}" \
  --namespace="${NAMESPACE}" 2>/dev/null || true

# Apply backend and frontend manifests (replaces entire objects on first run)
kubectl apply -f "${SCRIPT_DIR}/backend/"        --namespace="${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/frontend/"       --namespace="${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/ingress/"        --namespace="${NAMESPACE}"

# Update image again after apply (apply may reset it on first run)
kubectl set image deployment/backend  backend="${BACKEND_IMAGE}"  \
  --namespace="${NAMESPACE}"
kubectl set image deployment/frontend frontend="${FRONTEND_IMAGE}" \
  --namespace="${NAMESPACE}"

# ─────────────────────────────────────────────────────────────────────────────
# STEP 5 — Wait for rollout
# ─────────────────────────────────────────────────────────────────────────────
info "Waiting for infrastructure to become ready (up to 5 min)…"
kubectl rollout status statefulset/mysql          --namespace="${NAMESPACE}" --timeout=300s || warn "MySQL rollout timed out"
kubectl rollout status statefulset/redis          --namespace="${NAMESPACE}" --timeout=120s || warn "Redis rollout timed out"
kubectl rollout status statefulset/elasticsearch  --namespace="${NAMESPACE}" --timeout=300s || warn "Elasticsearch rollout timed out"

info "Waiting for application to become ready (up to 5 min)…"
kubectl rollout status deployment/backend         --namespace="${NAMESPACE}" --timeout=300s
kubectl rollout status deployment/frontend        --namespace="${NAMESPACE}" --timeout=120s

# ─────────────────────────────────────────────────────────────────────────────
# STEP 6 — Print summary
# ─────────────────────────────────────────────────────────────────────────────
info "Deployment complete!"
echo ""
kubectl get pods --namespace="${NAMESPACE}"
echo ""
INGRESS_IP=$(kubectl get ingress event-booking-ingress --namespace="${NAMESPACE}" \
  -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "<pending>")
info "Ingress IP: ${INGRESS_IP}"
info "Access the app at: http://booking.example.com  (update /etc/hosts if testing locally)"
