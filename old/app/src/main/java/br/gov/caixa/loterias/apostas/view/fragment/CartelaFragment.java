package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.*;
import br.gov.caixa.loterias.apostas.view.listener.*;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CartelaModel;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SuperSeteAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TimeCoracaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.NomeTimemaniaTextWatcher;
import br.gov.caixa.loterias.apostas.view.holder.DezenaHolder;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;
import br.gov.caixa.loterias.apostas.view.listener.NomeTimeTextWatcherListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnEscudoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import br.gov.caixa.loterias.apostas.view.listener.custom.LoteriasSensorEventListener;

public class CartelaFragment extends Fragment implements OnClickListener, LocateAdapterListener, LotogolListener {
	private static final int QUANTIDADE_COLUNA_LISTA = 5;
	private SensorManager mSensorManager;
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
	private AppCompatCheckBox selecioneOutrosNumeros;
	private View view;
	private EditText informeNomeTime;
	private RelativeLayout opcaoOutrosNumerosLayout, rlColunasSuperSete;
	private LinearLayout selecioneTimeCoracaoLayout;
	private RecyclerView mGridTimeCoracao;
	private RecyclerView listaDezenas;
	private ArrayList<ExpandableHeightGridView> listaGridsSuperSete;
	private ArrayList<SuperSeteAdapter> listaAdaptersSuperSete;
	private ExpandableHeightRecyclerView recyclerViewPartida;
	private ScrollView svSuperSete;
	private int qtdTotalSelecionadosSuperSete = 0;

	private boolean isEcolhaTimeBtnAtivado;
	private boolean isTelaSelecaoTimeAtivado;
	private boolean isEscolhaTimeCoracaoSurpresinha;
	private boolean isAvisou = false;
	public BigDecimal valorTotalAposta;
	public int typeGameColorLight, typeGameColorDark;
	public int corFonteFundoBranco, corFonteFundoClaro, corFonteFundoEscuro;
	public MaterialButton botaoFinalizar, botaoLimparAposta;
	public TextView valorAposta, valorTotalJogos, valorTotalSimples, valorTotalDuplas, valorTotalTriplas;
	public ListaDezenaRecyclerView simularDezenasAdapter;
	public String dataSorteioAtual;

	private SimularApostaModel model;
	private CartelaModel cartelaModel;
	private View rodape;
	private boolean mudouTamanhoValor = false;
	private TextView titleLoteca;

	public BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
	public CartelaFragment() {
		// Required empty public constructor
	}

