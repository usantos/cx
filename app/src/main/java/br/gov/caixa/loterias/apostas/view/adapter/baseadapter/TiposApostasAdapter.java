package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

/**
 * Created by cedesbr450 on 19/12/17.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;


public class TiposApostasAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<TipoAposta> tiposAposta;

    public TiposApostasAdapter(Context context, ArrayList<TipoAposta> tiposAposta) {
        this.mContext = context;
        this.tiposAposta = tiposAposta;
    }

    @Override
    public int getCount() {
        return tiposAposta.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_tipos_apostas, null);
        }
        final TipoAposta tipoAposta = tiposAposta.get(position);
        final ImageView imageViewTipoAPosta = convertView.findViewById(R.id.imageviewTrevoTipoAposta);

        if(tipoAposta.getValor() == 9){
            imageViewTipoAPosta.setImageResource(R.drawable.ic_caixa_mm_amarelo_branco);
        }else {
            imageViewTipoAPosta.setImageResource(R.drawable.trevo);
        }

        final LinearLayout linearLayoutTipoAposta = convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
        linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));

        final TextView tituloTipoAposta = convertView.findViewById(R.id.tituloTipoApostaTextView);
        tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, tipoAposta.getTextColor()));
        tituloTipoAposta.setText(tipoAposta.getTitulo());

        return convertView;
    }

}