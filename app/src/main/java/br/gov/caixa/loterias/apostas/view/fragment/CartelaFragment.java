package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoNovoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.data.CompletaJogoRsp;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SuperSeteAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.TimeCoracaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotogolAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;

public class CartelaFragment extends Fragment implements OnClickListener, LocateAdapterListener, LotogolListener {
	private SensorManager mSensorManager;
	private boolean isNumerosAleatoriosShake = false;
	private BigDecimal valorEmReais = BigDecimal.ZERO;
	private LotecaAdapter lotecaAdapter;

	private final SensorEventListener mSensorListener = new LoteriasSensorEventListener(0.00f,
																						SensorManager.GRAVITY_EARTH,
																						SensorManager.GRAVITY_EARTH).getSensor();

	private List<Dezena> dezenas = new ArrayList<>();
	private List<Integer> dezenasSelecionadas = new ArrayList<>();
	private List<Integer> dezenasApostaEspelho = new ArrayList<>();
	private List<List<Integer>> matrizSelecionada;
	private List<ParametroEquipe> listaEquipefiltro;
	private HashSet<ParametroPartida> partidasSelecionadas;

	private CartelaFragmentListener cartelaFragmentListener;

	private ParametroEquipe equipeSelecionada = null;
	private ParametroJogoDTO parametroJogo;
	private ModalidadeEnum tipoJogo;
	private TimeCoracaoAdapter timeCoracaoAdapter;

	private SimularApostaActivity parentActivity;
	private AppCompatCheckBox selcioneOutrosNumeros;
	private View view;
	private EditText informeNomeTime;
	private RelativeLayout opcaoOutrosNumerosLayout, rlColunasSuperSete;
	private LinearLayout selecioneTimeCoracaoLayout;
	private ExpandableHeightGridView mGridTimeCoracao, listaDezenas;
	private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
	private ArrayList<SuperSeteAdapter> listaAdaptersSuperSete;
	private ExpandableHeightRecyclerView recyclerViewPartida;
	private ScrollView svSuperSete;
	private int qtdTotalSelecionadosSuperSete = 0;

	private boolean isEcolhaTimeBtnAtivado;
	private boolean isTelaSelecaoTimeAtivado;
	private boolean isEscolhaTimeCoracaoSurpresinha;

	public BigDecimal valorTotalAposta;
	public int typeGameColorLight, typeGameColorDark, numeroConcursoAtual;
	public Button botaoFinalizar, botaoLimparAposta;
	public TextView valorAposta;
	public SimularDezenasAdapter simularDezenasAdapter;
	public String dataSorteioAtual;

	public CartelaFragment() {
		// Required empty public constructor
	}

	public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
		BigDecimal valorEmReais = BigDecimal.ZERO;
		if (parametroJogo == null) {
			SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
			parametroJogo = ViewUtils.getParametroSimulacao(sessaoUsuario, tipoJogo).getParametroJogo();
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
			ParametroValorApostaDTO valorAposta = parametroJogo.getValorApostaBy(numPrognostico, parametroJogo.getTrevos().getQtdMinima());
			parentActivity.atualizaValorAposta(valorAposta);

			return;
		}
		if (numPrognostico != 0) {
			for (ParametroValorApostaDTO parametro : parametroJogo.getValoresAposta()) {
				if (parametro.getNumeroPrognosticos() == numPrognostico) {
					valorEmReais = parametro.getValor();
					break;
				}
			}
		} else if (parametroJogo.getValorApostaMinima() != null) {
			valorEmReais = parametroJogo.getValorApostaMinima();
		} else if (parametroJogo.getConcurso().getValorApostaMinima() != null) {
			valorEmReais = parametroJogo.getConcurso().getValorApostaMinima();
		}

		valorEmReais = valorEmReais.equals(BigDecimal.ZERO) ? parametroJogo.getValoresAposta().get(0).getValor() : valorEmReais;

