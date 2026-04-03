import { Button, Tag, message } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import { copyText } from '../../utils/clipboard';
import { buildResourceDetailPath } from '../../utils/attribution';
import { formatDateTime, formatPrice } from '../../utils/format';
import styles from './ResourceCard.module.css';

function buildValueSummary(resource) {
  if (resource.scene && resource.grade) {
    return `适合 ${resource.grade} 老师在准备 ${resource.scene} 时快速判断是否能直接使用。`;
  }
  if (resource.scene) {
    return `适合正在准备 ${resource.scene} 的老师先看预览再决定是否咨询。`;
  }
  return '不是单个文件，而是完整备课资料包。';
}

function ResourceCard({ resource, onWechatClick, highlighted = false, highlightText = '' }) {
  const location = useLocation();
  const detailPath = buildResourceDetailPath(resource.id, resource.resourceCode, location.search);

  const handleCopyCode = async () => {
    if (!resource.resourceCode) {
      message.warning('当前资源没有可复制的资源码');
      return;
    }
    await copyText(resource.resourceCode);
    message.success('资源码已复制');
  };

  const metrics = [
    resource.previewCount ? `预览 ${resource.previewCount} 张` : '',
    resource.contentItemCount ? `内容 ${resource.contentItemCount} 项` : '',
    resource.updatedAt ? `更新于 ${formatDateTime(resource.updatedAt)}` : '',
  ].filter(Boolean);

  return (
    <article className={`${styles.card} ${highlighted ? styles.highlighted : ''}`}>
      {highlighted ? (
        <div className={styles.highlightBar}>
          <strong>已为你定位目标资源</strong>
          <span>{highlightText || '建议先复制这个资源码，再加微信咨询。'}</span>
        </div>
      ) : null}

      <Link to={detailPath} className={styles.coverWrap}>
        <img
          className={styles.cover}
          src={resource.coverImage}
          alt={resource.title}
          width="640"
          height="400"
          loading="lazy"
        />
      </Link>

      <div className={styles.body}>
        <div className={styles.meta}>
          <span>{resource.grade}</span>
          <span>{resource.scene}</span>
        </div>

        <Link to={detailPath} className={styles.title}>
          {resource.title}
        </Link>

        <p className={styles.summary}>
          {resource.summary || '查看详情页了解包含内容、预览样张、适用场景和微信咨询方式。'}
        </p>

        <div className={styles.codeBox}>
          <span className={styles.codeLabel}>资源码</span>
          <strong className={styles.codeValue}>{resource.resourceCode || '待补充'}</strong>
        </div>

        {metrics.length ? (
          <div className={styles.metrics}>
            {metrics.map((item) => (
              <span key={item}>{item}</span>
            ))}
          </div>
        ) : null}

        <p className={styles.valueText}>{buildValueSummary(resource)}</p>

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
            <p className={styles.tip}>先看预览，再加微信咨询</p>
          </div>
          <div className={styles.actions}>
            <Link to={detailPath}>
              <Button>查看详情</Button>
            </Link>
            <Button type={highlighted ? 'primary' : 'default'} onClick={handleCopyCode}>
              {highlighted ? '复制这个资源码' : '复制资源码'}
            </Button>
            <Button type="primary" onClick={() => onWechatClick?.(resource)}>
              {highlighted ? '直接加微信咨询' : '加微信咨询'}
            </Button>
          </div>
        </div>
      </div>
    </article>
  );
}

export default ResourceCard;
