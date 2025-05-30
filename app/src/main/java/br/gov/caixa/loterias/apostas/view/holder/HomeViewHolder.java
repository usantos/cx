package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.DataHoraServidorSingleton;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.EncerramentoUtil;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.animation.AnimacoesResultados;
import br.gov.caixa.loterias.apostas.view.listener.OnHomeListener;

public class HomeViewHolder extends LoteriasHolder<Modalidade> implements View.OnClickListener {

	private View titulo;
	private TextView game;
	private ImageView relogio;
	private ImageView calendario;
	private TextView valorAcumulado;
	private ImageView ivEspeciais;
	private TextView tvNomeEspeciais;
	private ConstraintLayout clEspeciais;
	private ImageView trevo;
	private RelativeLayout trevoFundo;
	private TextView premioEstimadoConcurso;
	private TextView dataSorteio;
	private TextView textViewSorteioOuEncerramento;
	private TextView textViewApostasEncerramEm, tempoRestanteParaAposta;
	private Button buttonActionResultadosModalidade;
	private RelativeLayout relativeLayoutTopResultados;
	private LinearLayout linearLayoutResultadosApostas;
	private RelativeLayout setaDireitaAcaoRelativeLayout;
	private RelativeLayout setaEsquerdaAcaoRelativeLayout;
	private ImageView imgSetaResultado;
	private View viewSeparador;
	private LinearLayout linearCalendar;
	private LinearLayout linearRelogio;
	private ImageView imageInstantanea;
	private TextView textPortal;

	private EstiloModalidadeMKP estilo;
	private OnHomeListener listener;
	private Modalidade modalidade;
	private int position;
	private Activity parentActivity;

	private ImageView imgRelogioCard;
	private TextView tvContadorCard;
	private ConstraintLayout clTimer30Min;
	private TextView tvVoceTem;

	public HomeViewHolder(View itemView, OnHomeListener listener, Activity parentActivity) {
		super(itemView);
		this.parentActivity = parentActivity;

		game = itemView.findViewById(R.id.textViewCardTitle);
		trevo = itemView.findViewById(R.id.trevoImagem);
		trevoFundo = itemView.findViewById(R.id.RelativeLayoutTrevo);
		calendario = itemView.findViewById(R.id.imageCalendar);
		relogio = itemView.findViewById(R.id.imageWatch);
		imgRelogioCard = itemView.findViewById(R.id.img_relogio_card);
		tvContadorCard = itemView.findViewById(R.id.tv_contador_card);
		clTimer30Min = itemView.findViewById(R.id.cl_timer30Min);
		tvVoceTem = itemView.findViewById(R.id.tv_voce_tem);
		titulo = itemView.findViewById(R.id.linearLayoutTopModalidades);
		valorAcumulado = itemView.findViewById(R.id.textVieCardAcumulatedValue);
		viewSeparador = itemView.findViewById(R.id.viewSeparador);
		linearCalendar = itemView.findViewById(R.id.linearCalendar);
		linearRelogio = itemView.findViewById(R.id.linearRelogio);
		imageInstantanea = itemView.findViewById(R.id.imageInstantanea);
		textPortal = itemView.findViewById(R.id.textPortalInstantanea);
		premioEstimadoConcurso = itemView.findViewById(R.id.textPremioEstimadoConcurso);
		dataSorteio = itemView.findViewById(R.id.textVieCardDrawDate);
		textViewSorteioOuEncerramento = itemView.findViewById(R.id.textViewSorteioOuEncerramento);
		textViewApostasEncerramEm = itemView.findViewById(R.id.textVieCardApostasEncerramEm);
		tempoRestanteParaAposta = itemView.findViewById(R.id.textVieCardClosingOfBets);
		buttonActionResultadosModalidade = itemView.findViewById(R.id.buttonActionResultadosModalidade);
		relativeLayoutTopResultados = itemView.findViewById(R.id.relativeLayoutTopResultados);
		linearLayoutResultadosApostas = itemView.findViewById(R.id.linearLayoutResultadosApostas);
		ivEspeciais = itemView.findViewById(R.id.iv_especiais);
		tvNomeEspeciais = itemView.findViewById(R.id.tv_nome_especiais);
		clEspeciais = itemView.findViewById(R.id.cl_especiais);
		imgSetaResultado = itemView.findViewById(R.id.imgSetaResultado);

		setaEsquerdaAcaoRelativeLayout = itemView.findViewById(R.id.setaEsquerdaAcaoRelativeLayout);
		setaDireitaAcaoRelativeLayout = itemView.findViewById(R.id.setaDireitaAcaoRelativeLayout);

		this.listener = listener;
	}

