package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoDTO;

public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.ViewHolder> {

    private static final int TIPO_CABECALHO = 0;
    private static final int TIPO_NOTIFICACAO = 1;

    public interface OnExcluirClickListener {
        void onExcluir(NotificacaoDTO notificacaoDTO);
    }

    private final Context context;
    private final OnExcluirClickListener listener;
    private List<Object> linhas = new ArrayList<>();

    public NotificacaoAdapter(Context context, List<NotificacaoDTO> listNotificacaoDTO, OnExcluirClickListener listener) {
        this.context = context;
        this.listener = listener;
        atualizar(listNotificacaoDTO);
    }

    public void atualizar(List<NotificacaoDTO> listNotificacaoDTO) {
        linhas = montarLinhas(listNotificacaoDTO);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return linhas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return linhas.get(position) instanceof String ? TIPO_CABECALHO : TIPO_NOTIFICACAO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType == TIPO_CABECALHO) {
            View itemView = inflater.inflate(R.layout.item_notificacao_cabecalho, parent, false);
            return new CabecalhoViewHolder(itemView);
        }

        View itemView = inflater.inflate(R.layout.item_notificacao,parent, false);
        return new NotificacaoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object linha = linhas.get(position);

        if (holder instanceof CabecalhoViewHolder) {
            ((CabecalhoViewHolder) holder).bind((String) linha);
        } else {
            ((NotificacaoViewHolder) holder).bind((NotificacaoDTO) linha, listener);
        }
    }

    private List<Object> montarLinhas(List<NotificacaoDTO> listNotificacaoDTO) {
        List<Object> linhas = new ArrayList<>();

        for (Map.Entry<String, List<NotificacaoDTO>> grupo : agruparPorData(listNotificacaoDTO).entrySet()) {
            linhas.add(grupo.getKey());
            linhas.addAll(grupo.getValue());
        }
        return linhas;
    }

    private Map<String, List<NotificacaoDTO>> agruparPorData(List<NotificacaoDTO> listNotificacaoDTO) {
        Map<String, List<NotificacaoDTO>> grupos = new LinkedHashMap<>();

        List<NotificacaoDTO> ordenadas = new ArrayList<>(listNotificacaoDTO);
        Collections.sort(ordenadas, new Comparator<NotificacaoDTO>() {
            @Override
            public int compare(NotificacaoDTO a, NotificacaoDTO b) {
                return b.getDataVisualizacao().getCalendar().compareTo(a.getDataVisualizacao().getCalendar());
            }
        });

        for (NotificacaoDTO notificacaoDTO : ordenadas) {
            String rotulo = obterRotuloGrupo(notificacaoDTO.getDataVisualizacao().getCalendar());
            List<NotificacaoDTO> list = grupos.get(rotulo);
            if (list == null) {
                list = new ArrayList<>();
                grupos.put(rotulo, list);
            }
            list.add(notificacaoDTO);
        }
        return grupos;
    }

    private String obterRotuloGrupo(Date dataVisualizacao) {
        Calendar hoje = Calendar.getInstance();

        Calendar inicioSemana = Calendar.getInstance();
        inicioSemana.setFirstDayOfWeek(Calendar.SUNDAY);
        inicioSemana.set(Calendar.DAY_OF_WEEK, inicioSemana.getFirstDayOfWeek());
        zerarHora(inicioSemana);

        Calendar dataCalendar = Calendar.getInstance();
        dataCalendar.setTime(dataVisualizacao);

        if(!dataCalendar.before(inicioSemana)) {
            return "Esta semana"; //context.getString(R)
        }
        return "VER DEPOIS";
    }

    private void zerarHora(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    private static class CabecalhoViewHolder extends ViewHolder {
        private final TextView textCabecalhoGrupo;
        CabecalhoViewHolder(@NonNull View itemView) {
            super(itemView);
            textCabecalhoGrupo = itemView.findViewById(R.id.textCabecalhoGrupo);
        }

        void bind(String titulo) {
            textCabecalhoGrupo.setText(titulo);
        }
    }
    private static class NotificacaoViewHolder extends ViewHolder {
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

        void bind(final NotificacaoDTO notificacaoDTO, final OnExcluirClickListener listener) {
            textTituloNotificacao.setText(notificacaoDTO.getTitulo());
            textMensagemNotificacao.setText(notificacaoDTO.getConteudo());
            badgeNaoLida.setVisibility(notificacaoDTO.isLida() ? View.INVISIBLE : View.VISIBLE);
            btnExcluirNotificacao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onExcluir(notificacaoDTO);
                    }
                }
            });
        }
    }
}
