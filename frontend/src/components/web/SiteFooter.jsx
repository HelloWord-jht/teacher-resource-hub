import { Link } from 'react-router-dom';
import styles from './SiteFooter.module.css';

function SiteFooter({ siteName, footerText }) {
  return (
    <footer className={styles.footer}>
      <div className={styles.inner}>
        <div className={styles.brand}>
          <h3>{siteName || '小学课件教案资源导流站'}</h3>
          <p>{footerText || '先看预览，再加微信咨询，帮助老师更快找到能直接使用的备课资料。'}</p>
        </div>

        <div className={styles.links}>
          <Link to="/about">关于我们</Link>
          <Link to="/contact">联系我们</Link>
          <Link to="/disclaimer">版权与免责声明</Link>
          <Link to="/refund-policy">退款/补发说明</Link>
        </div>
      </div>
    </footer>
  );
}

export default SiteFooter;
