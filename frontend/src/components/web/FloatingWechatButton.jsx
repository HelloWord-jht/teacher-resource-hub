import {
  ArrowUpOutlined,
  FileSearchOutlined,
  MessageOutlined,
  QrcodeOutlined,
  QuestionCircleOutlined,
  WechatOutlined,
} from '@ant-design/icons';
import { Button, message } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { buildAttributedPath } from '../../utils/attribution';
import { copyText } from '../../utils/clipboard';
import styles from './FloatingWechatButton.module.css';

function FloatingWechatButton({ onOpenConsult, wechatId, resourceCode }) {
  const [expanded, setExpanded] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const toolboxRef = useRef(null);

  useEffect(() => {
    setExpanded(false);
  }, [location.pathname, location.search]);

  useEffect(() => {
    if (!expanded) {
      return undefined;
    }

    const handlePointerDown = (event) => {
      if (!toolboxRef.current?.contains(event.target)) {
        setExpanded(false);
      }
    };

    const handleEscape = (event) => {
      if (event.key === 'Escape') {
        setExpanded(false);
      }
    };

    document.addEventListener('mousedown', handlePointerDown);
    document.addEventListener('keydown', handleEscape);
    return () => {
      document.removeEventListener('mousedown', handlePointerDown);
      document.removeEventListener('keydown', handleEscape);
    };
  }, [expanded]);

  const handleCopy = async (value, successText) => {
    if (!value) {
      message.warning('当前没有可复制内容');
      return;
    }
    await copyText(value);
    message.success(successText);
  };

  const jumpToSection = (sectionId) => {
    if (location.pathname === '/') {
      document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth' });
      return;
    }
    navigate(buildAttributedPath('/', location.search, {}, sectionId));
  };

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className={styles.toolbox} ref={toolboxRef}>
      {expanded ? (
        <div className={styles.panel} role="dialog" aria-label="咨询工具箱">
          <Button type="primary" icon={<WechatOutlined />} onClick={onOpenConsult}>
            加微信咨询
          </Button>
          <Button icon={<MessageOutlined />} onClick={() => handleCopy(wechatId, '微信号已复制')}>
            复制微信号
          </Button>
          {resourceCode ? (
            <Button icon={<QrcodeOutlined />} onClick={() => handleCopy(resourceCode, '资源码已复制')}>
              复制当前资源码
            </Button>
          ) : null}
          <Button icon={<FileSearchOutlined />} onClick={() => jumpToSection('delivery-process-section')}>
            查看交付流程
          </Button>
          <Button icon={<QuestionCircleOutlined />} onClick={() => jumpToSection('faq-section')}>
            查看常见问题
          </Button>
          <Button icon={<ArrowUpOutlined />} onClick={scrollToTop}>
            返回顶部
          </Button>
        </div>
      ) : null}

      <button
        type="button"
        className={styles.button}
        onClick={() => setExpanded((current) => !current)}
        aria-expanded={expanded}
        aria-label={expanded ? '收起咨询工具箱' : '展开咨询工具箱'}
      >
        <MessageOutlined />
        <span>{expanded ? '收起工具箱' : '咨询工具箱'}</span>
      </button>
    </div>
  );
}

export default FloatingWechatButton;
