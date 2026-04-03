import { create } from 'zustand';

export const useFulfillmentStore = create((set) => ({
  drawerOpen: false,
  resourceCode: '',
  leadId: null,
  openDrawer({ resourceCode, leadId = null } = {}) {
    set({
      drawerOpen: true,
      resourceCode: resourceCode || '',
      leadId,
    });
  },
  closeDrawer() {
    set({
      drawerOpen: false,
      resourceCode: '',
      leadId: null,
    });
  },
}));
