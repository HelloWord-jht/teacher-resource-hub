import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export const useAdminAuthStore = create(
  persist(
    (set) => ({
      token: '',
      userId: null,
      username: '',
      nickname: '',
      login(loginData) {
        set({
          token: loginData?.token || '',
          userId: loginData?.userId || null,
          username: loginData?.username || '',
          nickname: loginData?.nickname || '',
        });
      },
      logout() {
        set({
          token: '',
          userId: null,
          username: '',
          nickname: '',
        });
      },
    }),
    {
      name: 'teacher-resource-admin-auth',
      storage: createJSONStorage(() => window.localStorage),
    },
  ),
);
