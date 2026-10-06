package br.gov.caixa.loterias.apostas.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;

/**
 * Created by cedesbr450 on 12/04/18.
 */

public class Utils {

    private static Random random;
    private static final AtomicLong TS = new AtomicLong();

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(() -> delayCallback.afterDelay(), secs * 1000);
    }

    public static DadosUsuarioToken decodedToken(String JWTEncoded) throws Exception {
        if (JWTEncoded != null) {
            DadosUsuarioToken dadosUsuarioToken = new DadosUsuarioToken();
            try {
                String[] split = JWTEncoded.split("\\.");
                String json = getJson(split[1]);
                Gson gson = new Gson();
                dadosUsuarioToken = gson.fromJson(json, DadosUsuarioToken.class);
            } catch (UnsupportedEncodingException e) {

            }
            return dadosUsuarioToken;
        } else {
            return new DadosUsuarioToken();
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }


    public static long getIdUnico() {
        long micros = System.currentTimeMillis() * 1000;
        for (;;) {
            long value = TS.get();
            if (micros <= value)
                micros = value + 1;
            if (TS.compareAndSet(value, micros))
                return micros;
        }
    }

    public static Random getRandom() {
        if(random == null){
            random = new Random();
        }
        return random;
    }

    public static void vibra(){
        Context context = Aplicacao.application.getApplicationContext();
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window   window     = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradiente_titulo_caixa);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    public static boolean isErroNegocial(VolleyError erro){
        if(erro!= null && erro.networkResponse != null){
            return erro.networkResponse.statusCode == 400;
        }else {
            return false;
        }
    }

    public static List<List<Integer>> ordenaMatrizInteiro(List<List<Integer>> matriz) {
        for (List<Integer> lista: matriz) {
            Collections.sort(lista);
        }
        return matriz;
    }

    public static String getEquipeComUfBolao(EquipeDTO equipeDTO){
        String equipeComTime = equipeDTO.getParametroEquipe().getNome() != null ? equipeDTO.getParametroEquipe().getNome() : "";

        return equipeComTime + getUfTime(equipeDTO.getParametroEquipe());
    }

    public static String getEquipeNome(EquipeDTO equipeDTO){
        return (equipeDTO != null && equipeDTO.getNome() != null) ? equipeDTO.getNome(): "";
    }

    public static String getEquipeComUf(EquipeDTO equipeDTO){
        String equipeComTime = (equipeDTO != null && equipeDTO.getNome() != null) ? equipeDTO.getNome(): "";
        return equipeComTime + getUfTime(equipeDTO);
    }

    public static String getEquipeNome(ParametroEquipe equipe){
        return (equipe != null && equipe.getNome() != null) ? equipe.getNome(): "";
    }
    public static String getEquipeComUf(ParametroEquipe equipe){
        String equipeComTime = (equipe != null && equipe.getNome() != null) ? equipe.getNome(): "";
        return equipeComTime + getUfTime(equipe);
    }

    public static String getUfTime(EquipeDTO equipeDTO){
        String uf =  "";

        if (equipeDTO != null && equipeDTO.getNome() != null && equipeDTO.getSiglaPais() != null) {
            if(!equipeDTO.getSiglaPais().equalsIgnoreCase("BRA") || (equipeDTO.getNome().equalsIgnoreCase("BRASIL"))){
                uf +=  equipeDTO.getSiglaPais() != null
                        && !equipeDTO.getSiglaPais().isEmpty() ?
                        "/" + equipeDTO.getSiglaPais() : "";
            } else {
                uf += equipeDTO.getUf() != null
                        && !equipeDTO.getUf().isEmpty() ?
                        "/" + equipeDTO.getUf() : "";
            }
        }

        return uf;
    }

    public static String getUfTime(ParametroEquipe equipe){
        String uf =  "";

        if (equipe != null && equipe.getNome() != null && equipe.getSiglaPais() != null) {
            if(!equipe.getSiglaPais().equalsIgnoreCase("BRA") || (equipe.getNome().equalsIgnoreCase("BRASIL"))){
                uf +=  equipe.getSiglaPais() != null &&
                        !equipe.getSiglaPais().isEmpty() ?
                        "/" + equipe.getSiglaPais() : "";
            } else {
                uf += equipe.getUf() != null &&
                        !equipe.getUf().isEmpty() ?
                        "/" + equipe.getUf() : "";
            }
        }

        return uf;
    }

    public static void abreUrl(Activity activity,int str  ){
        Uri    uri    = Uri.parse(activity.getResources().getString(str));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    public static void enviaEmail(Activity activity, String email) {
        final Intent shareIntent = new Intent(Intent.ACTION_SENDTO);
        shareIntent.setData(Uri.parse( "mailto:"));
        shareIntent.putExtra(Intent.EXTRA_EMAIL,new String[] {email});
        try {
            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.enviando_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, activity.getResources().getString(R.string.erro_mail_provider), Toast.LENGTH_SHORT).show();
        }
    }

}
