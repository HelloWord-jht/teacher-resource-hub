import styles from './DeliveryFlowPanel.module.css';

function DeliveryFlowPanel({ steps, title = '交付流程' }) {
  if (!steps?.length) {
    return null;
  }

  return (
    <section className={styles.panel} aria-label={title}>
      <h3>{title}</h3>
      <div className={styles.grid}>
        {steps.map((item, index) => (
          <div key={`${item}-${index}`} className={styles.card}>
            <span className={styles.index}>{String(index + 1).padStart(2, '0')}</span>
            <strong>{item}</strong>
            <p>
              {index === 2
                ? '复制资源码后再咨询，管理员能更快完成资源匹配与发货。'
                : index === steps.length - 1
                  ? '确认需求后通过微信沟通交付，并支持链接失效补发。'
                  : '步骤固定清晰，尽量减少老师来回沟通的时间。'}
            </p>
          </div>
        ))}
      </div>
    </section>
  );
}

export default DeliveryFlowPanel;
