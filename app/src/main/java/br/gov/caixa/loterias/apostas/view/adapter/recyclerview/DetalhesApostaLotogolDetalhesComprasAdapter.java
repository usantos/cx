package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.view.custom.ApostaPartidaComprasLotogolView;

/**
 * Created by cedesbr450 on 20/03/18.
 */


public class DetalhesApostaLotogolDetalhesComprasAdapter extends RecyclerView.Adapter<DetalhesApostaLotogolDetalhesComprasAdapter.ApostaLotogolHolder> {

    private List<PartidaLotogolDTO> partidasLotogol;

    public DetalhesApostaLotogolDetalhesComprasAdapter(List<PartidaLotogolDTO> partidasLotogol) {
        this.partidasLotogol = partidasLotogol;
    }

    @Override
    public ApostaLotogolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApostaPartidaComprasLotogolView view = ApostaPartidaComprasLotogolView.build(parent.getContext());
        return new ApostaLotogolHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaLotogolHolder holder, int position) {
        holder.apostaPartidaLotogolView.setLayout(partidasLotogol.get(position));
        if(position != 0){
            holder.apostaPartidaLotogolView.getLayoutRowApostaPartidaLotogol().setBackgroundResource(R.color.transparente);
        }
    }

    @Override
    public int getItemCount() {
        return partidasLotogol.size();
    }

    public class ApostaLotogolHolder extends RecyclerView.ViewHolder {

        private ApostaPartidaComprasLotogolView apostaPartidaLotogolView;

        public ApostaLotogolHolder(View itemView) {
            super(itemView);

            apostaPartidaLotogolView = (ApostaPartidaComprasLotogolView) itemView;
        }
    }
}
