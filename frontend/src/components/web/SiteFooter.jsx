import { Link, useLocation } from 'react-router-dom';
import { buildAttributedPath } from '../../utils/attribution';
import styles from './SiteFooter.module.css';

function SiteFooter({ siteName, footerText }) {
  const location = useLocation();

  return (
    <footer className={styles.footer}>
      <div className={styles.inner}>
        <div className={styles.brand}>
          <h3>{siteName || '小学课件教案资源导流站'}</h3>
          <p>{footerText || '先看预览，再加微信咨询，帮助老师更快找到能直接使用的备课资料。'}</p>
        </div>

        <div className={styles.links}>
          <Link to={buildAttributedPath('/about', location.search)}>关于我们</Link>
          <Link to={buildAttributedPath('/contact', location.search)}>联系我们</Link>
          <Link to={buildAttributedPath('/disclaimer', location.search)}>版权与免责声明</Link>
          <Link to={buildAttributedPath('/refund-policy', location.search)}>退款/补发说明</Link>
        </div>
      </div>
    </footer>
  );
}

export default SiteFooter;
