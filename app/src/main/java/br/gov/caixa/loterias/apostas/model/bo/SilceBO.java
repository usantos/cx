package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoSilceSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.SilceSingleton;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public abstract class SilceBO extends BaseBO {

    public SilceBO() {
        super(SilceSingleton.getInstance().getRequestQueue(),
                BuildConfigManager.getVariavel("CAIXA_BASE_URL_SILCE"),
                true,
                onCertificadoListener());
    }

    private static OnCertificadoListener onCertificadoListener() {
        return (String baseURL) -> {
            if (CertificadoSilceSingleton.getInstance().atualizaNovoCertificado()){
                SilceSingleton.getInstance().initQueue();
                return SilceSingleton.getInstance().getRequestQueue();
            }

            return null;
        };
    }


}
