import { Modal, QRCode } from 'antd';

function WechatConsultModal({ open, consultInfo, onClose }) {
  return (
    <Modal
      open={open}
      onCancel={onClose}
      footer={null}
      centered
      title="加微信咨询"
      width={420}
    >
      <div style={{ textAlign: 'center' }}>
        <p style={{ marginBottom: 12, color: '#334155' }}>
          {consultInfo?.wechatTip || '先看预览样张，再添加微信咨询资源内容与交付方式。'}
        </p>
        <div style={{ display: 'inline-block', padding: 16, background: '#f8fafc', borderRadius: 16 }}>
          {consultInfo?.wechatQrUrl ? (
            <img
              src={consultInfo.wechatQrUrl}
              alt="微信二维码"
              style={{ width: 220, height: 220, objectFit: 'cover', borderRadius: 12 }}
            />
          ) : (
            <QRCode value={consultInfo?.wechatId || 'teacherziyuan001'} size={220} />
          )}
        </div>
        <div style={{ marginTop: 16, fontSize: 16, fontWeight: 600, color: '#0f172a' }}>
          微信号：{consultInfo?.wechatId || 'teacherziyuan001'}
        </div>
        <p style={{ marginTop: 8, color: '#64748b', lineHeight: 1.8 }}>
          建议添加时备注：资源名称 + 使用年级 + 课堂场景
        </p>
      </div>
    </Modal>
  );
}

export default WechatConsultModal;
