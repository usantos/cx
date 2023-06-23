package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;

import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.ApostasConfirmadasFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnApostaConfirmadaListener;

public class ApostaConfirmadaHolder extends LoteriasHolder<ApostaDTO> implements View.OnClickListener {
	private TextView dataApostaTxt;
	private View stripe;
	private TextView modalidadeTxt;
	private TextView concursoTxt;
	private View reloadBtn;
	private ImageView setaImg;
	private LinearLayout reloadBtnLayout;
	private LinearLayout setaLayout;
	private TextView situacaoTxt;
	private Button btnAposteAqui;
	private ProgressBar progressBarSituacao;
	private int color;
	private OnApostaConfirmadaListener onApostasConfirmadasListener;
	private Activity parentActivity;
	private ApostasConfirmadasFragment fragment;

	public ApostaConfirmadaHolder(View view, OnApostaConfirmadaListener listener, boolean listaVazia, Activity parentActivity, ApostasConfirmadasFragment fragment) {
		super(view);
		this.onApostasConfirmadasListener = listener;
		this.parentActivity = parentActivity;
		this.fragment = fragment;

		if (!listaVazia) {
			stripe = view.findViewById(R.id.stripe);
			modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
			dataApostaTxt = view.findViewById(R.id.dataApostaTxt);
			concursoTxt = view.findViewById(R.id.concursoTxt);
			reloadBtn = view.findViewById(R.id.reloadBtn);
			setaImg = view.findViewById(R.id.setaImg);
			setaLayout = view.findViewById(R.id.setaLayout);
			reloadBtnLayout = view.findViewById(R.id.reloadBtnLayout);
			situacaoTxt = view.findViewById(R.id.situacaoTxt);
			progressBarSituacao = view.findViewById(R.id.progressBarSituacao);

			setaLayout.setOnClickListener(this);
			setaImg.setOnClickListener(this);
			reloadBtnLayout.setOnClickListener(this);
			reloadBtn.setOnClickListener(this);
		} else {
			btnAposteAqui = view.findViewById(R.id.btnAposteAquiApostasConfirmadas);

			btnAposteAqui.setOnClickListener(this);
		}

		view.setOnClickListener(this);
	}

