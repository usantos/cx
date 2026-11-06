package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.ConcursoUtils;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.NovaApiUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.ApostasConfirmadasFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;
import br.gov.caixa.loterias.apostas.view.listener.OnApostaConfirmadaListener;

public class ApostaConfirmadaHolder extends LoteriasHolder<ApostaDTO> implements View.OnClickListener {
	private TextView dataApostaTxt;
	private View stripe;
	private TextView modalidadeTxt;
	private TextView concursoTxt;
	private AppCompatImageView addCarrinho;
	private AppCompatImageView detalheAposta;
	//private AppCompatImageView tagBolao;
	private TextView situacaoTxt;
	private Button btnAposteAqui;
	private ProgressBar progressBarSituacao;
	private AppCompatImageView reloadImg;
	private int color;
	private OnApostaConfirmadaListener onApostasConfirmadasListener;
	private Activity parentActivity;
	private ApostasConfirmadasFragment fragment;
	private EstiloModalidadeMKP estiloModalidadeMKP;
	private TipoApostaLinhaView tipoApostaLinhaView;
	private Boolean isMega30 = false;
	private Boolean isLotecaPais = false;

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
			addCarrinho = view.findViewById(R.id.id_add_carrinho);
			detalheAposta = view.findViewById(R.id.id_detalhe_aposta);
			//tagBolao = view.findViewById(R.id.id_tag_bolao);
			situacaoTxt = view.findViewById(R.id.situacaoTxt);
			tipoApostaLinhaView = view.findViewById(R.id.tipo_aposta_linha);
			progressBarSituacao = view.findViewById(R.id.progressBarSituacao);
			reloadImg = view.findViewById(R.id.reloadImg);
			reloadImg.setOnClickListener(this);
			addCarrinho.setOnClickListener(this);
			detalheAposta.setOnClickListener(this);
		} else {
			btnAposteAqui = view.findViewById(R.id.btnAposteAquiApostasConfirmadas);
			btnAposteAqui.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(), itemView.getContext().getResources().getString(R.string.label_aposte_aqui)));

			btnAposteAqui.setOnClickListener(this);
		}

		view.setOnClickListener(this);
	}

	@Override
	public void bind(ApostaDTO aposta, int position) {
		//if(aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL")){
		//	isMega30 = EspecialUtils.isMega30(aposta.getConcursoAlvo() != null ? aposta.getConcursoAlvo() : aposta.getConcursoInicial(), aposta.getTipoConcurso());
		//}
		isMega30 = EspecialUtils.isMega30(aposta.getModalidade(), aposta.getConcursoAlvo() != null ? aposta.getConcursoAlvo() : aposta.getConcursoInicial(), aposta.getTipoConcurso().getDescricao().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.toString()));
		isLotecaPais = EspecialUtils.isLotecaPais(aposta.getModalidade(), aposta.getConcursoAlvo() != null ? aposta.getConcursoAlvo() : aposta.getConcursoInicial(), aposta.getTipoConcurso().getDescricao().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.toString()));
		//estiloModalidadeMKP = new EstiloModalidadeMKP(aposta.getModalidade(), isMega30);
		estiloModalidadeMKP = EstiloModalidadeMKP.createModalidadeConcursoEsp(aposta.getModalidade(), aposta.getConcursoAlvo() != null ? aposta.getConcursoAlvo() : aposta.getConcursoInicial(), aposta.getTipoConcurso().getDescricao().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.toString()));

		int color = ContextCompat.getColor(itemView.getContext(), estiloModalidadeMKP.getCorClara());

		this.color = color;
		this.stripe.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), estiloModalidadeMKP.getCorLetraLista()));
		if (EspecialUtils.isParametrosOutubroRosa() && aposta.getModalidade() == ModalidadeEnum.MEGA_SENA){
			estiloModalidadeMKP.alteraOutubroRosaPorTela();
		}
		this.modalidadeTxt.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidadeMKP.getCorLetraLista()));
		if (isMega30) {
			modalidadeTxt.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, ""));
		} else if (isLotecaPais) {
			//modalidadeTxt.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, ""));
			modalidadeTxt.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_LINHA, ""));
		} else {
			this.modalidadeTxt.setText(ViewUtils.getNomeModalidadePorAposta(aposta).toLowerCase());
		}
		this.dataApostaTxt.setText(aposta.getDataCompra());

		String descricaoStatus = getDescricaoString(aposta, position);
		if (descricaoStatus.isEmpty()){
			this.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.falha_comunicacao_situacao_aposta));
		} else {
			this.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_espaco) + descricaoStatus.toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))));
		}

		checaBolao(aposta);
		trataStyleSituacao(aposta);
		preencheTeimosinha(aposta);
		montaTipoApostaLinha(aposta, estiloModalidadeMKP.getCorLetraLista());

		if (!SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_MICRO_SERVICO.get(), ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean()) && !NovaApiUtils.isPossoBuscarNovaAPI() && ConcursoUtils.concursoApurado(aposta.getModalidade(), aposta.getConcursoInicial()) && isEfetivada(aposta)){
			fragment.conferirResultado(position, aposta, this);
		}
	}

	private boolean isEfetivada(ApostaDTO aposta) {
		return aposta.getSituacao() != null && aposta.getSituacao().getValor() != null && aposta.getSituacao().getValor() == SituacaoAposta.EFETIVADA;
	}

	private void checaBolao(ApostaDTO aposta) {
		if (isApostaBolao(aposta)){
			addCarrinho.setVisibility(View.INVISIBLE);
			addCarrinho.setOnClickListener(null);
			//tagBolao.setImageResource(estiloModalidadeMKP.getImagemTriangulo());
			//tagBolao.setVisibility(View.VISIBLE);
		} else {
			if (aposta.getModalidade().equals(ModalidadeEnum.LOTECA)) {
				addCarrinho.setVisibility(View.INVISIBLE);
			} else {
				addCarrinho.setVisibility(View.VISIBLE);
				addCarrinho.setOnClickListener(this);
			}
			//tagBolao.setVisibility(View.GONE);
		}
	}

	private void montaTipoApostaLinha(ApostaDTO aposta, int iconColorRes) {

		List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

		if (aposta.getTroca()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
		}
		if (isApostaBolao(aposta)) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
		}
		if (aposta.getCombo()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
		}
		if (aposta.getEspelho()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
		}
		if (aposta.getSurpresinha()) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
		}
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
		}

		if (listTipoApostaLinha.size() > 0) {
			tipoApostaLinhaView.setVisibility(View.VISIBLE);
			//tipoApostaLinhaView.setupView(listTipoApostaLinha, R.color.bolao_linha_clara, R.color.cinzaescuro, iconColorRes, TipoApostaLinhaView.TriangleDirection.UP);
			tipoApostaLinhaView.setupView(listTipoApostaLinha, R.color.bolao_linha_clara, R.color.cinzaescuro, R.color.cinzaescuro, TipoApostaLinhaView.TriangleDirection.UP);
		}
	}

		private boolean isApostaBolao(ApostaDTO aposta) {
		return aposta.getIndicadorCotaBolao() != null && aposta.getIndicadorCotaBolao();
	}

	private String getDescricaoString(ApostaDTO aposta, int position) {
		String descricaoStatus;

		this.showProgressBarSituacao(false);

		if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_NOVA_API.get(), ConfiguracoesDefaultEnum.IS_NOVA_API.asBoolean()) &&
			aposta.getSituacao().getValor() == 4L) {
			reloadImg.setVisibility(View.VISIBLE);
		}

		ParametroSimulacao parametroJogo = getParametroPorJogo(aposta, TipoConcursoEnum.NORMAL);
		if (parametroJogo == null) {
			parametroJogo = getParametroPorJogo(aposta, TipoConcursoEnum.ESPECIAL);
		}

		if(parametroJogo != null && !ConcursoUtils.concursoApurado(aposta.getModalidade(), aposta.getConcursoInicial())){
			if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_NOVA_API.get(), ConfiguracoesDefaultEnum.IS_NOVA_API.asBoolean())
					|| SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_MICRO_SERVICO.get(), ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean())){
				descricaoStatus = ApostaUtils.descricaoCorrigida(aposta.getSituacao());
			}else {
				descricaoStatus = SituacaoAposta.EnumSituacaoAposta.CONCURSO_NAO_APURADO.getDescricao().toLowerCase();

				if (!BuildVersionUtil.isPRD()){
					descricaoStatus = "* " +descricaoStatus + " *";
				}
			}
			String msgLog = "Chama: false" + " = MP: " + parametroJogo.getParametroJogo().getConcurso().getModalidade() + "-" + parametroJogo.getParametroJogo().getConcurso().getNumero() + " = " + aposta.getModalidade() + "-" + aposta.getConcursoInicial() + " = diff: " + (parametroJogo.getParametroJogo().getConcurso().getNumero() - aposta.getConcursoInicial());
			Log.d("RESULTADO-APOSTA", msgLog);
		}else {

			if (parametroJogo != null){
				descricaoStatus = ApostaUtils.descricaoCorrigida(aposta.getSituacao());
			} else {
				descricaoStatus = "";
			}
		}
		return descricaoStatus;
	}

	private ParametroSimulacao getParametroPorJogo(ApostaDTO aposta, TipoConcursoEnum tipoConcurso) {
		ParametrosSimulacao parametrosSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
		ParametroSimulacao parametroSimulacao = null;
		if (parametrosSimulacao != null && parametrosSimulacao.getParametros() != null){
			for (ParametroSimulacao parametro : parametrosSimulacao.getParametros()){
				if(aposta.getModalidade() == parametro.getParametroJogo().getConcurso().getModalidade() && parametro.getParametroJogo().getConcurso().getTipoConcurso() == tipoConcurso){
					parametroSimulacao = parametro;
					break;
				}
			}
		}

		return parametroSimulacao;
	}

	private void trataStyleSituacao(ApostaDTO aposta) {
		if (aposta.getSituacao().getValor() != SituacaoAposta.PREMIADA) {
			this.situacaoTxt.setTypeface(FonteUtils.getFonte(FontCaixaEnum.REGULAR));
		}else {
			this.situacaoTxt.setTypeface(FonteUtils.getFonte(FontCaixaEnum.BOLD));
		}
		this.situacaoTxt.setTextSize(14);
	}

	private void preencheTeimosinha(ApostaDTO aposta) {
		int teimosinhas = aposta.getQuantidadeTeimosinhas() != null ? aposta.getQuantidadeTeimosinhas() : 0;
		if (teimosinhas > 0) {
			int quantidade = aposta.getConcursoInicial() + (teimosinhas - 1);
			this.concursoTxt.setText(itemView.getContext().getString(R.string.label_aposta_concursos,
														 aposta.getConcursoInicial(),
														 quantidade));
		} else if (isApostaBolao(aposta)){
			this.concursoTxt.setText("Cota " + aposta.getReservaCotaBolao().getNumeroCotaReservada() + "/" +
					aposta.getReservaCotaBolao().getQtdCotaTotalBolao() + "\n" +
					itemView.getContext().getString(R.string.label_aposta_concurso, aposta.getConcursoInicial()));
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

	public void showReloadImg(boolean show){
		if (show) {
			this.reloadImg.setVisibility(View.VISIBLE);
		} else {
			this.reloadImg.setVisibility(View.INVISIBLE);
		}
	}
	public boolean precisaConferirSituacao(){
		if (this.situacaoTxt != null && this.situacaoTxt.getText().toString().contains(SituacaoAposta.EnumSituacaoAposta.EFETIVADA.getDescricao().toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt),itemView.getContext().getResources().getString(R.string.br))))){
			return true;
		}
		return false;
	}

	public boolean naoConseguiuApresentar(){
		if (this.situacaoTxt != null && this.situacaoTxt.getText().toString().contains(itemView.getContext().getString(R.string.falha_comunicacao_situacao_aposta))){
			return true;
		}
		return false;
	}

	public void handle(int position, ApostaConfirmadaHolder holder, DTOEnumLong situacao) {
		if (situacao.getValor() == SituacaoAposta.PREMIADA) {
			holder.situacaoTxt.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_underline) + ApostaUtils.descricaoCorrigida(situacao).toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt), itemView.getContext().getResources().getString(R.string.br))) + itemView.getContext().getResources().getString(R.string.underline)));
		} else {
			holder.situacaoTxt.setText(itemView.getContext().getResources().getString(R.string.situacao_dois_pontos_espaco) + ApostaUtils.descricaoCorrigida(situacao).toLowerCase(new Locale(itemView.getContext().getResources().getString(R.string.pt),itemView.getContext().getResources().getString(R.string.br))));
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
					case R.id.reloadImg:
						onApostasConfirmadasListener.onItemReload(getAbsoluteAdapterPosition(), this);
						break;
					case R.id.id_add_carrinho:
						onApostasConfirmadasListener.onReload(getAbsoluteAdapterPosition());
						break;
					default:
						onApostasConfirmadasListener.onItemClick(getAbsoluteAdapterPosition(), this);
						break;
				}
			}
		}
	}
}
