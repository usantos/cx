package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;

import org.apache.commons.codec.binary.Base64;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.dao.crud.BiometriaCRUD;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;

public class DadosBiometriaBO extends SilceBO {

    public DadosBiometriaBO() {
        super();
    }

    public static void salvarBiometria(String refresh, String nome, String cpf) {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            BiometriaCRUD crud = new BiometriaCRUD(context);
            if (crud.lerBiometria() != null){
                crud.deletaBiometria();
            }
            crud.inserirBiometria(new BiometriaDTO(getEncoded(refresh), getEncoded(nome), getEncoded(cpf)));
        }
    }

    private static String getEncoded(String dado) {
        return Bin.fromUtf8(dado).toBase64();
    }

    private static String getDecoded(String dado) {
        Base64 ed = new Base64();
        String decoded = new String(ed.decode(dado.getBytes()));
        return decoded;
    }

    public static BiometriaDTO obterBiometria() {
        Context context = Aplicacao.application.getApplicationContext();

        BiometriaDTO biometriaDTO = null;
        if (context != null) {
            BiometriaCRUD crud = new BiometriaCRUD(context);
            biometriaDTO = crud.lerBiometria();

            if (biometriaDTO != null){
                biometriaDTO.setRefresh(getDecoded(biometriaDTO.getRefresh()));
                biometriaDTO.setNome(getDecoded(biometriaDTO.getNome()));
                biometriaDTO.setCpf(getDecoded(biometriaDTO.getCpf()));
            }
        }

        return biometriaDTO;
    }

    public static void limparRegistros() {
        salvarBiometria("","","");
    }
}
