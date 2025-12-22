package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;


public class CarrosselBolaoAdapter extends RecyclerView.Adapter<BolaoCarrosselHolder> {

    private List<CotasBolaoDTO> bolaoList;
    private OnItemCarrosselBolaoListener listener;
    private Activity activity;

    public CarrosselBolaoAdapter(Activity activity, List<CotasBolaoDTO> data, OnItemCarrosselBolaoListener listener) {
        this.bolaoList = data;
        this.activity = activity;
        this.listener = listener;
    }


    @Override
    public BolaoCarrosselHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_bolao_novo, parent, false);

        return new BolaoCarrosselHolder(activity, v, listener);
    }

    @Override
    public void onBindViewHolder(final BolaoCarrosselHolder holder, final int position) {
        CotasBolaoDTO cotaBolao = bolaoList.get(position);
        holder.bind(cotaBolao, position);
    }

    @Override
    public int getItemCount() {
        return bolaoList.size();
    }

}