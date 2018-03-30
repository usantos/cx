package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.view.custom.ApostaPartidaLotogolView;
import caixa.gov.br.loterias.view.custom.PartidaViewLoteca;

/**
 * Created by cedesbr450 on 30/03/18.
 */

public final class LotogolResultadoTimesAdapter extends RecyclerView.Adapter<LotogolResultadoTimesAdapter.ViewHolder> {


    private final Context context;
    private final List<PartidaLotogolDTO> partidaLotogolLista;
    private final ModalidadeEnum modalidade;

    public LotogolResultadoTimesAdapter(final Context context, final List<PartidaLotogolDTO> partidaLotogolLista, final ModalidadeEnum modalidade) {
        this.context = context;
        this.partidaLotogolLista = partidaLotogolLista;
        this.modalidade = modalidade;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_resultado_time_lotogol, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        EstiloModalidade estilo = new EstiloModalidade(modalidade);
//        final int numeroResultado = numeroslista.get(position);
//        holder.textViewTituloNumerosResultados.setText(numeroResultado < 10 ? "0" + numeroResultado : "" + numeroResultado);
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(context.getResources().getColor(estilo.getCorClara()));
//        drawable.setShape(GradientDrawable.OVAL);
//        drawable.setSize(ViewUtils.setDisplayMetric(40, context), ViewUtils.setDisplayMetric(40, context));
//        holder.linearLayoutContentNumerosResultados.setBackground(drawable);

        PartidaLotogolDTO partidaLotogolDTO = new PartidaLotogolDTO();
        partidaLotogolDTO.setEquipe1(partidaLotogolLista.get(position).getEquipe1());
        partidaLotogolDTO.setEquipe2(partidaLotogolLista.get(position).getEquipe2());
        holder.apostaPartidaLotogolView.setLayout(partidaLotogolDTO);
    }

    @Override
    public int getItemCount() {
        return partidaLotogolLista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ApostaPartidaLotogolView apostaPartidaLotogolView;

        public ViewHolder(final View itemView) {
            super(itemView);
            apostaPartidaLotogolView = (ApostaPartidaLotogolView) itemView.findViewById(R.id.resultadoLotogolPartidaView);
        }
    }

}