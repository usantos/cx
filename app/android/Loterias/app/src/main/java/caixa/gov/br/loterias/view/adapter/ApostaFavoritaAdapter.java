package caixa.gov.br.loterias.view.adapter;

/**
 * Created by cedesbr450 on 20/12/17.
 */


        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import caixa.gov.br.loterias.R;
        import caixa.gov.br.loterias.model.ApostaFavorita;


public class ApostaFavoritaAdapter extends BaseAdapter {

    private final Context mContext;
    private final ApostaFavorita[] apostaFavorita;

    // 1
    public ApostaFavoritaAdapter(Context context, ApostaFavorita[] apostaFavorita) {
        this.mContext = context;
        this.apostaFavorita = apostaFavorita;
    }

    // 2
    @Override
    public int getCount() {
        return apostaFavorita.length +1;
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
            convertView = layoutInflater.inflate(R.layout.linearlayout_aposta_favorita, null);
        }

        if (position == apostaFavorita.length){
//            final LinearLayout linearLayoutTipoAposta = (LinearLayout)convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
//            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, R.color.branco));
//            linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext,R.drawable.customborder));
//
//            final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloTipoApostaTextView);
//            tituloTipoAposta.setText("Adicionar");
//            tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.verdeazul));
//            final ImageView imageViewTipoAPosta = (ImageView)convertView.findViewById(R.id.imageviewTrevoTipoAposta);
//            imageViewTipoAPosta.setVisibility(View.GONE);
//
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
//            params.setMargins(0,0,0,0);
//            tituloTipoAposta.setLayoutParams(params);

        }else{
            final ApostaFavorita aposta = apostaFavorita[position];
//            final ImageView imageViewTipoAPosta = (ImageView)convertView.findViewById(R.id.imageviewTrevoTipoAposta);
//            imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
//
//            final LinearLayout linearLayoutTipoAposta = (LinearLayout)convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
//            linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
//
//            final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloTipoApostaTextView);
//            tituloTipoAposta.setText(tipoAposta.getTitulo());
        }

        return convertView;
    }

}