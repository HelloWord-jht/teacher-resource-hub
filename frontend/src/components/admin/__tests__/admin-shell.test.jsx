import { MemoryRouter } from 'react-router-dom';
import { render, screen } from '@testing-library/react';
import SideMenu from '../SideMenu';
import TopBar from '../TopBar';

describe('后台导航与快速发货入口', () => {
  it('应该展示新增的后台菜单入口', () => {
    render(
      <MemoryRouter initialEntries={['/admin/dashboard']}>
        <SideMenu />
      </MemoryRouter>,
    );

    expect(screen.getByText('快速发货中心')).toBeInTheDocument();
    expect(screen.getByText('渠道管理')).toBeInTheDocument();
    expect(screen.getByText('内容投放')).toBeInTheDocument();
    expect(screen.getByText('资料来源')).toBeInTheDocument();
    expect(screen.getByText('网盘绑定')).toBeInTheDocument();
    expect(screen.getByText('导入任务')).toBeInTheDocument();
  });

  it('应该展示顶部快速发货搜索框', () => {
    render(
      <MemoryRouter initialEntries={['/admin/dashboard']}>
        <TopBar />
      </MemoryRouter>,
    );

    expect(screen.getByPlaceholderText('输入资源码、标题或 slug，快速发货')).toBeInTheDocument();
  });
});
