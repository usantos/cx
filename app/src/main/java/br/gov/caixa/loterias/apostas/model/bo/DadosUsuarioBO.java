package br.gov.caixa.loterias.apostas.model.bo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutenticacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PushRegistrarDispositivoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PushRegistrarDispositivoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UsuarioLogadoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.UsuarioCRUD;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;

/**
 * Created by cedesbr450 on 07/12/17.
 */
public class DadosUsuarioBO extends SilceBO {
    private static final String INTRODUCAO_KEY = "INTRODUCAO_KEY";
    private static final String USUARIO_KEY = "USUARIO_KEY";
    private final static String RAPIDAO_KEY = "RAPIDAO_KEY";
    private final static String LOGIN_SUCESSO = "LOGIN_SUCESSO";
    public final static String SUPER_SETE_TUTORIAL = "SUPER_SETE_TUTORIAL";
    public final static String MAIS_MILIONARIA_TUTORIAL = "MAIS_MILIONARIA_TUTORIAL";
    public final static String BOLAO_TUTORIAL = "BOLAO_TUTORIAL";

    private static DadosUsuarioBO instance;

    private DadosUsuarioBO(){
        super();
    }

    public static DadosUsuarioBO getInstance(){
        if (instance == null){
            instance= new DadosUsuarioBO();
        }
        return instance;
    }

    public void cartoes(String meioPagamento, final RequestListener<ListCartaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", meioPagamento);

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.LIST_CARTOES_PATH, queryParams, ListCartaoDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void sair(final RequestListener<NetworkResponse> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.SAIR_PATH, queryParams,
                null, NetworkResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void gravarUsuario(CadastrarApostadorDTO apostadorDTO, @NonNull final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.GRAVAR_USUARIO_PATH, new LinkedHashMap<>(),
                apostadorDTO,
                ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public static void salvarUsuario(String name, String cpf) {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            UsuarioCRUD              crud = new UsuarioCRUD(context);
            if (crud.lerUsuario() != null){
                crud.deletaUsuario();
            }
            crud.inserirUsuario(new UsuarioDTO(getEncoded(name), getEncoded(cpf)));
        }
    }

    public static boolean temUsuarioBD() {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            UsuarioCRUD crud = new UsuarioCRUD(context);
            if (crud.lerUsuario() != null){
                return true;
            }
        }
        return false;
    }

    private static String getEncoded(String dado) {
        return Bin.fromUtf8(dado).toBase64();
    }

    public static String obterNome() {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            UsuarioCRUD       crud       = new UsuarioCRUD(context);
            UsuarioDTO        usuario = crud.lerUsuario();
            if (usuario != null){
                return getDecoded(usuario.getNome());
            }
        }
        return "";
    }

    public static String obterCpf() {
        Context context = Aplicacao.application.getApplicationContext();
        if (context != null) {
            UsuarioCRUD       crud       = new UsuarioCRUD(context);
            UsuarioDTO        usuario = crud.lerUsuario();
            if (usuario != null){
                return getDecoded(usuario.getCpf());
            }
        }
        return "";
    }

    private static String getDecoded(String dado) {
        Base64 ed = new Base64();
        String decoded = new String(ed.decode(dado.getBytes()));
        return decoded;
    }

    public static void limparRegistros() {
        salvarUsuario("", "");
    }

    public static void updateFecharIntroducao(Boolean status) {
        Context context =  Aplicacao.application.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(INTRODUCAO_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(INTRODUCAO_KEY, status);
        editor.commit();
    }

    public static Boolean checarFecharIntroducao() {
        Context context =  Aplicacao.application.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(INTRODUCAO_KEY,Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean(INTRODUCAO_KEY, false);
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

    public static Boolean checarTutorial(String tutorial) {
        return checarBoolean(tutorial);
    }

    public static void updateTutorial(Boolean status, String tutorial) {
        updateBoolean(status, tutorial);
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

    public void recalcularLimite(final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.USUARIOS_RECALCULAR_LIMITE_AUTORIZADO_PATH, queryParams,
                null, ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void getDadosUsuario(final RequestListener<ApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.USUARIOS_RECUPERAR_DADOS_PATH, queryParams, ApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void putDadosUsuario(ApostadorAlteracaoDTO apostadorAlteracaoDTO, final RequestListener<CadastrarApostadorDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        final GsonRequest request = getServiceConnection().buildPutRequest(ServerMethods.USUARIOS_PATH, new LinkedHashMap<>(),
                apostadorAlteracaoDTO, CadastrarApostadorDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void putLerNotificacao(final RequestListener<RetornoPadraoResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        final GsonRequest request = getServiceConnection().buildPutRequest(
                ServerMethods.LER_NOTIFICACAO.replace("{idNotificacao}", UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getId().toString()),
                new LinkedHashMap<>(),
                null, RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void deleteCartao(String idCartao, String meioPagamento, RequestListener<ListCartaoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("meioPagamento", meioPagamento);

        HashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());
        GsonRequest request = getServiceConnection().buildDeleteRequest(ServerMethods.EXCLUIR_CARTAO_PATH + idCartao, queryParams, ListCartaoDTOResponse.class,
                                                                 listener.getSilceListener(), listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void getVerificaUsuarioBloqueado(final RequestListener<RetornoPadraoResponse> listener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        GsonRequest request = getServiceConnection().buildGetRequest(ServerMethods.VERIFICA_USUARIO_BLOQUEADO, new LinkedHashMap<>(), RetornoPadraoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

    public void postPushRegistrarDispositivo(final String cpf, final String token, final RequestListener<PushRegistrarDispositivoResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + KeycloakBO.getInstance().getAccessToken());

        List<Long> listCpf = new ArrayList<>();
        listCpf.add(Long.valueOf(cpf));
        PushRegistrarDispositivoDTO pushRegistrarTokenDTO = new PushRegistrarDispositivoDTO();
        pushRegistrarTokenDTO.setPlataforma("android");
        pushRegistrarTokenDTO.setCanal("loterias");
        pushRegistrarTokenDTO.setIdentificadores(listCpf);
        pushRegistrarTokenDTO.setToken(token);

        final GsonRequest request = getServiceConnection().buildPostRequest(ServerMethods.PUSH_NOTIFICACAO_REGISTRAR_DISPOSITIVO_PATH, new LinkedHashMap<>(),
                pushRegistrarTokenDTO, PushRegistrarDispositivoResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(), headers);

        request.setErrorListener(interceptError(request, headers, listener));
        getServiceConnection().request(request, listener);
    }

}
