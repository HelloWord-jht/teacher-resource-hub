import { PlusOutlined } from '@ant-design/icons';
import { Button, Input, Pagination, Popconfirm, Select, Space, Table, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';
import { useFulfillmentStore } from '../../../store/fulfillmentStore';
import {
  authorizationStatusOptions,
  renderAuthorizationStatusTag,
  renderResourceStatusTag,
  renderYesNoTag,
  resourceStatusOptions,
} from '../../../utils/admin.jsx';
import { formatDateTime, formatPrice } from '../../../utils/format';

function AdminResourceListPage() {
  const navigate = useNavigate();
  const openFulfillmentDrawer = useFulfillmentStore((state) => state.openDrawer);
  const [categories, setCategories] = useState([]);
  const [pageData, setPageData] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    keyword: '',
    categoryId: undefined,
    status: undefined,
    authorizationStatus: undefined,
  });
  const [searchInput, setSearchInput] = useState('');

  const loadData = (pageNum = pageData.pageNum, pageSize = pageData.pageSize, nextFilters = filters) => {
    setLoading(true);
    adminApi
      .getResourcePage({
        pageNum,
        pageSize,
        ...nextFilters,
      })
      .then(setPageData)
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    adminApi.listCategories().then(setCategories).catch(() => {});
    loadData(1, 10, filters);
  }, []);

  if (loading && !pageData.list.length) {
    return <LoadingState text="正在加载资源管理列表..." />;
  }

  return (
    <div>
      <Space style={{ width: '100%', justifyContent: 'space-between', marginBottom: 16 }} wrap>
        <Typography.Title level={3} style={{ margin: 0 }}>资源管理</Typography.Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/admin/resources/create')}>
          新增资源
        </Button>
      </Space>

      <Space wrap style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="输入资源标题、资源码或搜索关键词"
          style={{ width: 300 }}
          value={searchInput}
          onChange={(event) => setSearchInput(event.target.value)}
          onSearch={(value) => {
            const nextFilters = { ...filters, keyword: value || '' };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
          allowClear
        />
        <Select
          allowClear
          placeholder="按分类筛选"
          style={{ width: 180 }}
          options={categories.map((item) => ({ label: item.name, value: item.id }))}
          value={filters.categoryId}
          onChange={(value) => {
            const nextFilters = { ...filters, categoryId: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Select
          allowClear
          placeholder="按状态筛选"
          style={{ width: 160 }}
          options={resourceStatusOptions}
          value={filters.status}
          onChange={(value) => {
            const nextFilters = { ...filters, status: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Select
          allowClear
          placeholder="按授权状态筛选"
          style={{ width: 180 }}
          options={authorizationStatusOptions}
          value={filters.authorizationStatus}
          onChange={(value) => {
            const nextFilters = { ...filters, authorizationStatus: value };
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        />
        <Button
          onClick={() => {
            const nextFilters = {
              keyword: '',
              categoryId: undefined,
              status: undefined,
              authorizationStatus: undefined,
            };
            setSearchInput('');
            setFilters(nextFilters);
            loadData(1, pageData.pageSize, nextFilters);
          }}
        >
          重置
        </Button>
      </Space>

      <Table
        rowKey="id"
        loading={loading}
        pagination={false}
        dataSource={pageData.list}
        scroll={{ x: 1660 }}
        columns={[
          { title: '资源标题', dataIndex: 'title', width: 240, fixed: 'left' },
          { title: '资源码', dataIndex: 'resourceCode', width: 200 },
          { title: '分类', dataIndex: 'categoryName', width: 140 },
          { title: '展示价格', dataIndex: 'displayPrice', width: 120, render: formatPrice },
          { title: '适用年级', dataIndex: 'grade', width: 140 },
          { title: '场景', dataIndex: 'scene', width: 120 },
          {
            title: '授权状态',
            dataIndex: 'authorizationStatusSnapshot',
            width: 120,
            render: renderAuthorizationStatusTag,
          },
          { title: '状态', dataIndex: 'status', width: 100, render: renderResourceStatusTag },
          { title: '推荐', dataIndex: 'isRecommended', width: 100, render: renderYesNoTag },
          { title: '浏览量', dataIndex: 'viewCount', width: 90 },
          { title: '咨询量', dataIndex: 'consultCount', width: 90 },
          { title: '预览图数', dataIndex: 'previewCount', width: 100 },
          { title: '内容项数', dataIndex: 'contentItemCount', width: 100 },
          { title: '排序值', dataIndex: 'sortOrder', width: 90 },
          { title: '更新时间', dataIndex: 'updatedAt', width: 170, render: formatDateTime },
          {
            title: '操作',
            width: 360,
            fixed: 'right',
            render: (_, record) => (
              <Space>
                <Button size="small" onClick={() => navigate(`/admin/resources/${record.id}/edit`)}>
                  编辑
                </Button>
                <Button
                  size="small"
                  type="primary"
                  ghost
                  onClick={() => openFulfillmentDrawer({ resourceCode: record.resourceCode })}
                >
                  快速发货
                </Button>
                <Button
                  size="small"
                  onClick={() =>
                    adminApi.updateResourceStatus(record.id, { status: record.status === 1 ? 0 : 1 }).then(() => {
                      message.success('资源状态已更新');
                      loadData();
                    })
                  }
                >
                  {record.status === 1 ? '下线' : '发布'}
                </Button>
                <Popconfirm
                  title="确认删除该资源吗？"
                  onConfirm={() =>
                    adminApi.deleteResource(record.id).then(() => {
                      message.success('资源删除成功');
                      loadData();
                    })
                  }
                >
                  <Button size="small" danger>删除</Button>
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />

      <div style={{ marginTop: 16, display: 'flex', justifyContent: 'center' }}>
        <Pagination
          current={pageData.pageNum}
          pageSize={pageData.pageSize}
          total={pageData.total}
          showSizeChanger
          showTotal={(total) => `共 ${total} 条`}
          onChange={(pageNum, pageSize) => loadData(pageNum, pageSize)}
        />
      </div>
    </div>
  );
}

export default AdminResourceListPage;
