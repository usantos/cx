package br.gov.caixa.loterias.apostas.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Calendar;

public class ContagemRegressiva extends CountDownTimer {

    public interface ContagemRegressivaListener {
        void onTick(long millisUntilFinish);
    }

    private TextView textView;
    private Calendar calendar = Calendar.getInstance();

    private ContagemRegressivaListener listener;
    private long millisUntilFinish = 0l;

    public ContagemRegressiva(TextView textView, long timeInFurure, long interval) {
        super(timeInFurure, interval);
        this.textView = textView;
    }

    @Override
    public void onTick(long millisUntilFinish) {
        this.millisUntilFinish = millisUntilFinish;
        String min = millisToMinute(millisUntilFinish);
        if (new Integer(min).intValue() < 10){
            min = "0" + min;
        }
        textView.setText(min + ":" + millisToSecond(millisUntilFinish));
        if (listener != null){
            listener.onTick(millisUntilFinish);
        }
    }

    public boolean estaFaltandoXMin(long millisUntilFinish, int minuto) {
        Integer min = Integer.valueOf(millisToMinute(millisUntilFinish));
        return min.intValue() < minuto;
    }

    @Override
    public void onFinish() {
        //textView.setText(millisToMinute(timeInFurure)+":"+millisToSecond(timeInFurure));
    }

    public long getMillisUntilFinish(){
        return millisUntilFinish;
    }

    private String millisToMinute(long millisUntilFinish) {
        calendar.setTimeInMillis(millisUntilFinish);
        return (""+calendar.get(Calendar.MINUTE));
    }

    private String millisToSecond(long millisUntilFinish) {
        calendar.setTimeInMillis(millisUntilFinish);
        return (calendar.get(Calendar.SECOND) < 10 ? "0"+calendar.get(Calendar.SECOND) : ""+calendar.get(Calendar.SECOND));
    }

    public void setListener(ContagemRegressivaListener listener){
        this.listener = listener;
    }
}
