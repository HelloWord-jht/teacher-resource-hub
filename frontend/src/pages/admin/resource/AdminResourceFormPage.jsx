import { ArrowLeftOutlined, MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Row,
  Select,
  Space,
  Typography,
  message,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';
import { resourceStatusOptions, yesNoOptions } from '../../../utils/admin.jsx';

function AdminResourceFormPage({ mode }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [categoryOptions, setCategoryOptions] = useState([]);
  const [tagOptions, setTagOptions] = useState([]);
  const [resourceOptions, setResourceOptions] = useState([]);

  useEffect(() => {
    Promise.all([
      adminApi.listCategories(),
      adminApi.listTags(),
      adminApi.getResourcePage({ pageNum: 1, pageSize: 200 }),
      mode === 'edit' ? adminApi.getResourceDetail(id) : Promise.resolve(null),
    ])
      .then(([categories, tags, resourcePage, detail]) => {
        setCategoryOptions(categories.map((item) => ({ label: item.name, value: item.id })));
        setTagOptions(tags.map((item) => ({ label: item.name, value: item.id })));
        setResourceOptions(
          resourcePage.list
            .filter((item) => String(item.id) !== String(id))
            .map((item) => ({ label: item.title, value: item.id })),
        );

        if (detail) {
          form.setFieldsValue({
            ...detail,
            publishTime: detail.publishTime ? dayjs(detail.publishTime) : null,
          });
        } else {
          form.setFieldsValue({
            displayPrice: 0,
            sortOrder: 0,
            status: 1,
            isRecommended: 1,
            contentItems: ['课件PPT', '配套教案'],
            previewImageList: ['https://placehold.co/1280x960/png?text=preview-1'],
            tagIdList: [],
            recommendedResourceIdList: [],
          });
        }
      })
      .finally(() => setLoading(false));
  }, [form, id, mode]);

  const handleSubmit = async (values) => {
    const payload = {
      ...values,
      publishTime: values.publishTime ? values.publishTime.format('YYYY-MM-DD HH:mm:ss') : null,
    };
    setSaving(true);
    try {
      if (mode === 'edit') {
        await adminApi.updateResource(id, payload);
        message.success('资源更新成功');
      } else {
        await adminApi.createResource(payload);
        message.success('资源创建成功');
      }
      navigate('/admin/resources');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <LoadingState text="正在加载资源表单..." />;
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/admin/resources')}>
          返回资源列表
        </Button>
        <Typography.Title level={3} style={{ margin: 0 }}>
          {mode === 'edit' ? '编辑资源' : '新增资源'}
        </Typography.Title>
      </Space>

      <Form layout="vertical" form={form} onFinish={handleSubmit}>
        <Card title="基础信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item label="资源标题" name="title" rules={[{ required: true, message: '请输入资源标题' }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item label="资源别名" name="slug" rules={[{ required: true, message: '请输入资源别名' }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="所属分类" name="categoryId" rules={[{ required: true, message: '请选择分类' }]}>
                <Select options={categoryOptions} />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="适用年级" name="grade" rules={[{ required: true, message: '请输入适用年级' }]}>
                <Input placeholder="例如：一年级-六年级" />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="适用场景" name="scene" rules={[{ required: true, message: '请输入适用场景' }]}>
                <Input placeholder="例如：家长会" />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="资源简介" name="summary" rules={[{ required: true, message: '请输入资源简介' }]}>
                <Input.TextArea rows={3} />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item label="封面图地址" name="coverImage" rules={[{ required: true, message: '请输入封面图地址' }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="展示价格" name="displayPrice" rules={[{ required: true, message: '请输入展示价格' }]}>
                <InputNumber style={{ width: '100%' }} min={0} precision={2} />
              </Form.Item>
            </Col>
            <Col xs={24} md={6}>
              <Form.Item label="排序值" name="sortOrder" rules={[{ required: true, message: '请输入排序值' }]}>
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="资源状态" name="status" rules={[{ required: true, message: '请选择资源状态' }]}>
                <Select options={resourceStatusOptions} />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="是否推荐" name="isRecommended" rules={[{ required: true, message: '请选择推荐状态' }]}>
                <Select options={yesNoOptions} />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="发布时间" name="publishTime">
                <DatePicker style={{ width: '100%' }} showTime format="YYYY-MM-DD HH:mm:ss" />
              </Form.Item>
            </Col>
          </Row>
        </Card>

        <Card title="标签与推荐配置" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item label="标签列表" name="tagIdList" rules={[{ required: true, message: '请选择标签' }]}>
                <Select mode="multiple" options={tagOptions} />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item label="推荐资源列表" name="recommendedResourceIdList">
                <Select mode="multiple" options={resourceOptions} />
              </Form.Item>
            </Col>
          </Row>
        </Card>

        <Card title="包含内容清单" style={{ marginBottom: 16 }}>
          <Form.List name="contentItems">
            {(fields, { add, remove }) => (
              <>
                {fields.map((field) => (
                  <Space key={field.key} align="baseline" style={{ display: 'flex', marginBottom: 8 }}>
                    <Form.Item
                      {...field}
                      rules={[{ required: true, message: '请输入内容项' }]}
                      style={{ width: 420 }}
                    >
                      <Input placeholder="例如：课件PPT" />
                    </Form.Item>
                    <Button icon={<MinusCircleOutlined />} onClick={() => remove(field.name)} />
                  </Space>
                ))}
                <Button icon={<PlusOutlined />} onClick={() => add()}>
                  新增内容项
                </Button>
              </>
            )}
          </Form.List>
        </Card>

        <Card title="预览图列表" style={{ marginBottom: 16 }}>
          <Form.List name="previewImageList">
            {(fields, { add, remove }) => (
              <>
                {fields.map((field) => (
                  <Space key={field.key} align="baseline" style={{ display: 'flex', marginBottom: 8 }}>
                    <Form.Item
                      {...field}
                      rules={[{ required: true, message: '请输入预览图地址' }]}
                      style={{ width: 620 }}
                    >
                      <Input placeholder="例如：https://placehold.co/1280x960/png?text=preview-1" />
                    </Form.Item>
                    <Button icon={<MinusCircleOutlined />} onClick={() => remove(field.name)} />
                  </Space>
                ))}
                <Button icon={<PlusOutlined />} onClick={() => add()}>
                  新增预览图
                </Button>
              </>
            )}
          </Form.List>
        </Card>

        <Card title="详情内容" style={{ marginBottom: 16 }}>
          <Form.Item label="资源说明 HTML" name="descriptionHtml" rules={[{ required: true, message: '请输入资源说明 HTML' }]}>
            <Input.TextArea rows={10} placeholder="<p>这里填写资源详情 HTML 内容</p>" />
          </Form.Item>
          <Form.Item label="使用说明" name="usageNotice" rules={[{ required: true, message: '请输入使用说明' }]}>
            <Input.TextArea rows={4} />
          </Form.Item>
        </Card>

        <Space>
          <Button onClick={() => navigate('/admin/resources')}>取消</Button>
          <Button type="primary" htmlType="submit" loading={saving}>
            保存资源
          </Button>
        </Space>
      </Form>
    </div>
  );
}

export default AdminResourceFormPage;
