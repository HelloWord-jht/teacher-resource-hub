import { Outlet, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import SiteHeader from '../components/web/SiteHeader';
import SiteFooter from '../components/web/SiteFooter';
import FloatingWechatButton from '../components/web/FloatingWechatButton';
import WechatConsultModal from '../components/web/WechatConsultModal';
import { useSiteStore } from '../store/siteStore';
import { useWechatStore } from '../store/wechatStore';
import { buildWechatModalPayload } from '../utils/attribution';

function WebLayout() {
  const location = useLocation();
  const { homeData, fetchHomeData } = useSiteStore();
  const { open, consultInfo, modalContext, pageContext, openModal, closeModal } = useWechatStore();

  useEffect(() => {
    fetchHomeData().catch(() => {});
  }, [fetchHomeData]);

  const currentConsultInfo = consultInfo || homeData?.wechatConsult;
  const searchParams = new URLSearchParams(location.search);
  const defaultModalPayload = buildWechatModalPayload({
    consultInfo: homeData?.wechatConsult,
    searchParams,
    pathname: location.pathname,
    search: location.search,
    resourceId: pageContext?.targetResourceId,
    resourceCode: pageContext?.resourceCode,
    resourceTitle: pageContext?.resourceTitle,
  });

  return (
    <>
      <SiteHeader
        siteName={homeData?.siteName}
        onWechatClick={() => openModal(defaultModalPayload)}
      />
      <main>
        <Outlet />
      </main>
      <SiteFooter siteName={homeData?.siteName} footerText={homeData?.footerText} />
      <FloatingWechatButton
        onOpenConsult={() => openModal(defaultModalPayload)}
        wechatId={currentConsultInfo?.wechatId}
        resourceCode={pageContext?.resourceCode}
      />
      <WechatConsultModal
        open={open}
        consultInfo={currentConsultInfo}
        modalContext={modalContext}
        onClose={closeModal}
      />
    </>
  );
}

export default WebLayout;
