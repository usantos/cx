package br.gov.caixa.loterias.apostas.model.bo;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoSuspensaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AutoavaliacaoRespostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BoloesDisponiveisResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfigConsultaResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConsultaDadosChavePixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosConfiguraveisDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesCompraResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;
import br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.FiltroMarketPlace;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public class ApostaNuvemBO extends NuvemBO implements ApostaService {
    private static ApostaNuvemBO instance;

    private ApostaNuvemBO(){
        super();
    }

    public static ApostaNuvemBO getInstance(){
        if (instance == null){
            instance = new ApostaNuvemBO();
        }
        return instance;
    }

    @Override
    public void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequestNuvem(ServerMethodsNuvem.EQUIPES_ESPORTIVAS_LISTA_EQUIPES_NUVEM_PATH,
                new LinkedHashMap<>(),
                EscudoEquipeEsportivaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
    @Override
    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        final GsonRequest request = getServiceConnection().buildGetRequestNuvem(ServerMethodsNuvem.MODALIDADES_NUVEM_PATH,
                new LinkedHashMap<>(),
                ModalidadeDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }

    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = getServiceConnection().buildGetRequestNuvem(ServerMethodsNuvem.APOSTAS_FAVORITAS_LISTA_UNICA_NUVEM_PATH,
                queryParams,
                ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                listener.getSilceListener(),
                listener.getErrorListener(),
                getHeaderToken());

        request.setErrorListener(interceptError(request, getHeaderToken(), listener));
        getServiceConnection().request(request, listener);
    }
}