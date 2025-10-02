package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.view.custom.PartidaViewLotecaResultado;

public class TipoApostaLinhaAdapter extends RecyclerView.Adapter<TipoApostaLinhaAdapter.TipoApostaLinhaHolder> {

    private List<TipoApostaLinhaEnum> itemList;
    private  Context context;
    private int textColor;
    private int iconColor;

    public TipoApostaLinhaAdapter(Context context, List<TipoApostaLinhaEnum> itemList,int textColor, int iconColor) {
        this.context = context;
        this.itemList = itemList;
        this.textColor = textColor;
        this.iconColor = iconColor;
    }

    @Override
    public TipoApostaLinhaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_tipo_aposta, parent, false);
        return new TipoApostaLinhaHolder(view);
    }

    @Override
    public void onBindViewHolder(TipoApostaLinhaHolder holder, int position) {
        TipoApostaLinhaEnum tipoApostaLinhaEnum = itemList.get(position);

        holder.text.setText(context.getString(tipoApostaLinhaEnum.getTextResId()));
        holder.text.setTextColor(ContextCompat.getColor(context, textColor));

        holder.icon.setImageResource(tipoApostaLinhaEnum.getIconResId());
        holder.icon.setColorFilter(ContextCompat.getColor(context, iconColor), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class TipoApostaLinhaHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView text;

        public TipoApostaLinhaHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.row_item_icon);
            text = itemView.findViewById(R.id.row_item_text);
        }
    }
}
