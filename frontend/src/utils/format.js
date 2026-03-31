export function formatPrice(value) {
  if (value === undefined || value === null || value === '') {
    return '咨询报价';
  }
  return `¥${Number(value).toFixed(2)}`;
}

export function formatDateTime(value) {
  if (!value) {
    return '--';
  }
  return String(value).replace('T', ' ').slice(0, 16);
}

export function stripHtml(html) {
  if (!html) {
    return '';
  }
  return html.replace(/<[^>]+>/g, '');
}

export function setDocumentMeta({ title, description }) {
  if (title) {
    document.title = title;
  }
  if (description) {
    const descriptionMeta = document.querySelector('meta[name="description"]');
    if (descriptionMeta) {
      descriptionMeta.setAttribute('content', description);
    }
  }
}
