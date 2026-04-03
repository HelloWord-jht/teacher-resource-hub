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
  getResourcePreviewFiles(resourceId) {
    return request.get(`/api/web/resources/${resourceId}/preview-files`);
  },
  getResourcePreviewPages(resourceId, fileId) {
    return request.get(`/api/web/resources/${resourceId}/preview-files/${fileId}/previews`);
  },
  getResourceByCode(resourceCode) {
    return request.get(`/api/web/resource-by-code/${resourceCode}`);
  },
  createLead(data) {
    return request.post('/api/web/leads', data);
  },
  createVisitTrace(data) {
    return request.post('/api/web/visit-trace', data);
  },
  getPage(pageCode) {
    return request.get(`/api/web/pages/${pageCode}`);
  },
};
