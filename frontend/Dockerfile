## ============================================================
## STAGE 1: BUILD
## ============================================================

FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
# ci installs exactly what package-lock.json declares — reproducible.
RUN npm ci --silent

COPY . .

# VITE_API_BASE_URL  — empty = same-origin (works behind any reverse proxy).
#                     For cross-origin deployments supply the full URL.
# VITE_WS_URL        — WebSocket endpoint path, must match
#                      spring.websocket.path in application.properties.
ARG VITE_API_BASE_URL=""
ARG VITE_WS_URL="/socket"
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_WS_URL=$VITE_WS_URL

RUN npm run build

## ============================================================
## STAGE 2: RUNTIME (nginx)
## ============================================================

FROM nginx:1.27-alpine

LABEL org.opencontainers.image.title="event-booking-frontend" \
      org.opencontainers.image.description="React Event Booking UI served by nginx"

# NGINX_CONF controls which server block to use:
#   nginx.conf      — Docker Compose: nginx also proxies /api paths to backend.
#   nginx-k8s.conf  — Kubernetes: Ingress handles routing; nginx only serves SPA.
ARG NGINX_CONF=nginx.conf

COPY --from=build /app/dist /usr/share/nginx/html
COPY ${NGINX_CONF} /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
