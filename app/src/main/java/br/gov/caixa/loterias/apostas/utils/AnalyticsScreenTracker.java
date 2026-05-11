package br.gov.caixa.loterias.apostas.utils;

public class AnalyticsScreenTracker {

    private static String lastScreenName = "app_start";

    public static String getLastScreenName() {
        return lastScreenName;
    }

    public static void updateLastScreenName(String screenName) {
        lastScreenName = screenName;
    }

    private AnalyticsScreenTracker() {
        // no-op
    }
}
