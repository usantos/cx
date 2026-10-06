package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.repository.ApostaFavoritaRepository;
import br.gov.caixa.loterias.apostas.model.repository.CarrinhoRepository;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.PickerApostaParamsManager;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolheApostaParamsListener;

public class FavoritaModel extends AppModel {
	//region Variables
	private ApostaFavoritaRepository repository;
	private CarrinhoRepository carrinhoRepository;
	private int offsetItem;
	private Boolean isMaisCompras = true;
	private Boolean bloquearRequestScroll = false;
	private List<ApostaFavoritaDTO> apostaList;
	//endregion

	//region Constructors
	public FavoritaModel(Activity activity) {
		super(activity);
		repository = new ApostaFavoritaRepository(activity);
		carrinhoRepository = new CarrinhoRepository(activity);
		offsetItem = 0;
		apostaList = new ArrayList<>();
	}
	//endregion

	//region Methods
	public void buscaApostas(OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO> listener){
		repository.buscaApostas(this.offsetItem, listener);
	}

	public void buscaApostasModalidade(int modalidade, OnSilceListener<ResultadoPesquisaPaginadaApostaFavoritaDTO> listener){
		repository.buscaApostasModalidade(modalidade, this.offsetItem, listener);
	}

	public void buscaApostasAgrupadas(OnSilceListener<AgrupadorDTOApostaFavoritaDTO> listener){
		repository.buscaApostasAgrupadas(listener);
	}

	public void setOffsetItem(int offsetItem){
		this.offsetItem = offsetItem;
	}

	public int getOffsetItem(){
		return this.offsetItem;
	}

	public Boolean getMaisCompras() {
		return isMaisCompras;
	}

	public void setMaisCompras(Boolean maisCompras) {
		isMaisCompras = maisCompras;
	}

	public Boolean getBloquearRequestScroll() {
		return bloquearRequestScroll;
	}

	public void setBloquearRequestScroll(Boolean bloquearRequestScroll) {
		this.bloquearRequestScroll = bloquearRequestScroll;
	}

	public List<ApostaFavoritaDTO> getApostaList() {
		return apostaList;
	}

	public void setApostaList(List<ApostaFavoritaDTO> apostaList) {
		this.apostaList = apostaList;
	}

//	public boolean temSwipe(int position) {
//		return apostaList.size() > 0 && position < apostaList.size();
//	}

