package br.gov.caixa.loterias.apostar.interno.utils;

/**
 * Created by lcardoso on 15/09/2017.
 */

public enum ColorEnum {
    MEGASENACLARO ("#00AB67", 0),
    MEGASENAESCURO ("#009B63", 1),
    LOTOFACILCLARO("#BD3E96", 2),
    LOTOFACILESCURO ("#A53384", 3),
    QUINACLARO("#803594", 4),
    QUINAESCURO ("#702A82", 5),
    DUPLASENACLARO("#A62A52" , 6),
    DUPLASENAESCURO("#90214A" , 7);

    private String stringValue;
    private int intValue;
    private ColorEnum(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
