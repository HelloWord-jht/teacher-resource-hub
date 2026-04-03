import { UploadOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Descriptions, Divider, Drawer, Form, Input, Modal, Pagination, Select, Space, Table, Tag, Typography, Upload, message } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminApi } from '../../../api/admin';
import FileProcessStatusTag from '../../../components/admin/FileProcessStatusTag';
import ImportFilePreviewPanel from '../../../components/admin/ImportFilePreviewPanel';
import ImportFileSidebar from '../../../components/admin/ImportFileSidebar';
import LoadingState from '../../../components/common/LoadingState';
import { formatDateTime } from '../../../utils/format';

const importStatusOptions = [
  { label: '待执行', value: 'pending' },
  { label: '执行中', value: 'processing' },
  { label: '部分成功', value: 'partial_success' },
  { label: '执行成功', value: 'success' },
  { label: '执行失败', value: 'failed' },
];

function AdminImportTaskPage() {
  const navigate = useNavigate();
  const [pageData, setPageData] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState(undefined);
  const [modalOpen, setModalOpen] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [currentTask, setCurrentTask] = useState(null);
  const [taskFiles, setTaskFiles] = useState([]);
  const [activeFile, setActiveFile] = useState(null);
  const [activeFilePreviews, setActiveFilePreviews] = useState([]);
  const [activeFileLogs, setActiveFileLogs] = useState([]);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [uploadingFile, setUploadingFile] = useState(false);
  const [executingPreview, setExecutingPreview] = useState(false);
  const [fileActionLoading, setFileActionLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const loadData = (pageNum = pageData.pageNum, pageSize = pageData.pageSize, importStatus = statusFilter) => {
    setLoading(true);
    adminApi
      .listImportTasks({ pageNum, pageSize, importStatus })
      .then(setPageData)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData(1, 10, undefined);
  }, []);

  const loadTaskFiles = async (taskId, preferredFileId) => {
    const files = await adminApi.listImportTaskFiles(taskId);
    setTaskFiles(files || []);
    const nextFile =
      (files || []).find((item) => item.id === preferredFileId) ||
      (files || []).find((item) => item.previewStatus === 'success') ||
      files?.[0] ||
      null;
    if (nextFile) {
      await openFile(nextFile, files);
    } else {
      setActiveFile(null);
      setActiveFilePreviews([]);
      setActiveFileLogs([]);
    }
  };

  const openFile = async (fileRecord, existingFiles = taskFiles) => {
    const targetFile = typeof fileRecord === 'object'
      ? fileRecord
      : (existingFiles || []).find((item) => item.id === fileRecord) || null;
    if (!targetFile) {
      return;
    }
    setPreviewLoading(true);
    try {
      const [previews, logs] = await Promise.all([
        adminApi.getResourceFilePreviews(targetFile.id),
        adminApi.getResourceFileLogs(targetFile.id),
      ]);
      setActiveFile(targetFile);
      setActiveFilePreviews(previews || []);
      setActiveFileLogs(logs || []);
    } finally {
      setPreviewLoading(false);
    }
  };

  const openDetail = async (record) => {
    const detail = await adminApi.getImportTask(record.id);
    setCurrentTask(detail);
    await loadTaskFiles(record.id);
    setDrawerOpen(true);
  };

  const handleCreate = async () => {
    const values = await form.validateFields();
    setSaving(true);
    try {
      await adminApi.createImportTask(values);
      message.success('导入任务创建成功');
      setModalOpen(false);
      form.resetFields();
      loadData();
    } finally {
      setSaving(false);
    }
  };

  const handleExecute = async (record) => {
    await adminApi.executeImportTask(record.id);
    message.success('导入任务已执行');
    loadData();
  };

  const handleExecutePreview = async () => {
    if (!currentTask) {
      return;
    }
    setExecutingPreview(true);
    try {
      await adminApi.executeImportTaskPreview(currentTask.id);
      const detail = await adminApi.getImportTask(currentTask.id);
      setCurrentTask(detail);
      await loadTaskFiles(currentTask.id, activeFile?.id);
      message.success('预览生成任务已执行');
      loadData(pageData.pageNum, pageData.pageSize, statusFilter);
    } finally {
      setExecutingPreview(false);
    }
  };

  const handleUploadFile = async ({ file, onSuccess, onError }) => {
    if (!currentTask) {
      onError?.(new Error('请先打开导入任务详情'));
      return;
    }
    setUploadingFile(true);
    try {
      await adminApi.uploadImportTaskFile(currentTask.id, file);
      const detail = await adminApi.getImportTask(currentTask.id);
      setCurrentTask(detail);
      await loadTaskFiles(currentTask.id);
      message.success('导入文件上传成功');
      onSuccess?.('ok');
      loadData(pageData.pageNum, pageData.pageSize, statusFilter);
    } catch (error) {
      onError?.(error);
    } finally {
      setUploadingFile(false);
    }
  };

  const runFileAction = async (action) => {
    if (!activeFile || !currentTask) {
      return;
    }
    setFileActionLoading(true);
    try {
      await action();
      const detail = await adminApi.getImportTask(currentTask.id);
      setCurrentTask(detail);
      await loadTaskFiles(currentTask.id, activeFile.id);
      loadData(pageData.pageNum, pageData.pageSize, statusFilter);
    } finally {
      setFileActionLoading(false);
    }
  };

  if (loading && !pageData.list.length) {
    return <LoadingState text="正在加载导入任务..." />;
  }

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={3} style={{ margin: 0 }}>导入任务管理</Typography.Title>
        <Space>
          <Select
            allowClear
            style={{ width: 180 }}
            placeholder="按执行状态筛选"
            options={importStatusOptions}
            value={statusFilter}
            onChange={(value) => {
              setStatusFilter(value);
              loadData(1, pageData.pageSize, value);
            }}
          />
          <Button type="primary" onClick={() => setModalOpen(true)}>新建导入任务</Button>
        </Space>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={pageData.list}
        scroll={{ x: 1400 }}
        columns={[
          { title: '任务名称', dataIndex: 'taskName', width: 220 },
          { title: '执行状态', dataIndex: 'importStatus', width: 120, render: (value) => importStatusOptions.find((item) => item.value === value)?.label || value },
          { title: '导入类型', dataIndex: 'importType', width: 120, render: (value) => value || '--' },
          { title: '文件总数', dataIndex: 'totalFileCount', width: 100, render: (value) => value ?? 0 },
          { title: '成功预览', dataIndex: 'previewSuccessCount', width: 100, render: (value) => value ?? 0 },
          { title: '失败文件', dataIndex: 'previewFailedCount', width: 100, render: (value) => value ?? 0 },
          { title: '生成资源码', dataIndex: 'generatedResourceCode', width: 200, render: (value) => value || '--' },
          { title: '推荐分类ID', dataIndex: 'recommendedCategoryId', width: 120, render: (value) => value || '--' },
          { title: '操作人', dataIndex: 'operatorName', width: 120 },
          { title: '执行时间', dataIndex: 'executedAt', width: 170, render: formatDateTime },
          { title: '创建时间', dataIndex: 'createdAt', width: 170, render: formatDateTime },
          {
            title: '操作',
            width: 260,
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => openDetail(record)}>查看详情</Button>
                <Button size="small" type="primary" ghost onClick={() => handleExecute(record)}>执行导入</Button>
                {record.generatedResourceId ? (
                  <Button size="small" onClick={() => navigate(`/admin/resources/${record.generatedResourceId}/edit`)}>
                    去修正资源
                  </Button>
                ) : null}
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
        width={1180}
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        title="导入任务详情与文件样张"
      >
        {currentTask ? (
          <Space direction="vertical" size={16} style={{ width: '100%' }}>
            <Descriptions bordered size="small" column={4}>
              <Descriptions.Item label="任务名称">{currentTask.taskName}</Descriptions.Item>
              <Descriptions.Item label="导入类型">{currentTask.importType || '--'}</Descriptions.Item>
              <Descriptions.Item label="当前状态">{importStatusOptions.find((item) => item.value === currentTask.importStatus)?.label || currentTask.importStatus}</Descriptions.Item>
              <Descriptions.Item label="执行时间">{formatDateTime(currentTask.executedAt)}</Descriptions.Item>
              <Descriptions.Item label="文件总数">{currentTask.totalFileCount ?? 0}</Descriptions.Item>
              <Descriptions.Item label="已识别">{currentTask.recognizedFileCount ?? 0}</Descriptions.Item>
              <Descriptions.Item label="预览成功">{currentTask.previewSuccessCount ?? 0}</Descriptions.Item>
              <Descriptions.Item label="预览失败">{currentTask.previewFailedCount ?? 0}</Descriptions.Item>
              <Descriptions.Item label="不可预览">{currentTask.unsupportedFileCount ?? 0}</Descriptions.Item>
              <Descriptions.Item label="解压状态">{currentTask.unzipStatus || '--'}</Descriptions.Item>
              <Descriptions.Item label="生成资源码">{currentTask.generatedResourceCode || '--'}</Descriptions.Item>
              <Descriptions.Item label="推荐标签" span={1}>{(currentTask.recommendedTagIds || []).join(', ') || '--'}</Descriptions.Item>
            </Descriptions>

            <Alert
              type="info"
              showIcon
              message="上传完就能看到文件列表，点击左侧文件即可在右侧查看前 2 页样张。ZIP 文件会默认打开第一个可预览文件。"
            />

            <Card
              title="导入文件与样张处理"
              extra={(
                <Space>
                  <Upload showUploadList={false} customRequest={handleUploadFile} multiple={false}>
                    <Button icon={<UploadOutlined />} loading={uploadingFile}>
                      上传导入文件
                    </Button>
                  </Upload>
                  <Button type="primary" onClick={handleExecutePreview} loading={executingPreview}>
                    执行预览生成
                  </Button>
                </Space>
              )}
            >
              <div style={{ display: 'grid', gridTemplateColumns: '280px minmax(0, 1fr)', gap: 20 }}>
                <ImportFileSidebar
                  files={taskFiles}
                  activeFileId={activeFile?.id}
                  onSelectFile={openFile}
                />
                {previewLoading ? (
                  <LoadingState text="正在加载文件样张..." />
                ) : (
                  <ImportFilePreviewPanel
                    currentFile={activeFile}
                    previewPages={activeFilePreviews}
                    actionLoading={fileActionLoading}
                    onRefresh={() => openFile(activeFile)}
                    onRegenerate={() => runFileAction(() => adminApi.regenerateResourceFilePreview(activeFile.id))}
                    onDelete={() => runFileAction(() => adminApi.deleteResourceFile(activeFile.id))}
                  />
                )}
              </div>
            </Card>

            <Card title="处理日志">
              {activeFileLogs.length ? (
                <Space direction="vertical" size={12} style={{ width: '100%' }}>
                  {activeFileLogs.map((log) => (
                    <div key={log.id} style={{ padding: 12, border: '1px solid #e2e8f0', borderRadius: 12 }}>
                      <Space size={8} wrap>
                        <Tag>{log.stepName}</Tag>
                        <FileProcessStatusTag status={log.stepStatus === 'skipped' ? 'unsupported' : log.stepStatus} />
                        <Typography.Text type="secondary">{formatDateTime(log.createdAt)}</Typography.Text>
                      </Space>
                      <div style={{ marginTop: 8, color: '#475569' }}>{log.message}</div>
                    </div>
                  ))}
                </Space>
              ) : (
                <Typography.Text type="secondary">当前文件暂时没有处理日志。</Typography.Text>
              )}
            </Card>

            <Divider />

            <div>
              <Typography.Title level={5}>执行结果</Typography.Title>
              <Input.TextArea rows={4} value={currentTask.executionResult || ''} readOnly />
            </div>
            <div>
              <Typography.Title level={5}>原始 JSON</Typography.Title>
              <Input.TextArea rows={14} value={currentTask.rawPayload || ''} readOnly />
            </div>
          </Space>
        ) : null}
      </Drawer>

      <Modal
        width={760}
        open={modalOpen}
        title="新建导入任务"
        onCancel={() => setModalOpen(false)}
        onOk={handleCreate}
        confirmLoading={saving}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item label="任务名称" name="taskName" rules={[{ required: true, message: '请输入任务名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="原始 JSON" name="rawPayload" rules={[{ required: true, message: '请粘贴导入 JSON' }]}>
            <Input.TextArea rows={16} placeholder='{"title":"小学期末复习冲刺资料整合包","sourceId":3}' />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default AdminImportTaskPage;