		if (qtdTeimosinhas != 0) {
			valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdTeimosinhas).movePointLeft(0));
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && selcioneOutrosNumeros.isChecked()) {
			valorEmReais = valorEmReais.multiply(Constantes.DOIS_BIG_DECIMAL);
		}

		ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
		valorTotalAposta = valorEmReais;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

		parentActivity = ((SimularApostaActivity) getActivity());
		if (parentActivity != null) {
			parametroJogo = parentActivity.getParametroSimulacao();
			identificarTipoJogo();
		}

		if (parametroJogo != null && dezenas.size() == 0 && parametroJogo.getPrognosticoMaximo() != null && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			for (int i = 1; i <= parametroJogo.getPrognosticoMaximo(); i++) {
				dezenas.add(new Dezena("" + i, Boolean.FALSE));
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int idList;
		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) || parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
			idList = R.id.recyclerViewPartida;
		} else {
			idList = R.id.listaDezenas;
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
			RelativeLayout ttvLayout           = view.findViewById(R.id.ttvLayout);
			TextView       ttvEscolha          = view.findViewById(R.id.ttvEscolha);
			TextView       ttvPagina           = view.findViewById(R.id.ttvPagina);
			TextView       ttvPaginaQuantidade = view.findViewById(R.id.ttvPaginaQuantidade);

			ttvEscolha.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.dia_sorte_escolha_dia)));
			ttvEscolha.setTextColor(getResources().getColor(R.color.timemaniaclaro));
			ttvPagina.setTextColor(getResources().getColor(R.color.timemaniaescuro));
			ttvPagina.setTypeface(null, Typeface.BOLD);
			ttvPaginaQuantidade.setTextColor(getResources().getColor(R.color.timemaniaclaro));
			ttvPaginaQuantidade.setText(getString(R.string.paginacao_quantidade, getString(R.string.label_numerico_dois)));
			ttvLayout.setVisibility(View.VISIBLE);
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
			opcaoOutrosNumerosLayout = view.findViewById(R.id.opcaoOutrosNumerosLayout);
			selcioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.selcioneOutrosNumeros);
			TextView infoSelecioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.infoSelecioneOutrosNumeros);
			infoSelecioneOutrosNumeros.setText(ViewUtils.fromHtml(Constantes.INFO_OUTROS_50_NUMEROS));
			onClickOutrosNumerosLayout();
			if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
				setVisibilityCheckedNumerosLayout(View.VISIBLE);
			} else {
				setVisibilityCheckedNumerosLayout(View.GONE);
			}
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			rlColunasSuperSete = view.findViewById(R.id.rl_colunas_super_7);
			svSuperSete = view.findViewById(R.id.sv_super_sete);
			rlColunasSuperSete.setVisibility(View.VISIBLE);
			svSuperSete.setVisibility(View.VISIBLE);
		}

		configuraBotoes(view);
		// Chama a tela de escolha de time
		if (isEscolhaTimeCoracaoSurpresinha) {
			mostrarCartelaEscolhaTimeCoracao();
		} else {
			isTelaSelecaoTimeAtivado = false;
			limparTimeSelecionado();
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA) && !isTelaSelecaoTimeAtivado) {
			// Tela de escolha de números
			TextView ttvEscolha = view.findViewById(R.id.ttvEscolha);
			TextView ttvPagina  = view.findViewById(R.id.ttvPagina);
			ttvEscolha.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.dia_sorte_escolha_dia)));
			ttvPagina.setText(R.string.label_numerico_um);
		}
		if (!isEscolhaTimeCoracaoSurpresinha) {
			configuraListaDezenas(view);
			verificaSeMostraBotaoLimparAposta();
			verificaSeMostraSalvarApostaView();
		}
		editaAposta();

		ViewUtils.organizaTabLayout(getActivity(), R.id.cartelaFragmentMainLayout, idList, R.id.cartelaFragmentScrollView);
		((ScrollView) view.findViewById(R.id.cartelaFragmentScrollView)).fullScroll(ScrollView.FOCUS_UP);
	}

	public void cartelaFragmentBuilder(Bundle dataBundle) {
		if (dataBundle != null) {
			numeroConcursoAtual = dataBundle.getInt("numeroConcursoAtual");
			dataSorteioAtual = dataBundle.getString("dataSorteioAtual");


			typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
			typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_cartela, container, false);
		if ((parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE) && parentActivity.getMesDeSorteCartela() == null) || parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {
			RelativeLayout ttvLayout           = view.findViewById(R.id.ttvLayout);
			TextView       ttvEscolha          = view.findViewById(R.id.ttvEscolha);
			TextView       ttvPagina           = view.findViewById(R.id.ttvPagina);
			TextView       ttvPaginaQuantidade = view.findViewById(R.id.ttvPaginaQuantidade);

			if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
				ttvEscolha.setText(getString(R.string.escolha_de_seis_a_doze_numeros));
			}else {
				ttvEscolha.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.dia_sorte_escolha_dia)));
			}
			ttvPaginaQuantidade.setText(getString(R.string.paginacao_quantidade, getString(R.string.label_numerico_dois)));
			ttvLayout.setVisibility(View.VISIBLE);

			if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)){
				ttvEscolha.setTextColor(getResources().getColor(R.color.diasortemedio));
				ttvPagina.setTextColor(getResources().getColor(R.color.diasortemedio));
				ttvPaginaQuantidade.setTextColor(getResources().getColor(R.color.diasorteclaro));
			}else if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
				ttvEscolha.setTextColor(getResources().getColor(R.color.mais_milionaria_media));
				ttvPagina.setTextColor(getResources().getColor(R.color.mais_milionaria_media));
				ttvPaginaQuantidade.setTextColor(getResources().getColor(R.color.mais_milionaria_clara));
			}
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			cartelaFragmentListener = (CartelaFragmentListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement CartelaFragmentListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		cartelaFragmentListener = null;
	}

	private void identificarTipoJogo() {
		if (parentActivity.getIntent() != null) {
			tipoJogo = (ModalidadeEnum) parentActivity.getIntent().getSerializableExtra("tipoAposta");
		}
	}

	private void preencherVisibilidaeGridView(int visibilidadeEscolhaTimeCoracao, int visibilidadeSelecioneTimeCoracao, int visibilidadeEscolhaLoteca,
											  int visibilidadeDezanas) {
		if (mGridTimeCoracao != null) {
			mGridTimeCoracao.setVisibility(visibilidadeEscolhaTimeCoracao);
		}

		if (selecioneTimeCoracaoLayout != null) {
			selecioneTimeCoracaoLayout.setVisibility(visibilidadeSelecioneTimeCoracao);
		}

		if (recyclerViewPartida != null) {
			recyclerViewPartida.setVisibility(visibilidadeEscolhaLoteca);

		}

		if (listaDezenas != null) {
			listaDezenas.setVisibility(visibilidadeDezanas);
		}

	}

	private void adicionarApostaNoCarrinho() {

		if (isEscolhaTimeCoracaoSurpresinha) {
			cartelaFragmentListener.adicionarTimeSurpresa(equipeSelecionada);
			return;
		}

		dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);

		if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> aposta = ApostaUtils
					.getApostaSuperSete(parametroJogo, dezenasSelecionadas,
										valorTotalAposta,parentActivity.qtdConcursoSelecionado,
										ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
			AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(parentActivity, aposta);
		} else {
			IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta = getApostaPreenchida();
			AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(parentActivity, aposta);
		}
	}

	private IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida() {

		if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)){
			return ApostaUtils.getApostaDiaDeSorte(parametroJogo, dezenasSelecionadas,
												   valorTotalAposta,
												   parentActivity.qtdConcursoSelecionado, parentActivity.getMesDeSorteCartela());
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)){
			return ApostaUtils.getApostaLoteca(parametroJogo, dezenasSelecionadas,
											   valorTotalAposta,parentActivity.qtdConcursoSelecionado,
											   partidasSelecionadas);
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)){
			return ApostaUtils.getApostaLotogol(parametroJogo, dezenasSelecionadas, valorTotalAposta,
												parentActivity.qtdConcursoSelecionado, partidasSelecionadas);
		} else if(parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)){
			return ApostaUtils.getApostaTimemania(parametroJogo, dezenasSelecionadas,
												  valorTotalAposta,parentActivity.qtdConcursoSelecionado,
												  equipeSelecionada);
		} else {
			return ApostaUtils.getApostaPreenchida(parametroJogo, dezenasSelecionadas,
												   valorTotalAposta, parentActivity.qtdConcursoSelecionado);
		}
	}

	// METODO DO PROTOCOLO ONCLICK
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.botaoAdicionarCompletarCartela: {
				if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
					if (isEcolhaTimeBtnAtivado && dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
						if (mGridTimeCoracao == null) {
							mostrarCartelaEscolhaTimeCoracao();
							timemaniaTimeSelecionadoEdicao();
						} else {
							if (equipeSelecionada != null) {
								if (equipeSelecionada.isSelecionado()) {
									adicionarApostaNoCarrinho();
								} else {
									selecionarTimeAleatorio();
								}
							} else {
								selecionarTimeAleatorio();
							}
						}
					} else {
						adcionarApostaPreencherNumerosAleatorios();
					}
				} else {
					adcionarApostaPreencherNumerosAleatorios();
				}
			}
			break;

			case R.id.botaoLimparAposta: { // LIMPAR APOSTA
				if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) && partidasSelecionadas != null) {
					limparPartidadasSelecionasLoteca(Boolean.FALSE);
				} else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA) && isTelaSelecaoTimeAtivado) {
					limparTimeSelecionado();
				} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL) && partidasSelecionadas != null) {
					limparPartidasSelecionadasLotogol(Boolean.FALSE);
				} else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
					limpaNumerosSuperSete();
				} else {
					dezenasSelecionadas.clear();
					simularDezenasAdapter.notifyDataSetChanged();
					voltaSalvarApostaParaLayoutInicial();
				}
				limparOpcaoOutrosNumerosLayout();

				verificaSeMostraBotaoLimparAposta();
				atualizaStatusBotaoFinalizar(false);
			}
			break;

			case R.id.salvarApostaLayout: {
				RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

				ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

				final float scale = parentActivity.getResources().getDisplayMetrics().density;
				if (relativelayoutParams.height != ((int) (140 * scale + 0.5f))){
					relativelayoutParams.height = (int) (140 * scale + 0.5f);
				} else  {
					relativelayoutParams.height = (int) (64 * scale + 0.5f);
				}

				salvarApostaLayout.setLayoutParams(relativelayoutParams);

				salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
				if (!DadosUsuarioBO.checarUsuarioLogado(getActivity())) {
					RedirectNetwork.connectKeycloak(getActivity());
				}
			}
			break;

			case R.id.btnSalvarAposta: {
				EditText nomeAposta = parentActivity.getEditNomeAposta();

				if (!TextUtils.isEmpty(nomeAposta.getText().toString())) {
					ApostaFavoritaDTO aposta = new ApostaFavoritaDTO();
					if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA) && equipeSelecionada != null) {
						aposta.setTimeDoCoracao(equipeSelecionada);
					}

					if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
						parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
					} else {
						parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), dezenasSelecionadas);

					}
				} else {
					ViewUtils.alert(getActivity(), getString(R.string.msg_favor_inserir_nome_aposta));
				}
			}

		}
	}

	private void selecionarTimeAleatorio() {
		int    randomInt       = 0;
		Random randomGenerator = Utils.getRandom();
		randomInt = randomGenerator.nextInt(listaEquipefiltro.size() - 1);
		equipeSelecionada = listaEquipefiltro.get(randomInt);
		equipeSelecionada.setSelecionado(true);
		String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
		informeNomeTime.setText(nomeTime);
		timeCoracaoAdapter.notifyDataSetChanged();
		verificaSeMostraBotaoLimparAposta();
		verificaSeMostraSalvarApostaView();
		atualizarAdicionarCarinhoCompletarCartela(false);
	}

	private void limparOpcaoOutrosNumerosLayout() {
		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && opcaoOutrosNumerosLayout.getVisibility() == View.VISIBLE) {
			setVisibilityCheckedNumerosLayout(View.GONE);
			atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
		}
	}

	private void setVisibilityCheckedNumerosLayout(int visibility) {
		opcaoOutrosNumerosLayout.setVisibility(visibility);
		selcioneOutrosNumeros.setChecked(Boolean.FALSE);
	}


	private void limparTimeSelecionado() {
		if (equipeSelecionada != null) {
			equipeSelecionada.setSelecionado(false);
			informeNomeTime.setText(StringUtils.EMPTY);
		}

		if (timeCoracaoAdapter != null) {
			timeCoracaoAdapter.notifyDataSetChanged();
		}

		voltaSalvarApostaParaLayoutInicial();
	}

	private void adcionarApostaPreencherNumerosAleatorios() {
		if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
			adicionarApostaPreencherNumerosTimemania();
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
			adicionarApostaPreencherNumerosLotomania();
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
			int duplo  = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
			int triplo = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

			if (partidasSelecionadas.size() == parametroJogo.getPartidas().size() && (duplo > 0 || triplo > 0)) {
				adicionarApostaNoCarrinho();
			} else {
				//TODO implementar regra de completar placar
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
			if (partidasSelecionadas.size() == parametroJogo.getPartidas().size()) {
				adicionarApostaNoCarrinho();
			} else {
				//TODO implementar regra de completar placar
			}
		} else if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) { //Adiciona aposta no carrinho
			if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE) && parentActivity.isFragmentCartela()) {
				parentActivity.mesDeSorteFragment(dezenasSelecionadas, false);
			} else if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA) && parentActivity.isFragmentCartela()){
				parentActivity.trevosFragment(dezenasSelecionadas);
			} else {
				adicionarApostaNoCarrinho();
			}
		} else {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
				if (qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
					adicionarApostaNoCarrinho();
				} else {
					preencheNumerosAleatoriosSuperSete(ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
				}
			} else {
				preencheNumerosAleatorios();
			}
		}
	}

	private void adicionarApostaPreencherNumerosLotomania() {
		if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
			//verifica se foi marcada a opção para gerar outros números
			if (selcioneOutrosNumeros.isChecked()) {
				// Gera uma nova aposta com os outros 50 números não selecionados

				IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida();

				aposta.setGerarEspelho(true);
				aposta.setValor(aposta.getValor().divide(Constantes.DOIS_BIG_DECIMAL));
				AdicionarApostaCarrinhoHelper.incluirApostaCarrinhoCartela(parentActivity, aposta);
			} else {
				adicionarApostaNoCarrinho();
			}
		} else {
			preencheNumerosAleatorios();
			setVisibilityCheckedNumerosLayout(View.VISIBLE);
			onClickOutrosNumerosLayout();
		}
	}

	private void onClickOutrosNumerosLayout() {
		opcaoOutrosNumerosLayout.setOnClickListener(view -> {
			if (selcioneOutrosNumeros.isChecked()) {
				selcioneOutrosNumeros.setChecked(false);
			} else {
				selcioneOutrosNumeros.setChecked(true);
			}
			atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
		});
	}

	private void adicionarApostaPreencherNumerosTimemania() {
		if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado && equipeSelecionada != null) {
			adicionarApostaNoCarrinho();
		} else if (isEscolhaTimeCoracaoSurpresinha) {
			parentActivity.adicionarTimeSurpresa(equipeSelecionada);
		} else {
			preencheNumerosAleatorios();
		}
	}

	private void preencheNumerosAleatorios() {
		AlertDialogUtils.show(getActivity());
		ApostaBO apostaBO = ApostaBO_.getInstance_(getActivity());
		dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);
		apostaBO.completaJogo(parentActivity.qtdDezenasPossiveisSelecionado, dezenasSelecionadas, 1,
							  parametroJogo.getPrognosticoMaximo(), new RequestListener<CompletaJogoRsp>() {
					@Override
					public void onResponse(CompletaJogoRsp response) {
						AlertDialogUtils.dismiss();
						isNumerosAleatoriosShake = false;
						AppCenterManager.registraEvento("ENTROU_COMPLETAR_COM_NUMEROS_ALEATORIOS");
						isEcolhaTimeBtnAtivado = true;
						dezenasSelecionadas.clear();
						dezenasSelecionadas.addAll(response.getPayload());
						simularDezenasAdapter.notifyDataSetChanged();
						atualizaBotoeslayout();
						if (response.getRedirect() != null) {
							RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						isNumerosAleatoriosShake = false;
						AlertDialogUtils.dismiss();
						RedirectNetwork.checkRedirect(error, getActivity());
					}
				});
	}

	private void preencheNumerosAleatoriosSuperSete(ArrayList<ArrayList<Integer>> listaInteiros) {
		AlertDialogUtils.show(getActivity());
		ApostaBO apostaBO = ApostaBO_.getInstance_(getActivity());
		dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);

		apostaBO.completaJogoNovo(parentActivity.qtdDezenasPossiveisSelecionado, listaInteiros,
								  parametroJogo.getConcurso().getModalidade().name(),
								  new RequestListener<CompletaJogoNovoRsp>() {
									  @Override
									  public void onResponse(CompletaJogoNovoRsp response) {
										  AlertDialogUtils.dismiss();
										  AppCenterManager.registraEvento( "ENTROU_COMPLETAR_COM_NUMEROS_ALEATORIOS");
										  isNumerosAleatoriosShake = false;
										  isEcolhaTimeBtnAtivado = true;
										  if (response.getRedirect() != null) {
											  RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
										  }
										  preencheSuperSete(response.getPayload());
										  atualizaBotoeslayout();
										  atualizarBotaoFinalizarAdicionarCarrinho();
									  }

									  @Override
									  public void onErrorResponse(VolleyError error) {
										  isNumerosAleatoriosShake = false;
										  AlertDialogUtils.dismiss();
										  RedirectNetwork.checkRedirect(error, getActivity());
									  }
								  });
	}

	private ArrayList<ArrayList<Integer>> atualizaSuperSetePrognosticoMaior(ArrayList<ArrayList<Integer>> listaInteiros) {
		if (parentActivity.qtdDezenasPossiveisSelecionado < getQtdTotalSelecionadosSuperSete(listaInteiros)) {
			qtdTotalSelecionadosSuperSete = 0;
			listaInteiros = new ArrayList<>();
			limpaNumerosSuperSete();
		}
		return listaInteiros;
	}

	private int getQtdTotalSelecionadosSuperSete(ArrayList<ArrayList<Integer>> listaInteiros) {
		int qtdTotal = 0;
		for (ArrayList<Integer> listaSelecionados : listaInteiros) {
			qtdTotal += listaSelecionados.size();
		}
		return qtdTotal;
	}

	private void preencheSuperSete(List<List<Integer>> numerosSelecionados) {
		int coluna = 0;
		qtdTotalSelecionadosSuperSete = 0;
		if (matrizSelecionada != null){
			matrizSelecionada = null;
		}
		for (SuperSeteAdapter adapter : listaAdaptersSuperSete) {
			adapter.dezenasSelecionadas = numerosSelecionados.get(coluna);
			qtdTotalSelecionadosSuperSete += adapter.dezenasSelecionadas.size();
			adapter.notifyDataSetChanged();
			coluna++;
		}
	}

	private boolean validaSelecaoSuperSete() {
		//lista de cima igual aos selecionados e no minimo 1 por coluna
		Boolean                       mostraSalvar          = false;
		int                           contColunasComSelecao = 0;
		ArrayList<ArrayList<Integer>> listaSuperSete        = ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete);
		for (ArrayList<Integer> listaSelecionados : listaSuperSete) {
			if (listaSelecionados.size() > 0) {
				contColunasComSelecao++;
			}
		}
		if (contColunasComSelecao == 7) {
			mostraSalvar = true;
		}
		return mostraSalvar;
	}

	private void atualizaBotoeslayout() {
		atualizaStatusBotaoFinalizar(false);
		verificaSeMostraBotaoLimparAposta();
		verificaSeMostraSalvarApostaView();
	}

	private void mostrarCartelaEscolhaTimeCoracao() {
		// Estando a aposta completa apresenta a tela de escolha do time
		// Seta a paginação
		TextView ttvEscolha = view.findViewById(R.id.ttvEscolha);
		TextView ttvPagina  = view.findViewById(R.id.ttvPagina);
		ttvEscolha.setText(R.string.label_escolha_time_seu_coracao);
		ttvPagina.setText(R.string.label_numerico_dois);

		isEcolhaTimeBtnAtivado = true;
		isTelaSelecaoTimeAtivado = true;
		selecioneTimeCoracaoLayout = view.findViewById(R.id.selecioneTimeCoracao);
		mGridTimeCoracao = view.findViewById(R.id.gridTimes);
		preencherVisibilidaeGridView(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
		informeNomeTime = selecioneTimeCoracaoLayout.findViewById(R.id.informe_nome_time);
		onChangeInformeNomeTime();
		configurarSelecaoTimeCoracao();
		atualizaStatusBotaoFinalizar(false);
		verificaSeMostraBotaoLimparAposta();
		verificaSeMostraSalvarApostaView();

	}

	private void onChangeInformeNomeTime() {
		informeNomeTime.addTextChangedListener(new TextWatcher() {
			private boolean isChanged = false;

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				if (isChanged) {
					return;
				}

				boolean contemIfem = charSequence.toString().contains("-");
				isChanged = true;

				List<ParametroEquipe> parametroEquipeList = parametroJogo.getEquipes();
				listaEquipefiltro.clear();
				if (StringUtils.isNotEmpty(charSequence.toString()) && !contemIfem) {
					for (ParametroEquipe parametroEquipe : parametroEquipeList) {
						if (parametroEquipe.getNome().toUpperCase().contains(charSequence.toString().toUpperCase())) {
							listaEquipefiltro.add(parametroEquipe);
						}
					}
				} else if (contemIfem) {
					informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
					listaEquipefiltro.addAll(parametroEquipeList);
				} else {
					listaEquipefiltro.addAll(parametroEquipeList);
					informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}

				timeCoracaoAdapter.notifyDataSetChanged();
				isChanged = false;
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
	}

	private void configuraBotoes(View fragmentView) {
		valorAposta = parentActivity.getValorApostaCartela();
		atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);

		botaoFinalizar = parentActivity.getBotaoAdicionarCompletarCartela();
		botaoFinalizar.setOnClickListener(this);

		botaoLimparAposta = parentActivity.getBotaoLimparAposta();
		botaoLimparAposta.setOnClickListener(this);


		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) || parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
			botaoFinalizar.setVisibility(View.INVISIBLE);
			String textoBotao;
			if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
				textoBotao = getString(R.string.label_selecionar_times_aleatoriamente);
			} else {
				textoBotao = getString(R.string.label_complete_placar_aleatoriamente);
			}
			botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(getActivity(), textoBotao));
		} else if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
				mGridTimeCoracao = null;
				atualizarBotaoFinalizar(R.string.label_escolha_time_coracao);
			} else if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
				atualizarBotaoFinalizar(R.string.escolhaOmes);
			} else if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
				atualizarBotaoFinalizar(R.string.label_escolha_trevos);
			}else {
				botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(fragmentView.getContext(), getString(R.string.adicionarAoCarrinho)));
				verificaSeMostraBotaoLimparAposta();
			}
		} else {
			atualizarBotaoFinalizarCompletarCartela();
			verificaSeMostraBotaoLimparAposta();
		}
	}

	public void voltaSalvarApostaParaLayoutInicial() {
		RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

		salvarApostaLayout.setBackgroundResource(R.color.cinzaescuro);
		TextView tituloSalvarAposta = parentActivity.getTextoSalveEstaAposta();

		tituloSalvarAposta.setText(R.string.label_salve_esta_aposta_carinha);

		ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

		final float scale = parentActivity.getResources().getDisplayMetrics().density;
		relativelayoutParams.height = (int) (64 * scale + 0.5f);

		salvarApostaLayout.setLayoutParams(relativelayoutParams);

		ImageView starSalvarApostaImageView = parentActivity.getIconStarSalvar();
		starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritar);

		salvarApostaLayout.setOnClickListener(this);

		EditText nomeAposta = parentActivity.getEditNomeAposta();
		nomeAposta.setText("");
		salvarApostaLayout.setVisibility(View.GONE);
	}

	private void verificaSeMostraBotaoLimparAposta() {
		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
			if (partidasSelecionadas.isEmpty()) {
				botaoLimparAposta.setVisibility(View.GONE);
			} else {
				botaoLimparAposta.setVisibility(View.VISIBLE);
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {

			int visibility = View.GONE;
			for (ParametroPartida parametroPartida : parametroJogo.getPartidas()) {
				if (StringUtils.isNotEmpty(parametroPartida.getEquipe1().getPlacar()) || StringUtils.isNotEmpty(parametroPartida.getEquipe2().getPlacar())) {
					visibility = View.VISIBLE;
					break;
				}
			}
			botaoLimparAposta.setVisibility(visibility);
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
			if (isTelaSelecaoTimeAtivado && (equipeSelecionada == null || !equipeSelecionada.isSelecionado())) {
				botaoLimparAposta.setVisibility(View.GONE);
			} else if (!isTelaSelecaoTimeAtivado && dezenasSelecionadas.isEmpty()) {
				botaoLimparAposta.setVisibility(View.GONE);
			} else {
				botaoLimparAposta.setVisibility(View.VISIBLE);
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			if (mostraLimparSuperSete()) {
				botaoLimparAposta.setVisibility(View.VISIBLE);
			} else {
				botaoLimparAposta.setVisibility(View.GONE);
			}
		} else if (dezenasSelecionadas.isEmpty()) {
			botaoLimparAposta.setVisibility(View.GONE);
		} else {
			botaoLimparAposta.setVisibility(View.VISIBLE);
		}
	}

	private void limpaNumerosSuperSete() {
		if (listaGridsSuperSete != null) {
			for (ExpandableHeightGridView grid : listaGridsSuperSete) {
				((SuperSeteAdapter) grid.getAdapter()).limpaNumeros();
			}
		}
		botaoLimparAposta.setVisibility(View.GONE);
		parentActivity.qtdDezenasPossiveisSelecionado = parametroJogo.getQuantidadeMinima() != null ? parametroJogo.getQuantidadeMinima() : 0;
		parentActivity.atualizaTextoBotoesQtdSelecionadas();

		qtdTotalSelecionadosSuperSete = 0;
		//parentActivity.qtdDezenasPossiveisSelecionado = parametroJogo.getQuantidadeMinima();
		atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
	}

	private boolean mostraLimparSuperSete() {
		if (listaGridsSuperSete != null) {
			for (ExpandableHeightGridView grid : listaGridsSuperSete) {
				if (((SuperSeteAdapter) grid.getAdapter()).dezenasSelecionadas.size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	private void verificaSeMostraSalvarApostaView() {

		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) || parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL) || parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE) || parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)) {
			ocutarSalvarAposta();
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
			if (isTelaSelecaoTimeAtivado && (equipeSelecionada != null && equipeSelecionada.isSelecionado())) {
				mostrarSalvarAposta();
			} else {
				ocutarSalvarAposta();
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			if (validaSelecaoSuperSete()) {
				mostrarSalvarAposta();
			} else {
				ocutarSalvarAposta();
			}
		} else if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
			mostrarSalvarAposta();
		} else {
			ocutarSalvarAposta();
		}
	}

	private void ocutarSalvarAposta() {
		RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

		salvarApostaLayout.setVisibility(View.GONE);
	}

	private void mostrarSalvarAposta() {
		RelativeLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

		salvarApostaLayout.setVisibility(View.VISIBLE);
		final float scale = parentActivity.getResources().getDisplayMetrics().density;
		salvarApostaLayout.getLayoutParams().height = (int) (64 * scale + 0.5f);
		salvarApostaLayout.setOnClickListener(this);
	}

	public void atualizaStatusBotaoFinalizar(Boolean isPickerPrognosticos) {
		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
			if (partidasSelecionadas.size() < parametroJogo.getPartidas().size()) {
				botaoFinalizar.setVisibility(View.INVISIBLE);
				botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
				botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_complete_placar_aleatoriamente)));
			} else {
				botaoFinalizar.setVisibility(View.VISIBLE);
				atualizarBotaoFinalizarAdicionarCarrinho();
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
			if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado && isEcolhaTimeBtnAtivado) {
				botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.timemaniaescuro));
				if (mGridTimeCoracao != null) {
					botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_selecionar_time_aleatoriamente)));
					botaoFinalizar.setBackgroundColor(getContext().getResources().getColor(R.color.cinza));
				} else {
					botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_escolha_time_coracao)));
					botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.timemaniaescuro));
				}
			} else {
				atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			if (matrizSelecionada != null) {
				preencheSuperSete(matrizSelecionada);
				matrizSelecionada = null;
				if (validaSelecaoSuperSete() && qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
					atualizarBotaoFinalizarAdicionarCarrinho();
				} else {
					atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
				}
			} else {
				if (validaSelecaoSuperSete() && qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
					atualizarBotaoFinalizarAdicionarCarrinho();
				} else {
					atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
				}
			}
		} else {
			atualizarAdicionarCarinhoCompletarCartela(isPickerPrognosticos);
		}
	}

	private void atualizaStatusBotaoFinalizarLoteca(Boolean mostrarBotao) {
		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
			int duplo  = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
			int triplo = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

			if (partidasSelecionadas.size() == parametroJogo.getPartidas().size() && (duplo > 0 || triplo > 0)) {
				if (mostrarBotao) {
					botaoFinalizar.setVisibility(View.VISIBLE);
					atualizarBotaoFinalizarAdicionarCarrinho();
				} else {
					botaoFinalizar.setVisibility(View.INVISIBLE);
				}

			} else {
				botaoFinalizar.setVisibility(View.INVISIBLE);
				botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
				botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_selecionar_times_aleatoriamente)));
			}
		}
	}

	private void atualizarAdicionarCarinhoCompletarCartela(Boolean isPickerPrognosticos) {
		if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado || isEscolhaTimeCoracaoSurpresinha) {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
				atualizarBotaoFinalizar(R.string.escolhaOmes);
			} else if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
				atualizarBotaoFinalizar(R.string.label_escolha_trevos);
			} else {
				atualizarBotaoFinalizarAdicionarCarrinho();
				if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
					if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
						botaoFinalizar.setClickable(true);
						botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.timemaniaescuro));
					} else {
						botaoFinalizar.setClickable(false);
						botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.azulclaro));
						parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
					}
				}
			}

		} else if (dezenasSelecionadas.size() > parentActivity.qtdDezenasPossiveisSelecionado) {
			dezenasSelecionadas.clear();
			simularDezenasAdapter.notifyDataSetChanged();
			atualizarBotaoFinalizarCompletarCartela();

			botaoLimparAposta.setVisibility(View.GONE);
		} else {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7) && isPickerPrognosticos) {
				atualizaSuperSetePrognosticoMaior(ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
			}
			atualizarBotaoFinalizarCompletarCartela();
		}
	}

	private void atualizarBotaoFinalizar(int stringId) {
		botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
		botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(stringId)));
		verificaSeMostraSalvarApostaView();
	}

	private void atualizarBotaoFinalizarCompletarCartela() {
		botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinza));
		botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.completarCartela)));
		parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
	}

	private void atualizarBotaoFinalizarAdicionarCarrinho() {
		botaoFinalizar.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
		botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.adicionarAoCarrinho)));
		if ((!parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) && !parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) && !parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
			parentActivity.getSalvarApostaLayout().setVisibility(View.VISIBLE);
		}
	}

	private void configuraListaDezenas(View fragmentView) {

		listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
		recyclerViewPartida = fragmentView.findViewById(R.id.recyclerViewPartida);

		if (dezenas != null && dezenas.size() > 0 && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {

			listaDezenas.setExpanded(true);
			simularDezenasAdapter = new SimularDezenasAdapter(getActivity(), dezenas);
			listaDezenas.setAdapter(simularDezenasAdapter);
			listaDezenas.setOnItemClickListener((adapterView, view, position, l) -> {
				Dezena  dezena           = dezenas.get(position);
				Integer valorSelecionado = Integer.valueOf(dezena.getValue());

				if (dezenasSelecionadas.contains(valorSelecionado)) {
					dezenasSelecionadas.remove(valorSelecionado);
					if (dezenasSelecionadas.size() >= parametroJogo.getQuantidadeMinima()) {
						parentActivity.qtdDezenasPossiveisSelecionado -= 1;
						parentActivity.verificaNovoQtdDezenasMax(dezenasSelecionadas.size());
						verificaSeMostraBotaoLimparAposta();
					}
					if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && selcioneOutrosNumeros.isChecked()) {
						selcioneOutrosNumeros.setChecked(false);
					}
					atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);

				} else {
					int maxNumerosParaSelecionar = parametroJogo.getQuantidadeMaxima();
					if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
						// VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS
						int qtdDezenasPossiveisSelecionadoAtual = parentActivity.qtdDezenasPossiveisSelecionado;

						parentActivity.qtdDezenasPossiveisSelecionado = cartelaFragmentListener.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);

						if (qtdDezenasPossiveisSelecionadoAtual < maxNumerosParaSelecionar) {
							parentActivity.qtdDezenasPossiveisSelecionado += 1;
							parentActivity.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);
							atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
							if (parentActivity.getFlagApostaSalva()) {
								voltaSalvarApostaParaLayoutInicial();
							}
							dezenasSelecionadas.add(valorSelecionado);
						} else {
							ViewUtils.alert(getActivity(), getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
						}
					} else {
						dezenasSelecionadas.add(valorSelecionado);
					}
				}

				if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
					isEcolhaTimeBtnAtivado = true;
				}

				if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
					if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
						setVisibilityCheckedNumerosLayout(View.VISIBLE);
					} else {
						setVisibilityCheckedNumerosLayout(View.GONE);
					}
				}


				verificaSeMostraSalvarApostaView();
				verificaSeMostraBotaoLimparAposta();
				if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
					atualizaStatusBotaoFinalizarLoteca(false);
				} else {
					atualizaStatusBotaoFinalizar(false);
				}

				simularDezenasAdapter.notifyDataSetChanged();
			});
		} else {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
				configurarTimesLoteca();
				partidasSelecionadas = new HashSet<>();
				fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
			} else if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
				configurarTimesLotogol();
				partidasSelecionadas = new HashSet<>();
				fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
				fragmentView.findViewById(R.id.layoutTitleLoteca).setPadding(20, 80, 0, 0);
				((TextView) fragmentView.findViewById(R.id.titleContinue)).setText(R.string.label_quantidade_gols_cada_time);

			} else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
				configuraSuperSete(fragmentView);
				partidasSelecionadas = new HashSet<>();
			}
		}
	}

	private void configurarTimesLoteca() {
		lotecaAdapter = new LotecaAdapter(parametroJogo, this);
		recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerViewPartida.setAdapter(lotecaAdapter);
		recyclerViewPartida.setExpanded(true);
	}

	private void configurarTimesLotogol() {
		LotogolAdapter lotogolAdapter = new LotogolAdapter(parametroJogo.getPartidas(), this);
		recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerViewPartida.setAdapter(lotogolAdapter);
		recyclerViewPartida.setExpanded(true);
	}


	private void configuraSuperSete(View viewLayout) {
		listaGridsSuperSete = new ArrayList<>();
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_primeiro));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_segundo));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_terceiro));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_quarto));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_quinto));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_sexto));
		listaGridsSuperSete.add(viewLayout.findViewById(R.id.ehgv_super_sete_setimo));

		listaAdaptersSuperSete = new ArrayList<>();
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));
		listaAdaptersSuperSete.add(new SuperSeteAdapter(typeGameColorDark, parentActivity));

		listaGridsSuperSete.get(0).setAdapter(listaAdaptersSuperSete.get(0));
		listaGridsSuperSete.get(1).setAdapter(listaAdaptersSuperSete.get(1));
		listaGridsSuperSete.get(2).setAdapter(listaAdaptersSuperSete.get(2));
		listaGridsSuperSete.get(3).setAdapter(listaAdaptersSuperSete.get(3));
		listaGridsSuperSete.get(4).setAdapter(listaAdaptersSuperSete.get(4));
		listaGridsSuperSete.get(5).setAdapter(listaAdaptersSuperSete.get(5));
		listaGridsSuperSete.get(6).setAdapter(listaAdaptersSuperSete.get(6));

		for (ExpandableHeightGridView listaNumeros : listaGridsSuperSete) {
			listaNumeros.setVisibility(View.VISIBLE);
			listaNumeros.setOnItemClickListener((adapterView, view, position, l) -> {
				SuperSeteAdapter   adapter           = ((SuperSeteAdapter) listaNumeros.getAdapter());
				Dezena             dezena            = adapter.dezenas.get(position);
				ArrayList<Integer> listaSelecionados = (ArrayList<Integer>) adapter.dezenasSelecionadas;
				Integer            valorSelecionado  = Integer.valueOf(dezena.getValue());

				if (listaSelecionados.contains(valorSelecionado)) {
					if (listaSelecionados.size() == adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado)) {
						listaSelecionados.remove(valorSelecionado);
						qtdTotalSelecionadosSuperSete--;
						if (qtdTotalSelecionadosSuperSete >= parametroJogo.getQuantidadeMinima()) {
							parentActivity.qtdDezenasPossiveisSelecionado -= 1;
							parentActivity.verificaNovoQtdDezenasMax(qtdTotalSelecionadosSuperSete);
							verificaSeMostraBotaoLimparAposta();
						}
						atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
					} else {
						ViewUtils.alert(getActivity(),
										getString(R.string.msg_bloqueio_coluna_super_sete_desmarcar).replace(
												"{qtdColunas}",
												String.valueOf(listaSelecionados.size())));
					}
				} else {
					int maxNumerosParaSelecionar = parametroJogo.getQuantidadeMaxima();
					if (qtdTotalSelecionadosSuperSete == parentActivity.qtdDezenasPossiveisSelecionado) {
						// VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS
						int qtdDezenasPossiveisSelecionadoAtual = parentActivity.qtdDezenasPossiveisSelecionado;

						parentActivity.qtdDezenasPossiveisSelecionado = cartelaFragmentListener.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);

						if (qtdDezenasPossiveisSelecionadoAtual < maxNumerosParaSelecionar) {
							if (listaSelecionados.size() < adapter.QTD_MAX_POR_COLUNA) {
								if (listaSelecionados.size() < adapter.getQtdColunasMaximas((parentActivity.qtdDezenasPossiveisSelecionado + 1))) {
									parentActivity.qtdDezenasPossiveisSelecionado += 1;
									parentActivity.verificaNovoQtdDezenasMax(parentActivity.qtdDezenasPossiveisSelecionado);
									atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
									if (parentActivity.getFlagApostaSalva()) {
										voltaSalvarApostaParaLayoutInicial();
									}
									listaSelecionados.add(valorSelecionado);
									qtdTotalSelecionadosSuperSete++;
								} else {
									ViewUtils.alert(getActivity(),
													getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
															"{qtdColunas}",
															String.valueOf(adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado))));
								}
							} else {
								ViewUtils.alert(getActivity(), getString(R.string.msg_qtd_max_col_super_sete));
							}
						} else {
							ViewUtils.alert(getActivity(), getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
						}
					} else {
						if (listaSelecionados.size() < adapter.QTD_MAX_POR_COLUNA) {
							if (listaSelecionados.size() < adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado)) {
								listaSelecionados.add(valorSelecionado);
								qtdTotalSelecionadosSuperSete++;
							} else {
								ViewUtils.alert(getActivity(),
												getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
														"{qtdColunas}",
														String.valueOf(adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado))));
							}
						} else {
							ViewUtils.alert(getActivity(), getString(R.string.msg_qtd_max_col_super_sete));

						}

					}
				}

				verificaSeMostraSalvarApostaView();
				verificaSeMostraBotaoLimparAposta();
				atualizaStatusBotaoFinalizar(false);
				adapter.notifyDataSetChanged();
			});
		}
	}

	/**
	 * Metodo configura a cartela de times do coracao
	 */
	private void configurarSelecaoTimeCoracao() {
		listaEquipefiltro = new ArrayList<>(parametroJogo.getEquipes());
		timeCoracaoAdapter = new TimeCoracaoAdapter(getActivity(), listaEquipefiltro);
		mGridTimeCoracao.setAdapter(timeCoracaoAdapter);
		mGridTimeCoracao.setExpanded(true);
		mGridTimeCoracao.setOnItemClickListener((adapterView, viewItem, i, l) -> {
			if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
				equipeSelecionada.setSelecionado(false);
			}
			equipeSelecionada = listaEquipefiltro.get(i);
			equipeSelecionada.setSelecionado(true);
			String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
			informeNomeTime.setText(nomeTime);
			timeCoracaoAdapter.notifyDataSetChanged();
			verificaSeMostraBotaoLimparAposta();
			verificaSeMostraSalvarApostaView();
			atualizarAdicionarCarinhoCompletarCartela(false);
		});
	}

	@Override
	public void clickEquipeLoteca(ParametroPartida parametroPartida) {
		BigDecimal ultimoValor = valorEmReais;
		if (parametroPartida.isSelecionado()) {
			if (parametroPartida.getQtdItensSelecionados() == 1) {
				parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) + 1));
				if (parametroPartida.getQtdItensSelecionadosAnterior() == 2) {
					parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
				}
			} else if (parametroPartida.getQtdItensSelecionados() == 2) {
				parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) + 1));
				if (parametroPartida.getQtdItensSelecionadosAnterior() == 3) {
					parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) - 1));
				} else {
					parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
				}
			} else if (parametroPartida.getQtdItensSelecionados() == 3) {
				parentActivity.setLabelValueDuplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()) - 1));
				parentActivity.setLabelValueTriplasLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()) + 1));
			}
			partidasSelecionadas.add(parametroPartida);
		} else {
			parentActivity.setLabelValueSimplesLoteca(String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()) - 1));
			partidasSelecionadas.remove(parametroPartida);
		}
		Boolean mostraBotao = atualizaValorApostaLoteca();
		atualizaStatusBotaoFinalizarLoteca(mostraBotao);
		verificaSeMostraBotaoLimparAposta();
	}

	private void limparPartidadasSelecionasLoteca(boolean desativado) {
		for (ParametroPartida partida : partidasSelecionadas) {
			partida.setEmpate(desativado);
			partida.getEquipe1().setSelecionado(desativado);
			partida.getEquipe2().setSelecionado(desativado);
			partida.setSelecionado(desativado);
			partida.setQtdItensSelecionadosAnterior(0);
			partida.setQtdItensSelecionados(0);
		}
		recyclerViewPartida.getAdapter().notifyDataSetChanged();
		parentActivity.zerarApostasLoteca();
		partidasSelecionadas.clear();
		ocutarSalvarAposta();
		atualizaValorApostaLoteca();
		botaoFinalizar.setVisibility(View.INVISIBLE);
	}

	private void limparPartidasSelecionadasLotogol(boolean desativado) {
		for (ParametroPartida partida : parametroJogo.getPartidas()) {
			partida.setSelecionado(desativado);
			partida.getEquipe1().setPlacar(StringUtils.EMPTY);
			partida.getEquipe2().setPlacar(StringUtils.EMPTY);

		}
		partidasSelecionadas.clear();
		recyclerViewPartida.setExpanded(true);
		recyclerViewPartida.setAdapter(new LotogolAdapter(parametroJogo.getPartidas(), this));
		verificaSeMostraSalvarApostaView();
	}

	@Override
	public void partidaSelecionada() {

	}

	@Override
	public void atualizarParametrosLotogol(ParametroPartida parametroPartida) {
		if (parametroPartida.isSelecionado()) {
			partidasSelecionadas.add(parametroPartida);
		} else {
			partidasSelecionadas.remove(parametroPartida);
		}
		verificaSeMostraBotaoLimparAposta();
		atualizaStatusBotaoFinalizar(false);
		verificaSeMostraSalvarApostaView();
	}

	public void setEscolhaTimeCoracaoSurpresinha(boolean escolhaTimeCoracaoSurpresinha) {
		isEscolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
	}

	private boolean atualizaValorApostaLoteca() {
		Boolean mostraBotao = false;
		Integer duplos      = Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString());
		Integer triplos     = Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString());

		if (duplos > 0 || triplos > 0) {
			for (ParametroValorApostaDTO parametroValorApostaDTO : parametroJogo.getValoresAposta()) {
				if (parametroValorApostaDTO.getQuantidadeDuplos().equals(duplos) && parametroValorApostaDTO.getQuantidadeTriplos().equals(triplos)) {
					valorEmReais = parametroValorApostaDTO.getValor();
					mostraBotao = true;
					break;
				}
			}
		} else {
			valorEmReais = parametroJogo.getValorApostaMinima();
		}

		ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
		valorTotalAposta = valorEmReais;
		return mostraBotao;
	}

	private void editaAposta() {
		if (parentActivity.getAposta() != null) {
			IdentificaoDeUmaApostaDas8Modalidades<List<Integer>>       aposta          = parentActivity.getAposta();
			IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> apostaSuperSete = parentActivity.getAposta();

			if (parentActivity.getAposta().getModalidade() == ModalidadeEnum.SUPER_7) {
				qtdTotalSelecionadosSuperSete = getQtdTotalSelecionadosSuperSete(apostaSuperSete.getMatrizNumerosSelecionados());
				configuraSuperSete(view);
				matrizSelecionada = new ArrayList<>();
				matrizSelecionada.addAll(apostaSuperSete.getMatrizNumerosSelecionados());
				parentActivity.qtdDezenasPossiveisSelecionado = apostaSuperSete.getQuantidadeNumeros();
				parentActivity.qtdConcursoSelecionado = apostaSuperSete.getQuantidadeTeimosinhas();
				parentActivity.atualizaTextoBotoesQtdSelecionadas();
				atualizaStatusBotaoFinalizar(false);
				ViewUtils.setMoedaFormatHtml(aposta.getValor(), valorAposta);

			} else {
				if (CollectionUtils.isNotEmpty(aposta.getListaNumerosSelecionados())) {
					dezenasSelecionadas = aposta.getListaNumerosSelecionados();
					parentActivity.qtdDezenasPossiveisSelecionado = aposta.getQuantidadeNumeros();
					parentActivity.qtdConcursoSelecionado = aposta.getQuantidadeTeimosinhas();
					parentActivity.atualizaTextoBotoesQtdSelecionadas();
					equipeSelecionada = aposta.getTimeDoCoracao();
					if (equipeSelecionada != null) {
						isEcolhaTimeBtnAtivado = Boolean.TRUE;
					}
					atualizaStatusBotaoFinalizar(false);
				}
				ViewUtils.setMoedaFormatHtml(aposta.getValor(), valorAposta);
			}
		}
	}

	private void timemaniaTimeSelecionadoEdicao() {
		if (equipeSelecionada != null) {
			equipeSelecionada.setSelecionado(Boolean.TRUE);
			String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
			informeNomeTime.setText(nomeTime);

			for (ParametroEquipe equipe : listaEquipefiltro) {
				if (equipe.getNumero().equals(equipeSelecionada.getNumero())) {
					equipe.setSelecionado(equipeSelecionada.isSelecionado());
					equipeSelecionada = equipe;
					break;
				}
			}
			timeCoracaoAdapter.notifyDataSetChanged();
			verificaSeMostraBotaoLimparAposta();
			verificaSeMostraSalvarApostaView();
			atualizarAdicionarCarinhoCompletarCartela(false);
		}
	}

	// Adapter do gridview das dezenas
	private class SimularDezenasAdapter extends BaseAdapter {
		private Context context;
		private List<Dezena> rows;

		SimularDezenasAdapter(Context context, List<Dezena> activitData) {
			this.context = context;
			this.rows = activitData;
		}

		@Override
		public int getCount() {
			return rows.size();
		}

		@Override
		public Object getItem(int i) {
			return rows.get(i);
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			TextView         textView;
			GradientDrawable shape     = new GradientDrawable();
			int              sizeItens = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, parentActivity.getResources().getDisplayMetrics());

			if (view == null) {
				textView = new TextView(this.context);

				textView.setLayoutParams(new GridView.LayoutParams(sizeItens, sizeItens));
			} else {
				textView = (TextView) view;
			}

			if (dezenasSelecionadas.contains(Integer.valueOf(rows.get(i).getValue()))) {
				shape.setColor(ContextCompat.getColor(context, typeGameColorLight));
				textView.setTextColor(Color.WHITE);
				rows.get(i).setSelected(Boolean.TRUE);
			} else {
				shape.setColor(Color.WHITE);
				textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));
				rows.get(i).setSelected(Boolean.FALSE);
			}

			shape.setCornerRadius(sizeItens / 2);
			shape.setStroke(1, Color.GRAY);
			textView.setBackground(shape);
			if (dezenas.get(i).getValue().length() == 1) {
				textView.setText(String.format("0%s", dezenas.get(i).getValue()));
			} else {

				textView.setText(dezenas.get(i).getValue());
			}
			textView.setTypeface(ViewUtils.getFontFuturaBold(context));
			textView.setGravity(Gravity.CENTER);


			return textView;
		}
	}
}