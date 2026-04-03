import {
  AppstoreOutlined,
  DeploymentUnitOutlined,
  FileTextOutlined,
  HomeOutlined,
  LinkOutlined,
  MessageOutlined,
  QuestionCircleOutlined,
  RocketOutlined,
  SettingOutlined,
  TagsOutlined,
  UnorderedListOutlined,
  SafetyCertificateOutlined,
  CloudServerOutlined,
} from '@ant-design/icons';
import { Layout, Menu } from 'antd';
import { useLocation, useNavigate } from 'react-router-dom';

const { Sider } = Layout;

const items = [
  { key: '/admin/dashboard', icon: <HomeOutlined />, label: '仪表盘' },
  { key: '/admin/fulfillment', icon: <RocketOutlined />, label: '快速发货中心' },
  { key: '/admin/resources', icon: <AppstoreOutlined />, label: '资源管理' },
  { key: '/admin/resource-sources', icon: <SafetyCertificateOutlined />, label: '资料来源' },
  { key: '/admin/resource-storages', icon: <CloudServerOutlined />, label: '网盘绑定' },
  { key: '/admin/import-tasks', icon: <DeploymentUnitOutlined />, label: '导入任务' },
  { key: '/admin/channels', icon: <LinkOutlined />, label: '渠道管理' },
  { key: '/admin/campaigns', icon: <FileTextOutlined />, label: '内容投放' },
  { key: '/admin/categories', icon: <UnorderedListOutlined />, label: '分类管理' },
  { key: '/admin/tags', icon: <TagsOutlined />, label: '标签管理' },
  { key: '/admin/faqs', icon: <QuestionCircleOutlined />, label: 'FAQ 管理' },
  { key: '/admin/leads', icon: <MessageOutlined />, label: '线索管理' },
  { key: '/admin/home-config', icon: <HomeOutlined />, label: '首页配置' },
  { key: '/admin/site-config', icon: <SettingOutlined />, label: '站点配置' },
  { key: '/admin/page-content', icon: <FileTextOutlined />, label: '页面内容' },
];

function SideMenu() {
  const location = useLocation();
  const navigate = useNavigate();

  const activeKey =
    items.find((item) => location.pathname.startsWith(item.key))?.key || '/admin/dashboard';

  return (
    <Sider breakpoint="lg" collapsedWidth="0" width={232} theme="light">
      <div
        style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          padding: '0 20px',
          borderBottom: '1px solid #f1f5f9',
          fontWeight: 700,
          color: '#0f172a',
        }}
      >
        资源发货后台
      </div>
      <Menu
        mode="inline"
        selectedKeys={[activeKey]}
        items={items}
        style={{ borderInlineEnd: 'none', paddingTop: 12 }}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  );
}

export default SideMenu;
