package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by pmotta on 15/03/2018.
 */

public class ApostasConfirmadasAdapter extends RecyclerView.Adapter<ApostasConfirmadasAdapter.ViewHolder> {

    private final Context context;
    private final List<ApostaDTO> list;

    public ApostasConfirmadasAdapter(Context context, List<ApostaDTO> list){
        this(context, list, null);
    }

    public ApostasConfirmadasAdapter(Context context, List<ApostaDTO> list, OnApostasConfirmadasListener listener){
        this.list = list;
        this.context = context;
        this.onApostasConfirmadasListener = listener;
    }

    public void setOnApostasConfirmadasListener(OnApostasConfirmadasListener onApostasConfirmadasListener) {
        this.onApostasConfirmadasListener = onApostasConfirmadasListener;
    }

    public OnApostasConfirmadasListener getOnApostasConfirmadasListener() {
        return onApostasConfirmadasListener;
    }

    private OnApostasConfirmadasListener onApostasConfirmadasListener;

    public void addApostasList(List<ApostaDTO> apostasList) {
        if(apostasList != null){
            list.addAll(apostasList);
        }
    }

    public interface OnApostasConfirmadasListener {
        void onReload(ApostaDTO apostaDTO);
        void onFavorite(ApostaDTO apostaDTO);
        void onItemClick(ApostaDTO apostaDTO);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View stripe;
        private TextView modalidadeTxt;
        private TextView concursoTxt;
        private TextView valorTxt;
        private View favoriteBtn;
        private View reloadBtn;

        public ViewHolder(View view){
            super(view);
            stripe = view.findViewById(R.id.stripe);
            modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
            concursoTxt = view.findViewById(R.id.concursoTxt);
            valorTxt = view.findViewById(R.id.valorTxt);
            favoriteBtn = view.findViewById(R.id.favoriteBtn);
            reloadBtn = view.findViewById(R.id.reloadBtn);

            favoriteBtn.setOnClickListener(this);
            reloadBtn.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onApostasConfirmadasListener != null) {
                ApostaDTO apostaDTO = list.get(getAdapterPosition());

                switch (view.getId()){
                    case R.id.favoriteBtn:{
                        onApostasConfirmadasListener.onFavorite(apostaDTO);
                        break;
                    }
                    case R.id.reloadBtn:{
                        onApostasConfirmadasListener.onReload(apostaDTO);
                        break;
                    }
                    default:
                        onApostasConfirmadasListener.onItemClick(apostaDTO);
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApostaDTO aposta = list.get(position);
        EstiloModalidade estiloModalidade = new EstiloModalidade(aposta.getModalidade());

        int color = ContextCompat.getColor(context, estiloModalidade.getCorEscura());

        holder.stripe.setBackgroundColor(color);
        holder.modalidadeTxt.setTextColor(color);
        holder.modalidadeTxt.setText(estiloModalidade.getStringId());

        int teimosinhas = aposta.getQuantidadeTeimosinhas() != null? aposta.getQuantidadeTeimosinhas() : 0;
        if(teimosinhas > 0){
            holder.concursoTxt.setText(context.getString(R.string.label_aposta_concursos,
                    aposta.getConcursoInicial(),
                    aposta.getConcursoAlvo()));

        }else{
            holder.concursoTxt.setText(context.getString(R.string.label_aposta_concurso, aposta.getConcursoInicial()));
        }

        ViewUtils.setMoedaFormatHtml(aposta.getValor(), holder.valorTxt);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.item_apostas_confirmadas,
                parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
