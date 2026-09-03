package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

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
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.AdicionarApostaCarrinhoHelper;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SurpresinhaGridViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;

public class SurpresinhaFragment extends Fragment implements OnClickListener {

    private static final int QUANTIDADE_MINIMA_SURPRESINHAS = 1;

    private TextView valorAposta;
    private SimularApostaActivity parentActivity;
    private SurpresinhaFragmentListener surpresinhaFragmentListener;
    private ParametroEquipe equipeSelecionada = null;
    private ModalidadeEnum tipoJogo;
    private ParametroJogoDTO parametroJogo;
    private int typeGameColorLight, typeGameColorDark, qtdSurpresinhas = 1, qtdTrevos, getQtdNumeros;
    private int corFonteFundoBranco, corFonteFundoClaro, corFonteFundoEscuro;
    private SurpresinhaGridViewAdapter simularDezenasAdapter;
    private BigDecimal valorTotalAposta;
    private boolean isAvisouS = false;
    private boolean isAvisouT = false;
    private boolean isAvisouN = false;
    private View view;
    private ImageView escolhaItemImageView;

    private TextView infoItemSurpresaTextView, qtdSurpresinhasSelecionadas, surpresinhaTitulo,
            surpresinhaQtdInfo, surpresinhaInfo, qtdSurpresinhasSelecionadasTrevos, qtdNumerosSelecionadasNovo;

    private Button escolherOutroItemBtn, btnMenosSurpresinha, btnMaisSurpresinha, btnMenosTrevos,
            btnMaisTrevos, btnNumerosMenos, btnNumerosMais;
    private RelativeLayout escolhaItemLayout;
    private ExpandableHeightGridView listaDezenasSurpresinha;
    private LinearLayout layoutEspelhoSurpresinha;
    private AppCompatCheckBox selcioneEspelhoSurpresinha;
    private ConstraintLayout maisMilionaria, numeroNovoConst;
    private SurpresinhaApostaModel model;

    private SurpresinhaFragment() {}

