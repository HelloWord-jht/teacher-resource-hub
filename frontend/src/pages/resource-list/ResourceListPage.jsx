import { Button, Pagination, Tag, message } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { useLocation, useSearchParams } from 'react-router-dom';
import { webApi } from '../../api/web';
import EmptyState from '../../components/common/EmptyState';
import ErrorState from '../../components/common/ErrorState';
import LoadingState from '../../components/common/LoadingState';
import PageBanner from '../../components/web/PageBanner';
import ResourceCard from '../../components/web/ResourceCard';
import StickyFilterBar from '../../components/web/StickyFilterBar';
import { useSiteStore } from '../../store/siteStore';
import { useWechatStore } from '../../store/wechatStore';
import {
  buildWechatModalPayload,
  reportVisitTraceOnce,
} from '../../utils/attribution';
import { copyText } from '../../utils/clipboard';
import { setDocumentMeta } from '../../utils/format';
import styles from './ResourceListPage.module.css';

function ResourceListPage() {
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [pageData, setPageData] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [keywordInput, setKeywordInput] = useState(searchParams.get('keyword') || '');
  const [highlightedResourceId, setHighlightedResourceId] = useState(null);
  const [targetFound, setTargetFound] = useState(false);
  const resourceRefs = useRef(new Map());
  const { homeData, fetchHomeData } = useSiteStore();
  const { openModal } = useWechatStore();

  const params = {
    categoryId: searchParams.get('categoryId') || undefined,
    tagId: searchParams.get('tagId') || undefined,
    grade: searchParams.get('grade') || undefined,
    scene: searchParams.get('scene') || undefined,
    keyword: searchParams.get('keyword') || undefined,
    sortType: searchParams.get('sortType') || 'latest',
    pageNum: Number(searchParams.get('pageNum') || 1),
    pageSize: Number(searchParams.get('pageSize') || 10),
  };

  const resourceCodeFromQuery = searchParams.get('resourceCode') || '';
  const targetResource = pageData.list.find((item) => item.resourceCode === resourceCodeFromQuery);

  useEffect(() => {
    setDocumentMeta({
      title: '资源中心｜小学课件教案资源导流站',
      description: '支持按分类、标签、年级、场景和关键词筛选资源，先看预览，再加微信咨询。',
    });
  }, []);

  useEffect(() => {
    fetchHomeData().catch(() => {});
    webApi.getCategories().then(setCategories).catch(() => {});
    webApi.getTags().then(setTags).catch(() => {});
  }, [fetchHomeData]);

  useEffect(() => {
    reportVisitTraceOnce({
      pathname: location.pathname,
      search: location.search,
      searchParams,
      targetResourceCode: resourceCodeFromQuery,
    });
  }, [location.pathname, location.search, resourceCodeFromQuery, searchParams]);

  useEffect(() => {
    setKeywordInput(searchParams.get('keyword') || '');
    setLoading(true);
    setError('');
    webApi
      .getResources(params)
      .then((result) => {
        setPageData(result);
      })
      .catch((err) => {
        setError(err?.message || '资源列表加载失败');
      })
      .finally(() => {
        setLoading(false);
      });
  }, [searchParams]);

  useEffect(() => {
    if (!resourceCodeFromQuery) {
      setHighlightedResourceId(null);
      setTargetFound(false);
      return;
    }
    if (!targetResource) {
      setHighlightedResourceId(null);
      setTargetFound(false);
      return;
    }
    setHighlightedResourceId(targetResource.id);
    setTargetFound(true);
    const targetNode = resourceRefs.current.get(targetResource.id);
    if (targetNode) {
      window.setTimeout(() => {
        targetNode.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }, 120);
    }
  }, [resourceCodeFromQuery, targetResource]);

  const updateParams = (patch, resetPage = true) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(patch).forEach(([key, value]) => {
      if (value === undefined || value === null || value === '') {
        next.delete(key);
      } else {
        next.set(key, String(value));
      }
    });
    if (resetPage) {
      next.set('pageNum', '1');
    }
    setSearchParams(next);
  };

  const handleReset = () => {
    setKeywordInput('');
    setSearchParams(
      new URLSearchParams(
        resourceCodeFromQuery
          ? { sortType: 'latest', pageNum: '1', pageSize: '10', resourceCode: resourceCodeFromQuery }
          : { sortType: 'latest', pageNum: '1', pageSize: '10' },
      ),
    );
  };

  const handleCopyResourceCode = async (resourceCode) => {
    if (!resourceCode) {
      message.warning('当前没有可复制的资源码');
      return;
    }
    await copyText(resourceCode);
    message.success('资源码已复制');
  };

  return (
    <>
      <PageBanner
        title="资源中心"
        description="按分类、标签、年级和使用场景快速筛选，帮助老师更快找到能直接上课的完整资料包。"
        extra={
          <Button
            type="primary"
            size="large"
            onClick={() =>
              openModal(
                buildWechatModalPayload({
                  consultInfo: homeData?.wechatConsult,
                  searchParams,
                  pathname: location.pathname,
                  search: location.search,
                  resourceCode: resourceCodeFromQuery,
                  resourceTitle: targetResource?.title || '',
                  resourceId: targetResource?.id || null,
                }),
              )
            }
          >
            加微信咨询
          </Button>
        }
      />

      <section className="section-block">
        <div className="page-container">
          <div className={styles.tipBar}>
            <strong>快速咨询提示</strong>
            <span>
              {homeData?.quickConsultBarText ||
                '看中资源后先复制资源码，加微信时直接发送给我们，可更快匹配和交付。'}
            </span>
          </div>

          <StickyFilterBar
            categories={categories}
            tags={tags}
            params={params}
            keywordInput={keywordInput}
            setKeywordInput={setKeywordInput}
            updateParams={updateParams}
            onReset={handleReset}
          />

          {resourceCodeFromQuery ? (
            <div className={`${styles.locatedBar} ${targetFound ? styles.locatedBarActive : styles.locatedBarWarning}`}>
              <div>
                <strong>{targetFound ? '已为你定位目标资源' : '当前页暂未命中目标资源'}</strong>
                <p>
                  {targetFound
                    ? `资源码 ${resourceCodeFromQuery} 已高亮显示，建议先复制资源码，再加微信咨询。`
                    : `当前筛选结果中没有找到资源码 ${resourceCodeFromQuery}，可尝试清空筛选后重新查看。`}
                </p>
              </div>
              {targetFound ? (
                <div className={styles.locatedActions}>
                  <Button onClick={() => handleCopyResourceCode(resourceCodeFromQuery)}>复制这个资源码</Button>
                  <Button
                    type="primary"
                    onClick={() =>
                      openModal(
                        buildWechatModalPayload({
                          consultInfo: homeData?.wechatConsult,
                          searchParams,
                          pathname: location.pathname,
                          search: location.search,
                          resourceCode: resourceCodeFromQuery,
                          resourceTitle: targetResource?.title || '',
                          resourceId: targetResource?.id || null,
                        }),
                      )
                    }
                  >
                    直接加微信咨询
                  </Button>
                </div>
              ) : null}
            </div>
          ) : null}

          {loading ? <LoadingState text="正在加载资源列表…" /> : null}
          {!loading && error ? <ErrorState subTitle={error} onRetry={() => updateParams({})} /> : null}
          {!loading && !error && pageData.list?.length === 0 ? <EmptyState description="没有找到符合条件的资源" /> : null}

          {!loading && !error && pageData.list?.length > 0 ? (
            <>
              <div className={styles.resultBar}>
                <div>
                  共找到 <strong>{pageData.total}</strong> 条资源
                </div>
                {resourceCodeFromQuery ? (
                  <Tag color="blue">当前落地资源码：{resourceCodeFromQuery}</Tag>
                ) : null}
              </div>
              <div className={styles.listGrid}>
                {pageData.list.map((resource) => {
                  const isTarget = resource.id === highlightedResourceId;
                  return (
                    <div
                      key={resource.id}
                      ref={(node) => {
                        if (node) {
                          resourceRefs.current.set(resource.id, node);
                        } else {
                          resourceRefs.current.delete(resource.id);
                        }
                      }}
                      className={isTarget ? styles.targetWrap : ''}
                    >
                      <ResourceCard
                        resource={resource}
                        highlighted={isTarget}
                        highlightText="看中这份资料后，先复制这个资源码，再加微信咨询，会更快完成匹配与交付。"
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
                    </div>
                  );
                })}
              </div>
              <div className={styles.paginationWrap}>
                <Pagination
                  current={pageData.pageNum}
                  pageSize={pageData.pageSize}
                  total={pageData.total}
                  showSizeChanger
                  showTotal={(total) => `共 ${total} 条`}
                  onChange={(pageNum, pageSize) => updateParams({ pageNum, pageSize }, false)}
                />
              </div>
            </>
          ) : null}
        </div>
      </section>
    </>
  );
}

export default ResourceListPage;
