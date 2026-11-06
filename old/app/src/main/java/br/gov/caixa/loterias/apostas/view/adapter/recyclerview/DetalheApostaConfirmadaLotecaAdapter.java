package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.view.custom.PartidasLotecaApostasConfirmadasView;

/**
 * Created by joafilho on 25/01/2018.
 * DetalheApostaConfirmadaLotecaAdapter
 */

public class DetalheApostaConfirmadaLotecaAdapter extends RecyclerView.Adapter<DetalheApostaConfirmadaLotecaAdapter.ApostaLotecaHolder> {

    private List<PartidaLotecaDTO> listaPartidas, listaResultados;

    public DetalheApostaConfirmadaLotecaAdapter(List<PartidaLotecaDTO> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    public DetalheApostaConfirmadaLotecaAdapter(List<PartidaLotecaDTO> listaPartidas, ResultadoConcursoDTO resultado ) {
        this.listaPartidas = listaPartidas;
        if (resultado != null && resultado.getPartidasLoteca() != null) {
            this.listaResultados = resultado.getPartidasLoteca();
        }
    }

    @Override
    public ApostaLotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        PartidasLotecaApostasConfirmadasView view = PartidasLotecaApostasConfirmadasView.build(parent.getContext(), listaResultados);
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