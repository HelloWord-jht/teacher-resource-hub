import {
  Card,
  Collapse,
  Form,
  Input,
  Tag,
  Typography,
  message,
} from 'antd';
import { useEffect, useState } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import { webApi } from '../../api/web';
import EmptyState from '../../components/common/EmptyState';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import CopyActionGroup from '../../components/web/CopyActionGroup';
import DeliveryFlowPanel from '../../components/web/DeliveryFlowPanel';
import QuickConsultPanel from '../../components/web/QuickConsultPanel';
import ResourceFilePreviewExplorer from '../../components/web/ResourceFilePreviewExplorer';
import RecommendedResourceCard from '../../components/web/RecommendedResourceCard';
import ResourcePreviewGallery from '../../components/web/ResourcePreviewGallery';
import ServicePromiseBar from '../../components/web/ServicePromiseBar';
import { useSiteStore } from '../../store/siteStore';
import { useWechatStore } from '../../store/wechatStore';
import {
  buildLeadPayload,
  buildWechatModalPayload,
  reportVisitTraceOnce,
} from '../../utils/attribution';
import { formatDateTime, formatPrice, setDocumentMeta } from '../../utils/format';
import styles from './ResourceDetailPage.module.css';

function buildSuitability(detail) {
  const suitableList = [
    `适合 ${detail.grade} 老师用于 ${detail.scene} 等高频场景快速备课。`,
    '适合希望先看样张，再确认是否适合本班风格和节奏的老师。',
    '适合想一次拿到课件、教案、沟通文案等完整资料包的老师。',
  ];
  const notSuitableList = [
    '如果你只想要单个零散页面或单个文件，这套资料不一定适合。',
    '如果你希望站内立即支付并自动下载，本页不提供此类流程。',
    `如果当前并不是在准备 ${detail.scene} 或相近场景，建议先看相关推荐再决定。`,
  ];
  const savedWorkList = [
    '省掉临时东拼西凑课件、教案和流程页的时间。',
    '省掉咨询时反复说明资源内容的时间，直接发送资源码即可。',
    '省掉交付后再追问包含内容的时间，页面已提前把结构讲清楚。',
  ];

  return { suitableList, notSuitableList, savedWorkList };
}

function buildValueSummary(detail) {
  return `适合 ${detail.grade} 老师在准备 ${detail.scene} 时，先看样张快速判断，再决定是否加微信咨询。`;
}

