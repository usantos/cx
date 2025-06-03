package br.gov.caixa.loterias.apostas.model.bo;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EscudoEquipeEsportivaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTOResponse;
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
}
