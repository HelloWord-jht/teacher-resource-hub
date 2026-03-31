import { Button, Card, Collapse } from 'antd';
import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import LoadingState from '../../components/common/LoadingState';
import ErrorState from '../../components/common/ErrorState';
import ResourceCard from '../../components/web/ResourceCard';
import { useSiteStore } from '../../store/siteStore';
import { useWechatStore } from '../../store/wechatStore';
import { setDocumentMeta } from '../../utils/format';
import styles from './HomePage.module.css';

const advantageList = [
  {
    title: '先看预览，再决定',
    description: '重点资源提供预览样张，老师可以先判断风格、结构和适用场景，再决定是否咨询。',
  },
  {
    title: '不是单个文件，而是整套资料',
    description: '优先提供课件、教案、话术、流程页等完整备课资料包，减少老师来回拼素材的时间。',
  },
  {
    title: '覆盖小学老师高频场景',
    description: '围绕班主任、家长会、班会、公开课、复习课、节日活动等真实教学场景进行整理。',
  },
];

const processList = [
  { title: '先浏览预览', desc: '进入资源详情页，先看封面和预览样张。' },
  { title: '确认适用场景', desc: '对照年级、班型和使用场景判断是否适合。' },
  { title: '加微信咨询', desc: '说明资源名称、年级和预计使用时间。' },
  { title: '确认交付方式', desc: '沟通完成后通过微信完成下单与资料交付。' },
];

function HomePage() {
  const navigate = useNavigate();
  const { homeData, loading, error, fetchHomeData } = useSiteStore();
  const { openModal } = useWechatStore();

  useEffect(() => {
    fetchHomeData().catch(() => {});
  }, [fetchHomeData]);

  useEffect(() => {
    if (homeData) {
      setDocumentMeta({
        title: homeData.seoTitle || homeData.siteName || '小学课件教案资源导流站',
        description: homeData.seoDescription,
      });
    }
  }, [homeData]);

  if (loading && !homeData) {
    return <LoadingState text="正在加载首页内容..." />;
  }

  if (error && !homeData) {
    return <ErrorState subTitle={error} onRetry={() => fetchHomeData(true)} />;
  }

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
              <Button type="primary" size="large" onClick={() => navigate('/resources')}>
                进入资源中心
              </Button>
              <Button size="large" onClick={() => openModal(homeData?.wechatConsult)}>
                加微信咨询
              </Button>
            </div>
            <div className={styles.heroTips}>
              <span>10分钟找到能直接上课的资源包</span>
              <span>先看预览，再加微信咨询</span>
              <span>完整资料包优先，不做零散文件堆砌</span>
            </div>
          </div>

          <div className={styles.heroPanel}>
            <Card bordered={false} className={styles.heroCard}>
              <h3>常见使用场景</h3>
              <ul>
                <li>班主任开学第一课</li>
                <li>新学期家长会</li>
                <li>安全教育主题班会</li>
                <li>语数英公开课展示</li>
                <li>期中期末复习动员</li>
              </ul>
              <div className={styles.heroPanelFooter}>
                <strong>核心方式：</strong>
                <span>先看样张，再微信沟通交付</span>
              </div>
            </Card>
          </div>
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>热门分类入口</h2>
            <p>优先覆盖一线小学老师最常用的备课和沟通场景。</p>
          </div>
          <div className={styles.categoryGrid}>
            {(homeData?.hotCategories || []).map((category) => (
              <Link
                key={category.id}
                to={`/resources?categoryId=${category.id}`}
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
            <p>先看预览，再加微信咨询，帮助老师更快确认是否适合当前班级使用。</p>
          </div>
          <div className={styles.resourceGrid}>
            {(homeData?.recommendedResources || []).map((resource) => (
              <ResourceCard
                key={resource.id}
                resource={resource}
                onWechatClick={() => openModal(homeData?.wechatConsult)}
              />
            ))}
          </div>
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>我们的优势</h2>
            <p>更关注老师真正的上课准备效率，而不是只提供零散文件。</p>
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

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>交付流程说明</h2>
            <p>网站不做站内支付，重点是让老师先判断资源是否适合，再进行微信沟通。</p>
          </div>
          <div className={styles.processGrid}>
            {processList.map((item, index) => (
              <div key={item.title} className={styles.processCard}>
                <span className={styles.processIndex}>0{index + 1}</span>
                <h3>{item.title}</h3>
                <p>{item.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="section-block">
        <div className="page-container">
          <div className="section-title">
            <h2>常见问题</h2>
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
