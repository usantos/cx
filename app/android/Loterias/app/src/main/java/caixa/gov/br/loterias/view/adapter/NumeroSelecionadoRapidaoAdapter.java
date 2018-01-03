package caixa.gov.br.loterias.view.adapter;

/**
 * Created by cedesbr450 on 21/12/17.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.NumerosSelecionadosRapidao;
import caixa.gov.br.loterias.view.NumerosRapidaoActivity;


public class NumeroSelecionadoRapidaoAdapter extends BaseAdapter {

    private final Context mContext;
    private final NumerosSelecionadosRapidao[] numerosSelecionadosRapidao;
    private final NumerosRapidaoActivity parentActivity;
    private final Boolean isObrigatorio;

    // 1
    public NumeroSelecionadoRapidaoAdapter(NumerosRapidaoActivity parentActivity, Context context, NumerosSelecionadosRapidao[] numerosSelecionadosRapidao, Boolean isObrigatorio) {
        this.parentActivity = parentActivity;
        this.mContext = context;
        this.numerosSelecionadosRapidao = numerosSelecionadosRapidao;
        this.isObrigatorio = isObrigatorio;
    }

    // 2
    @Override
    public int getCount() {
        return numerosSelecionadosRapidao.length;
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
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_numeros_rapidao, null);
        }

        final NumerosSelecionadosRapidao numeroRapidao = numerosSelecionadosRapidao[position];
        final TextView tituloTipoAposta = (TextView) convertView.findViewById(R.id.tituloNumeroRapidao);
        tituloTipoAposta.setText("" + numeroRapidao.getNumero());
        final LinearLayout linearLayoutNumerosRapidao = (LinearLayout) convertView.findViewById(R.id.LinearLayoutContentNumerosRapidao);
        if (numeroRapidao.getSelecionado()) {
            linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_obrigatorios));
        } else {
            linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_radius_verde_azul));
        }

        if (isObrigatorio == true){
            if (numerosSelecionadosRapidao[position].getSelecionado()) {
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_obrigatorios));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
            } else {
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_radius_verde_azul));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.verdeazul));

            }
        }else{
            if (numerosSelecionadosRapidao[position].getSelecionado()) {
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_proibidos));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
            } else {
                linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_radius_vermelho_bilhetes));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.vermelhoBilhetes));

            }
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isObrigatorio == true){
                    if (numerosSelecionadosRapidao[position].getSelecionado()) {
                        numerosSelecionadosRapidao[position].setSelecionado(false);
                        linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_radius_verde_azul));
                        tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.verdeazul));
                    } else {
                        numerosSelecionadosRapidao[position].setSelecionado(true);
                        linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_obrigatorios));
                        tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
                    }
                }else{
                    if (numerosSelecionadosRapidao[position].getSelecionado()) {
                        numerosSelecionadosRapidao[position].setSelecionado(false);
                        linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_radius_vermelho_bilhetes));
                        tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.vermelhoBilhetes));
                    } else {
                        numerosSelecionadosRapidao[position].setSelecionado(true);
                        linearLayoutNumerosRapidao.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_numeros_proibidos));
                        tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
                    }
                }

                for (NumerosSelecionadosRapidao item : numerosSelecionadosRapidao) {
                    if (item.getSelecionado()) {
                        parentActivity.mudarLayoutBotaoLimparSelecao(true);
                        return;
                    }
                }
                parentActivity.mudarLayoutBotaoLimparSelecao(false);
            }
        });
        return convertView;
    }

}