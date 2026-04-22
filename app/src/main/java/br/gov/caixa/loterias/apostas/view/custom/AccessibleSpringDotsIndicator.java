package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class AccessibleSpringDotsIndicator extends LinearLayout {

    private ViewPager2 viewPager2;
    private int dotCount = 0;
    private int selectedIndex = 0;
    private final List<View> dots = new ArrayList<>();

    private int dotColor = Color.TRANSPARENT;
    private int dotSelectedColor = Color.WHITE;
    private int dotStrokeColor = Color.WHITE;
    private int dotSize;
    private int dotSpacing;
    private float dotCornerRadius;

    private final int BORDER_WIDTH_NORMAL;
    private final int BORDER_WIDTH_SELECTED;
    public interface OnDotClickListener {
        void onDotClicked(int index);
    }

    private OnDotClickListener onDotClickListener;

    public void setOnDotClickListener(OnDotClickListener listener) {
        this.onDotClickListener = listener;
    }

    public AccessibleSpringDotsIndicator(Context context) {
        super(context);
        BORDER_WIDTH_NORMAL = dpToPx(1);
        BORDER_WIDTH_SELECTED = dpToPx(2);
        init(context, null);
    }

    public AccessibleSpringDotsIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        BORDER_WIDTH_NORMAL = dpToPx(1);
        BORDER_WIDTH_SELECTED = dpToPx(2);
        init(context, attrs);
    }

    public AccessibleSpringDotsIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BORDER_WIDTH_NORMAL = dpToPx(1);
        BORDER_WIDTH_SELECTED = dpToPx(2);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL);

        dotSize = dpToPx(8);
        dotSpacing = dpToPx(4);
        dotCornerRadius = dpToPx(4);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccessibleDotsIndicator);
            dotColor = typedArray.getColor(R.styleable.AccessibleDotsIndicator_dotsColor, dotColor);
            dotSelectedColor = typedArray.getColor(R.styleable.AccessibleDotsIndicator_dotsSelectedColor, dotSelectedColor);
            dotSize = typedArray.getDimensionPixelSize(R.styleable.AccessibleDotsIndicator_dotsSize, dotSize);
            dotSpacing = typedArray.getDimensionPixelSize(R.styleable.AccessibleDotsIndicator_dotsSpacing, dotSpacing);
            dotCornerRadius = typedArray.getDimension(R.styleable.AccessibleDotsIndicator_dotsCornerRadius, dotCornerRadius);
            dotStrokeColor = typedArray.getColor(R.styleable.AccessibleDotsIndicator_dotsStrokeColor, dotStrokeColor);
            typedArray.recycle();
        }
    }

    public void setDotCount(int count) {
        dotCount = count;
        createDots();
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
        updateDots();
    }

    public void setDotContentDescription(int index, String description) {
        if (index >= 0 && index < dots.size()) {
            dots.get(index).setContentDescription(description);
        }
    }

    public void setViewPager2(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;

        if (viewPager2.getAdapter() == null) return;

        setDotCount(viewPager2.getAdapter().getItemCount());

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setSelectedIndex(position);
            }
        });
    }

    private void createDots() {
        removeAllViews();
        dots.clear();

        for (int i = 0; i < dotCount; i++) {
            final int index = i;
            View dot = new View(getContext());
            LayoutParams params = new LayoutParams(dotSize, dotSize);
            if (i != 0) {
                params.setMarginStart(dotSpacing);
            }
            dot.setLayoutParams(params);
            dot.setBackground(createDotDrawable(dotColor));
            dot.setContentDescription("Página " + (index + 1));

            ViewCompat.setAccessibilityDelegate(dot, new AccessibilityDelegateCompat() {
                @Override
                public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(host, info);

                    info.setClassName(android.widget.Button.class.getName()); // semântica de botão
                    info.setClickable(true);
                    info.setFocusable(true);
                }
            });

            dot.setOnClickListener(v -> {
                if (onDotClickListener != null) {
                    onDotClickListener.onDotClicked(index);
                    setSelectedIndex(index);
                    focusAndAnnounceDotAtual();
                }

                if (viewPager2 != null) {
                    viewPager2.setCurrentItem(index, true);
                }
            });

            dots.add(dot);
            addView(dot);
        }

        updateDots();
    }

    private void focusAndAnnounceDotAtual() {
        if (selectedIndex < 0 || selectedIndex >= dots.size()) return;

        View dot = dots.get(selectedIndex);

        CharSequence cs = dot.getContentDescription();
        final String announcement = (cs != null && cs.length() > 0)
                ? cs.toString()
                : ("Página " + (selectedIndex + 1) + " Atual");

        dot.post(() -> {
            dot.setFocusable(true);
            dot.setFocusableInTouchMode(true);

            dot.requestFocus();
            dot.sendAccessibilityEvent(android.view.accessibility.AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
            dot.announceForAccessibility(announcement);
        });
    }

    private void updateDots() {
        for (int i = 0; i < dots.size(); i++) {
            View dot = dots.get(i);
            GradientDrawable drawable = (GradientDrawable) dot.getBackground();

            int color = (i == selectedIndex) ? dotSelectedColor : dotColor;
            int strokeWidth = (i == selectedIndex) ? BORDER_WIDTH_SELECTED : BORDER_WIDTH_NORMAL;

            drawable.setColor(color);
            drawable.setStroke(strokeWidth, dotStrokeColor);

            if (i == selectedIndex) {
                float finalScale = 1f;
                dot.setContentDescription("Página " + (i + 1) + " Atual");

                SpringForce spring = new SpringForce(finalScale);
                spring.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                spring.setStiffness(SpringForce.STIFFNESS_LOW);

                SpringAnimation scaleX = new SpringAnimation(dot, SpringAnimation.SCALE_X, finalScale);
                SpringAnimation scaleY = new SpringAnimation(dot, SpringAnimation.SCALE_Y, finalScale);

                scaleX.setSpring(spring);
                scaleY.setSpring(spring);

                scaleX.start();
                scaleY.start();
            } else {
                dot.setContentDescription("Página " + (i + 1));
                dot.setScaleX(1f);
                dot.setScaleY(1f);
            }
        }
    }

    private Drawable createDotDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dotCornerRadius);
        drawable.setColor(color);
        drawable.setStroke(BORDER_WIDTH_NORMAL, dotStrokeColor);
        return drawable;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
