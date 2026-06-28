import axiosClient from '../utils/axiosClient';

// Spring Boot Actuator — management.endpoints.web.exposure.include=
// health,info,metrics,circuitbreakers,circuitbreakerevents in
// application.properties. Default base path (/actuator), not customized.
// These endpoints are NOT in SecurityConfig's permitAll list, so they
// still require a valid JWT like everything else (anyRequest().authenticated()).
// Used by AdminDashboard.jsx for the "system health" view.

export function getHealth() {
  return axiosClient.get('/actuator/health').then((res) => res.data);
}

export function getCircuitBreakers() {
  return axiosClient.get('/actuator/circuitbreakers').then((res) => res.data);
}

export function getMetric(metricName) {
  return axiosClient.get(`/actuator/metrics/${metricName}`).then((res) => res.data);
}
