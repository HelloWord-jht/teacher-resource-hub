import { Button, Form, Input, InputNumber, Modal, Popconfirm, Select, Space, Table, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import { channelTypeOptions, commonStatusOptions, renderCommonStatusTag } from '../../../utils/admin.jsx';

function AdminLeadChannelPage() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form] = Form.useForm();

  const loadData = () => {
    setLoading(true);
    adminApi.listLeadChannels().then(setList).finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreate = () => {
    setEditingItem(null);
    form.resetFields();
    form.setFieldsValue({ channelType: 'website', sortOrder: 0, status: 1, description: '' });
    setModalOpen(true);
  };

  const openEdit = (record) => {
    setEditingItem(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    if (editingItem) {
      await adminApi.updateLeadChannel(editingItem.id, values);
      message.success('渠道更新成功');
    } else {
      await adminApi.createLeadChannel(values);
      message.success('渠道创建成功');
    }
    setModalOpen(false);
    loadData();
  };

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }}>
        <Typography.Title level={3} style={{ margin: 0 }}>渠道管理</Typography.Title>
        <Button type="primary" onClick={openCreate}>新增渠道</Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={list}
        columns={[
          { title: '渠道名称', dataIndex: 'channelName', width: 160 },
          { title: '渠道标识', dataIndex: 'channelKey', width: 180 },
          { title: '渠道类型', dataIndex: 'channelType', width: 140 },
          { title: '说明', dataIndex: 'description' },
          { title: '排序值', dataIndex: 'sortOrder', width: 100 },
          { title: '状态', dataIndex: 'status', width: 100, render: renderCommonStatusTag },
          {
            title: '操作',
            width: 180,
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openEdit(record)}>编辑</Button>
                <Popconfirm
                  title="确认删除该渠道吗？"
                  onConfirm={() => adminApi.deleteLeadChannel(record.id).then(() => {
                    message.success('渠道删除成功');
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
        open={modalOpen}
        title={editingItem ? '编辑渠道' : '新增渠道'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form layout="vertical" form={form}>
          <Form.Item label="渠道名称" name="channelName" rules={[{ required: true, message: '请输入渠道名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="渠道标识" name="channelKey" rules={[{ required: true, message: '请输入渠道标识' }]}>
            <Input placeholder="例如：xiaohongshu" />
          </Form.Item>
          <Form.Item label="渠道类型" name="channelType" rules={[{ required: true, message: '请选择渠道类型' }]}>
            <Select options={channelTypeOptions} />
          </Form.Item>
          <Form.Item label="渠道说明" name="description">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="排序值" name="sortOrder" rules={[{ required: true, message: '请输入排序值' }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item label="状态" name="status" rules={[{ required: true, message: '请选择状态' }]}>
            <Select options={commonStatusOptions} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default AdminLeadChannelPage;