	public void addApostaCarrinho(ApostaFavoritaDTO aposta, OnSilceListener<CarrinhoDTO> listener) {
		carrinhoRepository.buscaCarrinho(new OnSilceListener<CarrinhoDTO>() {
			@Override
			public void success(CarrinhoDTO payload) {
				AlertDialogUtils.dismiss();
				checarLimiteDiario(aposta, payload, listener);
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		});
	}
	//endregion

	//region Privates
	private void checarLimiteDiario(ApostaFavoritaDTO apostaFavoritaDTO, final CarrinhoDTO carrinhoDTO, OnSilceListener<CarrinhoDTO> listener) {

		BigDecimal valorApostaFavorita = IdentificaoDeUmaApostaDas8Modalidades.getValorApostaFavoritaPorProagnostico(apostaFavoritaDTO);

		if(valorApostaFavorita.compareTo(BigDecimal.valueOf(-1)) == 0){
			DialogUtils.dialogEntendiListener(getActivity(),
					getActivity().getResources().getString(R.string.nao_concluiu_aposta),
					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							Intent intent = IntentUtil.getIntentLimpandoPilhaActivities(getActivity(), PrincipalActivity.class);
							getActivity().startActivity(intent);
							getActivity().finish();
						}
					});
		}else{
			if (isLimiteDiarioExcedido(carrinhoDTO, apostaFavoritaDTO)) {
				DialogUtils.dialogSim(getActivity(),
						getActivity().getResources().getString(R.string.MA014),

						new OnDialogBotaoListener() {
							@Override
							public void onButtonClick(DialogInterface dialog, int which) {
								checarApostasRepetidas(apostaFavoritaDTO, carrinhoDTO, listener);
							}
						}
				);
			} else {
				checarApostasRepetidas(apostaFavoritaDTO, carrinhoDTO, listener);
			}
		}

	}
	private boolean isLimiteDiarioExcedido(CarrinhoDTO carrinhoDTO, ApostaFavoritaDTO apostaFavoritaDTO) {
		return SessaoUsuario.getInstance().getParametrosSimulacao() != null &&
				SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() != null &&
				carrinhoDTO != null && carrinhoDTO.getValorTotal() != null &&
				SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario()
						.compareTo(carrinhoDTO.getValorTotal()
								.add(IdentificaoDeUmaApostaDas8Modalidades.getValorApostaFavoritaPorProagnostico(apostaFavoritaDTO))) < 0;
	}

	private void checarApostasRepetidas(final ApostaFavoritaDTO apostaFavoritaDTO, final CarrinhoDTO carrinhoDTO, OnSilceListener<CarrinhoDTO> listener) {
		if (IdentificaoDeUmaApostaDas8Modalidades.checkExisteUmaApostaIgualCarrinho(apostaFavoritaDTO, carrinhoDTO)) {
			DialogUtils.dialogSim(getActivity(),
					getActivity().getResources().getString(R.string.MA015),

					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							AlertDialogUtils.show(getActivity());
							adicionarCarrinho(apostaFavoritaDTO.getId(), apostaFavoritaDTO.getModalidade(), listener);
						}
					}
			);

		} else {
			adicionarCarrinho(apostaFavoritaDTO.getId(), apostaFavoritaDTO.getModalidade(), listener);
		}
	}

	private void adicionarCarrinho(Long idApostaFavorita, DTOEnumInteger modalidadeEnum, OnSilceListener<CarrinhoDTO> listener) {
		SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
		List<ParametroSimulacao> arrayConcursos = new ArrayList<>();
		List<ParametroSimulacao> parametroSimulacaoList = sessaoUsuario.getParametrosSimulacao().getParametros();
		for (ParametroSimulacao concurso : parametroSimulacaoList) {
			if (concurso.getParametroJogo().getConcurso().getModalidadeDetalhada().getDescricao().equals(modalidadeEnum.getDescricao())) {
				arrayConcursos.add(concurso);
			}
		}

		if (arrayConcursos.size() > 1) {
			AlertDialogUtils.dismiss();
			Boolean temEspecialENormal = false;
			for (ParametroSimulacao modalidade : arrayConcursos) {
				if (modalidade.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL) {
					temEspecialENormal = true;
					if (modalidade.getParametroJogo().getTeimosinhas() != null && modalidade.getParametroJogo().getTeimosinhas().size() > 0) {
						popupAdicionarCarrinhoFavoritas(arrayConcursos, idApostaFavorita, temApostaEspelho(modalidade.getParametroJogo().getConcurso().getModalidade()));
						return;
					}
				} else {
					if (temEspecialENormal) {
						popupAdicionarCarrinhoFavoritas(arrayConcursos, idApostaFavorita, temApostaEspelho(modalidade.getParametroJogo().getConcurso().getModalidade()));
						return;
					}
				}
			}
		} else {
			if (arrayConcursos.isEmpty()){
				DialogUtils.dialogEntendi(getActivity(),"Modalidade não disponível no momento.");
				AlertDialogUtils.dismiss();
			} else {
				if (arrayConcursos.get(0).getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL) {
					adicionarCarrinhoCallWebservice(idApostaFavorita, 2, 0, null,listener);
				} else {
					AlertDialogUtils.dismiss();
					if (arrayConcursos.get(0).getParametroJogo().getTeimosinhas().size() > 0) {
						popupAdicionarCarrinhoFavoritas(arrayConcursos, idApostaFavorita, temApostaEspelho(arrayConcursos.get(0).getParametroJogo().getConcurso().getModalidade()));
						return;
					}
				}
			}
		}
	}

	private boolean temApostaEspelho(ModalidadeEnum modalidade) {
		return modalidade == ModalidadeEnum.LOTOMANIA;
	}

	private void popupAdicionarCarrinhoFavoritas(List<ParametroSimulacao> arrayConcursos, Long idApostaFavorita, boolean temEspelho) {
		new PickerApostaParamsManager(getActivity(), arrayConcursos, temEspelho, apostaParamsListener(idApostaFavorita)).iniciar();
	}

	private OnEscolheApostaParamsListener apostaParamsListener(Long idApostaFavorita) {
		return (Integer tipoConcurso, int qtdTeimosinhas, Boolean espelho) -> {
			Activity activity = getActivity();
			final AlertDialog loadViewProgress = LoadingViewLoterias.show(activity);

			ServicoFactoryUtil.getApostaService().postIncluirApostaFavoritaCarrinho(idApostaFavorita, tipoConcurso, qtdTeimosinhas, espelho, new RequestListener<CarrinhoDTOResponse>() {
				@Override
				public void onResponse(CarrinhoDTOResponse response) {
					loadViewProgress.dismiss();
					if (response.getRedirect() != null){
						RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
					}

					DialogUtils.dialogTituloEntendi(activity,
							activity.getResources().getString(R.string.apostas_favoritas),
							activity.getResources().getString(R.string.label_new_favorita_no_carrinho));

					CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
				}

				@Override
				public void onErrorResponse(VolleyError error) {
					loadViewProgress.dismiss();
					RedirectNetwork.checkRedirect(error, activity);
				}
			});
		};
	}


	private void adicionarCarrinhoCallWebservice(final Long idApostaFavorita, final int valorTipoConcurso,
												 final int qtdTeimosinhas, Boolean espelho, OnSilceListener<CarrinhoDTO> listener) {
		repository.incluirApostaFavorita(idApostaFavorita, valorTipoConcurso, qtdTeimosinhas, espelho, listener);

	}

//	public void alteraAposta(ApostaFavoritaDTO aposta, FavoritaHolder holder, OnSilceListener listener) {
//		aposta.setNome(holder.getNomeAlterado());
//		aposta.setOpenSwipeLayout(null);
//		repository.alteraAposta(aposta, listener);
//	}

	public void deletaAposta(ApostaFavoritaDTO aposta, OnSilceListener listener) {
		repository.deletaAposta(aposta.getId(), listener);
	}

	public boolean isListaVazia() {
		return apostaList.size() == 0;
	}

	public void removeAposta(int position) {
		apostaList.remove(position);
	}

	//endregion
}
