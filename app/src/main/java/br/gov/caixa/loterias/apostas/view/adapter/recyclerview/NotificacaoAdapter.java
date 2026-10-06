package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoCentralDTO;

public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder> {

    public interface OnExcluirClickListener {
        void onExcluir(NotificacaoCentralDTO notificacaoCentralDTO);
    }

    private final Context context;
    private final OnExcluirClickListener listener;
    private List<NotificacaoCentralDTO> notificacoes = new ArrayList<>();

    public NotificacaoAdapter(Context context, List<NotificacaoCentralDTO> listNotificacaoCentralDTO, OnExcluirClickListener listener) {
        this.context = context;
        this.listener = listener;
        atualizar(listNotificacaoCentralDTO);
    }

    public void atualizar(List<NotificacaoCentralDTO> listNotificacaoCentralDTO) {
        notificacoes = new ArrayList<>(listNotificacaoCentralDTO);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notificacoes.size();
    }

    @NonNull
    @Override
    public NotificacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_notificacao,parent, false);
        return new NotificacaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacaoViewHolder holder, int position) {
        holder.bind(notificacoes.get(position), listener);
    }

     static class NotificacaoViewHolder extends RecyclerView.ViewHolder {
        private final View badgeNaoLida;
        private final TextView textTituloNotificacao;
        private final TextView textMensagemNotificacao;
        private final View btnExcluirNotificacao;
        NotificacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeNaoLida = itemView.findViewById(R.id.badgeNaoLida);
            textTituloNotificacao = itemView.findViewById(R.id.textTituloNotificacao);
            textMensagemNotificacao = itemView.findViewById(R.id.textMensagemNotificacao);
            btnExcluirNotificacao = itemView.findViewById(R.id.btnExcluirNotificacao);
        }

        void bind(final NotificacaoCentralDTO notificacaoCentralDTO, final OnExcluirClickListener listener) {
            textTituloNotificacao.setText(notificacaoCentralDTO.getTitulo());
            textMensagemNotificacao.setText(notificacaoCentralDTO.getConteudo());
            badgeNaoLida.setVisibility(notificacaoCentralDTO.isLida() ? View.INVISIBLE : View.VISIBLE);
            btnExcluirNotificacao.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExcluir(notificacaoCentralDTO);
                }
            });
        }
    }
}
