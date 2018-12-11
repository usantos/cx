package br.gov.caixa.loterias.apostar.view.adapter;

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

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.NumerosRapidaoActivity;


public class NumeroSelecionadoRapidaoAdapter extends BaseAdapter {

    private final Context mContext;
    private final NumerosSelecionadosRapidao[] numerosSelecionadosRapidao;
    private final NumerosRapidaoActivity parentActivity;
    private final Boolean isObrigatorio;
    private List<Integer> listaNumerosContrariosRapidao = new ArrayList<Integer>();
    // 1
    public NumeroSelecionadoRapidaoAdapter(NumerosRapidaoActivity parentActivity, Context context, NumerosSelecionadosRapidao[] numerosSelecionadosRapidao, Boolean isObrigatorio, List<Integer> listaNumerosContrariosRapidao) {
        this.parentActivity = parentActivity;
        this.mContext = context;
        this.numerosSelecionadosRapidao = numerosSelecionadosRapidao;
        this.isObrigatorio = isObrigatorio;
        this.listaNumerosContrariosRapidao = listaNumerosContrariosRapidao;
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

        if (isObrigatorio){
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
                for(Integer item : listaNumerosContrariosRapidao){
                    if (item == numerosSelecionadosRapidao[position].getNumero()){
                        ViewUtils.alert(mContext, mContext.getString(R.string.MA016));
                        return;
                    }
                }
                if (isObrigatorio){
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