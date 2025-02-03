package br.gov.caixa.loterias.apostas.utils;

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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.DialogTitle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursoPremiadoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.enums.FontCaixaEnum;

/**
 * Created by lsimas on 05/04/2017.
 */

public class ViewUtils {

    private static Typeface fontFuturaBold = null;
    private static Typeface fontCaixaStdBold = null;
    private static Typeface fontCaixaStdRegular = null;


    public static Typeface getFontFuturaBold(Context context) {
        if (fontFuturaBold == null) {
            fontFuturaBold = Typeface.createFromAsset(context.getAssets(), "fonts/futura_std_bold.otf");
        }
        return fontFuturaBold;
    }

    public static Typeface getFontCaixaStdBold(Context context) {
        if (fontCaixaStdBold == null) {
            fontCaixaStdBold = Typeface.createFromAsset(context.getAssets(), "fonts/caixa_std_bold.ttf");
        }
        return fontCaixaStdBold;
    }

    public static Typeface getFontCaixaStdRegular(Context context) {
        if (fontCaixaStdRegular == null) {
            fontCaixaStdRegular = Typeface.createFromAsset(context.getAssets(), "fonts/caixa_std_regular.ttf");
        }
        return fontCaixaStdRegular;
    }

    public static void configuraStatusBarGradientLayout(Context context, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable background = context.getResources().getDrawable(R.drawable.navigation_gradient);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ColorUtils.setAlphaComponent(Color.BLACK, 77));
            window.setBackgroundDrawable(background);
        }
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


    public static AlertDialog.Builder dialogo(Context context, String mensagem) {

        TextView messageView = new TextView(context);
        messageView.setText(mensagem);
        messageView.setTypeface(getFontCaixaStdRegular(context));
        messageView.setPadding(50,50,50,50);

        return new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                //.setMessage(mensagem);
                .setView(messageView);

    }

    public static AlertDialog.Builder dialogo(Context context, int titulo, String mensagem) {

        TextView messageView = new TextView(context);
        messageView.setText(mensagem);
        messageView.setTypeface(getFontCaixaStdRegular(context));
        messageView.setPadding(50,50,50,50);

        return new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(titulo)
                //.setMessage(mensagem);
                .setView(messageView);
    }

    public static AlertDialog.Builder dialogo(Context context, String titulo, String mensagem) {

        TextView messageView = new TextView(context);
        messageView.setText(mensagem);
        messageView.setTypeface(getFontCaixaStdRegular(context));
        messageView.setPadding(50,50,50,50);

        return new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setTitle(titulo)
                //.setMessage(mensagem);
                .setView(messageView);
    }

    public static Dialog alert(Context context, String mensagem) {
        return alert(context, mensagem, null);
    }

    public static Dialog alertTitulo(Context context, int titulo, String mensagem) {
        return alertTitulo(context, titulo, mensagem, null);
    }

    public static void alertTitleButton(Context context, int title, String message, String buttonName) {
        alertTitleButton(context, title, message, buttonName, null);
    }

    public static Dialog alert(Context context, String mensagem, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, mensagem)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss()).show();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static Dialog alertTitulo(Context context, int titulo, String mensagem, DialogInterface.OnDismissListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, titulo, mensagem)
                .setCancelable(false)
                .setOnDismissListener(listener)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss()).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static void alertTitleButton(Context context, int title, String message, String buttonName, DialogInterface.OnDismissListener listener) {
        try {
            Dialog dialog = ViewUtils.dialogo(context, title, message)
                    .setCancelable(false)
                    .setOnDismissListener(listener)
                    .setPositiveButton(buttonName, (dialog1, which) -> dialog1.dismiss()).show();

            DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
            TextView textMessageView = dialog.findViewById(android.R.id.message);
            textTitleView.setTypeface(getFontCaixaStdBold(context));
            textMessageView.setTypeface(getFontCaixaStdRegular(context));
        }catch (Exception e){
            Log.d("", e.getLocalizedMessage());
        }
    }

    public static Dialog alertTitleButtonPositiveListener(Context context, int title, String message, int buttonName, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, title, message)
                .setCancelable(true)
                .setNegativeButton(R.string.cancelar, (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(buttonName, listener).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
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
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }


    public static Dialog alertTitleButtonYesOrNotListener(Context context, String message,
                                                          DialogInterface.OnClickListener listenerYes,
                                                          DialogInterface.OnClickListener  listenerNo) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                                 .setCancelable(false)
                                 .setNegativeButton (R.string.label_nao, listenerNo)
                                 .setPositiveButton(R.string.label_sim, listenerYes)
                                 .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static Dialog alertTitleButtonYesOrNotListener(Context context, String message,
                                                          String positiveButton, DialogInterface.OnClickListener listenerYes,
                                                          String negativeButton, DialogInterface.OnClickListener  listenerNo) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                                 .setCancelable(false)
                                 .setNegativeButton (negativeButton, listenerNo)
                                 .setPositiveButton(positiveButton, listenerYes)
                                 .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static Dialog alertTitleButtonYesListener(Context context, String message, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                                 .setCancelable(false)
                                 .setPositiveButton(R.string.ok, listener)
                                 .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static Dialog alertTitleButtonYesListener(Context context, String message, String btnYes, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                .setCancelable(false)
                .setPositiveButton(btnYes, listener)
                .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
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
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static Dialog alertTitleCustomPositiveListener(Context context,String negativeTitle, String positiveTitle, String message, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener  listenerNo) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, message)
                .setCancelable(false)
                .setNegativeButton (negativeTitle, listenerNo)
                .setPositiveButton(positiveTitle, listener)
                .show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
    }

    public static Dialog alertTitleCustomPositiveListener(Context context,String title,String negativeButton, String positiveButton, String message, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, title, message)
                                 .setCancelable(false)
                                 .setNegativeButton (negativeButton, (dialogInterface, i) -> dialogInterface.dismiss())
                                 .setPositiveButton(positiveButton, listener)
                                 .create();
        return dialog;
    }

    public static Dialog alertVerificarIdadeListener(Context context, DialogInterface.OnClickListener listener) {
        Dialog dialog = ViewUtils.dialogo(context, R.string.label_atencao, context.getResources().getString(R.string.MA003))
                .setCancelable(false)
                .setNegativeButton(R.string.label_nao, listener)
                .setPositiveButton(R.string.label_sim, (dialogInterface, i) -> dialogInterface.dismiss()).show();

        DialogTitle textTitleView = dialog.findViewById(R.id.alertTitle);
        TextView textMessageView = dialog.findViewById(android.R.id.message);
        textTitleView.setTypeface(getFontCaixaStdBold(context));
        textMessageView.setTypeface(getFontCaixaStdRegular(context));
        return dialog;
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

    public static void setMoedaFormatHtmlCombo(BigDecimal valor, TextView valorTextView) {
        String valorFormatoMoeda = getMoedaFormat(valor);

        if (Build.VERSION.SDK_INT < 28) {
            if (!valorFormatoMoeda.contains(" ")) {
                valorFormatoMoeda = new StringBuffer(valorFormatoMoeda).insert(2, " ").toString();
            }
        }

        String[] valorFormatoMoedaArray = valorFormatoMoeda.split("\\s");
        valorTextView.setText(fromHtml(valorFormatoMoedaArray[0] + valorFormatoMoedaArray[1]));
    }

    public static void setMoedaFormatHtml(BigDecimal valor, TextView valorTextView, String complemento) {
        String valorFormatoMoeda = getMoedaFormat(valor);

        if (Build.VERSION.SDK_INT < 28) {
            if (!valorFormatoMoeda.contains(" ")) {
                valorFormatoMoeda = new StringBuffer(valorFormatoMoeda).insert(2, " ").toString();
            }
        }

        String[] valorFormatoMoedaArray = valorFormatoMoeda.split("\\s");
        valorTextView.setText(fromHtml(complemento + "<strong> " + valorFormatoMoedaArray[0] + valorFormatoMoedaArray[1] + "</strong>"));
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
                        if(activity.findViewById(idViewToExpand) != null){
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
                }
            });
        }
    }

    // Metodos relacionados a caso o conteudo da tela seja inferior o ao height do dispositivo, mantenha a tab no bottom da tela

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
        try{
            Typeface font = ViewUtils.getFontFuturaBold(context);

            PositionStringBold positionStringBold = positionString(texto);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto.replace("_", ""));
            spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            return spannableStringBuilder;
        }catch (Exception e){
            return  new SpannableStringBuilder().append(texto);
        }
    }

    public static SpannableStringBuilder textCaixaSTDBold(Context context, String texto) {
        try{
            Typeface fontRegular = FonteUtils.getFonte(FontCaixaEnum.REGULAR);
            Typeface fontBold = FonteUtils.getFonte(FontCaixaEnum.BOLD);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

            boolean isBold = false;
            String[] parts = texto.split("_");
            for (String part: parts) {
                int start = spannableStringBuilder.length();
                spannableStringBuilder.append(part);
                Typeface fontToApply = isBold ? fontBold : fontRegular;
                spannableStringBuilder.setSpan(new CustomTypefaceSpan("", fontToApply),
                        start, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                isBold = !isBold;
            }
            return spannableStringBuilder;
        }catch (Exception e){
            return  new SpannableStringBuilder().append(texto);
        }
    }

    public static SpannableStringBuilder textColor(Context context, String texto, int idCor) {
        return textColor(context,texto,idCor,true);
    }

    public static SpannableStringBuilder textColor(Context context, String texto, int idCor, Boolean colorBold) {
        Typeface font = ViewUtils.getFontCaixaStdBold(context);

        PositionStringBold positionStringBold = positionString(texto);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto.replace("_", ""));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(idCor)) , positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        if(colorBold){
            spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder textColorCaixa(Context context, String texto, int idCor) {
        return textColorCaixa(context,texto,idCor,true);
    }
    public static SpannableStringBuilder textColorCaixa(Context context, String texto, int idCor, Boolean colorBold) {
        Typeface font = ViewUtils.getFontCaixaStdBold(context);

        PositionStringBold positionStringBold = positionString(texto);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texto.replace("_", ""));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(idCor)) , positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        if(colorBold){
            spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), positionStringBold.getStart(), positionStringBold.getEnd(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder textFuturaAndFuturaBoldTitle(Context context, String texto) {
        SpannableStringBuilder spannableStringBuilder = textFuturaAndFuturaBold(context, texto);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(16, true), 0, texto.replace("_", "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    public static SpannableStringBuilder textCaixaSTDBoldTitle(Context context, String texto) {
        SpannableStringBuilder spannableStringBuilder = textCaixaSTDBold(context, texto);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(16, true), 0, texto.replace("_", "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    public static PositionStringBold positionString(String texto) {
        String[] listaString = texto.split("_");
        int start = listaString[0].length();
        int end = listaString[1].length() + start;
        return new PositionStringBold(start, end);
    }

    public static List<PositionStringBold> listPositionString(String texto) {
        List<PositionStringBold> positionStringBoldList = new ArrayList<>();

        Pattern pattern = Pattern.compile("_(.*?)_");
        Matcher matcher = pattern.matcher(texto);

        int underline = 1;
        while (matcher.find()) {
            int start = matcher.start(1) - underline;
            int end = matcher.end(1) - underline;
            positionStringBoldList.add(new PositionStringBold(start, end));
            underline += 2;
        }

        return positionStringBoldList;
    }


    public static int setDisplayMetric(int valor, Context context) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valor, context.getResources().getDisplayMetrics());
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
                        faixa.setQtd(1);
                        faixa.setSituacao(concurso.getSituacao());

                        faixaPremiadaDTOList.add(faixa);
                    }
                }
            }
        }

        if(premioDTO != null && premioDTO.getConcursosNaoPremiado() != null){
            for (ConcursoPremiadoDTO concurso : premioDTO.getConcursosNaoPremiado()) {
                FaixaPremiadaDTO faixa = new FaixaPremiadaDTO();
                faixa.setId(Long.parseLong(concurso.getConcurso().getNumero().toString()));
                faixa.setSituacao(concurso.getSituacao());
                faixa.setQtd(-1);

                faixaPremiadaDTOList.add(faixa);
            }
        }

        return faixaPremiadaDTOList;
    }

    public static List<FaixaPremiadaDTO> getFaixaNaoPremiadoDTO(PremioDTO premioDTO) {
        List<FaixaPremiadaDTO> faixaPremiadaDTOList = new ArrayList<>();

        if(premioDTO != null && premioDTO.getConcursosNaoPremiado() != null){
            for (ConcursoPremiadoDTO concurso : premioDTO.getConcursosNaoPremiado()) {
                FaixaPremiadaDTO faixa = new FaixaPremiadaDTO();
                faixa.setId(Long.parseLong(concurso.getConcurso().getNumero().toString()));
                faixa.setSituacao(concurso.getSituacao());
                faixa.setQtd(-1);

                faixaPremiadaDTOList.add(faixa);
            }
        }

        return faixaPremiadaDTOList;
    }

    public static List<FaixaPremiadaDTO> getFaixaPremiadoNaoPremiado(PremioDTO premioDTO) {
        List<FaixaPremiadaDTO> faixaPremiadaDTOList = new ArrayList<>();

        if (premioDTO != null && premioDTO.getConcursos() != null) {
            for (ConcursoPremiadoDTO concurso : premioDTO.getConcursos()) {

                if (concurso.getConcurso() != null && concurso.getFaixas() != null) {
                    for (FaixaPremiadaDTO faixa : concurso.getFaixas()) {
                        faixa.setId(Long.parseLong(concurso.getConcurso().getNumero().toString()));
                        faixa.setQtd(1);
                        faixa.setSituacao(concurso.getSituacao());

                        faixaPremiadaDTOList.add(faixa);
                    }
                }
            }
        }

        if(premioDTO != null && premioDTO.getConcursosNaoPremiado() != null){
            for (ConcursoPremiadoDTO concurso : premioDTO.getConcursosNaoPremiado()) {
                FaixaPremiadaDTO faixa = new FaixaPremiadaDTO();
                faixa.setId(Long.parseLong(concurso.getConcurso().getNumero().toString()));
                faixa.setSituacao(concurso.getSituacao());
                faixa.setQtd(-1);

                faixaPremiadaDTOList.add(faixa);
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

    public static ParametroSimulacao getParametroSimulacao(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum, String valorTipoConcurso) {
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null) {
            for (ParametroSimulacao parametroSimulacao : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (parametroSimulacao.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum) {
                    if (parametroSimulacao.getParametroJogo().getConcurso().getTipoConcurso().getValor().equalsIgnoreCase(valorTipoConcurso)) {
                        return parametroSimulacao;
                    }
                }
            }
        }
        return null;
    }

    public static ParametroSimulacao getParametroSimulacao(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum, IdentificaoDeUmaApostaDas8Modalidades aposta) {
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null && aposta != null) {
            for (ParametroSimulacao parametroSimulacao : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (    parametroSimulacao.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum &&
                        aposta.getConcursoAlvo().equals(parametroSimulacao.getParametroJogo().getConcurso().getNumero())) {
                    return parametroSimulacao;
                }
            }
        }
        return null;
    }

    public static ParametroSimulacao getParametroSimulacao(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum, IncluirSurpresinhaDTO aposta) {
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null && aposta != null) {
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

            if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                return aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL") ?
                        ModalidadeEnum.getDescricaoEspecial(aposta.getModalidade()) : ModalidadeEnum.getDescricao(aposta.getModalidade());
            } else {
                return aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL") ?
                        modalidadeDetalhada.getDescricaoEspecial():
                        modalidadeDetalhada.getDescricao();
            }
        } else {
            return aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL") ?
                    ModalidadeEnum.getDescricaoEspecial(aposta.getModalidade()) :
                    ModalidadeEnum.getDescricao(aposta.getModalidade());
        }
    }

    public static String getNomeModalidadePorAposta(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        Context context = Aplicacao.application.getBaseContext();
        ParametroSimulacao param = getParametroSimulacao(SessaoUsuario.getInstance(),aposta.getModalidade());
        if(param != null){
            ModalidadeDTO modalidadeDetalhada = param.getParametroJogo().getConcurso().getModalidadeDetalhada();

            if (modalidadeDetalhada.getValor() == 9){
                return context.getString(R.string.label_mais_milionaria);
            } else {
                return aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL") ?
                        modalidadeDetalhada.getDescricaoEspecial():
                        modalidadeDetalhada.getDescricao();
            }
        } /*else {
            return ModalidadeEnum.fromString(aposta.getModalidade());
        }*/
        return "";
    }

    public static String getNomeModalidadePorApostaDuasLinas(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        ParametroSimulacao param = getParametroSimulacao(SessaoUsuario.getInstance(),aposta.getModalidade());
        if(param != null){
            ModalidadeDTO modalidadeDetalhada = param.getParametroJogo().getConcurso().getModalidadeDetalhada();

            if (modalidadeDetalhada.getValor() == 9){
                return Aplicacao.application.getBaseContext().getString(R.string.label_mais_milionaria);
            } else {
                return aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL") ?
                        ModalidadeEnum.getDescricaoEspecialDuasLinhas(aposta.getModalidade()):
                        modalidadeDetalhada.getDescricao();
            }
        }
        return "";
    }

    public static Integer getConcursoAtualPorAposta(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        List<ParametroSimulacao> parametos = getListaParametroSimulacao(SessaoUsuario.getInstance(), aposta.getModalidade());
        int numConc = 0;
        if (aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL")) {
            for (ParametroSimulacao param :parametos) {
                if (param.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.ESPECIAL){
                    numConc = param.getParametroJogo().getConcurso().getNumero();
                }
            }
        } else {
            for (ParametroSimulacao param :parametos) {
                if (param.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL){
                    numConc = param.getParametroJogo().getConcurso().getNumero();
                }
            }
        }
        return numConc;
    }

    public static Integer getConcursoAtualPorApostaBolao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta.getConcursoAlvo() != null ? aposta.getConcursoAlvo() : 0;
    }

    public static List<ParametroSimulacao> getListaParametroSimulacao(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum) {
        ArrayList<ParametroSimulacao> parametos = new ArrayList<>();
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null) {
            for (ParametroSimulacao parametroSimulacao : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (parametroSimulacao.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum) {
                    parametos.add(parametroSimulacao);
                }
            }
        }
        return parametos;
    }

    public static Integer getConcursoAtualPorModalidade(String modalidade) {
        ParametroSimulacao param = getParametroSimulacao(SessaoUsuario.getInstance(),ModalidadeEnum.toString(modalidade));
        if(param != null) {
            return param.getParametroJogo().getConcurso().getNumero();
        }
        return -1;
    }

    public static int getSmallSizeQrCode(Context context) {
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

}
