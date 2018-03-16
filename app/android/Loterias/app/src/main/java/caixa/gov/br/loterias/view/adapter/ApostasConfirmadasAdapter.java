package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.utils.EstiloModalidadeAposta;

/**
 * Created by pmotta on 15/03/2018.
 */

public class ApostasConfirmadasAdapter extends RecyclerView.Adapter<ApostasConfirmadasAdapter.ViewHolder> {

    private final Context context;
    private List<ApostaDTO> list;

    public ApostasConfirmadasAdapter(Context context, List<ApostaDTO> list){
        this.list = list;
        this.context = context;
    }

    private OnApostasConfirmadasClick onApostasConfirmadasClick;

    public interface OnApostasConfirmadasClick{
        void onReload(int position);
        void onFavorite(int position);
        void onItemClick(int position);
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
            if (onApostasConfirmadasClick != null) {
                switch (view.getId()){
                    case R.id.favoriteBtn:{
                        onApostasConfirmadasClick.onFavorite(getAdapterPosition());
                        break;
                    }
                    case R.id.reloadBtn:{
                        onApostasConfirmadasClick.onReload(getAdapterPosition());
                        break;
                    }
                    default:
                        onApostasConfirmadasClick.onItemClick(getAdapterPosition());
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApostaDTO aposta = list.get(position);
        EstiloModalidadeAposta estiloModalidade = new EstiloModalidadeAposta(aposta.getModalidade());

        holder.stripe.setBackgroundColor(estiloModalidade.getCorEscura());
        holder.modalidadeTxt.setText(estiloModalidade.getStringId());

        int teimosinhas = aposta.getQuantidadeTeimosinhas() != null? aposta.getQuantidadeTeimosinhas() : 0;
        if(teimosinhas > 0){
            holder.concursoTxt.setText(context.getString(R.string.label_aposta_concursos,
                    aposta.getConcursoInicial(),
                    aposta.getConcursoAlvo()));

        }else{
            holder.concursoTxt.setText(context.getString(R.string.label_aposta_concurso, aposta.getConcursoInicial()));
        }

        holder.valorTxt.setText(context.getString(R.string.formatar_valor, aposta.getValor()));
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
