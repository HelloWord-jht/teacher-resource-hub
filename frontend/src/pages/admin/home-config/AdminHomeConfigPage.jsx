import { Button, Form, Input, Select, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';

function AdminHomeConfigPage() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [categoryOptions, setCategoryOptions] = useState([]);
  const [resourceOptions, setResourceOptions] = useState([]);
  const [faqOptions, setFaqOptions] = useState([]);

  useEffect(() => {
    Promise.all([
      adminApi.getHomeConfig(),
      adminApi.listCategories(),
      adminApi.getResourcePage({ pageNum: 1, pageSize: 100 }),
      adminApi.listFaqs(),
    ])
      .then(([homeConfig, categories, resourcePage, faqs]) => {
        form.setFieldsValue(homeConfig);
        setCategoryOptions(categories.map((item) => ({ label: item.name, value: item.id })));
        setResourceOptions(resourcePage.list.map((item) => ({ label: item.title, value: item.id })));
        setFaqOptions(faqs.map((item) => ({ label: item.question, value: item.id })));
      })
      .finally(() => setLoading(false));
  }, [form]);

  const handleSubmit = async (values) => {
    setSaving(true);
    try {
      await adminApi.saveHomeConfig(values);
      message.success('首页配置保存成功');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <LoadingState text="正在加载首页配置..." />;
  }

  return (
    <div>
      <Typography.Title level={3}>首页配置</Typography.Title>
      <Form layout="vertical" form={form} onFinish={handleSubmit}>
        <Form.Item label="Banner 主标题" name="homeMainTitle" rules={[{ required: true, message: '请输入主标题' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="Banner 副标题" name="homeSubTitle" rules={[{ required: true, message: '请输入副标题' }]}>
          <Input.TextArea rows={4} />
        </Form.Item>
        <Form.Item label="微信号" name="wechatId" rules={[{ required: true, message: '请输入微信号' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="微信二维码图片地址" name="wechatQrUrl" rules={[{ required: true, message: '请输入二维码地址' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="微信引导文案" name="wechatTip" rules={[{ required: true, message: '请输入引导文案' }]}>
          <Input.TextArea rows={3} />
        </Form.Item>
        <Form.Item label="热门分类" name="homeHotCategoryIds" rules={[{ required: true, message: '请选择热门分类' }]}>
          <Select mode="multiple" options={categoryOptions} placeholder="请选择首页要展示的热门分类" />
        </Form.Item>
        <Form.Item label="推荐资源" name="homeRecommendedResourceIds" rules={[{ required: true, message: '请选择推荐资源' }]}>
          <Select mode="multiple" options={resourceOptions} placeholder="请选择首页推荐资源" />
        </Form.Item>
        <Form.Item label="首页 FAQ" name="homeFaqIds" rules={[{ required: true, message: '请选择首页 FAQ' }]}>
          <Select mode="multiple" options={faqOptions} placeholder="请选择首页 FAQ" />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={saving}>
          保存首页配置
        </Button>
      </Form>
    </div>
  );
}

export default AdminHomeConfigPage;
