package br.gov.caixa.loterias.apostas;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import static org.junit.Assert.*;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.ModalidadePreferences;

public class ModalidadeFavoritesTest {
    @Test public void confirmedGamesReachThresholdAndHistoryDoesNotDoubleCount() {
        Context base = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Context isolated = new ContextWrapper(base) {
            @Override public Context getApplicationContext() { return this; }
            @Override public SharedPreferences getSharedPreferences(String name, int mode) {
                return super.getSharedPreferences("test_modalidade_usage", mode);
            }
        };
        isolated.getSharedPreferences("test", 0).edit().clear().commit();
        try {
            ModalidadePreferences prefs = new ModalidadePreferences(isolated);
            br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO cart =
                    new br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO();
            br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades bet =
                    new br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades();
            bet.setId(100L);
            bet.setModalidade(ModalidadeEnum.LOTOFACIL);
            bet.setQuantidadeApostas(BuildConfig.MODALIDADE_REORDER_MIN_USAGE);
            cart.setApostas(java.util.Collections.singletonList(bet));
            cart.setApostasIndividuais(java.util.Collections.singletonList(bet));
            prefs.recordPurchase(cart);
            assertEquals(BuildConfig.MODALIDADE_REORDER_MIN_USAGE, prefs.usage(ModalidadeEnum.LOTOFACIL));
            java.util.Map<ModalidadeEnum, Long> history = new java.util.EnumMap<>(ModalidadeEnum.class);
            history.put(ModalidadeEnum.LOTOFACIL, (long) BuildConfig.MODALIDADE_REORDER_MIN_USAGE);
            history.put(ModalidadeEnum.QUINA, 12L);
            prefs.mergeConfirmedUsage(history);
            prefs.mergeConfirmedUsage(history);
            assertEquals(BuildConfig.MODALIDADE_REORDER_MIN_USAGE, prefs.usage(ModalidadeEnum.LOTOFACIL));
            assertEquals(12, new ModalidadePreferences(isolated).usage(ModalidadeEnum.QUINA));
            java.util.List<ModalidadeEnum> ordered = br.gov.caixa.loterias.apostas.utils.ModalidadeOrdering.sorted(
                    java.util.Arrays.asList(ModalidadeEnum.MEGA_SENA, ModalidadeEnum.LOTOFACIL),
                    item -> false, item -> false, prefs::usage, BuildConfig.MODALIDADE_REORDER_MIN_USAGE);
            assertEquals(ModalidadeEnum.LOTOFACIL, ordered.get(0));
        } finally {
            isolated.getSharedPreferences("test", 0).edit().clear().commit();
        }
    }

    @Test public void sixthFavoriteIsBlockedAndRemovalAllowsAnother() {
        Context base = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Context isolated = new ContextWrapper(base) {
            @Override public Context getApplicationContext() { return this; }
            @Override public SharedPreferences getSharedPreferences(String name, int mode) {
                return super.getSharedPreferences("test_modalidade_limit", mode);
            }
        };
        isolated.getSharedPreferences("test", 0).edit().clear().commit();
        try {
            ModalidadePreferences prefs = new ModalidadePreferences(isolated);
            ModalidadeEnum[] modalities = ModalidadeEnum.values();
            for (int i = 0; i < 5; i++) prefs.setFavorite(modalities[i], true);
            prefs.setFavorite(modalities[5], true);
            assertEquals(5, prefs.getFavorites().size());
            assertFalse(prefs.isFavorite(modalities[5]));
            prefs.setFavorite(modalities[0], true);
            assertEquals(5, prefs.getFavorites().size());
            prefs.setFavorite(modalities[0], false);
            prefs.setFavorite(modalities[5], true);
            assertTrue(new ModalidadePreferences(isolated).isFavorite(modalities[5]));
            assertEquals(5, prefs.getFavorites().size());
        } finally {
            isolated.getSharedPreferences("test", 0).edit().clear().commit();
        }
    }

    @Test public void favoritesSurviveNewInstanceAndCanBeRemoved() {
        Context base = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Context isolated = new ContextWrapper(base) {
            @Override public Context getApplicationContext() { return this; }
            @Override public SharedPreferences getSharedPreferences(String name, int mode) {
                return super.getSharedPreferences("test_modalidade_favorites", mode);
            }
        };
        isolated.getSharedPreferences("test", 0).edit().clear().commit();
        try {
            ModalidadePreferences first = new ModalidadePreferences(isolated);
            first.setFavorite(ModalidadeEnum.LOTOFACIL, true);
            first.setFavorite(ModalidadeEnum.LOTOFACIL, true);
            ModalidadePreferences reopened = new ModalidadePreferences(isolated);
            assertTrue(reopened.isFavorite(ModalidadeEnum.LOTOFACIL));
            assertEquals(1, reopened.getFavorites().size());
            reopened.setFavorite(ModalidadeEnum.LOTOFACIL, false);
            assertTrue(new ModalidadePreferences(isolated).getFavorites().isEmpty());
        } finally {
            isolated.getSharedPreferences("test", 0).edit().clear().commit();
        }
    }

    @Test public void heartIsCenteredOnHeaderBoundaryAndAlignedRight() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Context context = new ContextThemeWrapper(
                    InstrumentationRegistry.getInstrumentation().getTargetContext(),
                    androidx.appcompat.R.style.Theme_AppCompat);
            View card = LayoutInflater.from(context).inflate(R.layout.card_modalidade, null);
            float density = context.getResources().getDisplayMetrics().density;
            int width = Math.round(280 * density);
            card.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(Math.round(600 * density), View.MeasureSpec.EXACTLY));
            card.layout(0, 0, card.getMeasuredWidth(), card.getMeasuredHeight());
            View heart = card.findViewById(R.id.favoriteModalidade);
            View header = card.findViewById(R.id.linearLayoutTopModalidades);
            assertTrue(heart.getParent() instanceof FrameLayout);
            assertEquals(header.getBottom(), heart.getTop() + heart.getHeight() / 2, 1);
            assertEquals(Math.round(8 * density), width - heart.getRight(), 1);
        });
    }
}
