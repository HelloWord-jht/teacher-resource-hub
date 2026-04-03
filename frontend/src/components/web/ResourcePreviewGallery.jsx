import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import { Button } from 'antd';
import { useState } from 'react';
import EmptyState from '../common/EmptyState';
import PreviewLightboxModal from './PreviewLightboxModal';
import styles from './ResourcePreviewGallery.module.css';

function ResourcePreviewGallery({ title, images }) {
  const [activeIndex, setActiveIndex] = useState(0);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  if (!images?.length) {
    return <EmptyState description="当前资源暂未配置预览样张" />;
  }

  const safeIndex = activeIndex >= images.length ? 0 : activeIndex;
  const activeImage = images[safeIndex];

  const goPrevious = () => {
    setActiveIndex((current) => (current - 1 + images.length) % images.length);
  };

  const goNext = () => {
    setActiveIndex((current) => (current + 1) % images.length);
  };

  return (
    <>
      <section className={styles.gallery} aria-label="资源预览样张">
        <div className={styles.mainStage}>
          <Button
            type="text"
            className={styles.arrow}
            aria-label="查看上一张预览图"
            onClick={goPrevious}
            icon={<LeftOutlined />}
          />
          <button
            type="button"
            className={styles.imageButton}
            onClick={() => setLightboxOpen(true)}
            aria-label={`放大查看 ${title} 预览图 ${safeIndex + 1}`}
          >
            <img
              src={activeImage}
              alt={`${title}预览图 ${safeIndex + 1}`}
              className={styles.image}
            />
          </button>
          <Button
            type="text"
            className={styles.arrow}
            aria-label="查看下一张预览图"
            onClick={goNext}
            icon={<RightOutlined />}
          />
        </div>

        <div className={styles.toolbar}>
          <strong>样张判断区</strong>
          <span className={styles.counter}>
            {safeIndex + 1} / {images.length}
          </span>
        </div>

        <div className={styles.thumbnailRow}>
          {images.map((imageUrl, index) => (
            <button
              key={`${imageUrl}-${index}`}
              type="button"
              className={`${styles.thumbnail} ${safeIndex === index ? styles.thumbnailActive : ''}`}
              onClick={() => setActiveIndex(index)}
              aria-label={`查看第 ${index + 1} 张预览图`}
            >
              <img src={imageUrl} alt="" aria-hidden="true" />
            </button>
          ))}
        </div>
      </section>

      <PreviewLightboxModal
        open={lightboxOpen}
        title={title}
        images={images}
        activeIndex={safeIndex}
        onChange={setActiveIndex}
        onClose={() => setLightboxOpen(false)}
      />
    </>
  );
}

export default ResourcePreviewGallery;
