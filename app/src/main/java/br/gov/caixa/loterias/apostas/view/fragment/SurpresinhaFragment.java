package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.SurpresinhaApostaModel;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SurpresinhaGridViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;

@EFragment(R.layout.fragment_surpresinha)
public class SurpresinhaFragment extends Fragment implements OnClickListener {

    private static final int QUANTIDADE_MINIMA_SURPRESINHAS = 1;

    private TextView valorAposta;
    private SimularApostaActivity parentActivity;
    private SurpresinhaFragmentListener surpresinhaFragmentListener;
    private ParametroEquipe equipeSelecionada = null;
    private ModalidadeEnum tipoJogo;
    private ParametroJogoDTO parametroJogo;
    private int typeGameColorLight, qtdSurpresinhas = 1, qtdTrevos, getQtdNumeros = 6;
    private SurpresinhaGridViewAdapter simularDezenasAdapter;
    private BigDecimal valorTotalAposta;

    @ViewById
    protected ImageView escolhaItemImageView;

    @ViewById
    protected TextView infoItemSurpresaTextView, qtdSurpresinhasSelecionadas, surpresinhaTitulo, surpresinhaQtdInfo, surpresinhaInfo, qtdSurpresinhasSelecionadasTrevos, qtdNumerosSelecionadasNovo;

    @ViewById
    protected Button escolherOutroItemBtn, btnMenosSurpresinha, btnMaisSurpresinha, btnMenosTrevos, btnMaisTrevos, btnNumerosMenos, btnNumerosMais;

    @ViewById
    protected RelativeLayout escolhaItemLayout;

    @ViewById
    protected ExpandableHeightGridView listaDezenasSurpresinha;

    @ViewById
    protected LinearLayout layoutEspelhoSurpresinha;

    @ViewById
    protected AppCompatCheckBox selcioneEspelhoSurpresinha;

    @ViewById
    protected ConstraintLayout maisMilionaria, numeroNovoConst;

    private SurpresinhaApostaModel model;

    public SurpresinhaFragment() {
        // Required empty public constructor
    }

    @AfterViews
    protected void init() {
        model = new SurpresinhaApostaModel(getActivity());

        AppCenterManager.registraEvento("ENTROU_SURPRESINHA");

        parentActivity = ((SimularApostaActivity) getActivity());
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
            if (parametroJogo != null && qtdTrevos == 0
                    && parametroJogo.getConcurso().getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                qtdTrevos = parametroJogo.getTrevos().getQtdMinima();
            }
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            equipeSelecionada = new Gson().fromJson(bundle.getString("equipeSelecionada"), ParametroEquipe.class);
        }

        identificarTipoJogo();
        configuraListaDezenas();
        atualizaDezenaAdapter();
        configuraBotoes();

        if (isLotomania()) {
            layoutEspelhoSurpresinha.setVisibility(View.VISIBLE);
            selcioneEspelhoSurpresinha = layoutEspelhoSurpresinha.findViewById(R.id.selcioneEspelhoSurpresinha);
        }

        if (equipeSelecionada != null) {
            adicionarSurpresinhaNoCarrinho();
        }

        if (isTimemania()) {
            escolhaItemLayout.setVisibility(View.VISIBLE);
        }

        if (isDiaDeSorte()) {
            initDiaDeSorte();
        }

        surpresinhaTitulo.setTextColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        surpresinhaInfo.setTextColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        surpresinhaQtdInfo.setTextColor(ContextCompat.getColor(parentActivity, typeGameColorLight));

        if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
            maisMilionaria.setVisibility(View.VISIBLE);
            numeroNovoConst.setVisibility(View.VISIBLE);

