import {
  Button,
  Descriptions,
  Drawer,
  Form,
  Input,
  Pagination,
  Select,
  Space,
  Table,
  Typography,
  message,
} from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';
import { useFulfillmentStore } from '../../../store/fulfillmentStore';
import {
  channelTypeOptions,
  leadDealStatusOptions,
  leadStatusOptions,
  renderLeadDealStatusTag,
  renderLeadStatusTag,
  renderWechatAddedTag,
  wechatAddedStatusOptions,
} from '../../../utils/admin.jsx';
import { formatDateTime } from '../../../utils/format';

function AdminLeadPage() {
  const openFulfillmentDrawer = useFulfillmentStore((state) => state.openDrawer);
  const [pageData, setPageData] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    status: undefined,
    channel: undefined,
    wechatAddedStatus: undefined,
    dealStatus: undefined,
    keyword: '',
    targetResourceCode: '',
  });
  const [keywordInput, setKeywordInput] = useState('');
  const [resourceCodeInput, setResourceCodeInput] = useState('');
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [currentLead, setCurrentLead] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const loadData = (pageNum = pageData.pageNum, pageSize = pageData.pageSize, nextFilters = filters) => {
    setLoading(true);
    adminApi
      .getLeadPage({
        pageNum,
        pageSize,
        ...nextFilters,
      })
      .then(setPageData)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData(1, 10, filters);
  }, []);

  const openDetail = async (record) => {
    const detail = await adminApi.getLeadDetail(record.id);
    setCurrentLead(detail);
    form.setFieldsValue({
      status: detail.status,
      wechatAddedStatus: detail.wechatAddedStatus,
      dealStatus: detail.dealStatus,
      followUpNote: detail.followUpNote,
    });
    setDrawerOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    setSaving(true);
    try {
      await Promise.all([
        adminApi.updateLeadStatus(currentLead.id, {
          status: values.status,
          followUpNote: values.followUpNote || '',
        }),
        adminApi.updateLeadWechatStatus(currentLead.id, {
          wechatAddedStatus: values.wechatAddedStatus,
          followUpNote: values.followUpNote || '',
        }),
        adminApi.updateLeadDealStatus(currentLead.id, {
          dealStatus: values.dealStatus,
          followUpNote: values.followUpNote || '',
        }),
      ]);
      message.success('线索跟进信息已保存');
      await openDetail(currentLead);
      loadData();
    } finally {
      setSaving(false);
    }
  };

  if (loading && !pageData.list.length) {
    return <LoadingState text="正在加载线索列表..." />;
  }

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }}>
        <Typography.Title level={3} style={{ margin: 0 }}>线索跟进</Typography.Title>
      </Space>

      <Space wrap style={{ marginBottom: 16 }}>
        <Input.Search
          allowClear
          style={{ width: 260 }}
          placeholder="搜索老师、联系方式、咨询内容"
          value={keywordInput}
          onChange={(event) => setKeywordInput(event.target.value)}
          onSearch={(value) => {
            const nextFilters = { ...filters, keyword: value || '' };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Input.Search
          allowClear
          style={{ width: 220 }}
          placeholder="按资源码筛选"
          value={resourceCodeInput}
          onChange={(event) => setResourceCodeInput(event.target.value)}
          onSearch={(value) => {
            const nextFilters = { ...filters, targetResourceCode: value || '' };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Select
          allowClear
          style={{ width: 160 }}
          placeholder="跟进状态"
          options={leadStatusOptions}
          value={filters.status}
          onChange={(value) => {
            const nextFilters = { ...filters, status: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Select
          allowClear
          style={{ width: 160 }}
          placeholder="来源渠道"
          options={channelTypeOptions}
          value={filters.channel}
          onChange={(value) => {
            const nextFilters = { ...filters, channel: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Select
          allowClear
          style={{ width: 160 }}
          placeholder="加微状态"
          options={wechatAddedStatusOptions}
          value={filters.wechatAddedStatus}
          onChange={(value) => {
            const nextFilters = { ...filters, wechatAddedStatus: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Select
          allowClear
          style={{ width: 160 }}
          placeholder="成交状态"
          options={leadDealStatusOptions}
          value={filters.dealStatus}
          onChange={(value) => {
            const nextFilters = { ...filters, dealStatus: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Button
          onClick={() => {
            const nextFilters = {
              status: undefined,
              channel: undefined,
              wechatAddedStatus: undefined,
              dealStatus: undefined,
              keyword: '',
              targetResourceCode: '',
            };
            setKeywordInput('');
            setResourceCodeInput('');
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        >
          重置
        </Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={pageData.list}
        scroll={{ x: 1520 }}
        columns={[
          { title: '老师称呼', dataIndex: 'name', width: 120, fixed: 'left' },
          { title: '联系方式', dataIndex: 'contact', width: 180 },
          { title: '资源码', dataIndex: 'targetResourceCode', width: 190, render: (value) => value || '--' },
          { title: '来源渠道', dataIndex: 'channel', width: 140, render: (value) => value || '--' },
          { title: 'tracking_code', dataIndex: 'trackingCode', width: 180, render: (value) => value || '--' },
          {
            title: '跟进状态',
            dataIndex: 'status',
            width: 120,
            render: renderLeadStatusTag,
          },
          {
            title: '加微状态',
            dataIndex: 'wechatAddedStatus',
            width: 120,
            render: renderWechatAddedTag,
          },
          {
            title: '成交状态',
            dataIndex: 'dealStatus',
            width: 120,
            render: renderLeadDealStatusTag,
          },
          { title: '来源页面', dataIndex: 'sourcePage', ellipsis: true },
          { title: '咨询内容', dataIndex: 'message', ellipsis: true },
          {
            title: '最近跟进',
            dataIndex: 'lastFollowUpTime',
            width: 170,
            render: formatDateTime,
          },
          {
            title: '操作',
            width: 220,
            fixed: 'right',
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openDetail(record)}>查看详情</Button>
                <Button
                  size="small"
                  type="primary"
                  ghost
                  disabled={!record.targetResourceCode}
                  onClick={() => {
                    if (record.targetResourceCode) {
                      openFulfillmentDrawer({ resourceCode: record.targetResourceCode, leadId: record.id });
                    }
                  }}
                >
                  去发货
                </Button>
              </Space>
            ),
          },
        ]}
      />

      <div style={{ marginTop: 16, display: 'flex', justifyContent: 'center' }}>
        <Pagination
          current={pageData.pageNum}
          pageSize={pageData.pageSize}
          total={pageData.total}
          showSizeChanger
          showTotal={(total) => `共 ${total} 条`}
          onChange={(pageNum, pageSize) => loadData(pageNum, pageSize)}
        />
      </div>

      <Drawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        title="线索详情"
        width={560}
        extra={
          <Button type="primary" loading={saving} onClick={handleSave}>
            保存跟进信息
          </Button>
        }
      >
        {currentLead ? (
          <>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="老师称呼">{currentLead.name}</Descriptions.Item>
              <Descriptions.Item label="联系方式">{currentLead.contact}</Descriptions.Item>
              <Descriptions.Item label="来源渠道">{currentLead.channel || '--'}</Descriptions.Item>
              <Descriptions.Item label="tracking_code">{currentLead.trackingCode || '--'}</Descriptions.Item>
              <Descriptions.Item label="来源资源码">{currentLead.targetResourceCode || '--'}</Descriptions.Item>
              <Descriptions.Item label="来源页面">{currentLead.sourcePage}</Descriptions.Item>
              <Descriptions.Item label="咨询内容">{currentLead.message || '未填写'}</Descriptions.Item>
              <Descriptions.Item label="提交时间">{formatDateTime(currentLead.createdAt)}</Descriptions.Item>
              <Descriptions.Item label="最后跟进时间">{formatDateTime(currentLead.lastFollowUpTime)}</Descriptions.Item>
            </Descriptions>

            <Form layout="vertical" form={form} style={{ marginTop: 20 }}>
              <Space style={{ width: '100%' }} align="start">
                <Form.Item
                  label="跟进状态"
                  name="status"
                  rules={[{ required: true, message: '请选择线索状态' }]}
                  style={{ flex: 1 }}
                >
                  <Select options={leadStatusOptions} />
                </Form.Item>
                <Form.Item
                  label="加微状态"
                  name="wechatAddedStatus"
                  rules={[{ required: true, message: '请选择加微状态' }]}
                  style={{ flex: 1 }}
                >
                  <Select options={wechatAddedStatusOptions} />
                </Form.Item>
                <Form.Item
                  label="成交状态"
                  name="dealStatus"
                  rules={[{ required: true, message: '请选择成交状态' }]}
                  style={{ flex: 1 }}
                >
                  <Select options={leadDealStatusOptions} />
                </Form.Item>
              </Space>
              <Form.Item label="跟进备注" name="followUpNote">
                <Input.TextArea rows={6} placeholder="记录老师需求、跟进进度和下次联系要点" />
              </Form.Item>
            </Form>
          </>
        ) : null}
      </Drawer>
    </div>
  );
}

export default AdminLeadPage;
