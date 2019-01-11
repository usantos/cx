package br.gov.caixa.loterias.apostar.interno.view.adapter;

/**
 * Created by cedesbr450 on 20/12/17.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;


public class NumerosRapidaoAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Integer> numeroslista;
    private final Boolean isObrigatorio;

    // 1
    public NumerosRapidaoAdapter(Context context, List<Integer> numeroslista, Boolean isObrigatorio) {
        this.mContext = context;
        this.numeroslista = numeroslista;
        this.isObrigatorio = isObrigatorio;
    }

    // 2
    @Override
    public int getCount() {
        return numeroslista.size() + 1;
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
            convertView = layoutInflater.inflate(R.layout.linearlayout_numeros_rapidao, null);
        }

        if (position == numeroslista.size()) {
            final LinearLayout linearLayoutNumerosRapidao = (LinearLayout) convertView.findViewById(R.id.LinearLayoutContentNumerosRapidao);

            final TextView tituloTipoAposta = (TextView) convertView.findViewById(R.id.tituloNumeroRapidao);
            tituloTipoAposta.setText("");
            if (isObrigatorio != true) {
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.icon_config_mais2));
            } else {
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.icon_config_mais));
            }

        } else {
            final int numeroRapidao = numeroslista.get(position);
            final TextView tituloTipoAposta = (TextView) convertView.findViewById(R.id.tituloNumeroRapidao);
            tituloTipoAposta.setText(numeroRapidao < 10 ? "0" + numeroRapidao : "" + numeroRapidao);
            if (isObrigatorio != true) {
                final LinearLayout linearLayoutNumerosRapidao = (LinearLayout) convertView.findViewById(R.id.LinearLayoutContentNumerosRapidao);
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_proibidos));
            }

        }

        return convertView;
    }

}