package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.TipoAposta;

public class VisualizarModalidadesAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<TipoAposta> tiposAposta;

    private OnModalidadeSelecionadaListener listener;

    public VisualizarModalidadesAdapter(Context context, List<TipoAposta> tiposAposta, OnModalidadeSelecionadaListener listener) {
        this.mContext = context;
        this.tiposAposta = tiposAposta;
        setListener(listener);
    }

    public void setListener(OnModalidadeSelecionadaListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return tiposAposta.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_tipos_apostas, null);
        }

        final TipoAposta tipoAposta = tiposAposta.get(position);
        final ImageView imageViewTipoAPosta = convertView.findViewById(R.id.imageviewTrevoTipoAposta);
        final LinearLayout linearLayoutTipoAposta = convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
        final TextView tituloTipoAposta = convertView.findViewById(R.id.tituloTipoApostaTextView);

        tituloTipoAposta.setText(tipoAposta.getTitulo());
        imageViewTipoAPosta.setImageResource(R.drawable.trevo);
        linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
        tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));

        convertView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(tiposAposta.get(position));
            }
            tiposAposta.get(position);
        });
        return convertView;
    }

    public interface OnModalidadeSelecionadaListener{
        void onItemClick(TipoAposta item);
    }


}




