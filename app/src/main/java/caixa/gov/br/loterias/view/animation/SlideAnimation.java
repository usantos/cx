package caixa.gov.br.loterias.view.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Fabio Vieira on 28/05/2018.
 */

public class SlideAnimation extends Animation {

    private int _fromHeight;
    private int _toHeight;
    private View _view;
    private UpdateListener _updateListener;

    public interface UpdateListener {
        void onUpdateAnimation(int fromHeight, int toHeight, int currentHeight, View view);
    }

    public SlideAnimation(View view, int fromHeight, int toHeight) {
        _view = view;
        _fromHeight = fromHeight;
        _toHeight = toHeight;
    }

    public void animate(long duration, AnimationListener listener, UpdateListener updateListener) {
        setInterpolator(new AccelerateInterpolator());
        setDuration(duration);
        _view.setAnimation(this);
        _view.startAnimation(this);
        if (listener != null) {
            setAnimationListener(listener);
        }
        if (updateListener != null) {
            this._updateListener = updateListener;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int newHeight;

        if (_view.getHeight() != _toHeight) {
            newHeight = (int) (_fromHeight + ((_toHeight - _fromHeight) * interpolatedTime));
            _view.getLayoutParams().height = newHeight;

            if (this._updateListener != null) {
                this._updateListener.onUpdateAnimation(_fromHeight, _toHeight, newHeight, _view);
            }

            _view.requestLayout();
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
