package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DezenasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DezenasResultadoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.JogosBolaoRecyclerView;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosFragment;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;

@EViewGroup(R.layout.item_compras_dezenas_layout)
public class ComprasDezenasView extends LinearLayout {

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @ViewById
    protected LinearLayout detalheLayout;

    @ViewById
    protected RelativeLayout rl_container_fragment, rl_trevos_fragment;

    @ViewById
    protected TextView labelDetalheTxt, detalheTxt;

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

    public void setLayout(ApostaDTO aposta, int corFonte) {
        this.corFonte = corFonte;
        switch (aposta.getModalidade()) {
            case TIMEMANIA:
                if(!aposta.getIndicadorCotaBolao()) {
                    labelDetalheTxt.setText(R.string.label_time_coracao_dois_pontos);
                    detalheTxt.setText(ViewUtils.textFuturaAndFuturaBold(getContext(),
                            getContext().getString(R.string.texto_futura_bold,
                                    aposta.getTimeDoCoracao().getNome() + "/" +
                                            aposta.getTimeDoCoracao().getUf()
                            )));
                    detalheLayout.setVisibility(View.VISIBLE);
                }
                break;

            case DIA_DE_SORTE:
                if(!aposta.getIndicadorCotaBolao()) {
                    labelDetalheTxt.setText(R.string.label_mes_sorte_dois_pontos);
                    detalheTxt.setText(aposta.getMesDeSorte() == null ? "" : ViewUtils.textFuturaAndFuturaBold(getContext(), getContext().getString(R.string.texto_futura_bold, aposta.getMesDeSorte().getNome())));
                    detalheLayout.setVisibility(View.VISIBLE);
                }
                break;

            default:
                break;
        }
        initRecyclerView(aposta);
    }

    private void initRecyclerView(ApostaDTO aposta) {
        if (aposta != null) {
            if  (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
                if (!aposta.getMatrizNumerosSelecionados().isEmpty()) {
                    configuraSuperSeteFragment(aposta);
                    dezenasRecyclerView.setVisibility(View.GONE);
                }
            } else if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                configuraMaisMilionariaFragment(aposta);
            } else {
                if(aposta.getIndicadorCotaBolao()) {
                    JogosBolaoRecyclerView adpterJogos = new JogosBolaoRecyclerView(aposta.getModalidade(), corFonte,
                            aposta.getReservaCotaBolao().getDescricaoCotaBolao().getListApostasBolao(),
                            new ArrayList<>(), getDezenaConfig());
                    dezenasRecyclerView.setAdapter(adpterJogos);
                    dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(getDezenas(aposta.getListaNumerosSelecionados()), new ArrayList<>(), getDezenaConfig(), null));
                    dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
                }
            }
        }
    }

    private void configuraMaisMilionariaFragment(ApostaDTO aposta) {
        dezenasRecyclerView.setAdapter(new ListaDezenaRecyclerView(getDezenas(aposta.getListaNumerosSelecionados()), new ArrayList<>(), getDezenaConfig(), null));
        dezenasRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));

        List trevos = AppUtils.converteListaInteiros(aposta.getListaTrevosSelecionados());
        rl_container_fragment.setVisibility(GONE);
        final FragmentManager fm = activity.getSupportFragmentManager();
        int newContainerTrevosId = getNewContainerId(fm);
        rl_trevos_fragment.setId(newContainerTrevosId);
        NumerosFragment trevosFrag = NumerosFragment.newInstance(activity.getResources().getString(R.string.trevosTitulo),
                                                               trevos, trevos,
                                                               configuraEstiloNumeros(aposta), getTrevoShape());
        fm.beginTransaction().replace(newContainerTrevosId, trevosFrag).commit();
        rl_trevos_fragment.setVisibility(VISIBLE);
    }

    private ShapeConfig getTrevoShape() {
        return new ShapeConfig(R.drawable.ic_item_trevo,
                               R.drawable.ic_item_trevo_selecionado,
                               R.color.branco, R.color.mais_milionaria_escura);
    }

    private ConfiguracaoNumeros configuraEstiloNumeros(ApostaDTO aposta) {
        EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());
        if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
            return new ConfiguracaoNumeros(estilo.getCorClara(), R.color.branco);
        } else {
            return new ConfiguracaoNumeros(estilo.getCorEscura(), R.color.branco);
        }
    }

    private void configuraSuperSeteFragment(ApostaDTO aposta) {
        FragmentManager fm = activity.getSupportFragmentManager();
        int             newContainerId = getNewContainerId(fm);
        rl_container_fragment.setId(newContainerId);
        NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados());
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
}
