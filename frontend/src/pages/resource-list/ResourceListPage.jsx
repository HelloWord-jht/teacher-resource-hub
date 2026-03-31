import { Button, Input, Pagination, Select, Space } from 'antd';
import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { webApi } from '../../api/web';
import LoadingState from '../../components/common/LoadingState';
import EmptyState from '../../components/common/EmptyState';
import ErrorState from '../../components/common/ErrorState';
import PageBanner from '../../components/web/PageBanner';
import ResourceCard from '../../components/web/ResourceCard';
import { useSiteStore } from '../../store/siteStore';
import { useWechatStore } from '../../store/wechatStore';
import { setDocumentMeta } from '../../utils/format';
import styles from './ResourceListPage.module.css';

const gradeOptions = [
  { label: '一年级-六年级', value: '一年级-六年级' },
  { label: '三年级-六年级', value: '三年级-六年级' },
];

const sceneOptions = [
  '开学第一课',
  '家长会',
  '主题班会',
  '安全教育',
  '期中复习',
  '期末复习',
  '公开课',
  '家校沟通',
  '节日活动',
].map((item) => ({ label: item, value: item }));

const sortOptions = [
  { label: '最新发布', value: 'latest' },
  { label: '热门优先', value: 'hot' },
  { label: '推荐优先', value: 'recommended' },
];

function ResourceListPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [pageData, setPageData] = useState({ list: [], total: 0, pageNum: 1, pageSize: 10 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [keywordInput, setKeywordInput] = useState(searchParams.get('keyword') || '');
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

  return (
    <>
      <PageBanner
        title="资源中心"
        description="按分类、标签、年级和使用场景快速筛选，帮助老师更快找到能直接上课的完整资料包。"
        extra={
          <Button type="primary" size="large" onClick={() => openModal(homeData?.wechatConsult)}>
            加微信咨询
          </Button>
        }
      />

      <section className="section-block">
        <div className="page-container">
          <div className={styles.filterPanel}>
            <div className={styles.filterGrid}>
              <Select
                allowClear
                placeholder="选择分类"
                value={params.categoryId}
                options={categories.map((item) => ({ label: item.name, value: String(item.id) }))}
                onChange={(value) => updateParams({ categoryId: value })}
              />
              <Select
                allowClear
                placeholder="选择标签"
                value={params.tagId}
                options={tags.map((item) => ({ label: item.name, value: String(item.id) }))}
                onChange={(value) => updateParams({ tagId: value })}
              />
              <Select
                allowClear
                placeholder="选择年级"
                value={params.grade}
                options={gradeOptions}
                onChange={(value) => updateParams({ grade: value })}
              />
              <Select
                allowClear
                placeholder="选择场景"
                value={params.scene}
                options={sceneOptions}
                onChange={(value) => updateParams({ scene: value })}
              />
              <Input.Search
                placeholder="输入资源标题或简介关键词"
                value={keywordInput}
                onChange={(event) => setKeywordInput(event.target.value)}
                onSearch={() => updateParams({ keyword: keywordInput })}
                allowClear
              />
            </div>

            <Space wrap>
              {sortOptions.map((item) => (
                <Button
                  key={item.value}
                  type={params.sortType === item.value ? 'primary' : 'default'}
                  onClick={() => updateParams({ sortType: item.value })}
                >
                  {item.label}
                </Button>
              ))}
              <Button
                onClick={() => {
                  setKeywordInput('');
                  setSearchParams(new URLSearchParams({ sortType: 'latest', pageNum: '1', pageSize: '10' }));
                }}
              >
                重置筛选
              </Button>
            </Space>
          </div>

          {loading ? <LoadingState text="正在加载资源列表..." /> : null}
          {!loading && error ? <ErrorState subTitle={error} onRetry={() => updateParams({})} /> : null}
          {!loading && !error && pageData.list?.length === 0 ? <EmptyState description="没有找到符合条件的资源" /> : null}

          {!loading && !error && pageData.list?.length > 0 ? (
            <>
              <div className={styles.resultBar}>
                共找到 <strong>{pageData.total}</strong> 条资源
              </div>
              <div className={styles.listGrid}>
                {pageData.list.map((resource) => (
                  <ResourceCard
                    key={resource.id}
                    resource={resource}
                    onWechatClick={() => openModal(homeData?.wechatConsult)}
                  />
                ))}
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
