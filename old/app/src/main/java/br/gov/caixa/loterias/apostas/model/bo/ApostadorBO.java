package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnCertificadoListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ApostadorSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNuvemSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.NuvemSingleton;
import br.gov.caixa.loterias.apostas.utils.ServicoApostadorUtil;

public class ApostadorBO extends BaseBO {

    protected static final String LOGADO = "/logado";

    public ApostadorBO() {
        super(ApostadorSingleton.getInstance().getRequestQueue(),
                ServicoApostadorUtil.getBaseUrlApostador(),
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