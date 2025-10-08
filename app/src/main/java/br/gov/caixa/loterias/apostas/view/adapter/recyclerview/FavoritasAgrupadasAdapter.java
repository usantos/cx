package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.FavoritasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;

public class FavoritasAgrupadasAdapter extends RecyclerView.Adapter<FavoritasAgrupadasAdapter.ModalidadeViewHolder> {

    private final Context context;
    private final FavoritasActivity activity;
    private final ApostaFavoritaListener listener;
    private final List<Map.Entry<String, ResultadoPesquisaPaginadaDTOApostaFavoritaDTO>> entradaModalidades;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public FavoritasAgrupadasAdapter(final FavoritasActivity activity, final Context context,
                                     Map<String, ResultadoPesquisaPaginadaDTOApostaFavoritaDTO> resultado,
                                     ApostaFavoritaListener listener) {
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        setHasStableIds(true);

        // Filtra e transforma o Map em uma lista de entradas válidas
        entradaModalidades = new ArrayList<>();
        for (Map.Entry<String, ResultadoPesquisaPaginadaDTOApostaFavoritaDTO> entry : resultado.entrySet()) {
            if (entry.getValue().getRowCount().intValue() > 0) {
                entradaModalidades.add(entry);
            }
        }
    }

    @Override
    public ModalidadeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agrupador_modalidade, parent, false);
        return new ModalidadeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModalidadeViewHolder holder, int position) {
        holder.bind(entradaModalidades.get(position));
    }

    @Override
    public int getItemCount() {
        return entradaModalidades.size();
    }

    @Override
    public long getItemId(int position) {
        return entradaModalidades.get(position).getKey().hashCode();
    }

    class ModalidadeViewHolder extends RecyclerView.ViewHolder {
        TextView modalidadeTitulo;
        View titulo;
        ImageView trevo;
        RelativeLayout trevoFundo;
        RecyclerView recyclerApostas;
        FavoritasRecyclerViewAdapter favoritasAdapter;

        public ModalidadeViewHolder(View itemView) {
            super(itemView);
            modalidadeTitulo = itemView.findViewById(R.id.textViewCardTitle);
            titulo = itemView.findViewById(R.id.linearLayoutTopModalidades);
            trevo = itemView.findViewById(R.id.trevoImagem);
            trevoFundo = itemView.findViewById(R.id.RelativeLayoutTrevo);
            recyclerApostas = itemView.findViewById(R.id.recyclerApostas);

            recyclerApostas.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerApostas.setRecycledViewPool(viewPool);
            favoritasAdapter = new FavoritasRecyclerViewAdapter(activity, context, new ArrayList<>(), listener);
            recyclerApostas.setAdapter(favoritasAdapter);
        }

        public void bind(Map.Entry<String, ResultadoPesquisaPaginadaDTOApostaFavoritaDTO> entry) {
            String codigo = entry.getKey();
            ResultadoPesquisaPaginadaDTOApostaFavoritaDTO dto = entry.getValue();

            EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(
                    ModalidadeEnum.fromInteger(Integer.parseInt(codigo))
            );

            //String descricao = dto.getLista().get(0).getModalidade().getDescricao();
            String descricao = ModalidadeEnum.fromString(ModalidadeEnum.fromInteger(Integer.parseInt(codigo)));

            modalidadeTitulo.setText(descricao.toLowerCase());
            modalidadeTitulo.setTextColor(ContextCompat.getColor(context, estilo.getCorFonteFundoClaro()));
            titulo.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
            trevo.setImageDrawable(ContextCompat.getDrawable(context, estilo.getTrevoFundoEscuro()));
            trevoFundo.setBackground(ContextCompat.getDrawable(context, estilo.getTrapezio()));

            favoritasAdapter.updateData(dto.getLista());
        }
    }
}