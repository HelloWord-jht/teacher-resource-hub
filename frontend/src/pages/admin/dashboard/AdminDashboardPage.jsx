import { Card, Col, List, Row, Statistic, Table, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import ErrorState from '../../../components/common/ErrorState';
import LoadingState from '../../../components/common/LoadingState';
import {
  renderLeadStatusTag,
  renderResourceStatusTag,
} from '../../../utils/admin.jsx';
import { formatDateTime } from '../../../utils/format';

function AdminDashboardPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadData = () => {
    setLoading(true);
    setError('');
    adminApi
      .getDashboard()
      .then(setData)
      .catch((err) => setError(err?.message || '仪表盘加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) {
    return <LoadingState text="正在加载仪表盘..." />;
  }

  if (error) {
    return <ErrorState subTitle={error} onRetry={loadData} />;
  }

  return (
    <div>
      <Typography.Title level={3}>仪表盘</Typography.Title>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="已发布资源数" value={data?.publishedResourceTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="待审核资源数" value={data?.pendingResourceTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="风险资源数" value={data?.riskResourceTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="资源总数" value={data?.resourceTotal || 0} /></Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="今日新增线索" value={data?.todayLeadTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="今日已加微信数" value={data?.todayWechatAddedTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="今日已成交数" value={data?.todayDealTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="今日发货数" value={data?.todayDeliveryTotal || 0} /></Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} xl={14}>
          <Card title="最近新增资源">
            <Table
              rowKey="id"
              pagination={false}
              dataSource={data?.recentResources || []}
              columns={[
                { title: '资源标题', dataIndex: 'title' },
                { title: '分类', dataIndex: 'categoryName', width: 140 },
                {
                  title: '状态',
                  dataIndex: 'status',
                  width: 100,
                  render: renderResourceStatusTag,
                },
                {
                  title: '创建时间',
                  dataIndex: 'createdAt',
                  width: 160,
                  render: formatDateTime,
                },
              ]}
            />
          </Card>
        </Col>
        <Col xs={24} xl={10}>
          <Card title="最近线索">
            <Table
              rowKey="id"
              pagination={false}
              dataSource={data?.recentLeads || []}
              columns={[
                { title: '老师称呼', dataIndex: 'name', width: 100 },
                { title: '资源码', dataIndex: 'targetResourceCode', width: 180, render: (value) => value || '--' },
                { title: '联系方式', dataIndex: 'contact' },
                {
                  title: '状态',
                  dataIndex: 'status',
                  width: 100,
                  render: renderLeadStatusTag,
                },
                {
                  title: '提交时间',
                  dataIndex: 'createdAt',
                  width: 160,
                  render: formatDateTime,
                },
              ]}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} xl={12}>
          <Card title="最近发货资源">
            <Table
              rowKey="id"
              pagination={false}
              dataSource={data?.recentDeliveries || []}
              columns={[
                { title: '资源码', dataIndex: 'resourceCode', width: 200 },
                { title: '线索ID', dataIndex: 'leadId', width: 100, render: (value) => value || '--' },
                { title: '发货人', dataIndex: 'operatorName', width: 120 },
                { title: '发货备注', dataIndex: 'deliveryRemark', ellipsis: true },
                { title: '发货时间', dataIndex: 'createdAt', width: 160, render: formatDateTime },
              ]}
            />
          </Card>
        </Col>
        <Col xs={24} xl={12}>
          <Card title="最近导入任务">
            <Table
              rowKey="id"
              pagination={false}
              dataSource={data?.recentImportTasks || []}
              columns={[
                { title: '任务名称', dataIndex: 'taskName' },
                { title: '生成资源码', dataIndex: 'generatedResourceCode', width: 200, render: (value) => value || '--' },
                { title: '执行结果', dataIndex: 'executionResult', ellipsis: true },
                { title: '执行时间', dataIndex: 'executedAt', width: 160, render: formatDateTime },
              ]}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} xl={8}>
          <Card title="渠道线索分布">
            <List
              dataSource={data?.channelLeadStats || []}
              locale={{ emptyText: '暂无渠道线索数据' }}
              renderItem={(item) => (
                <List.Item>
                  <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', gap: 16 }}>
                    <Typography.Text>{item.channel || 'unknown'}</Typography.Text>
                    <Typography.Text strong>{item.leadCount || 0}</Typography.Text>
                  </div>
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col xs={24} xl={8}>
          <Card title="热门资源点击排行">
            <List
              dataSource={data?.hotViewResources || []}
              locale={{ emptyText: '暂无点击排行' }}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    title={`${item.title}（${item.resourceCode}）`}
                    description={`点击量：${item.countValue || 0}`}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col xs={24} xl={8}>
          <Card title="热门资源咨询排行">
            <List
              dataSource={data?.hotConsultResources || []}
              locale={{ emptyText: '暂无咨询排行' }}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    title={`${item.title}（${item.resourceCode}）`}
                    description={`咨询量：${item.countValue || 0}`}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default AdminDashboardPage;
