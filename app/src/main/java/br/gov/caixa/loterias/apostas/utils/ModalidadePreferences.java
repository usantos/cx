package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.content.SharedPreferences;
import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.EnumMap;
import java.util.Map;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

/** Device-local preferences, independent of session cleanup. */
public final class ModalidadePreferences {
    public static final int MAX_FAVORITES = BuildConfig.MODALIDADE_MAX_FAVORITES;
    private final SharedPreferences preferences;
    private final String usagePrefix;

    public ModalidadePreferences(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences("modalidades_locais", Context.MODE_PRIVATE);
        usagePrefix = "usage_account_" + DadosUsuarioBO.obterCpf() + "_";
    }

    public boolean isFavorite(ModalidadeEnum modalidade) {
        return modalidade != null && preferences.getBoolean("favorite_" + modalidade.name(), false);
    }

    public void toggleFavorite(ModalidadeEnum modalidade) {
        setFavorite(modalidade, !isFavorite(modalidade));
    }

    public boolean setFavorite(ModalidadeEnum modalidade, boolean favorite) {
        if (modalidade == null) return false;
        if (favorite && !isFavorite(modalidade) && isFavoriteLimitReached()) return false;
        preferences.edit().putBoolean("favorite_" + modalidade.name(), favorite).apply();
        return true;
    }

    public boolean isFavoriteLimitReached() {
        return getFavorites().size() >= MAX_FAVORITES;
    }

    public List<ModalidadeEnum> getFavorites() {
        List<ModalidadeEnum> favorites = new ArrayList<>();
        for (ModalidadeEnum modalidade : ModalidadeEnum.values()) {
            if (isFavorite(modalidade)) favorites.add(modalidade);
        }
        return favorites;
    }

    public long usage(ModalidadeEnum modalidade) {
        return modalidade == null ? 0 : preferences.getLong(usagePrefix + modalidade.name(), 0);
    }

    public void mergeConfirmedUsage(Map<ModalidadeEnum, Long> counts) {
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<ModalidadeEnum, Long> entry : counts.entrySet()) {
            editor.putLong(usagePrefix + entry.getKey().name(), Math.max(usage(entry.getKey()), entry.getValue()));
        }
        editor.apply();
    }

    public void recordPurchase(CarrinhoDTO cart) {
        if (cart == null) return;
        Map<ModalidadeEnum, Long> purchased = new EnumMap<>(ModalidadeEnum.class);
        Set<String> seen = new HashSet<>();
        collect(cart.getApostas(), purchased, seen);
        collect(cart.getApostasIndividuais(), purchased, seen);
        collect(cart.getBoloes(), purchased, seen);
        if (cart.getCombos() != null && !cart.getCombos().isEmpty()) {
            purchased.put(ModalidadeEnum.COMBO, (long) cart.getCombos().size());
            for (ComboApostaDTO combo : cart.getCombos()) {
                if (combo != null) collect(combo.getApostas(), purchased, seen);
            }
        }
        SharedPreferences.Editor editor = preferences.edit();
        for (ModalidadeEnum modalidade : purchased.keySet()) {
            long count = usage(modalidade);
            long added = purchased.get(modalidade);
            editor.putLong(usagePrefix + modalidade.name(), count > Long.MAX_VALUE - added ? Long.MAX_VALUE : count + added);
        }
        editor.apply();
    }

    private void collect(List<IdentificaoDeUmaApostaDas8Modalidades> bets,
                         Map<ModalidadeEnum, Long> purchased, Set<String> seen) {
        if (bets == null) return;
        for (IdentificaoDeUmaApostaDas8Modalidades bet : bets) {
            if (bet == null || bet.getModalidade() == null) continue;
            if (bet.getId() != null && !seen.add(bet.getModalidade().name() + ":" + bet.getId())) continue;
            long quantity = bet.getQuantidadeApostas() == null || bet.getQuantidadeApostas() < 1
                    ? 1 : bet.getQuantidadeApostas();
            long count = purchased.containsKey(bet.getModalidade()) ? purchased.get(bet.getModalidade()) : 0;
            purchased.put(bet.getModalidade(), count > Long.MAX_VALUE - quantity ? Long.MAX_VALUE : count + quantity);
        }
    }
}
