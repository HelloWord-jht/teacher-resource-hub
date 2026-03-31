import { Button, Card, Form, Input, List, Space, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';
import { pageCodeOptions } from '../../../utils/admin.jsx';

function AdminPageContentPage() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [list, setList] = useState([]);
  const [activeCode, setActiveCode] = useState('about');

  const loadData = async (targetCode = activeCode) => {
    setLoading(true);
    try {
      const pageList = await adminApi.getPageContentList();
      setList(pageList);
      const detail = await adminApi.getPageContent(targetCode);
      setActiveCode(targetCode);
      form.setFieldsValue(detail);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData('about');
  }, []);

  const handleSubmit = async (values) => {
    setSaving(true);
    try {
      await adminApi.savePageContent(activeCode, values);
      message.success('页面内容保存成功');
      loadData(activeCode);
    } finally {
      setSaving(false);
    }
  };

  if (loading && !list.length) {
    return <LoadingState text="正在加载页面内容..." />;
  }

  const titleMap = Object.fromEntries(pageCodeOptions.map((item) => [item.value, item.label]));

  return (
    <div>
      <Typography.Title level={3}>页面内容管理</Typography.Title>
      <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: 16 }}>
        <Card title="页面列表">
          <List
            dataSource={pageCodeOptions}
            renderItem={(item) => (
              <List.Item
                onClick={() => loadData(item.value)}
                style={{
                  cursor: 'pointer',
                  borderRadius: 10,
                  padding: '10px 12px',
                  background: item.value === activeCode ? '#eff6ff' : 'transparent',
                }}
              >
                {item.label}
              </List.Item>
            )}
          />
        </Card>

        <Card
          title={titleMap[activeCode] || '页面内容'}
          extra={
            <Space>
              <Button onClick={() => loadData(activeCode)}>重新加载</Button>
              <Button type="primary" loading={saving} onClick={() => form.submit()}>
                保存内容
              </Button>
            </Space>
          }
        >
          <Form layout="vertical" form={form} onFinish={handleSubmit}>
            <Form.Item label="页面标题" name="title" rules={[{ required: true, message: '请输入页面标题' }]}>
              <Input />
            </Form.Item>
            <Form.Item label="HTML 内容" name="contentHtml" rules={[{ required: true, message: '请输入页面内容' }]}>
              <Input.TextArea rows={18} placeholder="<p>这里填写页面 HTML 内容</p>" />
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  );
}

export default AdminPageContentPage;
