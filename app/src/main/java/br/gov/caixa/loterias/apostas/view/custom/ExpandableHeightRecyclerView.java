package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by joafilho on 02/01/2018.
 */

public class ExpandableHeightRecyclerView extends RecyclerView {

    boolean expanded;

    public ExpandableHeightRecyclerView(Context context) {
        super(context);
    }

    public ExpandableHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (isExpanded()) {
            int expandSpec = View.MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                    View.MeasureSpec.AT_MOST);
            super.onMeasure(widthSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else {
            super.onMeasure(widthSpec, heightSpec);
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
