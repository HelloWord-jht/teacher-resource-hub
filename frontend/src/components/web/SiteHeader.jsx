import { Link, NavLink, useLocation } from 'react-router-dom';
import { Button } from 'antd';
import { buildAttributedPath, buildResourceListPath } from '../../utils/attribution';
import styles from './SiteHeader.module.css';

const navList = [
  { path: '/', label: '首页' },
  { path: '/resources', label: '资源中心' },
  { path: '/about', label: '关于我们' },
  { path: '/contact', label: '联系我们' },
];

function SiteHeader({ siteName, onWechatClick }) {
  const location = useLocation();
  const homePath = buildAttributedPath('/', location.search);
  const resourcePath = buildResourceListPath(location.search);
  const aboutPath = buildAttributedPath('/about', location.search);
  const contactPath = buildAttributedPath('/contact', location.search);
  const pathMap = {
    '/': homePath,
    '/resources': resourcePath,
    '/about': aboutPath,
    '/contact': contactPath,
  };

  return (
    <header className={styles.header}>
      <div className={styles.inner}>
        <Link to={homePath} className={styles.logo}>
          <span className={styles.logoBadge}>教</span>
          <div>
            <strong>{siteName || '小学课件教案资源导流站'}</strong>
            <p>先看预览，再加微信咨询</p>
          </div>
        </Link>

        <nav className={styles.nav}>
          {navList.map((item) => (
            <NavLink
              key={item.path}
              to={pathMap[item.path] || item.path}
              end={item.path === '/'}
              className={({ isActive }) =>
                `${styles.navItem} ${
                  isActive || (item.path !== '/' && location.pathname.startsWith(item.path))
                    ? styles.active
                    : ''
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className={styles.action}>
          <Button type="primary" onClick={onWechatClick}>
            加微信咨询
          </Button>
        </div>
      </div>
    </header>
  );
}

export default SiteHeader;
