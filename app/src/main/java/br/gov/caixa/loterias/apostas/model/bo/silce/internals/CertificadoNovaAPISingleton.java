package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import java.util.ArrayList;
import java.util.List;

public class CertificadoNovaAPISingleton {
    private static CertificadoNovaAPISingleton instance;
    private static List<Integer> certificados;
    private static int position;

    private CertificadoNovaAPISingleton(){
    }

    public static CertificadoNovaAPISingleton getInstance(){
        if (instance == null){
            instance = new CertificadoNovaAPISingleton();
            initCertificados();
        }
        return instance;
    }

    private static void initCertificados() {
        certificados = new ArrayList<>();
        //precisa do certificado da nova api
        position = 0;
    }

    public int getCertificadoAtual() {
        return certificados.get(position);
    }

    public boolean trocaCertificado() {
        if (certificados != null && !certificados.isEmpty() && position < (certificados.size() -1)){
            position++;
            return true;
        }
        return false;
    }
}
