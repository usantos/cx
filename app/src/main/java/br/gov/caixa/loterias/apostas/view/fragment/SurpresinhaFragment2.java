package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.model.SurpresinhaApostaModel;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SurpresinhaGridViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostas.viewModel.SimulaViewModel;

public class SurpresinhaFragment2 extends Fragment {

    private static final int QUANTIDADE_MINIMA_SURPRESINHAS = 1;

    private TextView valorAposta;
    private SimulaActivity parentActivity;
    private ParametroEquipe equipeSelecionada = null;
    private ModalidadeEnum tipoJogo;
    private ParametroJogoDTO parametroJogo;
    private int typeGameColorLight;
    private int corFonteFundoBranco;
    private SurpresinhaGridViewAdapter simularDezenasAdapter;
    private BigDecimal valorTotalAposta;
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
    private SimulaViewModel viewModel;

    public SurpresinhaFragment2() {}

    public static SurpresinhaFragment2 newInstance(){
        return new SurpresinhaFragment2();
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
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUiState()
                .observe(
                        getViewLifecycleOwner(),
                        this::renderState
                );
    }
    private void renderState(
            SimulaUiState state
    ) {

        renderQuantidadeSurpresinhas(state);
        renderQuantidadeNumeros(state);
        renderQuantidadeTrevos(state);
        renderValorAposta(state);

        atualizaBotoesQtdSurpresinha(state);

        if (parametroJogo != null
                && parametroJogo.isTipoJogo(
                ModalidadeEnum.MAIS_MILIONARIA
        )) {

            atualizaBotoesQtdTrevos(state);
        }

        atualizaBotoesQtdNumeros(state);
    }

