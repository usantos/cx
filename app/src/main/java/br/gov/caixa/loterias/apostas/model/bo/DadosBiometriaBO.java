package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.EBean;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.BaseBO;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.ServerMethods;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutenticacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BiometriaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.BiometriaCRUD;
import br.gov.caixa.loterias.apostas.model.dao.crud.UsuarioCRUD;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.Criptografia;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class DadosBiometriaBO extends BaseBO {

    public static void salvarBiometria(String refresh) {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            BiometriaCRUD crud = new BiometriaCRUD(context);
            if (crud.lerBiometria() != null){
                crud.deletaBiometria();
            }
            crud.inserirBiometria(new BiometriaDTO(getEncoded(refresh)));
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
            }
        }

        return biometriaDTO;
    }

    public static void limparRegistros() {
        salvarBiometria("");
    }
}
