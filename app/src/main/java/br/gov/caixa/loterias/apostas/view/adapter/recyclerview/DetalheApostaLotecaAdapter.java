package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.view.custom.ApostaPartidaLotecaView;

/**
 * Created by joafilho on 25/01/2018.
 * DetalheApostaLotecaAdapter
 */

public class DetalheApostaLotecaAdapter extends RecyclerView.Adapter<DetalheApostaLotecaAdapter.ApostaLotecaHolder> {

    private List<PartidaLotecaDTO> listaPartidas;

    public DetalheApostaLotecaAdapter(List<PartidaLotecaDTO> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    @Override
    public ApostaLotecaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ApostaPartidaLotecaView view =
                ApostaPartidaLotecaView.build(parent.getContext());

        RecyclerView.LayoutParams params =
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        view.setLayoutParams(params);

        float density = parent.getContext().getResources().getDisplayMetrics().density;

        int paddingStart = (int) (8 * density);
        int paddingEnd = (int) (24 * density);
        int paddingTop =  (int) (24 * density);

        view.setPadding(
                paddingStart,
                paddingTop,
                paddingEnd,
                view.getPaddingBottom()
        );

        return new ApostaLotecaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotecaHolder holder, int position) {
        holder.apostaPartidaLotecaView.setLayout(listaPartidas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return this.listaPartidas.size();
    }

    class ApostaLotecaHolder extends RecyclerView.ViewHolder {

        private ApostaPartidaLotecaView apostaPartidaLotecaView;

        public ApostaLotecaHolder(View itemView) {
            super(itemView);

            this.apostaPartidaLotecaView = (ApostaPartidaLotecaView) itemView;
        }
    }
}
