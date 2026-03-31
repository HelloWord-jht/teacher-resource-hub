import { Outlet } from 'react-router-dom';
import { useEffect } from 'react';
import SiteHeader from '../components/web/SiteHeader';
import SiteFooter from '../components/web/SiteFooter';
import FloatingWechatButton from '../components/web/FloatingWechatButton';
import WechatConsultModal from '../components/web/WechatConsultModal';
import { useSiteStore } from '../store/siteStore';
import { useWechatStore } from '../store/wechatStore';

function WebLayout() {
  const { homeData, fetchHomeData } = useSiteStore();
  const { open, consultInfo, openModal, closeModal } = useWechatStore();

  useEffect(() => {
    fetchHomeData().catch(() => {});
  }, [fetchHomeData]);

  const currentConsultInfo = consultInfo || homeData?.wechatConsult;

  return (
    <>
      <SiteHeader
        siteName={homeData?.siteName}
        onWechatClick={() => openModal(homeData?.wechatConsult)}
      />
      <main>
        <Outlet />
      </main>
      <SiteFooter siteName={homeData?.siteName} footerText={homeData?.footerText} />
      <FloatingWechatButton onClick={() => openModal(homeData?.wechatConsult)} />
      <WechatConsultModal open={open} consultInfo={currentConsultInfo} onClose={closeModal} />
    </>
  );
}

export default WebLayout;
