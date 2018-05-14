package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private boolean isHistorico;


    public ApostasConfirmadasAdapter(Context context, List<ApostaDTO> list, boolean isHistorico) {
        this(context, list, null, isHistorico);
    }

    public ApostasConfirmadasAdapter(Context context, List<ApostaDTO> list, OnApostasConfirmadasListener listener, boolean isHistorico) {
        this.list = list;
        this.context = context;
        this.onApostasConfirmadasListener = listener;
        this.isHistorico = isHistorico;
    }

    public void setOnApostasConfirmadasListener(OnApostasConfirmadasListener onApostasConfirmadasListener) {
        this.onApostasConfirmadasListener = onApostasConfirmadasListener;
    }

    public OnApostasConfirmadasListener getOnApostasConfirmadasListener() {
        return onApostasConfirmadasListener;
    }

    private OnApostasConfirmadasListener onApostasConfirmadasListener;

    public void addApostasList(List<ApostaDTO> apostasList) {
        if (apostasList != null) {
            list.addAll(apostasList);
        }
    }

    public interface OnApostasConfirmadasListener {
        void onReload(ApostaDTO apostaDTO);

        void onResult(ApostaDTO apostaDTO);

        void onItemClick(ApostaDTO apostaDTO);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View stripe;
        private TextView modalidadeTxt;
        private TextView concursoTxt;
        private View resultBtn;
        private View reloadBtn;
        private ImageView setaImg;
        private LinearLayout setaLayout;
        private TextView situacaoLabel;
        private TextView situacaoTxt;

        public ViewHolder(View view) {
            super(view);
            stripe = view.findViewById(R.id.stripe);
            modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
            concursoTxt = view.findViewById(R.id.concursoTxt);
            resultBtn = view.findViewById(R.id.resultBtn);
            reloadBtn = view.findViewById(R.id.reloadBtn);
            setaImg = view.findViewById(R.id.setaImg);
            setaLayout = view.findViewById(R.id.setaLayout);
            situacaoLabel = view.findViewById(R.id.situacaoLabel);
            situacaoTxt = view.findViewById(R.id.situacaoTxt);

            reloadBtn.setOnClickListener(this);
            setaLayout.setOnClickListener(this);
            setaImg.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onApostasConfirmadasListener != null) {
                ApostaDTO apostaDTO = list.get(getAdapterPosition());

                switch (view.getId()) {
                    case R.id.resultBtn: {
                        onApostasConfirmadasListener.onResult(apostaDTO);
                        break;
                    }
                    case R.id.reloadBtn: {
                        onApostasConfirmadasListener.onReload(apostaDTO);
                        break;
                    }
                    case R.id.setaLayout: {
                        onApostasConfirmadasListener.onItemClick(apostaDTO);
                        break;
                    }
                    case R.id.setaImg: {
                        onApostasConfirmadasListener.onItemClick(apostaDTO);
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
        holder.situacaoLabel.setTextColor(color);
        holder.situacaoTxt.setTextColor(color);
        holder.situacaoTxt.setText(aposta.getSituacao().getDescricao());
//        ViewUtils.setColorDrawable(context, holder.setaImg.getDrawable(), R.color.colorAccent);

//        if (context.getString(R.string.label_efetivada).equalsIgnoreCase(aposta.getSituacao().getDescricao())) {

        if (isHistorico == false) {
            holder.resultBtn.setVisibility(View.VISIBLE);
            holder.resultBtn.setOnClickListener(holder);
        }else{
            holder.resultBtn.setVisibility(View.INVISIBLE);
            holder.resultBtn.setClickable(Boolean.FALSE);
        }

        int teimosinhas = aposta.getQuantidadeTeimosinhas() != null ? aposta.getQuantidadeTeimosinhas() : 0;
        if (teimosinhas > 0) {
            holder.concursoTxt.setText(context.getString(R.string.label_aposta_concursos,
                    aposta.getConcursoInicial(),
                    aposta.getConcursoAlvo()));

        } else {
            holder.concursoTxt.setText(context.getString(R.string.label_aposta_concurso, aposta.getConcursoInicial()));
        }
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
