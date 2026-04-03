import { AutoComplete, Input, Tag, Typography } from 'antd';
import { startTransition, useDeferredValue, useEffect, useState } from 'react';
import { adminApi } from '../../api/admin';

function FulfillmentSearchBox({
  placeholder = '输入资源码、标题或 slug，快速发货',
  style,
  onPicked,
}) {
  const [keyword, setKeyword] = useState('');
  const [options, setOptions] = useState([]);
  const deferredKeyword = useDeferredValue(keyword);

  useEffect(() => {
    if (!deferredKeyword || !deferredKeyword.trim()) {
      setOptions([]);
      return undefined;
    }

    const timer = window.setTimeout(() => {
      adminApi
        .quickSearchFulfillment(deferredKeyword.trim())
        .then((list) => {
          startTransition(() => {
            setOptions(
              list.map((item) => ({
                value: item.resourceCode,
                label: (
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                    <div style={{ minWidth: 0 }}>
                      <div style={{ fontWeight: 600 }}>{item.title}</div>
                      <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                        {item.resourceCode} · {item.categoryName || '未分类'}
                      </Typography.Text>
                    </div>
                    <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
                      <Tag color={item.status === 1 ? 'blue' : 'default'}>
                        {item.status === 1 ? '已发布' : '下线'}
                      </Tag>
                      <Tag color={item.authorizationStatusSnapshot === 'APPROVED' ? 'green' : 'orange'}>
                        {item.authorizationStatusSnapshot || '待审'}
                      </Tag>
                    </div>
                  </div>
                ),
                item,
              })),
            );
          });
        })
        .catch(() => {
          startTransition(() => {
            setOptions([]);
          });
        });
    }, 250);

    return () => window.clearTimeout(timer);
  }, [deferredKeyword]);

  return (
    <AutoComplete
      style={style}
      value={keyword}
      options={options}
      onSearch={setKeyword}
      onChange={setKeyword}
      onSelect={(value, option) => {
        setKeyword(value);
        onPicked?.(option?.item || null);
      }}
    >
      <Input.Search placeholder={placeholder} allowClear />
    </AutoComplete>
  );
}

export default FulfillmentSearchBox;
