import { Button, Tag } from 'antd';
import { Link } from 'react-router-dom';
import { formatPrice } from '../../utils/format';
import styles from './ResourceCard.module.css';

function ResourceCard({ resource, onWechatClick }) {
  return (
    <article className={styles.card}>
      <Link to={`/resources/${resource.id}`} className={styles.coverWrap}>
        <img className={styles.cover} src={resource.coverImage} alt={resource.title} />
      </Link>

      <div className={styles.body}>
        <div className={styles.meta}>
          <span>{resource.grade}</span>
          <span>{resource.scene}</span>
        </div>

        <Link to={`/resources/${resource.id}`} className={styles.title}>
          {resource.title}
        </Link>

        <p className={styles.summary}>
          {resource.summary || '查看详情页了解包含内容、预览样张、适用场景和微信咨询方式。'}
        </p>

        <div className={styles.tags}>
          {(resource.tags || []).map((tag) => (
            <Tag key={tag.id} bordered={false} color="blue">
              {tag.name}
            </Tag>
          ))}
        </div>

        <div className={styles.bottom}>
          <div>
            <strong className={styles.price}>{formatPrice(resource.displayPrice)}</strong>
            <p className={styles.tip}>先看预览，再咨询交付方式</p>
          </div>
          <div className={styles.actions}>
            <Link to={`/resources/${resource.id}`}>
              <Button>查看详情</Button>
            </Link>
            <Button type="primary" onClick={() => onWechatClick(resource)}>
              加微信咨询
            </Button>
          </div>
        </div>
      </div>
    </article>
  );
}

export default ResourceCard;
