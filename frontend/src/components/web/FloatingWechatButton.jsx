import { MessageOutlined } from '@ant-design/icons';
import styles from './FloatingWechatButton.module.css';

function FloatingWechatButton({ onClick }) {
  return (
    <button type="button" className={styles.button} onClick={onClick}>
      <MessageOutlined />
      <span>加微信咨询</span>
    </button>
  );
}

export default FloatingWechatButton;
