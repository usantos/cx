package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class BolaoHolder extends LoteriasHolder<CotasBolaoDTO> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView modalidade;
	private TextView loterica;
	private TextView concurso;
	private TextView cidade;
	private TextView valor;
	private TextView textQtdDezenas;
	private TextView textQtdCotas;
	private ImageView seta;
	private ConstraintLayout verMais;
	private TextView valorPremio;
	private TextView qtdCotas;
	private Button btnDetalhes;
	private Button btnDiminui;
	private Button btnAumenta;
	private Button btnAdd;
	private View linhaNumero;
	private EstiloModalidadeMKP estilo;

	private OnItemBolaoListener listener;


	public BolaoHolder(Activity activity, View itemView, OnItemBolaoListener listener) {
		super(itemView);
		this.activity = activity;
		modalidade 	= itemView.findViewById(R.id.modalidade_bolao);
		loterica 	= itemView.findViewById(R.id.loterica_bolao);
		concurso 	= itemView.findViewById(R.id.concurso_bolao);
		cidade 		= itemView.findViewById(R.id.cidade_bolao);
		valor 		= itemView.findViewById(R.id.valor_bolao);
		textQtdDezenas = itemView.findViewById(R.id.descricao_qtd_dezenas_it);
		textQtdCotas = itemView.findViewById(R.id.descricao_qtd_cotas_disponiveis_it);

		seta 		= itemView.findViewById(R.id.seta_bolao);
		verMais 	= itemView.findViewById(R.id.container_ver_mais);
		valorPremio = itemView.findViewById(R.id.valor_estimado_premio);
		btnDetalhes = itemView.findViewById(R.id.detalhes);
		btnDiminui 	= itemView.findViewById(R.id.diminui_cota);
		btnAumenta 	= itemView.findViewById(R.id.aumenta_cota);
		qtdCotas 	= itemView.findViewById(R.id.quantidade_cotas);
		btnAdd		= itemView.findViewById(R.id.adicionar_carrinho);
		linhaNumero		= itemView.findViewById(R.id.linha_numero);

		this.listener = listener;
		seta.setOnClickListener(this);
		btnDetalhes.setOnClickListener(this);
		btnDiminui.setOnClickListener(this);
		btnAumenta.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
	}

	@Override
	public void bind(CotasBolaoDTO bolao, int position) {
		estilo = new EstiloModalidadeMKP(bolao.getModalidade());
		aplicaEstilo();
		if (isEspecial(bolao.getTipoConcurso())){
			modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
		} else {
			modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()).toLowerCase());
		}
		concurso.setText(String.valueOf("Conc. "+bolao.getConcurso()));
		if (bolao.getNomeFantasia().length() > 13){
			loterica.setText(bolao.getNomeFantasia().substring(0,13).concat("..."));
		} else {
			loterica.setText(bolao.getNomeFantasia());
		}
		String cidadeUf = bolao.getMunicipio().getNome() + " - " + bolao.getUf().getSigla();
		if (cidadeUf.length() > 13) {
			cidade.setText(cidadeUf.substring(0,13).concat("..."));
		} else {
			cidade.setText(cidadeUf);
		}
		if (bolao.getVrPremioEstimado() != null) {
			valorPremio.setText("R$ " + StringUtils.formatToCurrency(bolao.getVrPremioEstimado().doubleValue()));
		}
		valor.setText("R$ " + StringUtils.formatToCurrency(bolao.getVrCotaComTarifa().doubleValue()));

		textQtdDezenas.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),
				getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros())));

		textQtdCotas.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),
				getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal())));

		atualizaBotoesQtdCotas(bolao.getQtdCotasBolao(), bolao.getQtdCotaDisponivel());
		mostrarDetalhes(bolao.isAbertoParaMostrarDetalhes());
		qtdCotas.setText(Integer.toString(bolao.getQtdCotasBolao()));
	}

	private boolean isEspecial(int tipoConcurso){
		return tipoConcurso == 2;
	}

	private void aplicaEstilo() {
		int corEscura = ContextCompat.getColor(activity, estilo.getCorEscura());
		int corFonteFundoEscuro = ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro());
		int corLetraLista = ContextCompat.getColor(activity, estilo.getCorLetraLista());
		modalidade.setTextColor(corLetraLista);
		valor.setTextColor(corLetraLista);
		valorPremio.setTextColor(corLetraLista);
		btnDetalhes.setTextColor(corLetraLista);
		btnDetalhes.setBackground(ContextCompat.getDrawable(activity, estilo.getShapeBtnDetalhes()));
		btnDiminui.setBackgroundColor(corEscura);
		btnDiminui.setTextColor(corFonteFundoEscuro);
		btnAumenta.setBackgroundColor(corEscura);
		btnAumenta.setTextColor(corFonteFundoEscuro);
		linhaNumero.setBackgroundColor(corEscura);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.seta_bolao:
				listener.verMais(this, getAbsoluteAdapterPosition());
				break;
			case R.id.detalhes:
				listener.detalhes(getAbsoluteAdapterPosition());
				break;
			case R.id.diminui_cota:
				listener.diminui(this, getAbsoluteAdapterPosition());
				break;
			case R.id.aumenta_cota:
				listener.aumenta(this, getAbsoluteAdapterPosition());
				break;
			case R.id.adicionar_carrinho:
				listener.adicionarCarrinho(getAbsoluteAdapterPosition());
				break;
		}
	}

	public void mostrarDetalhes(Boolean aberto) {
		if (aberto){
			seta.setBackground(VectorUtils.getShape(R.drawable.ic_seta_cima_resultado, R.color.azulBolao));
			verMais.setVisibility(View.VISIBLE);
		} else {
			seta.setBackground(VectorUtils.getShape(R.drawable.ic_seta_baixo_resultado, R.color.azulBolao));
			verMais.setVisibility(View.GONE);
		}
	}

	@Override
	public void atualizaQuantidadeTextView(int quantidade, int maxPermitido) {
		qtdCotas.setText(Integer.toString(quantidade));
		atualizaBotoesQtdCotas(quantidade, maxPermitido);
	}

	@Override
	public void atualizaBotoesQtdCotas(int quantidade, int maxPermitido) {
		if (quantidade == QUANTIDADE_MINIMA){
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinza_desativado_mkp));
			btnDiminui.setEnabled(false);
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnAumenta.setEnabled(true);
		} else if (quantidade == maxPermitido){
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinza_desativado_mkp));
			btnAumenta.setEnabled(false);
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnDiminui.setEnabled(true);
		} else {
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnDiminui.setEnabled(true);
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnAumenta.setEnabled(true);
		}
	}

	@Override
	public void atualizaValorTotal(CotasBolaoDTO bolao) {
		valor.setText(getMoedaFormat(bolao));
	}

	private static String getMoedaFormat(CotasBolaoDTO bolao) {
		Double qtdApostas = Double.valueOf(bolao.getQtdCotasBolao() - 1);
		BigDecimal valor = bolao.getVrUltimaCotaComTarifa().add(BigDecimal.valueOf(bolao.getVrCotaComTarifa().doubleValue() * qtdApostas));

		return ViewUtils.getMoedaFormat(valor);
	}

	private ColorStateList getCor(int cor) {
		return ContextCompat.getColorStateList(activity, cor);
	}
}
