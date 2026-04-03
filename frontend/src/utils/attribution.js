import { webApi } from '../api/web';

const CLIENT_ID_KEY = 'teacher_resource_hub_client_id';
const TRACE_PREFIX = 'teacher_resource_hub_trace';

export function getTrackingCode(searchParams) {
  return (
    searchParams.get('trackingCode') ||
    searchParams.get('tracking_code') ||
    searchParams.get('campaign') ||
    ''
  ).trim();
}

export function getChannel(searchParams) {
  return (searchParams.get('channel') || '').trim();
}

export function getTargetResourceCode(searchParams) {
  return (searchParams.get('resourceCode') || '').trim();
}

export function ensureClientId() {
  if (typeof window === 'undefined') {
    return 'server-render';
  }
  const stored = window.localStorage.getItem(CLIENT_ID_KEY);
  if (stored) {
    return stored;
  }
  const value = `client-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;
  window.localStorage.setItem(CLIENT_ID_KEY, value);
  return value;
}

export function getLandingPage(pathname, search = '') {
  return `${pathname}${search || ''}`.slice(0, 255);
}

export function buildAttributionParams(search) {
  const current = new URLSearchParams(search || '');
  const next = new URLSearchParams();
  const channel = getChannel(current);
  const trackingCode = getTrackingCode(current);
  const resourceCode = getTargetResourceCode(current);

  if (channel) {
    next.set('channel', channel);
  }
  if (trackingCode) {
    next.set('campaign', trackingCode);
  }
  if (resourceCode) {
    next.set('resourceCode', resourceCode);
  }
  return next;
}

export function buildAttributedPath(pathname, search, extraParams = {}, hash = '') {
  const params = buildAttributionParams(search);
  Object.entries(extraParams).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      params.delete(key);
    } else {
      params.set(key, String(value));
    }
  });
  const query = params.toString();
  const targetPath = query ? `${pathname}?${query}` : pathname;
  return hash ? `${targetPath}#${hash.replace(/^#/, '')}` : targetPath;
}

export function buildResourceDetailPath(resourceId, resourceCode, search) {
  return buildAttributedPath(`/resources/${resourceId}`, search, {
    resourceCode: resourceCode || '',
  });
}

export function buildResourceListPath(search, extraParams = {}) {
  return buildAttributedPath('/resources', search, extraParams);
}

export function buildWechatModalPayload({
  consultInfo,
  searchParams,
  pathname,
  search,
  resourceId = null,
  resourceCode = '',
  resourceTitle = '',
}) {
  return {
    consultInfo,
    sourcePage: getLandingPage(pathname, search),
    channel: getChannel(searchParams),
    trackingCode: getTrackingCode(searchParams),
    targetResourceId: resourceId,
    resourceCode: resourceCode || getTargetResourceCode(searchParams),
    resourceTitle,
  };
}

export function buildLeadPayload({
  values,
  searchParams,
  pathname,
  search,
  resourceId = null,
  resourceCode = '',
}) {
  return {
    ...values,
    sourcePage: getLandingPage(pathname, search),
    channel: getChannel(searchParams),
    trackingCode: getTrackingCode(searchParams),
    targetResourceId: resourceId,
    targetResourceCode: resourceCode || getTargetResourceCode(searchParams),
  };
}

export function buildLeadPayloadFromContext({ values, modalContext }) {
  return {
    ...values,
    sourcePage: modalContext?.sourcePage || '',
    channel: modalContext?.channel || '',
    trackingCode: modalContext?.trackingCode || '',
    targetResourceId: modalContext?.targetResourceId || null,
    targetResourceCode: modalContext?.resourceCode || '',
  };
}

export async function reportVisitTraceOnce({
  pathname,
  search,
  searchParams,
  targetResourceId = null,
  targetResourceCode = '',
}) {
  if (typeof window === 'undefined') {
    return;
  }

  const landingPage = getLandingPage(pathname, search);
  const channel = getChannel(searchParams);
  const trackingCode = getTrackingCode(searchParams);
  const resourceCode = targetResourceCode || getTargetResourceCode(searchParams);
  const clientId = ensureClientId();
  const traceKey = [
    TRACE_PREFIX,
    landingPage,
    channel || '-',
    trackingCode || '-',
    targetResourceId || '-',
    resourceCode || '-',
  ].join(':');

  if (window.sessionStorage.getItem(traceKey)) {
    return;
  }

  try {
    await webApi.createVisitTrace({
      channel,
      trackingCode,
      landingPage,
      targetResourceId,
      targetResourceCode: resourceCode,
      clientId,
    });
    window.sessionStorage.setItem(traceKey, '1');
  } catch (error) {
    // 访问归因失败不应阻塞页面主流程
  }
}
