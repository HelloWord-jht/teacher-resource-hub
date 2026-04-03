import { Spin } from 'antd';
import './state.css';

function LoadingState({ text = '正在加载，请稍候…' }) {
  return (
    <div className="state-wrapper" role="status" aria-live="polite">
      <Spin size="large" />
      <p className="state-text">{text}</p>
    </div>
  );
}

export default LoadingState;