    private void renderQuantidadeSurpresinhas(
            SimulaUiState state
    ) {
        qtdSurpresinhasSelecionadas.setText(
                String.valueOf(
                        state.getQuantidadeSurpresinhas()
                )
        );
    }
    private void renderQuantidadeTrevos(
            SimulaUiState state
    ) {
        qtdSurpresinhasSelecionadasTrevos.setText(
                String.valueOf(
                        state.getQuantidadeTrevosSurpresinha()
                )
        );
    }
    private void renderQuantidadeNumeros(
            SimulaUiState state
    ) {
        qtdNumerosSelecionadasNovo.setText(
                String.valueOf(
                        state.getQuantidadeNumerosSurpresinha()
                )
        );
    }
    private SimulaUiState getStateAtual() {

        if (viewModel == null
                || viewModel.getUiState() == null
                || viewModel.getUiState().getValue() == null) {
            return SimulaUiState.initial();
        }

        return viewModel.getUiState().getValue();
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

        parentActivity = ((SimulaActivity) getActivity());
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
        }
        viewModel =
                new ViewModelProvider(
                        requireActivity()
                ).get(SimulaViewModel.class);
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

        }
    }

    private void atualizaBotoesQtdNumeros(
            SimulaUiState state
    ) {

        int qtdNumeros =
                state.getQuantidadeNumerosSurpresinha();

        int minimo =
                parametroJogo.getQuantidadeMinima();

        int maximo =
                parametroJogo.getQuantidadeMaxima();

        if (qtdNumeros > minimo
                && qtdNumeros < maximo) {

            btnNumerosMenos.setBackgroundTintList(
                    getCor(typeGameColorLight)
            );

            btnNumerosMais.setBackgroundTintList(
                    getCor(typeGameColorLight)
            );

        } else if (qtdNumeros == minimo) {

            btnNumerosMenos.setBackgroundTintList(
                    getCor(R.color.cinzaescuro)
            );

            btnNumerosMais.setBackgroundTintList(
                    getCor(typeGameColorLight)
            );

        } else {

            btnNumerosMenos.setBackgroundTintList(
                    getCor(typeGameColorLight)
            );

            btnNumerosMais.setBackgroundTintList(
                    getCor(R.color.cinzaescuro)
            );
        }
    }

    private boolean isDiaDeSorte() {
        return tipoJogo == ModalidadeEnum.DIA_DE_SORTE;
    }

    public void surpresinhaFragmentBuilder(Bundle dataBundle) {
        if (dataBundle == null) {
            return;
        }

        typeGameColorLight = dataBundle.getInt("typeGameColorLight");

        corFonteFundoBranco = dataBundle.getInt(SimulaActivity.COR_FONTE_FUNDO_BRANCO);

    }

    private void identificarTipoJogo() {
        tipoJogo = (ModalidadeEnum) getActivity().getIntent().getSerializableExtra("tipoAposta");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // METODOS DE CONFIGURACOES
    private void atualizaDezenaAdapter() {
        simularDezenasAdapter.atualizaDezenas(montaListaDezenasSurpresinha());
    }

    private void configuraBotoes() {

        if (parentActivity == null
                || parametroJogo == null) {
            return;
        }

        valorAposta =
                parentActivity.getValorApostaCartela();

        if (viewModel != null) {

            viewModel.onEvent(
                    new SimulaUiEvent.SurpresinhaAberta()
            );
        }
    }

    private void atualizaBotoesQtdSurpresinha(
            SimulaUiState state
    ) {

        int quantidade =
                state.getQuantidadeSurpresinhas();

        if (quantidade <= QUANTIDADE_MINIMA_SURPRESINHAS) {

            btnMenosSurpresinha.setBackgroundTintList(
                    getCor(R.color.cinzaescuro)
            );

            btnMaisSurpresinha.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );

        } else {

            btnMenosSurpresinha.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );

            btnMaisSurpresinha.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );
        }
    }

    private void atualizaBotoesQtdTrevos(
            SimulaUiState state
    ) {

        int qtdTrevos =
                state.getQuantidadeTrevosSurpresinha();

        int minimo =
                parametroJogo.getTrevos()
                        .getQtdMinima();

        int maximo =
                parametroJogo.getTrevos()
                        .getQtdMaxima();

        if (qtdTrevos > minimo
                && qtdTrevos < maximo) {

            btnMenosTrevos.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );

            btnMaisTrevos.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );

        } else if (qtdTrevos == minimo) {

            btnMenosTrevos.setBackgroundTintList(
                    getCor(R.color.cinzaescuro)
            );

            btnMaisTrevos.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );

        } else {

            btnMenosTrevos.setBackgroundTintList(
                    getCor(corFonteFundoBranco)
            );

            btnMaisTrevos.setBackgroundTintList(
                    getCor(R.color.cinzaescuro)
            );
        }
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


    public void adicionarSurpresinhaNoCarrinho() {

        SimulaUiState state =
                getStateAtual();

        if (state == null
                || parametroJogo == null) {
            return;
        }

        IncluirSurpresinhaDTO surpresinha;

        int quantidadeNumeros =
                state.getQuantidadeNumerosSurpresinha();

        int quantidadeSurpresinhas =
                state.getQuantidadeSurpresinhas();

        int quantidadeConcursos =
                state.getQtdConcursoSelecionado();

        BigDecimal valorAposta =
                state.getValorAposta();

        switch (
                parametroJogo.getConcurso()
                        .getModalidade()
        ) {

            case TIMEMANIA:

                surpresinha =
                        ApostaUtils.getSurpresinhaTimemania(
                                parametroJogo,
                                quantidadeNumeros,
                                quantidadeSurpresinhas,
                                quantidadeConcursos,
                                valorAposta,
                                equipeSelecionada
                        );

                break;

            case LOTOMANIA:

                surpresinha =
                        ApostaUtils.getSurpresinhaLotomania(
                                parametroJogo,
                                quantidadeNumeros,
                                quantidadeSurpresinhas,
                                quantidadeConcursos,
                                valorAposta,
                                selcioneEspelhoSurpresinha
                                        .isChecked()
                        );

                break;

            case MAIS_MILIONARIA:

                surpresinha =
                        ApostaUtils.getSurpresinhaMaisMilionaria(
                                parametroJogo,
                                quantidadeNumeros,
                                quantidadeSurpresinhas,
                                quantidadeConcursos,
                                valorAposta,
                                state.getQuantidadeTrevosSurpresinha()
                        );

                break;

            default:

                surpresinha =
                        ApostaUtils.getSurpresinha(
                                parametroJogo,
                                quantidadeNumeros,
                                quantidadeSurpresinhas,
                                quantidadeConcursos,
                                valorAposta
                        );

                break;
        }

        model.addSurpresinhaCarrinho(
                surpresinha
        );
    }

    private boolean isLotomania() {
        return tipoJogo == ModalidadeEnum.LOTOMANIA;
    }

    public void setEquipeSelecionada(ParametroEquipe equipeSelecionada) {
        this.equipeSelecionada = equipeSelecionada;
    }

    public ParametroJogoDTO getParametroJogo() {
        return parametroJogo;
    }

    private void escolherOutroItemBtn() {
        if (isTimemania()) {
            parentActivity.escolherOutroTimeSurpresinha();
        } else if (isDiaDeSorte()) {
            parentActivity.mesDeSorteFragment(null);
        }

    }

    private boolean isTimemania() {
        return tipoJogo == ModalidadeEnum.TIMEMANIA;
    }


    private void btnMaisSurpresinha() {

        SimulaUiState state =
                getStateAtual();

        int novaQtd =
                state.getQuantidadeSurpresinhas() + 1;

        viewModel.onEvent(
                new SimulaUiEvent
                        .AtualizarQuantidadeSurpresinhas(
                        novaQtd
                )
        );
    }

    private void btnMenosSurpresinha() {

        SimulaUiState state =
                getStateAtual();

        int atual =
                state.getQuantidadeSurpresinhas();

        if (atual <= 1) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent
                        .AtualizarQuantidadeSurpresinhas(
                        atual - 1
                )
        );
    }
    private void btnMaisTrevos() {

        SimulaUiState state =
                getStateAtual();

        int atual =
                state.getQuantidadeTrevosSurpresinha();

        if (atual <
                parametroJogo.getTrevos()
                        .getQtdMaxima()) {

            viewModel.onEvent(
                    new SimulaUiEvent
                            .AtualizarQuantidadeTrevosSurpresinha(
                            atual + 1
                    )
            );
        }

        if ((atual + 1 > 2)
                && !isAvisouT) {

            DialogUtils.dialogEntendi(
                    getActivity(),
                    getString(
                            R.string.msg_qtd_max_ultrapassada_milionaria
                    )
            );

            isAvisouT = true;
        }
    }
    private void btnMenosTrevos() {

        SimulaUiState state =
                getStateAtual();

        int atual =
                state.getQuantidadeTrevosSurpresinha();

        int minimo =
                parametroJogo.getTrevos()
                        .getQtdMinima();

        if (atual > minimo) {

            viewModel.onEvent(
                    new SimulaUiEvent
                            .AtualizarQuantidadeTrevosSurpresinha(
                            atual - 1
                    )
            );
        }
    }
    private void btnNumerosMais() {

        SimulaUiState state =
                getStateAtual();

        int atual =
                state.getQuantidadeNumerosSurpresinha();

        if (atual <
                parametroJogo.getQuantidadeMaxima()) {

            int novo =
                    atual + 1;

            viewModel.onEvent(
                    new SimulaUiEvent
                            .AtualizarQuantidadeNumerosSurpresinha(
                            novo
                    )
            );

            if ((novo > 6)
                    && !isAvisouN) {

                DialogUtils.dialogEntendi(
                        getActivity(),
                        getString(
                                R.string.msg_qtd_max_ultrapassada
                        )
                );

                isAvisouN = true;
            }
        }
    }
    private void btnNumerosMenos() {

        SimulaUiState state =
                getStateAtual();

        int atual =
                state.getQuantidadeNumerosSurpresinha();

        if (atual >
                parametroJogo.getQuantidadeMinima()) {

            viewModel.onEvent(
                    new SimulaUiEvent
                            .AtualizarQuantidadeNumerosSurpresinha(
                            atual - 1
                    )
            );
        }
    }
    private void renderValorAposta(
            SimulaUiState state
    ) {

        if (valorAposta == null) {
            return;
        }

        ViewUtils.setMoedaFormatHtml(
                state.getValorAposta(),
                valorAposta
        );
    }

    private void layoutEspelhoSurpresinha() {
        selecionarEspelho();
    }

    private void selecionarEspelho() {

        selcioneEspelhoSurpresinha.setChecked(
                !selcioneEspelhoSurpresinha.isChecked()
        );

        SimulaUiState state = getStateAtual();

        viewModel.onEvent(
                new SimulaUiEvent
                        .AtualizarQuantidadeSurpresinhas(
                        state.getQuantidadeSurpresinhas()
                )
        );
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

        if (viewModel == null
                || parametroJogo == null
                || parametroJogo.getTrevos() == null) {
            return;
        }

        viewModel.onEvent(
                new SimulaUiEvent
                        .AtualizarQuantidadeTrevosSurpresinha(
                        parametroJogo
                                .getTrevos()
                                .getQtdMinima()
                )
        );
    }
    public void onAdicionarRodapeClicado() {
        adicionarSurpresinhaNoCarrinho();
    }
}
