package br.gov.caixa.loterias.apostar.interno.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.NetworkResponse;

import org.androidannotations.annotations.EBean;

import java.util.HashMap;
import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.RetornoListaCarrinho;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AutenticacaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostar.interno.utils.Aplicacao;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * Created by cedesbr450 on 07/12/17.
 */

@EBean(scope = Singleton)
public class DadosUsuarioBO extends BaseBO {

    private static final String USUARIO_KEY = "USUARIO_KEY";
    private final static String RAPIDAO_KEY = "RAPIDAO_KEY";

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
        silceConnection.request(request);
    }

    public void autenticar(final String email, final String senha,
                           final RequestListener<UsuarioLogadoResponse> listener) {

        AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
        autenticacaoDTO.setEmail(email);
        autenticacaoDTO.setSenha(senha);

        silceConnection.post(ServerMethods.LOGIN_PATH, new LinkedHashMap<>(),
                autenticacaoDTO, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), new LinkedHashMap<>());
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
        silceConnection.request(request);
    }

    public void gravarUsuario(CadastrarApostadorDTO apostadorDTO, final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.GRAVAR_USUARIO_PATH, new LinkedHashMap<>(),
                apostadorDTO,
                ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
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
            editor.putString(CPF, cpf);
            editor.apply();
        }
    }

    public static String obterCpf() {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(USUARIO_KEY, Context.MODE_PRIVATE);
            return sharedPreferences.getString(CPF, "");
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
        silceConnection.request(request);
    }

    public void putDadosUsuario(ApostadorAlteracaoDTO apostadorAlteracaoDTO, final RequestListener<CadastrarApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPutRequest(ServerMethods.USUARIOS_PATH, new LinkedHashMap<>(),
                apostadorAlteracaoDTO, CadastrarApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void postVerificarCadastro(final RetornoListaCarrinho listaApostas, final RequestListener<UsuarioLogadoResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.USUARIOS_VERIFICAR_CADASTRO_PATH, new LinkedHashMap<>(),
                listaApostas, UsuarioLogadoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void deleteCartao(Long idCartao, RequestListener<ListCartaoDTOResponse> listener) {
        HashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + keycloakBO.getAccessToken());
        GsonRequest request = silceConnection.buildDeleteRequest(ServerMethods.EXCLUIR_CARTAO_PATH, idCartao.toString(), ListCartaoDTOResponse.class,
                listener.getSilceListener(), listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }
}
