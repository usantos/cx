package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.os.SystemClock;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DataHoraServidorSingleton {
    private static DataHoraServidorSingleton instance;
    public static final DateTimeZone SAO_PAULO_ZONE = DateTimeZone.forID("America/Sao_Paulo");
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final DateTimeFormatter DEFAULT_FORMATTER_SP = DateTimeFormat
            .forPattern(DEFAULT_DATE_TIME_PATTERN).withZone(SAO_PAULO_ZONE);
    private DateTime serverReferenceTime;
    private long syncElapserRealTimeMillis;

    private DataHoraServidorSingleton() {
        this.serverReferenceTime = new DateTime(SAO_PAULO_ZONE);
        this.syncElapserRealTimeMillis = SystemClock.elapsedRealtime();
    }

    public static synchronized DataHoraServidorSingleton getInstance(){
        if (instance == null){
            instance = new DataHoraServidorSingleton();
        }
        return instance;
    }

    public void atualizaDataHoraServidor(String dataHoraServidor) {
        DateTime parseTime = DEFAULT_FORMATTER_SP.parseDateTime(dataHoraServidor);
        this.serverReferenceTime = parseTime;
        this.syncElapserRealTimeMillis = SystemClock.elapsedRealtime();
    }

    public DateTime getCurrentDateTime() {
        long nowElapsedRealTimeMillis = SystemClock.elapsedRealtime();
        long millisPassaedSinceSync = nowElapsedRealTimeMillis - this.syncElapserRealTimeMillis;
        return this.serverReferenceTime.plus(millisPassaedSinceSync);
    }

    public long getDataHoraServidorMillis() {
        DateTime currentTime = getCurrentDateTime();
        return currentTime.getMillis();
    }
}
