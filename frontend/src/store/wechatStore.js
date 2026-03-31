import { create } from 'zustand';

export const useWechatStore = create((set) => ({
  open: false,
  consultInfo: null,
  openModal(consultInfo) {
    set({ open: true, consultInfo });
  },
  closeModal() {
    set({ open: false });
  },
}));
