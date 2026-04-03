import styles from './ServicePromiseBar.module.css';

function ServicePromiseBar({ items, title = '服务承诺' }) {
  if (!items?.length) {
    return null;
  }

  return (
    <section className={styles.bar} aria-label={title}>
      <strong className={styles.title}>{title}</strong>
      <div className={styles.items}>
        {items.map((item) => (
          <span key={item} className={styles.item}>
            {item}
          </span>
        ))}
      </div>
    </section>
  );
}

export default ServicePromiseBar;
