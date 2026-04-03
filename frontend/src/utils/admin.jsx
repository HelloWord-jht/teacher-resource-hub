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
  { label: '跟进中', value: 1 },
  { label: '已完成', value: 2 },
  { label: '无效', value: 3 },
];

export const authorizationStatusOptions = [
  { label: '已通过', value: 'APPROVED' },
  { label: '待审核', value: 'PENDING' },
  { label: '已拒绝', value: 'REJECTED' },
  { label: '风险待定', value: 'RISK' },
];

export const riskLevelOptions = [
  { label: '低风险', value: 'LOW' },
  { label: '中风险', value: 'MEDIUM' },
  { label: '高风险', value: 'HIGH' },
];

export const resourceSourceTypeOptions = [
  { label: '自营整理', value: 'self_compiled' },
  { label: '合作授权', value: 'cooperation' },
  { label: '投稿资料', value: 'submission' },
  { label: '外部整理', value: 'curated' },
];

export const storagePlatformOptions = [
  { label: '百度网盘', value: 'baidu_pan' },
  { label: '夸克网盘', value: 'quark_pan' },
  { label: '阿里云盘', value: 'aliyun_pan' },
  { label: '天翼云盘', value: 'tianyi_pan' },
];

export const contentCampaignTypeOptions = [
  { label: '笔记', value: 'note' },
  { label: '图文', value: 'image_post' },
  { label: '文章', value: 'article' },
  { label: '落地页', value: 'landing_page' },
];

export const channelTypeOptions = [
  { label: '网站', value: 'website' },
  { label: '小红书', value: 'xiaohongshu' },
  { label: '公众号', value: 'wechat_official' },
  { label: '其他', value: 'other' },
];

export const wechatAddedStatusOptions = [
  { label: '未加微信', value: 0 },
  { label: '已加微信', value: 1 },
];

export const leadDealStatusOptions = [
  { label: '未成交', value: 0 },
  { label: '已成交', value: 1 },
  { label: '已关闭', value: 2 },
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
    return <Tag color="success">已完成</Tag>;
  }
  if (status === 1) {
    return <Tag color="processing">跟进中</Tag>;
  }
  if (status === 3) {
    return <Tag color="default">无效</Tag>;
  }
  return <Tag color="warning">待跟进</Tag>;
}

export function renderAuthorizationStatusTag(status) {
  if (status === 'APPROVED') {
    return <Tag color="green">已通过</Tag>;
  }
  if (status === 'REJECTED') {
    return <Tag color="red">已拒绝</Tag>;
  }
  if (status === 'RISK') {
    return <Tag color="orange">风险待定</Tag>;
  }
  return <Tag color="gold">待审核</Tag>;
}

export function renderRiskLevelTag(level) {
  if (level === 'HIGH') {
    return <Tag color="red">高风险</Tag>;
  }
  if (level === 'MEDIUM') {
    return <Tag color="orange">中风险</Tag>;
  }
  return <Tag color="green">低风险</Tag>;
}

export function renderStoragePlatform(platform) {
  return storagePlatformOptions.find((item) => item.value === platform)?.label || platform || '--';
}

export function renderWechatAddedTag(status) {
  return status === 1 ? <Tag color="green">已加微信</Tag> : <Tag color="default">未加微信</Tag>;
}

export function renderLeadDealStatusTag(status) {
  if (status === 1) {
    return <Tag color="success">已成交</Tag>;
  }
  if (status === 2) {
    return <Tag color="default">已关闭</Tag>;
  }
  return <Tag color="warning">未成交</Tag>;
}
