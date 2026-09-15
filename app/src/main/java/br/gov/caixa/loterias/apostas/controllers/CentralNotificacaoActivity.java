package br.gov.caixa.loterias.apostas.controllers;

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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.NotificacaoResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NotificacaoAdapter;

public class CentralNotificacaoActivity extends LoteriasBaseAppActivity {

    private static final String ABA_TODAS = "TODAS";
    private static final String ABA_NAO_LIDAS = "NAO_LIDAS";
    private Button btnAbaTodas, btnAbaNaoLidas,btnMarcarTodas, btnExcluirTodas;
    private RecyclerView rvNotificacoes;
    private LinearLayout llEstadoVazio;
    private TextView textVazioTitulo;
    private NotificacaoAdapter notificacaoAdapter;
    private String abaSelerionada = ABA_TODAS;
    private List<NotificacaoDTO> listNotificacaoTodas, listNotificacaoNaoLidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_notificacao);

        setTitle();
        setViews();
        selecionaAba(ABA_TODAS);
        carregarDados();

        desabilitaBtn(btnMarcarTodas);
        desabilitaBtn(btnExcluirTodas);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(AlertDialogUtils.isShow()){
//            AlertDialogUtils.dismiss();
//        }
    }

    private void setTitle() {
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setText(getString(R.string.central_notificacoes));
        textTitulo.setHint(getString(R.string.titulo));
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

//        Data dataHoje = new Data();
//        dataHoje.setCalendar(new Date());
//        List<NotificacaoDTO> listNotificacaoDTO = new ArrayList<>();
//        listNotificacaoDTO.add(new NotificacaoDTO(1L,"Titulo 01", "Conteudo 01", dataHoje));
//        listNotificacaoDTO.add(new NotificacaoDTO(2L,"Titulo 02", "Conteudo 02", dataHoje));
//        listNotificacaoDTO.add(new NotificacaoDTO(3L,"Titulo 03", "Conteudo 03", dataHoje));

        notificacaoAdapter = new NotificacaoAdapter(this, new ArrayList<NotificacaoDTO>(), new NotificacaoAdapter.OnExcluirClickListener() {
            @Override
            public void onExcluir(NotificacaoDTO notificacaoDTO) {
            }
        });
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

        List<NotificacaoDTO> listNotificacaoSelecionada = todasSelecionada ? listNotificacaoTodas : listNotificacaoNaoLidas;
        if (listNotificacaoSelecionada != null) {
            exibirNotificacoes(aba, listNotificacaoSelecionada);
        }
    }

    private void exibirNotificacoes(String aba, List<NotificacaoDTO> listNotificacao) {
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void carregarDados() {
        AlertDialogUtils.show(this);
        DadosUsuarioBO.getInstance().getHistoricoNotificacao(new RequestListener<NotificacaoResponse>() {
            @Override
            public void onResponse(NotificacaoResponse response) {
                AlertDialogUtils.dismiss();
                if (response != null && response.getPayload() != null) {
                    listNotificacaoTodas = response.getPayload();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, CentralNotificacaoActivity.this);
            }
        });


    }

    private void habilitaBtn(Button btn) {
        btn.setBackground(getDrawable(R.drawable.btn_rounded_azul));
        btn.setTextColor(getColor(R.color.branco));
    }
    private void desabilitaBtn(Button btn) {
        btn.setBackground(getDrawable(R.drawable.btn_rounded_disable));
        btn.setTextColor(getColor(R.color.cinza70));
    }
}
