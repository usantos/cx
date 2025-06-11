package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bean.MeioPagamento;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResourceResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;

public interface ApostaService {
    void getEscudosEquipes(final RequestListener<EscudoEquipeEsportivaDTOResponse> listener);
    void getModalidades(final RequestListener<ModalidadeDTOResponse> listener);
    void getApostasFavoritas(final int offset, final RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse> listener);
    void deleteApostasFavoritas(final String idAposta, final RequestListener<RetornoPadraoResponse> listener);
    void buscaCarrinhosFavoritos(RequestListener<ListCarrinhoFavoritoResponse> listener);
    void validarCarrinhoFavorito(String nomeCarrinho, RequestListener<CarrinhoFavoritoDTOResponse> listener);
    void deletaCarrinhoFavorito(final String idCarrinho,final RequestListener<ListCarrinhoFavoritoResponse> listener);
    void buscaApostasCarrinhoFavorito(Long idCarrinho,RequestListener<ListApostaCarrinhoFavoritoResponse> listener);
    void deletaApostaCarrinhoFavorito(final String idCarrinho,final String idAposta,
                                      final RequestListener<ListApostaCarrinhoFavoritoResponse> listener);
    void salvarCarrinhoFavorito(final String nomeCarrinho,final String idCompra,final Boolean mantemSurpresinhas,
                                final RequestListener<CarrinhoFavoritoDTOResponse> listener);
    void adicionarApostaNoCarrinho(final IdentificaoDeUmaApostaDas8Modalidades aposta,
                                   final RequestListener<CarrinhoDTOResponse> listener);

    void adicionarSurpresinhaNoCarrinho(final IncluirSurpresinhaDTO aposta,
                                               final RequestListener<CarrinhoDTOResponse> listener);

    void limparCarrinho(RequestListener<CarrinhoDTOResponse> listener);

    void postRegistrarApostaCarrinhoAsync(final MeioPagamento meioPagamento, final RequestListener<CompraAsyncResponse> listener);

    void validarCarrinho(final RequestListener<ResourceResponse> listener);

    void verificaCompraProcessamento(final Long idCompraCarrinho, final RequestListener<CompraAsyncResponse> listener);

    void deleteApostaCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener);

    void deleteComboCarrinho(String id, final RequestListener<CarrinhoDTOResponse> listener);

    void postIncluirApostaFavoritaCarrinho(final Long idApostaFavorita, final Integer valorTipoConcurso,
                                           final Integer qtdTeimosinhas, final RequestListener<CarrinhoDTOResponse> listener);

    void transformarCarrinho(Long idCarrinhoFavorito, Integer opcaoSelecionada, RequestListener<CarrinhoDTOResponse> listener);

    void buscaCarrinho(final RequestListener<CarrinhoDTOResponse> listener);

}
