package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.DezenasAdapter;

@EViewGroup(R.layout.item_compras_dezenas_layout)
public class ComprasDezenasView extends LinearLayout {

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @ViewById
    protected LinearLayout detalheLayout;

    @ViewById
    protected RelativeLayout rl_container_fragment;

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
                    configuraFragment(aposta);
                    dezenasRecyclerView.setVisibility(View.GONE);

                }
            } else {
                if (!aposta.getListaNumerosSelecionados().isEmpty()) {
                    dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listIntegerToListString(aposta.getListaNumerosSelecionados())));
                    dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        }
    }

    private void configuraFragment(ApostaDTO aposta) {
        FragmentManager fm          = activity.getSupportFragmentManager();
        int             containerId = rl_container_fragment.getId();
        Fragment        oldFragment = fm.findFragmentById(containerId);
        if (oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }

        int newContainerId = (int) Utils.getIdUnico();
        rl_container_fragment.setId(newContainerId);
        NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados());
        fm.beginTransaction().replace(newContainerId, fragment).commit();
        rl_container_fragment.setVisibility(View.VISIBLE);

    }
}
