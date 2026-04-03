import { Button, Divider, Form, Input, Modal, QRCode, Space, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { webApi } from '../../api/web';
import { buildLeadPayloadFromContext } from '../../utils/attribution';
import { copyText } from '../../utils/clipboard';
import styles from './WechatConsultModal.module.css';

function WechatConsultModal({ open, consultInfo, modalContext, onClose }) {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);
  const [copiedState, setCopiedState] = useState('');

  useEffect(() => {
    if (!open) {
      form.resetFields();
      setCopiedState('');
    }
  }, [form, open]);

  const handleCopy = async (value, copyType, successText) => {
    if (!value) {
      message.warning('当前没有可复制内容');
      return;
    }
    await copyText(value);
    setCopiedState(copyType);
    message.success(successText);
  };

  const handleSubmit = async (values) => {
    setSubmitting(true);
    try {
      await webApi.createLead(
        buildLeadPayloadFromContext({
          values,
          modalContext,
        }),
      );
      message.success('咨询信息已提交，请留意微信回复');
      form.resetFields();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      open={open}
      onCancel={onClose}
      footer={null}
      centered
      title="加微信咨询"
      width={560}
      className={styles.modal}
      destroyOnHidden
    >
      <div className={styles.layout}>
        <div className={styles.introBlock}>
          <p className={styles.tipText}>
            {consultInfo?.wechatTip || '先看预览样张，再添加微信咨询资源内容与交付方式。'}
          </p>

          {(modalContext?.resourceTitle || modalContext?.resourceCode) ? (
            <div className={styles.contextCard}>
              <Typography.Text strong className={styles.contextTitle}>
                {modalContext?.resourceTitle || '当前资源'}
              </Typography.Text>
              <Typography.Text code className={styles.contextCode}>
                {modalContext?.resourceCode || '未识别资源码'}
              </Typography.Text>
              <p className={styles.contextDesc}>
                加微信时发送资源码，可更快为你匹配和交付。
              </p>
            </div>
          ) : null}

          <div className={styles.qrPanel}>
            {consultInfo?.wechatQrUrl ? (
              <img
                src={consultInfo.wechatQrUrl}
                alt="微信二维码"
                width="220"
                height="220"
                loading="lazy"
                className={styles.qrImage}
              />
            ) : (
              <QRCode value={consultInfo?.wechatId || 'teacherziyuan001'} size={220} />
            )}
          </div>

          <div className={styles.contactBlock}>
            <Typography.Title level={5} className={styles.contactTitle}>
              微信号：{consultInfo?.wechatId || 'teacherziyuan001'}
            </Typography.Title>
            <Typography.Paragraph className={styles.contactDesc}>
              本站不做在线支付，确认需求后通过微信沟通交付。建议添加时直接发送资源码。
            </Typography.Paragraph>
          </div>

          <Space direction="vertical" size={10} style={{ width: '100%' }}>
            <Button block onClick={() => handleCopy(consultInfo?.wechatId || 'teacherziyuan001', 'wechatId', '微信号已复制')}>
              复制微信号
            </Button>
            {modalContext?.resourceCode ? (
              <Button block onClick={() => handleCopy(modalContext.resourceCode, 'resourceCode', '资源码已复制')}>
                复制资源码
              </Button>
            ) : null}
            <Button type="primary" block onClick={onClose}>
              我已复制，去微信咨询
            </Button>
          </Space>

          {copiedState === 'resourceCode' ? (
            <div className={styles.statusHint} role="status" aria-live="polite">已复制资源码，建议下一步加微信后直接发送给我们。</div>
          ) : null}
          {copiedState === 'wechatId' ? (
            <div className={styles.statusHint} role="status" aria-live="polite">已复制微信号，下一步可添加微信后发送资源码，更快确认是否适合你的年级和场景。</div>
          ) : null}
        </div>

        <div className={styles.formBlock}>
          <h3>也可以先提交咨询信息</h3>
          <p className={styles.formDesc}>
            表单会自动带上当前资源码和来源归因信息，方便后续更快跟进。
          </p>
          <Form layout="vertical" form={form} onFinish={handleSubmit}>
            <Form.Item
              label="老师称呼"
              name="name"
              rules={[{ required: true, message: '请填写老师称呼' }]}
            >
              <Input placeholder="例如：张老师…" autoComplete="name" />
            </Form.Item>
            <Form.Item
              label="联系方式"
              name="contact"
              rules={[{ required: true, message: '请填写手机号或微信号' }]}
            >
              <Input placeholder="手机号或微信号…" autoComplete="off" spellCheck={false} />
            </Form.Item>
            <Form.Item label="咨询内容" name="message">
              <Input.TextArea
                rows={4}
                placeholder={`建议说明资源码 ${modalContext?.resourceCode || '当前资源'}、使用年级和计划上课时间…`}
              />
            </Form.Item>
            <Divider />
            <Button type="primary" htmlType="submit" block loading={submitting}>
              提交咨询信息
            </Button>
          </Form>
        </div>
      </div>
    </Modal>
  );
}

export default WechatConsultModal;
