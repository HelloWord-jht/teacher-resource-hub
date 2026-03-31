import { create } from 'zustand';
import { webApi } from '../api/web';

export const useSiteStore = create((set, get) => ({
  homeData: null,
  loading: false,
  error: '',
  async fetchHomeData(force = false) {
    if (get().homeData && !force) {
      return get().homeData;
    }
    set({ loading: true, error: '' });
    try {
      const homeData = await webApi.getHome();
      set({ homeData, loading: false, error: '' });
      return homeData;
    } catch (error) {
      set({ loading: false, error: error?.message || '首页数据加载失败' });
      throw error;
    }
  },
}));