    public static SurpresinhaFragment newInstance(){
        return new SurpresinhaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_surpresinha, container, false);
        setViews();
        setListeners();
        init();
        return view;
    }

    private void setViews() {
        escolhaItemImageView = view.findViewById(R.id.escolhaItemImageView);
        infoItemSurpresaTextView = view.findViewById(R.id.infoItemSurpresaTextView);
        qtdSurpresinhasSelecionadas = view.findViewById(R.id.qtdSurpresinhasSelecionadas);
        surpresinhaTitulo = view.findViewById(R.id.surpresinhaTitulo);
        surpresinhaQtdInfo = view.findViewById(R.id.surpresinhaQtdInfo);
        surpresinhaInfo = view.findViewById(R.id.surpresinhaInfo);
        qtdSurpresinhasSelecionadasTrevos = view.findViewById(R.id.qtdSurpresinhasSelecionadasTrevos);
        qtdNumerosSelecionadasNovo = view.findViewById(R.id.qtdNumerosSelecionadasNovo);
        escolherOutroItemBtn = view.findViewById(R.id.escolherOutroItemBtn);
        btnMenosSurpresinha = view.findViewById(R.id.btnMenosSurpresinha);
        btnMaisSurpresinha = view.findViewById(R.id.btnMaisSurpresinha);
        btnMenosTrevos = view.findViewById(R.id.btnMenosTrevos);
        btnMaisTrevos = view.findViewById(R.id.btnMaisTrevos);
        btnNumerosMenos = view.findViewById(R.id.btnNumerosMenos);
        btnNumerosMais = view.findViewById(R.id.btnNumerosMais);
        escolhaItemLayout = view.findViewById(R.id.escolhaItemLayout);
        listaDezenasSurpresinha = view.findViewById(R.id.listaDezenasSurpresinha);
        layoutEspelhoSurpresinha = view.findViewById(R.id.layoutEspelhoSurpresinha);
        selcioneEspelhoSurpresinha = view.findViewById(R.id.selcioneEspelhoSurpresinha);
        maisMilionaria = view.findViewById(R.id.maisMilionaria);
        numeroNovoConst = view.findViewById(R.id.numeroNovoConst);
    }

    private void setListeners(){
        escolherOutroItemBtn.setOnClickListener(v -> escolherOutroItemBtn());
        btnMenosSurpresinha.setOnClickListener(v -> btnMenosSurpresinha());
        btnMaisSurpresinha.setOnClickListener(v -> btnMaisSurpresinha());
        btnMenosTrevos.setOnClickListener(v -> btnMenosTrevos());
        btnMaisTrevos.setOnClickListener(v -> btnMaisTrevos());
        btnNumerosMais.setOnClickListener(v -> btnNumerosMais());
        btnNumerosMenos.setOnClickListener(v -> btnNumerosMenos());
        layoutEspelhoSurpresinha.setOnClickListener(v -> layoutEspelhoSurpresinha());
    }

    private void init() {
        model = new SurpresinhaApostaModel(getActivity());

        AppCenterManager.registraEvento("ENTROU_SURPRESINHA");

        parentActivity = ((SimularApostaActivity) getActivity());
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
            if (parametroJogo != null && parametroJogo.getConcurso().getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                if (qtdTrevos == 0){
                    qtdTrevos = parametroJogo.getTrevos().getQtdMinima();
                }
                if (getQtdNumeros == 0){
                    getQtdNumeros = parametroJogo.getQuantidadeMinima();
                }
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

        surpresinhaTitulo.setTextColor(ContextCompat.getColor(parentActivity, corFonteFundoBranco));
        surpresinhaInfo.setTextColor(ContextCompat.getColor(parentActivity, corFonteFundoBranco));
        surpresinhaQtdInfo.setTextColor(ContextCompat.getColor(parentActivity, corFonteFundoBranco));

        if(parametroJogo.isTipoJogo(ModalidadeEnum.MAIS_MILIONARIA)){
            maisMilionaria.setVisibility(View.VISIBLE);
            numeroNovoConst.setVisibility(View.VISIBLE);

            ConstraintLayout.LayoutParams textViewParams = (ConstraintLayout.LayoutParams) surpresinhaQtdInfo.getLayoutParams();
            textViewParams.topToBottom = R.id.maisMilionaria;
            surpresinhaQtdInfo.setLayoutParams(textViewParams);

            getQtdNumeros = parentActivity.qtdDezenasPossiveisSelecionado;
            qtdNumerosSelecionadasNovo.setText(String.format(Locale.getDefault(), "%d", getQtdNumeros));
            atualizaBotoesQtdNumeros();
        }
    }

    private void atualizaBotoesQtdNumeros() {
        if (getQtdNumeros > parametroJogo.getQuantidadeMinima() && getQtdNumeros < parametroJogo.getQuantidadeMaxima()){
            btnNumerosMenos.setBackgroundTintList(getCor(typeGameColorLight));
            btnNumerosMais.setBackgroundTintList(getCor(typeGameColorLight));
        }else if (getQtdNumeros == parametroJogo.getQuantidadeMinima()){
            btnNumerosMenos.setBackgroundTintList(getCor(R.color.cinzaescuro));
            btnNumerosMais.setBackgroundTintList(getCor(typeGameColorLight));
        } else {
            btnNumerosMenos.setBackgroundTintList(getCor(typeGameColorLight));
            btnNumerosMais.setBackgroundTintList(getCor(R.color.cinzaescuro));
        }
    }

    private boolean isDiaDeSorte() {
        return tipoJogo == ModalidadeEnum.DIA_DE_SORTE;
    }

    public void surpresinhaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle != null) {
            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
            corFonteFundoBranco = (int) dataBundle.get(SimularApostaActivity.COR_FONTE_FUNDO_BRANCO);
            corFonteFundoClaro = (int) dataBundle.get(SimularApostaActivity.COR_FONTE_FUNDO_CLARO);
            corFonteFundoEscuro = (int) dataBundle.get(SimularApostaActivity.COR_FONTE_FUNDO_ESCURO);
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

        MaterialButton botaoAdicionarSurpresinhaAoCarrinho = parentActivity.getBotaoAdicionarCompletarCartela();
        //botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorLight));
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(parentActivity, typeGameColorDark));
        botaoAdicionarSurpresinhaAoCarrinho.setOnClickListener(this);
        botaoAdicionarSurpresinhaAoCarrinho.setText(ViewUtils.textCaixaSTDBold(parentActivity, getString(R.string.adicionarAoCarrinho)));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(getResources().getColor(corFonteFundoEscuro));

        parentActivity.getBotaoLimparAposta().setVisibility(View.GONE);
        parentActivity.getSalvarApostaLayout().setVisibility(View.GONE);

        btnMenosSurpresinha.setBackgroundTintList(getCor(R.color.cinzaescuro));
        btnMaisSurpresinha.setBackgroundTintList(getCor(corFonteFundoBranco));
    }

    private void atualizaBotoesQtdSurpresinha() {
        if (qtdSurpresinhas == QUANTIDADE_MINIMA_SURPRESINHAS){
            btnMenosSurpresinha.setBackgroundTintList(getCor(R.color.cinzaescuro));
            btnMaisSurpresinha.setBackgroundTintList(getCor(corFonteFundoBranco));
        }else {
            btnMenosSurpresinha.setBackgroundTintList(getCor(corFonteFundoBranco));
            btnMaisSurpresinha.setBackgroundTintList(getCor(corFonteFundoBranco));
        }

        qtdSurpresinhasSelecionadas.setTextColor(ContextCompat.getColor(parentActivity, corFonteFundoBranco));
        qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
    }

    private void atualizaBotoesQtdTrevos() {
        if (qtdTrevos > parametroJogo.getTrevos().getQtdMinima() && qtdTrevos < parametroJogo.getTrevos().getQtdMaxima()){
            btnMenosTrevos.setBackgroundTintList(getCor(corFonteFundoBranco));
            btnMaisTrevos.setBackgroundTintList(getCor(corFonteFundoBranco));
        }else if (qtdTrevos == parametroJogo.getTrevos().getQtdMinima()){
            btnMenosTrevos.setBackgroundTintList(getCor(R.color.cinzaescuro));
            btnMaisTrevos.setBackgroundTintList(getCor(corFonteFundoBranco));
        } else {
            btnMenosTrevos.setBackgroundTintList(getCor(corFonteFundoBranco));
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
        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha(), getActivity(), corFonteFundoBranco);
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
        AnalyticsHelper.getInstance().logSelectContent(
                AnalyticsHelper.Tela.MONTAR_APOSTA,
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                AnalyticsHelper.ContentCategoryParams.CONFIG,
                ModalidadeEnum.fromString(tipoJogo),
                String.format(Locale.getDefault(), "%d", qtdSurpresinhas)
        );
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

        if (parametroJogo.getConcurso() != null && parametroJogo.getConcurso().getModalidade() != null
            && parametroJogo.getConcurso().getNumero() != null) {
            AnalyticsHelper.getInstance().logInteraction(
                    AnalyticsHelper.EventCategoryParams.CTA,
                    AnalyticsHelper.EventActionParams.CLICK,
                    AnalyticsHelper.EventLabelParams.ADD_TO_CART,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    ModalidadeEnum.fromString(parametroJogo.getConcurso().getModalidade()),
                    parametroJogo.getConcurso().getNumero().toString(),
                    String.valueOf(qtdSurpresinhas),
                    String.valueOf(parentActivity.qtdDezenasPossiveisSelecionado),
                    String.valueOf(parentActivity.qtdConcursoSelecionado),
                    ViewUtils.getMoedaFormat(valorTotalAposta)
            );
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
                if (!AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(getActivity())) {
                    adicionarSurpresinhaNoCarrinho();
                }
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

    private void escolherOutroItemBtn() {
        if (isTimemania()) {
            parentActivity.escolherOutroTimeSurpresinha();
        } else if (isDiaDeSorte()) {
            parentActivity.mesDeSorteFragment(null, true);
        }

    }

    private boolean isTimemania() {
        return tipoJogo == ModalidadeEnum.TIMEMANIA;
    }

    private void btnMenosSurpresinha() {
        if (qtdSurpresinhas > 1) {
            qtdSurpresinhas--;
            if (qtdSurpresinhas == 1) {
                atualizaBotoesQtdSurpresinha();
            }
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
            AnalyticsHelper.getInstance().logSelectContent(
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.ContentCategoryParams.CONFIG,
                    ModalidadeEnum.fromString(tipoJogo),
                    String.format(Locale.getDefault(), "%d", qtdSurpresinhas)
            );
        }
    }

    private void btnMaisSurpresinha() {
        if (qtdSurpresinhas == 1) {
            atualizaBotoesQtdSurpresinha();
        }

        qtdSurpresinhas++;

        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        qtdSurpresinhasSelecionadas.setText(String.format(Locale.getDefault(), "%d", qtdSurpresinhas));
        btnMenosSurpresinha.setBackgroundTintList(getCor(corFonteFundoBranco));
        AnalyticsHelper.getInstance().logSelectContent(
                AnalyticsHelper.Tela.MONTAR_APOSTA,
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                AnalyticsHelper.ContentCategoryParams.CONFIG,
                ModalidadeEnum.fromString(tipoJogo),
                String.format(Locale.getDefault(), "%d", qtdSurpresinhas)
        );

    }

    private void btnMaisTrevos() {
        if (qtdTrevos < parametroJogo.getTrevos().getQtdMaxima()) {
            qtdTrevos++;
            atualizaBotoesQtdTrevos();
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
        }
        if 	((qtdTrevos > (2)) && (!isAvisouT))
        {
            DialogUtils.dialogEntendi(
                    getActivity(),
                    getString(R.string.msg_qtd_max_ultrapassada_milionaria)
            );
            isAvisouT = true;
        }

    }

    private void btnMenosTrevos() {
        if (qtdTrevos > parametroJogo.getTrevos().getQtdMinima()) {
            qtdTrevos--;
            atualizaBotoesQtdTrevos();
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
        }
    }

    private void btnNumerosMais() {
        if (getQtdNumeros < parametroJogo.getQuantidadeMaxima()) {
            getQtdNumeros++;
            parentActivity.qtdDezenasPossiveisSelecionado = getQtdNumeros;
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdNumerosSelecionadasNovo.setText(String.format(Locale.getDefault(), "%d", getQtdNumeros));
        }
        atualizarButtonColor();
        parentActivity.qtdDezenasPossiveisSelecionado = getQtdNumeros;
        parentActivity.atualizaTextoBotoesQtdSelecionadas();
        if 	((getQtdNumeros > (6)) && (!isAvisouN))
        {
           DialogUtils.dialogEntendi(
                    getActivity(),
                    getString(R.string.msg_qtd_max_ultrapassada)
            );
            isAvisouN = true;
        }

    }

    private void btnNumerosMenos() {
        if (getQtdNumeros > parametroJogo.getQuantidadeMinima()) {
            getQtdNumeros--;
            parentActivity.qtdDezenasPossiveisSelecionado = getQtdNumeros;
            atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
            qtdNumerosSelecionadasNovo.setText(String.format(Locale.getDefault(), "%d", getQtdNumeros));
        }
        atualizarButtonColor();
        parentActivity.qtdDezenasPossiveisSelecionado = getQtdNumeros;
        parentActivity.atualizaTextoBotoesQtdSelecionadas();
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

    private void layoutEspelhoSurpresinha() {
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
        infoItemSurpresaTextView.setTextColor(getResources().getColor(corFonteFundoBranco));
        escolherOutroItemBtn.setText(R.string.label_escolher_outro_mes);
        escolherOutroItemBtn.setVisibility(View.GONE);
        escolhaItemLayout.setVisibility(View.VISIBLE);
    }

    public void resetQtdTrevos() {
        qtdTrevos = parametroJogo.getTrevos().getQtdMinima();
        atualizaValorTotalAposta(parentActivity.qtdDezenasPossiveisSelecionado, parentActivity.qtdConcursoSelecionado);
        qtdSurpresinhasSelecionadasTrevos.setText(String.format(Locale.getDefault(), "%d", qtdTrevos));
        btnMenosTrevos.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.cinzaescuro));
        btnMaisTrevos.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.milionaria_escuro_mkp));
    }
}
