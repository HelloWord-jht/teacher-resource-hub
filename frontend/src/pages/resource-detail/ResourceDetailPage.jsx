import {
  Button,
  Card,
  Carousel,
  Collapse,
  Form,
  Input,
  List,
  message,
  Tag,
} from 'antd';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { webApi } from '../../api/web';
import LoadingState from '../../components/common/LoadingState';
import ErrorState from '../../components/common/ErrorState';
import EmptyState from '../../components/common/EmptyState';
import ResourceCard from '../../components/web/ResourceCard';
import { useSiteStore } from '../../store/siteStore';
import { useWechatStore } from '../../store/wechatStore';
import { formatDateTime, formatPrice, setDocumentMeta } from '../../utils/format';
import styles from './ResourceDetailPage.module.css';

function ResourceDetailPage() {
  const { id } = useParams();
  const [form] = Form.useForm();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { homeData, fetchHomeData } = useSiteStore();
  const { openModal } = useWechatStore();

  useEffect(() => {
    fetchHomeData().catch(() => {});
  }, [fetchHomeData]);

  useEffect(() => {
    setLoading(true);
    setError('');
    webApi
      .getResourceDetail(id)
      .then((result) => {
        setDetail(result);
        setDocumentMeta({
          title: `${result.title}｜小学课件教案资源导流站`,
          description: result.summary,
        });
      })
      .catch((err) => setError(err?.message || '资源详情加载失败'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleLeadSubmit = async (values) => {
    setSubmitting(true);
    try {
      await webApi.createLead({
        ...values,
        sourcePage: `/resources/${id}`,
      });
      message.success('咨询信息已提交，请留意微信回复');
      form.resetFields();
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <LoadingState text="正在加载资源详情..." />;
  }

  if (error) {
    return <ErrorState subTitle={error} onRetry={() => window.location.reload()} />;
  }

  if (!detail) {
    return <EmptyState description="资源不存在或已下线" />;
  }

  return (
    <section className="section-block">
      <div className={`page-container ${styles.detailLayout}`}>
        <div className={styles.main}>
          <div className={styles.heroCard}>
            <div className={styles.coverWrap}>
              <img src={detail.coverImage} alt={detail.title} className={styles.cover} />
            </div>
            <div className={styles.info}>
              <p className={styles.scene}>{detail.scene}</p>
              <h1>{detail.title}</h1>
              <p className={styles.summary}>{detail.summary}</p>

              <div className={styles.tagGroup}>
                {(detail.tags || []).map((tag) => (
                  <Tag color="blue" key={tag.id}>
                    {tag.name}
                  </Tag>
                ))}
              </div>

              <div className={styles.metaGrid}>
                <div>
                  <span>适用年级</span>
                  <strong>{detail.grade}</strong>
                </div>
                <div>
                  <span>适用场景</span>
                  <strong>{detail.scene}</strong>
                </div>
                <div>
                  <span>更新时间</span>
                  <strong>{formatDateTime(detail.updatedAt)}</strong>
                </div>
                <div>
                  <span>展示价格</span>
                  <strong>{formatPrice(detail.displayPrice)}</strong>
                </div>
              </div>

              <div className={styles.heroActions}>
                <Button type="primary" size="large" onClick={() => openModal(detail.wechatConsult || homeData?.wechatConsult)}>
                  加微信咨询
                </Button>
                <Button size="large" onClick={() => document.getElementById('lead-form-card')?.scrollIntoView({ behavior: 'smooth' })}>
                  提交咨询表单
                </Button>
              </div>
            </div>
          </div>

          <Card title="预览样张" className={styles.sectionCard}>
            <Carousel autoplay dots>
              {(detail.previewImages || []).map((imageUrl) => (
                <div key={imageUrl}>
                  <img src={imageUrl} alt="资源预览图" className={styles.previewImage} />
                </div>
              ))}
            </Carousel>
          </Card>

          <div className={styles.sectionGrid}>
            <Card title="包含内容清单" className={styles.sectionCard}>
              <List
                dataSource={detail.contentItems || []}
                renderItem={(item) => <List.Item>{item}</List.Item>}
              />
            </Card>
            <Card title="使用说明" className={styles.sectionCard}>
              <p className={styles.notice}>{detail.usageNotice}</p>
            </Card>
          </div>

          <Card title="资源说明" className={styles.sectionCard}>
            <div
              className="content-html"
              dangerouslySetInnerHTML={{ __html: detail.descriptionHtml }}
            />
          </Card>

          <Card title="常见问题" className={styles.sectionCard}>
            <Collapse
              items={(detail.faqList || []).map((faq) => ({
                key: faq.id,
                label: faq.question,
                children: <div className="content-html">{faq.answer}</div>,
              }))}
            />
          </Card>

          <Card title="推荐相关资源" className={styles.sectionCard}>
            {detail.relatedResources?.length ? (
              <div className={styles.relatedGrid}>
                {detail.relatedResources.map((resource) => (
                  <ResourceCard
                    key={resource.id}
                    resource={resource}
                    onWechatClick={() => openModal(detail.wechatConsult || homeData?.wechatConsult)}
                  />
                ))}
              </div>
            ) : (
              <EmptyState description="暂时没有更多推荐资源" />
            )}
          </Card>
        </div>

        <aside className={styles.sidebar}>
          <Card title="微信咨询区" className={styles.sidebarCard}>
            <div className={styles.consultBox}>
              <p>先看预览样张，再添加微信沟通交付方式和资料细节。</p>
              <div className={styles.consultValue}>微信号：{detail.wechatConsult?.wechatId || homeData?.wechatConsult?.wechatId}</div>
              <Button type="primary" block onClick={() => openModal(detail.wechatConsult || homeData?.wechatConsult)}>
                打开微信咨询弹窗
              </Button>
            </div>
          </Card>

          <Card title="线索提交表单" className={styles.sidebarCard} id="lead-form-card">
            <Form layout="vertical" form={form} onFinish={handleLeadSubmit}>
              <Form.Item
                label="老师称呼"
                name="name"
                rules={[{ required: true, message: '请填写老师称呼' }]}
              >
                <Input placeholder="例如：张老师" />
              </Form.Item>
              <Form.Item
                label="联系方式"
                name="contact"
                rules={[{ required: true, message: '请填写手机号或微信号' }]}
              >
                <Input placeholder="手机号或微信号" />
              </Form.Item>
              <Form.Item label="咨询内容" name="message">
                <Input.TextArea
                  rows={4}
                  placeholder="建议说明资源名称、使用年级和预计上课时间"
                />
              </Form.Item>
              <Button type="primary" htmlType="submit" block loading={submitting}>
                提交咨询信息
              </Button>
            </Form>
          </Card>
        </aside>
      </div>
    </section>
  );
}

export default ResourceDetailPage;
