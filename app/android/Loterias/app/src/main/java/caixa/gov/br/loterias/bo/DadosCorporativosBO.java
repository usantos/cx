package caixa.gov.br.loterias.bo;

import android.content.Context;

import com.android.volley.NetworkResponse;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;

import caixa.gov.br.loterias.BuildConfig;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.data.UsuarioLogadoSingleton;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.BairroDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ListDTOEnumIntegerResponse;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ParametrosSimulacaoResponse;
import caixa.gov.br.loterias.bo.silce.dto.RepassometroDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.TermoDeUsoDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.bo.silce.internals.SilceSingleton;

/**
 * Created by igorvilar on 10/11/17.
 */

public class DadosCorporativosBO {

    private SilceClient silceClient;
    private Context context;


    public DadosCorporativosBO(Context context){
        this.context = context;

        setupSilceClient();
    }

    private void setupSilceClient(){
        SilceSingleton queue = new SilceSingleton(context);
        SilceConnection conn = SilceConnection.create(queue.getRequestQueue(), BuildConfig.CAIXA_BASE_URL_SILCE);
        silceClient = SilceClient.create(conn);
    }

    public void ufs(final SilceClient.Listener<UnidadeFederacaoDTOResponse> listener,
                             final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();


        silceClient.getConnection().get(SilceClient.UFS_PATH, queryParams, UnidadeFederacaoDTOResponse.class,
                silceClient.volleyListener(listener, UnidadeFederacaoDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void municipios(String idUf, final SilceClient.Listener<MunicipioDTOResponse> listener,
                    final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("idUF", idUf);

        silceClient.getConnection().get(SilceClient.MUNICIPIOS_PATH, queryParams, MunicipioDTOResponse.class,
                silceClient.volleyListener(listener, MunicipioDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void bairros(String idUf, String numeroCidade, String digitoVerificador, String cep, final SilceClient.Listener<BairroDTOResponse> listener,
                        final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(idUf)) {
            queryParams.put("idUF", idUf);
        }
        if (StringUtils.isNotEmpty(numeroCidade)) {
            queryParams.put("numero", numeroCidade);
        }
        if (StringUtils.isNotEmpty(digitoVerificador)) {
            queryParams.put("digitoVerificador", digitoVerificador);
        }
        if (StringUtils.isNotEmpty(cep)) {
            queryParams.put("cep", cep);
        }

        silceClient.getConnection().get(SilceClient.BAIRROS_PATH, queryParams, BairroDTOResponse.class, silceClient.volleyListener(listener, BairroDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void termo(final SilceClient.Listener<TermoDeUsoDTOResponse> listener,
                    final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();


        silceClient.getConnection().get(SilceClient.TERMO_PATH, queryParams, TermoDeUsoDTOResponse.class,
                silceClient.volleyListener(listener, TermoDeUsoDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), new LinkedHashMap<String, String>());
    }

    public void repasses(final SilceClient.Listener<RepassometroDTOResponse> listener,
                          final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        silceClient.getConnection().get(SilceClient.REPASSOMETRO_PATH, queryParams, RepassometroDTOResponse.class,
                silceClient.volleyListener(listener, RepassometroDTOResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }

    public void parametrosSimulacao(final SilceClient.Listener<ParametrosSimulacaoResponse> listener,
                         final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

        silceClient.getConnection().get(SilceClient.PARAM_SIMULACAO_PATH, queryParams, ParametrosSimulacaoResponse.class,
                silceClient.volleyListener(listener, ParametrosSimulacaoResponse.class),
                silceClient.volleyErrorListener(errorListener), headers);
    }


    public void validarApostaFavorita(final ApostaFavoritaDTO aposta,
                                     final SilceClient.Listener<NetworkResponse> listener,
                                     final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);

        silceClient.getConnection().post(SilceClient.VALIDAR_FAVORITA_PATH, new LinkedHashMap<String, String>(),
                aposta, NetworkResponse.class,
                silceClient.volleyListener(listener, NetworkResponse.class),
                silceClient.volleyErrorListener(errorListener),headers);
    }

    public void salvarApostaFavorita(final ApostaFavoritaDTO aposta,
                           final SilceClient.Listener<NetworkResponse> listener,
                           final SilceClient.ErrorListener errorListener) {

        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Authorization", "Bearer " + UsuarioLogadoSingleton.getInstance().token);


        silceClient.getConnection().post(SilceClient.INCLUIR_FAVORITA_PATH, new LinkedHashMap<String, String>(),
                aposta, NetworkResponse.class,
                silceClient.volleyListener(listener, NetworkResponse.class),
                silceClient.volleyErrorListener(errorListener),headers);
    }

    public void buscarLotericasHabilitadas(final SilceClient.Listener<ListDTOEnumIntegerResponse> listener, final SilceClient.ErrorListener errorListerner) {

        silceClient.getConnection().get(SilceClient.LOTERIAS_HABILITADAS, new LinkedHashMap<String, String>(), ListDTOEnumIntegerResponse.class,
                silceClient.volleyListener(listener, ListDTOEnumIntegerResponse.class), silceClient.volleyErrorListener(errorListerner), new LinkedHashMap<String, String>());
    }


    private static <T> String join(Collection<T> collection, String delimiter) {
        StringBuffer result = new StringBuffer("");
        boolean first = true;

        for(T e : collection) {
            if(!first) {
                result.append(delimiter);
                first = true;
            }
            result.append(e);
        }

        return result.toString();
    }


}
