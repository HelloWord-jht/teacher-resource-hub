import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import WechatConsultModal from '../WechatConsultModal';
import ResourceFilePreviewExplorer from '../ResourceFilePreviewExplorer';
import ResourcePreviewGallery from '../ResourcePreviewGallery';
import RecommendedResourceCard from '../RecommendedResourceCard';

vi.mock('../../../utils/clipboard', () => ({
  copyText: vi.fn().mockResolvedValue(true),
}));

describe('教师用户侧成交交互组件', () => {
  it('样张系统应该支持通过缩略图切换当前预览图', async () => {
    const user = userEvent.setup();

    render(
      <ResourcePreviewGallery
        title="小学家长会 PPT + 发言稿通用包"
        images={[
          'https://example.com/preview-1.png',
          'https://example.com/preview-2.png',
          'https://example.com/preview-3.png',
        ]}
      />,
    );

    expect(screen.getByText('1 / 3')).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: '查看第 2 张预览图' }));
    expect(screen.getByText('2 / 3')).toBeInTheDocument();
    expect(screen.getByRole('img', { name: '小学家长会 PPT + 发言稿通用包预览图 2' })).toBeInTheDocument();
  });

  it('相关推荐简版卡在数据不完整时不应该显示残缺占位指标', () => {
    render(
      <MemoryRouter>
        <RecommendedResourceCard
          resource={{
            id: 2,
            title: '小学家长会 PPT + 发言稿通用包',
            resourceCode: 'RES-JZH-20260403-0001',
            coverImage: 'https://example.com/cover.png',
            displayPrice: 49,
            grade: '一年级-六年级',
            scene: '家长会',
            summary: '适合班主任召开新学期、阶段总结和期末反馈场景。',
          }}
        />
      </MemoryRouter>,
    );

    expect(screen.getByText('RES-JZH-20260403-0001')).toBeInTheDocument();
    expect(screen.queryByText(/0 张/)).not.toBeInTheDocument();
    expect(screen.queryByText(/0 项/)).not.toBeInTheDocument();
    expect(screen.queryByText(/--/)).not.toBeInTheDocument();
  });

  it('微信咨询弹窗在复制资源码后应该给出持续引导提示', async () => {
    const user = userEvent.setup();

    render(
      <WechatConsultModal
        open
        consultInfo={{
          wechatId: 'teacherziyuan001',
          wechatTip: '先看预览，再加微信咨询。',
        }}
        modalContext={{
          resourceTitle: '小学家长会 PPT + 发言稿通用包',
          resourceCode: 'RES-JZH-20260403-0001',
          sourcePage: '/resources/2',
          channel: 'xiaohongshu',
          trackingCode: 'XHS-JZH-20260401-A',
          targetResourceId: 2,
        }}
        onClose={() => {}}
      />,
    );

    await user.click(screen.getByRole('button', { name: '复制资源码' }));

    expect(screen.getByText('已复制资源码，建议下一步加微信后直接发送给我们。')).toBeInTheDocument();
  });

  it('多文件样张区应该支持左侧文件切换和前两页切换', async () => {
    const user = userEvent.setup();

    render(
      <ResourceFilePreviewExplorer
        files={[
          {
            id: 11,
            fileName: '家长会主课件.pdf',
            fileType: 'pdf',
            previewPageCount: 2,
            primary: true,
          },
          {
            id: 12,
            fileName: '家校沟通话术.docx',
            fileType: 'docx',
            previewPageCount: 1,
            primary: false,
          },
        ]}
        activeFileId={11}
        activeFileName="家长会主课件.pdf"
        previewPages={[
          { pageNo: 1, previewType: 'image', previewImageUrl: 'https://example.com/file-11-page-1.png' },
          { pageNo: 2, previewType: 'image', previewImageUrl: 'https://example.com/file-11-page-2.png' },
        ]}
        onFileSelect={() => {}}
      />,
    );

    expect(screen.getByText('当前查看文件：家长会主课件.pdf')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '第 1 页' })).toHaveAttribute('aria-pressed', 'true');

    await user.click(screen.getByRole('button', { name: '第 2 页' }));
    expect(screen.getByRole('img', { name: '家长会主课件.pdf样张第 2 页' })).toBeInTheDocument();
    expect(screen.getByText('当前展示为样张预览，仅展示前 2 页')).toBeInTheDocument();
  });
});
