import {
  Button,
  Descriptions,
  Drawer,
  Form,
  Input,
  InputNumber,
  Space,
  Table,
  Tag,
  Typography,
  message,
} from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../api/admin';
import { useFulfillmentStore } from '../../store/fulfillmentStore';
import { copyText } from '../../utils/clipboard';
import { formatDateTime } from '../../utils/format';
import LoadingState from '../common/LoadingState';
import ErrorState from '../common/ErrorState';

function FulfillmentDrawer() {
  const { drawerOpen, resourceCode, leadId, closeDrawer } = useFulfillmentStore();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [template, setTemplate] = useState('');
  const [marking, setMarking] = useState(false);
  const [form] = Form.useForm();

  const loadData = () => {
    if (!drawerOpen || !resourceCode) {
      return;
    }
    setLoading(true);
    setError('');
    adminApi
      .getFulfillmentResource(resourceCode)
      .then((result) => {
        setData(result);
        setTemplate(result.deliveryTemplate || '');
        form.setFieldsValue({
          leadId: leadId || undefined,
          deliveryRemark: '',
        });
      })
      .catch((err) => setError(err?.message || '发货信息加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, [drawerOpen, resourceCode, leadId]);

  const handleCopy = async (text, successText) => {
    if (!text) {
      message.warning('当前没有可复制内容');
      return;
    }
    await copyText(text);
    message.success(successText);
  };

  const handleCopyTemplate = async () => {
    if (!resourceCode) {
      return;
    }
    const result = await adminApi.copyFulfillmentTemplate({ resourceCode });
    setTemplate(result);
    await copyText(result);
    message.success('完整发货文案已复制');
  };

  const handleMarkDelivered = async () => {
    const values = await form.validateFields();
    setMarking(true);
    try {
      await adminApi.markDelivered({
        resourceCode,
        leadId: values.leadId || null,
        deliveryRemark: values.deliveryRemark || '',
      });
      message.success('已记录发货');
      loadData();
    } finally {
      setMarking(false);
    }
  };

  return (
    <Drawer
      open={drawerOpen}
      onClose={closeDrawer}
      width={720}
      title="快速发货面板"
      extra={
        <Space>
          <Button onClick={handleCopyTemplate}>一键复制完整发货模板</Button>
          <Button type="primary" loading={marking} onClick={handleMarkDelivered}>
            标记已发货
          </Button>
        </Space>
      }
      destroyOnClose={false}
    >
      {loading ? <LoadingState text="正在加载发货信息..." /> : null}
      {!loading && error ? <ErrorState subTitle={error} onRetry={loadData} /> : null}
      {!loading && !error && data ? (
        <Space direction="vertical" size={16} style={{ width: '100%' }}>
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="资源标题">{data.title}</Descriptions.Item>
            <Descriptions.Item label="资源码">
              <Space>
                <Typography.Text code>{data.resourceCode}</Typography.Text>
                <Button size="small" onClick={() => handleCopy(data.resourceCode, '资源码已复制')}>
                  复制资源码
                </Button>
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="适用场景">{data.scene || '--'}</Descriptions.Item>
            <Descriptions.Item label="分类">{data.categoryName || '--'}</Descriptions.Item>
            <Descriptions.Item label="授权状态">
              <Tag color={data.authorizationStatusSnapshot === 'APPROVED' ? 'green' : 'orange'}>
                {data.authorizationStatusSnapshot || '--'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="发布状态">
              <Tag color={data.status === 1 ? 'blue' : 'default'}>{data.status === 1 ? '已发布' : '下线'}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="网盘平台">{data.storagePlatform || '--'}</Descriptions.Item>
            <Descriptions.Item label="分享链接">
              <Space wrap>
                <Typography.Text copyable={false}>{data.shareUrl || '--'}</Typography.Text>
                <Button size="small" onClick={() => handleCopy(data.shareUrl, '分享链接已复制')}>
                  复制分享链接
                </Button>
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="分享码">
              <Space wrap>
                <Typography.Text>{data.shareCode || '--'}</Typography.Text>
                <Button size="small" onClick={() => handleCopy(data.shareCode, '分享码已复制')}>
                  复制分享码
                </Button>
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="提取码">
              <Space wrap>
                <Typography.Text>{data.extractCode || '--'}</Typography.Text>
                <Button size="small" onClick={() => handleCopy(data.extractCode, '提取码已复制')}>
                  复制提取码
                </Button>
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="交付备注">{data.deliveryNote || '--'}</Descriptions.Item>
          </Descriptions>

          <div>
            <Typography.Title level={5}>完整发货文案</Typography.Title>
            <Input.TextArea rows={6} value={template} readOnly />
          </div>

          <Form form={form} layout="vertical">
            <Form.Item label="关联线索 ID" name="leadId">
              <InputNumber style={{ width: '100%' }} min={1} placeholder="可选，来自线索管理页时会自动带入" />
            </Form.Item>
            <Form.Item label="发货备注" name="deliveryRemark">
              <Input.TextArea rows={3} placeholder="例如：已按低年级需求提醒老师微调案例" />
            </Form.Item>
          </Form>

          <div>
            <Typography.Title level={5}>最近发货记录</Typography.Title>
            <Table
              rowKey="id"
              size="small"
              pagination={false}
              dataSource={data.recentDeliveryRecords || []}
              columns={[
                { title: '线索ID', dataIndex: 'leadId', width: 90, render: (value) => value || '--' },
                { title: '发货人', dataIndex: 'operatorName', width: 120 },
                { title: '发货备注', dataIndex: 'deliveryRemark', ellipsis: true },
                {
                  title: '发货时间',
                  dataIndex: 'createdAt',
                  width: 160,
                  render: formatDateTime,
                },
              ]}
            />
          </div>
        </Space>
      ) : null}
    </Drawer>
  );
}

export default FulfillmentDrawer;
