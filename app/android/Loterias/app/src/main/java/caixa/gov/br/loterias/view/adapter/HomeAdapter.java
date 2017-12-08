package caixa.gov.br.loterias.view.adapter;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.Modalidade;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;

import java.util.List;

/**
 * Created by brfernan on 26/10/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private int itemHeight;
    private List<Modalidade> data;
    private EstiloModalidade estilo;
    private Context context;
    private static Button aposta;

    public HomeAdapter(Context context, List<Modalidade> data, View aposta) {
        this.data = data;
        this.context = context;
        this.aposta = (Button) aposta;
    }

//    @Override
//    public void onAttachedToRecyclerView (RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        Activity context = (Activity) recyclerView.getContext();
//        Point windowDimensions = new Point();
//        context.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
//        itemHeight = Math.round(windowDimensions.y * 0.4f);
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.card_modalidade, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        estilo = new EstiloModalidade(data.get(position).getTipoModalidade());

        holder.game.setText(data.get(position).getNome());
        holder.resultados.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorEscura()));
        holder.trevo.setBackground(ContextCompat.getDrawable(context, estilo.getTrevoBackground()));
        holder.trevoFundo.setBackground(ContextCompat.getDrawable(context, estilo.getTrevoBackground()));
        holder.titulo.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
        holder.valor.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
        aposta.setText(ViewUtils.fromHtml("<b>Aposte</b><br>por R$" + data.get(position).getValor()));
        Log.d("nome", data.get(position).getNome());

        Log.d("position", "" + position);

        Log.d("valor", "" + data.get(position).getValor());
        Glide.with(holder.itemView.getContext())
                .load(estilo.getImagemCalendario())
                .into(holder.calendario);
        Glide.with(holder.itemView.getContext())
                .load(estilo.getImagemRelogio())
                .into(holder.relogio);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View titulo;
        private TextView game;
        private ImageView relogio;
        private ImageView calendario;
        private LinearLayout resultados;
        private TextView valor;
        private ImageView trevo;
        private RelativeLayout trevoFundo;


        public ViewHolder (View itemView) {
            super(itemView);
            game = (TextView) itemView.findViewById(R.id.textViewCardTitle);
            resultados = (LinearLayout) itemView.findViewById(R.id.relativeLayoutTopResultados);
            trevo = (ImageView) itemView.findViewById(R.id.trevoImagem);
            trevoFundo = (RelativeLayout) itemView.findViewById(R.id.RelativeLayoutTrevo) ;
            calendario = (ImageView)  itemView.findViewById(R.id.imageCalendar);
            relogio = (ImageView)  itemView.findViewById(R.id.imageWatch);
            titulo = itemView.findViewById(R.id.relativeLayoutTopModalidades);
            valor = itemView.findViewById(R.id.textVieCardAcumulatedValue);
        }

        public void setOverlayColor(Context context, EstiloModalidade estilo) {
            aposta.setBackgroundColor(ContextCompat.getColor(context, estilo.getCorClara()));
        }
    }
}
