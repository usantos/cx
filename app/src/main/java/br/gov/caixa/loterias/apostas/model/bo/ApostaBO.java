package br.gov.caixa.loterias.apostas.model.bo;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoNovoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesHistoricoPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.InformacaoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacoesApostaFiltroEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StringResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.GsonRequest;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

@EBean(scope = Singleton)
public class ApostaBO extends BaseBO {

    private static LinkedHashMap<String, String> header;

    private LinkedHashMap<String, String> getHeader(){
        if(header == null){
            this.header = new LinkedHashMap<>();
        }
        this.header.put("Authorization", "Bearer " + keycloakBO.getAccessToken());

        return header;
    }

    //Carrinho Favorito
    public void validarCarrinhoFavorito(String nomeCarrinho, RequestListener<CarrinhoFavoritoDTOResponse> listener){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("nome", nomeCarrinho);

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.CARRINHO_FAVORITO_VALIDAR_NOME,
                                                                     new LinkedHashMap<>(),
                                                                     params, CarrinhoFavoritoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void transformarCarrinho(Long idCarrinhoFavorito, Integer opcaoSelecionada, RequestListener<CarrinhoDTOResponse> listener){
        String url = ServerMethods.CARRINHO_FAVORITO_TRANSFORMAR.replace("{idCarrinhoFavorito}", idCarrinhoFavorito.toString());
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("incluirEspecial", opcaoSelecionada.toString());

        final GsonRequest request = silceConnection.buildPostRequest(url,
                                                                     new LinkedHashMap<>(),
                                                                     params,
                                                                     CarrinhoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }



    public void deletaApostaCarrinhoFavorito(final String idCarrinho,
                                             final String idAposta,
                                             final RequestListener<ListApostaCarrinhoFavoritoDTO> listener) {
        String url = ServerMethods.CARRINHO_FAVORITO_DELETAR_APOSTA.replace("{id}", idCarrinho);
        url = url.replace("{idAposta}",idAposta);

        final GsonRequest request = silceConnection.buildDeleteRequest(url ,
                                                                       new LinkedHashMap<>(),
                                                                       ListApostaCarrinhoFavoritoDTO.class,
                                                                       listener.getSilceListener(),
                                                                       listener.getErrorListener(),
                                                                       getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void deletaCarrinhoFavorito(final String idCarrinho,
                                       final RequestListener<ListCarrinhoFavoritoDTO> listener) {
        String url = ServerMethods.CARRINHO_FAVORITO_DELETAR_CARRINHO.replace("{id}", idCarrinho);

        final GsonRequest request = silceConnection.buildDeleteRequest(url ,
                                                                       new LinkedHashMap<>(),
                                                                       ListCarrinhoFavoritoDTO.class,
                                                                       listener.getSilceListener(),
                                                                       listener.getErrorListener(),
                                                                       getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void salvarCarrinhoFavorito(final String nomeCarrinho,
                                       final RequestListener<CarrinhoFavoritoDTOResponse> listener) {
        salvarCarrinhoFavorito(nomeCarrinho,null,true, listener);
    }

    public void salvarCarrinhoFavorito(final String nomeCarrinho,
                                       final String idCompra,
                                       final Boolean mantemSurpresinhas,
                                       final RequestListener<CarrinhoFavoritoDTOResponse> listener) {

        Map<String, String> params = new HashMap<>();
        params.put("nome", nomeCarrinho);
        params.put("manterSurpresinhas", mantemSurpresinhas.toString());

        String url;
        if(idCompra != null){
            url = ServerMethods.CARRINHO_FAVORITO_SALVAR_COMPRA.replace("{idCompra}", idCompra);
        } else {
            url = ServerMethods.CARRINHO_FAVORITO_SALVAR;
        }


        final GsonRequest request = silceConnection.buildPostRequest(url,
                                                                     new LinkedHashMap<>(),
                                                                     params,
                                                                     CarrinhoFavoritoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void buscaCarrinhosFavoritos(RequestListener<ListCarrinhoFavoritoDTO> listener){
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.CARRINHO_FAVORITO_BUSCAR_CARRINHOS,
                                                                    new LinkedHashMap<>(),
                                                                    ListCarrinhoFavoritoDTO.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void buscaApostasCarrinhoFavorito(Long idCarrinho,RequestListener<ListApostaCarrinhoFavoritoDTO> listener){
        String url = ServerMethods.CARRINHO_FAVORITO_BUSCA_APOSTAS.replace("{idCarrinho}", idCarrinho.toString());
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    new LinkedHashMap<>(),
                                                                    ListApostaCarrinhoFavoritoDTO.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void completaJogo(final int quantidadeTotalNumeros,
                             final List<Integer> numerosMarcados,
                             final int menorPrognostico,
                             final int maiorPrognostico,
                             final RequestListener<CompletaJogoRsp> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcados", join(numerosMarcados, ","));
        queryParams.put("menorPrognostico", String.valueOf(menorPrognostico));
        queryParams.put("maiorPrognostico", String.valueOf(maiorPrognostico));

        silceConnection.get(ServerMethods.COMPLETA_JOGO_PATH,
                            queryParams,
                            CompletaJogoRsp.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    public void completaJogoNovo(final int quantidadeTotalNumeros,
                                 final ArrayList<ArrayList<Integer>> numerosMarcados,
                                 final String modalidade,
                                 final RequestListener<CompletaJogoNovoRsp> listener) {

        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        String url = ServerMethods.COMPLETA_JOGO_NOVO_PATH.replace("{modalidade}",modalidade);
        Gson gsonNumeros = new Gson();
        queryParams.put("quantidadeTotalNumeros", String.valueOf(quantidadeTotalNumeros));
        queryParams.put("numerosMarcadosMatriz",gsonNumeros.toJson(numerosMarcados));

        silceConnection.get(url,
                            queryParams,
                            CompletaJogoNovoRsp.class,
                            listener.getSilceListener(),
                            listener.getErrorListener(),
                            new LinkedHashMap<>());
    }

    //APOSTA
    public void getApostasConfirmadas(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener) {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        if(page.getAno() > 0){
            queryParams.put("ano", String.valueOf(page.getAno()));
        }
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));
        if(page.getSituacao() != 0){
            queryParams.put("situacao", String.valueOf(page.getSituacao()));
        } else {
            queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));
        }
        if(page.getModalidade() != 0){
            queryParams.put("modalidade", String.valueOf(page.getModalidade()));
        }

        if(page.getOrdenarPor() != 0) {
            queryParams.put("ordenacao", String.valueOf(page.getOrdenarPor()));
        }
        //queryParams.put("situacao", String.valueOf(page.getSituacao()));

        //        queryParams.put("idsApostas", idsApostas);

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_PATH,
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOApostaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getApostasHistorico(ApostaPageRequest page, final RequestListener<ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("mes", String.valueOf(page.getMes()));
        queryParams.put("ano", String.valueOf(page.getAno()));
        queryParams.put("offset", String.valueOf(page.getOffset()));
        queryParams.put("size", String.valueOf(page.getSize()));
        queryParams.put("situacao", String.valueOf(SituacoesApostaFiltroEnum.TODAS));

        //        queryParams.put("idsApostas", idsApostas);
        //        queryParams.put("modalidade", modalidade);

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_PATH,
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOHistoricoApostaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void postResgatarAposta(final Long idAposta,
                                   final InformacaoPagamentoDTO infoPgtoDTO,
                                   final RequestListener<RetornoPadraoDTO> listener) {

        String url = ServerMethods.APOSTAS_RESGATAR_PATH.replace("{idAposta}", String.valueOf( idAposta ));
        final GsonRequest request = silceConnection.buildPostRequest(url,
                                                                     new LinkedHashMap<>(),
                                                                     infoPgtoDTO, RetornoPadraoDTO.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void  getMercadoPagoToken(final String email, final RequestListener<StringResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("email", email);

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.TOKEN_CADASTRO_MP_PATH,
                                                                    queryParams,
                                                                    StringResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }




    public void getDatasApostasConfirmadas(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_MES_ANO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    MesAnoDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    null,
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getDatasApostasHistorico(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_HISTORICO_MES_ANO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    MesAnoDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf("8"));

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.APOSTAS_FAVORITAS_LISTA_UNICA_PATH,
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void getApostaHistoricoComprovante(Long id, final RequestListener<DetalhesHistoricoPremioDTOResponse> listener) {
        String url = ServerMethods.APOSTAS_CONFIRMADAS_HISTORICO_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    DetalhesHistoricoPremioDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void getApostaGerarCodigoResgate(Long id, final RequestListener<CodigoResgateDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_GERAR_CODIGO_RESGATE_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    CodigoResgateDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getApostaDetalhePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_DETALHES_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    DetalhesPremioDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getApostaDetalheComprovante(Long id, final RequestListener<ComprovanteApostaDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFIRMADAS_COMPROVANTE_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    ComprovanteApostaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void getApostaComprovantePremio(Long id, final RequestListener<DetalhesPremioDTOResponse> listener) {

        String url = ServerMethods.APOSTAS_COMPROVANTE_PREMIO_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    DetalhesPremioDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getApostaConferir(Long id, final RequestListener<DTOEnumIntegerResponse> listener) {

        String url = ServerMethods.APOSTAS_CONFERIR_PATH.replace("{id}", id.toString());
        final Map<String, String> queryParams = new HashMap<>();
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    DTOEnumIntegerResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    //APOSTAS FAVORITAS
    public void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoDTO> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("id", idAposta);
        final GsonRequest request = silceConnection.buildDeleteRequest(ServerMethods.APOSTAS_FAVORITAS_PATH + idAposta,
                                                                       queryParams,
                                                                       RetornoPadraoDTO.class,
                                                                       listener.getSilceListener(),
                                                                       listener.getErrorListener(),
                                                                       getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void postApostasFavoritas(final ApostaFavoritaDTO apostaFavoritaDTO, final RequestListener<RetornoPadraoDTO> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.APOSTAS_FAVORITAS_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     apostaFavoritaDTO, RetornoPadraoDTO.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void postIncluirApostaFavoritaCarrinho(final Long idApostaFavorita,
                                                  final Integer valorTipoConcurso,
                                                  final Integer qtdTeimosinhas,
                                                  final RequestListener<CarrinhoDTOResponse> listener) {
        String url = ServerMethods.CARRINHOS_INCLUIR_APOSTA_FAVORITA_PATH.replace("{idApostaFavorita}", String.valueOf( idApostaFavorita ));
        url = url.replace("{valorTipoConcurso}", String.valueOf(valorTipoConcurso));
        url = url.replace("{qtdTeimosinhas}", String.valueOf(qtdTeimosinhas));

        final GsonRequest request = silceConnection.buildPostRequest(url,
                                                                     new LinkedHashMap<>(),
                                                                     "",
                                                                     CarrinhoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    //CARRINHO
    public void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                          final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_APOSTA_CARRINHO_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     aposta,
                                                                     AgrupadorDTOApostaDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void limparCarrinho(RequestListener<CarrinhoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.LIMPAR_CARRINHO_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     null,
                                                                     CarrinhoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void validarCarrinho(final RequestListener<ResourceResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.VALIDAR_CARRINHO,
                                                                    new LinkedHashMap<>(),
                                                                    ResourceResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void adicionarApostasNoCarrinho(final List<IdentificaoDeUmaApostaDas8Modalidades> apostas,
                                           final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_APOSTAS_CARRINHO_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     apostas, AgrupadorDTOApostaDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final RequestListener<AgrupadorDTOApostaDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.INCLUIR_SURPRESINHA_CARRINHO_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     aposta,
                                                                     AgrupadorDTOApostaDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.BUSCA_CARRINHO_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    CarrinhoDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void  deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildDeleteRequest(ServerMethods.DELETE_APOSTA_CARRINHO_PATH + id,
                                                                       new LinkedHashMap<>(),
                                                                       CarrinhoDTOResponse.class,
                                                                       listener.getSilceListener(),
                                                                       listener.getErrorListener(),
                                                                       getHeader());
        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void postRegistrarApostaCarrinho(final MeioPagamento meioPagamento, final RequestListener<RetornoPadraoDTO> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.CARRINHOS_REGISTRAR_APOSTAS_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     meioPagamento, RetornoPadraoDTO.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }
    //RAPIDAO
    public void getRapidoes(final RequestListener<RapidaoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.RAPIDOES_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    RapidaoDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void postRapidoes(final RapidaoDTO rapidao,
                             final RequestListener<RapidaoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.RAPIDOES_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     rapidao, RapidaoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }


    public void gerarApostas(final int quantidade, final RequestListener<CarrinhoDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();
        if (quantidade > 0) {
            queryParams.put("quantidade", String.valueOf(quantidade));
        }

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.GERAR_APOSTAS_RAPIDAO_PATH,
                                                                     queryParams,
                                                                     "",
                                                                     CarrinhoDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    //COMPRAS
    public void getCompras(final int mes, final int ano, final int offset, final int size,  final RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse> listener) {
        LinkedHashMap<String, String> queryParams = new LinkedHashMap<>();

        queryParams.put("mes", String.valueOf(mes));
        queryParams.put("ano", String.valueOf(ano));
        queryParams.put("offset", String.valueOf(offset));
        queryParams.put("size", String.valueOf(size));

        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.COMPRAS_PATH,
                                                                    queryParams,
                                                                    ResultadoPesquisaPaginadaDTOCompraDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getDetalhesCompras(final String idSelecionado, final RequestListener<AgrupadorApostaCompraDTOResponse> listener) {
        String url = ServerMethods.DETALHES_COMPRAS_PATH.replace("{id}", idSelecionado);
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    new LinkedHashMap<>(),
                                                                    AgrupadorApostaCompraDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getMesesCompras(final RequestListener<MesAnoDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.COMPRAS_MESES_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    MesAnoDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    //RESULTADOS
    public void getResultadoModalidade(final String modalidade, final String concurso, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        Map<String, String> queryParams = new HashMap<>();
        String url = ServerMethods.RESULTADOS_MODALIDADE_CONCURSO_PATH.replace("{modalidade}", modalidade);
        url = url.replace("{concurso}",concurso);

        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    queryParams,
                                                                    ResultadoConcursoDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void getResultadoModalidade(final String modalidade, final RequestListener<ResultadoConcursoDTOResponse> listener) {
        getResultadoModalidade( modalidade, "", listener);
    }


    public void getModalidades(final RequestListener<ModalidadeDTOResponse> listener) {
        final GsonRequest request = silceConnection.buildGetRequest(ServerMethods.MODALIDADES_PATH,
                                                                    new LinkedHashMap<>(),
                                                                    ModalidadeDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    public void postConferirBilhetes(final String codigoBilhete,
                                     final RequestListener<ResultadoConsultaBilheteDTOResponse> listener) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("codigo", codigoBilhete);
        params.put("grecaptchaResponse", "");

        final GsonRequest request = silceConnection.buildPostRequest(ServerMethods.BILHETES_CONFERIR_BILHETE_PATH,
                                                                     new LinkedHashMap<>(),
                                                                     params,
                                                                     ResultadoConsultaBilheteDTOResponse.class,
                                                                     listener.getSilceListener(),
                                                                     listener.getErrorListener(),
                                                                     headers);

        request.setErrorListener(interceptError(request, headers, listener));
        silceConnection.request(request);
    }

    public void obterEscudoTime(String id, final RequestListener<EscudoEquipeEsportivaDTOResponse> listener) {
        String url = ServerMethods.EQUIPES_ESPORTIVAS_ESCUDO_PATH.replace("{id}", id);
        final GsonRequest request = silceConnection.buildGetRequest(url,
                                                                    new LinkedHashMap<>(),
                                                                    EscudoEquipeEsportivaDTOResponse.class,
                                                                    listener.getSilceListener(),
                                                                    listener.getErrorListener(),
                                                                    getHeader());

        request.setErrorListener(interceptError(request, getHeader(), listener));
        silceConnection.request(request);
    }

    private static String join(List<Integer> collection, String delimiter) {
        StringBuilder result = new StringBuilder("");

        for (Integer e : collection) {
            if (!(e.equals( collection.get(0)))) {
                result.append(delimiter);
            }
            result.append(e);
        }

        return result.toString();
    }

}