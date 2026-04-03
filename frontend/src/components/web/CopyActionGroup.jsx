import { Button, message } from 'antd';
import { useEffect, useState } from 'react';
import { copyText } from '../../utils/clipboard';
import styles from './CopyActionGroup.module.css';

function CopyActionGroup({
  resourceCode,
  wechatId,
  onWechatClick,
  onScrollToLeadForm,
  showLeadButton = false,
  primaryWechatText = '加微信咨询',
  hintText = '已复制资源码，建议下一步加微信后直接发送给我们。',
  align = 'left',
  compact = false,
}) {
  const [copiedType, setCopiedType] = useState('');

  useEffect(() => {
    if (!copiedType) {
      return undefined;
    }
    const timer = window.setTimeout(() => {
      setCopiedType('');
    }, 2400);
    return () => window.clearTimeout(timer);
  }, [copiedType]);

  const handleCopy = async (value, type, successText) => {
    if (!value) {
      message.warning('当前没有可复制内容');
      return;
    }
    await copyText(value);
    setCopiedType(type);
    message.success(successText);
  };

  return (
    <div
      className={`${styles.wrapper} ${align === 'right' ? styles.alignRight : ''} ${compact ? styles.compact : ''}`}
    >
      <div className={styles.actions}>
        <Button onClick={() => handleCopy(resourceCode, 'resourceCode', '资源码已复制')}>复制资源码</Button>
        <Button onClick={() => handleCopy(wechatId, 'wechatId', '微信号已复制')}>复制微信号</Button>
        {showLeadButton ? (
          <Button onClick={onScrollToLeadForm}>
            提交咨询表单
          </Button>
        ) : null}
        <Button type="primary" onClick={onWechatClick}>
          {primaryWechatText}
        </Button>
      </div>
      {copiedType === 'resourceCode' ? <p className={styles.hint}>{hintText}</p> : null}
      {copiedType === 'wechatId' ? <p className={styles.hint}>已复制微信号，建议与资源码一起发送，便于更快匹配资料。</p> : null}
    </div>
  );
}

export default CopyActionGroup;
