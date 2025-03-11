package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoLoteca;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumerosSuperSete;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ComprasSuperSeteRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosBolaoRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaComprasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PartidasLotecaRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosFragment;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TrevosSurpresinhaFragment;

public class ComprasDezenasView extends LinearLayout {

    private boolean alreadyInflated = false;

    private RecyclerView dezenasRecyclerView;
    private LinearLayout detalheLayout;
    private RelativeLayout rl_container_fragment, rl_trevos_fragment;
    private TextView labelDetalheTxt, detalheTxt;

    protected DetalhesComprasActivity activity;
    private int corFonte;


    public ComprasDezenasView(DetalhesComprasActivity context) {
        super(context);
        activity = context;
    }

    public ComprasDezenasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ComprasDezenasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static ComprasDezenasView build(DetalhesComprasActivity context) {
        ComprasDezenasView instance = new ComprasDezenasView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_compras_dezenas_layout, this);
        }
        super.onFinishInflate();
        init();
    }

    private void init() {
        this.dezenasRecyclerView = findViewById(R.id.dezenasRecyclerView);
        this.detalheLayout = findViewById(R.id.detalheLayout);
        this.rl_container_fragment = findViewById(R.id.rl_container_fragment);
        this.rl_trevos_fragment = findViewById(R.id.rl_trevos_fragment);
        this.labelDetalheTxt = findViewById(R.id.labelDetalheTxt);
        this.detalheTxt = findViewById(R.id.detalheTxt);
    }

    public void setLayout(ApostaDTO aposta, int corFonte) {
        this.corFonte = corFonte;
        if (!aposta.getIndicadorCotaBolao()) {
            switch (aposta.getModalidade()) {
                case TIMEMANIA:
                    setDetalheLayout(R.string.label_time_coracao_dois_pontos, aposta, corFonte);
                    break;

                case DIA_DE_SORTE:
                    setDetalheLayout(R.string.label_mes_sorte_dois_pontos, aposta, corFonte);
                    break;

                default:
                    break;            }
        }
        initRecyclerView(aposta);
    }

    private void setDetalheLayout(int labelId, ApostaDTO aposta, int corFonte) {
        labelDetalheTxt.setText(labelId);
        if (isSurpresinhaNaoEfetivada(aposta)) {
            detalheTxt.setText(getContext().getString(R.string.label_tres_interrogacoes));
        } else {
            detalheTxt.setText(getDetalheText(aposta));
        }
        labelDetalheTxt.setTextColor(getContext().getResources().getColor(corFonte));
        detalheTxt.setTextColor(getContext().getResources().getColor(corFonte));
        detalheLayout.setVisibility(View.VISIBLE);
    }

    private String getDetalheText(ApostaDTO aposta) {
        switch (aposta.getModalidade()) {
            case TIMEMANIA:
                return ViewUtils.textCaixaSTDBold(getContext(),
                        getContext().getString(R.string.texto_caixaStd_bold,
                                aposta.getTimeDoCoracao().getNome() + "/" +
                                        aposta.getTimeDoCoracao().getUf()
                        )).toString();
            case DIA_DE_SORTE:
                return aposta.getMesDeSorte() == null ? "" : ViewUtils.textCaixaSTDBold(getContext(), getContext().getString(R.string.texto_caixaStd_bold, aposta.getMesDeSorte().getNome())).toString();
            default:
                return "";
        }
    }

    private boolean isSurpresinhaNaoEfetivada(ApostaDTO aposta) {
        return (aposta.getSurpresinha() && sitacoesApostaNaoEfetivada(aposta));
    }

    //Conforme solicitado no RTC 22631906
    private boolean sitacoesApostaNaoEfetivada(ApostaDTO aposta) {

        int situacao = aposta.getSituacao().getValor().intValue();

        return  situacao == 1 || //Não Registrada
                situacao == 2 || //Em Processamento
                situacao == 3 || //Em Processamento - SISPL
                situacao == 5 || //Não Efetivada - Dinheiro em Devolução
                situacao == 6 || //Não Efetivada - Dinheiro Devolvido
                situacao == 8 || //Cancelada
                situacao == 12; //Não Efetivada - Aguardando Devolução Pix
    }

    //Conforme solicitado no RTC 22631906
    private boolean situacoesApostaEfetivada(ApostaDTO aposta){
        int situacao = aposta.getSituacao().getValor().intValue();

        return  situacao == 4 ||  //Efetivada
                situacao == 7 || //Pagamento em Processamento
                situacao == 9 || //Não Premiada
                situacao == 10 || //Prescrita
                situacao == 11; //Prêmio Pago
    }

    private void initRecyclerView(ApostaDTO aposta) {
        if (aposta == null) {
            return;
        }
        switch (aposta.getModalidade()) {
            case LOTECA:
                configuraLotecaRecyclerView(aposta);
                break;
            case SUPER_7:
                configuraSuper7RecyclerView(aposta);
                break;
            case MAIS_MILIONARIA:
                configuraMaisMilionariaRecyclerView(aposta);
                break;
            default:
                configuraRecyclerViewPadrao(aposta);
                break;
        }
    }

    private void configuraLotecaRecyclerView(ApostaDTO aposta) {
        if (aposta.getIndicadorCotaBolao()) {
            PartidasLotecaRecyclerViewAdapter partidasAdapter = new PartidasLotecaRecyclerViewAdapter(
                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao().get(0).getPartidasLoteca(),
                    activity, getConfigitacaoLoteca());
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            dezenasRecyclerView.setAdapter(partidasAdapter);
        } else {
            LotecaComprasAdapter lotecaAdapter = new LotecaComprasAdapter(aposta.getPartidasLoteca());
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            dezenasRecyclerView.setAdapter(lotecaAdapter);
        }
    }

    private void configuraSuper7RecyclerView(ApostaDTO aposta) {
        if (aposta.getIndicadorCotaBolao()) {
            ComprasSuperSeteRecyclerViewAdapter superSeteAdpter = new ComprasSuperSeteRecyclerViewAdapter(
                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
                    getContext(), getDezenaConfigSuper7());
            dezenasRecyclerView.setAdapter(superSeteAdpter);
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            if (isSurpresinhaNaoEfetivada(aposta)) {
                dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(true, getMascaraSurpresinha(aposta.getQuantidadeNumeros()), new ArrayList<>(), getDezenaConfigSurpresinha(new EstiloModalidadeMKP(aposta.getModalidade())), null));
                dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
            } else {
                if (!aposta.getMatrizNumerosSelecionados().isEmpty()) {
                    configuraSuperSeteFragment(aposta);
                    dezenasRecyclerView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void configuraMaisMilionariaRecyclerView(ApostaDTO aposta) {
        if (aposta.getIndicadorCotaBolao()) {
            JogosBolaoRecyclerView adpterJogos = new JogosBolaoRecyclerView(aposta.getModalidade(), corFonte,
                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
                    new ArrayList<>(), getDezenaConfig(), getTrevoConfig());
            dezenasRecyclerView.setAdapter(adpterJogos);
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            configuraMaisMilionariaFragment(aposta);
        }
    }

    private void configuraRecyclerViewPadrao(ApostaDTO aposta) {
        if (aposta.getIndicadorCotaBolao()) {
            JogosBolaoRecyclerView adpterJogos = new JogosBolaoRecyclerView(aposta.getModalidade(), corFonte,
                    aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
                    new ArrayList<>(), getDezenaConfig());
            dezenasRecyclerView.setAdapter(adpterJogos);
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            if (isSurpresinhaNaoEfetivada(aposta)) {
                dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(true, getMascaraSurpresinha(aposta.getQuantidadeNumeros()), new ArrayList<>(), getDezenaConfigSurpresinha(new EstiloModalidadeMKP(aposta.getModalidade())), null));
                dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
            } else {
                dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(getDezenas(aposta.getListaNumerosSelecionados()), new ArrayList<>(), getDezenaConfig(), null));
                dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
            }
        }
    }

    private void configuraMaisMilionariaFragment(ApostaDTO aposta) {

        if(isSurpresinhaNaoEfetivada(aposta)){
            dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(true, getMascaraSurpresinha(aposta.getQuantidadeNumeros()), new ArrayList<>(), getDezenaConfigSurpresinha(new EstiloModalidadeMKP(aposta.getModalidade())), null));
            dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
            List trevos;

            trevos = getMascaraSurpresinha(aposta.getQtdDeTrevosMaisMilionaria());

            rl_container_fragment.setVisibility(GONE);
            final FragmentManager fm = activity.getSupportFragmentManager();
            int newContainerTrevosId = getNewContainerId(fm);
            rl_trevos_fragment.setId(newContainerTrevosId);
            TrevosSurpresinhaFragment trevosFrag = TrevosSurpresinhaFragment.newInstance(activity.getResources().getString(R.string.trevosTitulo),
                    trevos,
                    configuraEstiloNumerosTrevos(aposta), getTrevoShape());

            fm.beginTransaction().replace(newContainerTrevosId, trevosFrag).commit();
            rl_trevos_fragment.setVisibility(VISIBLE);

           }else{
            dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(getDezenas(aposta.getListaNumerosSelecionados()), new ArrayList<>(), getDezenaConfig(), null));
            dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));

            List trevos = AppUtils.converteListaInteiros(aposta.getListaTrevosSelecionados());
            rl_container_fragment.setVisibility(GONE);
            final FragmentManager fm = activity.getSupportFragmentManager();
            int newContainerTrevosId = getNewContainerId(fm);
            rl_trevos_fragment.setId(newContainerTrevosId);
            NumerosFragment trevosFrag = NumerosFragment.newInstance(activity.getResources().getString(R.string.trevosTitulo),
                    trevos, trevos,
                    configuraEstiloNumerosTrevos(aposta), getTrevoShape());
            fm.beginTransaction().replace(newContainerTrevosId, trevosFrag).commit();
            rl_trevos_fragment.setVisibility(VISIBLE);
        }
    }
    private ShapeConfig getTrevoShape() {
        return new ShapeConfig(R.drawable.ic_item_trevo_branco,
                               R.drawable.ic_item_trevo_branco,
                               R.color.branco, R.color.branco);
    }

    private DezenaConfig getTrevoConfig() {
        ShapeConfig shapeConfig = new ShapeConfig(R.drawable.ic_item_trevo,
                R.drawable.ic_item_trevo_selecionado,
                R.color.branco, R.color.milionaria_escuro_mkp);

        return new DezenaConfig(false, corFonte,
                R.layout.item_dezena_detalhe,
                shapeConfig,false);
    }
    
    private ConfiguracaoNumeros configuraEstiloNumerosTrevos(ApostaDTO aposta) {
        return new ConfiguracaoNumeros(new EstiloModalidadeMKP(aposta.getModalidade()).getCorLetraLista(),
                new EstiloModalidadeMKP(aposta.getModalidade()).getCorFonteFundoClaro());
    }

    private void configuraSuperSeteFragment(ApostaDTO aposta) {
        FragmentManager fm = activity.getSupportFragmentManager();
        int             newContainerId = getNewContainerId(fm);
        rl_container_fragment.setId(newContainerId);
        NumerosSuperSeteFragment fragment =
                NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados(),
                                                     configuraEstiloSuperSete(aposta.getModalidade()));
        fm.beginTransaction().replace(newContainerId, fragment).commit();
        rl_container_fragment.setVisibility(View.VISIBLE);

    }

    private int getNewContainerId(FragmentManager fm){
        int             containerId = rl_container_fragment.getId();
        Fragment oldFragment = fm.findFragmentById(containerId);
        if (oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }

        return (int) Utils.getIdUnico();
    }

    private DezenaConfig getDezenaConfig() {
        ShapeConfig shapeConfig = new ShapeConfig(R.color.branco, R.color.mega_verde_escuro_mkp);

        return new DezenaConfig(false, corFonte,
                R.layout.item_dezena_detalhe,
                shapeConfig,false);
    }

    private DezenaConfig getDezenaConfigSurpresinha(EstiloModalidadeMKP estilo) {
        ShapeConfig shapeConfig = new ShapeConfig(estilo.getCorClara(), R.color.mega_verde_escuro_mkp);

        return new DezenaConfig(false, corFonte,
                R.layout.item_dezena_detalhe,
                shapeConfig,false);
    }
    private DezenaConfig getDezenaConfigSuper7() {
        ShapeConfig shapeConfig = new ShapeConfig(R.color.branco, R.color.mega_verde_escuro_mkp);

        return new DezenaConfig(false, corFonte,
                R.layout.item_dezena_detalhe_super7,
                shapeConfig,false);
    }

    private List<Dezena> getDezenas(List<Integer> listaInteiros) {
        List<Dezena> dezenas = new ArrayList<>();
        for (int i = 0; i < listaInteiros.size(); i++) {
            if (listaInteiros.get(i) == 100 || listaInteiros.get(i) == 0){
                dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE, "00"));
            } else {
                dezenas.add(new Dezena("" + listaInteiros.get(i), Boolean.FALSE, "" + listaInteiros.get(i)));
            }

        }
        return dezenas;
    }

    private List<Dezena> getMascaraSurpresinha(Integer qtdNumeros) {
        List<Dezena> dezenas = new ArrayList<>();
        for (int i = 0; i < qtdNumeros; i++) {
            dezenas.add(new Dezena("XX", Boolean.FALSE, "XX"));
        }
        return dezenas;
    }

    public ConfiguracaoNumerosSuperSete configuraEstiloSuperSete(ModalidadeEnum modalidade) {
        EstiloModalidadeMKP estiloMKP = new EstiloModalidadeMKP(modalidade);

        return new ConfiguracaoNumerosSuperSete(
                estiloMKP.getCorClara(),            //corInteriorCirculo
                R.color.branco,                     //corInteriorSelecionado
                estiloMKP.getCorFonteFundoClaro(),  //corTextoCirculo
                R.color.branco,                     //corTextoCirculoSelecionado
                estiloMKP.getCorFonteFundoClaro(),  //corBordaCirculo
                R.color.branco,                     //corBordaCirculoSelecionado
                estiloMKP.getCorEscura(),           //corInteriorQuadrado
                estiloMKP.getCorFonteFundoEscuro(), //textoQuadrado
                estiloMKP.getCorEscura());          //corBordaQuadrado
    }

    private ConfiguracaoLoteca getConfigitacaoLoteca() {
        return new ConfiguracaoLoteca(R.color.branco);
    }
}
