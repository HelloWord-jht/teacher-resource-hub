import { useEffect, useState } from 'react';
import { webApi } from '../../api/web';
import LoadingState from '../../components/common/LoadingState';
import ErrorState from '../../components/common/ErrorState';
import PageBanner from '../../components/web/PageBanner';
import { useSiteStore } from '../../store/siteStore';
import { setDocumentMeta } from '../../utils/format';
import styles from './ContentPage.module.css';

function ContentPage({ pageCode }) {
  const [pageData, setPageData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { homeData, fetchHomeData } = useSiteStore();

  useEffect(() => {
    fetchHomeData().catch(() => {});
  }, [fetchHomeData]);

  useEffect(() => {
    setLoading(true);
    setError('');
    webApi
      .getPage(pageCode)
      .then((result) => {
        setPageData(result);
        setDocumentMeta({
          title: `${result.title}｜${homeData?.siteName || '小学课件教案资源导流站'}`,
          description: `${result.title}页面`,
        });
      })
      .catch((err) => setError(err?.message || '页面内容加载失败'))
      .finally(() => setLoading(false));
  }, [pageCode, homeData?.siteName]);

  if (loading) {
    return <LoadingState text="正在加载页面内容..." />;
  }

  if (error) {
    return <ErrorState subTitle={error} onRetry={() => window.location.reload()} />;
  }

  return (
    <>
      <PageBanner title={pageData?.title} description="页面内容支持后台实时维护，前台刷新即可查看最新信息。" />
      <section className="section-block">
        <div className={`page-container ${styles.wrap}`}>
          <article
            className={`content-html ${styles.article}`}
            dangerouslySetInnerHTML={{ __html: pageData?.contentHtml || '' }}
          />
        </div>
      </section>
    </>
  );
}

export default ContentPage;
