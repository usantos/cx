package caixa.gov.br.loterias.view.adapter;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import caixa.gov.br.loterias.R;

/**
 * Created by pmotta on 15/03/2018.
 */

public class ApostasConfirmadasAdapter extends RecyclerView.Adapter<ApostasConfirmadasAdapter.ViewHolder> {

    private OnApostasConfirmadasClick onApostasConfirmadasClick;

    public interface OnApostasConfirmadasClick{
        void onReload(int position);
        void onFavorite(int position);
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View stripe;
        private TextView modalidadeTxt;
        private TextView concursoTxt;
        private TextView valorTxt;
        private View favoriteBtn;
        private View reloadBtn;

        public ViewHolder(View view){
            super(view);
            stripe = view.findViewById(R.id.stripe);
            modalidadeTxt = view.findViewById(R.id.modalidadeTxt);
            concursoTxt = view.findViewById(R.id.concursoTxt);
            valorTxt = view.findViewById(R.id.valorTxt);
            favoriteBtn = view.findViewById(R.id.favoriteBtn);
            reloadBtn = view.findViewById(R.id.reloadBtn);

            favoriteBtn.setOnClickListener(this);
            reloadBtn.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onApostasConfirmadasClick != null) {
                switch (view.getId()){
                    case R.id.favoriteBtn:{
                        onApostasConfirmadasClick.onFavorite(getAdapterPosition());
                        break;
                    }
                    case R.id.reloadBtn:{
                        onApostasConfirmadasClick.onReload(getAdapterPosition());
                        break;
                    }
                    default:
                        onApostasConfirmadasClick.onItemClick(getAdapterPosition());
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_favoritas,
                parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
