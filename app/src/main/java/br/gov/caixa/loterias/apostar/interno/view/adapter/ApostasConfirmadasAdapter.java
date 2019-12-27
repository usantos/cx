package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.utils.helper.ConferirResultadoHandler;
import br.gov.caixa.loterias.apostar.interno.utils.helper.ConferirResultadoHelper;

/**
 * Created by pmotta on 15/03/2018.
 */

public class ApostasConfirmadasAdapter extends RecyclerView.Adapter<ApostasConfirmadasAdapter.ViewHolder> implements ConferirResultadoHandler {

    private final Context context;
    private final List<ApostaDTO> list;
    private boolean isHistorico;
    private OnApostasConfirmadasListener onApostasConfirmadasListener;
    private MinhasApostasActivity parentActivity;
    //private AnimatorSet scaleIn, scaleOut;

    public ApostasConfirmadasAdapter(MinhasApostasActivity parentActivity, Context context, List<ApostaDTO> list, boolean isHistorico) {
        this(parentActivity, context, list, null, isHistorico);
    }

    public ApostasConfirmadasAdapter(MinhasApostasActivity parentActivity, Context context, List<ApostaDTO> list, OnApostasConfirmadasListener listener, boolean isHistorico) {
        this.list = list;
        this.context = context;
        this.onApostasConfirmadasListener = listener;
        this.isHistorico = isHistorico;
        this.parentActivity = parentActivity;
    }

    public OnApostasConfirmadasListener getOnApostasConfirmadasListener() {
        return onApostasConfirmadasListener;
    }

    public void setOnApostasConfirmadasListener(OnApostasConfirmadasListener onApostasConfirmadasListener) {
        this.onApostasConfirmadasListener = onApostasConfirmadasListener;
    }

    public void addApostasList(List<ApostaDTO> apostasList) {
        if (apostasList != null) {
            list.addAll(apostasList);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.size() != 0) {
            ApostaDTO aposta = list.get(position);
            EstiloModalidade estiloModalidade = new EstiloModalidade(aposta.getModalidade());

            int color = ContextCompat.getColor(context, estiloModalidade.getCorEscura());

            String descricaoStatus = "";
            if (aposta.getSituacao().getValor() == 4) {
                descricaoStatus = "Conferindo resultado.";
                holder.situacaoTxt.setTextColor(ContextCompat.getColor(context, R.color.azulescuroapostafavorita));
                holder.progressBarSituacao.setVisibility(View.VISIBLE);
                conferirResultado(aposta, holder);
            } else {
                descricaoStatus = aposta.getSituacao().getDescricao();
            }


            holder.color = color;
            holder.stripe.setBackgroundColor(color);
            holder.modalidadeTxt.setTextColor(color);
            holder.modalidadeTxt.setText(ViewUtils.getNomeModalidadePorAposta(aposta));
            holder.situacaoTxt.setTextColor(color);
            holder.situacaoTxt.setText("situação: " + descricaoStatus.toLowerCase(new Locale("pt", "BR")));


            if (aposta.getSituacao().getValor() != 100) {
                holder.situacaoTxt.setTypeface(null, Typeface.NORMAL);
                holder.situacaoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                //holder.modalidadeTxt.setTypeface(null, Typeface.NORMAL);
            }

            int teimosinhas = aposta.getQuantidadeTeimosinhas() != null ? aposta.getQuantidadeTeimosinhas() : 0;
            if (teimosinhas > 0) {
                int quantidade = aposta.getConcursoInicial() + (teimosinhas - 1);
                holder.concursoTxt.setText(context.getString(R.string.label_aposta_concursos,
                        aposta.getConcursoInicial(),
                        quantidade));
            } else {
                holder.concursoTxt.setText(context.getString(R.string.label_aposta_concurso, aposta.getConcursoInicial()));
            }
        }
    }

    /*private void animaTvSituacao(ViewHolder holder){
        scaleIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.scale_in_anim);
        scaleOut = (AnimatorSet) AnimatorInflater.loadAnimator(context  , R.animator.scale_out_anim);
        scaleIn.setTarget(holder.situacaoTxt);
        scaleOut.setTarget(holder.situacaoTxt);

        new CountDownTimer(TimeUnit.MINUTES.toMillis(100000), 1000) {
            public void onTick(long millisegs) {
                scaleIn.start();
                scaleOut.start();
            }

            public void onFinish() {
                scaleIn.start();
            }
        }.start();
    }*/

