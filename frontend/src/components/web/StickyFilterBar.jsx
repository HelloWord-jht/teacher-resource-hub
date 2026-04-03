import { Button, Input, Select, Space, Tag } from 'antd';
import styles from './StickyFilterBar.module.css';

function StickyFilterBar({
  categories,
  tags,
  params,
  keywordInput,
  setKeywordInput,
  updateParams,
  onReset,
}) {
  const activeFilters = [
    params.categoryId
      ? { key: 'categoryId', label: `分类：${categories.find((item) => String(item.id) === String(params.categoryId))?.name || params.categoryId}` }
      : null,
    params.tagId
      ? { key: 'tagId', label: `标签：${tags.find((item) => String(item.id) === String(params.tagId))?.name || params.tagId}` }
      : null,
    params.grade ? { key: 'grade', label: `年级：${params.grade}` } : null,
    params.scene ? { key: 'scene', label: `场景：${params.scene}` } : null,
    params.keyword ? { key: 'keyword', label: `关键词：${params.keyword}` } : null,
    params.sortType ? { key: 'sortType', label: `排序：${params.sortType}` } : null,
  ].filter(Boolean);

  return (
    <section className={styles.panel} aria-label="资源筛选栏">
      <div className={styles.grid}>
        <label className={styles.field}>
          <span>分类</span>
          <Select
            allowClear
            placeholder="选择分类"
            value={params.categoryId}
            options={categories.map((item) => ({ label: item.name, value: String(item.id) }))}
            onChange={(value) => updateParams({ categoryId: value })}
          />
        </label>

        <label className={styles.field}>
          <span>标签</span>
          <Select
            allowClear
            placeholder="选择标签"
            value={params.tagId}
            options={tags.map((item) => ({ label: item.name, value: String(item.id) }))}
            onChange={(value) => updateParams({ tagId: value })}
          />
        </label>

        <label className={styles.field}>
          <span>年级</span>
          <Select
            allowClear
            placeholder="选择年级"
            value={params.grade}
            options={[
              { label: '一年级-六年级', value: '一年级-六年级' },
              { label: '三年级-六年级', value: '三年级-六年级' },
            ]}
            onChange={(value) => updateParams({ grade: value })}
          />
        </label>

        <label className={styles.field}>
          <span>场景</span>
          <Select
            allowClear
            placeholder="选择场景"
            value={params.scene}
            options={[
              '开学第一课',
              '家长会',
              '主题班会',
              '安全教育',
              '期中复习',
              '期末复习',
              '公开课',
              '家校沟通',
              '节日活动',
            ].map((item) => ({ label: item, value: item }))}
            onChange={(value) => updateParams({ scene: value })}
          />
        </label>

        <label className={styles.field}>
          <span>关键词</span>
          <Input.Search
            placeholder="输入资源标题、资源码或关键词"
            value={keywordInput}
            onChange={(event) => setKeywordInput(event.target.value)}
            onSearch={() => updateParams({ keyword: keywordInput })}
            allowClear
          />
        </label>

        <label className={styles.field}>
          <span>排序</span>
          <Select
            value={params.sortType}
            options={[
              { label: '最新发布', value: 'latest' },
              { label: '热门优先', value: 'hot' },
              { label: '推荐优先', value: 'recommended' },
            ]}
            onChange={(value) => updateParams({ sortType: value })}
          />
        </label>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.activeFilters}>
          {activeFilters.map((item) => (
            <Tag
              color="blue"
              key={item.key}
              closable={item.key !== 'sortType'}
              onClose={() => updateParams({ [item.key]: item.key === 'sortType' ? 'latest' : '' })}
            >
              {item.label}
            </Tag>
          ))}
        </div>
        <Space wrap>
          <Button onClick={onReset}>重置筛选</Button>
        </Space>
      </div>
    </section>
  );
}

export default StickyFilterBar;
