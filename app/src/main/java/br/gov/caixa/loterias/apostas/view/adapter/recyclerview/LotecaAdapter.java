package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.view.custom.PartidaView;
import br.gov.caixa.loterias.apostas.view.holder.LotecaHolder;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;

/**
 * Created by joafilho on 26/12/2017.
 * Class LotecaAdapter
 */

public class LotecaAdapter extends RecyclerView.Adapter<LotecaHolder> {

    private List<ParametroPartida> listaPartidas;
    private ParametroJogoDTO parametroJogo;
    private LocateAdapterListener listener;

    public LotecaAdapter(ParametroJogoDTO parametroJogo, LocateAdapterListener listener) {
        this.parametroJogo = parametroJogo;
        this.listaPartidas = parametroJogo != null ? parametroJogo.getPartidas(): new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public LotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //PartidaView view = PartidaView_.build(parent.getContext());
        PartidaView view = PartidaView.build(parent.getContext());
        return new LotecaHolder(view, parametroJogo, listener);
    }

    @Override
    public void onBindViewHolder(LotecaHolder holder, int position) {
        ParametroPartida parametroPartida = listaPartidas.get(position);
        holder.bind(parametroPartida, position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listaPartidas.size();
    }

}
