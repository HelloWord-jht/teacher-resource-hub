import { create } from 'zustand';

export const useWechatStore = create((set) => ({
  open: false,
  consultInfo: null,
  modalContext: null,
  pageContext: null,
  openModal(payload) {
    const normalized =
      payload && (
        payload.consultInfo ||
        payload.resourceCode ||
        payload.resourceTitle ||
        payload.sourcePage ||
        payload.channel ||
        payload.trackingCode ||
        payload.targetResourceId !== undefined
      )
        ? payload
        : { consultInfo: payload };
    set({
      open: true,
      consultInfo: normalized.consultInfo || null,
      modalContext: {
        sourcePage: normalized.sourcePage || '',
        channel: normalized.channel || '',
        trackingCode: normalized.trackingCode || '',
        targetResourceId: normalized.targetResourceId || null,
        resourceCode: normalized.resourceCode || '',
        resourceTitle: normalized.resourceTitle || '',
      },
    });
  },
  setPageContext(payload) {
    set({
      pageContext: payload
        ? {
            sourcePage: payload.sourcePage || '',
            channel: payload.channel || '',
            trackingCode: payload.trackingCode || '',
            targetResourceId: payload.targetResourceId || null,
            resourceCode: payload.resourceCode || '',
            resourceTitle: payload.resourceTitle || '',
          }
        : null,
    });
  },
  closeModal() {
    set({ open: false, consultInfo: null, modalContext: null });
  },
}));
