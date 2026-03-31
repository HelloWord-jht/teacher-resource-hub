import styles from './PageBanner.module.css';

function PageBanner({ title, description, extra }) {
  return (
    <section className={styles.banner}>
      <div className={styles.inner}>
        <div>
          <p className={styles.eyebrow}>小学老师高频备课场景</p>
          <h1>{title}</h1>
          {description ? <p className={styles.desc}>{description}</p> : null}
        </div>
        {extra ? <div className={styles.extra}>{extra}</div> : null}
      </div>
    </section>
  );
}

export default PageBanner;
