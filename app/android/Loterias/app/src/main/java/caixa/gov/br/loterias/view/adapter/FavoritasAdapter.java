package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.utils.ViewUtils;


/**
 * Created by cedesbr450 on 12/01/18.
 */


public class FavoritasAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<ApostaFavoritaDTO> apostaFavorita;


    // 1
    public FavoritasAdapter(Context context, List<ApostaFavoritaDTO> apostaFavorita) {
        this.mContext = context;
        this.apostaFavorita = apostaFavorita;
    }

    // 2
    @Override
    public int getCount() {
        return apostaFavorita.size();
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
            convertView = layoutInflater.inflate(R.layout.linearlayout_favoritas, null);
        }

        final ApostaFavoritaDTO aposta = apostaFavorita.get(position);


        final RelativeLayout relativeLayoutContentFavoritas = (RelativeLayout) convertView.findViewById(R.id.RelativeLayoutContentFavoritas);
        final RelativeLayout relativeLayoutContentAlterarFavoritas = (RelativeLayout) convertView.findViewById(R.id.RelativeLayoutContentAlterarFavoritas);


        final TextView tituloApostaFavorira = (TextView) convertView.findViewById(R.id.tituloApostaFavorira);
//        tituloApostaFavorira.setText(aposta.getModalidade().getDescricao());

        final TextView numerosApostaFavorita = (TextView) convertView.findViewById(R.id.numerosApostaFavorita);
        String numeroStr = "";
        for (Integer num : aposta.getNumerosSelecionados()) {
            if (num == (aposta.getNumerosSelecionados().get(aposta.getNumerosSelecionados().size() - 1))) {
                numeroStr += String.format("%02d", num);
            } else {
                numeroStr += String.format("%02d - ", num);
            }
        }
        numerosApostaFavorita.setText(numeroStr);

        convertView.setOnTouchListener(new View.OnTouchListener() {
            private String TAG = "Favoritas";
            float initialX;

            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                int action = event.getActionMasked();
                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        Log.d(TAG, "Action was DOWN");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "Action was MOVE");
//                        Log.d("event.getX()", "event.getX(): " + event.getX());
                        Float now = event.getX();
                        Float distX = now - initialX;
                        int widthView = relativeLayoutContentFavoritas.getWidth();
                        int d = (int) Math.min(distX, widthView - ViewUtils.setDisplayMetric(70, mContext));
                        relativeLayoutContentFavoritas.setX(d);


                        break;
                    case MotionEvent.ACTION_UP:
                        float finalX = event.getX();

                        Log.d(TAG, "Action was UP");

                        if (initialX < finalX) {
                            Log.d(TAG, "Left to Right swipe performed");
                        }

                        if (initialX > finalX) {
                            Log.d(TAG, "Right to Left swipe performed");
                        }

                        break;

                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG, "Action was CANCEL");
                        break;

                    case MotionEvent.ACTION_OUTSIDE:
                        Log.d(TAG, "Movement occurred outside bounds of current screen element");
                        break;
                }


                return true;
            }
        });


        return convertView;
    }

}