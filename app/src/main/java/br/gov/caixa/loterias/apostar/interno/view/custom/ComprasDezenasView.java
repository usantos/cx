package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DezenasAdapter;

@EViewGroup(R.layout.item_compras_dezenas_layout)
public class ComprasDezenasView extends LinearLayout {

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @ViewById
    protected LinearLayout detalheLayout;

    @ViewById
    protected TextView labelDetalheTxt, detalheTxt;

    public ComprasDezenasView(Context context) {
        super(context);
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
        if (!aposta.getNumerosSelecionados().isEmpty()) {
            if (aposta.getIndicadorSurpresinha().getValor() == 2) {
                dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listDezenasSurpresinha(aposta.getNumerosSelecionados().size())));
            } else {
                dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listIntegerToListString(aposta.getNumerosSelecionados())));
            }
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }
}
