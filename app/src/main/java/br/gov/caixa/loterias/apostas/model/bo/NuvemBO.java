package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNuvemSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NuvemSingleton;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public abstract class NuvemBO extends BaseBO {

    public NuvemBO() {
        super(NuvemSingleton.getInstance().getRequestQueue(),
                //BuildConfigManager.getVariavel("CAIXA_BASE_URL_SILCE"),
                "https://apim-silce-tqs.azure-api.net/loterias-web/carrinho/v1",
                true,
                onCertificadoListener());
    }

    private static OnCertificadoListener onCertificadoListener() {
        return (String urlBase) -> {
            if (CertificadoNuvemSingleton.getInstance().atualizaNovoCertificado()){
                NuvemSingleton.getInstance().initQueue();
                return NuvemSingleton.getInstance().getRequestQueue();
            }

            return null;
        };
    }


}