            ConstraintLayout.LayoutParams textViewParams = (ConstraintLayout.LayoutParams) surpresinhaQtdInfo.getLayoutParams();
            textViewParams.topToBottom = R.id.maisMilionaria;
            surpresinhaQtdInfo.setLayoutParams(textViewParams);

        }
    }

    private boolean isDiaDeSorte() {
        return tipoJogo == ModalidadeEnum.DIA_DE_SORTE;
    }

    public void surpresinhaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle != null) {
            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
        }
    }

    private void identificarTipoJogo() {
        tipoJogo = (ModalidadeEnum) getActivity().getIntent().getSerializableExtra("tipoAposta");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            surpresinhaFragmentListener = (SurpresinhaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SurpresinhaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        surpresinhaFragmentListener = null;
    }

    // METODOS DE CONFIGURACOES
    private void atualizaDezenaAdapter() {
        simularDezenasAdapter.atualizaDezenas(montaListaDezenasSurpresinha());
    }

    private void configuraBotoes() {
        valorAposta = parentActivity.getValorApostaCartela();
        if (parametroJogo != null) {
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        }

        atualizaBotoesQtdSurpresinha();
        if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
            atualizaBotoesQtdTrevos();
        }

        Button botaoAdicionarSurpresinhaAoCarrinho = parentActivity.getBotaoAdicionarCompletarCartela();
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(Color.WHITE);
        botaoAdicionarSurpresinhaAoCarrinho.setOnClickListener(this);
        botaoAdicionarSurpresinhaAoCarrinho.setText(ViewUtils.textFuturaAndFuturaBold(parentActivity, getString(R.string.adicionarAoCarrinho)));

        parentActivity.getBotaoLimparAposta().setVisibility(View.GONE);
        parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);

        btnMenosSurpresinha.setBackgroundTintList(getCor(R.color.cinzaescuro));
        btnMaisSurpresinha.setBackgroundTintList(getCor(typeGameColorLight));
    }

    private void atualizaBotoesQtdSurpresinha() {
        if (qtdSurpresinhas == QUANTIDADE_MINIMA_SURPRESINHAS){
            btnMenosSurpresinha.setBackgroundTintList(getCor(R.color.cinzaescuro));
            btnMaisSurpresinha.setBackgroundTintList(getCor(typeGameColorLight));
        }else {
            btnMenosSurpresinha.setBackgroundTintList(getCor(typeGameColorLight));
            btnMaisSurpresinha.setBackgroundTintList(getCor(typeGameColorLight));
        }

        qtdSurpresinhasSelecionadas.setTextColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
    }

    private void atualizaBotoesQtdTrevos() {
        if (qtdTrevos > parametroJogo.getTrevos().getQtdMinima() && qtdTrevos < parametroJogo.getTrevos().getQtdMaxima()){
            btnMenosTrevos.setBackgroundTintList(getCor(typeGameColorLight));
            btnMaisTrevos.setBackgroundTintList(getCor(typeGameColorLight));
        }else if (qtdTrevos == parametroJogo.getTrevos().getQtdMinima()){
            btnMenosTrevos.setBackgroundTintList(getCor(R.color.cinzaescuro));
            btnMaisTrevos.setBackgroundTintList(getCor(typeGameColorLight));
        } else {
            btnMenosTrevos.setBackgroundTintList(getCor(typeGameColorLight));
            btnMaisTrevos.setBackgroundTintList(getCor(R.color.cinzaescuro));
        }

        qtdSurpresinhasSelecionadasTrevos.setTextColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
    }

    private ColorStateList getCor(int cor) {
        return ContextCompat.getColorStateList(getActivity(), cor);
    }

    private void configuraListaDezenas() {
        listaDezenasSurpresinha.setExpanded(true);
        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha(), getActivity(), typeGameColorLight);
        listaDezenasSurpresinha.setAdapter(simularDezenasAdapter);
    }

    private ArrayList<String> montaListaDezenasSurpresinha() {
        ArrayList<String> arrayDezenas = new ArrayList<>();
        for (int i = 0; i < Constantes.CINCO_INTEIRO; i++) {
            arrayDezenas.add("XX");
        }

        return arrayDezenas;
    }

    public void atualizaValorTotalAposta(int numPrognostico, int qtdTeimosinhas) {
        configuraListaDezenas();

        BigDecimal valorEmReais = BigDecimal.ZERO;

        if (parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
            ParametroValorApostaDTO valor = parametroJogo.getValorApostaBy(numPrognostico, qtdTrevos);
            valorEmReais = valor.getValor();
        }else {
            for (ParametroValorApostaDTO parametro : parametroJogo.getValoresAposta()) {
                if (parametro.getNumeroPrognosticos() == numPrognostico) {
                    valorEmReais = parametro.getValor();
                    break;
                }
            }
        }

        if (qtdSurpresinhas > 0) {
            if (qtdTeimosinhas == 0) {
                valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(1.0)).multiply(BigDecimal.valueOf(qtdSurpresinhas));
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            } else {
                valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdTeimosinhas)).multiply(BigDecimal.valueOf(qtdSurpresinhas));
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            }
        } else {
            if (qtdTeimosinhas == 0) {
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            } else {
                valorEmReais = valorEmReais.multiply(BigDecimal.valueOf(qtdTeimosinhas));
                ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
            }
        }
        if (isLotomania() && selcioneEspelhoSurpresinha.isChecked()) {
            valorEmReais = valorEmReais.multiply(Constantes.DOIS_BIG_DECIMAL);
            ViewUtils.setMoedaFormatHtml(valorEmReais, valorAposta);
        }

        valorTotalAposta = valorEmReais;
    }

    public void adicionarSurpresinhaNoCarrinho() {

        IncluirSurpresinhaDTO surpresinha;
        switch (parametroJogo.getConcurso().getModalidade()){
            case TIMEMANIA:
                surpresinha = ApostaUtils.getSurpresinhaTimemania(parametroJogo,parentActivity.qtdDezenasPossiveisSelecionado,
                                                             qtdSurpresinhas, parentActivity.qtdConcursoSelecionado,
                                                             valorTotalAposta, equipeSelecionada);
                break;
            case LOTOMANIA:
                surpresinha = ApostaUtils.getSurpresinhaLotomania(parametroJogo,parentActivity.qtdDezenasPossiveisSelecionado,
                                                             qtdSurpresinhas, parentActivity.qtdConcursoSelecionado,
                                                             valorTotalAposta, selcioneEspelhoSurpresinha.isChecked());
                break;
            case MAIS_MILIONARIA:
                surpresinha = ApostaUtils.getSurpresinhaMaisMilionaria(parametroJogo,parentActivity.qtdDezenasPossiveisSelecionado,
                                                             qtdSurpresinhas, parentActivity.qtdConcursoSelecionado,
                                                             valorTotalAposta, qtdTrevos);
                break;
            default:
                surpresinha = ApostaUtils.getSurpresinha(parametroJogo,parentActivity.qtdDezenasPossiveisSelecionado,
                                                             qtdSurpresinhas, parentActivity.qtdConcursoSelecionado,
                                                             valorTotalAposta);

        }

        model.addSurpresinhaCarrinho(surpresinha);
    }

    private boolean isLotomania() {
        return tipoJogo == ModalidadeEnum.LOTOMANIA;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botaoAdicionarCompletarCartela: {
                adicionarSurpresinhaNoCarrinho();
            }
            break;
        }
    }

    public void setEquipeSelecionada(ParametroEquipe equipeSelecionada) {
        this.equipeSelecionada = equipeSelecionada;
    }

    public BigDecimal getValorTotalAposta() {
        return valorTotalAposta;
    }

    public ParametroJogoDTO getParametroJogo() {
        return parametroJogo;
    }

    @Click
    protected void escolherOutroItemBtn() {
        if (isTimemania()) {
            parentActivity.escolherOutroTimeSurpresinha();
        } else if (isDiaDeSorte()) {
            parentActivity.mesDeSorteFragment(null, true);
        }

    }

    private boolean isTimemania() {
        return tipoJogo == ModalidadeEnum.TIMEMANIA;
    }

    @Click
    protected void btnMenosSurpresinha() {
        if (qtdSurpresinhas > 1) {
            qtdSurpresinhas--;
            if (qtdSurpresinhas == 1) {
                atualizaBotoesQtdSurpresinha();
            }
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
        }
    }

    @Click
    protected void btnMaisSurpresinha() {
        if (qtdSurpresinhas == 1) {
            atualizaBotoesQtdSurpresinha();
        }

        qtdSurpresinhas++;

        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
        btnMenosSurpresinha.setBackgroundTintList(getCor(typeGameColorLight));
    }

    @Click
    protected void btnMaisTrevos() {
        if (qtdTrevos < 6) {
            qtdTrevos++;
            atualizaBotoesQtdTrevos();
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
        }
    }

    @Click
    protected void btnMenosTrevos() {
        if (qtdTrevos > 2) {
            qtdTrevos--;
            atualizaBotoesQtdTrevos();
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
        }
    }

    @Click
    protected void btnNumerosMais() {
        if (getQtdNumeros < 12) {
            getQtdNumeros++;
            parentActivity.qtdDezenasPossiveisSelecionado = getQtdNumeros;
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdNumerosSelecionadasNovo.setText(String.format(Locale.getDefault(), "%d", getQtdNumeros));
        }
        atualizarButtonColor();
    }

    @Click
    protected void btnNumerosMenos() {
        if (getQtdNumeros > 6) {
            getQtdNumeros--;
            parentActivity.qtdDezenasPossiveisSelecionado = getQtdNumeros;
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdNumerosSelecionadasNovo.setText(String.format(Locale.getDefault(), "%d", getQtdNumeros));
        }
        atualizarButtonColor();
    }

    protected void atualizarButtonColor() {
        if (getQtdNumeros == 6) {
            btnNumerosMenos.setBackgroundTintList(getCor(R.color.cinzaescuro));
        } else {
            btnNumerosMenos.setBackgroundTintList(getCor(typeGameColorLight));
        }
        if (getQtdNumeros == 12){
            btnNumerosMais.setBackgroundTintList(getCor(R.color.cinzaescuro));
        }else {
            btnNumerosMais.setBackgroundTintList(getCor(typeGameColorLight));
        }

    }

    @Click
    protected void layoutEspelhoSurpresinha() {
        selecionarEspelho();
    }

    private void selecionarEspelho() {
        if (selcioneEspelhoSurpresinha.isChecked()) {
            selcioneEspelhoSurpresinha.setChecked(Boolean.FALSE);
        } else {
            selcioneEspelhoSurpresinha.setChecked(Boolean.TRUE);
        }
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
    }

    private void initDiaDeSorte() {
        escolhaItemImageView.setImageDrawable(getResources().getDrawable(R.drawable.agrupar_1));
        infoItemSurpresaTextView.setText(R.string.label_mes_sorte);
        infoItemSurpresaTextView.setTextColor(getResources().getColor(R.color.diasortemedio));
        escolherOutroItemBtn.setText(R.string.label_escolher_outro_mes);
        escolherOutroItemBtn.setVisibility(View.GONE);
        escolhaItemLayout.setVisibility(View.VISIBLE);
    }

    public void resetQtdTrevos() {
        qtdTrevos = parametroJogo.getTrevos().getQtdMinima();
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
        btnMenosTrevos.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinzaescuro));
        btnMaisTrevos.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.mais_milionaria_escura));
    }
}
