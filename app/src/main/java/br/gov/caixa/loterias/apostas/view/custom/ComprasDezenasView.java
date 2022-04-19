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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DezenasAdapter;
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

    public void setLayout(ApostaDTO aposta) {
        switch (aposta.getModalidade()) {
            case TIMEMANIA:
                labelDetalheTxt.setText(R.string.label_time_coracao);
                detalheTxt.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getContext().getString(R.string.texto_futura_bold, aposta.getTimeDoCoracao().getNome())));
                detalheLayout.setVisibility(View.VISIBLE);
                break;

            case DIA_DE_SORTE:
                labelDetalheTxt.setText(R.string.label_mes_sorte);
                detalheTxt.setText(aposta.getMesDeSorte() == null ? "" : ViewUtils.textFuturaAndFuturaBold(getContext(), getContext().getString(R.string.texto_futura_bold, aposta.getMesDeSorte().getNome())));
                detalheLayout.setVisibility(View.VISIBLE);
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
                dezenasRecyclerView.setVisibility(View.GONE);
            } else {
                if (!aposta.getListaNumerosSelecionados().isEmpty()) {
                    dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listIntegerToListString(aposta.getListaNumerosSelecionados())));
                    dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        }
    }

    private void configuraMaisMilionariaFragment(ApostaDTO aposta) {
        FragmentManager fm = activity.getSupportFragmentManager();
        int             newContainerId = getNewContainerId(fm);
        rl_container_fragment.setId(newContainerId);

        List numeros = AppUtils.converteListaInteiros(aposta.getListaNumerosSelecionados());
        List trevos = AppUtils.converteListaInteiros(aposta.getListaTrevosSelecionados());

        NumerosFragment fragment = NumerosFragment.newInstance("",
                                                                     numeros, new ArrayList<>(),
                                                                     configuraEstiloNumeros(aposta), null);
        fm.beginTransaction().replace(newContainerId, fragment).commit();
        rl_container_fragment.setVisibility(VISIBLE);


        int             newContainerTrevosId = getNewContainerId(fm);
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
}
