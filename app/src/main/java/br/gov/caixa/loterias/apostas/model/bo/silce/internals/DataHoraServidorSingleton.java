package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class DataHoraServidorSingleton {
    private static DataHoraServidorSingleton instance;
    private static final long UPDATE_INTERVAL = 1000;

    private long serverTimeMillis;
    private Timer timer;

    private DataHoraServidorSingleton() {
    }

    public static DataHoraServidorSingleton getInstance(){
        if (instance == null){
            instance = new DataHoraServidorSingleton();
        }
        return instance;
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    serverTimeMillis += UPDATE_INTERVAL;
                }
            }, UPDATE_INTERVAL, UPDATE_INTERVAL);
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void atualizaDataHoraServidor(String dataHoraServidor) {
        stopTimer();
        if (dataHoraServidor == null) {
            serverTimeMillis = System.currentTimeMillis();
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
                Date date = sdf.parse(dataHoraServidor);
                serverTimeMillis = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                serverTimeMillis = System.currentTimeMillis();
            }
        }
        startTimer();
    }

    public long getDataHoraServidorMillis() {
        return serverTimeMillis;
    }
}
