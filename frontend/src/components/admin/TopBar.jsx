import { LogoutOutlined } from '@ant-design/icons';
import { Button, Layout, Space, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAdminAuthStore } from '../../store/adminAuthStore';

const { Header } = Layout;

function TopBar() {
  const navigate = useNavigate();
  const { nickname, username, logout } = useAdminAuthStore();

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
        justifyContent: 'space-between',
      }}
    >
      <Typography.Title level={5} style={{ margin: 0 }}>
        小学课件教案资源导流站后台
      </Typography.Title>
      <Space size={16}>
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
