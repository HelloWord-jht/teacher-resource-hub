import { Button, Form, Input, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';

function AdminSiteConfigPage() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    adminApi
      .getSiteConfig()
      .then((result) => form.setFieldsValue(result))
      .finally(() => setLoading(false));
  }, [form]);

  const handleSubmit = async (values) => {
    setSaving(true);
    try {
      await adminApi.saveSiteConfig(values);
      message.success('站点配置保存成功');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <LoadingState text="正在加载站点配置..." />;
  }

  return (
    <div>
      <Typography.Title level={3}>站点配置</Typography.Title>
      <Form layout="vertical" form={form} onFinish={handleSubmit}>
        <Form.Item label="站点名称" name="siteName" rules={[{ required: true, message: '请输入站点名称' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="SEO 标题" name="seoTitle" rules={[{ required: true, message: '请输入 SEO 标题' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="SEO 关键词" name="seoKeywords" rules={[{ required: true, message: '请输入 SEO 关键词' }]}>
          <Input.TextArea rows={3} />
        </Form.Item>
        <Form.Item label="SEO 描述" name="seoDescription" rules={[{ required: true, message: '请输入 SEO 描述' }]}>
          <Input.TextArea rows={4} />
        </Form.Item>
        <Form.Item label="联系电话" name="contactPhone">
          <Input />
        </Form.Item>
        <Form.Item label="联系微信" name="contactWechat">
          <Input />
        </Form.Item>
        <Form.Item label="联系邮箱" name="contactEmail">
          <Input />
        </Form.Item>
        <Form.Item label="页脚文案" name="footerText">
          <Input.TextArea rows={4} />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={saving}>
          保存站点配置
        </Button>
      </Form>
    </div>
  );
}

export default AdminSiteConfigPage;
