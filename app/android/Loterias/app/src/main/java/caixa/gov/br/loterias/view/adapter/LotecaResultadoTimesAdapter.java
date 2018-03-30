package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.custom.PartidaViewLoteca;
import caixa.gov.br.loterias.view.custom.PartidaViewLoteca_;

/**
 * Created by cedesbr450 on 29/03/18.
 */


public final class LotecaResultadoTimesAdapter extends RecyclerView.Adapter<LotecaResultadoTimesAdapter.ViewHolder> {


    private final Context context;
    private final List<PartidaLotecaDTO> partidalotecaLista;
    private final ModalidadeEnum modalidade;

    public LotecaResultadoTimesAdapter(final Context context, final List<PartidaLotecaDTO> partidalotecaLista, final ModalidadeEnum modalidade) {
        this.context = context;
        this.partidalotecaLista = partidalotecaLista;
        this.modalidade = modalidade;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_resultado_time_loteca, parent, false);
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

        PartidaLotecaDTO partidaLotecaDTO = new PartidaLotecaDTO();
        partidaLotecaDTO.setEquipe1(partidalotecaLista.get(position).getEquipe1());
        partidaLotecaDTO.setEquipe2(partidalotecaLista.get(position).getEquipe2());
        partidaLotecaDTO.setEmpate(partidalotecaLista.get(position).getEmpate());
        holder.partidaViewLoteca.setLayout(partidaLotecaDTO);
    }

    @Override
    public int getItemCount() {
        return partidalotecaLista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected  PartidaViewLoteca partidaViewLoteca;

        public ViewHolder(final View itemView) {
            super(itemView);
            partidaViewLoteca = (PartidaViewLoteca) itemView.findViewById(R.id.resultadoLotecaPartidaView);
        }
    }

}