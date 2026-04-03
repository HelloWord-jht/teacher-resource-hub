import { Button, Card, Col, List, Row, Space, Table, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import FulfillmentSearchBox from '../../../components/admin/FulfillmentSearchBox';
import EmptyState from '../../../components/common/EmptyState';
import ErrorState from '../../../components/common/ErrorState';
import LoadingState from '../../../components/common/LoadingState';
import { useFulfillmentStore } from '../../../store/fulfillmentStore';
import { formatDateTime } from '../../../utils/format';

function AdminFulfillmentCenterPage() {
  const openDrawer = useFulfillmentStore((state) => state.openDrawer);
  const [dashboard, setDashboard] = useState(null);
  const [recentSearches, setRecentSearches] = useState([]);
  const [deliveryPage, setDeliveryPage] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadData = () => {
    setLoading(true);
    setError('');
    Promise.all([
      adminApi.getDashboard(),
      adminApi.getFulfillmentRecentSearches(10),
      adminApi.getDeliveryRecords({ pageNum: 1, pageSize: 10 }),
    ])
      .then(([dashboardData, searchLogs, deliveryData]) => {
        setDashboard(dashboardData);
        setRecentSearches(searchLogs);
        setDeliveryPage(deliveryData);
      })
      .catch((err) => setError(err?.message || '快速发货中心加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) {
    return <LoadingState text="正在加载快速发货中心..." />;
  }

  if (error) {
    return <ErrorState subTitle={error} onRetry={loadData} />;
  }

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Space style={{ width: '100%', justifyContent: 'space-between' }} wrap>
        <Typography.Title level={3} style={{ margin: 0 }}>
          快速发货中心
        </Typography.Title>
        <FulfillmentSearchBox
          style={{ width: 420 }}
          onPicked={(item) => {
            if (item?.resourceCode) {
              openDrawer({ resourceCode: item.resourceCode });
            }
          }}
        />
      </Space>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={12} xl={6}>
          <Card><Typography.Text type="secondary">今日新增线索</Typography.Text><Typography.Title level={3}>{dashboard?.todayLeadTotal || 0}</Typography.Title></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Typography.Text type="secondary">今日已加微信</Typography.Text><Typography.Title level={3}>{dashboard?.todayWechatAddedTotal || 0}</Typography.Title></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Typography.Text type="secondary">今日已成交</Typography.Text><Typography.Title level={3}>{dashboard?.todayDealTotal || 0}</Typography.Title></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Typography.Text type="secondary">今日发货数</Typography.Text><Typography.Title level={3}>{dashboard?.todayDeliveryTotal || 0}</Typography.Title></Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} xl={8}>
          <Card title="最近搜索">
            {recentSearches?.length ? (
              <List
                dataSource={recentSearches}
                renderItem={(item) => (
                  <List.Item
                    actions={[
                      <Button
                        key="open"
                        type="link"
                        onClick={() => {
                          if (item.matchedResourceId && item.keyword?.startsWith('RES-')) {
                            openDrawer({ resourceCode: item.keyword });
                          }
                        }}
                      >
                        继续发货
                      </Button>,
                    ]}
                  >
                    <List.Item.Meta
                      title={item.keyword}
                      description={`${item.searchType || 'keyword'} · ${formatDateTime(item.createdAt)}`}
                    />
                  </List.Item>
                )}
              />
            ) : (
              <EmptyState description="最近还没有搜索记录" />
            )}
          </Card>
        </Col>
        <Col xs={24} xl={8}>
          <Card title="今日待处理线索">
            {(dashboard?.recentLeads || []).length ? (
              <List
                dataSource={(dashboard?.recentLeads || []).filter((item) => item.status === 0).slice(0, 6)}
                renderItem={(item) => (
                  <List.Item
                    actions={[
                      item.targetResourceCode ? (
                        <Button
                          key="deliver"
                          type="link"
                          onClick={() => openDrawer({ resourceCode: item.targetResourceCode, leadId: item.id })}
                        >
                          去发货
                        </Button>
                      ) : null,
                    ].filter(Boolean)}
                  >
                    <List.Item.Meta
                      title={`${item.name} · ${item.contact}`}
                      description={`${item.targetResourceCode || '无资源码'} · ${formatDateTime(item.createdAt)}`}
                    />
                  </List.Item>
                )}
              />
            ) : (
              <EmptyState description="当前没有待处理线索" />
            )}
          </Card>
        </Col>
        <Col xs={24} xl={8}>
          <Card title="最近导入任务">
            {(dashboard?.recentImportTasks || []).length ? (
              <List
                dataSource={dashboard?.recentImportTasks || []}
                renderItem={(item) => (
                  <List.Item>
                    <List.Item.Meta
                      title={item.taskName}
                      description={`${item.generatedResourceCode || '未生成资源码'} · ${formatDateTime(item.executedAt || item.createdAt)}`}
                    />
                  </List.Item>
                )}
              />
            ) : (
              <EmptyState description="最近没有导入任务" />
            )}
          </Card>
        </Col>
      </Row>

      <Card title="最近发货记录">
        <Table
          rowKey="id"
          pagination={false}
          dataSource={deliveryPage.list || []}
          columns={[
            { title: '资源码', dataIndex: 'resourceCode', width: 200 },
            { title: '线索ID', dataIndex: 'leadId', width: 100, render: (value) => value || '--' },
            { title: '发货方式', dataIndex: 'deliveryChannel', width: 100 },
            { title: '发货人', dataIndex: 'operatorName', width: 120 },
            { title: '发货备注', dataIndex: 'deliveryRemark', ellipsis: true },
            { title: '发货时间', dataIndex: 'createdAt', width: 180, render: formatDateTime },
            {
              title: '操作',
              width: 120,
              render: (_, record) => (
                <Button size="small" onClick={() => openDrawer({ resourceCode: record.resourceCode, leadId: record.leadId })}>
                  再次发货
                </Button>
              ),
            },
          ]}
        />
      </Card>
    </Space>
  );
}

export default AdminFulfillmentCenterPage;
