package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.NomeLotericaFormatter;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.BotaoFavoritar;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDePalpites;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaListaColapsada;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class BolaoHolder extends FiltroHolder<CotasBolaoDTO> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
    public static final int MODALIDADE_MARGIN_START = 24;
    public static final int LOTERICA_MARGIN_START = 56;
    private Activity activity;
    private static final int QUANTIDADE_MINIMA = 1;
    private TextView loterica;
    private ConstraintLayout containerModalidade;
    private ConstraintLayout containerLoterica;
    private TextView cidade;
    private TextView cotasEDezenas;
    private TextView valor;
    private TextView textQtdDezenas;
    private ImageView seta;
    private ConstraintLayout containerResumo;
    private ConstraintLayout verMais;
    private Button btnAdd;
    private EstiloModalidadeMKP estilo;
    private BotaoFavoritar botaoFavoritar;
    private OnItemBolaoListener listener;
    private TextView modalidade;
    private TextView valorPremio;
    private TextView valorPremioPorExtenso;
    private TextView concurso;
    private Boolean isMega30 = false;
    private Boolean isLotecaPais = false;

    //ViewStub e flag de controle
    private final ViewStub verMaisStub;
    private View verMaisContainer;//raiz inflada (inicialmente null)
    private boolean verMaisInflado = false;

    /**
     * Views do ver mais só existem depois de inflar o stub.
     */
    //private ConstraintLayout verMais;
    private Button btnDetalhes;
    private TextView textQtdCotas;
    private Button btnDiminui;
    private TextView qtdCotas;
    private View linhaNumero;
    private Button btnAumenta; /** Fim */

    public BolaoHolder(Activity activity, View itemView, OnItemBolaoListener listener) {
        super(itemView);
        this.activity = activity;

        botaoFavoritar = itemView.findViewById(R.id.btn_favoritar_mkp);
        containerModalidade = itemView.findViewById(R.id.container_modalidade);
        containerLoterica = itemView.findViewById(R.id.container_loterica);
        loterica = itemView.findViewById(R.id.loterica_bolao);
        cidade = itemView.findViewById(R.id.cidade_bolao);
        cotasEDezenas = itemView.findViewById(R.id.cotas_e_dezenas);
        modalidade = itemView.findViewById(R.id.modalidade_bolao);
        valorPremio = itemView.findViewById(R.id.valor_estimado_premio_bolao);
        valorPremioPorExtenso = itemView.findViewById(R.id.valor_estimado_premio_bolao_por_extenso);
        concurso = itemView.findViewById(R.id.concurso_bolao);
        valor = itemView.findViewById(R.id.valor_bolao);
        textQtdDezenas = itemView.findViewById(R.id.descricao_qtd_dezenas_it);
        textQtdCotas = itemView.findViewById(R.id.descricao_qtd_cotas_disponiveis_it);

        seta = itemView.findViewById(R.id.seta_bolao);
        containerResumo = itemView.findViewById(R.id.container_resumo_item_list_bolao);

        btnDetalhes = itemView.findViewById(R.id.detalhes);
        btnDiminui = itemView.findViewById(R.id.diminui_cota);
        btnAumenta = itemView.findViewById(R.id.aumenta_cota);
        qtdCotas = itemView.findViewById(R.id.quantidade_cotas);
        btnAdd = itemView.findViewById(R.id.adicionar_carrinho);
        linhaNumero = itemView.findViewById(R.id.linha_numero);
		seta 		= itemView.findViewById(R.id.seta_bolao);

		verMaisStub = itemView.findViewById(R.id.verMaisStub);

        this.listener = listener;
        seta.setOnClickListener(this);
        containerResumo.setOnClickListener(this);
        ViewCompat.setAccessibilityHeading(loterica, true);
        ViewCompat.setAccessibilityHeading(modalidade, true);
    }

    @Override
    public void bind(CotasBolaoDTO bolao, int position, FiltroAplicadoMarketplace filtro) {
        isMega30 = EspecialUtils.isMega30(bolao.getModalidade(), bolao.getConcurso(), bolao.getTipoConcurso().toString().equals(TipoConcursoEnum.ESPECIAL.getValor()));
        isLotecaPais = EspecialUtils.isLotecaPais(bolao.getModalidade(), bolao.getConcurso(), bolao.getTipoConcurso().toString().equals(TipoConcursoEnum.ESPECIAL.getValor()));
        estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(bolao.getModalidade(), bolao.getConcurso(), bolao.getTipoConcurso().toString().equals(TipoConcursoEnum.ESPECIAL.getValor()));

        aplicaEstilo();
        if(verMaisInflado){
            aplicaEstiloVerMais();
        }
        showContainer(filtro);
        if (isFiltradoLoterica(filtro) && !Boolean.FALSE.equals(filtro.isTodasAsModalidades())) {
            botaoFavoritar.setVisibility(View.GONE);
            loterica.setVisibility(View.GONE);
            modalidade.setVisibility(View.VISIBLE);
            if (isEspecial(bolao.getTipoConcurso())) {
                if (isMega30) {
                    modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS, ""));
                } else if (isLotecaPais) {
                    modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, ""));
                } else {
                    modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
                }
            } else {
                modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()).toLowerCase());
            }
            cidade.setVisibility(View.GONE);
            showCotasRestantes(true);
            cotas(bolao);
            valorEstimado(bolao);
        } else {
            botaoFavoritar.setVisibility(View.VISIBLE);
            modalidade.setVisibility(View.GONE);
            loterica.setVisibility(View.VISIBLE);
            loterica.setText(NomeLotericaFormatter.formatar(
                    StringUtils.capitalizerNovo(bolao.getNomeFantasia() == null ? "" : bolao.getNomeFantasia())));
            valorPremio.setVisibility(View.GONE);
            valorPremioPorExtenso.setVisibility(View.GONE);
            cidade.setVisibility(View.VISIBLE);
            showCotasRestantes(true);
            String cidadeUf = StringUtils.capitalizerNovo(bolao.getMunicipio().getNome() + " - " + bolao.getUf().getSigla());
            if (cidadeUf.length() > 14) {
                cidade.setText(cidadeUf.substring(0, 14).concat("..."));
            } else {
                cidade.setText(cidadeUf);
            }
            cotas(bolao);
        }
        valor.setText("R$ " + StringUtils.formatToCurrency(bolao.getVrUltimaCotaComTarifa().doubleValue()));

		mostrarDetalhes(bolao, filtro);

		botaoFavoritar.setState(bolao.isLotericaFavorita());

        botaoFavoritar.setOnStateChangeListener(isFavoritado -> {

            int pos = getBindingAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                listener.onFavoritarClick(pos);
            }
        });

    }

    private void garantirVerMaisInflado(){
        if (verMaisInflado) return;

        verMaisContainer = verMaisStub.inflate();

        btnDetalhes = verMaisContainer.findViewById(R.id.detalhes);
        textQtdCotas = verMaisContainer.findViewById(R.id.descricao_qtd_cotas_disponiveis_it);
        btnDiminui 	= verMaisContainer.findViewById(R.id.diminui_cota);
        qtdCotas 	= verMaisContainer.findViewById(R.id.quantidade_cotas);
        linhaNumero	= verMaisContainer.findViewById(R.id.linha_numero);
        btnAumenta 	= verMaisContainer.findViewById(R.id.aumenta_cota);
        btnAdd		= verMaisContainer.findViewById(R.id.adicionar_carrinho);
        concurso = verMaisContainer.findViewById(R.id.concurso_bolao);
        textQtdDezenas = verMaisContainer.findViewById(R.id.descricao_qtd_dezenas_it);

        btnDetalhes.setOnClickListener(this);
        btnDiminui.setOnClickListener(this);
        btnAumenta.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        aplicaEstiloVerMais();

        verMaisInflado = true;

    }

    private void showContainer(FiltroAplicadoMarketplace filtro) {
        boolean conjuntoVerdade = !Boolean.FALSE.equals(filtro.isTodasAsModalidades());
        containerModalidade.setVisibility(isFiltradoLoterica(filtro) && conjuntoVerdade? View.VISIBLE:View.GONE);
        containerLoterica.setVisibility(isFiltradoLoterica(filtro) && conjuntoVerdade? View.GONE:View.VISIBLE);
        alinharValorCota(isFiltradoLoterica(filtro) && conjuntoVerdade);
        int dp = isFiltradoLoterica(filtro)? MODALIDADE_MARGIN_START : LOTERICA_MARGIN_START;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cotasEDezenas.getLayoutParams();
        params.setMarginStart(dpToPx(dp, cotasEDezenas.getContext()));
    }

    private void alinharValorCota(boolean exibindoModalidade) {
        TextView tituloValor = itemView.findViewById(R.id.valor_cota);
        ConstraintLayout.LayoutParams tituloParams = (ConstraintLayout.LayoutParams) tituloValor.getLayoutParams();
        ConstraintLayout.LayoutParams valorParams = (ConstraintLayout.LayoutParams) valor.getLayoutParams();
        ConstraintLayout.LayoutParams setaParams = (ConstraintLayout.LayoutParams) seta.getLayoutParams();
        // Ao reciclar, restaura também o alinhamento da lista filtrada por lotérica.
        tituloParams.topToTop = exibindoModalidade ? ConstraintLayout.LayoutParams.PARENT_ID : ConstraintLayout.LayoutParams.UNSET;
        tituloParams.bottomToTop = exibindoModalidade ? R.id.valor_bolao : ConstraintLayout.LayoutParams.UNSET;
        tituloParams.bottomToBottom = exibindoModalidade ? ConstraintLayout.LayoutParams.UNSET : R.id.container_loterica;
        valorParams.bottomToBottom = exibindoModalidade ? ConstraintLayout.LayoutParams.PARENT_ID : ConstraintLayout.LayoutParams.UNSET;
        setaParams.topToTop = exibindoModalidade ? ConstraintLayout.LayoutParams.PARENT_ID : R.id.valor_cota;
        setaParams.bottomToBottom = exibindoModalidade ? ConstraintLayout.LayoutParams.PARENT_ID : R.id.valor_cota;
        tituloValor.setLayoutParams(tituloParams);
        valor.setLayoutParams(valorParams);
        seta.setLayoutParams(setaParams);
    }

    private void valorEstimado(CotasBolaoDTO bolao) {
        valorPremio.setVisibility(View.VISIBLE);
        valorPremio.setText(ViewUtils.getMoedaFormatComCentavos(bolao.getVrPremioEstimado(), 2));
        String valorEstimadoPorExtenso = ViewUtils.getMoedaFormatPorExtenso(bolao.getVrPremioEstimado());
        if (!valorEstimadoPorExtenso.isEmpty()){
            valorPremioPorExtenso.setText(String.format("(%s)",valorEstimadoPorExtenso));
            valorPremioPorExtenso.setVisibility(View.VISIBLE);
        } else {
            valorPremioPorExtenso.setVisibility(View.GONE);
        }
    }

    private void showCotasRestantes(boolean shouldShown) {
        if (shouldShown) {
            cotasEDezenas.setVisibility(View.VISIBLE);
        } else {
            cotasEDezenas.setVisibility(View.GONE);
        }
    }

    private void cotas(CotasBolaoDTO bolao) {
        String resumoCotas = getCotaListaColapsada(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal()) + "\n";
        String resumoCotasEDezenas = bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ? resumoCotas + getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()).replace("_", "") : resumoCotas + getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros()).replace("_", "");
        cotasEDezenas.setText(resumoCotasEDezenas);
    }

    private boolean isFiltradoLoterica(FiltroAplicadoMarketplace filtro) {
        return (filtro != null && filtro.getLotericaDTO() != null &&
                filtro.getLotericaDTO().getCodigo() != null);
    }

    private boolean isEspecial(int tipoConcurso) {
        return tipoConcurso == 2;
    }

	private void aplicaEstilo() {
		int corLetraLista = ContextCompat.getColor(activity, estilo.getCorLetraLista());

		modalidade.setTextColor(corLetraLista);
	}

	private void aplicaEstiloVerMais() {
		int corEscura = ContextCompat.getColor(activity, estilo.getCorEscura());
		int corFonteFundoEscuro = ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro());
		int corLetraLista = ContextCompat.getColor(activity, estilo.getCorLetraLista());

		btnDetalhes.setTextColor(corLetraLista);
		btnDetalhes.setBackground(ContextCompat.getDrawable(activity, estilo.getShapeBtnDetalhes()));
		btnDiminui.setBackgroundColor(corEscura);
		btnDiminui.setTextColor(corFonteFundoEscuro);
		btnAumenta.setBackgroundColor(corEscura);
		btnAdd.setBackground(VectorUtils.getShape(R.drawable.btn_rounded_blue, estilo.getCorEscura()));
		btnAdd.setTextColor(corFonteFundoEscuro);
		concurso.setTextColor(corLetraLista);

		btnAumenta.setTextColor(corFonteFundoEscuro);
		linhaNumero.setBackgroundColor(corEscura);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seta_bolao:
                listener.verMais(this, getBindingAdapterPosition());
                break;
            case R.id.detalhes:
                listener.detalhes(getBindingAdapterPosition());
                break;
            case R.id.diminui_cota:
                listener.diminui(this, getBindingAdapterPosition());
                break;
            case R.id.aumenta_cota:
                listener.aumenta(this, getBindingAdapterPosition());
                break;
            case R.id.adicionar_carrinho:
                listener.adicionarCarrinho(getBindingAdapterPosition());
                break;
            case R.id.container_resumo_item_list_bolao:
                listener.verMais(this, getBindingAdapterPosition());
                break;
        }
    }

	public void mostrarDetalhes(CotasBolaoDTO bolao,  FiltroAplicadoMarketplace filtro) {
		if (Boolean.TRUE.equals(bolao.isAbertoParaMostrarDetalhes())){
            garantirVerMaisInflado();
            bindDadosVerMais(bolao);
            showCotasRestantes(false);
			seta.setBackground(VectorUtils.getShape(R.drawable.seta_cima_lista_boloes, R.color.azulBolao));
            if (verMaisContainer != null) {
                verMaisContainer.setVisibility(View.VISIBLE);
            }
            if(isFiltradoLoterica(filtro)){
                concurso.setVisibility(View.VISIBLE);
                concurso.setText(String.valueOf("Concurso " + bolao.getConcurso()));
            }else{
                concurso.setVisibility(View.GONE);
            }
		} else {
            showCotasRestantes(true);
			seta.setBackground(VectorUtils.getShape(R.drawable.seta_baixo_lista_boloes, R.color.azulBolao));

			if(verMaisInflado && verMaisContainer != null){
				verMaisContainer.setVisibility(View.GONE);
			}
		}
	}

    private void bindDadosVerMais(CotasBolaoDTO bolao){
        if (bolao == null || textQtdCotas == null || qtdCotas == null || textQtdDezenas == null) return;
        textQtdCotas.setText(ViewUtils.infoPrognosticosMkp(itemView.getContext(),
                getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal()), 18, 18));
        atualizaBotoesQtdCotas(bolao.getQtdCotasBolao(), bolao.getQtdCotaDisponivel());
        qtdCotas.setText(String.valueOf(bolao.getQtdCotasBolao()));
        textQtdDezenas.setText(ViewUtils.infoPrognosticosMkp(itemView.getContext(),
                bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ?
                        getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()) :
                        getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros()), 18, 16));
    }

    @Override
    public void atualizaQuantidadeTextView(int quantidade, int maxPermitido) {
        qtdCotas.setText(Integer.toString(quantidade));
        atualizaBotoesQtdCotas(quantidade, maxPermitido);
    }

    @Override
    public void atualizaBotoesQtdCotas(int quantidade, int maxPermitido) {
        if (quantidade == QUANTIDADE_MINIMA) {
            btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinza_desativado_mkp));
            btnDiminui.setEnabled(false);
            btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
            btnAumenta.setEnabled(true);
        } else if (quantidade == maxPermitido) {
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

    private int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

}
