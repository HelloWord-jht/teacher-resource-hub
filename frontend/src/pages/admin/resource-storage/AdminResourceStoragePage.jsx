import { Button, Form, Input, InputNumber, Modal, Popconfirm, Select, Space, Table, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import { commonStatusOptions, renderCommonStatusTag, renderStoragePlatform, storagePlatformOptions } from '../../../utils/admin.jsx';
import { formatDateTime } from '../../../utils/format';

function AdminResourceStoragePage() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [resourceId, setResourceId] = useState(undefined);
  const [resourceCode, setResourceCode] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form] = Form.useForm();

  const loadData = (nextResourceId = resourceId) => {
    setLoading(true);
    adminApi
      .listResourceStorages(nextResourceId ? { resourceId: nextResourceId } : {})
      .then(setList)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const locateByResourceCode = async () => {
    if (!resourceCode.trim()) {
      setResourceId(undefined);
      loadData(undefined);
      return;
    }
    const detail = await adminApi.getResourceDetailByCode(resourceCode.trim());
    setResourceId(detail.id);
    loadData(detail.id);
    message.success(`已定位资源：${detail.title}`);
  };

  const openCreate = () => {
    setEditingItem(null);
    form.resetFields();
    form.setFieldsValue({
      resourceId,
      storagePlatform: 'baidu_pan',
      shareCode: '',
      extractCode: '',
      deliveryNote: '',
      status: 1,
    });
    setModalOpen(true);
  };

  const openEdit = async (record) => {
    const detail = await adminApi.getResourceStorage(record.id);
    setEditingItem(detail);
    form.setFieldsValue(detail);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    if (editingItem) {
      await adminApi.updateResourceStorage(editingItem.id, values);
      message.success('网盘绑定更新成功');
    } else {
      await adminApi.createResourceStorage(values);
      message.success('网盘绑定创建成功');
    }
    setModalOpen(false);
    loadData();
  };

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={3} style={{ margin: 0 }}>网盘绑定管理</Typography.Title>
        <Button type="primary" onClick={openCreate}>新增网盘绑定</Button>
      </Space>

      <Space wrap style={{ marginBottom: 16 }}>
        <Input.Search
          value={resourceCode}
          onChange={(event) => setResourceCode(event.target.value)}
          onSearch={locateByResourceCode}
          allowClear
          style={{ width: 280 }}
          placeholder="输入资源码定位网盘绑定"
        />
        <Button onClick={() => {
          setResourceCode('');
          setResourceId(undefined);
          loadData(undefined);
        }}>
          重置筛选
        </Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        dataSource={list}
        pagination={false}
        scroll={{ x: 1200 }}
        columns={[
          { title: '资源ID', dataIndex: 'resourceId', width: 100 },
          { title: '网盘平台', dataIndex: 'storagePlatform', width: 140, render: renderStoragePlatform },
          { title: '分享链接', dataIndex: 'shareUrl', ellipsis: true },
          { title: '分享码', dataIndex: 'shareCode', width: 120 },
          { title: '提取码', dataIndex: 'extractCode', width: 120 },
          { title: '交付备注', dataIndex: 'deliveryNote', ellipsis: true },
          { title: '状态', dataIndex: 'status', width: 100, render: renderCommonStatusTag },
          { title: '更新时间', dataIndex: 'updatedAt', width: 170, render: formatDateTime },
          {
            title: '操作',
            width: 180,
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openEdit(record)}>编辑</Button>
                <Popconfirm
                  title="确认删除该网盘绑定吗？"
                  onConfirm={() => adminApi.deleteResourceStorage(record.id).then(() => {
                    message.success('删除成功');
                    loadData();
                  })}
                >
                  <Button size="small" danger>删除</Button>
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />

      <Modal
        width={720}
        open={modalOpen}
        title={editingItem ? '编辑网盘绑定' : '新增网盘绑定'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form layout="vertical" form={form}>
          <Form.Item label="资源ID" name="resourceId" rules={[{ required: true, message: '请输入资源ID' }]}>
            <InputNumber style={{ width: '100%' }} min={1} />
          </Form.Item>
          <Form.Item label="网盘平台" name="storagePlatform" rules={[{ required: true, message: '请选择网盘平台' }]}>
            <Select options={storagePlatformOptions} />
          </Form.Item>
          <Form.Item label="分享链接" name="shareUrl" rules={[{ required: true, message: '请输入分享链接' }]}>
            <Input />
          </Form.Item>
          <Space style={{ width: '100%' }} align="start">
            <Form.Item label="分享码" name="shareCode" style={{ flex: 1 }}>
              <Input />
            </Form.Item>
            <Form.Item label="提取码" name="extractCode" style={{ flex: 1 }}>
              <Input />
            </Form.Item>
          </Space>
          <Form.Item label="交付备注" name="deliveryNote">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[{ required: true, message: '请选择状态' }]}>
            <Select options={commonStatusOptions} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default AdminResourceStoragePage;
