package br.gov.caixa.loterias.apostas.view.holder;

import android.graphics.Typeface;
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
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostas.view.listener.OnApostaConfirmadaListener;

public class ApostaConfirmadaHolder extends LoteriasHolder<ApostaDTO> implements View.OnClickListener, ConferirResultadoHandler {
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
	private MinhasApostasActivity parentActivity;
	private OnApostaConfirmadaListener onApostasConfirmadasListener;

	public ApostaConfirmadaHolder(View view, OnApostaConfirmadaListener listener, boolean listaVazia) {
		super(view);
		this.onApostasConfirmadasListener = listener;
		this.parentActivity = parentActivity;

		if (!listaVazia) {
			stripe = view.findViewById(R.id.stripe);
			modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
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
	public void bind(ApostaDTO aposta) {
		EstiloModalidade estiloModalidade = new EstiloModalidade(aposta.getModalidade());

		int color = ContextCompat.getColor(itemView.getContext(), estiloModalidade.getCorEscura());

		String descricaoStatus = getDescricaoString(aposta);

		this.color = color;
		this.stripe.setBackgroundColor(color);
		this.modalidadeTxt.setTextColor(color);
		this.modalidadeTxt.setText(ViewUtils.getNomeModalidadePorAposta(aposta));
		this.situacaoTxt.setTextColor(color);
		this.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_espaco) + descricaoStatus.toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));

		trataStyleSituacao(aposta);
		preencheTeimosinha(aposta);
	}

	private String getDescricaoString(ApostaDTO aposta) {
		String descricaoStatus;

		this.showProgressBarSituacao(false);

		ParametroSimulacao parametroJogo = getParametroPorJogo(aposta);
		if(!precisaBuscarResultadoNoSilce(aposta, parametroJogo)){
			descricaoStatus = itemView.getContext().getResources().getString(R.string.nao_apurado);
		}else {
			// 4 = aposta efetivada
			if (aposta.getSituacao().getValor() == 4) {
				this.situacaoTxt.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.azulescuroapostafavorita));
				descricaoStatus = itemView.getContext().getResources().getString(R.string.a_conferir);
			} else {
				descricaoStatus = aposta.getSituacao().getDescricao();
			}
		}
		return descricaoStatus;
	}

	private boolean precisaBuscarResultadoNoSilce(ApostaDTO aposta, ParametroSimulacao parametroJogo) {
		// refatorar logica das portas logicas
		if (aposta.getConcursoInicial() < parametroJogo.getParametroJogo().getConcurso().getNumero()){
			if (aposta.getConcursoInicial()+1 == parametroJogo.getParametroJogo().getConcurso().getNumero()
				&& parametroJogo.getParametroJogo().getConcurso().getAberto()){
				return true;
			}else if(aposta.getConcursoInicial()+1 == parametroJogo.getParametroJogo().getConcurso().getNumero()
					&& !parametroJogo.getParametroJogo().getConcurso().getAberto()){
				return false;
			}else {
				return true;
			}
		}
		return false;
	}

	private ParametroSimulacao getParametroPorJogo(ApostaDTO aposta) {
		ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
		ParametroSimulacao parametroSimulacao = null;
		for (ParametroSimulacao parametro : parametrosSimulacao.getParametros()){
			if(aposta.getModalidade() == parametro.getParametroJogo().getConcurso().getModalidade()){
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

	@Override
	public void handle(DTOEnumLong situacao) {

	}

	@Override
	public void handle(ApostaConfirmadaHolder holder, DTOEnumLong situacao) {
		this.stripe.setBackgroundColor(this.color);
		this.modalidadeTxt.setTextColor(this.color);
		this.situacaoTxt.setTextColor(this.color);

		if (situacao.getDescricao().equalsIgnoreCase(itemView.getContext().getResources().getString(R.string.premiada_maiusculo))) {
			this.situacaoTxt.setTypeface(null, Typeface.BOLD);
			this.situacaoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			this.situacaoTxt.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(),itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_underline) + situacao.getDescricao().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))) + itemView.getContext().getResources().getString(R.string.underline)));
		} else {
			this.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_espaco)
											 + situacao.getDescricao().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt),
																							  itemView.getContext().getResources().getString(R.string.br))));
		}

		this.progressBarSituacao.setVisibility(View.INVISIBLE);
	}

	@Override
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
