package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

/**
 * Created by cedesbr450 on 21/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.NumerosSelecionadosRapidao;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;


public class NumeroSelecionadoRapidaoAdapter extends BaseAdapter {

    private final Context mContext;
    private final NumerosSelecionadosRapidao[] numerosSelecionadosRapidao;
    private final Activity parentActivity;
    private final Boolean isObrigatorio;
    private List<Integer> listaNumerosContrariosRapidao = new ArrayList<>();
    private NumeroSelecionadoListener listener;
    // 1
    public NumeroSelecionadoRapidaoAdapter(Activity parentActivity,
                                           Context context,
                                           NumerosSelecionadosRapidao[] numerosSelecionadosRapidao,
                                           Boolean isObrigatorio,
                                           List<Integer> listaNumerosContrariosRapidao,
                                           NumeroSelecionadoListener listener) {
        this.parentActivity = parentActivity;
        this.mContext = context;
        this.numerosSelecionadosRapidao = numerosSelecionadosRapidao;
        this.isObrigatorio = isObrigatorio;
        this.listaNumerosContrariosRapidao = listaNumerosContrariosRapidao;
        this.listener = listener;
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
        final TextView tituloTipoAposta = convertView.findViewById(R.id.tituloNumeroRapidao);
        if(numeroRapidao.getNumero() == 100){
            tituloTipoAposta.setText(mContext.getResources().getString(R.string.string_vazia) + "00");
        } else {
            tituloTipoAposta.setText(mContext.getResources().getString(R.string.string_vazia) + numeroRapidao.getNumero());
        }
        final LinearLayout linearLayoutNumerosRapidao = convertView.findViewById(R.id.LinearLayoutContentNumerosRapidao);
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

        convertView.setOnClickListener(v -> {
            for(Integer item : listaNumerosContrariosRapidao){
                if (item == numerosSelecionadosRapidao[position].getNumero()){
                    DialogUtils.dialogEntendi(mContext, mContext.getString(R.string.MA016));
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
                    listener.mudarLayoutBotaoLimparSelecao(true);
                    return;
                }
            }
            listener.mudarLayoutBotaoLimparSelecao(false);
        });
        return convertView;
    }

    public interface NumeroSelecionadoListener{
        void mudarLayoutBotaoLimparSelecao(Boolean visible);
    }
}