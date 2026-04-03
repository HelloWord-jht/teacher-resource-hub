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
  updateLeadWechatStatus(id, data) {
    return request.put(`/api/admin/leads/${id}/wechat-status`, data, { withAuth: true });
  },
  updateLeadDealStatus(id, data) {
    return request.put(`/api/admin/leads/${id}/deal-status`, data, { withAuth: true });
  },
  getResourcePage(params) {
    return request.get('/api/admin/resources', { params, withAuth: true });
  },
  getResourceDetail(id) {
    return request.get(`/api/admin/resources/${id}`, { withAuth: true });
  },
  getResourceDetailByCode(resourceCode) {
    return request.get(`/api/admin/resources/by-code/${resourceCode}`, { withAuth: true });
  },
  quickSearchResources(keyword) {
    return request.get('/api/admin/resources/quick-search', { params: { keyword }, withAuth: true });
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
  listResourceSources(params) {
    return request.get('/api/admin/resource-sources', { params, withAuth: true });
  },
  getResourceSource(id) {
    return request.get(`/api/admin/resource-sources/${id}`, { withAuth: true });
  },
  createResourceSource(data) {
    return request.post('/api/admin/resource-sources', data, { withAuth: true });
  },
  updateResourceSource(id, data) {
    return request.put(`/api/admin/resource-sources/${id}`, data, { withAuth: true });
  },
  auditResourceSource(id, data) {
    return request.put(`/api/admin/resource-sources/${id}/audit`, data, { withAuth: true });
  },
  listResourceStorages(params) {
    return request.get('/api/admin/resource-storages', { params, withAuth: true });
  },
  getResourceStorage(id) {
    return request.get(`/api/admin/resource-storages/${id}`, { withAuth: true });
  },
  createResourceStorage(data) {
    return request.post('/api/admin/resource-storages', data, { withAuth: true });
  },
  updateResourceStorage(id, data) {
    return request.put(`/api/admin/resource-storages/${id}`, data, { withAuth: true });
  },
  deleteResourceStorage(id) {
    return request.delete(`/api/admin/resource-storages/${id}`, { withAuth: true });
  },
  listImportTasks(params) {
    return request.get('/api/admin/import-tasks', { params, withAuth: true });
  },
  createImportTask(data) {
    return request.post('/api/admin/import-tasks', data, { withAuth: true });
  },
  executeImportTask(id) {
    return request.post(`/api/admin/import-tasks/${id}/execute`, null, { withAuth: true });
  },
  getImportTask(id) {
    return request.get(`/api/admin/import-tasks/${id}`, { withAuth: true });
  },
  uploadImportTaskFile(id, file) {
    const formData = new FormData();
    formData.append('file', file);
    return request.post(`/api/admin/import-tasks/${id}/files/upload`, formData, {
      withAuth: true,
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  listImportTaskFiles(id) {
    return request.get(`/api/admin/import-tasks/${id}/files`, { withAuth: true });
  },
  executeImportTaskPreview(id) {
    return request.post(`/api/admin/import-tasks/${id}/execute-preview`, null, { withAuth: true });
  },
  uploadResourceFile(resourceId, file) {
    const formData = new FormData();
    formData.append('file', file);
    return request.post(`/api/admin/resources/${resourceId}/files/upload`, formData, {
      withAuth: true,
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  listResourceFiles(resourceId) {
    return request.get(`/api/admin/resources/${resourceId}/files`, { withAuth: true });
  },
  getResourceFile(resourceId, fileId) {
    return request.get(`/api/admin/resources/${resourceId}/files/${fileId}`, { withAuth: true });
  },
  getResourceFilePreviews(fileId) {
    return request.get(`/api/admin/resource-files/${fileId}/previews`, { withAuth: true });
  },
  getResourceFileLogs(fileId) {
    return request.get(`/api/admin/resource-files/${fileId}/logs`, { withAuth: true });
  },
  regenerateResourceFilePreview(fileId) {
    return request.post(`/api/admin/resource-files/${fileId}/regenerate-preview`, null, { withAuth: true });
  },
  setPrimaryResourceFile(resourceId, fileId) {
    return request.put(`/api/admin/resources/${resourceId}/primary-file/${fileId}`, null, { withAuth: true });
  },
  deleteResourceFile(fileId) {
    return request.delete(`/api/admin/resource-files/${fileId}`, { withAuth: true });
  },
  listLeadChannels() {
    return request.get('/api/admin/lead-channels', { withAuth: true });
  },
  getLeadChannel(id) {
    return request.get(`/api/admin/lead-channels/${id}`, { withAuth: true });
  },
  createLeadChannel(data) {
    return request.post('/api/admin/lead-channels', data, { withAuth: true });
  },
  updateLeadChannel(id, data) {
    return request.put(`/api/admin/lead-channels/${id}`, data, { withAuth: true });
  },
  deleteLeadChannel(id) {
    return request.delete(`/api/admin/lead-channels/${id}`, { withAuth: true });
  },
  listContentCampaigns(params) {
    return request.get('/api/admin/content-campaigns', { params, withAuth: true });
  },
  getContentCampaign(id) {
    return request.get(`/api/admin/content-campaigns/${id}`, { withAuth: true });
  },
  createContentCampaign(data) {
    return request.post('/api/admin/content-campaigns', data, { withAuth: true });
  },
  updateContentCampaign(id, data) {
    return request.put(`/api/admin/content-campaigns/${id}`, data, { withAuth: true });
  },
  deleteContentCampaign(id) {
    return request.delete(`/api/admin/content-campaigns/${id}`, { withAuth: true });
  },
  listVisitTraces(params) {
    return request.get('/api/admin/visit-traces', { params, withAuth: true });
  },
  quickSearchFulfillment(keyword) {
    return request.get('/api/admin/fulfillment/quick-search', { params: { keyword }, withAuth: true });
  },
  getFulfillmentResource(resourceCode) {
    return request.get(`/api/admin/fulfillment/resource/${resourceCode}`, { withAuth: true });
  },
  getFulfillmentRecentSearches(limit = 10) {
    return request.get('/api/admin/fulfillment/recent-searches', { params: { limit }, withAuth: true });
  },
  copyFulfillmentTemplate(data) {
    return request.post('/api/admin/fulfillment/copy-template', data, { withAuth: true });
  },
  markDelivered(data) {
    return request.post('/api/admin/fulfillment/mark-delivered', data, { withAuth: true });
  },
  getDeliveryRecords(params) {
    return request.get('/api/admin/delivery-records', { params, withAuth: true });
  },
};
