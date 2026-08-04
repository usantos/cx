package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;

public class DescricaoLoteca {
    public static String descricaoPartida(PartidaLotecaDTO partida) {
        if (partida == null || partida.getDescricaoLegenda() == null) {
            return "";
        }
        if (partida.getDescricaoLegenda() != null) {
            if (partida.getDescricaoLegenda().contains("FEM.") ||
                    partida.getDescricaoLegenda().contains("FEMININO")) {
                return " (feminino)";
            }
            if (partida.getDescricaoLegenda().contains("SUB17")) {
                return " (sub 17)";
            }
            if (partida.getDescricaoLegenda().contains("SUB20")) {
                return " (sub 20)";
            }
        }

        return "";
    }
    public static String descricaoPartida(ParametroPartida partida) {
        if (partida == null || partida.getDescricaoLegenda() == null) {
            return "";
        }
        if (partida.getDescricaoLegenda() != null) {
            if (partida.getDescricaoLegenda().contains("FEM.") ||
                    partida.getDescricaoLegenda().contains("FEMININO")) {
                return " (feminino)";
            }
            if (partida.getDescricaoLegenda().contains("SUB17")) {
                return " (sub 17)";
            }
            if (partida.getDescricaoLegenda().contains("SUB20")) {
                return " (sub 20)";
            }
        }

        return "";
    }
}
