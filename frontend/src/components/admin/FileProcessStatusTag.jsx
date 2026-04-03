import { Tag } from 'antd';

const STATUS_MAP = {
  pending: { color: 'default', label: '等待生成' },
  processing: { color: 'processing', label: '生成中' },
  success: { color: 'success', label: '预览成功' },
  failed: { color: 'error', label: '生成失败' },
  unsupported: { color: 'warning', label: '暂不支持' },
};

function FileProcessStatusTag({ status }) {
  const config = STATUS_MAP[status] || { color: 'default', label: status || '未知状态' };
  return <Tag color={config.color}>{config.label}</Tag>;
}

export default FileProcessStatusTag;
