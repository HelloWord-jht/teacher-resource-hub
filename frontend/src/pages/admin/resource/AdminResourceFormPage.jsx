import { ArrowLeftOutlined, MinusCircleOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
import {
  Alert,
  Button,
  Card,
  Col,
  DatePicker,
  Descriptions,
  Form,
  Input,
  InputNumber,
  Row,
  Select,
  Space,
  Typography,
  Upload,
  message,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { adminApi } from '../../../api/admin';
import FileProcessStatusTag from '../../../components/admin/FileProcessStatusTag';
import ImportFilePreviewPanel from '../../../components/admin/ImportFilePreviewPanel';
import ImportFileSidebar from '../../../components/admin/ImportFileSidebar';
import LoadingState from '../../../components/common/LoadingState';
import {
  renderAuthorizationStatusTag,
  resourceStatusOptions,
  yesNoOptions,
} from '../../../utils/admin.jsx';

function AdminResourceFormPage({ mode }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [categoryOptions, setCategoryOptions] = useState([]);
  const [sourceOptions, setSourceOptions] = useState([]);
  const [tagOptions, setTagOptions] = useState([]);
  const [resourceOptions, setResourceOptions] = useState([]);
  const [detailData, setDetailData] = useState(null);
  const [resourceFiles, setResourceFiles] = useState([]);
  const [activeResourceFile, setActiveResourceFile] = useState(null);
  const [activeResourceFilePreviews, setActiveResourceFilePreviews] = useState([]);
  const [activeResourceFileLogs, setActiveResourceFileLogs] = useState([]);
  const [resourceFileLoading, setResourceFileLoading] = useState(false);
  const [resourceFileUploading, setResourceFileUploading] = useState(false);
  const [resourceFileActionLoading, setResourceFileActionLoading] = useState(false);

  useEffect(() => {
    Promise.all([
      adminApi.listCategories(),
      adminApi.listResourceSources(),
      adminApi.listTags(),
      adminApi.getResourcePage({ pageNum: 1, pageSize: 200 }),
      mode === 'edit' ? adminApi.getResourceDetail(id) : Promise.resolve(null),
    ])
      .then(([categories, sources, tags, resourcePage, detail]) => {
        setCategoryOptions(categories.map((item) => ({ label: item.name, value: item.id })));
        setSourceOptions(
          sources.map((item) => ({
            label: `${item.sourceName}（${item.authorizationStatus}）`,
            value: item.id,
          })),
        );
        setTagOptions(tags.map((item) => ({ label: item.name, value: item.id })));
        setResourceOptions(
          resourcePage.list
            .filter((item) => String(item.id) !== String(id))
            .map((item) => ({ label: `${item.title}（${item.resourceCode}）`, value: item.id })),
        );

        if (detail) {
          setDetailData(detail);
          form.setFieldsValue({
            ...detail,
            publishTime: detail.publishTime ? dayjs(detail.publishTime) : null,
          });
        } else {
          form.setFieldsValue({
            resourceCode: '',
            displayPrice: 0,
            sortOrder: 0,
            status: 0,
            isRecommended: 0,
            contentItems: ['课件PPT', '配套教案'],
            previewImageList: ['https://placehold.co/1280x960/png?text=preview-1'],
            tagIdList: [],
            recommendedResourceIdList: [],
            deliveryNotice: '本站不做在线支付，请先查看预览并确认需求，再通过微信沟通交付。',
          });
        }
      })
      .finally(() => setLoading(false));
  }, [form, id, mode]);

  const openResourceFile = async (fileRecord, existingFiles = resourceFiles) => {
    const targetFile = typeof fileRecord === 'object'
      ? fileRecord
      : (existingFiles || []).find((item) => item.id === fileRecord) || null;
    if (!targetFile) {
      return;
    }
    setResourceFileLoading(true);
    try {
      const [previews, logs] = await Promise.all([
        adminApi.getResourceFilePreviews(targetFile.id),
        adminApi.getResourceFileLogs(targetFile.id),
      ]);
      setActiveResourceFile(targetFile);
      setActiveResourceFilePreviews(previews || []);
      setActiveResourceFileLogs(logs || []);
    } finally {
      setResourceFileLoading(false);
    }
  };

  const loadResourceFiles = async (resourceId = id, preferredFileId) => {
    if (!resourceId) {
      return;
    }
    const files = await adminApi.listResourceFiles(resourceId);
    setResourceFiles(files || []);
    const nextFile =
      (files || []).find((item) => item.id === preferredFileId) ||
      (files || []).find((item) => item.isPrimary) ||
      (files || []).find((item) => item.previewStatus === 'success') ||
      files?.[0] ||
      null;
    if (nextFile) {
      await openResourceFile(nextFile, files);
    } else {
      setActiveResourceFile(null);
      setActiveResourceFilePreviews([]);
      setActiveResourceFileLogs([]);
    }
  };

  useEffect(() => {
    if (mode === 'edit' && id) {
      loadResourceFiles(id).catch(() => {});
    }
  }, [id, mode]);

  const handleSubmit = async (values) => {
    const payload = {
      ...values,
      resourceCode: values.resourceCode || '',
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

  const handleUploadResourceFile = async ({ file, onSuccess, onError }) => {
    if (!id) {
      onError?.(new Error('请先保存资源后再上传文件'));
      return;
    }
    setResourceFileUploading(true);
    try {
      await adminApi.uploadResourceFile(id, file);
      await loadResourceFiles(id);
      message.success('资源文件上传成功');
      onSuccess?.('ok');
    } catch (error) {
      onError?.(error);
    } finally {
      setResourceFileUploading(false);
    }
  };

  const runResourceFileAction = async (action) => {
    if (!activeResourceFile || !id) {
      return;
    }
    setResourceFileActionLoading(true);
    try {
      await action();
      await loadResourceFiles(id, activeResourceFile.id);
      message.success('文件状态已更新');
    } finally {
      setResourceFileActionLoading(false);
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
        {detailData ? (
          <Card title="当前资源状态" style={{ marginBottom: 16 }}>
            <Descriptions column={4} size="small">
              <Descriptions.Item label="资源码">{detailData.resourceCode || '系统自动生成'}</Descriptions.Item>
              <Descriptions.Item label="授权状态">{renderAuthorizationStatusTag(detailData.authorizationStatusSnapshot)}</Descriptions.Item>
              <Descriptions.Item label="预览图数">{detailData.previewCount || 0}</Descriptions.Item>
              <Descriptions.Item label="内容项数">{detailData.contentItemCount || 0}</Descriptions.Item>
              <Descriptions.Item label="浏览量">{detailData.viewCount || 0}</Descriptions.Item>
              <Descriptions.Item label="咨询量">{detailData.consultCount || 0}</Descriptions.Item>
              <Descriptions.Item label="搜索关键词" span={2}>{detailData.searchKeywords || '--'}</Descriptions.Item>
            </Descriptions>
          </Card>
        ) : null}

        <Card
          title="资源预览管理"
          style={{ marginBottom: 16 }}
          extra={mode === 'edit' ? (
            <Upload showUploadList={false} customRequest={handleUploadResourceFile} multiple={false}>
              <Button icon={<UploadOutlined />} loading={resourceFileUploading}>
                上传资源文件
              </Button>
            </Upload>
          ) : null}
        >
          {mode === 'edit' ? (
            <Space direction="vertical" size={16} style={{ width: '100%' }}>
              <Alert
                type="info"
                showIcon
                message="左侧选择文件，右侧直接查看前 2 页样张。可在当前页直接设为主展示文件、重试生成预览或删除文件。"
              />

              <div style={{ display: 'grid', gridTemplateColumns: '280px minmax(0, 1fr)', gap: 20 }}>
                <ImportFileSidebar
                  files={resourceFiles}
                  activeFileId={activeResourceFile?.id}
                  onSelectFile={openResourceFile}
                />
                {resourceFileLoading ? (
                  <LoadingState text="正在加载资源样张..." />
                ) : (
                  <ImportFilePreviewPanel
                    currentFile={activeResourceFile}
                    previewPages={activeResourceFilePreviews}
                    actionLoading={resourceFileActionLoading}
                    onRefresh={() => openResourceFile(activeResourceFile)}
                    onRegenerate={() => runResourceFileAction(() => adminApi.regenerateResourceFilePreview(activeResourceFile.id))}
                    onSetPrimary={() => runResourceFileAction(() => adminApi.setPrimaryResourceFile(id, activeResourceFile.id))}
                    onDelete={() => runResourceFileAction(() => adminApi.deleteResourceFile(activeResourceFile.id))}
                  />
                )}
              </div>

              <Card size="small" title="当前文件处理日志">
                {activeResourceFileLogs.length ? (
                  <Space direction="vertical" size={12} style={{ width: '100%' }}>
                    {activeResourceFileLogs.map((log) => (
                      <div key={log.id} style={{ padding: 12, border: '1px solid #e2e8f0', borderRadius: 12 }}>
                        <Space size={8} wrap>
                          <Typography.Text strong>{log.stepName}</Typography.Text>
                          <FileProcessStatusTag status={log.stepStatus === 'skipped' ? 'unsupported' : log.stepStatus} />
                          <Typography.Text type="secondary">{log.createdAt || '--'}</Typography.Text>
                        </Space>
                        <div style={{ marginTop: 8, color: '#475569' }}>{log.message}</div>
                      </div>
                    ))}
                  </Space>
                ) : (
                  <Typography.Text type="secondary">当前文件还没有处理日志。</Typography.Text>
                )}
              </Card>
            </Space>
          ) : (
            <Typography.Text type="secondary">
              请先保存资源，再上传文件并生成样张预览。
            </Typography.Text>
          )}
        </Card>

        <Card title="基础信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col xs={24} md={12}>
              <Form.Item label="资源码（可留空自动生成）" name="resourceCode">
                <Input placeholder="例如：RES-JZH-20260403-0001" />
              </Form.Item>
            </Col>
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
            <Col xs={24} md={12}>
              <Form.Item label="封面图地址" name="coverImage" rules={[{ required: true, message: '请输入封面图地址' }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="所属分类" name="categoryId" rules={[{ required: true, message: '请选择分类' }]}>
                <Select options={categoryOptions} />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="资料来源" name="sourceId" rules={[{ required: true, message: '请选择资料来源' }]}>
                <Select
                  options={sourceOptions}
                  placeholder="来源未配置时请先到资料来源页新增"
                  showSearch
                  optionFilterProp="label"
                />
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
            <Col xs={24} md={8}>
              <Form.Item label="展示价格" name="displayPrice" rules={[{ required: true, message: '请输入展示价格' }]}>
                <InputNumber style={{ width: '100%' }} min={0} precision={2} />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item label="排序值" name="sortOrder" rules={[{ required: true, message: '请输入排序值' }]}>
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="资源简介" name="summary" rules={[{ required: true, message: '请输入资源简介' }]}>
                <Input.TextArea rows={3} />
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
          <Form.Item label="交付说明" name="deliveryNotice" rules={[{ required: true, message: '请输入交付说明' }]}>
            <Input.TextArea rows={4} placeholder="例如：本站不做在线支付，确认需求后通过微信沟通交付。" />
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