	@Override
	public void bind(ApostaDTO aposta, int position) {
		EstiloModalidade estiloModalidade = new EstiloModalidade(aposta.getModalidade());

		int color = ContextCompat.getColor(itemView.getContext(), estiloModalidade.getCorEscura());

		String descricaoStatus = getDescricaoString(aposta, position);

		this.color = color;
		this.stripe.setBackgroundColor(color);
		this.modalidadeTxt.setTextColor(color);
		this.dataApostaTxt.setText(aposta.getDataCompra());
		this.dataApostaTxt.setTextColor(color);
		this.modalidadeTxt.setText(ViewUtils.getNomeModalidadePorAposta(aposta));
		this.situacaoTxt.setTextColor(color);
		this.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_espaco) + descricaoStatus.toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));

		trataStyleSituacao(aposta);
		preencheTeimosinha(aposta);

		if (precisaConferirSituacaoAutomaticamente(descricaoStatus)){
			fragment.conferirResultado(position, aposta, this);
		}
	}

	private String getDescricaoString(ApostaDTO aposta, int position) {
		String descricaoStatus;

		this.showProgressBarSituacao(false);

		ParametroSimulacao parametroJogo = getParametroPorJogo(aposta, TipoConcursoEnum.NORMAL);
		if (parametroJogo == null) {
			parametroJogo = getParametroPorJogo(aposta, TipoConcursoEnum.ESPECIAL);
		}

		if(parametroJogo != null && !precisaBuscarResultadoNoSilce(aposta, parametroJogo)){
			if (ConfiguracaoAppSingleton.getInstance().isMicroServico()){
				descricaoStatus = aposta.getSituacao().getDescricao();
			}else {
				descricaoStatus = itemView.getContext().getResources().getString(R.string.nao_apurado);

				if (!BuildVersionUtil.isPRD()){
					descricaoStatus = "* " +descricaoStatus + " *";
				}
			}
			String msgLog = "Chama: false" + " = MP: " + parametroJogo.getParametroJogo().getConcurso().getModalidade() + "-" + parametroJogo.getParametroJogo().getConcurso().getNumero() + " = " + aposta.getModalidade() + "-" + aposta.getConcursoInicial() + " = diff: " + (parametroJogo.getParametroJogo().getConcurso().getNumero() - aposta.getConcursoInicial());
			Log.d("RESULTADO-APOSTA", msgLog);
		}else {
			// 4 = aposta efetivada
			if (aposta.getSituacao().getValor() == 4) {
				this.situacaoTxt.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.azulescuroapostafavorita));
				descricaoStatus = itemView.getContext().getResources().getString(R.string.a_conferir);

				conferirResultado(position, aposta);
				if (parametroJogo != null) {
					String msgLog = "Chama: true" + " = MP: " + parametroJogo.getParametroJogo().getConcurso().getModalidade() + "-" + parametroJogo.getParametroJogo().getConcurso().getNumero() + " = " + aposta.getModalidade() + "-" + aposta.getConcursoInicial() + " = diff: " + (parametroJogo.getParametroJogo().getConcurso().getNumero() - aposta.getConcursoInicial());
					Log.d("RESULTADO-APOSTA", msgLog);
				} else {
					Log.d("RESULTADO-APOSTA", "Chama: true");
				}
			} else {
				descricaoStatus = aposta.getSituacao().getDescricao();
				String msgLog = "Chama: false" + " - JÁ TEMOS O RESULTADO";
				Log.d("RESULTADO-APOSTA", msgLog);
			}
		}
		return descricaoStatus;
	}

	private void conferirResultado(int position, ApostaDTO aposta) {
		this.showProgressBarSituacao(true);
		fragment.conferirResultado(position, aposta, this);
	}

	private boolean precisaBuscarResultadoNoSilce(ApostaDTO aposta, ParametroSimulacao parametroJogo) {
		boolean precisaBuscar = false;

		Integer numeroConcursoAposta = aposta.getConcursoInicial();
		ConcursoDTO concursoAtual         = parametroJogo.getParametroJogo().getConcurso();

		if (numeroConcursoAposta < concursoAtual.getNumero()){
			if(numeroConcursoAposta + 1 == concursoAtual.getNumero()){
				if (concursoAtual.getAberto()){
					precisaBuscar = true;
				} else {
					precisaBuscar = false;
				}
			}else {
				precisaBuscar = true;
			}
		}
		return precisaBuscar;
	}

	private ParametroSimulacao getParametroPorJogo(ApostaDTO aposta, TipoConcursoEnum tipoConcurso) {
		ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
		ParametroSimulacao parametroSimulacao = null;
		for (ParametroSimulacao parametro : parametrosSimulacao.getParametros()){
			if(aposta.getModalidade() == parametro.getParametroJogo().getConcurso().getModalidade() && parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso){
				parametroSimulacao = parametro;
				break;
			}
		}

		return parametroSimulacao;
	}

	private void trataStyleSituacao(ApostaDTO aposta) {
		if (aposta.getSituacao().getValor() != 100) {
			this.situacaoTxt.setTypeface(null, Typeface.NORMAL);
			this.situacaoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			//holder.modalidadeTxt.setTypeface(null, Typeface.NORMAL);
		}else {
			this.situacaoTxt.setTypeface(null, Typeface.BOLD);
		}
	}

	private void preencheTeimosinha(ApostaDTO aposta) {
		int teimosinhas = aposta.getQuantidadeTeimosinhas() != null ? aposta.getQuantidadeTeimosinhas() : 0;
		if (teimosinhas > 0) {
			int quantidade = aposta.getConcursoInicial() + (teimosinhas - 1);
			this.concursoTxt.setText(itemView.getContext().getString(R.string.label_aposta_concursos,
														 aposta.getConcursoInicial(),
														 quantidade));
		} else {
			this.concursoTxt.setText(itemView.getContext().getString(R.string.label_aposta_concurso, aposta.getConcursoInicial()));
		}
	}

	public void showProgressBarSituacao(boolean show){
		if (show){
			this.progressBarSituacao.setVisibility(View.VISIBLE);
		}else {
			this.progressBarSituacao.setVisibility(View.INVISIBLE);
		}
	}

	public boolean precisaConferirSituacao(){
		if (this.situacaoTxt != null && this.situacaoTxt.getText().toString().contains(itemView.getContext().getString(R.string.a_conferir))){
			return true;
		}
		return false;
	}

	public boolean precisaConferirSituacaoAutomaticamente(String descricaoStatus){
		if (ConfiguracaoAppSingleton.getInstance().isMicroServico() && descricaoStatus != null
				&& descricaoStatus.equalsIgnoreCase(parentActivity.getResources().getString(R.string.label_efetivada))){
			return true;
		}
		return false;
	}

	public void handle(int position, ApostaConfirmadaHolder holder, DTOEnumLong situacao) {
		holder.stripe.setBackgroundColor(holder.color);
		holder.modalidadeTxt.setTextColor(holder.color);
		holder.situacaoTxt.setTextColor(holder.color);

		if (situacao.getDescricao().equalsIgnoreCase(itemView.getContext().getResources().getString(R.string.premiada_maiusculo))) {
			holder.situacaoTxt.setTypeface(null, Typeface.BOLD);
			holder.situacaoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			holder.situacaoTxt.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(),itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_underline) + situacao.getDescricao().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))) + itemView.getContext().getResources().getString(R.string.underline)));
		} else {
			holder.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_espaco)
											 + situacao.getDescricao().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt),
																							  itemView.getContext().getResources().getString(R.string.br))));
		}

		holder.progressBarSituacao.setVisibility(View.INVISIBLE);
	}

	public void handleError(ApostaConfirmadaHolder holder, VolleyError error) {
		this.situacaoTxt.setText(itemView.getResources().getString(R.string.nao_foi_possivel_conferir_o_resultado));
		this.progressBarSituacao.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View view) {
		if (onApostasConfirmadasListener != null) {
			if (view.getId() == R.id.row_apostas_confirmadas_empty || view.getId() == R.id.btnAposteAquiApostasConfirmadas) {
				onApostasConfirmadasListener.onEmptyCellClick();
			} else {

				switch (view.getId()) {
					case R.id.reloadBtn:
					case R.id.reloadBtnLayout: {
						onApostasConfirmadasListener.onReload(getAdapterPosition());
						break;
					}
					case R.id.setaLayout:
					case R.id.setaImg: {
						onApostasConfirmadasListener.onItemClick(getAdapterPosition(), this);
						break;
					}
					default:
						onApostasConfirmadasListener.onItemClick(getAdapterPosition(), this);
						break;
				}
			}
		}
	}
}
