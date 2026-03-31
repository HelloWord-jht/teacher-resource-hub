import { Card, Col, Row, Statistic, Table, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { adminApi } from '../../../api/admin';
import LoadingState from '../../../components/common/LoadingState';
import ErrorState from '../../../components/common/ErrorState';
import { formatDateTime } from '../../../utils/format';
import { renderLeadStatusTag, renderResourceStatusTag } from '../../../utils/admin.jsx';

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
          <Card><Statistic title="资源总数" value={data?.resourceTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="分类总数" value={data?.categoryTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="标签总数" value={data?.tagTotal || 0} /></Card>
        </Col>
        <Col xs={24} md={12} xl={6}>
          <Card><Statistic title="线索总数" value={data?.leadTotal || 0} /></Card>
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
                  title: '发布时间',
                  dataIndex: 'publishTime',
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
    </div>
  );
}

export default AdminDashboardPage;
