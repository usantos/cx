package br.gov.caixa.loterias.apostas.model.bo;

import org.androidannotations.annotations.EBean;

import java.util.LinkedHashMap;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class BolaoBO extends BaseBO {

    public void getBoloesDisponiveis(final int tipoConsulta, final int idMunicipio,
                                     final int idUf, final int pagina, final  int qtdPorPagina,
                                     final RequestListener<BoloesDisponiveisResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("tipoConsulta", String.valueOf(tipoConsulta));
        queryParams.put("idMunicipio", String.valueOf(idMunicipio));
        queryParams.put("idUf", String.valueOf(idUf));
        queryParams.put("pagina", String.valueOf(pagina));
        queryParams.put("qtdPorPagina", String.valueOf(qtdPorPagina));

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.BOLOES_DISPONIVEIS,
                                                                    queryParams,
                                                                    BoloesDisponiveisResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request, listener);
    }
}