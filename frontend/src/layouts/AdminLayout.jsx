import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import FulfillmentDrawer from '../components/admin/FulfillmentDrawer';
import SideMenu from '../components/admin/SideMenu';
import TopBar from '../components/admin/TopBar';

function AdminLayout() {
  return (
    <Layout style={{ minHeight: '100vh', background: '#f8fafc' }}>
      <SideMenu />
      <Layout>
        <TopBar />
        <Layout.Content style={{ padding: 20 }}>
          <div
            style={{
              minHeight: 'calc(100vh - 104px)',
              background: '#fff',
              borderRadius: 16,
              padding: 20,
              boxShadow: '0 14px 30px rgba(15, 23, 42, 0.05)',
            }}
          >
            <Outlet />
          </div>
        </Layout.Content>
      </Layout>
      <FulfillmentDrawer />
    </Layout>
  );
}

export default AdminLayout;
