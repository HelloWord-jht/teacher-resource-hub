import { lazy, Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { useAdminAuthStore } from '../store/adminAuthStore';
import LoadingState from '../components/common/LoadingState';

const WebLayout = lazy(() => import('../layouts/WebLayout'));
const AdminLayout = lazy(() => import('../layouts/AdminLayout'));
const HomePage = lazy(() => import('../pages/home/HomePage'));
const ResourceListPage = lazy(() => import('../pages/resource-list/ResourceListPage'));
const ResourceDetailPage = lazy(() => import('../pages/resource-detail/ResourceDetailPage'));
const ContentPage = lazy(() => import('../pages/content/ContentPage'));
const AdminLoginPage = lazy(() => import('../pages/admin/login/AdminLoginPage'));
const AdminDashboardPage = lazy(() => import('../pages/admin/dashboard/AdminDashboardPage'));
const AdminCategoryPage = lazy(() => import('../pages/admin/category/AdminCategoryPage'));
const AdminTagPage = lazy(() => import('../pages/admin/tag/AdminTagPage'));
const AdminFaqPage = lazy(() => import('../pages/admin/faq/AdminFaqPage'));
const AdminLeadPage = lazy(() => import('../pages/admin/lead/AdminLeadPage'));
const AdminSiteConfigPage = lazy(() => import('../pages/admin/site-config/AdminSiteConfigPage'));
const AdminHomeConfigPage = lazy(() => import('../pages/admin/home-config/AdminHomeConfigPage'));
const AdminPageContentPage = lazy(() => import('../pages/admin/page-content/AdminPageContentPage'));
const AdminResourceListPage = lazy(() => import('../pages/admin/resource/AdminResourceListPage'));
const AdminResourceFormPage = lazy(() => import('../pages/admin/resource/AdminResourceFormPage'));

function AdminRoute({ children }) {
  const token = useAdminAuthStore((state) => state.token);
  if (!token) {
    return <Navigate to="/admin/login" replace />;
  }
  return children;
}

function AppRouter() {
  return (
    <Suspense fallback={<LoadingState text="页面加载中，请稍候..." />}>
      <Routes>
        <Route element={<WebLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/resources" element={<ResourceListPage />} />
          <Route path="/resources/:id" element={<ResourceDetailPage />} />
          <Route path="/about" element={<ContentPage pageCode="about" />} />
          <Route path="/contact" element={<ContentPage pageCode="contact" />} />
          <Route path="/disclaimer" element={<ContentPage pageCode="disclaimer" />} />
          <Route path="/refund-policy" element={<ContentPage pageCode="refund-policy" />} />
        </Route>

        <Route path="/admin/login" element={<AdminLoginPage />} />
        <Route
          path="/admin"
          element={
            <AdminRoute>
              <AdminLayout />
            </AdminRoute>
          }
        >
          <Route index element={<Navigate to="/admin/dashboard" replace />} />
          <Route path="dashboard" element={<AdminDashboardPage />} />
          <Route path="resources" element={<AdminResourceListPage />} />
          <Route path="resources/create" element={<AdminResourceFormPage mode="create" />} />
          <Route path="resources/:id/edit" element={<AdminResourceFormPage mode="edit" />} />
          <Route path="categories" element={<AdminCategoryPage />} />
          <Route path="tags" element={<AdminTagPage />} />
          <Route path="faqs" element={<AdminFaqPage />} />
          <Route path="leads" element={<AdminLeadPage />} />
          <Route path="site-config" element={<AdminSiteConfigPage />} />
          <Route path="home-config" element={<AdminHomeConfigPage />} />
          <Route path="page-content" element={<AdminPageContentPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}

export default AppRouter;
