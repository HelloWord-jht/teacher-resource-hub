import { Button, Card, Collapse } from 'antd';
import { useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import DeliveryFlowPanel from '../../components/web/DeliveryFlowPanel';
import ResourceCard from '../../components/web/ResourceCard';
import SceneEntrySection from '../../components/web/SceneEntrySection';
import ServicePromiseBar from '../../components/web/ServicePromiseBar';
import { useSiteStore } from '../../store/siteStore';
import { useWechatStore } from '../../store/wechatStore';
import {
  buildResourceListPath,
  buildWechatModalPayload,
  reportVisitTraceOnce,
} from '../../utils/attribution';
import { setDocumentMeta } from '../../utils/format';
import styles from './HomePage.module.css';

const advantageList = [
  {
    title: '不是单个文件，而是完整资料包',
    description: '优先提供课件、教案、逐字稿、流程页和沟通模板，帮助老师减少来回拼素材的时间。',
  },
  {
    title: '先看预览，再判断是否适合',
    description: '重点资源提供预览样张、内容清单和适用场景说明，老师可以先判断再咨询。',
  },
  {
    title: '支持资源码快速匹配与交付',
    description: '加微信时发送资源码，可更快完成需求确认、资源匹配和资料交付。',
  },
  {
    title: '聚焦小学老师高频教学场景',
    description: '围绕班主任、家长会、班会、公开课、复习课和节日活动等高频需求整理。',
  },
];

const fallbackProcessList = [
  '选资源',
  '看预览',
  '复制资源码',
  '加微信发送资源码',
  '确认需求',
  '微信沟通交付',
];

const sceneEntryList = [
  {
    eyebrow: '高频临时需求',
    title: '明天要开家长会',
    description: '直接筛到家长会常用资料，先看预览，再决定是否咨询。',
    filters: { categoryId: 2, scene: '家长会', sortType: 'recommended' },
  },
  {
    eyebrow: '班主任常见场景',
    title: '急找班会课件',
    description: '优先查看主题班会与安全教育相关资料，减少临时找素材时间。',
    filters: { categoryId: 3, sortType: 'hot' },
  },
  {
    eyebrow: '教研展示',
    title: '公开课 / 说课资料',
    description: '直接进入公开课精品区，快速判断模板和展示结构是否适合。',
    filters: { categoryId: 4, sortType: 'recommended' },
  },
  {
    eyebrow: '复习冲刺',
    title: '期中期末复习课',
    description: '按复习课场景快速筛选，优先看预览和内容清单。',
    filters: { categoryId: 5, sortType: 'latest' },
  },
  {
    eyebrow: '活动主题准备',
    title: '节日主题班会',
    description: '直接看节日专题和主题班会资源，提前判断是否适合活动安排。',
    filters: { categoryId: 6, sortType: 'latest' },
  },
  {
    eyebrow: '长期常备',
    title: '班主任常用资料',
    description: '适合开学、家校沟通、班级管理等高频工作场景。',
    filters: { categoryId: 1, sortType: 'hot' },
  },
];

const defaultServicePromises = [
  '先看预览，再决定是否咨询',
  '加微信发送资源码，可更快匹配和交付',
  '链接失效支持补发',
  '本站不做在线支付，确认需求后通过微信沟通交付',
];

function HomePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const { homeData, loading, error, fetchHomeData } = useSiteStore();
  const { openModal } = useWechatStore();

  useEffect(() => {
    fetchHomeData().catch(() => {});
  }, [fetchHomeData]);

  useEffect(() => {
    reportVisitTraceOnce({
      pathname: location.pathname,
      search: location.search,
      searchParams,
      targetResourceCode: searchParams.get('resourceCode') || '',
    });
  }, [location.pathname, location.search, searchParams]);

  useEffect(() => {
    if (homeData) {
      setDocumentMeta({
        title: homeData.seoTitle || homeData.siteName || '小学课件教案资源导流站',
        description: homeData.seoDescription,
      });
    }
  }, [homeData]);

  useEffect(() => {
    if (!location.hash) {
      return;
    }
    const timer = window.setTimeout(() => {
      document.getElementById(location.hash.replace('#', ''))?.scrollIntoView({ behavior: 'smooth' });
    }, 120);
    return () => window.clearTimeout(timer);
  }, [location.hash, homeData]);

  if (loading && !homeData) {
    return <LoadingState text="正在加载首页内容…" />;
  }

  if (error && !homeData) {
    return <ErrorState subTitle={error} onRetry={() => fetchHomeData(true)} />;
  }

  const deliveryProcess = homeData?.deliveryProcess?.length ? homeData.deliveryProcess : fallbackProcessList;
  const consultPayload = buildWechatModalPayload({
    consultInfo: homeData?.wechatConsult,
    searchParams,
    pathname: location.pathname,
    search: location.search,
  });

  return (
    <>
      <section className={styles.hero}>
        <div className={`page-container ${styles.heroInner}`}>
          <div className={styles.heroContent}>
            <p className={styles.eyebrow}>面向小学老师的备课资料整理站</p>
            <h1>{homeData?.homeMainTitle || '10分钟找到能直接上课的资源包'}</h1>
            <p className={styles.summary}>
              {homeData?.homeSubTitle ||
                '覆盖班主任、家长会、班会、公开课、复习课等高频场景，先看预览，再加微信咨询。'}
            </p>

            <div className={styles.heroActions}>
              <Button
                type="primary"
                size="large"
                onClick={() => navigate(buildResourceListPath(location.search))}
              >
                查看热门资源
              </Button>
              <Button size="large" onClick={() => openModal(consultPayload)}>
                加微信咨询
              </Button>
              <Button
                size="large"
                onClick={() => document.getElementById('delivery-process-section')?.scrollIntoView({ behavior: 'smooth' })}
              >
                如何交付
              </Button>
            </div>

            <div className={styles.heroTips}>
              <span>10分钟找到能直接上课的资源包</span>
              <span>先看预览，再加微信咨询</span>
              <span>加微信时发送资源码，可更快匹配和交付</span>
            </div>

            <div className={styles.noticeBar}>
              <strong>交付说明</strong>
              <p>{homeData?.consultNotice || '本站不做在线支付，确认需求后通过微信沟通交付。'}</p>
            </div>
          </div>

          <div className={styles.heroPanel}>
            <Card bordered={false} className={styles.heroCard}>
              <h3>咨询前先确认这 4 件事</h3>
              <ul>
                <li>这是不是你当前年级和场景真正要用的资料</li>
                <li>页面预览样张是否符合你的课堂风格</li>
                <li>包含内容是否覆盖课件、教案和沟通模板</li>
                <li>加微信时是否已经准备好资源码</li>
              </ul>
              <div className={styles.heroPanelFooter}>
                <strong>建议动作</strong>
                <span>先看预览，再复制资源码，加微信后直接发送资源码咨询</span>
              </div>
            </Card>
          </div>
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <SceneEntrySection entries={sceneEntryList} search={location.search} />
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>热门分类入口</h2>
            <p>优先覆盖一线小学老师最常用的备课、沟通和课堂展示场景。</p>
          </div>
          <div className={styles.categoryGrid}>
            {(homeData?.hotCategories || []).map((category) => (
              <Link
                key={category.id}
                to={buildResourceListPath(location.search, { categoryId: category.id })}
                className={styles.categoryCard}
              >
                <div className={styles.categoryIcon}>{category.name.slice(0, 2)}</div>
                <h3>{category.name}</h3>
                <p>{category.description}</p>
              </Link>
            ))}
          </div>
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>热门资源推荐</h2>
            <p>先看预览样张，再加微信咨询。每张卡片都可以直接复制资源码。</p>
          </div>
          <div className={styles.resourceGrid}>
            {(homeData?.recommendedResources || []).map((resource) => (
              <ResourceCard
                key={resource.id}
                resource={resource}
                onWechatClick={() =>
                  openModal(
                    buildWechatModalPayload({
                      consultInfo: homeData?.wechatConsult,
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
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <ServicePromiseBar items={defaultServicePromises} title="服务承诺条" />
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>我们的优势</h2>
            <p>重点不是卖零散文件，而是帮助老师更快完成一整套备课准备。</p>
          </div>
          <div className={styles.advantageGrid}>
            {advantageList.map((item) => (
              <div key={item.title} className={styles.advantageCard}>
                <h3>{item.title}</h3>
                <p>{item.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="section-block" id="delivery-process-section">
        <div className="page-container">
          <div className="section-title">
            <h2>交付流程说明</h2>
            <p>网站不做站内支付，重点是让老师先判断资料是否适合，再通过微信快速确认与交付。</p>
          </div>
          <DeliveryFlowPanel steps={deliveryProcess} />
        </div>
      </section>

      <section className="section-block" id="faq-section">
        <div className="page-container">
          <div className="section-title">
            <h2>咨询前你会关心什么</h2>
            <p>围绕可编辑性、预览方式、微信咨询和交付说明整理。</p>
          </div>
          <Collapse
            items={(homeData?.faqList || []).map((faq) => ({
              key: faq.id,
              label: faq.question,
              children: <div className="content-html">{faq.answer}</div>,
            }))}
            size="large"
            bordered={false}
          />
        </div>
      </section>
    </>
  );
}

export default HomePage;
