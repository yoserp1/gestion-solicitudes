FROM node:24-alpine AS build

WORKDIR /app
COPY frontend/package*.json ./
COPY frontend/analytics-remote/package.json ./analytics-remote/package.json
RUN npm ci
COPY frontend/ ./
ENV ANALYTICS_REMOTE_URL=/analytics-remote/remoteEntry.js
RUN npm run build

FROM nginx:1.29-alpine

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
COPY --from=build /app/analytics-remote/dist /usr/share/nginx/html/analytics-remote
EXPOSE 80
