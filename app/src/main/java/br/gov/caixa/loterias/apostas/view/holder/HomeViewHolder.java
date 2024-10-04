package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.content.Context;
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
import android.os.CountDownTimer;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
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
	private TextView tempoRestanteParaAposta;
	private Button buttonActionResultadosModalidade;
	private RelativeLayout relativeLayoutTopResultados;
	private LinearLayout linearLayoutResultadosApostas;
	private RelativeLayout setaDireitaAcaoRelativeLayout;
	private RelativeLayout setaEsquerdaAcaoRelativeLayout;
	private ImageView imgSetaResultado;

	private EstiloModalidade estilo;
	private OnHomeListener listener;
	private Modalidade modalidade;
	private int position;
	private Activity parentActivity;

	public HomeViewHolder(View itemView, OnHomeListener listener, Activity parentActivity) {
		super(itemView);
		this.parentActivity = parentActivity;

		game = itemView.findViewById(R.id.textViewCardTitle);
		trevo = itemView.findViewById(R.id.trevoImagem);
		trevoFundo = itemView.findViewById(R.id.RelativeLayoutTrevo);
		calendario = itemView.findViewById(R.id.imageCalendar);
		relogio = itemView.findViewById(R.id.imageWatch);
		titulo = itemView.findViewById(R.id.linearLayoutTopModalidades);
		valorAcumulado = itemView.findViewById(R.id.textVieCardAcumulatedValue);
		premioEstimadoConcurso = itemView.findViewById(R.id.textPremioEstimadoConcurso);
		dataSorteio = itemView.findViewById(R.id.textVieCardDrawDate);
		textViewSorteioOuEncerramento = itemView.findViewById(R.id.textViewSorteioOuEncerramento);
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
		estilo = new EstiloModalidade(modalidade.getTipoModalidade());
		if (modalidade.getConcurso().getTipoConcurso().toString().equals(itemView.getContext().getResources().getString(R.string.especial_maiusculo))) {
			this.game.setText(modalidade.getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));
			this.tvNomeEspeciais.setText(modalidade.getConcurso().getModalidadeDetalhada().getDescricaoEspecial().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));
		} else {
			this.game.setText(ModalidadeEnum.fromString(modalidade.getTipoModalidade()));
			if (BuildVersionUtil.isAutomacao() && modalidade.getTipoModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
				this.tvNomeEspeciais.setText(R.string.label_mais_milionaria);
			}
		}

		this.relativeLayoutTopResultados.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorEscura()));
		this.linearLayoutResultadosApostas.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorEscura()));
		//TODO: Altera a cor do título do carrosel
		this.trevo.setBackground(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoBackground()));
		this.trevoFundo.setBackground(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoBackground()));
		this.titulo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorClara()));
		this.valorAcumulado.setTextColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorClara()));

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
				case MAIS_MILIONARIA:
					this.ivEspeciais.setImageDrawable(itemView.getContext().getDrawable(R.drawable.mais_milionaria));
					break;
				default:
					this.titulo.setVisibility(View.VISIBLE);
					this.clEspeciais.setVisibility(View.GONE);
			}
		}

		if (modalidade.getConcurso().getEstimativa().compareTo(BigDecimal.ZERO) != 0) {
			String estimativaFormatado = ViewUtils.getMoedaFormat(modalidade.getConcurso().getEstimativa());
			this.valorAcumulado.setText(estimativaFormatado);
			this.valorAcumulado.setTextSize(20);
			this.premioEstimadoConcurso.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(), itemView.getContext().getString(R.string.label_premio_estimado_concurso_numero_bold,
																											   modalidade.getConcurso().getNumero())));

			this.premioEstimadoConcurso.setVisibility(View.VISIBLE);
		} else {
			this.valorAcumulado.setText(retornaStringConcursoSemEstimativa(modalidade.getConcurso().getNumero().toString(), estilo.getCorClara()));
			this.valorAcumulado.setTextSize(14);

			this.premioEstimadoConcurso.setVisibility(View.INVISIBLE);
		}

		DateFormat formatterDataBr;
		DateFormat formatterDia;
		DateFormat formatterTime;

		DateFormat formatterDate = new SimpleDateFormat(itemView.getContext().getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));
		Locale BRAZIL = new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br));
		formatterDataBr = SimpleDateFormat.getDateInstance(DateFormat.SHORT, BRAZIL);
		formatterTime = new SimpleDateFormat(itemView.getContext().getResources().getString(R.string.hh_mm_ss));
		formatterDia = new SimpleDateFormat(itemView.getContext().getResources().getString(R.string.eeee));
		formatterDia =  formatterDia.getDateInstance(DateFormat.FULL ,BRAZIL);
		Date dataSorteioFormatada  = null;
		Date dataFechamentoSorteio = null;

		try {
			dataSorteioFormatada = formatterDate.parse(modalidade.getConcurso().getDataHoraSorteio());
			dataFechamentoSorteio = formatterDate.parse(modalidade.getConcurso().getDataFechamento());
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
		String dmy = formatterDataBr.format(dataSorteioFormatada);
		String dataDia[] = formatterDia.format(dataSorteioFormatada).split( itemView.getContext().getResources().getString(R.string.virgula));
		String dia_dmy = dataDia[0] + ",\n" + dmy;
		String horaMinSeg = formatterTime.format(dataSorteioFormatada);
		this.dataSorteio.setText(ViewUtils.capitalized(dia_dmy) +
										   itemView.getContext().getResources().getString(R.string.as_espacado) +
										   horaMinSeg);

		this.calendario.setImageResource(estilo.getImagemCalendario());
		this.relogio.setImageResource(estilo.getImagemRelogio());

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
			dataFechamentoSorteio = formatterDate.parse(modalidade.getConcurso().getDataFechamento());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (dataFechamentoSorteio != null) {
			long tempoRestanteMillis = dataFechamentoSorteio.getTime() - System.currentTimeMillis();

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
						tempoRestanteParaAposta.setText(itemView.getContext().getResources().getString(R.string.aposta_encerrada));
					}

				}.start();
			} else {
				tempoRestanteParaAposta.setText(itemView.getContext().getResources().getString(R.string.aposta_encerrada));
			}
		}

	}

	public void setOverlayColor(EstiloModalidade estilo, Button aposta) {
		aposta.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estilo.getCorClara()));
	}

	private SpannableStringBuilder retornaStringConcursoSemEstimativa(String numConcurso, int corModalidadeInt) {
		final SpannableStringBuilder sb = new SpannableStringBuilder(itemView.getContext().getResources().getString(R.string.concurso_barra_n_barra_n) +
																			 numConcurso +
																			 itemView.getContext().getResources().getString(R.string.barra_n_barra_n_ainda_sem_estimativa_de_premio));

		final ForegroundColorSpan corModalidade = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), corModalidadeInt));
		final ForegroundColorSpan corTexto      = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.cinzaescuro));

		sb.setSpan(corModalidade, 0, 7, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		sb.setSpan(corTexto, 10, 11 + numConcurso.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		sb.setSpan(corModalidade, 11 + numConcurso.length(), sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

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
						imgSetaResultado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_seta_cima_resultado));
					} else {
						animacaoAbrirResultados(itemView.getContext(),
												this.relativeLayoutTopResultados,
												this.linearLayoutResultadosApostas,
												position,
												this.setaEsquerdaAcaoRelativeLayout,
												this.setaDireitaAcaoRelativeLayout);
						imgSetaResultado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_seta_cima_resultado));
					}
				} else {
					modalidade.setResultadosAberto(false);
					animacaoFecharResultados(itemView.getContext(),
											 this.relativeLayoutTopResultados,
											 this.linearLayoutResultadosApostas,
											 this.setaEsquerdaAcaoRelativeLayout,
											 this.setaDireitaAcaoRelativeLayout);
					imgSetaResultado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_seta_baixo_resultado));
				}
				break;
		}
	}
}
