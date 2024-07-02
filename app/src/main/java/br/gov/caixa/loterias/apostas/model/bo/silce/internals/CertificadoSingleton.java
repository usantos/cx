package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import br.gov.caixa.loterias.apostas.R;

public class CertificadoSingleton {
    private static CertificadoSingleton instance;
    private static int certificadoAtual;

    private CertificadoSingleton(){ }

    public static CertificadoSingleton getInstance(){
        if (instance == null){
            instance = new CertificadoSingleton();
            certificadoAtual = R.raw.certificado_mobilidade_atual;
        }
        return instance;
    }

    public int getCertificadoAtual() {
        return certificadoAtual;
    }

    public void trocaCertificado() {
        certificadoAtual = R.raw.certificado_mobilidade_novo;
    }
}
