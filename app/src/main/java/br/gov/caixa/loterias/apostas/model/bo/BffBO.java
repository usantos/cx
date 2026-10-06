package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.BffSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoBffSingleton;
import br.gov.caixa.loterias.apostas.utils.ServicoBffUtil;

public abstract class BffBO extends BaseBO {
    protected static final String PUBLICO = "/publico";
    protected static final String LOGADO = "/logado";

    public BffBO() {
        super(BffSingleton.getInstance().getRequestQueue(),
                ServicoBffUtil.getBaseUrlBff(),
                true,
                onCertificadoListener());
    }

    private static OnCertificadoListener onCertificadoListener() {
        return (String urlBase) -> {
            if (CertificadoBffSingleton.getInstance().atualizaNovoCertificado()){
                BffSingleton.getInstance().initQueue();
                return BffSingleton.getInstance().getRequestQueue();
            }

            return null;
        };
    }


}
