import { Typography } from 'antd';
import CopyActionGroup from './CopyActionGroup';
import styles from './QuickConsultPanel.module.css';

function QuickConsultPanel({
  resourceCode,
  wechatId,
  onWechatClick,
  onScrollToLeadForm,
  deliveryNotice,
  servicePromiseItems,
}) {
  return (
    <aside className={styles.panel}>
      <p className={styles.eyebrow}>咨询操作区</p>
      <Typography.Title level={4} className={styles.title}>
        先看预览，再咨询是否适合你的年级和场景
      </Typography.Title>
      <Typography.Paragraph className={styles.summary}>
        本站不做在线支付。建议先复制资源码，加微信时直接发送给我们，可更快匹配和交付。
      </Typography.Paragraph>

      <div className={styles.codeBox}>
        <span>当前资源码</span>
        <strong>{resourceCode || '待补充'}</strong>
      </div>

      <CopyActionGroup
        resourceCode={resourceCode}
        wechatId={wechatId}
        onWechatClick={onWechatClick}
        onScrollToLeadForm={onScrollToLeadForm}
        showLeadButton
      />

      <div className={styles.promiseList}>
        <h3>服务承诺</h3>
        <ul>
          {(servicePromiseItems || []).map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      </div>

      <div className={styles.deliveryBox}>
        <h3>交付说明</h3>
        <p>{deliveryNotice}</p>
      </div>
    </aside>
  );
}

export default QuickConsultPanel;
