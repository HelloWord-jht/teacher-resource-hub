import { FileTextOutlined, ReloadOutlined, StarOutlined } from '@ant-design/icons';
import { Button, Space, Tag } from 'antd';
import { useEffect, useMemo, useState } from 'react';
import EmptyState from '../common/EmptyState';
import FileProcessStatusTag from './FileProcessStatusTag';
import styles from './ImportFilePreviewPanel.module.css';

function formatSourceType(sourceType) {
  return sourceType === 'extracted' ? 'ZIP 解压' : '上传文件';
}

function ImportFilePreviewPanel({
  currentFile,
  previewPages = [],
  onRegenerate,
  onSetPrimary,
  onDelete,
  onRefresh,
  actionLoading = false,
}) {
  const [activePageNo, setActivePageNo] = useState(1);

  useEffect(() => {
    setActivePageNo(previewPages[0]?.pageNo || 1);
  }, [currentFile?.id, previewPages]);

  const currentPage = useMemo(
    () => previewPages.find((page) => page.pageNo === activePageNo) || previewPages[0] || null,
    [activePageNo, previewPages],
  );

  if (!currentFile) {
    return <EmptyState description="请选择左侧文件查看样张" />;
  }

  return (
    <section className={styles.panel}>
      <div className={styles.header}>
        <div>
          <strong>当前文件：{currentFile.fileName}</strong>
          <div className={styles.metaLine}>
            <Tag>{String(currentFile.fileType || '文件').toUpperCase()}</Tag>
            <Tag>{formatSourceType(currentFile.sourceType)}</Tag>
            <FileProcessStatusTag status={currentFile.previewStatus} />
            {currentFile.isPrimary ? <Tag color="blue">主展示文件</Tag> : null}
          </div>
        </div>
        <Space wrap>
          <Button icon={<ReloadOutlined />} onClick={onRefresh} loading={actionLoading}>
            刷新状态
          </Button>
          <Button onClick={onRegenerate} loading={actionLoading}>
            重新生成预览
          </Button>
          <Button icon={<StarOutlined />} onClick={onSetPrimary} loading={actionLoading}>
            设为主展示文件
          </Button>
          {onDelete ? (
            <Button danger onClick={onDelete} loading={actionLoading}>
              删除该文件
            </Button>
          ) : null}
        </Space>
      </div>

      {previewPages.length ? (
        <>
          <div className={styles.pageHeader}>
            <span className={styles.pageSummary}>当前查看第 {currentPage?.pageNo || 1} 页，共 {previewPages.length} 页样张</span>
          </div>

          <div className={styles.pageTabs}>
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
              <div className={styles.iconWrap}>
                <FileTextOutlined />
              </div>
              <pre>{currentPage.previewTextExcerpt || '当前文件暂无文本样张。'}</pre>
            </div>
          ) : (
            <div className={styles.imageStage}>
              <img
                src={currentPage?.previewImageUrl}
                alt={`${currentFile.fileName}样张第 ${currentPage?.pageNo || 1} 页`}
              />
            </div>
          )}
        </>
      ) : (
        <div className={styles.emptyPreview}>
          <EmptyState description="当前文件暂不支持在线预览，或样张尚未生成成功" />
        </div>
      )}
    </section>
  );
}

export default ImportFilePreviewPanel;
