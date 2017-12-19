package caixa.gov.br.loterias.view.adapter;

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

import org.w3c.dom.Text;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.TipoAposta;


public class TiposApostasAdapter extends BaseAdapter {

    private final Context mContext;
    private final TipoAposta[] tiposAposta;

    // 1
    public TiposApostasAdapter(Context context, TipoAposta[] tiposAposta) {
        this.mContext = context;
        this.tiposAposta = tiposAposta;
    }

    // 2
    @Override
    public int getCount() {
        return tiposAposta.length;
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
        final TipoAposta tipoAposta = tiposAposta[position];

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_tipos_apostas, null);
        }
        final ImageView imageViewTipoAPosta = (ImageView)convertView.findViewById(R.id.imageviewTrevoTipoAposta);
        imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());

        final LinearLayout linearLayoutTipoAposta = (LinearLayout)convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
        linearLayoutTipoAposta.setBackgroundColor(tipoAposta.getCorBackground());

        final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloTipoApostaTextView);
        tituloTipoAposta.setText(tipoAposta.getTitulo());

        return convertView;
    }

}