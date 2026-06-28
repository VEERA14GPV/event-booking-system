import axiosClient from '../utils/axiosClient';

export function getAllShows() {
  return axiosClient.get('/shows').then((res) => res.data);
}

export function getShowsByEvent(eventId) {
  return axiosClient.get(`/shows/event/${eventId}`).then((res) => res.data);
}

export function getShowById(showId) {
  return axiosClient.get(`/shows/${showId}`).then((res) => res.data);
}

export function createShow(payload) {
  return axiosClient.post('/shows', payload).then((res) => res.data);
}

export function createShows(payloadArray) {
  return axiosClient.post('/shows/bulk', payloadArray).then((res) => res.data);
}
