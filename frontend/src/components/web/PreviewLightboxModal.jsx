import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import { Button, Modal } from 'antd';
import styles from './PreviewLightboxModal.module.css';

function PreviewLightboxModal({ open, title, images, activeIndex, onClose, onChange }) {
  if (!images?.length) {
    return null;
  }

  const currentIndex = activeIndex < 0 ? 0 : activeIndex;
  const currentImage = images[currentIndex];

  const handlePrevious = () => {
    onChange((currentIndex - 1 + images.length) % images.length);
  };

  const handleNext = () => {
    onChange((currentIndex + 1) % images.length);
  };

  return (
    <Modal open={open} onCancel={onClose} footer={null} width={960} centered className={styles.modal} destroyOnHidden>
      <div className={styles.viewer}>
        <div className={styles.topBar}>
          <strong>{title}</strong>
          <span>
            {currentIndex + 1} / {images.length}
          </span>
        </div>
        <div className={styles.imageWrap}>
          <Button
            type="text"
            className={styles.arrow}
            aria-label="查看上一张预览图"
            onClick={handlePrevious}
            icon={<LeftOutlined />}
          />
          <img src={currentImage} alt={`${title}大图预览 ${currentIndex + 1}`} className={styles.image} />
          <Button
            type="text"
            className={styles.arrow}
            aria-label="查看下一张预览图"
            onClick={handleNext}
            icon={<RightOutlined />}
          />
        </div>
      </div>
    </Modal>
  );
}

export default PreviewLightboxModal;
