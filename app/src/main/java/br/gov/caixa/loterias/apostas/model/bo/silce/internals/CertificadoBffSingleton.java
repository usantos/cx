package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class CertificadoBffSingleton {
    private static CertificadoBffSingleton instance;
    private static List<Integer> certificados;
    private static int position;

    private CertificadoBffSingleton(){ }

    public static CertificadoBffSingleton getInstance(){
        if (instance == null){
            instance = new CertificadoBffSingleton();
            initCertificados();
        }
        return instance;
    }

    private static void initCertificados() {
        certificados = new ArrayList<>();
        certificados.add(R.raw.certificado_apim_nuvem);
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
