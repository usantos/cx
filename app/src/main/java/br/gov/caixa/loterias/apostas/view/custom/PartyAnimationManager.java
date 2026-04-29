package br.gov.caixa.loterias.apostas.view.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import br.gov.caixa.loterias.apostas.R;

public class PartyAnimationManager {

    private static final int MAX_BALLOONS = 20;
    private int currentBalloonCount = 0;

    private final Context context;
    private final FrameLayout animationContainer;
    private final FrameLayout bannerOverlay;   // always on top of animationContainer
    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAnimating = false;
    private MediaPlayer mediaPlayer;

    // Banners
    private TextView bannerTopRight;
    private TextView bannerCenter;

    // Balloon colors: opaque vivid colors — BalloonView applies RadialGradient from these
    private final int[] balloonColors = {
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#E91E63"), // Pink
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#FFC107"), // Amber
    };

    // Confetti colors: bright and saturated like paper strips
    private final int[] confettiColors = {
            Color.parseColor("#FF3D00"), // Deep Orange
            Color.parseColor("#2979FF"), // Blue
            Color.parseColor("#00E676"), // Green
            Color.parseColor("#FFEA00"), // Yellow
            Color.parseColor("#D500F9"), // Purple
            Color.parseColor("#FF1744"), // Red
            Color.parseColor("#00B0FF"), // Light Blue
            Color.parseColor("#76FF03"), // Light Green
    };

    public PartyAnimationManager(Activity activity) {
        this.context = activity;

        // Layer 1 – animation (balloons + confetti)
        this.animationContainer = new FrameLayout(activity);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        this.animationContainer.setLayoutParams(params);
        this.animationContainer.setClickable(false);
        this.animationContainer.setFocusable(false);

        // Layer 2 – banners (always rendered above animations)
        this.bannerOverlay = new FrameLayout(activity);
        this.bannerOverlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.bannerOverlay.setClickable(false);
        this.bannerOverlay.setFocusable(false);
        this.bannerOverlay.setClipChildren(false); // allow shadow to bleed
        this.bannerOverlay.setClipToPadding(false);

        ViewGroup rootView = activity.findViewById(android.R.id.content);
        rootView.addView(this.animationContainer);
        rootView.addView(this.bannerOverlay);  // added AFTER → always on top
    }

    public void start(long durationMillis) {
        start();
        if (durationMillis > 0) {
            handler.postDelayed(this::stop, durationMillis);
        }
    }