	public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
		BigDecimal valorEmReais = BigDecimal.ZERO;
		loadParametroJogo();
		if (parametroJogo.getValoresAposta() == null) {
			DialogUtils.dialogEntendiListener(getActivity(),
					getString(R.string.nao_concluiu_aposta),
					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							Intent intent = IntentUtil.getIntentLimpandoPilhaActivities(getActivity(), PrincipalActivity.class);
							getActivity().startActivity(intent);
							getActivity().finish();
						}
					});

			return;
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
			ParametroValorApostaDTO valorAposta = parametroJogo.getValorApostaBy(numPrognostico, parametroJogo.getTrevos().getQtdMinima());

			valorEmReais = valorAposta.getValor();
			if (parentActivity.getQtdConcursos() > 0){
				valorTotalAposta = valorEmReais.multiply(BigDecimal.valueOf(parentActivity.getQtdConcursos()).movePointLeft(0));
			} else {
				valorTotalAposta = valorEmReais;
			}

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

		try{
			valorEmReais = valorEmReais.equals(BigDecimal.ZERO) ? parametroJogo.getValoresAposta().get(0).getValor() : valorEmReais;
		}catch (Exception e){
			if (parametroJogo == null) {
				loadParametroJogo();
			}
				valorEmReais = valorEmReais.equals(BigDecimal.ZERO) ? parametroJogo.getValorApostaMinima() : valorEmReais;
		}

		if (qtdTeimosinhas != 0) {
			valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdTeimosinhas).movePointLeft(0));
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && selecioneOutrosNumeros.isChecked()) {
			valorEmReais = valorEmReais.multiply(Constantes.DOIS_BIG_DECIMAL);
		}

		if(valorEmReais != null) {
			if (valorEmReais.doubleValue() > 999.99) {
				if (!mudouTamanhoValor) {
					TextViewUtils.mudarTamanhoPorPorcentagem(valorAposta, -30);
					mudouTamanhoValor = true;
				}
			} else {
				if (mudouTamanhoValor) {
					TextViewUtils.mudarTamanhoPorPorcentagem(valorAposta, 30);
					mudouTamanhoValor = false;
				}
			}
			ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
			valorAposta.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			valorTotalAposta = valorEmReais;
		}
		else {
			DialogUtils.dialogEntendiListener(getActivity(),
					getString(R.string.nao_concluiu_aposta),
					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							Intent intent = IntentUtil.getIntentLimpandoPilhaActivities(getActivity(), PrincipalActivity.class);
							getActivity().startActivity(intent);
							getActivity().finish();
						}
					});
		}
	}

	private void loadParametroJogo() {
		if (parametroJogo == null) {
			SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
			parametroJogo = ViewUtils.getParametroSimulacao(sessaoUsuario, tipoJogo).getParametroJogo();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);



			if (getArguments() != null) {
				try {
					barraTituloDTO = (BarraTituloDTO) getArguments().getSerializable("barraTituloModel");
				}catch (Exception e){
					Log.e("ERROR", "Não foi possível adiquirir os elementos da barra de título");
				}
			}
		parentActivity = ((SimularApostaActivity) getActivity());
		identificarTipoJogo();
		if (parentActivity != null) {
			parametroJogo = parentActivity.getParametroSimulacao();
		}

		if (parametroJogo != null && dezenas.size() == 0 && parametroJogo.getPrognosticoMaximo() != null
				&& !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			for (int i = 1; i <= parametroJogo.getPrognosticoMaximo(); i++) {
				if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && i == 100){
					dezenas.add(new Dezena("" + i, Boolean.FALSE, "00"));
				} else {
					dezenas.add(new Dezena("" + i, Boolean.FALSE, "" + i));
				}
			}
		}

		if (parametroJogo != null && dezenas.size() == 0 && parametroJogo.getPrognosticoMaximo() != null && !(tipoJogo == ModalidadeEnum.SUPER_7)) {
			dezenas = VolanteUtils.getDezenas(tipoJogo, parametroJogo.getPrognosticoMaximo());
		}

		model = new SimularApostaModel(getActivity());
		cartelaModel = new CartelaModel();
		partidasSelecionadas = new HashSet<>();

	}

	@Override
	public void onStart() {
		super.onStart();

		loadParametroJogo();

		if(parentActivity != null && parametroJogo != null){
			if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) || parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)){
				configuraBotoesLotecaOuLotogol();
			}
		}

		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
			opcaoOutrosNumerosLayout = view.findViewById(R.id.opcaoOutrosNumerosLayout);
			selecioneOutrosNumeros = opcaoOutrosNumerosLayout.findViewById(R.id.selcioneOutrosNumeros);
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

		if (!isEscolhaTimeCoracaoSurpresinha) {
			configuraListaDezenas(view);
			verificaSeMostraBotaoLimparAposta();
			verificaSeMostraSalvarApostaView();
		}
		editaAposta();
	}

	public void cartelaFragmentBuilder(Bundle dataBundle) {
		if (dataBundle != null) {
			dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

			typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
			typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
			corFonteFundoBranco = (int) dataBundle.get(SimularApostaActivity.COR_FONTE_FUNDO_BRANCO);
			corFonteFundoClaro = (int) dataBundle.get(SimularApostaActivity.COR_FONTE_FUNDO_CLARO);
			corFonteFundoEscuro = (int) dataBundle.get(SimularApostaActivity.COR_FONTE_FUNDO_ESCURO);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_cartela, container, false);
		rodape = view.findViewById(R.id.rodape_espacamento);
		titleLoteca = view.findViewById(R.id.titleInformeBold);
		ViewCompat.setAccessibilityHeading(titleLoteca,true);
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
		if(AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(getActivity(), 2)) return;
		if(LoginSP.isLoginRealizado() && checaLimiteDiario()){
			DialogUtils.dialogSim(getContext(),
					getString(R.string.MA014),

					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							executaAddCarrinho();
						}
					}
			);
		} else {
			executaAddCarrinho();
		}
	}

	private boolean checaLimiteDiario() {
		return CarrinhoSingleton.getInstance().getCarrinho() != null &&
				CarrinhoSingleton.getInstance().getCarrinho().getValorTotal() != null &&
				SessaoUsuario.getInstance().getParametrosSimulacao() != null &&
				SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() != null &&
				valorTotalAposta != null &&
				((CarrinhoSingleton.getInstance().getCarrinho().getValorTotal()
						.add(valorTotalAposta))
						.compareTo(SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario()) > 0);
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
private void zeraLoteca(){
	limparPartidadasSelecionasLoteca(Boolean.FALSE);
	limparOpcaoOutrosNumerosLayout();
	verificaSeMostraBotaoLimparAposta();
	atualizaStatusBotaoFinalizar(false);
	ocutarSalvarAposta();
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
							parentActivity.proximaPagina();
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
				}else if(parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)){
					adcionarApostaPreencherNumerosAleatorios();
					zeraLoteca();
				}
				else {
					adcionarApostaPreencherNumerosAleatorios();
				}
			}
			break;

			case R.id.botaoLimparAposta: { // LIMPAR APOSTA
				if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) && partidasSelecionadas != null) {
					limparPartidadasSelecionasLoteca(Boolean.FALSE);
				} else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA) && isTelaSelecaoTimeAtivado) {
					limparTimeSelecionado();
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
				ocutarSalvarAposta();
			}
			break;

			case R.id.salvarApostaLayout: {
				ConstraintLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

				ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

				final float scale = parentActivity.getResources().getDisplayMetrics().density;
				if (relativelayoutParams.height != ((int) (140 * scale + 0.5f))){
					relativelayoutParams.height = (int) (140 * scale + 0.5f);
				} else  {
					relativelayoutParams.height = (int) (64 * scale + 0.5f);
				}

				salvarApostaLayout.setLayoutParams(relativelayoutParams);

				if(!parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
					salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
				}

				if (!DadosUsuarioBO.checarUsuarioLogado(parentActivity)) {
					AlertDialogExperimenteLogarSingleton.show(getActivity(), false, null);
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

					if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
						aposta.setParametroTrevo(new ParametroTrevo());
					}

					if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
						parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
					} else {
						parentActivity.salvarAposta(aposta, nomeAposta.getText().toString(), dezenasSelecionadas);

					}
				} else {
					DialogUtils.dialogEntendi(getContext(), getString(R.string.msg_favor_inserir_nome_aposta));
				}
			}

		}
	}

	private void executaAddCarrinho() {
		if (isEscolhaTimeCoracaoSurpresinha) {
			cartelaFragmentListener.adicionarTimeSurpresa(equipeSelecionada);
			return;
		}

		dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);
		logAposta();

		if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> aposta = ApostaUtils
					.getApostaSuperSete(parametroJogo, dezenasSelecionadas,
							valorTotalAposta,parentActivity.qtdConcursoSelecionado,
							ListaUtils.pegaListaSuperSete(listaAdaptersSuperSete));
			model.adicionaApostaNoCarrinho(parentActivity, aposta, barraTituloDTO);
		} else {
			IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta = getApostaPreenchida();
			model.adicionaApostaNoCarrinho(parentActivity, aposta, barraTituloDTO);
		}
	}

	private void logAposta(){
		if (parametroJogo.getConcurso() == null || parametroJogo.getConcurso().getModalidade() == null
			|| parametroJogo.getConcurso().getNumero() == null || parentActivity.getLabelValueJogosLoteca().getText() == null
			|| parentActivity.getLabelValueSimplesLoteca().getText() == null || parentActivity.getLabelValueDuplasLoteca().getText() == null
			|| parentActivity.getLabelValueTriplasLoteca().getText() == null || valorTotalAposta == null){
			return;
		}
		if(parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)){
			AnalyticsHelper.getInstance().logInteraction(
					AnalyticsHelper.EventCategoryParams.CTA,
					AnalyticsHelper.EventActionParams.CLICK,
					AnalyticsHelper.EventLabelParams.ADD_TO_CART,
					AnalyticsHelper.Tela.MONTAR_APOSTA,
					AnalyticsHelper.JourneyParams.APOSTAR,
					AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
					ModalidadeEnum.fromString(parametroJogo.getConcurso().getModalidade()),
					parametroJogo.getConcurso().getNumero().toString(),
					parentActivity.getLabelValueJogosLoteca().getText().toString(),
					parentActivity.getLabelValueSimplesLoteca().getText().toString(),
					parentActivity.getLabelValueDuplasLoteca().getText().toString(),
					parentActivity.getLabelValueTriplasLoteca().getText().toString(),
					ViewUtils.getMoedaFormat(valorTotalAposta)
			);
		}
		else{
			AnalyticsHelper.getInstance().logInteraction(
					AnalyticsHelper.EventCategoryParams.CTA,
					AnalyticsHelper.EventActionParams.CLICK,
					AnalyticsHelper.EventLabelParams.ADD_TO_CART,
					AnalyticsHelper.JourneyParams.APOSTAR,
					AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
					AnalyticsHelper.Tela.MONTAR_APOSTA,
					ModalidadeEnum.fromString(parametroJogo.getConcurso().getModalidade()),
					parametroJogo.getConcurso().getNumero().toString(),
					"0",
					String.valueOf(parentActivity.qtdDezenasPossiveisSelecionado),
					String.valueOf(parentActivity.qtdConcursoSelecionado),
					ViewUtils.getMoedaFormat(valorTotalAposta)
			);
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
		selecioneOutrosNumeros.setChecked(Boolean.FALSE);
	}

	private void limparTimeSelecionado() {
		if (equipeSelecionada != null) {
			equipeSelecionada.setSelecionado(false);
			if (informeNomeTime != null){
				informeNomeTime.setText(StringUtils.EMPTY);
			}
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

	private void orderAscDezenas() {
		Integer       menorDezenaAtual = null;
		List<Integer> listaOrdenadaAsc = new ArrayList<>();
		while (dezenasSelecionadas.size() > 0) {
			for (Integer dezena : dezenasSelecionadas) {
				if (menorDezenaAtual == null) {
					menorDezenaAtual = dezena;
				} else if (menorDezenaAtual > dezena) {
					menorDezenaAtual = dezena;
				}
			}
			listaOrdenadaAsc.add(menorDezenaAtual);
			dezenasSelecionadas.remove(menorDezenaAtual);
			menorDezenaAtual = null;
		}
		dezenasSelecionadas = listaOrdenadaAsc;
	}

	private void adicionarApostaPreencherNumerosLotomania() {
		if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
			//verifica se foi marcada a opção para gerar outros números
			if (selecioneOutrosNumeros.isChecked()) {
				// Gera uma nova aposta com os outros 50 números não selecionados

				if (dezenasSelecionadas != null && dezenasSelecionadas.size() > 0) {
					orderAscDezenas();
				}

				IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida();

				aposta.setGerarEspelho(true);
				aposta.setValor(aposta.getValor().divide(Constantes.DOIS_BIG_DECIMAL));
				model.adicionaApostaNoCarrinho(parentActivity, aposta, barraTituloDTO);
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
			if (selecioneOutrosNumeros.isChecked()) {
				selecioneOutrosNumeros.setChecked(false);
			} else {
				selecioneOutrosNumeros.setChecked(true);
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

		dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);
		model.preencheNumerosAleatorios(parentActivity.qtdDezenasPossiveisSelecionado, dezenasSelecionadas, parametroJogo.getPrognosticoMaximo(),
										new OnSilceListener<List<Integer>>() {
											@Override
											public void success(List<Integer> list) {
												AlertDialogUtils.dismiss();
												isEcolhaTimeBtnAtivado = true;
												dezenasSelecionadas.clear();
												dezenasSelecionadas.addAll(list);
												simularDezenasAdapter.atualizaSelecionados(dezenasSelecionadas);
												atualizaBotoeslayout();
											}

											@Override
											public void error(VolleyError error) {
												AlertDialogUtils.dismiss();
											}
										});
	}

	private void preencheNumerosAleatoriosSuperSete(ArrayList<ArrayList<Integer>> listaInteiros) {
		AlertDialogUtils.show(getActivity());
		dezenasSelecionadas = ListaUtils.orderAscDezenas(dezenasSelecionadas);

		model.preencheNumerosAleatoriosSuperSete(parentActivity.qtdDezenasPossiveisSelecionado, listaInteiros, parametroJogo.getConcurso().getModalidade().name(),
												 new OnSilceListener<List<List<Integer>>>() {
													 @Override
													 public void success(List<List<Integer>> payload) {
														 AlertDialogUtils.dismiss();
														 isEcolhaTimeBtnAtivado = true;

														 preencheSuperSete(payload);
														 atualizaBotoeslayout();
														 atualizarBotaoFinalizarAdicionarCarrinho();
													 }

													 @Override
													 public void error(VolleyError error) {
														 AlertDialogUtils.dismiss();
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

		isEcolhaTimeBtnAtivado = true;
		isTelaSelecaoTimeAtivado = true;
		selecioneTimeCoracaoLayout = view.findViewById(R.id.selecioneTimeCoracao);
		mGridTimeCoracao = view.findViewById(R.id.gridTimes);
		preencherVisibilidaeGridView(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE);
		informeNomeTime = selecioneTimeCoracaoLayout.findViewById(R.id.informe_nome_time);
		onChangeInformeNomeTime();
		EscudoBO.getInstance().carregaEscudos(getContext(), () -> configurarSelecaoTimeCoracao());
		configurarSelecaoTimeCoracao();
		atualizaStatusBotaoFinalizar(false);
		verificaSeMostraBotaoLimparAposta();
		verificaSeMostraSalvarApostaView();

	}

	private void onChangeInformeNomeTime() {
		informeNomeTime.addTextChangedListener(new NomeTimemaniaTextWatcher(getActivity(), informeNomeTime, parametroJogo.getEquipes(),
				onNomeTimeTextWatchListener()));
	}

	private NomeTimeTextWatcherListener onNomeTimeTextWatchListener(){
		return new NomeTimeTextWatcherListener() {
			@Override
			public void isValido(boolean isValid) {
				if (isValid){
					aplicaCheckTimeSelecionado();
				} else {
					informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}
			}

			@Override
			public void limparFiltro() {
				listaEquipefiltro.clear();
			}

			@Override
			public void addFiltro(List<ParametroEquipe> listaFriltrada) {
				listaEquipefiltro.addAll(listaFriltrada);
				timeCoracaoAdapter.notifyDataSetChanged();
			}
		};
	}

	private void aplicaCheckTimeSelecionado() {
		informeNomeTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
	}

	private void configuraBotoesLotecaOuLotogol(){
		botaoFinalizar = parentActivity.getBotaoAdicionarCompletarCartela();
		botaoFinalizar.setOnClickListener(this);
			botaoFinalizar.setVisibility(View.GONE);
			String textoBotao;
			if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
				textoBotao = getString(R.string.label_selecionar_times_aleatoriamente);
			} else {
				textoBotao = getString(R.string.label_complete_placar_aleatoriamente);
			}
			botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(getActivity(), textoBotao));
	}

	private void configuraBotoes(View fragmentView) {
		valorAposta = parentActivity.getValorApostaCartela();
		atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);

		botaoFinalizar = parentActivity.getBotaoAdicionarCompletarCartela();
		botaoFinalizar.setOnClickListener(this);

		botaoLimparAposta = parentActivity.getBotaoLimparAposta();
		botaoLimparAposta.setOnClickListener(this);

		if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
				mGridTimeCoracao = null;
				atualizarBotaoFinalizar(R.string.label_escolha_time_coracao);
			} else if (parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
				atualizarBotaoFinalizar(R.string.escolhaOmes);
			} else if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
				atualizarBotaoFinalizar(R.string.label_escolha_trevos);
			}else {
				botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(fragmentView.getContext(), getString(R.string.adicionarAoCarrinho)));
				botaoFinalizar.setTextColor(ContextCompat.getColor(parentActivity, corFonteFundoEscuro));
				verificaSeMostraBotaoLimparAposta();
			}
		} else {
			atualizarBotaoFinalizarCompletarCartela();
			verificaSeMostraBotaoLimparAposta();
		}
	}

	public void voltaSalvarApostaParaLayoutInicial() {
		ConstraintLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

		salvarApostaLayout.setBackgroundResource(R.drawable.bg_salvar_aposta);
		TextView tituloSalvarAposta = parentActivity.getTextoSalveEstaAposta();

		tituloSalvarAposta.setText(R.string.label_salve_esta_aposta_carinha);

		ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

		final float scale = parentActivity.getResources().getDisplayMetrics().density;
		relativelayoutParams.height = (int) (64 * scale + 0.5f);

		salvarApostaLayout.setLayoutParams(relativelayoutParams);

		ImageView starSalvarApostaImageView = parentActivity.getIconStarSalvar();
		starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritar_novo);

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

		verificaMostraRodape();
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
		ConstraintLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

		salvarApostaLayout.setVisibility(View.GONE);
		verificaMostraRodape();
	}

	private void verificaMostraRodape(){
		if (parentActivity.getBotaoLimparAposta().getVisibility() == View.VISIBLE ||
		parentActivity.getSalvarApostaLayout().getVisibility() == View.VISIBLE){
			rodape.setVisibility(View.VISIBLE);
		} else {
			rodape.setVisibility(View.GONE);
		}
	}

	private void mostrarSalvarAposta() {
		ConstraintLayout salvarApostaLayout = parentActivity.getSalvarApostaLayout();

		salvarApostaLayout.setVisibility(View.VISIBLE);
		final float scale = parentActivity.getResources().getDisplayMetrics().density;
		salvarApostaLayout.getLayoutParams().height = (int) (64 * scale + 0.5f);
		salvarApostaLayout.setOnClickListener(this);

		verificaMostraRodape();
	}

	public void atualizaStatusBotaoFinalizar(Boolean isPickerPrognosticos) {
		if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) {
			if (partidasSelecionadas.size() < parametroJogo.getPartidas().size()) {
				botaoFinalizar.setVisibility(View.INVISIBLE);
				botaoFinalizar.setBackgroundTintList(
						ColorStateList.valueOf(ContextCompat.getColor(parentActivity, R.color.cinza))
				);

				botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_complete_placar_aleatoriamente)));
				botaoFinalizar.setTextColor(getResources().getColor(R.color.branco));
			} else {
				botaoFinalizar.setVisibility(View.VISIBLE);
				atualizarBotaoFinalizarAdicionarCarrinho();
			}
		} else if (parametroJogo.isTipoJogo(ModalidadeEnum.TIMEMANIA)) {
			if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado && isEcolhaTimeBtnAtivado) {
				botaoFinalizar.setBackgroundTintList(
						ColorStateList.valueOf(ContextCompat.getColor(parentActivity, typeGameColorDark))
				);
				if (mGridTimeCoracao != null) {
					botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.label_selecionar_time_aleatoriamente)));
					botaoFinalizar.setTextColor(getResources().getColor(R.color.branco));
					botaoFinalizar.setBackgroundTintList(
							ColorStateList.valueOf(getContext().getResources().getColor(R.color.cinza))
					);
				} else {
					botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.label_escolha_time_coracao)));
					botaoFinalizar.setTextColor(getResources().getColor(corFonteFundoEscuro));
					botaoFinalizar.setBackgroundTintList(
							ColorStateList.valueOf(ContextCompat.getColor(parentActivity, typeGameColorDark))
					);
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
					botaoFinalizar.setVisibility(View.GONE);
				}

			} else {
				botaoFinalizar.setVisibility(View.GONE);
				botaoFinalizar.setBackgroundTintList(
						ColorStateList.valueOf(ContextCompat.getColor(parentActivity, R.color.cinza))
				);

				botaoFinalizar.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.label_selecionar_times_aleatoriamente)));
				botaoFinalizar.setTextColor(getResources().getColor(R.color.branco));
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
						botaoFinalizar.setBackgroundTintList(
								ColorStateList.valueOf(ContextCompat.getColor(parentActivity, typeGameColorDark))
						);
					} else {
						botaoFinalizar.setClickable(false);
						botaoFinalizar.setBackgroundTintList(
								ColorStateList.valueOf(ContextCompat.getColor(parentActivity, R.color.azulclaro))
						);
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
		botaoFinalizar.setBackgroundTintList(
				ColorStateList.valueOf(ContextCompat.getColor(parentActivity, typeGameColorDark))
		);
		botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(stringId)));
		botaoFinalizar.setTextColor(getResources().getColor(corFonteFundoEscuro));
		verificaSeMostraSalvarApostaView();
	}

	private void atualizarBotaoFinalizarCompletarCartela() {
		botaoFinalizar.setBackgroundTintList(
				ColorStateList.valueOf(ContextCompat.getColor(parentActivity, R.color.cinza))
		);
		botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.completarCartela)));
		botaoFinalizar.setTextColor(getResources().getColor(R.color.branco));
		parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);
	}

	private void atualizarBotaoFinalizarAdicionarCarrinho() {
		botaoFinalizar.setBackgroundTintList(
				ColorStateList.valueOf(ContextCompat.getColor(parentActivity, typeGameColorDark))
		);
		botaoFinalizar.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.adicionarAoCarrinho)));
		botaoFinalizar.setTextColor(ContextCompat.getColor(parentActivity, corFonteFundoEscuro));
		if ((!parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA) && !parametroJogo.isTipoJogo(ModalidadeEnum.LOTOGOL)) && !parametroJogo.isTipoJogo(ModalidadeEnum.DIA_DE_SORTE)) {
			parentActivity.getSalvarApostaLayout().setVisibility(View.VISIBLE);
		}
	}

	private void configuraListaDezenas(View fragmentView) {
		listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
		recyclerViewPartida = fragmentView.findViewById(R.id.recyclerViewPartida);

		if (dezenas != null && dezenas.size() > 0 && !parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
			//simularDezenasAdapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
			//													typeGameColorDark, onItemClickListener());
			simularDezenasAdapter = new ListaDezenaRecyclerView(dezenas, dezenasSelecionadas,
																corFonteFundoBranco, onItemClickListener());
			listaDezenas.setAdapter(simularDezenasAdapter);

			RecyclerView.LayoutManager layot = new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA);
			listaDezenas.setLayoutManager(layot);
			listaDezenas.setNestedScrollingEnabled(false);
		} else {
			if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTECA)) {
				EscudoBO.getInstance().carregaEscudos(getContext(), new OnEscudoListener() {
					@Override
					public void aposCarregarEscudos() {
						configurarTimesLoteca();
					}
				});
				//configurarTimesLoteca();

				fragmentView.findViewById(R.id.layoutTitleLoteca).setVisibility(View.VISIBLE);
			}else if (parametroJogo.isTipoJogo(ModalidadeEnum.SUPER_7)) {
				configuraSuperSete(fragmentView);
			}
		}
	}

	@NotNull
	private OnItemClickListener<DezenaHolder> onItemClickListener() {
		return (OnItemClickListener<DezenaHolder>) (holder, position) -> {
			try {
				Dezena  dezena           = dezenas.get(position);
				dezena.setSelected(!dezena.isSelected());
				Integer valorSelecionado = Integer.valueOf(dezena.getValue());

				if (dezenasSelecionadas.contains(valorSelecionado)) {
					dezenasSelecionadas.remove(valorSelecionado);
					if (dezenasSelecionadas.size() >= parametroJogo.getQuantidadeMinima()) {
						parentActivity.qtdDezenasPossiveisSelecionado = dezenasSelecionadas.size();
						parentActivity.verificaNovoQtdDezenasMax(dezenasSelecionadas.size());
						verificaSeMostraBotaoLimparAposta();
					}
					if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA) && selecioneOutrosNumeros.isChecked()) {
						selecioneOutrosNumeros.setChecked(false);
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
							if 	((qtdDezenasPossiveisSelecionadoAtual == (parentActivity.qtdDezenasPossiveisSelecionado-1)) && (!isAvisou))
							{
								DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_ultrapassada));
								isAvisou = true;
							}
						} else {
							DialogUtils.dialogEntendi(getActivity(), getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
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

				simularDezenasAdapter.atualizaSelecionados(dezenasSelecionadas);

			} catch (Exception e){
				Log.d("", e.getLocalizedMessage());
			}
		};
	}

	private void configurarTimesLoteca() {
		lotecaAdapter = new LotecaAdapter(parametroJogo, this);
		recyclerViewPartida.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerViewPartida.setAdapter(lotecaAdapter);
		recyclerViewPartida.setExpanded(true);
	}

	private void configuraSuperSete(View viewLayout) {
		listaGridsSuperSete = cartelaModel.getListGridSuperSete(viewLayout);

		listaAdaptersSuperSete = cartelaModel.getListAdaptersSuperSete(typeGameColorDark, parentActivity);

		cartelaModel.preencheGridComListaSuperSete(listaGridsSuperSete, listaAdaptersSuperSete);

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
						DialogUtils.dialogEntendi(getActivity(),
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

									if 	((qtdTotalSelecionadosSuperSete == (parentActivity.qtdDezenasPossiveisSelecionado)) && (!isAvisou))
									{
										DialogUtils.dialogEntendi(getActivity(), getString(R.string.msg_qtd_max_ultrapassada));
										isAvisou = true;
									}
								} else {
									DialogUtils.dialogEntendi(getActivity(),getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
													"{qtdColunas}",
													String.valueOf(adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado))));
								}
							} else {
								DialogUtils.dialogEntendi(getActivity(),getString(R.string.msg_qtd_max_col_super_sete));
							}
						} else {
							DialogUtils.dialogEntendi(getActivity(),getString(R.string.label_nao_possivel_selecionar_quantidade_numeros_superior_limite_modalidade_loterica));
						}
					} else {
						if (listaSelecionados.size() < adapter.QTD_MAX_POR_COLUNA) {
							if (listaSelecionados.size() < adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado)) {
								listaSelecionados.add(valorSelecionado);
								qtdTotalSelecionadosSuperSete++;
							} else {
								DialogUtils.dialogEntendi(getActivity(),getString(R.string.msg_bloqueio_coluna_super_sete_marcar).replace(
												"{qtdColunas}",
												String.valueOf(adapter.getQtdColunasMaximas(parentActivity.qtdDezenasPossiveisSelecionado))));
							}
						} else {
							DialogUtils.dialogEntendi(getActivity(),getString(R.string.msg_qtd_max_col_super_sete));

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
		timeCoracaoAdapter = new TimeCoracaoAdapter(getActivity(), listaEquipefiltro, onTimeSelecionadoListener());
		mGridTimeCoracao.setLayoutManager(new GridLayoutManager(getContext(), 3));
		mGridTimeCoracao.setAdapter(timeCoracaoAdapter);
	}

	private OnItemClickListener onTimeSelecionadoListener() {
		return (holder, position) -> {
			if (equipeSelecionada != null && equipeSelecionada.isSelecionado()) {
				equipeSelecionada.setSelecionado(false);
			}
			equipeSelecionada = listaEquipefiltro.get(position);
			equipeSelecionada.setSelecionado(true);
			String nomeTime = String.format("%s-%s", equipeSelecionada.getNome(), equipeSelecionada.getUf());
			informeNomeTime.setText(nomeTime);
			aplicaCheckTimeSelecionado();
			listaEquipefiltro.clear();
			listaEquipefiltro.addAll(parametroJogo.getEquipes());
			timeCoracaoAdapter.notifyDataSetChanged();
			verificaSeMostraBotaoLimparAposta();
			verificaSeMostraSalvarApostaView();
			atualizarAdicionarCarinhoCompletarCartela(false);
			InputUtils.closeKeyboard(getActivity());
		};
	}

	@Override
	public boolean clickEquipeLoteca(ParametroPartida parametroPartida) {
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
		int somaTotais = 0;
		int valueSimples = Integer.parseInt((String.valueOf(Integer.parseInt(parentActivity.getLabelValueSimplesLoteca().getText().toString()))));
		int valueDuplas= Integer.parseInt((String.valueOf(Integer.parseInt(parentActivity.getLabelValueDuplasLoteca().getText().toString()))));
		int valueTriplas= Integer.parseInt((String.valueOf(Integer.parseInt(parentActivity.getLabelValueTriplasLoteca().getText().toString()))));
		somaTotais = valueSimples +valueDuplas + valueTriplas;

		parentActivity.setLabelValueJogosLoteca(String.valueOf(Integer.parseInt(String.valueOf(somaTotais))));

		valorTotalJogos = parentActivity.getLabelValueJogosLoteca();
		valorTotalSimples= parentActivity.getLabelValueSimplesLoteca();
		valorTotalDuplas= parentActivity.getLabelValueDuplasLoteca();
		valorTotalTriplas= parentActivity.getLabelValueTriplasLoteca();


		if (somaTotais >= 14) {
			valorTotalJogos.setTextColor(getResources().getColor(R.color.greyText));
			valorTotalSimples.setTextColor(getResources().getColor(R.color.greyText));
		} else {
			valorTotalJogos.setTextColor(getResources().getColor(R.color.loteca_numbers));
			valorTotalSimples.setTextColor(getResources().getColor(R.color.loteca_numbers));
		}
		if (valueDuplas  == 0 && valueTriplas == 0) {
			valorTotalDuplas.setTextColor(getResources().getColor(R.color.loteca_numbers));
			valorTotalTriplas.setTextColor(getResources().getColor(R.color.loteca_numbers));
		} else {
			valorTotalDuplas.setTextColor(getResources().getColor(R.color.greyText));
			valorTotalTriplas.setTextColor(getResources().getColor(R.color.greyText));
		}

		parentActivity.setContentDescriptionLoteca(
				parentActivity.getLabelValueJogosLoteca().getText().toString(),
				parentActivity.getLabelValueSimplesLoteca().getText().toString(),
				parentActivity.getLabelValueDuplasLoteca().getText().toString(),
				parentActivity.getLabelValueTriplasLoteca().getText().toString()
		);

		Boolean mostraBotao = atualizaValorApostaLoteca();
		atualizaStatusBotaoFinalizarLoteca(mostraBotao);
		verificaSeMostraBotaoLimparAposta();
		return true;
	}

	private void limparPartidadasSelecionasLoteca(boolean desativado) {
		for (ParametroPartida partida : parametroJogo.getPartidas()) {
			partida.setEmpate(desativado);
			partida.getEquipe1().setSelecionado(desativado);
			partida.getEquipe2().setSelecionado(desativado);
			partida.setSelecionado(desativado);
			partida.setQtdItensSelecionadosAnterior(0);
			partida.setQtdItensSelecionados(0);
		}
		recyclerViewPartida.getAdapter().notifyItemRangeChanged(
				0,
				recyclerViewPartida.getAdapter().getItemCount()
		);
		partidasSelecionadas.clear();
		parentActivity.zerarApostasLoteca();
		ocutarSalvarAposta();
		atualizaValorApostaLoteca();
		botaoFinalizar.setVisibility(View.GONE);
	}

	@Override
	public void partidaSelecionada() { }

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
				if (mostraLimparSuperSete()) {
					botaoLimparAposta.setVisibility(View.VISIBLE);
				} else {
					botaoLimparAposta.setVisibility(View.GONE);
				}
			} else {
				if (CollectionUtils.isNotEmpty(aposta.getListaNumerosSelecionados())) {
					dezenasSelecionadas = aposta.getListaNumerosSelecionados();
					if (parametroJogo.isTipoJogo(ModalidadeEnum.LOTOMANIA)) {
						if (dezenasSelecionadas.size() == parentActivity.qtdDezenasPossiveisSelecionado) {
							setVisibilityCheckedNumerosLayout(View.VISIBLE);
						} else {
							setVisibilityCheckedNumerosLayout(View.GONE);
						}
					}
					simularDezenasAdapter.atualizaSelecionados(dezenasSelecionadas);
					parentActivity.qtdDezenasPossiveisSelecionado = aposta.getQuantidadeNumeros();
					parentActivity.qtdConcursoSelecionado = aposta.getQuantidadeTeimosinhas();
					parentActivity.atualizaTextoBotoesQtdSelecionadas();
					equipeSelecionada = aposta.getTimeDoCoracao();
					if (equipeSelecionada != null) {
						isEcolhaTimeBtnAtivado = Boolean.TRUE;
					}
					atualizaStatusBotaoFinalizar(false);
					verificaSeMostraBotaoLimparAposta();
					verificaMostraRodape();
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
}