package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
public class CertificadoNuvemSingleton {
    private static CertificadoNuvemSingleton instance;
    private static List<Integer> certificados;
    private static int position;

    private CertificadoNuvemSingleton(){ }

    public static CertificadoNuvemSingleton getInstance(){
        if (instance == null){
            instance = new CertificadoNuvemSingleton();
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
