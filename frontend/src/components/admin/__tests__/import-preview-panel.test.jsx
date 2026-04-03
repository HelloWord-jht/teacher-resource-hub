import { render, screen } from '@testing-library/react';
import ImportFilePreviewPanel from '../ImportFilePreviewPanel';

describe('后台文件预览面板', () => {
  it('应该展示当前文件状态和样张页切换', () => {
    render(
      <ImportFilePreviewPanel
        currentFile={{
          id: 21,
          fileName: '开学第一课课件.pptx',
          fileType: 'pptx',
          sourceType: 'upload',
          previewStatus: 'success',
          previewPageCount: 2,
          isPrimary: 1,
        }}
        previewPages={[
          { pageNo: 1, previewType: 'image', previewImageUrl: 'https://example.com/ppt-page-1.png' },
          { pageNo: 2, previewType: 'image', previewImageUrl: 'https://example.com/ppt-page-2.png' },
        ]}
      />,
    );

    expect(screen.getByText('当前文件：开学第一课课件.pptx')).toBeInTheDocument();
    expect(screen.getByText('主展示文件')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '第 1 页' })).toBeInTheDocument();
    expect(screen.getByRole('img', { name: '开学第一课课件.pptx样张第 1 页' })).toBeInTheDocument();
  });
});
