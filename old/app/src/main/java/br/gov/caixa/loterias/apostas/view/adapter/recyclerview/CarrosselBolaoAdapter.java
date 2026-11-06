package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;


public class CarrosselBolaoAdapter extends RecyclerView.Adapter<BolaoCarrosselHolder> {

    private List<CotasBolaoDTO> bolaoList;
    private OnItemCarrosselBolaoListener listener;
    private boolean desativarCoracao = false;
    private Activity activity;
    private @Nullable FiltroAplicadoMarketplace filtro;
    private FrameLayout frameCoracao;

    public CarrosselBolaoAdapter(Activity activity, List<CotasBolaoDTO> data, OnItemCarrosselBolaoListener listener, @Nullable FiltroAplicadoMarketplace filtro) {
        this.bolaoList = data;
        this.activity = activity;
        this.listener = listener;
        this.filtro = filtro;
    }

    public CarrosselBolaoAdapter(Activity activity, List<CotasBolaoDTO> data, OnItemCarrosselBolaoListener listener, @Nullable FiltroAplicadoMarketplace filtro,Boolean desativarCoracao) {
        this.bolaoList = data;
        this.activity = activity;
        this.listener = listener;
        this.filtro = filtro;
        this.desativarCoracao = desativarCoracao;
    }


    @Override
    public BolaoCarrosselHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_bolao_novo, parent, false);
        frameCoracao = v.findViewById(R.id.layoutFavoritarCabecalho);
        if(desativarCoracao) frameCoracao.setVisibility(View.INVISIBLE);
        return new BolaoCarrosselHolder(activity, v, listener, filtro);
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