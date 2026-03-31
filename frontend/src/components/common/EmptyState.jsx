import { Empty } from 'antd';
import './state.css';

function EmptyState({ description = '暂时没有相关内容' }) {
  return (
    <div className="state-wrapper">
      <Empty description={description} />
    </div>
  );
}

export default EmptyState;
