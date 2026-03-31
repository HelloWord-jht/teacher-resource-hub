import { Button, Result } from 'antd';
import './state.css';

function ErrorState({ title = '页面加载失败', subTitle = '请稍后重试', onRetry }) {
  return (
    <div className="state-wrapper">
      <Result
        status="error"
        title={title}
        subTitle={subTitle}
        extra={
          onRetry ? (
            <Button type="primary" onClick={onRetry}>
              重新加载
            </Button>
          ) : null
        }
      />
    </div>
  );
}

export default ErrorState;
