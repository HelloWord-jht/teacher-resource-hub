import { Button, Form, Input, Modal, Popconfirm, Select, Space, Table, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import {
  authorizationStatusOptions,
  commonStatusOptions,
  renderAuthorizationStatusTag,
  renderCommonStatusTag,
  renderRiskLevelTag,
  resourceSourceTypeOptions,
  riskLevelOptions,
} from '../../../utils/admin.jsx';
import { formatDateTime } from '../../../utils/format';

function AdminResourceSourcePage() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [authorizationStatus, setAuthorizationStatus] = useState(undefined);
  const [modalOpen, setModalOpen] = useState(false);
  const [auditOpen, setAuditOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [auditItem, setAuditItem] = useState(null);
  const [form] = Form.useForm();
  const [auditForm] = Form.useForm();

  const loadData = (nextStatus = authorizationStatus) => {
    setLoading(true);
    adminApi
      .listResourceSources(nextStatus ? { authorizationStatus: nextStatus } : {})
      .then(setList)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreate = () => {
    setEditingItem(null);
    form.resetFields();
    form.setFieldsValue({
      sourceType: 'self_compiled',
      status: 1,
      ownerName: '',
      ownerContact: '',
      authorizationProofUrl: '',
      auditRemark: '',
    });
    setModalOpen(true);
  };

  const openEdit = async (record) => {
    const detail = await adminApi.getResourceSource(record.id);
    setEditingItem(detail);
    form.setFieldsValue(detail);
    setModalOpen(true);
  };

  const openAudit = async (record) => {
    const detail = await adminApi.getResourceSource(record.id);
    setAuditItem(detail);
    auditForm.setFieldsValue({
      authorizationStatus: detail.authorizationStatus,
      riskLevel: detail.riskLevel,
      auditRemark: detail.auditRemark,
    });
    setAuditOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    if (editingItem) {
      await adminApi.updateResourceSource(editingItem.id, values);
      message.success('资料来源更新成功');
    } else {
      await adminApi.createResourceSource(values);
      message.success('资料来源创建成功');
    }
    setModalOpen(false);
    loadData();
  };

  const handleAudit = async () => {
    const values = await auditForm.validateFields();
    await adminApi.auditResourceSource(auditItem.id, values);
    message.success('审核结果已保存');
    setAuditOpen(false);
    loadData();
  };

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={3} style={{ margin: 0 }}>资料来源与授权审核</Typography.Title>
        <Space>
          <Select
            allowClear
            style={{ width: 180 }}
            placeholder="按审核状态筛选"
            options={authorizationStatusOptions}
            value={authorizationStatus}
            onChange={(value) => {
              setAuthorizationStatus(value);
              loadData(value);
            }}
          />
          <Button type="primary" onClick={openCreate}>新增资料来源</Button>
        </Space>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        dataSource={list}
        scroll={{ x: 1400 }}
        pagination={false}
        columns={[
          { title: '来源名称', dataIndex: 'sourceName', width: 180 },
          { title: '来源类型', dataIndex: 'sourceType', width: 120 },
          { title: '来源方', dataIndex: 'ownerName', width: 140 },
          { title: '联系方式', dataIndex: 'ownerContact', width: 160 },
          { title: '审核状态', dataIndex: 'authorizationStatus', width: 120, render: renderAuthorizationStatusTag },
          { title: '风险等级', dataIndex: 'riskLevel', width: 120, render: renderRiskLevelTag },
          { title: '审核备注', dataIndex: 'auditRemark', ellipsis: true },
          { title: '审核时间', dataIndex: 'auditedAt', width: 170, render: formatDateTime },
          { title: '状态', dataIndex: 'status', width: 100, render: renderCommonStatusTag },
          {
            title: '操作',
            width: 220,
            fixed: 'right',
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openEdit(record)}>编辑</Button>
                <Button size="small" type="primary" ghost onClick={() => openAudit(record)}>审核</Button>
                <Popconfirm
                  title="确认按当前状态重新查看吗？"
                  onConfirm={() => adminApi.getResourceSource(record.id).then(() => message.success('已刷新详情'))}
                >
                  <Button size="small">刷新</Button>
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />

      <Modal
        width={720}
        open={modalOpen}
        title={editingItem ? '编辑资料来源' : '新增资料来源'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form layout="vertical" form={form}>
          <Form.Item label="来源名称" name="sourceName" rules={[{ required: true, message: '请输入来源名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="来源类型" name="sourceType" rules={[{ required: true, message: '请选择来源类型' }]}>
            <Select options={resourceSourceTypeOptions} />
          </Form.Item>
          <Space style={{ width: '100%' }} align="start">
            <Form.Item label="来源方名称" name="ownerName" style={{ flex: 1 }}>
              <Input />
            </Form.Item>
            <Form.Item label="联系方式" name="ownerContact" style={{ flex: 1 }}>
              <Input />
            </Form.Item>
          </Space>
          <Form.Item label="授权证明材料地址" name="authorizationProofUrl">
            <Input />
          </Form.Item>
          <Form.Item label="来源备注" name="auditRemark">
            <Input.TextArea rows={4} />
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[{ required: true, message: '请选择状态' }]}>
            <Select options={commonStatusOptions} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        open={auditOpen}
        title={`授权审核${auditItem ? `：${auditItem.sourceName}` : ''}`}
        onCancel={() => setAuditOpen(false)}
        onOk={handleAudit}
        destroyOnClose
      >
        <Form layout="vertical" form={auditForm}>
          <Form.Item label="审核结果" name="authorizationStatus" rules={[{ required: true, message: '请选择审核结果' }]}>
            <Select options={authorizationStatusOptions} />
          </Form.Item>
          <Form.Item label="风险等级" name="riskLevel" rules={[{ required: true, message: '请选择风险等级' }]}>
            <Select options={riskLevelOptions} />
          </Form.Item>
          <Form.Item label="审核备注" name="auditRemark">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default AdminResourceSourcePage;
