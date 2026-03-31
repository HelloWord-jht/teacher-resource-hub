import { Tag } from 'antd';

export const commonStatusOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 },
];

export const resourceStatusOptions = [
  { label: '已发布', value: 1 },
  { label: '下线', value: 0 },
];

export const yesNoOptions = [
  { label: '是', value: 1 },
  { label: '否', value: 0 },
];

export const leadStatusOptions = [
  { label: '待跟进', value: 0 },
  { label: '已联系', value: 1 },
  { label: '已成交', value: 2 },
  { label: '已关闭', value: 3 },
];

export const pageCodeOptions = [
  { label: '关于我们', value: 'about' },
  { label: '联系我们', value: 'contact' },
  { label: '版权与免责声明', value: 'disclaimer' },
  { label: '退款/补发说明', value: 'refund-policy' },
];

export function renderCommonStatusTag(status) {
  return status === 1 ? <Tag color="green">启用</Tag> : <Tag color="default">禁用</Tag>;
}

export function renderResourceStatusTag(status) {
  return status === 1 ? <Tag color="blue">已发布</Tag> : <Tag color="orange">下线</Tag>;
}

export function renderYesNoTag(value) {
  return value === 1 ? <Tag color="success">推荐</Tag> : <Tag>普通</Tag>;
}

export function renderLeadStatusTag(status) {
  if (status === 2) {
    return <Tag color="success">已成交</Tag>;
  }
  if (status === 1) {
    return <Tag color="processing">已联系</Tag>;
  }
  if (status === 3) {
    return <Tag color="default">已关闭</Tag>;
  }
  return <Tag color="warning">待跟进</Tag>;
}
