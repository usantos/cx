package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotecaView;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotecaView_;
import caixa.gov.br.loterias.view.custom.PartidasLotecaApostasConfirmadasView;
import caixa.gov.br.loterias.view.custom.PartidasLotecaApostasConfirmadasView_;

/**
 * Created by joafilho on 25/01/2018.
 * DetalheApostaConfirmadaLotecaAdapter
 */

public class DetalheApostaConfirmadaLotecaAdapter extends RecyclerView.Adapter<DetalheApostaConfirmadaLotecaAdapter.ApostaLotecaHolder> {

    private List<PartidaLotecaDTO> listaPartidas;

    public DetalheApostaConfirmadaLotecaAdapter(List<PartidaLotecaDTO> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    @Override
    public ApostaLotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        PartidasLotecaApostasConfirmadasView view = PartidasLotecaApostasConfirmadasView_.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ApostaLotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotecaHolder holder, int position) {
        holder.partidasLotecaApostasConfirmadasView.setLayout(listaPartidas.get(position));
    }

    @Override
    public int getItemCount() {
        return this.listaPartidas.size();
    }

    class ApostaLotecaHolder extends RecyclerView.ViewHolder {

        private PartidasLotecaApostasConfirmadasView partidasLotecaApostasConfirmadasView;

        public ApostaLotecaHolder(View itemView) {
            super(itemView);

            this.partidasLotecaApostasConfirmadasView = (PartidasLotecaApostasConfirmadasView) itemView;
        }
    }
}
