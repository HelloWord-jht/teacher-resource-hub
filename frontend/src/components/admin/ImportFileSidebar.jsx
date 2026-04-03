import FileProcessStatusTag from './FileProcessStatusTag';
import styles from './ImportFileSidebar.module.css';

function formatType(fileType) {
  return fileType ? String(fileType).toUpperCase() : '文件';
}

function ImportFileSidebar({ files = [], activeFileId, onSelectFile }) {
  if (!files.length) {
    return <div className={styles.empty}>当前还没有导入文件</div>;
  }

  return (
    <aside className={styles.sidebar}>
      {files.map((file) => (
        <button
          key={file.id}
          type="button"
          className={`${styles.fileButton} ${file.id === activeFileId ? styles.active : ''}`}
          onClick={() => onSelectFile?.(file)}
          aria-pressed={file.id === activeFileId}
        >
          <div className={styles.main}>
            <strong>{file.fileName}</strong>
            <span>{formatType(file.fileType)}</span>
          </div>
          <div className={styles.meta}>
            <FileProcessStatusTag status={file.previewStatus} />
            {file.isPrimary ? <span className={styles.primaryText}>主展示</span> : null}
          </div>
        </button>
      ))}
    </aside>
  );
}

export default ImportFileSidebar;