function ResourceDetailPage() {
  const { id } = useParams();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const channelParam = searchParams.get('channel') || '';
  const trackingCodeParam =
    searchParams.get('trackingCode') ||
    searchParams.get('tracking_code') ||
    searchParams.get('campaign') ||
    '';
  const resourceCodeParam = searchParams.get('resourceCode') || '';
  const sourcePage = `${location.pathname}${location.search || ''}`;
  const [form] = Form.useForm();
  const [detail, setDetail] = useState(null);
  const [previewFiles, setPreviewFiles] = useState([]);
  const [previewPages, setPreviewPages] = useState([]);
  const [activePreviewFileId, setActivePreviewFileId] = useState(null);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { homeData, fetchHomeData } = useSiteStore();
  const { openModal, setPageContext } = useWechatStore();

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

  useEffect(() => {
    if (!detail) {
      setPreviewFiles([]);
      setPreviewPages([]);
      setActivePreviewFileId(null);
      return;
    }
    let ignore = false;
    setPreviewLoading(true);
    webApi
      .getResourcePreviewFiles(detail.id)
      .then(async (files) => {
        if (ignore) {
          return;
        }
        setPreviewFiles(files || []);
        const preferredFile = (files || []).find((item) => item.current) || (files || []).find((item) => item.primary) || files?.[0];
        if (!preferredFile) {
          setActivePreviewFileId(null);
          setPreviewPages([]);
          return;
        }
        setActivePreviewFileId(preferredFile.id);
        const pages = await webApi.getResourcePreviewPages(detail.id, preferredFile.id);
        if (!ignore) {
          setPreviewPages(pages || []);
        }
      })
      .catch(() => {
        if (!ignore) {
          setPreviewFiles([]);
          setPreviewPages([]);
          setActivePreviewFileId(null);
        }
      })
      .finally(() => {
        if (!ignore) {
          setPreviewLoading(false);
        }
      });

    return () => {
      ignore = true;
    };
  }, [detail]);

  useEffect(() => {
    if (!detail) {
      return;
    }
    const resourceCode = detail.resourceCode || resourceCodeParam;
    reportVisitTraceOnce({
      pathname: location.pathname,
      search: location.search,
      searchParams,
      targetResourceId: detail.id,
      targetResourceCode: resourceCode,
    });
  }, [detail, location.pathname, location.search, resourceCodeParam]);

  useEffect(() => {
    if (!detail) {
      return undefined;
    }
    const resourceCode = detail.resourceCode || resourceCodeParam;
    setPageContext({
      sourcePage,
      channel: channelParam,
      trackingCode: trackingCodeParam,
      targetResourceId: detail.id,
      resourceCode,
      resourceTitle: detail.title,
    });
    return () => {
      setPageContext(null);
    };
  }, [detail, sourcePage, channelParam, trackingCodeParam, resourceCodeParam, setPageContext]);

  const openWechatModal = () => {
    if (!detail) {
      return;
    }
    const resourceCode = detail.resourceCode || resourceCodeParam;
    openModal(
      buildWechatModalPayload({
        consultInfo: detail.wechatConsult || homeData?.wechatConsult,
        searchParams,
        pathname: location.pathname,
        search: location.search,
        resourceId: detail.id,
        resourceCode,
        resourceTitle: detail.title,
      }),
    );
  };

  const handleLeadSubmit = async (values) => {
    if (!detail) {
      return;
    }
    const resourceCode = detail.resourceCode || resourceCodeParam;
    setSubmitting(true);
    try {
      await webApi.createLead(
        buildLeadPayload({
          values,
          searchParams,
          pathname: location.pathname,
          search: location.search,
          resourceId: detail.id,
          resourceCode,
        }),
      );
      message.success('咨询信息已提交，请留意微信回复');
      form.resetFields();
    } finally {
      setSubmitting(false);
    }
  };

  const handlePreviewFileSelect = async (file) => {
    if (!detail || !file?.id) {
      return;
    }
    setPreviewLoading(true);
    try {
      const pages = await webApi.getResourcePreviewPages(detail.id, file.id);
      setActivePreviewFileId(file.id);
      setPreviewPages(pages || []);
    } finally {
      setPreviewLoading(false);
    }
  };

  if (loading) {
    return <LoadingState text="正在加载资源详情…" />;
  }

  if (error) {
    return <ErrorState subTitle={error} onRetry={() => window.location.reload()} />;
  }

  if (!detail) {
    return <EmptyState description="资源不存在或已下线" />;
  }

  const effectiveResourceCode = detail.resourceCode || resourceCodeParam;
  const activePreviewFile = previewFiles.find((item) => item.id === activePreviewFileId) || previewFiles.find((item) => item.current) || previewFiles[0] || null;
  const effectivePreviewCount = previewFiles.length
    ? previewFiles.reduce((total, item) => total + (item.previewPageCount || 0), 0)
    : detail.previewCount || detail.previewImages?.length || 0;
  const effectiveContentItemCount = detail.contentItemCount || detail.contentItems?.length || 0;
  const effectiveDeliveryNotice =
    detail.deliveryNotice ||
    '本站不做在线支付，确认需求后通过微信沟通交付。建议先查看预览，再发送资源码咨询。';
  const deliveryProcess = homeData?.deliveryProcess?.length
    ? homeData.deliveryProcess
    : ['选资源', '看预览', '复制资源码', '加微信发送资源码', '确认需求', '微信沟通交付'];
  const servicePromiseItems = [
    '先看预览，再决定是否咨询',
    '加微信发送资源码，可更快匹配和交付',
    '链接失效支持补发',
    '本站不做在线支付，确认需求后通过微信沟通交付',
  ];
  const { suitableList, notSuitableList, savedWorkList } = buildSuitability(detail);
  const fitSummary = buildValueSummary(detail);
  const wechatId = detail.wechatConsult?.wechatId || homeData?.wechatConsult?.wechatId;

  return (
    <section className="section-block">
      <div className={`page-container ${styles.page}`}>
        <div className={styles.topGrid}>
          <div className={styles.summaryCard}>
            <p className={styles.eyebrow}>{detail.scene}</p>
            <h1>{detail.title}</h1>
            <p className={styles.fitSummary}>{fitSummary}</p>
            <p className={styles.summary}>{detail.summary}</p>

            <div className={styles.codePanel}>
              <span className={styles.codeLabel}>资源码</span>
              <strong className={styles.codeValue}>{effectiveResourceCode || '待补充'}</strong>
            </div>

            <div className={styles.tagGroup}>
              {(detail.tags || []).map((tag) => (
                <Tag color="blue" key={tag.id}>
                  {tag.name}
                </Tag>
              ))}
            </div>

            <div className={styles.metaGrid}>
              <div>
                <span>展示价格</span>
                <strong>{formatPrice(detail.displayPrice)}</strong>
              </div>
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
                <span>预览数量</span>
                <strong>{effectivePreviewCount} 张</strong>
              </div>
              <div>
                <span>内容项数量</span>
                <strong>{effectiveContentItemCount} 项</strong>
              </div>
            </div>
          </div>

          <QuickConsultPanel
            resourceCode={effectiveResourceCode}
            wechatId={wechatId}
            onWechatClick={openWechatModal}
            onScrollToLeadForm={() => document.getElementById('lead-form-card')?.scrollIntoView({ behavior: 'smooth' })}
            deliveryNotice={effectiveDeliveryNotice}
            servicePromiseItems={servicePromiseItems}
          />
        </div>

        <ServicePromiseBar items={servicePromiseItems} title="咨询前先确认这些承诺" />

        <div className={styles.detailLayout}>
          <div className={styles.main}>
            <Card
              title={`样张系统（共 ${effectivePreviewCount} 张预览图）`}
              className={styles.sectionCard}
              extra={<span className={styles.cardExtra}>{previewFiles.length ? '左侧点文件，右侧直接切换样张' : '点击主图可放大查看'}</span>}
            >
              {previewLoading ? (
                <LoadingState text="正在加载文件样张…" />
              ) : previewFiles.length ? (
                <ResourceFilePreviewExplorer
                  files={previewFiles}
                  activeFileId={activePreviewFileId}
                  activeFileName={activePreviewFile?.fileName || detail.title}
                  previewPages={previewPages}
                  onFileSelect={handlePreviewFileSelect}
                />
              ) : (
                <ResourcePreviewGallery title={detail.title} images={detail.previewImages || []} />
              )}
            </Card>

            <div className={styles.sectionGrid}>
              <Card title="适合谁使用" className={styles.sectionCard}>
                <ul className={styles.bulletList}>
                  {suitableList.map((item) => (
                    <li key={item}>{item}</li>
                  ))}
                </ul>
              </Card>

              <Card title="不太适合谁" className={styles.sectionCard}>
                <ul className={styles.bulletList}>
                  {notSuitableList.map((item) => (
                    <li key={item}>{item}</li>
                  ))}
                </ul>
              </Card>
            </div>

            <Card title="这份资料能帮你省掉什么" className={styles.sectionCard}>
              <div className={styles.valueGrid}>
                {savedWorkList.map((item, index) => (
                  <div key={item} className={styles.valueCard}>
                    <span className={styles.valueIndex}>{String(index + 1).padStart(2, '0')}</span>
                    <p>{item}</p>
                  </div>
                ))}
              </div>
            </Card>

            <Card title="包含内容清单" className={styles.sectionCard}>
              <div className={styles.contentGrid}>
                {(detail.contentItems || []).map((item, index) => (
                  <div key={`${item}-${index}`} className={styles.contentCard}>
                    <span className={styles.contentIndex}>{String(index + 1).padStart(2, '0')}</span>
                    <strong>{item}</strong>
                    <p>交付时按完整资料包整理，便于老师拿到后直接查看与使用。</p>
                  </div>
                ))}
              </div>
            </Card>

            <Card title="资源说明" className={styles.sectionCard}>
              <div
                className="content-html"
                dangerouslySetInnerHTML={{ __html: detail.descriptionHtml }}
              />
            </Card>

            <Card title="使用说明与交付提醒" className={styles.sectionCard}>
              <div className={styles.noteGrid}>
                <div className={styles.noteBox}>
                  <h3>使用说明</h3>
                  <p>{detail.usageNotice}</p>
                </div>
                <div className={styles.noteBox}>
                  <h3>交付说明</h3>
                  <p>{effectiveDeliveryNotice}</p>
                </div>
              </div>
            </Card>

            <Card title="咨询前你会关心什么" className={styles.sectionCard}>
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
                    <RecommendedResourceCard
                      key={resource.id}
                      resource={resource}
                      onWechatClick={() =>
                        openModal(
                          buildWechatModalPayload({
                            consultInfo: detail.wechatConsult || homeData?.wechatConsult,
                            searchParams,
                            pathname: location.pathname,
                            search: location.search,
                            resourceId: resource.id,
                            resourceCode: resource.resourceCode,
                            resourceTitle: resource.title,
                          }),
                        )
                      }
                    />
                  ))}
                </div>
              ) : (
                <EmptyState description="暂时没有更多推荐资源" />
              )}
            </Card>

            <Card title="确认适合后再咨询" className={styles.bottomConsultCard}>
              <div className={styles.bottomConsultContent}>
                <div>
                  <Typography.Title level={5} style={{ marginTop: 0 }}>
                    资源码：{effectiveResourceCode || '待补充'}
                  </Typography.Title>
                  <Typography.Paragraph className={styles.bottomText}>
                    如果你已经看过样张、确认适合自己的年级和场景，下一步只需要复制资源码并加微信咨询。
                    我们会根据资源码更快为你匹配和交付。
                  </Typography.Paragraph>
                </div>
                <CopyActionGroup
                  resourceCode={effectiveResourceCode}
                  wechatId={wechatId}
                  onWechatClick={openWechatModal}
                  align="right"
                  compact
                />
              </div>
            </Card>
          </div>

          <aside className={styles.sidebar}>
            <Card title="线索提交表单" className={styles.sidebarCard} id="lead-form-card">
              <div className={styles.formTip}>
                提交表单时会自动带上当前资源码和渠道归因信息。若准备直接加微信，也建议先复制资源码。
              </div>
              <Form layout="vertical" form={form} onFinish={handleLeadSubmit}>
                <Form.Item
                  label="老师称呼"
                  name="name"
                  rules={[{ required: true, message: '请填写老师称呼' }]}
                >
                  <Input placeholder="例如：张老师…" autoComplete="name" />
                </Form.Item>
                <Form.Item
                  label="联系方式"
                  name="contact"
                  rules={[{ required: true, message: '请填写手机号或微信号' }]}
                >
                  <Input placeholder="手机号或微信号…" autoComplete="off" spellCheck={false} />
                </Form.Item>
                <Form.Item label="咨询内容" name="message">
                  <Input.TextArea
                    rows={4}
                    placeholder={`建议说明资源码 ${effectiveResourceCode || '当前资源'}、使用年级和计划上课时间…`}
                  />
                </Form.Item>
                <button type="submit" className={styles.primarySubmit} disabled={submitting}>
                  {submitting ? '正在提交…' : '提交咨询信息'}
                </button>
              </Form>
            </Card>

            <Card title="咨询动作建议" className={styles.sidebarCard}>
              <CopyActionGroup
                resourceCode={effectiveResourceCode}
                wechatId={wechatId}
                onWechatClick={openWechatModal}
                showLeadButton
                onScrollToLeadForm={() => document.getElementById('lead-form-card')?.scrollIntoView({ behavior: 'smooth' })}
              />
            </Card>

            <Card className={styles.sidebarCard}>
              <DeliveryFlowPanel steps={deliveryProcess} />
            </Card>
          </aside>
        </div>
      </div>
    </section>
  );
}

export default ResourceDetailPage;
