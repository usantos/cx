package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

/**
 * Created by cedesbr450 on 20/12/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;


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
        return numeroslista.size();
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


            final int numeroRapidao = numeroslista.get(position);
            final TextView tituloTipoAposta = convertView.findViewById(R.id.tituloNumeroRapidao);
            tituloTipoAposta.setText(numeroRapidao < 10 ? mContext.getResources().getString(R.string.zero) + numeroRapidao : mContext.getResources().getString(R.string.string_vazia) + numeroRapidao);
            if (isObrigatorio != true) {
                final LinearLayout linearLayoutNumerosRapidao = convertView.findViewById(R.id.LinearLayoutContentNumerosRapidao);
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_proibidos));
            }


        return convertView;
    }

}