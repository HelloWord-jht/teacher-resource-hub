import request from './http';

export const webApi = {
  getHome() {
    return request.get('/api/web/home');
  },
  getCategories() {
    return request.get('/api/web/categories');
  },
  getTags() {
    return request.get('/api/web/tags');
  },
  getResources(params) {
    return request.get('/api/web/resources', { params });
  },
  getResourceDetail(id) {
    return request.get(`/api/web/resources/${id}`);
  },
  createLead(data) {
    return request.post('/api/web/leads', data);
  },
  getPage(pageCode) {
    return request.get(`/api/web/pages/${pageCode}`);
  },
};
