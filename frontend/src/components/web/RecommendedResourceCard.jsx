import { Button } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import { buildResourceDetailPath } from '../../utils/attribution';
import { formatPrice } from '../../utils/format';
import styles from './RecommendedResourceCard.module.css';

function RecommendedResourceCard({ resource, onWechatClick }) {
  const location = useLocation();
  const detailPath = buildResourceDetailPath(resource.id, resource.resourceCode, location.search);

  return (
    <article className={styles.card}>
      <Link to={detailPath} className={styles.coverWrap}>
        <img src={resource.coverImage} alt={resource.title} className={styles.cover} loading="lazy" />
      </Link>
      <div className={styles.body}>
        <div className={styles.meta}>
          {resource.scene ? <span>{resource.scene}</span> : null}
          {resource.grade ? <span>{resource.grade}</span> : null}
        </div>
        <Link to={detailPath} className={styles.title}>
          {resource.title}
        </Link>
        {resource.summary ? <p className={styles.summary}>{resource.summary}</p> : null}
        {resource.resourceCode ? (
          <div className={styles.codeBox}>
            <span>资源码</span>
            <strong>{resource.resourceCode}</strong>
          </div>
        ) : null}
        <div className={styles.bottom}>
          <strong className={styles.price}>{formatPrice(resource.displayPrice)}</strong>
          <div className={styles.actions}>
            <Link to={detailPath}>
              <Button>查看详情</Button>
            </Link>
            <Button type="primary" onClick={() => onWechatClick?.(resource)}>
              加微信咨询
            </Button>
          </div>
        </div>
      </div>
    </article>
  );
}

export default RecommendedResourceCard;
