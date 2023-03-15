package br.gov.caixa.loterias.apostas.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Calendar;

public class ContagemRegressiva extends CountDownTimer {

    private TextView textView;
    private Calendar calendar = Calendar.getInstance();


    public ContagemRegressiva(TextView textView, long timeInFurure, long interval) {
        super(timeInFurure, interval);
        this.textView = textView;
    }

    @Override
    public void onTick(long millisUntilFinish) {
        textView.setText(millisToMinute(millisUntilFinish)+"m "+millisToSecond(millisUntilFinish)+"seg");
    }

    @Override
    public void onFinish() {
        //textView.setText(millisToMinute(timeInFurure)+":"+millisToSecond(timeInFurure));
    }

    private String millisToMinute(long millisUntilFinish) {
        calendar.setTimeInMillis(millisUntilFinish);
        return (""+calendar.get(Calendar.MINUTE));
    }

    private String millisToSecond(long millisUntilFinish) {
        calendar.setTimeInMillis(millisUntilFinish);
        return (calendar.get(Calendar.SECOND) < 10 ? "0"+calendar.get(Calendar.SECOND) : ""+calendar.get(Calendar.SECOND));
    }
}