    public void start() {
        if (isAnimating) return;
        isAnimating = true;

        // Tocar som de aplauso
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.aplauso);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }

        // Show promotional banners
        showBanners();

        // --- Balloon spawner ---
        Runnable balloonSpawner = new Runnable() {
            @Override
            public void run() {
                if (!isAnimating) return;
                // 2-3 balloons per tick → dense coverage like in the image
                int count = 2 + random.nextInt(2);
                for (int i = 0; i < count; i++) spawnBalloon();
                long delay = 100 + random.nextInt(150);
                handler.postDelayed(this, delay);
            }
        };
        handler.post(balloonSpawner);

        // --- Confetti spawner (batch) ---
        Runnable confettiSpawner = new Runnable() {
            @Override
            public void run() {
                if (!isAnimating) return;
                int count = 4 + random.nextInt(5);
                for (int i = 0; i < count; i++) {
                    spawnConfetti();
                }
                handler.postDelayed(this, 80 + random.nextInt(120));
            }
        };
        handler.post(confettiSpawner);

        // --- Firework spawner ---
        Runnable fireworkSpawner = new Runnable() {
            @Override
            public void run() {
                if (!isAnimating) return;
                int count = 1 + random.nextInt(2);
                for (int i = 0; i < count; i++) {
                    spawnFirework();
                }
                long delay = 500 + random.nextInt(500);
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(fireworkSpawner, 200);
    }

    /**
     * Creates two promotional banners matching the reference design:
     *  1. Small badge in the top-right corner — yellow/gold bg, dark green text, no border.
     *  2. Large centered call-to-action — green bg, white text, no border.
     */
    private void showBanners() {
        int yellowBg   = Color.parseColor("#FFC107"); // amber/gold — top-right badge
        int darkGreen  = Color.parseColor("#1B5E20"); // dark green text — top-right badge
        int greenBg    = Color.parseColor("#2E7D32"); // green — center banner
        int white      = Color.WHITE;
        int cornerDp   = 14;

        // ── Banner 1 · Top-right badge ─────────────────────────────────────────
        // "MEGA-SENA" smaller; "30 anos" bold + larger — via SpannableString
        String line1 = "MEGA-SENA";
        String line2 = "30 anos";
        String fullText = line1 + "\n" + line2;
        SpannableString span = new SpannableString(fullText);

        // "MEGA-SENA" → 14sp, bold
        span.setSpan(new AbsoluteSizeSpan(14, true), 0, line1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), 0, line1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // "30 anos" → 22sp, bold
        int start2 = line1.length() + 1; // +1 for '\n'
        span.setSpan(new AbsoluteSizeSpan(22, true), start2, fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), start2, fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        bannerTopRight = new TextView(context);
        bannerTopRight.setText(span);
        bannerTopRight.setGravity(Gravity.CENTER);
        bannerTopRight.setTextColor(darkGreen);
        bannerTopRight.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        bannerTopRight.setBackground(
                createRoundedDrawable(yellowBg, dpToPx(cornerDp)));

        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.END);
        p1.topMargin   = dpToPx(48);  // descido em relação ao topo
        p1.rightMargin = dpToPx(12);
        bannerTopRight.setLayoutParams(p1);
        bannerOverlay.addView(bannerTopRight);  // added to overlay → always on top

        // ── Banner 2 · Center call-to-action ───────────────────────────────────
        // We use a specific translucent color for the background so balloons can be seen passing behind.
        int translucentGreenBg = Color.parseColor("#D82E7D32"); // ~85% opaque green

        bannerCenter = new TextView(context);
        bannerCenter.setText("Aposte agora na Mega-\nSena 30 Anos!!");
        bannerCenter.setGravity(Gravity.CENTER);
        bannerCenter.setTextColor(white);
        bannerCenter.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 22);
        bannerCenter.setTypeface(null, Typeface.BOLD);
        bannerCenter.setPadding(dpToPx(24), dpToPx(18), dpToPx(24), dpToPx(18));
        // Note: background is applied to the shadowWrapper instead

        // Wrapper carries the elevation for an authentic Android shadow
        FrameLayout shadowWrapper = new FrameLayout(context);
        shadowWrapper.setClipChildren(false);
        shadowWrapper.setElevation(dpToPx(14));
        shadowWrapper.setOutlineProvider(android.view.ViewOutlineProvider.BOUNDS);

        GradientDrawable wrapperBg = new GradientDrawable();
        wrapperBg.setShape(GradientDrawable.RECTANGLE);
        wrapperBg.setColor(translucentGreenBg);
        wrapperBg.setCornerRadius(0);
        shadowWrapper.setBackground(wrapperBg);
        shadowWrapper.addView(bannerCenter);

        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        shadowWrapper.setLayoutParams(p2);
        bannerOverlay.addView(shadowWrapper);

        // ── Synchronized pulse on BOTH banners ─────────────────────────────────
        ObjectAnimator cx = ObjectAnimator.ofFloat(shadowWrapper,  "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator cy = ObjectAnimator.ofFloat(shadowWrapper,  "scaleY", 1f, 1.05f, 1f);
        ObjectAnimator tx = ObjectAnimator.ofFloat(bannerTopRight, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator ty = ObjectAnimator.ofFloat(bannerTopRight, "scaleY", 1f, 1.05f, 1f);
        for (ObjectAnimator a : new ObjectAnimator[]{cx, cy, tx, ty}) {
            a.setDuration(1200);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            a.setRepeatCount(ValueAnimator.INFINITE);
        }
        AnimatorSet pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(cx, cy, tx, ty);
        pulseAnim.start();
    }

    /**
     * Builds a {@link android.graphics.drawable.GradientDrawable} with a solid fill
     * and rounded corners (no border/stroke).
     */
    private android.graphics.drawable.GradientDrawable createRoundedDrawable(
            int fillColor, int cornerRadiusPx) {
        android.graphics.drawable.GradientDrawable gd =
                new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setColor(fillColor);
        gd.setCornerRadius(cornerRadiusPx);
        return gd;
    }

    public void stop() {
        isAnimating = false;
        currentBalloonCount = 0;
        handler.removeCallbacksAndMessages(null);

        // Clear banner overlay
        if (bannerTopRight != null) {
            bannerOverlay.removeView(bannerTopRight);
            bannerTopRight = null;
        }
        if (bannerCenter != null) {
            bannerOverlay.removeView(bannerCenter);
            bannerCenter = null;
        }
        bannerOverlay.removeAllViews();
        ViewGroup bannerParent = (ViewGroup) bannerOverlay.getParent();
        if (bannerParent != null) bannerParent.removeView(bannerOverlay);

        // Clear animation container
        animationContainer.removeAllViews();
        ViewGroup parent = (ViewGroup) animationContainer.getParent();
        if (parent != null) parent.removeView(animationContainer);

        // Parar e limpar o som
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    /**
     * Spawn a 3D-realistic balloon using BalloonView (Canvas + RadialGradient).
     * Each balloon has a unique color, size variation, rise speed, sway and wobble.
     */
    private void spawnBalloon() {
        if (currentBalloonCount >= MAX_BALLOONS) return;
        currentBalloonCount++;

        int containerWidth = animationContainer.getWidth();
        int containerHeight = animationContainer.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;

        BalloonView balloon = new BalloonView(context);

        int color = balloonColors[random.nextInt(balloonColors.length)];
        balloon.setBalloonColor(color);

        // Width 55–95 dp. Height 2.3x: 50% body + 50% long curved string space
        int widthPx  = dpToPx(55 + random.nextInt(40));
        int heightPx = (int) (widthPx * 2.3f);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthPx, heightPx);
        balloon.setLayoutParams(params);

        // Start below the bottom edge at a random horizontal position
        float startX = random.nextFloat() * Math.max(1, containerWidth - widthPx);
        float startY = containerHeight + heightPx;
        balloon.setX(startX);
        balloon.setY(startY);
        balloon.setAlpha(0.90f + random.nextFloat() * 0.10f); // slight alpha variation

        animationContainer.addView(balloon);

        long duration = 7000 + random.nextInt(4000); // slow, leisurely rise

        // Rise animation (Y): bottom → above screen
        ObjectAnimator riseAnim = ObjectAnimator.ofFloat(
                balloon, "translationY", startY, -heightPx * 2f);
        riseAnim.setDuration(duration);
        riseAnim.setInterpolator(new LinearInterpolator());

        // Sway (X): oscillate left/right like a balloon drifting in air
        float swayPx = dpToPx(18 + random.nextInt(28));
        float swayDir = random.nextBoolean() ? swayPx : -swayPx;
        ObjectAnimator swayAnim = ObjectAnimator.ofFloat(
                balloon, "translationX",
                startX, startX + swayDir, startX, startX - swayDir / 2f, startX);
        swayAnim.setDuration(2000 + random.nextInt(1500));
        swayAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        swayAnim.setRepeatCount(ValueAnimator.INFINITE);
        swayAnim.setRepeatMode(ValueAnimator.REVERSE);

        // Wobble (rotation Z): gentle tilt while floating
        float wobbleDeg = 5f + random.nextFloat() * 10f;
        ObjectAnimator wobbleAnim = ObjectAnimator.ofFloat(
                balloon, "rotation",
                0f, wobbleDeg, 0f, -wobbleDeg, 0f);
        wobbleAnim.setDuration(1600 + random.nextInt(1200));
        wobbleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        wobbleAnim.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(riseAnim, swayAnim, wobbleAnim);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationContainer.removeView(balloon);
                currentBalloonCount = Math.max(0, currentBalloonCount - 1);
            }
        });

        set.start();
    }

    /**
     * Spawn a paper-strip confetti piece that falls while flexing.
     */
    private void spawnConfetti() {
        int containerWidth = animationContainer.getWidth();
        int containerHeight = animationContainer.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;

        ImageView confetti = new ImageView(context);
        confetti.setImageResource(R.drawable.ic_confetti_rect);

        int color = confettiColors[random.nextInt(confettiColors.length)];
        confetti.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        // Random size: elongated rectangle like a paper strip
        int w = dpToPx(18 + random.nextInt(22));
        int h = dpToPx(6 + random.nextInt(6));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        confetti.setLayoutParams(params);

        float startX = random.nextFloat() * containerWidth;
        confetti.setX(startX);
        confetti.setY(-h);
        confetti.setRotation(random.nextFloat() * 360f);

        animationContainer.addView(confetti);

        long duration = 2200 + random.nextInt(2000);

        // Fall from top to bottom
        ObjectAnimator fallAnim = ObjectAnimator.ofFloat(
                confetti, "translationY", -h, containerHeight + h);
        fallAnim.setDuration(duration);
        fallAnim.setInterpolator(new LinearInterpolator());

        // Horizontal drift (paper drifts sideways while falling)
        float driftEnd = startX + (random.nextBoolean() ? 1 : -1) * dpToPx(30 + random.nextInt(60));
        ObjectAnimator driftAnim = ObjectAnimator.ofFloat(
                confetti, "translationX", startX, driftEnd, startX);
        driftAnim.setDuration(duration);
        driftAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        driftAnim.setRepeatCount(ValueAnimator.INFINITE);
        driftAnim.setRepeatMode(ValueAnimator.REVERSE);

        // Rotation Z (spin around like paper tumbling)
        float startRotation = random.nextFloat() * 360f;
        float endRotation = startRotation + (random.nextBoolean() ? 360f : -360f);
        ObjectAnimator rotateZ = ObjectAnimator.ofFloat(confetti, "rotation", startRotation, endRotation);
        rotateZ.setDuration(900 + random.nextInt(700));
        rotateZ.setInterpolator(new LinearInterpolator());
        rotateZ.setRepeatCount(ValueAnimator.INFINITE);

        // scaleX: 1 → 0 → -1 → 0 → 1, simulates paper flex/fold
        ObjectAnimator flexAnim = ObjectAnimator.ofFloat(confetti, "scaleX", 1f, 0.1f, -1f, 0.1f, 1f);
        flexAnim.setDuration(500 + random.nextInt(400));
        flexAnim.setInterpolator(new LinearInterpolator());
        flexAnim.setRepeatCount(ValueAnimator.INFINITE);

        // Optional: slight scaleY breathe to simulate the soft paper curve
        ObjectAnimator breatheAnim = ObjectAnimator.ofFloat(confetti, "scaleY", 1f, 0.7f, 1f);
        breatheAnim.setDuration(flexAnim.getDuration());
        breatheAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        breatheAnim.setRepeatCount(ValueAnimator.INFINITE);
        breatheAnim.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fallAnim, driftAnim, rotateZ, flexAnim, breatheAnim);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationContainer.removeView(confetti);
            }
        });

        set.start();
    }

    /**
     * Spawn a firework explosion with a central flash and expanding particles.
     */
    private void spawnFirework() {
        int containerWidth = animationContainer.getWidth();
        int containerHeight = animationContainer.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;

        // Random center for the explosion, mostly in the upper/mid area
        float centerX = dpToPx(30) + random.nextFloat() * (containerWidth - dpToPx(60));
        float centerY = dpToPx(50) + random.nextFloat() * (containerHeight * 0.7f);

        // Can be a single colored firework or multi-colored
        boolean multiColor = random.nextBoolean();
        int mainColor = confettiColors[random.nextInt(confettiColors.length)];

        // MUCH more voluminous
        int particleCount = 45 + random.nextInt(35); // 45 to 80 particles
        float explosionRadius = dpToPx(100 + random.nextInt(80)); // 100px to 180px radius

        // 1. Ambient colored glow for "mais luz"
        ImageView ambientGlow = new ImageView(context);
        android.graphics.drawable.GradientDrawable glowCircle = new android.graphics.drawable.GradientDrawable();
        glowCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        glowCircle.setColor(mainColor);
        ambientGlow.setImageDrawable(glowCircle);
        ambientGlow.setAlpha(0.6f); // soft haze

        int ambientSize = dpToPx(100 + random.nextInt(40));
        FrameLayout.LayoutParams ambientParams = new FrameLayout.LayoutParams(ambientSize, ambientSize);
        ambientGlow.setLayoutParams(ambientParams);
        ambientGlow.setX(centerX - ambientSize / 2f);
        ambientGlow.setY(centerY - ambientSize / 2f);
        animationContainer.addView(ambientGlow);

        ObjectAnimator ambientFade = ObjectAnimator.ofFloat(ambientGlow, "alpha", 0.6f, 0f);
        ObjectAnimator ambientScaleX = ObjectAnimator.ofFloat(ambientGlow, "scaleX", 0.5f, 2.5f);
        ObjectAnimator ambientScaleY = ObjectAnimator.ofFloat(ambientGlow, "scaleY", 0.5f, 2.5f);
        AnimatorSet ambientSet = new AnimatorSet();
        ambientSet.playTogether(ambientFade, ambientScaleX, ambientScaleY);
        ambientSet.setDuration(900);
        ambientSet.setInterpolator(new android.view.animation.DecelerateInterpolator());
        ambientSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationContainer.removeView(ambientGlow);
            }
        });
        ambientSet.start();

        // 2. Central white flash (Bigger, more intense)
        ImageView flash = new ImageView(context);
        android.graphics.drawable.GradientDrawable flashCircle = new android.graphics.drawable.GradientDrawable();
        flashCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        flashCircle.setColor(Color.WHITE);
        flash.setImageDrawable(flashCircle);
        int flashSize = dpToPx(35 + random.nextInt(25)); // Bigger flash
        FrameLayout.LayoutParams flashParams = new FrameLayout.LayoutParams(flashSize, flashSize);
        flash.setLayoutParams(flashParams);
        flash.setX(centerX - flashSize / 2f);
        flash.setY(centerY - flashSize / 2f);
        flash.setElevation(dpToPx(5));
        animationContainer.addView(flash);

        ObjectAnimator flashFade = ObjectAnimator.ofFloat(flash, "alpha", 1f, 0f);
        ObjectAnimator flashScaleX = ObjectAnimator.ofFloat(flash, "scaleX", 0f, 2f);
        ObjectAnimator flashScaleY = ObjectAnimator.ofFloat(flash, "scaleY", 0f, 2f);

        AnimatorSet flashSet = new AnimatorSet();
        flashSet.playTogether(flashFade, flashScaleX, flashScaleY);
        flashSet.setDuration(450);
        flashSet.setInterpolator(new android.view.animation.DecelerateInterpolator());
        flashSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationContainer.removeView(flash);
            }
        });
        flashSet.start();

        // 3. Exploding particles (More particles, larger spread)
        for (int i = 0; i < particleCount; i++) {
            ImageView particle = new ImageView(context);

            android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
            circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);

            int color = multiColor ? confettiColors[random.nextInt(confettiColors.length)] : mainColor;

            // Add bright sparks for "mais luz"
            float randGlow = random.nextFloat();
            if (randGlow < 0.15f) {
                color = Color.WHITE;
            } else if (randGlow < 0.25f) {
                color = Color.parseColor("#FFF59D"); // very light yellow/amber spark
            }
            circle.setColor(color);
            particle.setImageDrawable(circle);

            // Larger particles for volume
            int size = dpToPx(5 + random.nextInt(7));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            particle.setLayoutParams(params);

            particle.setX(centerX - size / 2f);
            particle.setY(centerY - size / 2f);

            animationContainer.addView(particle);

            // Random spread inside the volume
            double angle = Math.toRadians((360.0 / particleCount) * i + random.nextDouble() * 15);
            float innerSpread = 0.3f + random.nextFloat() * 0.7f; // particles spread throughout the radius, not just edge
            float distance = explosionRadius * innerSpread;

            float endX = centerX + (float) (Math.cos(angle) * distance) - size / 2f;
            float endY = centerY + (float) (Math.sin(angle) * distance) - size / 2f;

            long duration = 800 + random.nextInt(400);

            ObjectAnimator moveX = ObjectAnimator.ofFloat(particle, "x", particle.getX(), endX);
            ObjectAnimator moveY = ObjectAnimator.ofFloat(particle, "y", particle.getY(), endY);

            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(particle, "alpha", 1f, 0f);
            fadeOut.setDuration(duration / 2);
            fadeOut.setStartDelay(duration / 2);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(particle, "scaleX", 1f, 0.2f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(particle, "scaleY", 1f, 0.2f);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(moveX, moveY, fadeOut, scaleDownX, scaleDownY);
            set.setInterpolator(new android.view.animation.DecelerateInterpolator(1.2f));
            set.setDuration(duration);

            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animationContainer.removeView(particle);
                }
            });
            set.start();
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
