package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DataNotificacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoCentralDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NotificacaoAdapter;

public class CentralNotificacaoActivity extends LoteriasBaseAppActivity {

    private static final String ABA_TODAS = "TODAS";
    private static final String ABA_NAO_LIDAS = "NAO_LIDAS";
    private Button btnAbaTodas;
    private Button btnAbaNaoLidas;
    private Button btnMarcarTodas;
    private Button btnExcluirTodas;
    private RecyclerView rvNotificacoes;
    private LinearLayout llEstadoVazio;
    private TextView textVazioTitulo;
    private NotificacaoAdapter notificacaoAdapter;
    private String abaSelerionada = ABA_TODAS;
    private List<NotificacaoCentralDTO> listNotificacaoTodas;
    private List<NotificacaoCentralDTO> listNotificacaoNaoLidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_notificacao);

        setTitle();
        setViews();
        selecionaAba(ABA_TODAS);
        carregarLidasServico();
        carregarNaoLidasServico();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AlertDialogUtils.isShow()){
            AlertDialogUtils.dismiss();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("QTD_NAO_LIDAS", listNotificacaoNaoLidas != null ? listNotificacaoNaoLidas.size(): 0);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void setTitle() {
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.central_notificacoes));
        textTitulo.setHint(getString(R.string.titulo));
        ImageView btnBack = findViewById(R.id.customButton);
        btnBack.setOnClickListener(v -> finish());
        ImageView imageTrevo = findViewById(R.id.icon_loterias);
        imageTrevo.setVisibility(View.VISIBLE);
    }

    private void setViews() {
        btnAbaTodas = findViewById(R.id.btnAbaTodas);
        btnAbaNaoLidas = findViewById(R.id.btnAbaNaoLidas);
        btnMarcarTodas = findViewById(R.id.btnMarcarTodas);
        btnExcluirTodas = findViewById(R.id.btnExcluirTodas);
        rvNotificacoes = findViewById(R.id.rvNotificacoes);
        llEstadoVazio = findViewById(R.id.llEstadoVazio);
        textVazioTitulo = findViewById(R.id.textVazioTitulo);

        btnAbaTodas.setOnClickListener(v -> selecionaAba(ABA_TODAS));
        btnAbaNaoLidas.setOnClickListener(v -> selecionaAba(ABA_NAO_LIDAS));

        btnMarcarTodas.setOnClickListener(v -> marcarTodas());
        btnExcluirTodas.setOnClickListener(v -> excluirTodas());

        notificacaoAdapter = new NotificacaoAdapter(this, new ArrayList<NotificacaoCentralDTO>(),
                this::excluirNoticacao);
        rvNotificacoes.setLayoutManager(new LinearLayoutManager(this));
        rvNotificacoes.setAdapter(notificacaoAdapter);
    }

    private void selecionaAba(String aba) {
        abaSelerionada = aba;

        boolean todasSelecionada = ABA_TODAS.equals(aba);
        btnAbaTodas.setBackground(getDrawable(todasSelecionada ? R.drawable.tab_selecionada : R.drawable.tab_nao_selecionada));
        btnAbaTodas.setTextColor(getColor(todasSelecionada ? R.color.branco : R.color.cinza70));
        btnAbaNaoLidas.setBackground(getDrawable(todasSelecionada ? R.drawable.tab_nao_selecionada : R.drawable.tab_selecionada));
        btnAbaNaoLidas.setTextColor(getColor(todasSelecionada ? R.color.cinza70 : R.color.branco));

        atualizarContadorNaoLidas();
        atualizarBotoesRodape();

        List<NotificacaoCentralDTO> listNotificacaoSelecionada = todasSelecionada ? listNotificacaoTodas : listNotificacaoNaoLidas;
        if (listNotificacaoSelecionada != null) {
            exibirNotificacoes(aba, listNotificacaoSelecionada);
        }
    }

    private void exibirNotificacoes(String aba, List<NotificacaoCentralDTO> listNotificacao) {
        if (listNotificacao == null || listNotificacao.isEmpty()) {
            boolean todas = ABA_TODAS.equals(aba);
            textVazioTitulo.setText(todas ? R.string.sem_notificacao : R.string.sem_notificacao_nao_lida);
            llEstadoVazio.setVisibility(View.VISIBLE);
            rvNotificacoes.setVisibility(View.GONE);
            return;
        }

        llEstadoVazio.setVisibility(View.GONE);
        rvNotificacoes.setVisibility(View.VISIBLE);
        notificacaoAdapter.atualizar(listNotificacao);
    }

    private void carregarLidasServico() {
        AlertDialogUtils.show(this);
        DadosUsuarioBO.getInstance().getHistoricoNotificacao(new RequestListener<NotificacaoResponse>() {
            @Override
            public void onResponse(NotificacaoResponse response) {
                AlertDialogUtils.dismiss();
                if (response != null && response.getPayload() != null) {
                    listNotificacaoTodas = response.getPayload();
                    atualizarBotoesRodape();
                    if (abaSelerionada.equals(ABA_TODAS)) {
                        exibirNotificacoes(ABA_TODAS, listNotificacaoTodas);
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                listNotificacaoTodas = new ArrayList<>();
                atualizarBotoesRodape();
                if (abaSelerionada.equals(ABA_TODAS)) {
                    exibirNotificacoes(ABA_TODAS, listNotificacaoTodas);
                }
                RedirectNetwork.checkRedirect(error, CentralNotificacaoActivity.this);
            }
        });
    }

    private void carregarNaoLidasServico() {
        AlertDialogUtils.show(this);
        DadosUsuarioBO.getInstance().getHistoricoNotificacaoNaoLidas(new RequestListener<NotificacaoResponse>() {
            @Override
            public void onResponse(NotificacaoResponse response) {
                AlertDialogUtils.dismiss();
                if (response != null && response.getPayload() != null) {
                    listNotificacaoNaoLidas = response.getPayload();
                    atualizarContadorNaoLidas();
                    atualizarBotoesRodape();
                    if(abaSelerionada.equals(ABA_NAO_LIDAS)) {
                        exibirNotificacoes(ABA_NAO_LIDAS, listNotificacaoNaoLidas);
                    }
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                listNotificacaoNaoLidas = new ArrayList<>();
                atualizarContadorNaoLidas();
                atualizarBotoesRodape();
                if(abaSelerionada.equals(ABA_NAO_LIDAS)) {
                    exibirNotificacoes(ABA_NAO_LIDAS, listNotificacaoNaoLidas);
                }
                RedirectNetwork.checkRedirect(error, CentralNotificacaoActivity.this);
            }
        });
    }

    private void excluirNotificacaoServico(NotificacaoCentralDTO notificacaoCentralDTO) {
        AlertDialogUtils.show(this);
        DadosUsuarioBO.getInstance().putRemoveNotificacao(notificacaoCentralDTO.getId(), new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                AlertDialogUtils.dismiss();

                removerNotificacaoPorId(listNotificacaoTodas, notificacaoCentralDTO.getId());
                removerNotificacaoPorId(listNotificacaoNaoLidas, notificacaoCentralDTO.getId());

                atualizarContadorNaoLidas();
                atualizarBotoesRodape();

                if (ABA_TODAS.equals(abaSelerionada)) {
                    exibirNotificacoes(ABA_TODAS, listNotificacaoTodas);
                } else {
                    exibirNotificacoes(ABA_NAO_LIDAS, listNotificacaoNaoLidas);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, CentralNotificacaoActivity.this);
            }
        });
    }

    private void limpaNotificacoesServico() {
        AlertDialogUtils.show(this);
        DadosUsuarioBO.getInstance().putLimpaNotificacoes(new RequestListener<RetornoPadraoResponse>() {
            @Override
            public void onResponse(RetornoPadraoResponse response) {
                AlertDialogUtils.dismiss();

                listNotificacaoTodas = new ArrayList<>();
                listNotificacaoNaoLidas = new ArrayList<>();

                atualizarContadorNaoLidas();
                atualizarBotoesRodape();

                if (ABA_TODAS.equals(abaSelerionada)) {
                    exibirNotificacoes(ABA_TODAS, listNotificacaoTodas);
                } else {
                    exibirNotificacoes(ABA_NAO_LIDAS, listNotificacaoNaoLidas);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, CentralNotificacaoActivity.this);
            }
        });
    }

    private void visualizaTodasNotificacoesServico() {
        marcarNaoLidasComoLidas();
    }

    private void marcarNaoLidasComoLidas() {

        if (listNotificacaoNaoLidas == null || listNotificacaoNaoLidas.isEmpty()) {
            return;
        }

        final int total = listNotificacaoNaoLidas.size();
        final int[] concluidas = {0};

        for (NotificacaoCentralDTO notificacao : new ArrayList<>(listNotificacaoNaoLidas)) {

            DadosUsuarioBO.getInstance().putLerIdNotificacao(
                    notificacao.getId(),
                    new RequestListener<RetornoPadraoResponse>() {

                        @Override
                        public void onResponse(RetornoPadraoResponse response) {
                            removerNotificacaoPorId(listNotificacaoNaoLidas, notificacao.getId());
                            finalizarProcessamento(concluidas, total);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            finalizarProcessamento(concluidas, total);
                        }
                    });
        }
    }

    private void finalizarProcessamento(int[] concluidas, int total) {
        concluidas[0]++;

        if (concluidas[0] == total) {
            atualizarTelaAposMarcarComoLida();
        }
    }

    private void atualizarTelaAposMarcarComoLida() {
        carregarLidasServico();
        atualizarContadorNaoLidas();
        atualizarBotoesRodape();

        if (ABA_TODAS.equals(abaSelerionada)) {
            exibirNotificacoes(ABA_TODAS, listNotificacaoTodas);
            return;
        }

        exibirNotificacoes(ABA_NAO_LIDAS, listNotificacaoNaoLidas);
    }

    private void marcarTodas() {
        DialogUtils.dialogTituloSim(this,
                getString(R.string.marcar_todas_como_lidas),
                getString(R.string.deseja_marcar_todas_como_lidas),
                (dialog, which) -> visualizaTodasNotificacoesServico());
    }

    private void excluirTodas() {
        DialogUtils.dialogTituloSim(this,
                getString(R.string.excluir_notificacoes),
                getString(R.string.deseja_excluir_todas),
                (dialog, which) -> limpaNotificacoesServico());
    }

    private void excluirNoticacao(NotificacaoCentralDTO notificacaoCentralDTO) {
        DialogUtils.dialogTituloSim(this,
                getString(R.string.excluir_notificacao),
                getString(R.string.deseja_excluir_notificacao) +" \""+ notificacaoCentralDTO.getTitulo() +"\"?",
                (dialog, which) -> excluirNotificacaoServico(notificacaoCentralDTO));
    }

    private void removerNotificacaoPorId(List<NotificacaoCentralDTO> lista, Long id) {
        if (lista == null || id == null) {
            return;
        }

        for (int i = 0; i < lista.size(); i++) {
            NotificacaoCentralDTO notificacao = lista.get(i);

            if (id.equals(notificacao.getId())) {
                lista.remove(i);
                break;
            }
        }
    }

    private void atualizarContadorNaoLidas() {
        int quantidade = listNotificacaoNaoLidas != null ? listNotificacaoNaoLidas.size() : 0;
        btnAbaNaoLidas.setText(getString(R.string.tab_nao_lidas).replace("0", String.valueOf(quantidade)));
    }

    private void atualizarBotoesRodape() {
        if(abaSelerionada.equals(ABA_TODAS)) {
            if(listNotificacaoTodas == null || listNotificacaoTodas.isEmpty()) {
                desabilitaBtn(btnMarcarTodas);
                btnExcluirTodas.setVisibility(View.VISIBLE);
                desabilitaBtn(btnExcluirTodas);
            } else {
                if (listNotificacaoNaoLidas == null || listNotificacaoNaoLidas.isEmpty()) {
                    desabilitaBtn(btnMarcarTodas);
                } else {
                    habilitaBtnAzul(btnMarcarTodas);
                }
                btnExcluirTodas.setVisibility(View.VISIBLE);
                habilitaBtnBordaAzul(btnExcluirTodas);
            }
        } else {
            if(listNotificacaoNaoLidas == null || listNotificacaoNaoLidas.isEmpty()) {
                desabilitaBtn(btnMarcarTodas);
                btnExcluirTodas.setVisibility(View.GONE);
            } else {
                habilitaBtnAzul(btnMarcarTodas);
                btnExcluirTodas.setVisibility(View.GONE);
            }
        }
    }

    private void habilitaBtnAzul(Button btn) {
        btn.setEnabled(true);
        btn.setBackground(getDrawable(R.drawable.btn_rounded_azul));
        btn.setTextColor(getColor(R.color.branco));
    }
    private void habilitaBtnBordaAzul(Button btn) {
        btn.setEnabled(true);
        btn.setBackground(getDrawable(R.drawable.btn_rounded_azul_borda));
        btn.setTextColor(getColor(R.color.bolao_azul_filtro));
    }
    private void desabilitaBtn(Button btn) {
        btn.setEnabled(false);
        btn.setBackground(getDrawable(R.drawable.btn_rounded_disable));
        btn.setTextColor(getColor(R.color.cinza70));
    }
}
