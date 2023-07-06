package br.gov.caixa.loterias.apostas.controllers;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class MensagensPushActivity extends LoteriasAppActivity {

    private List<MessagePush> mensagensList;
    private List<MessagePush> mensagensNaolidas;
    private List<MessagePush> mensagensFiltradas;
    private MensagempushAdapter mensagempushAdapter;
    private EditText searchEditText;
    private TextView mensagensVazioTextview;
    private TabLayout tabLayout;
    private GridLayoutManager layoutManager;
    private NestedScrollView scrollViewContentViewMensagens;
    private RecyclerView recyclerViewMensagens;
    private View progressLoadingMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_msg);
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        recyclerViewMensagens = findViewById(R.id.recyclerViewCentralMsg);
        tabLayout = findViewById(R.id.tab_layout);
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.titulo_central_mensagens)));
        ImageView ibAjuda = findViewById(R.id.ib_duvidas);
        ibAjuda.setVisibility(View.GONE);
        Button btnVoltar = findViewById(R.id.btn_voltar);
        mensagensVazioTextview = findViewById(R.id.mensagensVazioTextview);

        //Inicializa listas de mensagens

        mensagensList = gerarMsgTodasMock();
        mensagensNaolidas = gerarMsgNaoLidasMock();

        //Define adaptador inicial
        mensagempushAdapter = new MensagempushAdapter(mensagensList);
        recyclerViewMensagens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMensagens.setAdapter(mensagempushAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mensagensFiltradas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        //Configura as abas
        tabLayout.addTab(tabLayout.newTab().setText("Todas"));
        tabLayout.addTab(tabLayout.newTab().setText("Não Lidas"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#0066B3"));
        tabLayout.setSelectedTabIndicator(R.drawable.tab_push_select);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.parseColor("#0066B3")));
        tabLayout.setInlineLabel(true);

        // configurar titulo do layout
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            if (tab != null) {
//                View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//                TextView tabTitleTextView = customView.findViewById(R.id.tabTitleTextView);
//                tabTitleTextView.setText(tab.getText().toString().toUpperCase());
//            }
//        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    mensagempushAdapter.setMensagensList(mensagensList);
                } else if (position == 1) {
                    mensagempushAdapter.setMensagensList(mensagensNaolidas);
                }
                mensagempushAdapter.notifyDataSetChanged();
                checarEstadoVazio();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        checarEstadoVazio();
    }

    private void checarEstadoVazio() {
        if (mensagempushAdapter.getItemCount() == 0) {
            Toast.makeText(MensagensPushActivity.this, "Lista de mensagens está vazia", Toast.LENGTH_LONG).show();
        }
    }

    private List<MessagePush> gerarMsgTodasMock() {
        mensagensList = new ArrayList<>();
//        massa de dados mensagens lidas
        mensagensList.add(new MessagePush(R.drawable.ic_pagamentos, 1, "Pagamentos", "Aproveite e aposte ! este item dos pagamentos , aproveite aposte , aproveite aposte, aproveite aposte, aproveite aposte", "01-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_logo_money, 2,"Lembretes de sorte", "Aproveite e aposte ! , estou continuando a mensagem para ver como fica o aproveite e aposte ", "02-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_push_dicas_news, 4,"Dicas e Novidades", "Aproveite e aposte ! , estou alogando a mensagem para ver como fica o texto até o final ", "03-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_push_resultados,3, "Resultados disponiveis", "Aproveite e aposte ! esta mensagem fala sobre resultados disponiveis, aqui voce vai ganhar link para resultados disponiveis, aqui vai botao de resultados", "01-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_push_alertas_seguranca, 5,"Alerta de segurança", "Conteudo de alerta de segurança da mensagem 5, esta msg fala sobre segurança no app, informacoes de como fazer a seguranca no seu celular e dar tudo o que puder", "01-fev", true));
    return mensagensList;
    }
    private List<MessagePush> gerarMsgNaoLidasMock() {
        mensagensNaolidas = new ArrayList<>();
        //      massa de dados mensagens nao lidas
        mensagensNaolidas.add(new MessagePush(R.drawable.ic_teimosinha, 0,"Teimosinha", "Conteudo da mensagem Teimosinha, fala sobre tudo o que puder, mostrando as vantagens da teimosinha a todo o vapor", "01-fev", false));
        mensagensNaolidas.add(new MessagePush(R.drawable.icon_trevo, 0,"Trevo", "Conteudo da Trevo, aqui estou mostrando as vantagens que o Trevo da sorte pode trazer para os clientes ", "09-fev", false));
        mensagensNaolidas.add(new MessagePush(R.drawable.icon_loteria, 0,"Loteria ", "Conteudo da Loteria, aqui vamos na mensagem das loterias e estamos demonstrando o que fazer nesta loteria da CEF","06-fev", false));
        mensagensNaolidas.add(new MessagePush(R.drawable.icon_add_carrinho, 0,"Adicione este combo", "Conteudo da mensagem dos combos , aqui vamos descrever como mostrar o combo das atividades estamos fazendo tudo o que puder", "03-fev", false));
        mensagensNaolidas.add(new MessagePush(R.drawable.icon_data_megasena, 0,"Megasena data", "Conteudo da mensagem, acumulou a nova MEGASENA , agora vamos mostrar o prêmio milionário e sua chance de mostrar ", "02-fev", false));
        mensagensNaolidas.add(new MessagePush(R.drawable.icon_surpresinha, 0,"Surpresinha", "Conteudo da mensagem Surpresinha , aqui voce nao perde tempo na hora de aposta e criar seus jogos estantaneamente fazendo o acerto de tudo ", "08-fev", false));
        return mensagensNaolidas;
    }
    private void mensagensFiltradas(String searchEditText) {
        List<MessagePush> mensagensFiltradas = new ArrayList<>();

        if (searchEditText.isEmpty()) {
            mensagensFiltradas.addAll(mensagensList);
        } else {
            for (MessagePush message : mensagensList) {
                if (message.getTitulo().toLowerCase().contains(searchEditText.toLowerCase()) ||
                        message.getConteudo().toLowerCase().contains(searchEditText.toLowerCase())) {
                    mensagensFiltradas.add(message);
                }
            }
            mensagempushAdapter.setMensagensList(mensagensFiltradas);
            mensagempushAdapter.notifyDataSetChanged();
        }

    }
}