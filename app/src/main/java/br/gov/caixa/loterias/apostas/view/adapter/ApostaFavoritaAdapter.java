package br.gov.caixa.loterias.apostas.view.adapter;

/**
 * Created by cedesbr450 on 20/12/17.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;


public class ApostaFavoritaAdapter extends BaseAdapter {

    private final Context mContext;
    private List<ApostaFavoritaDTO> favoritosSalvos;

    // 1
    public ApostaFavoritaAdapter(Context context, List<ApostaFavoritaDTO> favoritosSalvos) {
        this.mContext = context;
        this.favoritosSalvos = favoritosSalvos;
    }

    // 2
    @Override
    public int getCount() {
        return favoritosSalvos.size();
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
            final LayoutInflater layoutInflater = LayoutInflater.from( mContext );
            convertView = layoutInflater.inflate( R.layout.linearlayout_aposta_favorita, null );
        }

        final ApostaFavoritaDTO aposta = favoritosSalvos.get( position );


        final TextView tituloApostaFavorira = convertView.findViewById( R.id.tituloApostaFavorira );
        tituloApostaFavorira.setText( aposta.getModalidade().getDescricao() );
        EstiloModalidade estilo = new EstiloModalidade(ModalidadeEnum.toString(favoritosSalvos.get(position).getModalidade().getDescricao().toLowerCase(new Locale(mContext.getResources().getString(R.string.pt), mContext.getResources().getString(R.string.br)))));
        tituloApostaFavorira.setText(favoritosSalvos.get(position).getModalidade().getDescricao());
        tituloApostaFavorira.setTextColor(parent.getResources().getColor(estilo.getCorClara()));

        final TextView numerosApostaFavorita = convertView.findViewById( R.id.numerosApostaFavorita );
        StringBuilder numeroStr = new StringBuilder();

        List<Integer> numerosSelecionados =  aposta.getListaNumerosSelecionados();
        for (Integer num : numerosSelecionados) {
            if (num.equals(numerosSelecionados.get(numerosSelecionados.size() - 1 ))) {
                numeroStr.append(String.format(mContext.getResources().getString(R.string.percent_zero_dois_d), num));
            } else {
                numeroStr.append(String.format(mContext.getResources().getString(R.string.percent_zero_dois_d_traco), num));
            }
        }

        if (ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()) == ModalidadeEnum.TIMEMANIA) {
            numeroStr.append(mContext.getResources().getString(R.string.barra_n_time_do_coracao_dois_pontos_espaco)).append(aposta.getTimeDoCoracao().getNome());
        }

        if (ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()) == ModalidadeEnum.DIA_DE_SORTE) {
            numeroStr.append(mContext.getResources().getString(R.string.barra_n_mes_da_sorte_dois_pontos)).append(aposta.getMesDeSorte().getNome());
        }

        numerosApostaFavorita.setText(numeroStr.toString());

        final CheckBox checkbox = convertView.findViewById( R.id.checkboxApostaFavorita );
        checkbox.setVisibility(View.GONE);

        return convertView;
    }
}