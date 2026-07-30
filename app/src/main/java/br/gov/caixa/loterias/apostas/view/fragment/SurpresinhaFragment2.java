package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.SurpresinhaGridViewAdapter2;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class SurpresinhaFragment2 extends BaseEtapaFragment {

    private static final int QUANTIDADE_MINIMA_SURPRESINHAS = 1;

    private TextView valorAposta;
    private SimulaActivity parentActivity;
    private ParametroEquipe equipeSelecionada = null;
    private ModalidadeEnum tipoJogo;
    private ParametroJogoDTO parametroJogo;
    private int typeGameColorLight;
    private int corFonteFundoBranco;
    private SurpresinhaGridViewAdapter2 simularDezenasAdapter;
    private BigDecimal valorTotalAposta;
    private boolean isAvisouT = false;
    private boolean isAvisouN = false;
    private View view;

    private TextView qtdSurpresinhasSelecionadas,
            surpresinhaQtdInfo, qtdSurpresinhasSelecionadasTrevos, qtdNumerosSelecionadasNovo;

    private Button escolherOutroItemBtn, btnMenosSurpresinha, btnMaisSurpresinha, btnMenosTrevos,
            btnMaisTrevos, btnNumerosMenos, btnNumerosMais;
    private LinearLayout escolhaItemLayout;
    private ExpandableHeightGridView listaDezenasSurpresinha;
    private LinearLayout layoutEspelhoSurpresinha;
    private AppCompatCheckBox selcioneEspelhoSurpresinha;
    private ConstraintLayout maisMilionaria, numeroNovoConst;
    private ConstraintLayout mesesContainer;

    public SurpresinhaFragment2() {}

    public static SurpresinhaFragment2 newInstance(){
        return new SurpresinhaFragment2();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_surpresinha_novo, container, false);
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
    private void setViews() {
        qtdSurpresinhasSelecionadas = view.findViewById(R.id.qtdSurpresinhasSelecionadas);
        surpresinhaQtdInfo = view.findViewById(R.id.surpresinhaQtdInfo);
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
        mesesContainer = view.findViewById(R.id.mesesContainer);
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

        AppCenterManager.registraEvento("ENTROU_SURPRESINHA");

        parentActivity = ((SimulaActivity) getActivity());
        if (parentActivity != null) {
            parametroJogo = parentActivity.getParametroSimulacao();
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

        if (isTimemania()) {
            escolhaItemLayout.setVisibility(View.VISIBLE);
        }

        if (isDiaDeSorte()) {
            mesesContainer.setVisibility(View.VISIBLE);
        }


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
                    getCor(R.color.cinza110)
            );

            btnNumerosMais.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

        } else if (qtdNumeros == minimo) {

            btnNumerosMenos.setBackgroundTintList(
                    getCor(R.color.linhaDivisa)
            );

            btnNumerosMais.setBackgroundTintList(
                    getCor(R.color.linhaDivisa)
            );

        } else {

            btnNumerosMenos.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

            btnNumerosMais.setBackgroundTintList(
                    getCor(R.color.linhaDivisa)
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

        valorAposta = parentActivity.getValorApostaCartela();
        dispatch(new SimulaUiEvent.SurpresinhaAberta());

    }

    private void atualizaBotoesQtdSurpresinha(
            SimulaUiState state
    ) {

        int quantidade =
                state.getQuantidadeSurpresinhas();

        if (quantidade <= QUANTIDADE_MINIMA_SURPRESINHAS) {

            btnMenosSurpresinha.setBackgroundTintList(
                    getCor(R.color.linhaDivisa)
            );

            btnMaisSurpresinha.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

        } else {

            btnMenosSurpresinha.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

            btnMaisSurpresinha.setBackgroundTintList(
                    getCor(R.color.cinza110)
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
                    getCor(R.color.cinza110)
            );

            btnMaisTrevos.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

        } else if (qtdTrevos == minimo) {

            btnMenosTrevos.setBackgroundTintList(
                    getCor(R.color.linhaDivisa)
            );

            btnMaisTrevos.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

        } else {

            btnMenosTrevos.setBackgroundTintList(
                    getCor(R.color.cinza110)
            );

            btnMaisTrevos.setBackgroundTintList(
                    getCor(R.color.linhaDivisa)
            );
        }
    }

    private ColorStateList getCor(int cor) {
        return ContextCompat.getColorStateList(getActivity(), cor);
    }

    private void configuraListaDezenas() {
        listaDezenasSurpresinha.setExpanded(true);
        simularDezenasAdapter = new SurpresinhaGridViewAdapter2(getActivity(), montaListaDezenasSurpresinha(), getActivity());
        listaDezenasSurpresinha.setAdapter(simularDezenasAdapter);
    }

    private ArrayList<String> montaListaDezenasSurpresinha() {
        ArrayList<String> arrayDezenas = new ArrayList<>();
        for (int i = 0; i < Constantes.SEIS_INTEIRO; i++) {
            arrayDezenas.add("XX");
        }

        return arrayDezenas;
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
            dispatch(new SimulaUiEvent.SolicitarSelecaoTimeCoracao());
            return;
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

        dispatch(
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
        dispatch(
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
            dispatch(
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
            dispatch(
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

            dispatch(
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

            dispatch(
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
        BigDecimal valor = state.getValorAposta();

        if (selcioneEspelhoSurpresinha != null
                && selcioneEspelhoSurpresinha.isChecked()) {
            valor = valor.multiply(BigDecimal.valueOf(2));
        }

        ViewUtils.setMoedaFormatHtml(
                valor,
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

        dispatch(
                new SimulaUiEvent
                        .AtualizarQuantidadeSurpresinhas(
                        state.getQuantidadeSurpresinhas()
                )
        );
    }

    public void resetQtdTrevos() {

        if (parametroJogo == null || parametroJogo.getTrevos() == null) {
            return;
        }

        dispatch(
                new SimulaUiEvent
                        .AtualizarQuantidadeTrevosSurpresinha(
                        parametroJogo
                                .getTrevos()
                                .getQtdMinima()
                )
        );
    }
    public void onAdicionarRodapeClicado() {
        dispatch(
                new SimulaUiEvent.AdicionarSurpresinhaCarrinho(
                        selcioneEspelhoSurpresinha.isChecked()
                )
        );
    }
    protected ApostaFavoritaDTO criarApostaFavoritaDTO(){
        ApostaFavoritaDTO apostaFavorita =
                new ApostaFavoritaDTO();

        return apostaFavorita;
    }
    @Override
    public void onSalvarFavoritoRodapeConfirmado(
            String nome
    ) {

    }
    @Override
    public void onCompletarRodapeClicado() {

    }
    @Override
    public void onLimparRodapeClicado() {

    }
    @Override
    public boolean possuiSelecaoValida() {
        return true;
    }
    @Override
    public EtapaAposta getEtapa() {
        return EtapaAposta.NUMEROS;
    }
}
