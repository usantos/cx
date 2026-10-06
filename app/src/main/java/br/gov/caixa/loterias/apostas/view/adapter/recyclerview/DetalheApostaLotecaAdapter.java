package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.util.TypedValue;
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

        int paddingStart = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                parent.getResources().getDisplayMetrics());

        int paddingEnd = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                32,
                parent.getResources().getDisplayMetrics());

        view.setPaddingRelative(
                paddingStart,
                0,
                paddingEnd,
                0
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
