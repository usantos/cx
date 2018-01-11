package caixa.gov.br.loterias.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import caixa.gov.br.loterias.R;

/**
 * Created by lsimas on 05/04/2017.
 */

public class ViewUtils {

    private static Toast toast;
    private static ProgressDialog progress;

    private static Typeface fontFutura = null;
    private static Typeface fontFuturaBold = null;
    private static Typeface fontAriallMT = null;

    public static Typeface getFontFutura(Context context) {
        if (fontFutura == null) {
            fontFutura = Typeface.createFromAsset(context.getAssets(), "fonts/futura_std_book.otf");
        }

        return fontFutura;
    }


    public static Typeface getFontFuturaBold(Context context) {
        if (fontFuturaBold == null) {
            fontFuturaBold = Typeface.createFromAsset(context.getAssets(), "fonts/futura_std_bold.otf");
        }

        return fontFuturaBold;
    }

    public static Typeface getFontArialMT(Context context) {
        if (fontAriallMT == null) {
            fontAriallMT = Typeface.createFromAsset(context.getAssets(), "fonts/arialmt.ttf");
        }

        return fontAriallMT;
    }


    private static int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());

        return px;
    }

    public static void configuraStatusBarGradientLayout(Context context, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable background = context.getResources().getDrawable(R.drawable.navigation_gradient);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ColorUtils.setAlphaComponent(Color.BLACK, 77));
            window.setBackgroundDrawable(background);
        }
    }

    public static SpannableString configuraButtonText(Context context, String texto, int posicaoTrocaStyle) {
        SpannableString spannableString = new SpannableString(texto);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, posicaoTrocaStyle, 0);
        spannableString.setSpan(new AbsoluteSizeSpan(convertSpToPixels(17.7f, context)), 0, posicaoTrocaStyle, 0);

        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), posicaoTrocaStyle + 1, spannableString.length(), 0);
        spannableString.setSpan(new AbsoluteSizeSpan(convertSpToPixels(14.2f, context)), posicaoTrocaStyle + 1, spannableString.length(), 0);

        return spannableString;
    }

    public static void abrirUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void abrirSiteInternetBanking(Context context) {
        ViewUtils.abrirUrl(context, "http://www.caixa.gov.br");
    }

    public static String capitalized(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static void closeKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void toast(Context context, String text, int length) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, length);
        toast.show();
    }

    public static void toast(Context context, int resString, int length) {
        ViewUtils.toast(context, context.getString(resString), length);
    }

    public static AlertDialog.Builder dialogo(Context context, String mensagem) {
        return new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(mensagem);
    }

    public static AlertDialog.Builder dialogo(Context context, int titulo, String mensagem) {
        return new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(titulo)
                .setMessage(mensagem);
    }

    public static void alert(Context context, String mensagem) {
        alert(context, mensagem, null);
    }

    public static void alertTitulo(Context context, int titulo, String mensagem) {
        alertTitulo(context, titulo, mensagem, null);
    }

    public static void alertTitleButton(Context context, int title, String message, String buttonName) {
        alertTitleButton(context, title, message, buttonName, null);
    }

    public static void alert(Context context, String mensagem, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, mensagem)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
        TextView textView = ((TextView) dialog.findViewById(android.R.id.message));
        textView.setTypeface(getFontFutura(context));
    }

    public static void alertTitulo(Context context, int titulo, String mensagem, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, titulo, mensagem)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        DialogTitle textTitleView = ((DialogTitle) dialog.findViewById(R.id.alertTitle));
        TextView textMessageView = ((TextView) dialog.findViewById(android.R.id.message));
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
    }

    public static void alertTitleButton(Context context, int title, String message, String buttonName, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, title, message)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        DialogTitle textTitleView = ((DialogTitle) dialog.findViewById(R.id.alertTitle));
        TextView textMessageView = ((TextView) dialog.findViewById(android.R.id.message));
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
    }

    public static void loading(Context context) {
        ViewUtils.dismissLoading();
        progress = ProgressDialog.show(
                new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_Dialog),
                "", "Carregando...", true);
    }

    public static void dismissLoading() {
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    public static void cancelAnimation(View view) {
        Animation a = view.getAnimation();
        if (a != null) {
            a.cancel();
            a.setAnimationListener(null);
        }
        view.clearAnimation();
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }

    public static void flipTop(final View view, final AnimationEndListener listenerAdapter) {
        view.animate().rotationX(90).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setRotationX(-90);
                view.animate().rotationX(0).setDuration(200).setListener(null);
                if (listenerAdapter != null) {
                    listenerAdapter.onAnimationEnd();
                }
            }
        });
    }

    public static void flipBottom(final View view, final AnimationEndListener listenerAdapter) {
        view.animate().rotationX(-90).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setRotationX(90);
                view.animate().rotationX(0).setDuration(200).setListener(null);
                if (listenerAdapter != null) {
                    listenerAdapter.onAnimationEnd();
                }
            }
        });
    }

    public static void fadeOut(final View view, final int finalVisibility) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(finalVisibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }

    public static void fadeIn(final View view, final int finalVisibility) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(finalVisibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }

    public interface KeyboardListener {
        void abriu();

        void fechou();
    }

    public static void removeKeyboardListener(final Activity activity, final ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }

        final View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }

    public static ViewTreeObserver.OnGlobalLayoutListener setKeyboardListener(final Activity activity, final KeyboardListener listener) {

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        final float density = dm.density;

        final View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content).getRootView();

        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            int previousHeightDiff = 0;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                rootView.getWindowVisibleDisplayFrame(r);

                // cache properties for later use
                int rootViewHeight = rootView.getRootView().getHeight();
                int resultBottom = r.bottom;

                // calculate screen height differently for android versions >= 21: Lollipop 5.x, Marshmallow 6.x
                //http://stackoverflow.com/a/29257533/3642890 beware of nexus 5
                int screenHeight;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    screenHeight = size.y;
                } else {
                    screenHeight = rootViewHeight;
                }

                int heightDiff = screenHeight - resultBottom;

                int pixelHeightDiff = (int) (heightDiff / density);
                if (pixelHeightDiff > 100 && pixelHeightDiff != previousHeightDiff) { // if more than 100 pixels, its probably a keyboard...
                    listener.abriu();
                } else if (pixelHeightDiff != previousHeightDiff && (previousHeightDiff - pixelHeightDiff) > 100) {
                    listener.fechou();
                }
                previousHeightDiff = pixelHeightDiff;
            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        return layoutListener;
    }

    /**
     * Used to scroll to the given view.
     *
     * @param scrollViewParent Parent ScrollView
     * @param view             View to which we need to scroll.
     */
    public static void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);

        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumalated Offset.
     */
    private static void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        if (!(parent instanceof ViewGroup)) {
            return;
        }

        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    public static DecimalFormat getMoedaFormatter() {
        DecimalFormatSymbols sym = new DecimalFormatSymbols();
        sym.setDecimalSeparator(',');
        sym.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("###,###.##", sym);
        formatter.setMinimumFractionDigits(2);
        formatter.setDecimalSeparatorAlwaysShown(true);
        return formatter;
    }

    public static String getMoedaFormat(BigDecimal valor) {
        Locale ptBrLocale = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBrLocale);
        return nf.format(valor);
    }

    public static int monthDiff(Calendar startCalendar, Calendar endCalendar) {
        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        return diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    public static boolean compareCalendarsMonths(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean compareCalendarDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * Para ativar as fontes bold com o Calligraphy
     *
     * @param textViews lista de textviews
     */
    public static void setBold(TextView... textViews) {
        if (textViews == null || textViews.length == 0) {
            return;
        }

        for (TextView tv : textViews) {
            if (tv == null) {
                continue;
            }

            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        }
    }

    public static void setFontFuturaBold(Context context, TextView... textViews) {
        if (textViews == null || textViews.length == 0) {
            return;
        }

        Typeface typeface = ViewUtils.getFontFuturaBold(context);

        for (TextView tv : textViews) {
            if (tv == null) {
                continue;
            }

            tv.setTypeface(typeface);
        }
    }


    // Metodos relacionados a caso o conteudo da tela seja inferior o ao height do dispositivo, mantenha a tab no bottom da tela

    public static void organizaTabLayout(final Activity activity, int idMainLayout, final int idViewToExpand, final int idScrollView) {
        final ConstraintLayout constraintLayout = activity.findViewById(idMainLayout);
        ViewTreeObserver viewTreeObserver = constraintLayout.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int heightWindow = displayMetrics.heightPixels;

                    if (constraintLayout.getMeasuredHeight() > 0) {
                        Resources resources = activity.getResources();
                        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                        int heightNavigationBar = resources.getDimensionPixelSize(resourceId);

                        int heightLayout = constraintLayout.getMeasuredHeight();
                        int heightElementToExpand = activity.findViewById(idViewToExpand).getMeasuredHeight();
                        float heightStatusBar = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, resources.getDisplayMetrics());

                        if (!((ScrollView) activity.findViewById(idScrollView)).canScrollVertically(1)) {
                            ConstraintSet set = new ConstraintSet();

                            set.clone(constraintLayout);
                            set.constrainHeight(idViewToExpand, (heightElementToExpand + heightWindow - (int) heightStatusBar - heightLayout - heightNavigationBar - 14));
                            set.applyTo(constraintLayout);
                        }
                    }
                }
            });
        }
    }


    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }

        return size;
    }


    /**
     * O texto que estiver dentro de _ receberá fonte FuturaBold, ex: Texto Normal _Texto Bold_
     * @param context
     * @param texto
     * @return
     */
    public static SpannableStringBuilder textFuturaAndFuturaBold(Context context, String texto) {
        Typeface font = ViewUtils.getFontFuturaBold(context);

        PositionStringBold positionStringBold = positionString(texto);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto.replace("_", ""));
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        return spannableStringBuilder;
    }

    private static PositionStringBold positionString(String texto){
        String[] listaString = texto.split("_");
        int start = listaString[0].length();
        int end = listaString[1].length() + start;
        return new PositionStringBold(start, end);
    }

    public static int setDisplayMetric(int valor, Context context) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valor, context.getResources().getDisplayMetrics());
    }

}
