import { Button, Form, Input, InputNumber, Modal, Popconfirm, Select, Space, Table, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import { commonStatusOptions, renderCommonStatusTag } from '../../../utils/admin.jsx';

function AdminFaqPage() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form] = Form.useForm();

  const loadData = () => {
    setLoading(true);
    adminApi.listFaqs().then(setList).finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreate = () => {
    setEditingItem(null);
    form.resetFields();
    form.setFieldsValue({ sortOrder: 0, status: 1 });
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
      await adminApi.updateFaq(editingItem.id, values);
      message.success('FAQ 更新成功');
    } else {
      await adminApi.createFaq(values);
      message.success('FAQ 创建成功');
    }
    setModalOpen(false);
    loadData();
  };

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }}>
        <Typography.Title level={3} style={{ margin: 0 }}>FAQ 管理</Typography.Title>
        <Button type="primary" onClick={openCreate}>新增 FAQ</Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={list}
        columns={[
          { title: '问题', dataIndex: 'question' },
          {
            title: '回答',
            dataIndex: 'answer',
            render: (value) => <div style={{ color: '#475569' }}>{value}</div>,
          },
          { title: '排序值', dataIndex: 'sortOrder', width: 100 },
          { title: '状态', dataIndex: 'status', width: 100, render: renderCommonStatusTag },
          {
            title: '操作',
            width: 240,
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openEdit(record)}>编辑</Button>
                <Button
                  size="small"
                  onClick={() =>
                    adminApi.updateFaqStatus(record.id, { status: record.status === 1 ? 0 : 1 }).then(() => {
                      message.success('状态更新成功');
                      loadData();
                    })
                  }
                >
                  {record.status === 1 ? '停用' : '启用'}
                </Button>
                <Popconfirm
                  title="确认删除该 FAQ 吗？"
                  onConfirm={() =>
                    adminApi.deleteFaq(record.id).then(() => {
                      message.success('删除成功');
                      loadData();
                    })
                  }
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
        title={editingItem ? '编辑 FAQ' : '新增 FAQ'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form layout="vertical" form={form}>
          <Form.Item label="问题" name="question" rules={[{ required: true, message: '请输入问题' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="回答" name="answer" rules={[{ required: true, message: '请输入回答内容' }]}>
            <Input.TextArea rows={5} />
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

export default AdminFaqPage;
