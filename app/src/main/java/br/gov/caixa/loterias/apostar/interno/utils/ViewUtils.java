package br.gov.caixa.loterias.apostar.interno.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PremioDTO;

/**
 * Created by lsimas on 05/04/2017.
 */

public class ViewUtils {

    private static Toast toast;

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

    public static AlertDialog.Builder dialogo(Context context, String mensagem) {
        return new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setMessage(mensagem);
    }

    public static AlertDialog.Builder dialogo(Context context, int titulo, String mensagem) {
        return new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(titulo)
                .setMessage(mensagem);
    }

    public static AlertDialog.Builder dialogo(Context context, String titulo, String mensagem) {
        return new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(titulo)
                .setMessage(mensagem);
    }

    public static Dialog alert(Context context, String mensagem) {
        return alert(context, mensagem, null);
    }

    public static Dialog alertTitulo(Context context, int titulo, String mensagem) {
        return alertTitulo(context, titulo, mensagem, null);
    }

    public static Dialog alertTitleButton(Context context, int title, String message, String buttonName) {
        return alertTitleButton(context, title, message, buttonName, null);
    }

    public static Dialog alert(Context context, String mensagem, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, mensagem)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss()).show();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTypeface(getFontFutura(context));
        return dialog;
    }

    public static Dialog alertEditText(Context context){
        final EditText editText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Titulo")
                .setMessage("Message next?")
                .setView(editText)
                .setPositiveButton("OK", (dialog1, which) -> {
                    String task = String.valueOf(editText.getText());

                })
                .setNegativeButton("Cancel", null)
                .create();
        return dialog;

    }

    public static Dialog alertTitulo(Context context, int titulo, String mensagem, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, titulo, mensagem)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss()).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
        return dialog;
    }

    public static Dialog alertTitleButton(Context context, int title, String message, String buttonName, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, title, message)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton(buttonName, (dialog1, which) -> dialog1.dismiss()).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
        return dialog;
    }

    public static Dialog alertTitleButtonPositiveListener(Context context, int title, String message, int buttonName, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, title, message)
                .setCancelable(true)
                .setNegativeButton(R.string.cancelar, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(buttonName, listener).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
        return dialog;
    }

    public static Dialog alertTitleButtonYesOrNotListener(Context context, String message, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                .setCancelable(false)
                .setNegativeButton (R.string.label_nao, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(R.string.label_sim, listener)
                .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
        return dialog;
    }

    public static Dialog alertTitleCustomPositiveListener(Context context,String negativeTitle, String positiveTitle, String message, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                .setCancelable(false)
                .setNegativeButton (negativeTitle, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(positiveTitle, listener)
                .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
        return dialog;
    }

    public static Dialog alertTitleCustomPositiveListener(Context context,String title,String negativeButton, String positiveButton, String message, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, title, message)
                                 .setCancelable(false)
                                 .setNegativeButton (negativeButton, (dialogInterface, i) -> dialogInterface.dismiss())
                                 .setPositiveButton(positiveButton, listener)
                                 .create();

        /*DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));*/
        return dialog;
    }

    public static Dialog alertVerificarIdadeListener(Context context, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, context.getResources().getString(R.string.MA003))
                .setCancelable(false)
                .setNegativeButton(R.string.label_nao, listener)
                .setPositiveButton(R.string.label_sim, (dialogInterface, i) -> dialogInterface.dismiss()).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontFuturaBold(context));
        textMessageView.setTypeface(getFontFutura(context));
        return dialog;
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

    public static String getMoedaFormat(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        Locale ptBrLocale = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBrLocale);
        return nf.format(valor).replace("R$", "R$");
    }

    public static void setMoedaFormatHtml(BigDecimal valor, TextView valorTextView) {
        String valorFormatoMoeda = getMoedaFormat(valor);

        if (Build.VERSION.SDK_INT < 28) {
            if (!valorFormatoMoeda.contains(" ")) {
                valorFormatoMoeda = new StringBuffer(valorFormatoMoeda).insert(2, " ").toString();
            }
        }

        String[] valorFormatoMoedaArray = valorFormatoMoeda.split("\\s");
        valorTextView.setText(fromHtml(valorFormatoMoedaArray[0] + "<strong> " + valorFormatoMoedaArray[1] + "</strong>"));
    }

    public static void setMoedaFormatHtml(BigDecimal valor, TextView valorTextView, String complemento) {
        String valorFormatoMoeda = getMoedaFormat(valor);

        if (Build.VERSION.SDK_INT < 28) {
            if (!valorFormatoMoeda.contains(" ")) {
                valorFormatoMoeda = new StringBuffer(valorFormatoMoeda).insert(2, " ").toString();
            }
        }

        String[] valorFormatoMoedaArray = valorFormatoMoeda.split("\\s");
        valorTextView.setText(fromHtml(complemento + valorFormatoMoedaArray[0] + "<strong> " + valorFormatoMoedaArray[1] + "</strong>"));
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

                        if (!activity.findViewById(idScrollView).canScrollVertically(1)) {
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

    // Metodos relacionados a caso o conteudo da tela seja inferior o ao height do dispositivo, mantenha a tab no bottom da tela

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
     *
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

    public static SpannableStringBuilder textColor(Context context, String texto,int idCor) {
        Typeface font = ViewUtils.getFontFuturaBold(context);

        PositionStringBold positionStringBold = positionString(texto);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto.replace("_", ""));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(idCor)) , positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        return spannableStringBuilder;
    }

    public static SpannableStringBuilder textFuturaAndFuturaBoldTitle(Context context, String texto) {
        SpannableStringBuilder spannableStringBuilder = textFuturaAndFuturaBold(context, texto);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(16, true), 0, texto.replace("_", "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    public static SpannableStringBuilder textFuturaAndSizeTitle(Context context, String texto) {
        Typeface font = ViewUtils.getFontFutura(context);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), 0, texto.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(16, true), 0, texto.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    private static PositionStringBold positionString(String texto) {
        String[] listaString = texto.split("_");
        int start = listaString[0].length();
        int end = listaString[1].length() + start;
        return new PositionStringBold(start, end);
    }

    public static int setDisplayMetric(int valor, Context context) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valor, context.getResources().getDisplayMetrics());
    }

    /**
     * Método que retorna data e hora no formato dd/MM/yyyy - HHhMM
     *
     * @param date String
     * @param hour String
     * @return String
     */
    public static String getDateAndHour(String date, String hour) {

        String[] hHMMSS = hour.split(":");

        return String.format("%s - %sh%s", date, hHMMSS[0], hHMMSS[1]);
    }

    /**
     * Método que retorna data e hora no formato dd/MM | HHhMM
     *
     * @param dateHour String
     * @return String
     */
    public static String getDateAndHour(String dateHour) {

        String[] dateHours = dateHour.split(" ");
        String[] hHMMSS = dateHours[1].split(":");

        Date date = DateUtils.stringToDate(dateHours[0], DateUtils.PATTERN_DDMM_YYYY);

        return String.format("%s | %sh%s", DateUtils.getDateToString(date, DateUtils.PATTERN_DD_MM), hHMMSS[0], hHMMSS[1]);
    }

    public static String getNumberIntegerToString(Integer number) {

        if (number < 10) {
            return String.format(Locale.getDefault(), "0%d", number);
        } else {
            return String.format(Locale.getDefault(), "%d", number);
        }
    }

    public static void setColorDrawable(Context context, Drawable drawable, int color) {
        drawable.mutate();
        drawable.setColorFilter(ContextCompat.getColor(context, color),
                PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Método que retorna uma lista de Faixa de Premiação por concurso
     *
     * @param premioDTO PremioDTO
     * @return List<FaixaPremiadaDTO>
     */
    public static List<FaixaPremiadaDTO> getFaixaPremiacaoDTO(PremioDTO premioDTO) {
        List<FaixaPremiadaDTO> faixaPremiadaDTOList = new ArrayList<>();

        if (premioDTO != null && premioDTO.getConcursos() != null) {
            for (ConcursoPremiadoDTO concurso : premioDTO.getConcursos()) {

                if (concurso.getConcurso() != null && concurso.getFaixas() != null) {
                    for (FaixaPremiadaDTO faixa : concurso.getFaixas()) {
                        faixa.setId(Long.parseLong(concurso.getConcurso().getNumero().toString()));
                        faixaPremiadaDTOList.add(faixa);
                    }
                }
            }
        }
        return faixaPremiadaDTOList;
    }

    /**
     * Método reponsável por retornar ParametroSimulacao da SessaoUsuario de uma modalidade
     *
     * @param sessaoUsuario  SessaoUsuario
     * @param modalidadeEnum ModalidadeEnum
     * @return ParametroSimulacao
     */
    public static ParametroSimulacao getParametroSimulacao(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum) {
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null) {
            for (ParametroSimulacao parametroSimulacao : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (parametroSimulacao.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum) {
                    return parametroSimulacao;
                }
            }
        }
        return null;
    }

    public static ParametroSimulacao getParametroSimulacao(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum, IdentificaoDeUmaApostaDas8Modalidades aposta) {
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null) {
            for (ParametroSimulacao parametroSimulacao : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (    parametroSimulacao.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum &&
                        aposta.getConcursoAlvo().equals(parametroSimulacao.getParametroJogo().getConcurso().getNumero())) {
                    return parametroSimulacao;
                }
            }
        }
        return null;
    }

    public static String getNomeModalidadePorAposta(ApostaDTO aposta) {
        ParametroSimulacao param = getParametroSimulacao(SessaoUsuario.getInstance(),aposta.getModalidade());
        if(param != null){
            ModalidadeDTO modalidadeDetalhada = param.getParametroJogo().getConcurso().getModalidadeDetalhada();
            return aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL") ?
                    modalidadeDetalhada.getDescricaoEspecial().toLowerCase(new Locale("pt", "BR")):
                    modalidadeDetalhada.getDescricao().toLowerCase(new Locale("pt", "BR"));
        }
        return "";
    }

    public static Integer getConcursoAtualPorAposta(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        ParametroSimulacao param = getParametroSimulacao(SessaoUsuario.getInstance(),aposta.getModalidade());
        if(param != null) {
            return param.getParametroJogo().getConcurso().getNumero();
        }
        return -1;
    }

    public static Integer getConcursoAtualPorModalidade(String modalidade) {
        ParametroSimulacao param = getParametroSimulacao(SessaoUsuario.getInstance(),ModalidadeEnum.toString(modalidade));
        if(param != null) {
            return param.getParametroJogo().getConcurso().getNumero();
        }
        return -1;
    }

    public static int getSmallSizeQrCode(Context context) {

//        Point point = getAppUsableScreenSize(context);
//        int width = point.x;
//        int height = point.y;
//        int smallerDimension = width < height ? width : height;
//        smallerDimension = smallerDimension * 3 / 4;

        return ViewUtils.setDisplayMetric( 105, context );
    }

    public static String replaceNullable(String source, String target, String replacement) {
        if (source != null) {
            if (target == null) {
                Log.e("replaceNullable", "target is null");
                target = "";
            }
            if (replacement == null) {
                Log.e("replaceNullable", "replacement is null");
                replacement = "";
            }
            return source.replace(target, replacement);
        } else {
            Log.e("replaceNullable", "source is null");
            return "";
        }
    }

    public static byte[] drawableInByte(Context context, int drawable) {
        Drawable d = context.getResources().getDrawable(drawable);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        return bitmapdata;
    }

    public static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }

    public interface KeyboardListener {
        void abriu();

        void fechou();
    }
}