    private void conferirResultado (ApostaDTO aposta, ViewHolder holder) {
        ConferirResultadoHelper.conferirResultadoHandler = this;
        ConferirResultadoHelper.conferirResultadoWithoutLoading(holder, aposta.getId(), parentActivity);
    }

    @Override
    public void handle(ViewHolder holder, DTOEnumLong situacao) {
        holder.stripe.setBackgroundColor(holder.color);
        holder.modalidadeTxt.setTextColor(holder.color);
        holder.situacaoTxt.setTextColor(holder.color);

        if (situacao.getDescricao().equalsIgnoreCase("Premiada")) {
            holder.situacaoTxt.setTypeface(null, Typeface.BOLD);
            holder.situacaoTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.situacaoTxt.setText(ViewUtils.textFuturaAndFuturaBold(context,"situação: _" + situacao.getDescricao().toLowerCase(new Locale("pt", "BR")) + "_"));
        } else {
            holder.situacaoTxt.setText("situação: " + situacao.getDescricao().toLowerCase(new Locale("pt", "BR")));
        }

        holder.progressBarSituacao.setVisibility(View.INVISIBLE);
    }

    @Override
    public void handleError(ViewHolder holder, VolleyError error) {
        holder.situacaoTxt.setText("Não foi possível conferir o resultado.");
        holder.progressBarSituacao.setVisibility(View.INVISIBLE);
    }

    @Override
    public void handle(DTOEnumLong situacao) { }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view;
        if (list.size() == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.row_apostas_confirmadas_empty,
                    parent,
                    false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_apostas_confirmadas,
                    parent,
                    false);
        }


        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (list.size() == 0) {
            return  1;
        }
        return list.size();
    }

    public interface OnApostasConfirmadasListener {
        void onReload(ApostaDTO apostaDTO);

        void onItemClick(ApostaDTO apostaDTO);

        void onEmptyCellClick();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View stripe;
        private TextView modalidadeTxt;
        private TextView concursoTxt;
        private View reloadBtn;
        private ImageView setaImg;
        private LinearLayout reloadBtnLayout;
        private LinearLayout setaLayout;
        private TextView situacaoTxt;
        private Button btnAposteAqui;
        private ProgressBar progressBarSituacao;
        private int color;

        public ViewHolder(View view) {
            super(view);

            if (list.size() != 0) {
                stripe = view.findViewById(R.id.stripe);
                modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
                concursoTxt = view.findViewById(R.id.concursoTxt);
                reloadBtn = view.findViewById(R.id.reloadBtn);
                setaImg = view.findViewById(R.id.setaImg);
                setaLayout = view.findViewById(R.id.setaLayout);
                reloadBtnLayout = view.findViewById(R.id.reloadBtnLayout);
                situacaoTxt = view.findViewById(R.id.situacaoTxt);
                progressBarSituacao = view.findViewById(R.id.progressBarSituacao);

                setaLayout.setOnClickListener(this);
                setaImg.setOnClickListener(this);
                reloadBtnLayout.setOnClickListener(this);
                reloadBtn.setOnClickListener(this);
            } else {
                btnAposteAqui = view.findViewById(R.id.btnAposteAquiApostasConfirmadas);

                btnAposteAqui.setOnClickListener(this);
            }


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onApostasConfirmadasListener != null) {
                if (view.getId() == R.id.row_apostas_confirmadas_empty || view.getId() == R.id.btnAposteAquiApostasConfirmadas) {
                    onApostasConfirmadasListener.onEmptyCellClick();
                } else {
                    ApostaDTO apostaDTO = list.get(getAdapterPosition());

                    switch (view.getId()) {
                        case R.id.reloadBtn: {
                            onApostasConfirmadasListener.onReload(apostaDTO);
                            break;
                        }
                        case R.id.reloadBtnLayout: {
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
    }
}
