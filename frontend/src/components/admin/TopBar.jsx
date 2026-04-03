import { LogoutOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Layout, Space, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAdminAuthStore } from '../../store/adminAuthStore';
import { useFulfillmentStore } from '../../store/fulfillmentStore';
import FulfillmentSearchBox from './FulfillmentSearchBox';

const { Header } = Layout;

function TopBar() {
  const navigate = useNavigate();
  const { nickname, username, logout } = useAdminAuthStore();
  const openDrawer = useFulfillmentStore((state) => state.openDrawer);

  const handleLogout = () => {
    logout();
    navigate('/admin/login');
  };

  return (
    <Header
      style={{
        background: '#fff',
        padding: '0 20px',
        borderBottom: '1px solid #f1f5f9',
        display: 'flex',
        alignItems: 'center',
        gap: 20,
      }}
    >
      <Typography.Title level={5} style={{ margin: 0 }}>
        小学教师备课资源后台
      </Typography.Title>
      <FulfillmentSearchBox
        style={{ flex: 1, maxWidth: 560 }}
        onPicked={(item) => {
          if (item?.resourceCode) {
            openDrawer({ resourceCode: item.resourceCode });
          }
        }}
      />
      <Space size={16} style={{ marginLeft: 'auto' }}>
        <Button icon={<SearchOutlined />} onClick={() => navigate('/admin/fulfillment')}>
          快速发货中心
        </Button>
        <div style={{ textAlign: 'right' }}>
          <div style={{ fontWeight: 600 }}>{nickname || '管理员'}</div>
          <div style={{ fontSize: 12, color: '#64748b' }}>{username || 'admin'}</div>
        </div>
        <Button icon={<LogoutOutlined />} onClick={handleLogout}>
          退出登录
        </Button>
      </Space>
    </Header>
  );
}

export default TopBar;
