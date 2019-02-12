package br.gov.caixa.loterias.apostar.interno.view.adapter;

/**
 * Created by cedesbr450 on 19/12/17.
 */


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.TipoAposta;


public class TiposApostasAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<TipoAposta> tiposAposta;

    // 1
    public TiposApostasAdapter(Context context, ArrayList<TipoAposta> tiposAposta) {
        this.mContext = context;
        this.tiposAposta = tiposAposta;
    }

    // 2
    @Override
    public int getCount() {
        return tiposAposta.size() +1;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_tipos_apostas, null);
        }
        if (position == tiposAposta.size()){
            final LinearLayout linearLayoutTipoAposta = (LinearLayout)convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, R.color.branco));
            linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext,R.drawable.customborder));

            final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloTipoApostaTextView);
            tituloTipoAposta.setText("Adicionar ou remover");
            tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.verdeazul));
            final ImageView imageViewTipoAPosta = (ImageView)convertView.findViewById(R.id.imageviewTrevoTipoAposta);
            imageViewTipoAPosta.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
            params.setMargins(0,0,0,0);
            tituloTipoAposta.setLayoutParams(params);
        }else{
            final TipoAposta tipoAposta = tiposAposta.get(position);
            final ImageView imageViewTipoAPosta = (ImageView)convertView.findViewById(R.id.imageviewTrevoTipoAposta);
            imageViewTipoAPosta.setImageResource(R.drawable.trevo);

            final LinearLayout linearLayoutTipoAposta = (LinearLayout)convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));

            final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloTipoApostaTextView);
            tituloTipoAposta.setText(tipoAposta.getTitulo());
        }
        return convertView;
    }

}