package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

/**
 * Created by cedesbr450 on 21/12/17.
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
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.RapidaoConfigSingleton;


public class TiposApostasMultiplaSelecaoAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<TipoAposta> tiposAposta;

    // 1
    public TiposApostasMultiplaSelecaoAdapter(Context context, List<TipoAposta> tiposAposta) {
        this.mContext = context;
        this.tiposAposta = tiposAposta;
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
//            if(tipoAposta.getValor() == 9){
//                imageViewTipoAPosta.setImageResource(R.drawable.ic_caixa_mm_amarelo_branco);
//            }else {
//                imageViewTipoAPosta.setImageResource(R.drawable.trevo);
//            }
            imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
            tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, tipoAposta.getTextColor()));
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
//                if(tipoAposta.getValor() == 9){
//                    imageViewTipoAPosta.setImageResource(R.drawable.ic_caixa_mm_amarelo_branco);
//                }else {
//                    imageViewTipoAPosta.setImageResource(R.drawable.trevo);
//                }
                imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
                linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
                tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, tipoAposta.getTextColor()));
            }

            RapidaoConfigSingleton.getInstance().getRapidaoConfig().setModalidades(convertTipoApostaParaModalidades());
        });

        return convertView;
    }

    private List<ModalidadeDTO> convertTipoApostaParaModalidades() {
        List<ModalidadeDTO> modalidades = new ArrayList<>();
        for (TipoAposta tipoAposta: tiposAposta) {
            if(tipoAposta.getSelect()){
                ModalidadeDTO modalidade = new ModalidadeDTO();
                modalidade.setDescricao(tipoAposta.getTitulo());
                modalidade.setDescricaoEspecial(tipoAposta.getDescricaoEspecial());
                modalidade.setValor(tipoAposta.getValor());
                modalidades.add(modalidade);
            }
        }
        return modalidades;
    }
}