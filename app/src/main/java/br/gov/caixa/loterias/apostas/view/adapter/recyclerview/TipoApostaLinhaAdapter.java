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
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
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
    private boolean isBold;

    public TipoApostaLinhaAdapter(Context context, List<TipoApostaLinhaEnum> itemList,int textColor, int iconColor, boolean isBold) {
        this.context = context;
        this.itemList = itemList;
        this.textColor = textColor;
        this.iconColor = iconColor;
        this.isBold = isBold;
    }

    @Override
    public TipoApostaLinhaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(isBold ? R.layout.row_tipo_aposta_bold : R.layout.row_tipo_aposta, parent, false);
        return new TipoApostaLinhaHolder(view);
    }

    @Override
    public void onBindViewHolder(TipoApostaLinhaHolder holder, int position) {
        TipoApostaLinhaEnum tipoApostaLinhaEnum = itemList.get(position);

        holder.itemView.setContentDescription(context.getString(tipoApostaLinhaEnum.getTextResId()));
        ViewCompat.setAccessibilityHeading(holder.itemView, true);
        holder.itemView.setFocusable(true);
        holder.itemView.setClickable(false);

        ViewCompat.setAccessibilityDelegate(holder.itemView, new AccessibilityDelegateCompat(){
            @Override
            public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                info.setClassName(TextView.class.getName());
            }
        });

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
            itemView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            icon = itemView.findViewById(R.id.row_item_icon);
            text = itemView.findViewById(R.id.row_item_text);
        }
    }
}
