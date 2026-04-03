import { Button, DatePicker, Form, Input, InputNumber, Modal, Popconfirm, Select, Space, Table, Typography, message } from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import { commonStatusOptions, contentCampaignTypeOptions, renderCommonStatusTag } from '../../../utils/admin.jsx';
import { formatDateTime } from '../../../utils/format';

function AdminContentCampaignPage() {
  const [list, setList] = useState([]);
  const [channels, setChannels] = useState([]);
  const [loading, setLoading] = useState(false);
  const [channelId, setChannelId] = useState(undefined);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form] = Form.useForm();

  const loadData = (nextChannelId = channelId) => {
    setLoading(true);
    Promise.all([
      adminApi.listLeadChannels(),
      adminApi.listContentCampaigns(nextChannelId ? { channelId: nextChannelId } : {}),
    ])
      .then(([channelList, campaignList]) => {
        setChannels(channelList);
        setList(campaignList);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreate = () => {
    setEditingItem(null);
    form.resetFields();
    form.setFieldsValue({
      contentType: 'note',
      status: 1,
      viewCount: 0,
      consultCount: 0,
      wechatAddCount: 0,
      dealCount: 0,
      targetResourceCode: '',
      landingPage: '',
      remark: '',
    });
    setModalOpen(true);
  };

  const openEdit = async (record) => {
    const detail = await adminApi.getContentCampaign(record.id);
    setEditingItem(detail);
    form.setFieldsValue({
      ...detail,
      publishTime: detail.publishTime ? dayjs(detail.publishTime) : null,
    });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const payload = {
      ...values,
      publishTime: values.publishTime ? values.publishTime.format('YYYY-MM-DD HH:mm:ss') : null,
    };
    if (editingItem) {
      await adminApi.updateContentCampaign(editingItem.id, payload);
      message.success('投放内容更新成功');
    } else {
      await adminApi.createContentCampaign(payload);
      message.success('投放内容创建成功');
    }
    setModalOpen(false);
    loadData();
  };

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={3} style={{ margin: 0 }}>内容投放管理</Typography.Title>
        <Space>
          <Select
            allowClear
            style={{ width: 220 }}
            placeholder="按渠道筛选"
            value={channelId}
            options={channels.map((item) => ({ label: item.channelName, value: item.id }))}
            onChange={(value) => {
              setChannelId(value);
              loadData(value);
            }}
          />
          <Button type="primary" onClick={openCreate}>新增投放内容</Button>
        </Space>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        dataSource={list}
        scroll={{ x: 1400 }}
        pagination={false}
        columns={[
          { title: '标题', dataIndex: 'title', width: 220 },
          { title: '渠道', dataIndex: 'channelName', width: 120 },
          { title: '内容类型', dataIndex: 'contentType', width: 120 },
          { title: '追踪码', dataIndex: 'trackingCode', width: 180 },
          { title: '目标资源码', dataIndex: 'targetResourceCode', width: 180 },
          { title: '浏览量', dataIndex: 'viewCount', width: 100 },
          { title: '咨询量', dataIndex: 'consultCount', width: 100 },
          { title: '加微量', dataIndex: 'wechatAddCount', width: 100 },
          { title: '成交量', dataIndex: 'dealCount', width: 100 },
          { title: '发布时间', dataIndex: 'publishTime', width: 170, render: formatDateTime },
          { title: '状态', dataIndex: 'status', width: 100, render: renderCommonStatusTag },
          {
            title: '操作',
            width: 180,
            fixed: 'right',
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openEdit(record)}>编辑</Button>
                <Popconfirm
                  title="确认删除该投放内容吗？"
                  onConfirm={() => adminApi.deleteContentCampaign(record.id).then(() => {
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
        width={760}
        open={modalOpen}
        title={editingItem ? '编辑投放内容' : '新增投放内容'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Space style={{ width: '100%' }} align="start">
            <Form.Item label="所属渠道" name="channelId" rules={[{ required: true, message: '请选择渠道' }]} style={{ flex: 1 }}>
              <Select options={channels.map((item) => ({ label: item.channelName, value: item.id }))} />
            </Form.Item>
            <Form.Item label="内容类型" name="contentType" rules={[{ required: true, message: '请选择内容类型' }]} style={{ width: 180 }}>
              <Select options={contentCampaignTypeOptions} />
            </Form.Item>
          </Space>
          <Form.Item label="投放标题" name="title" rules={[{ required: true, message: '请输入投放标题' }]}>
            <Input />
          </Form.Item>
          <Space style={{ width: '100%' }} align="start">
            <Form.Item label="追踪码" name="trackingCode" rules={[{ required: true, message: '请输入追踪码' }]} style={{ flex: 1 }}>
              <Input />
            </Form.Item>
            <Form.Item label="发布时间" name="publishTime" style={{ width: 220 }}>
              <DatePicker style={{ width: '100%' }} showTime format="YYYY-MM-DD HH:mm:ss" />
            </Form.Item>
          </Space>
          <Space style={{ width: '100%' }} align="start">
            <Form.Item label="目标资源ID" name="targetResourceId" style={{ width: 180 }}>
              <InputNumber style={{ width: '100%' }} min={1} />
            </Form.Item>
            <Form.Item label="目标资源码" name="targetResourceCode" style={{ flex: 1 }}>
              <Input />
            </Form.Item>
          </Space>
          <Form.Item label="落地页地址" name="landingPage">
            <Input />
          </Form.Item>
          <Space style={{ width: '100%' }} align="start">
            <Form.Item label="浏览量" name="viewCount" style={{ width: 140 }}>
              <InputNumber style={{ width: '100%' }} min={0} />
            </Form.Item>
            <Form.Item label="咨询量" name="consultCount" style={{ width: 140 }}>
              <InputNumber style={{ width: '100%' }} min={0} />
            </Form.Item>
            <Form.Item label="加微量" name="wechatAddCount" style={{ width: 140 }}>
              <InputNumber style={{ width: '100%' }} min={0} />
            </Form.Item>
            <Form.Item label="成交量" name="dealCount" style={{ width: 140 }}>
              <InputNumber style={{ width: '100%' }} min={0} />
            </Form.Item>
          </Space>
          <Form.Item label="备注" name="remark">
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

export default AdminContentCampaignPage;
