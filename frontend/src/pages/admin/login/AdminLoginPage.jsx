import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Typography, message } from 'antd';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminApi } from '../../../api/admin';
import { useAdminAuthStore } from '../../../store/adminAuthStore';
import styles from './AdminLoginPage.module.css';

function AdminLoginPage() {
  const [loading, setLoading] = useState(false);
  const login = useAdminAuthStore((state) => state.login);
  const navigate = useNavigate();

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const result = await adminApi.login(values);
      login(result);
      message.success('登录成功');
      navigate('/admin/dashboard');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <Card className={styles.card} bordered={false}>
        <Typography.Title level={2} className={styles.title}>
          小学资源后台登录
        </Typography.Title>
        <Typography.Paragraph className={styles.desc}>
          统一管理资源、分类、FAQ、首页配置、微信导流信息与老师咨询线索。
        </Typography.Paragraph>

        <Form layout="vertical" onFinish={handleSubmit} initialValues={{ username: 'admin', password: '123456' }}>
          <Form.Item
            label="用户名"
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="请输入管理员用户名" />
          </Form.Item>
          <Form.Item
            label="密码"
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入管理员密码" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block size="large" loading={loading}>
            登录后台
          </Button>
        </Form>
      </Card>
    </div>
  );
}

export default AdminLoginPage;
