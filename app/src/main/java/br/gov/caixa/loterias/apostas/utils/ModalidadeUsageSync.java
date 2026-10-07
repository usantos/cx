package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import com.android.volley.VolleyError;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import br.gov.caixa.loterias.apostas.model.bean.ApostaPageRequest;
import br.gov.caixa.loterias.apostas.model.bo.*;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;

/** Reads all available confirmed-bet pages without blocking the home screen. */
public final class ModalidadeUsageSync {
    private boolean loading;

    public void refresh(Context context, Runnable onComplete) {
        if (loading) return;
        String account = DadosUsuarioBO.obterCpf();
        if (account == null || account.trim().isEmpty()) return;
        loading = true;
        requestPage(0, account, new ModalidadePreferences(context),
                new EnumMap<>(ModalidadeEnum.class), new HashSet<>(), onComplete);
    }

    private void requestPage(int offset, String account, ModalidadePreferences preferences,
                             Map<ModalidadeEnum, Long> counts, Set<String> seen, Runnable onComplete) {
        ApostaPageRequest page = new ApostaPageRequest(100);
        page.setOffset(offset);
        page.setSituacao(1);
        RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse> listener =
                new RequestListener<ResultadoPesquisaPaginadaDTOApostaDTOResponse>() {
            @Override public void onResponse(ResultadoPesquisaPaginadaDTOApostaDTOResponse response) {
                if (!account.equals(DadosUsuarioBO.obterCpf()) || response == null
                        || response.getRedirect() != null || response.getPayload() == null
                        || response.getPayload().getLista() == null) {
                    loading = false;
                    return;
                }
                List<ApostaDTO> bets = response.getPayload().getLista();
                int previousSize = seen.size();
                for (ApostaDTO bet : bets) {
                    if (bet == null || bet.getModalidade() == null) continue;
                    if (bet.getId() != null && !seen.add(bet.getModalidade().name() + ":" + bet.getId())) continue;
                    long quantity = bet.getQuantidadeApostas() == null || bet.getQuantidadeApostas() < 1
                            ? 1 : bet.getQuantidadeApostas();
                    long old = counts.containsKey(bet.getModalidade()) ? counts.get(bet.getModalidade()) : 0;
                    counts.put(bet.getModalidade(), old > Long.MAX_VALUE - quantity ? Long.MAX_VALUE : old + quantity);
                }
                Long total = response.getPayload().getRowCount();
                int nextOffset = offset + bets.size();
                boolean more = !bets.isEmpty() && (total != null ? nextOffset < total : bets.size() == page.getSize());
                // Stop if a backend ignores pagination and repeats the same records.
                if (more && (seen.size() > previousSize || bets.stream().anyMatch(b -> b != null && b.getId() == null))) {
                    requestPage(nextOffset, account, preferences, counts, seen, onComplete);
                } else {
                    preferences.mergeConfirmedUsage(counts);
                    loading = false;
                    onComplete.run();
                }
            }
            @Override public void onErrorResponse(VolleyError error) {
                loading = false;
            }
        };
        if (NovaApiUtils.isPossoBuscarNovaAPI()) {
            NovaAPIBO.getInstance().getApostasConfirmadas(page, listener);
        } else if (Boolean.TRUE.equals(SharedPreferencesUtils.getValorBoolean(
                ConfiguracoesEnum.IS_MICRO_SERVICO.get(), ConfiguracoesDefaultEnum.IS_MICRO_SERVICO.asBoolean()))) {
            ApostaMicroServicoBO.getInstance().getApostasConfirmadasMicroServico(page, listener);
        } else {
            ApostaSilceBO.getInstance().getApostasConfirmadasSilce(page, listener);
        }
    }
}
