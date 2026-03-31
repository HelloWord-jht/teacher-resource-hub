import { Button, Descriptions, Drawer, Form, Input, Pagination, Select, Space, Table, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';
import { formatDateTime } from '../../../utils/format';
import { leadStatusOptions, renderLeadStatusTag } from '../../../utils/admin.jsx';

function AdminLeadPage() {
  const [pageData, setPageData] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState(undefined);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [currentLead, setCurrentLead] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const loadData = (pageNum = pageData.pageNum, pageSize = pageData.pageSize, status = statusFilter) => {
    setLoading(true);
    adminApi
      .getLeadPage({
        pageNum,
        pageSize,
        status,
      })
      .then(setPageData)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData(1, 10, undefined);
  }, []);

  const openDetail = async (record) => {
    const detail = await adminApi.getLeadDetail(record.id);
    setCurrentLead(detail);
    form.setFieldsValue({
      status: detail.status,
      followUpNote: detail.followUpNote,
    });
    setDrawerOpen(true);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    setSaving(true);
    try {
      await adminApi.updateLeadStatus(currentLead.id, values);
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
        <Typography.Title level={3} style={{ margin: 0 }}>线索管理</Typography.Title>
        <Select
          allowClear
          style={{ width: 220 }}
          placeholder="按状态筛选"
          options={leadStatusOptions}
          value={statusFilter}
          onChange={(value) => {
            setStatusFilter(value);
            loadData(1, pageData.pageSize, value);
          }}
        />
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={pageData.list}
        columns={[
          { title: '老师称呼', dataIndex: 'name', width: 120 },
          { title: '联系方式', dataIndex: 'contact', width: 180 },
          { title: '来源页面', dataIndex: 'sourcePage' },
          {
            title: '状态',
            dataIndex: 'status',
            width: 120,
            render: renderLeadStatusTag,
          },
          {
            title: '提交时间',
            dataIndex: 'createdAt',
            width: 170,
            render: formatDateTime,
          },
          {
            title: '操作',
            width: 120,
            render: (_, record) => <Button size="small" onClick={() => openDetail(record)}>查看详情</Button>,
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
        width={520}
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
              <Descriptions.Item label="来源页面">{currentLead.sourcePage}</Descriptions.Item>
              <Descriptions.Item label="咨询内容">{currentLead.message || '未填写'}</Descriptions.Item>
              <Descriptions.Item label="提交时间">{formatDateTime(currentLead.createdAt)}</Descriptions.Item>
            </Descriptions>

            <Form layout="vertical" form={form} style={{ marginTop: 20 }}>
              <Form.Item label="线索状态" name="status" rules={[{ required: true, message: '请选择线索状态' }]}>
                <Select options={leadStatusOptions} />
              </Form.Item>
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
