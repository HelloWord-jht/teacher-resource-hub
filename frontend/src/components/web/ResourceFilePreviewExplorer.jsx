import { FileTextOutlined, PictureOutlined } from '@ant-design/icons';
import { Button, Tag } from 'antd';
import { useEffect, useMemo, useState } from 'react';
import EmptyState from '../common/EmptyState';
import PreviewLightboxModal from './PreviewLightboxModal';
import styles from './ResourceFilePreviewExplorer.module.css';

function formatFileTypeLabel(fileType) {
  if (!fileType) {
    return '文件';
  }
  return String(fileType).toUpperCase();
}

function ResourceFilePreviewExplorer({
  files = [],
  activeFileId,
  activeFileName,
  previewPages = [],
  onFileSelect,
}) {
  const [activePageNo, setActivePageNo] = useState(1);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  useEffect(() => {
    setActivePageNo(previewPages[0]?.pageNo || 1);
  }, [activeFileId, previewPages]);

  const currentPage = useMemo(() => {
    return previewPages.find((page) => page.pageNo === activePageNo) || previewPages[0] || null;
  }, [activePageNo, previewPages]);

  const lightboxImages = useMemo(
    () => previewPages.filter((page) => page.previewType === 'image' && page.previewImageUrl).map((page) => page.previewImageUrl),
    [previewPages],
  );
  const lightboxIndex = Math.max(
    lightboxImages.findIndex((imageUrl) => imageUrl === currentPage?.previewImageUrl),
    0,
  );

  if (!files.length) {
    return <EmptyState description="当前资源暂未生成可展示的文件样张" />;
  }

  return (
    <>
      <section className={styles.explorer}>
        <aside className={styles.sidebar} aria-label="文件列表">
          {files.map((file) => (
            <button
              key={file.id}
              type="button"
              className={`${styles.fileButton} ${file.id === activeFileId ? styles.fileButtonActive : ''}`}
              onClick={() => onFileSelect?.(file)}
              aria-pressed={file.id === activeFileId}
            >
              <div className={styles.fileMain}>
                <strong>{file.fileName}</strong>
                <span>{formatFileTypeLabel(file.fileType)}</span>
              </div>
              <div className={styles.fileMeta}>
                {file.primary ? <Tag color="blue">主展示文件</Tag> : null}
                <span>{file.previewPageCount > 0 ? `${file.previewPageCount} 页样张` : '暂不支持样张'}</span>
              </div>
            </button>
          ))}
        </aside>

        <div className={styles.previewPanel}>
          <div className={styles.panelHeader}>
            <div>
              <strong>当前查看文件：{activeFileName || files.find((file) => file.id === activeFileId)?.fileName || '未选择文件'}</strong>
              <p>当前展示为样张预览，仅展示前 2 页</p>
            </div>
            {previewPages.length ? (
              <span className={styles.pageSummary}>第 {currentPage?.pageNo || 1} 页 / 共 {previewPages.length} 页样张</span>
            ) : null}
          </div>

          {previewPages.length ? (
            <>
              <div className={styles.pageTabs} role="tablist" aria-label="样张页切换">
                {previewPages.map((page) => (
                  <button
                    key={page.pageNo}
                    type="button"
                    className={`${styles.pageTab} ${page.pageNo === currentPage?.pageNo ? styles.pageTabActive : ''}`}
                    onClick={() => setActivePageNo(page.pageNo)}
                    aria-pressed={page.pageNo === currentPage?.pageNo}
                  >
                    第 {page.pageNo} 页
                  </button>
                ))}
              </div>

              {currentPage?.previewType === 'text' ? (
                <div className={styles.textPreview}>
                  <div className={styles.textIcon}>
                    <FileTextOutlined />
                  </div>
                  <pre>{currentPage.previewTextExcerpt || '当前文件暂无可展示文本样张。'}</pre>
                </div>
              ) : (
                <button
                  type="button"
                  className={styles.imageStage}
                  onClick={() => setLightboxOpen(true)}
                  aria-label={`放大查看 ${activeFileName} 第 ${currentPage?.pageNo || 1} 页`}
                >
                  <img
                    src={currentPage?.previewImageUrl}
                    alt={`${activeFileName}样张第 ${currentPage?.pageNo || 1} 页`}
                  />
                  <span className={styles.imageHint}>
                    <PictureOutlined />
                    点击放大查看
                  </span>
                </button>
              )}
            </>
          ) : (
            <div className={styles.emptyWrap}>
              <EmptyState description="当前文件暂不支持在线预览，请通过管理员交付查看完整内容" />
            </div>
          )}
        </div>
      </section>

      <PreviewLightboxModal
        open={lightboxOpen}
        title={activeFileName || '文件样张'}
        images={lightboxImages}
        activeIndex={lightboxIndex}
        onChange={(imageIndex) => {
          const targetPage = previewPages.filter((page) => page.previewType === 'image')[imageIndex];
          if (targetPage) {
            setActivePageNo(targetPage.pageNo);
          }
        }}
        onClose={() => setLightboxOpen(false)}
      />
    </>
  );
}

export default ResourceFilePreviewExplorer;
