import axios from 'axios';
import { message } from 'antd';
import { useAdminAuthStore } from '../store/adminAuthStore';

const request = axios.create({
  baseURL: '/',
  timeout: 15000,
});

request.interceptors.request.use((config) => {
  const nextConfig = { ...config };
  if (nextConfig.withAuth) {
    const token = useAdminAuthStore.getState().token;
    if (token) {
      nextConfig.headers = {
        ...nextConfig.headers,
        Authorization: `Bearer ${token}`,
      };
    }
  }
  return nextConfig;
});

request.interceptors.response.use(
  (response) => {
    const result = response.data;
    if (result?.code === 0) {
      return result.data;
    }
    const errorMessage = result?.message || '请求失败，请稍后再试';
    message.error(errorMessage);
    return Promise.reject(new Error(errorMessage));
  },
  (error) => {
    const status = error?.response?.status;
    if (status === 401) {
      useAdminAuthStore.getState().logout();
      if (window.location.pathname !== '/admin/login') {
        message.error('登录已过期，请重新登录');
        window.location.href = '/admin/login';
      }
      return Promise.reject(error);
    }
    const errorMessage =
      error?.response?.data?.message ||
      error?.message ||
      '网络异常，请检查后重试';
    message.error(errorMessage);
    return Promise.reject(error);
  },
);

export default request;
