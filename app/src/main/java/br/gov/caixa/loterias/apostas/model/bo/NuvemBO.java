package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNuvemSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NuvemSingleton;
import br.gov.caixa.loterias.apostas.utils.ServicoNuvemUtil;

public abstract class NuvemBO extends BaseBO {
    protected static final String PUBLICO = "/publico";
    protected static final String LOGADO = "/logado";

    public NuvemBO() {
        super(NuvemSingleton.getInstance().getRequestQueue(),
                //BuildConfigManager.getVariavel("CAIXA_BASE_URL_SILCE"),
                //"https://apim-silce-tqs.azure-api.net/loterias-web/carrinho/v1",
                //"https://silce.carrinho.tqs.caixa.gov.br/loterias-web/carrinho/v1",
                ServicoNuvemUtil.getBaseUrlNuvem(),
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
