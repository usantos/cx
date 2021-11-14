package br.gov.caixa.loterias.apostas.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnApostaConfirmadaListener;

/**
 * Created by pmotta on 15/03/2018.
 */

public class ApostasConfirmadasAdapter extends RecyclerView.Adapter<ApostaConfirmadaHolder> {

    private final Context context;
    private final List<ApostaDTO> list;
    private boolean isHistorico;
    private OnApostaConfirmadaListener onApostasConfirmadasListener;
    private MinhasApostasActivity parentActivity;

    public ApostasConfirmadasAdapter(MinhasApostasActivity parentActivity, Context context, List<ApostaDTO> list, boolean isHistorico) {
        this(parentActivity, context, list, null, isHistorico);
    }

    public ApostasConfirmadasAdapter(MinhasApostasActivity parentActivity, Context context, List<ApostaDTO> list, OnApostaConfirmadaListener listener, boolean isHistorico) {
        this.list = list;
        this.context = context;
        this.onApostasConfirmadasListener = listener;
        this.isHistorico = isHistorico;
        this.parentActivity = parentActivity;
    }

    public void setOnApostasConfirmadasListener(OnApostaConfirmadaListener onApostasConfirmadasListener) {
        this.onApostasConfirmadasListener = onApostasConfirmadasListener;
    }

    public void addApostasList(List<ApostaDTO> apostasList) {
        if (apostasList != null) {
            list.addAll(apostasList);
        }
    }

    private void conferirResultado (ApostaDTO aposta, ApostaConfirmadaHolder holder) {
        ConferirResultadoHelper.conferirResultadoHandler = holder;
        ConferirResultadoHelper.conferirResultadoWithoutLoading(holder, aposta.getId(), parentActivity);
    }


    @Override
    public ApostaConfirmadaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view;
        if (isListaVazia()) {
            view = LayoutInflater.from(context).inflate(R.layout.row_apostas_confirmadas_empty,
                    parent,
                    false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_apostas_confirmadas,
                    parent,
                    false);
        }


        return new ApostaConfirmadaHolder(view, onApostasConfirmadasListener, isListaVazia(), parentActivity);
    }

    private boolean isListaVazia() {
        return list.size() == 0;
    }

    @Override
    public void onBindViewHolder(ApostaConfirmadaHolder holder, int position) {
        if (list.size() != 0) {
            ApostaDTO aposta = list.get(position);
            holder.bind(aposta);
        }
    }

    @Override
    public int getItemCount() {
        if (isListaVazia()) {
            return  1;
        }
        return list.size();
    }
}
