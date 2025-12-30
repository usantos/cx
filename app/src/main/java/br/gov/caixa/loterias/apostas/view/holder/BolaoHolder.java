package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.BotaoFavoritar;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDePalpites;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class BolaoHolder extends LoteriasHolder<CotasBolaoDTO> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView loterica;
	private TextView cidade;
	private TextView valor;
	private TextView textQtdDezenas;
	private TextView textQtdCotas;
	private ImageView seta;
	private ConstraintLayout verMais;
	private TextView qtdCotas;
	private Button btnDetalhes;
	private Button btnDiminui;
	private Button btnAumenta;
	private Button btnAdd;
	private View linhaNumero;
	private EstiloModalidadeMKP estilo;
	private BotaoFavoritar botaoFavoritar;
	private OnItemBolaoListener listener;
	private FiltroAplicadoMarketplace filtro;
	private TextView modalidade;
	private TextView valorPremio;
	private TextView concurso;


	public BolaoHolder(Activity activity, View itemView, OnItemBolaoListener listener, FiltroAplicadoMarketplace filtro) {
		super(itemView);
		this.activity = activity;
		this.filtro = filtro;

		botaoFavoritar = itemView.findViewById(R.id.btn_favoritar_mkp);
		loterica 	= itemView.findViewById(R.id.loterica_bolao);
		cidade 		= itemView.findViewById(R.id.cidade_bolao);
		modalidade 	= itemView.findViewById(R.id.modalidade_bolao);
		valorPremio = itemView.findViewById(R.id.valor_estimado_premio);
		concurso = itemView.findViewById(R.id.concurso_bolao);
		valor 		= itemView.findViewById(R.id.valor_bolao);
		textQtdDezenas = itemView.findViewById(R.id.descricao_qtd_dezenas_it);
		textQtdCotas = itemView.findViewById(R.id.descricao_qtd_cotas_disponiveis_it);

		seta 		= itemView.findViewById(R.id.seta_bolao);
		verMais 	= itemView.findViewById(R.id.container_ver_mais);
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
		int corPadrao = ContextCompat.getColor(activity, R.color.cinza90);

		aplicaEstilo();
		if (isFiltradoLoterica()) {
			botaoFavoritar.setVisibility(View.GONE);
			loterica.setVisibility(View.GONE);
			modalidade.setVisibility(View.VISIBLE);
			if (isEspecial(bolao.getTipoConcurso())){
				modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
			} else {
				modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()).toLowerCase());
			}
			cidade.setVisibility(View.GONE);
			valorPremio.setVisibility(View.VISIBLE);
			if (bolao.getVrPremioEstimado() != null) {
				valorPremio.setText(ViewUtils.getMoedaFormatPorExtenso(bolao.getVrPremioEstimado()));
			}
			concurso.setVisibility(View.VISIBLE);
			concurso.setText(String.valueOf("Concurso " + bolao.getConcurso()));
		} else {
			botaoFavoritar.setVisibility(View.VISIBLE);
			modalidade.setVisibility(View.GONE);
			loterica.setVisibility(View.VISIBLE);
			if (bolao.getNomeFantasia().length() > 13){
				loterica.setText(bolao.getNomeFantasia().substring(0,13).concat("..."));
			} else {
				loterica.setText(bolao.getNomeFantasia());
			}
			valorPremio.setVisibility(View.GONE);
			cidade.setVisibility(View.VISIBLE);
			String cidadeUf = bolao.getMunicipio().getNome() + " - " + bolao.getUf().getSigla();
			if (cidadeUf.length() > 13) {
				cidade.setText(cidadeUf.substring(0,13).concat("..."));
			} else {
				cidade.setText(cidadeUf);
			}
			concurso.setVisibility(View.GONE);
		}

		valor.setText("R$ " + StringUtils.formatToCurrency(bolao.getVrUltimaCotaComTarifa().doubleValue()));

		textQtdDezenas.setText(ViewUtils.infoPrognosticosMkp(itemView.getContext(),
				bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ?
						getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()) :
						getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros()), 18, 16,corPadrao,corPadrao));

		textQtdCotas.setText(ViewUtils.infoPrognosticosMkp(itemView.getContext(),
				getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal()), 18, 18,corPadrao,corPadrao));

		atualizaBotoesQtdCotas(bolao.getQtdCotasBolao(), bolao.getQtdCotaDisponivel());
		mostrarDetalhes(bolao.isAbertoParaMostrarDetalhes());
		qtdCotas.setText(Integer.toString(bolao.getQtdCotasBolao()));
		botaoFavoritar.setState(bolao.isLotericaFavorita());

		botaoFavoritar.setOnStateChangeListener(isFavoritado -> {
			if(listener != null){
				listener.onFavoritarClick(position, isFavoritado);
			}
		});

	}

	private boolean isFiltradoLoterica() {
		return (filtro != null && filtro.getLotericaDTO() != null && filtro.getLotericaDTO().getCodigo() != null);
	}
	private boolean isEspecial(int tipoConcurso){
		return tipoConcurso == 2;
	}

	private void aplicaEstilo() {
		int corEscura = ContextCompat.getColor(activity, estilo.getCorEscura());
		int corFonteFundoEscuro = ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro());
		int corLetraLista = ContextCompat.getColor(activity, estilo.getCorLetraLista());

		modalidade.setTextColor(corLetraLista);
		concurso.setTextColor(corLetraLista);
		btnDetalhes.setTextColor(corLetraLista);
		btnDetalhes.setBackground(ContextCompat.getDrawable(activity, estilo.getShapeBtnDetalhes()));
		btnDiminui.setBackgroundColor(corEscura);
		btnDiminui.setTextColor(corFonteFundoEscuro);
		btnAumenta.setBackgroundColor(corEscura);
		btnAdd.setBackground(VectorUtils.getShape(R.drawable.btn_rounded_blue, estilo.getCorEscura()));
		btnAdd.setTextColor(corFonteFundoEscuro);

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
