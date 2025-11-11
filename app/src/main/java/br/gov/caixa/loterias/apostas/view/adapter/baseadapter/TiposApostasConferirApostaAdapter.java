package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

/**
 * Created by cedesbr450 on 26/01/18.
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

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.controllers.LerBilhetesEscolhaJogoActivity;


public class TiposApostasConferirApostaAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<TipoAposta> tiposAposta;
    private final LerBilhetesEscolhaJogoActivity parentActivity;

    // 1
    public TiposApostasConferirApostaAdapter(LerBilhetesEscolhaJogoActivity parentActivity, Context context, List<TipoAposta> tiposAposta) {
        this.mContext = context;
        this.tiposAposta = tiposAposta;
        this.parentActivity = parentActivity;
    }

    // 2
    @Override
    public int getCount() {
        return tiposAposta.size();
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
            convertView = layoutInflater.inflate(R.layout.linearlayout_tipos_apostas, null);
        }

        final TipoAposta tipoAposta = tiposAposta.get(position);
        final ImageView imageViewTipoAPosta = convertView.findViewById(R.id.imageviewTrevoTipoAposta);

        final LinearLayout linearLayoutTipoAposta = convertView.findViewById(R.id.LinearLayoutContentTiposAposta);

        final TextView tituloTipoAposta = convertView.findViewById(R.id.tituloTipoApostaTextView);
        tituloTipoAposta.setText(tipoAposta.getTitulo());

        if (tipoAposta.getSelect()){
            imageViewTipoAPosta.setImageResource(R.drawable.trevo);
            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
            tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
        }else{
            imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, R.color.branco));
            linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext,R.drawable.custom_border_cinza));
            tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.cinzanaoselecionado));
        }

        convertView.setOnClickListener(v -> {
            if (tiposAposta.get(position).getSelect()){
                tiposAposta.get(position).setSelect(false);
                imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
                linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, R.color.branco));
                linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext,R.drawable.custom_border_cinza));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.cinzanaoselecionado));
            }else{
                tiposAposta.get(position).setSelect(true);
                imageViewTipoAPosta.setImageResource(R.drawable.trevo);
                linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
            }

            for (TipoAposta item : tiposAposta) {
                if (item.getSelect()) {
                    parentActivity.mudarLayoutBotaoSalvar(true);
                    return;
                }
            }
            parentActivity.mudarLayoutBotaoSalvar(false);

        });


        return convertView;
    }
}