	@Override
	public void bind(Modalidade modalidade, int position) {
		this.modalidade = modalidade;
		this.position = position;
		setIsRecyclable(false);
		estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade());

		this.game.setTextColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorFonteFundoClaro()));

		if (modalidade.getTipoModalidade().equals(ModalidadeEnum.INSTANTANEA)) {
			EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade());
			this.game.setText(ModalidadeEnum.fromString(modalidade.getTipoModalidade()));
			this.relativeLayoutTopResultados.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorEscura()));
			this.linearLayoutResultadosApostas.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorEscura()));
			//TODO: Altera a cor do título do carrosel
			this.trevo.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoFundoEscuro()));
			this.trevoFundo.setBackground(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrapezio()));
			this.titulo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorClara()));
			this.valorAcumulado.setVisibility(View.INVISIBLE);
			this.premioEstimadoConcurso.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(), itemView.getContext().getString(R.string.instantanea_texto_card)));
			this.viewSeparador.setVisibility(View.GONE);
			this.linearCalendar.setVisibility(View.GONE);
			this.linearRelogio.setVisibility(View.GONE);
			this.imageInstantanea.setVisibility(View.VISIBLE);
			this.textPortal.setVisibility(View.VISIBLE);
			this.textPortal.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(), itemView.getContext().getString(R.string.portal_instantanea)));
			this.buttonActionResultadosModalidade.setVisibility(View.GONE);
			this.imgSetaResultado.setVisibility(View.GONE);

			return;
		}

		if (modalidade.getConcurso().getTipoConcurso().toString().equals(itemView.getContext().getResources().getString(R.string.especial_maiusculo))) {
			this.game.setText(modalidade.getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));
			this.tvNomeEspeciais.setText(modalidade.getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));
		} else {
			this.game.setText(ModalidadeEnum.fromString(modalidade.getTipoModalidade()));
			if (BuildVersionUtil.isAutomacao() && modalidade.getTipoModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
				this.tvNomeEspeciais.setText(R.string.label_mais_milionaria);
			}
		}

		this.buttonActionResultadosModalidade.setTextColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorFonteFundoEscuro()));
		this.imgSetaResultado.setImageDrawable(VectorUtils.getShape(R.drawable.ic_seta_baixo_resultado, estilo.getCorFonteFundoEscuro()));

		this.relativeLayoutTopResultados.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorEscura()));
		this.linearLayoutResultadosApostas.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorEscura()));
		//TODO: Altera a cor do título do carrosel
		//this.trevo.setBackground(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoFundoEscuro()));
		this.trevo.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoFundoEscuro()));
		this.trevoFundo.setBackground(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrapezio()));
		this.titulo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorClara()));
		this.valorAcumulado.setTextColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorLetraLista()));

		if (modalidade.getConcurso().getTipoConcurso().toString().equals(itemView.getContext().getResources().getString(R.string.especial_maiusculo)) || modalidade.getConcurso().getModalidade() == ModalidadeEnum.MAIS_MILIONARIA) {
			this.titulo.setVisibility(View.GONE);
			this.clEspeciais.setVisibility(View.VISIBLE);
			switch (modalidade.getTipoModalidade()){
				case MEGA_SENA:
					this.ivEspeciais.setImageDrawable(itemView.getContext().getDrawable(R.drawable.mega_virada));
					break;
				case DUPLA_SENA:
					this.ivEspeciais.setImageDrawable(itemView.getContext().getDrawable(R.drawable.cabecalho_superior_dupla_de_pascoa));
					break;
				case LOTOFACIL:
					this.ivEspeciais.setImageDrawable(itemView.getContext().getDrawable(R.drawable.cabecalho_lotofacil_da_independencia));
					break;
				case QUINA:
					this.ivEspeciais.setImageDrawable(itemView.getContext().getDrawable(R.drawable.cabecalho_sao_joao));
					break;
				default:
					this.titulo.setVisibility(View.VISIBLE);
					this.clEspeciais.setVisibility(View.GONE);
			}
		}

		if (modalidade.getConcurso().getEstimativa().compareTo(BigDecimal.ZERO) != 0) {
			String estimativaFormatado = ViewUtils.getMoedaFormatPorExtenso(modalidade.getConcurso().getEstimativa());
			this.valorAcumulado.setText(estimativaFormatado);
			this.valorAcumulado.setTextSize(20);
			this.premioEstimadoConcurso.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(), itemView.getContext().getString(R.string.label_premio_estimado_concurso_numero_bold,
																											   modalidade.getConcurso().getNumero())));
			this.premioEstimadoConcurso.setVisibility(View.VISIBLE);
		} else {
			this.valorAcumulado.setTextColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorLetraLista()));
			this.valorAcumulado.setText(retornaStringConcursoSemEstimativa(modalidade.getConcurso().getNumero().toString(), estilo.getCorLetraLista()));
			this.valorAcumulado.setTextSize(14);

			this.premioEstimadoConcurso.setVisibility(View.INVISIBLE);
		}

		DateFormat formatterDataBr;
		DateFormat formatterDia;
		DateFormat formatterTime;

		String formatoData = itemView.getContext().getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss);
		DateFormat formatterDate = new SimpleDateFormat(formatoData);
		Locale BRAZIL = new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br));
		formatterDataBr = SimpleDateFormat.getDateInstance(DateFormat.SHORT, BRAZIL);
		formatterTime = new SimpleDateFormat(itemView.getContext().getResources().getString(R.string.hh_mm_ss));
		formatterDia = new SimpleDateFormat(itemView.getContext().getResources().getString(R.string.eeee));
		formatterDia =  formatterDia.getDateInstance(DateFormat.FULL ,BRAZIL);
		Date dataSorteioFormatada  = null;
		DateTime dataFechamentoSorteio = null;

		try {
			if (modalidade.getConcurso().getDataHoraSorteio() != null) {
				dataSorteioFormatada = formatterDate.parse(modalidade.getConcurso().getDataHoraSorteio());
			}
			dataFechamentoSorteio = DateUtils.criaDataHora(modalidade.getConcurso().getDataFechamento(), formatoData);
			if (modalidade.getTipoModalidade() == ModalidadeEnum.LOTECA){
				dataSorteioFormatada = formatterDate.parse(modalidade.getConcurso().getDataFechamento());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (modalidade.getTipoModalidade() == ModalidadeEnum.LOTECA ||
				modalidade.getTipoModalidade() == ModalidadeEnum.LOTOGOL) {
			this.textViewSorteioOuEncerramento.setText(R.string.label_encerramento);
		} else {
			this.textViewSorteioOuEncerramento.setText(R.string.label_sorteio_case);

		}
		if (dataSorteioFormatada != null) {
			String dmy = formatterDataBr.format(dataSorteioFormatada);
			String dataDia[] = formatterDia.format(dataSorteioFormatada).split(itemView.getContext().getResources().getString(R.string.virgula));
			String dia_dmy = dataDia[0] + ",\n" + dmy;
			String horaMinSeg = formatterTime.format(dataSorteioFormatada);
			this.dataSorteio.setText(ViewUtils.capitalized(dia_dmy) +
					itemView.getContext().getResources().getString(R.string.as_espacado) +
					horaMinSeg);
		} else {
			this.linearCalendar.setVisibility(View.GONE);
		}

//		this.calendario.setImageResource(estilo.getImagemCalendario());
//		this.relogio.setImageResource(estilo.getImagemRelogio());
		this.calendario.setImageDrawable(VectorUtils.getShape(R.drawable.ic_calendario, estilo.getCorLetraLista()));
		this.relogio.setImageDrawable(VectorUtils.getShape(R.drawable.icon_horario_megasena, estilo.getCorLetraLista()));

		this.buttonActionResultadosModalidade.setOnClickListener(this);
		this.imgSetaResultado.setOnClickListener(this);

		if (modalidade.getResultadosAberto()) {
			ViewGroup.LayoutParams layoutParams = this.linearLayoutResultadosApostas.getLayoutParams();
			layoutParams.height = ViewUtils.getRealScreenSize(itemView.getContext()).y - ViewUtils.setDisplayMetric(250, itemView.getContext());
			layoutParams.width = ViewUtils.setDisplayMetric(280, itemView.getContext());
			this.linearLayoutResultadosApostas.setLayoutParams(layoutParams);

			//this.setaDireitaAcaoRelativeLayout.setVisibility(View.VISIBLE);
			//this.setaEsquerdaAcaoRelativeLayout.setVisibility(View.VISIBLE);
		} else {
			ViewGroup.LayoutParams layoutParams = this.linearLayoutResultadosApostas.getLayoutParams();
			layoutParams.height = 0;
			this.linearLayoutResultadosApostas.setLayoutParams(layoutParams);
		}

		if (modalidade.getResultadoConcursoDTO() != null) {
			listener.setLayoutResultado(this.linearLayoutResultadosApostas, position);
		} else {
			this.linearLayoutResultadosApostas.removeAllViews();
		}

		this.setaEsquerdaAcaoRelativeLayout.setOnClickListener(this);
		this.setaDireitaAcaoRelativeLayout.setOnClickListener(this);

		try {
			dataFechamentoSorteio = DateUtils.criaDataHora(modalidade.getConcurso().getDataFechamento(), formatoData);

//			EncerramentoUtil.mockSetDataHoraEncerramento(
//					"06/05/2025",
//					"10:30:00",
//					"11:45:00",
//					ModalidadeEnum.MEGA_SENA, TipoConcursoEnum.NORMAL, 5663);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataFechamentoSorteio != null) {
			long tempoRestanteMillis = dataFechamentoSorteio.getMillis() - DataHoraServidorSingleton.getInstance().getDataHoraServidorMillis();

			if (tempoRestanteMillis > 0) {
				new CountDownTimer(tempoRestanteMillis, 1000) {

					public void onTick(long millisUntilFinished) {
						long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
						millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

						long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
						millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

						long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
						millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

						long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

						String tempoRestante;
						if (days > 0) {
							tempoRestante = days + (days == 1 ? " dia" : " dias");
						} else if (hours > 0) {
							tempoRestante = hours + (hours == 1 ? " hora" : " horas") + " e " + minutes + (minutes == 1 ? " minuto" : " minutos");
						} else if (minutes > 0) {
							tempoRestante = minutes + (minutes == 1 ? " minuto" : " minutos");
						} else {
							tempoRestante = seconds + (seconds == 1 ? " segundo" : " segundos");
						}

						tempoRestanteParaAposta.setText(tempoRestante);
					}

					public void onFinish() {
						textViewApostasEncerramEm.setText(itemView.getContext().getResources().getString(R.string.apostas));
						tempoRestanteParaAposta.setText(itemView.getContext().getResources().getString(R.string.aposta_encerrada));
						verificaTempoExtraAposEncerramento();
					}

				}.start();
			} else {
				textViewApostasEncerramEm.setText(itemView.getContext().getResources().getString(R.string.apostas));
				tempoRestanteParaAposta.setText(itemView.getContext().getResources().getString(R.string.aposta_encerrada));
				verificaTempoExtraAposEncerramento();
			}
		}

	}

	public void setOverlayColor(EstiloModalidadeMKP estilo, Button aposta) {
		aposta.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorClara()));
	}

	private SpannableStringBuilder retornaStringConcursoSemEstimativa(String numConcurso, int corModalidadeInt) {
		final SpannableStringBuilder sb = new SpannableStringBuilder(itemView.getContext().getResources().getString(R.string.proximo_concurso_barra_n_barra_n) +
																		  numConcurso +
																		  itemView.getContext().getResources().getString(R.string.barra_n_barra_n_aguardando_estimativa_de_premio));

		int tamT1 = itemView.getContext().getResources().getString(R.string.proximo_concurso_barra_n_barra_n).length();
		int tamT2 = numConcurso.length();
		int tamT3 = itemView.getContext().getResources().getString(R.string.barra_n_barra_n_aguardando_estimativa_de_premio).length();

		final ForegroundColorSpan corModalidade = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), corModalidadeInt));
		final ForegroundColorSpan corTexto      = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.cinzaescuro));

		sb.setSpan(corModalidade, 0, tamT1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		sb.setSpan(corTexto, tamT1, tamT1+tamT2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		sb.setSpan(corModalidade, tamT1+tamT2, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		return sb;
	}

	private Map<TimeUnit, Long> computeDiff(Date date1, Date date2) {
		long           diffInMillies = date2.getTime() - date1.getTime();
		List<TimeUnit> units         = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);
		Map<TimeUnit, Long> result = new LinkedHashMap<>();
		long milliesRest = diffInMillies;
		for (TimeUnit unit : units) {
			long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit, diff);
		}
		return result;
	}

	private String retornaStringDias(Long dia) {
		if (dia > 0) {
			if (dia == 1) {
				return String.format(Locale.getDefault(), itemView.getContext().getResources().getString(R.string.percent_d_dia), dia);
			}
			return String.format(Locale.getDefault(), itemView.getContext().getResources().getString(R.string.percent_d_dias), dia);
		}
		return itemView.getContext().getResources().getString(R.string.string_vazia);
	}

	private String retornaStringHoras(Long dia, Long hora, Activity activity) {
		String string;
		string = dia > 0 ? itemView.getContext().getResources().getString(R.string.espaco_e_espaco) : itemView.getContext().getResources().getString(R.string.string_vazia);

		if (hora > 0) {
			if (hora == 1) {
				return string + String.format(Locale.getDefault(), itemView.getContext().getResources().getString(R.string.percent_d_hora), hora);
			}
			return string + String.format(Locale.getDefault(), itemView.getContext().getResources().getString(R.string.percent_d_horas), hora);
		} else {
			if (dia == 0) {
				return activity.getResources().getString(R.string.menos_uma_hora);
			}
		}
		return itemView.getContext().getResources().getString(R.string.string_vazia);
	}

	public void animacaoAbrirResultados(Context context,
										 RelativeLayout linearLayoutTopResultados,
										 LinearLayout linearLayoutResultadosApostas,
										 int position,
										 RelativeLayout setaEsquerdaAcaoRelativeLayout,
										 RelativeLayout setaDireitaAcaoRelativeLayout) {
		AnimacoesResultados animacoesResultados = new AnimacoesResultados(
				linearLayoutTopResultados, linearLayoutResultadosApostas,
				context,
				ViewUtils.setDisplayMetric(240, context),
				ViewUtils.setDisplayMetric(280, context)
		);
		modalidade.setResultadosAberto(true);
		animacoesResultados.AnimacaoDescerContent(ViewUtils.getRealScreenSize(context).y - ViewUtils.setDisplayMetric(315, context));
		//setaDireitaAcaoRelativeLayout.setVisibility(View.VISIBLE);
		//setaEsquerdaAcaoRelativeLayout.setVisibility(View.VISIBLE);
	}

	private void animacaoFecharResultados(Context context,
										  RelativeLayout linearLayoutTopResultados,
										  LinearLayout linearLayoutResultadosApostas,
										  RelativeLayout setaEsquerdaAcaoRelativeLayout,
										  RelativeLayout setaDireitaAcaoRelativeLayout) {
		AnimacoesResultados animacoesResultados = new AnimacoesResultados(
				linearLayoutTopResultados,
				linearLayoutResultadosApostas,
				context,
				ViewUtils.setDisplayMetric(240, context),
				ViewUtils.setDisplayMetric(280, context)
		);

		animacoesResultados.AnimacaoSubirContent();
		//setaDireitaAcaoRelativeLayout.setVisibility(View.INVISIBLE);
		//setaEsquerdaAcaoRelativeLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.setaEsquerdaAcaoRelativeLayout:
				listener.callWebserviceResultadoConcurso(this.relativeLayoutTopResultados,
									 this.linearLayoutResultadosApostas,
									 ApostaDTO.lowerCaseFromString(modalidade.getTipoModalidade()),
									 modalidade.getConcurso().getNumero() - 1,
									 position,
									 this.setaEsquerdaAcaoRelativeLayout,
									 this.setaDireitaAcaoRelativeLayout,
									 false, this);
				break;
			case R.id.setaDireitaAcaoRelativeLayout:
				listener.callWebserviceResultadoConcurso(this.relativeLayoutTopResultados,
									 this.linearLayoutResultadosApostas,
									 ApostaDTO.lowerCaseFromString(modalidade.getTipoModalidade()),
									 modalidade.getConcurso().getNumero() + 1,
									 position,
									 this.setaEsquerdaAcaoRelativeLayout,
									 this.setaDireitaAcaoRelativeLayout,
									 false, this);
				break;
			case R.id.imgSetaResultado:
			case R.id.buttonActionResultadosModalidade:
				if (this.linearLayoutResultadosApostas.getHeight() == 0) {
					if (modalidade.getResultadoConcursoDTO() == null) {
						listener.callWebserviceResultadoConcurso(this.relativeLayoutTopResultados,
														this.linearLayoutResultadosApostas,
														ApostaDTO.lowerCaseFromString(modalidade.getTipoModalidade()),
														modalidade.getConcurso().getNumero(), position,
														this.setaEsquerdaAcaoRelativeLayout,
														this.setaDireitaAcaoRelativeLayout,
														true, this);
						//imgSetaResultado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_seta_cima_resultado));
						imgSetaResultado.setImageDrawable(VectorUtils.getShape(R.drawable.ic_seta_cima_resultado, estilo.getCorFonteFundoEscuro()));

					} else {
						animacaoAbrirResultados(itemView.getContext(),
												this.relativeLayoutTopResultados,
												this.linearLayoutResultadosApostas,
												position,
												this.setaEsquerdaAcaoRelativeLayout,
												this.setaDireitaAcaoRelativeLayout);
						//imgSetaResultado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_seta_cima_resultado));
						imgSetaResultado.setImageDrawable(VectorUtils.getShape(R.drawable.ic_seta_cima_resultado, estilo.getCorFonteFundoEscuro()));

					}
				} else {
					modalidade.setResultadosAberto(false);
					animacaoFecharResultados(itemView.getContext(),
											 this.relativeLayoutTopResultados,
											 this.linearLayoutResultadosApostas,
											 this.setaEsquerdaAcaoRelativeLayout,
											 this.setaDireitaAcaoRelativeLayout);
					//imgSetaResultado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_seta_baixo_resultado));
					imgSetaResultado.setImageDrawable(VectorUtils.getShape(R.drawable.ic_seta_baixo_resultado, estilo.getCorFonteFundoEscuro()));

				}
				break;
		}
	}

	private void verificaTempoExtraAposEncerramento() {

		Long tempoExtraRestanteAposEncerrmento = EncerramentoUtil.
												 tempoExtraRestanteAposEncerramento(modalidade.getTipoModalidade(),
														 modalidade.getConcurso().getTipoConcurso(),
														 modalidade.getConcurso().getNumero());
		if (tempoExtraRestanteAposEncerrmento != null) {
			startTimerPosEncerramento(tempoExtraRestanteAposEncerrmento);
		}
	}

	private void startTimerPosEncerramento(Long tempoRestanteMillis) {

		linearRelogio.setVisibility(View.GONE);
		clTimer30Min.setVisibility(View.VISIBLE);
		if (!BuildConfig.FLAVOR.equals("prd")) {
			ModalidadeDisponivelCota modalidadeDisponivelCota = EncerramentoUtil.
					getModalidadeDisponivelCota(modalidade.getTipoModalidade(),
							modalidade.getConcurso().getTipoConcurso(),
							modalidade.getConcurso().getNumero());
			tvVoceTem.setText(modalidadeDisponivelCota.getDataEncerramentoRevenda().substring(0,5) + " " +
					modalidadeDisponivelCota.getHoraEncerramentoRevenda().substring(0,5) + "\nVocê tem");
		}
		new CountDownTimer(tempoRestanteMillis, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

				Long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
				millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
				Long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
				millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);
				Long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

				String tempoRestante;
				if (hours > 0) {
					tempoRestante = "HH:MM:SS";
					tempoRestante = tempoRestante.replace("HH", StringUtils.padZeroLeft(hours.intValue(), 2));
				} else {
					tempoRestante = "MM:SS";
				}
				tempoRestante = tempoRestante.replace("MM", StringUtils.padZeroLeft(minutes.intValue(), 2));
				tempoRestante = tempoRestante.replace("SS", StringUtils.padZeroLeft(seconds.intValue(), 2));

				tvContadorCard.setText(tempoRestante);

				if (hours <= 0) {
					if (minutes < 5) {
						imgRelogioCard.setImageDrawable(VectorUtils.getShape(R.drawable.icon_horario_megasena, R.color.vermelho_dialog));
						tvContadorCard.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.vermelho_dialog));
					} else if (minutes < 10){
						imgRelogioCard.setImageDrawable(VectorUtils.getShape(R.drawable.icon_horario_megasena, R.color.jr_amarelo));
						tvContadorCard.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jr_amarelo));
					}
				}
			}

			@Override
			public void onFinish() {
				refazCarrossel();
			}
		}.start();
	}

	private void refazCarrossel() {
		parentActivity.recreate();
	}
}
