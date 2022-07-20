package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutenticacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.SilceCrypto;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.Criptografia;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

import androidx.annotation.NonNull;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;

/**
 * Created by cedesbr450 on 07/12/17.
 */

@EBean(scope = Singleton)
public class DadosUsuarioBO extends BaseBO {

    private static final String USUARIO_KEY = "USUARIO_KEY";
    private final static String RAPIDAO_KEY = "RAPIDAO_KEY";
    private final static String LOGIN_SUCESSO = "LOGIN_SUCESSO";
    private final static String SUPER_SETE_TUTORIAL = "SUPER_SETE_TUTORIAL";
    private final static String MAIS_MILIONARIA_TUTORIAL = "MAIS_MILIONARIA_TUTORIAL";

    private static final String NOME = "NOME";
    private static final String CPF = "CPF";

    public void cartoes(final RequestListener<ListCartaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", "1");

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        GsonRequest request = silceConnection.buildGetRequest(ServerMethods.LIST_CARTOES_PATH, queryParams, ListCartaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public void autenticar(final String email, final String senha,
                           final RequestListener<UsuarioLogadoResponse> listener) {

        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setEmail(email);
        autenticacaoDTO.setSenha(senha);

        silceConnection.post(ServerMethods.LOGIN_PATH, new LinkedHashMap<>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), new LinkedHashMap<>(), listener);
    }

    public void sair(final RequestListener<NetworkResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        GsonRequest request = silceConnection.buildPostRequest(ServerMethods.SAIR_PATH, queryParams,
                "", NetworkResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public void gravarUsuario(CadastrarApostadorDTO apostadorDTO, @NonNull final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.GRAVAR_USUARIO_PATH, new LinkedHashMap<>(),
                apostadorDTO,
                ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public static void salvarNomeUsuairo(String name) {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(NOME, name);
            editor.apply();
        }
    }

    public static String obterNome() {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getString(NOME, "");
        }
        return "";
    }

    public static void salvarCpfUsuario(String cpf) {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CPF, Criptografia.encrypt(cpf,12));
            editor.apply();
        }
    }

    public static String obterCpf() {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
            return Criptografia.decrypt(sharedPreferences.getString(CPF, ""),12);
        }
        return "";
    }

    public static void limparRegistros() {
        salvarCpfUsuario("");
        salvarNomeUsuairo("");
    }

    public static void updateFecharIntroducao(Activity activity, Boolean status) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.key_introducao), status);
        editor.commit();

    }

    public static Boolean checarFecharIntroducao(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(activity.getString(R.string.key_introducao), false);
        return status;
    }

    public static Boolean checarTutorialRapidao() {
        Context context =  Aplicacao.application.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(RAPIDAO_KEY,Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(RAPIDAO_KEY, false);
        return status;
    }

    public static void updateTutorialRapidao(Boolean status) {
        Context context =  Aplicacao.application.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(RAPIDAO_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(RAPIDAO_KEY, status);
        editor.commit();

    }

    public static Boolean checarTutorialSuperSete() {
        return checarBoolean(SUPER_SETE_TUTORIAL);
    }

    public static Boolean checarTutorialMaisMilionaria() {
        return checarBoolean(MAIS_MILIONARIA_TUTORIAL);
    }

    public static void updateTutorialSuperSete(Boolean status) {
        updateBoolean(status, SUPER_SETE_TUTORIAL);
    }

    public static void updateTutorialMaisMilionaria(Boolean status) {
        updateBoolean(status, MAIS_MILIONARIA_TUTORIAL);
    }

    public static Boolean checarBoolean(String key) {
        Context context =  Aplicacao.application.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(key,Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(key, false);
        return status;
    }

    public static void updateBoolean(Boolean status, String key) {
        Context context =  Aplicacao.application.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, status);
        editor.commit();
    }

    public static void updateUsuarioLogado(Activity activity, Boolean status) {
        SharedPreferences sharedPref = activity.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.key_usuario_logado), status);
        editor.commit();
    }

    public static Boolean checarUsuarioLogado(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(activity.getString(R.string.key_usuario_logado), false);
        return status;
    }

    public static void updateVerificarIdade(Activity activity, Boolean status) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(activity.getString(R.string.key_idade), status);
        editor.commit();
    }

    public static Boolean checarVerificarIdade(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(activity.getString(R.string.key_idade), true);
        return status;
    }

    public void getDadosUsuario(final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        GsonRequest request = silceConnection.buildGetRequest(ServerMethods.USUARIOS_RECUPERAR_DADOS_PATH, queryParams, ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public void putDadosUsuario(ApostadorAlteracaoDTO apostadorAlteracaoDTO, final RequestListener<CadastrarApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPutRequest(ServerMethods.USUARIOS_PATH, new LinkedHashMap<>(),
                apostadorAlteracaoDTO, CadastrarApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public void postVerificarCadastro(final List<IdentificaoDeUmaApostaDas8Modalidades> listaApostas, final RequestListener<UsuarioLogadoResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setApostas(listaApostas);
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.USUARIOS_VERIFICAR_CADASTRO_PATH, new LinkedHashMap<>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public void putLerNotificacao(final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPutRequest(
                ServerMethods.LER_NOTIFICACAO.replace("{idNotificacao}", UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getId().toString()),
                new LinkedHashMap<>(),
                null, RetornoPadraoDTO.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

    public void deleteCartao(Long idCartao, RequestListener<ListCartaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", "1");

        HashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
        GsonRequest request = silceConnection.buildDeleteRequest(ServerMethods.EXCLUIR_CARTAO_PATH + idCartao.toString(), queryParams, ListCartaoDTOResponse.class,
                listener.getSilceListener(), listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request, listener);
    }

}
