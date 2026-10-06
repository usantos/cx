package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by cedesbr450 on 22/02/18.
 */

public class FavoritasHorizontalScrollView extends HorizontalScrollView {
    private OnScrollViewListener mOnScrollViewListener;

    public FavoritasHorizontalScrollView(Context context) {
        super(context);
    }

    public FavoritasHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavoritasHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface OnScrollViewListener {
        void onScrollChanged( FavoritasHorizontalScrollView v, int l, int t, int oldl, int oldt );
    }

    public void setOnScrollViewListener(OnScrollViewListener l) {
        this.mOnScrollViewListener = l;
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        mOnScrollViewListener.onScrollChanged( this, l, t, oldl, oldt );
        super.onScrollChanged( l, t, oldl, oldt );
    }
}


