package caixa.gov.br.loterias.view.adapter;

/**
 * Created by cedesbr450 on 20/12/17.
 */
        import android.content.Context;
        import android.support.v4.content.ContextCompat;
        import android.util.TypedValue;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import caixa.gov.br.loterias.R;


public class NumerosRapidaoAdapter extends BaseAdapter {

    private final Context mContext;
    private final int[] numeroslista;

    // 1
    public NumerosRapidaoAdapter(Context context, int[] numeroslista) {
        this.mContext = context;
        this.numeroslista = numeroslista;
    }

    // 2
    @Override
    public int getCount() {
        return numeroslista.length +1;
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

        if (position == numeroslista.length){
            final LinearLayout linearLayoutTipoAposta = (LinearLayout)convertView.findViewById(R.id.LinearLayoutContentNumerosRapidao);
            linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_radius_verde_azul));

            final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloNumeroRapidao);
            tituloTipoAposta.setText("+");
            tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.verdeazul));

        }else{
            final int numeroRapidao = numeroslista[position];
            final TextView tituloTipoAposta = (TextView)convertView.findViewById(R.id.tituloNumeroRapidao);
            tituloTipoAposta.setText(""+numeroRapidao);

        }

        return convertView;
    }

}