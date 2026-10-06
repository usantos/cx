package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class CertificadoSilceSingleton {
    private static CertificadoSilceSingleton instance;
    private static List<Integer> certificados;
    private static int position;

    private CertificadoSilceSingleton(){ }

    public static CertificadoSilceSingleton getInstance(){
        if (instance == null){
            instance = new CertificadoSilceSingleton();
            initCertificados();
        }
        return instance;
    }

    private static void initCertificados() {
        certificados = new ArrayList<>();
        certificados.add(R.raw.certificado_atual);
        certificados.add(R.raw.novo_certificado);
        position = 0;
    }

    public int getCertificadoAtual() {
        return certificados.get(position);
    }

    public boolean atualizaNovoCertificado() {
        if (certificados != null && !certificados.isEmpty() && position < (certificados.size() -1)){
            position++;
            return true;
        }
        return false;
    }
}
