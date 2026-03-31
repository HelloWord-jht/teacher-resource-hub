import request from './http';

export const adminApi = {
  login(data) {
    return request.post('/api/admin/auth/login', data);
  },
  getDashboard() {
    return request.get('/api/admin/dashboard', { withAuth: true });
  },
  listCategories() {
    return request.get('/api/admin/categories', { withAuth: true });
  },
  createCategory(data) {
    return request.post('/api/admin/categories', data, { withAuth: true });
  },
  updateCategory(id, data) {
    return request.put(`/api/admin/categories/${id}`, data, { withAuth: true });
  },
  updateCategoryStatus(id, data) {
    return request.put(`/api/admin/categories/${id}/status`, data, { withAuth: true });
  },
  deleteCategory(id) {
    return request.delete(`/api/admin/categories/${id}`, { withAuth: true });
  },
  listTags() {
    return request.get('/api/admin/tags', { withAuth: true });
  },
  createTag(data) {
    return request.post('/api/admin/tags', data, { withAuth: true });
  },
  updateTag(id, data) {
    return request.put(`/api/admin/tags/${id}`, data, { withAuth: true });
  },
  updateTagStatus(id, data) {
    return request.put(`/api/admin/tags/${id}/status`, data, { withAuth: true });
  },
  deleteTag(id) {
    return request.delete(`/api/admin/tags/${id}`, { withAuth: true });
  },
  listFaqs() {
    return request.get('/api/admin/faqs', { withAuth: true });
  },
  createFaq(data) {
    return request.post('/api/admin/faqs', data, { withAuth: true });
  },
  updateFaq(id, data) {
    return request.put(`/api/admin/faqs/${id}`, data, { withAuth: true });
  },
  updateFaqStatus(id, data) {
    return request.put(`/api/admin/faqs/${id}/status`, data, { withAuth: true });
  },
  deleteFaq(id) {
    return request.delete(`/api/admin/faqs/${id}`, { withAuth: true });
  },
  getLeadPage(params) {
    return request.get('/api/admin/leads', { params, withAuth: true });
  },
  getLeadDetail(id) {
    return request.get(`/api/admin/leads/${id}`, { withAuth: true });
  },
  updateLeadStatus(id, data) {
    return request.put(`/api/admin/leads/${id}/status`, data, { withAuth: true });
  },
  getResourcePage(params) {
    return request.get('/api/admin/resources', { params, withAuth: true });
  },
  getResourceDetail(id) {
    return request.get(`/api/admin/resources/${id}`, { withAuth: true });
  },
  createResource(data) {
    return request.post('/api/admin/resources', data, { withAuth: true });
  },
  updateResource(id, data) {
    return request.put(`/api/admin/resources/${id}`, data, { withAuth: true });
  },
  updateResourceStatus(id, data) {
    return request.put(`/api/admin/resources/${id}/status`, data, { withAuth: true });
  },
  deleteResource(id) {
    return request.delete(`/api/admin/resources/${id}`, { withAuth: true });
  },
  getSiteConfig() {
    return request.get('/api/admin/site-config', { withAuth: true });
  },
  saveSiteConfig(data) {
    return request.put('/api/admin/site-config', data, { withAuth: true });
  },
  getHomeConfig() {
    return request.get('/api/admin/home-config', { withAuth: true });
  },
  saveHomeConfig(data) {
    return request.put('/api/admin/home-config', data, { withAuth: true });
  },
  getPageContentList() {
    return request.get('/api/admin/page-content', { withAuth: true });
  },
  getPageContent(pageCode) {
    return request.get(`/api/admin/page-content/${pageCode}`, { withAuth: true });
  },
  savePageContent(pageCode, data) {
    return request.put(`/api/admin/page-content/${pageCode}`, data, { withAuth: true });
  },
};
