package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
//        setaViews();
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        recyclerViewMensagens = findViewById(R.id.recyclerViewCentralMsg);
        tabLayout = findViewById(R.id.tab_layout);
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.titulo_central_mensagens)));
        Button btnVoltar = findViewById(R.id.btn_voltar);
        mensagensVazioTextview = findViewById(R.id.mensagensVazioTextview);

        //Inicializa listas de mensagens

        mensagensList = gerarMsgTodas();
        mensagensNaolidas = gerarMsgNaoLidas();

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
            mensagensVazioTextview.setVisibility(View.VISIBLE);
        } else {
            mensagensVazioTextview.setVisibility(View.GONE);
        }
    }

    private List<MessagePush> gerarMsgTodas() {
        mensagensList = new ArrayList<>();
//        massa de dados mensagens lidas
        mensagensList.add(new MessagePush(R.drawable.ic, "Pagamentos", "Aproveite e aposte !", "01-fev"));
        mensagensList.add(new MessagePush(R.drawable.ic,"Lembretes de sorte", "Aproveite e aposte !", "02-fev"));
        mensagensList.add(new MessagePush(R.drawable.ic,"Dicas e Novidades", "Aproveite e aposte !", "03-fev"));
        mensagensList.add(new MessagePush(R.drawable.ic,"Resultados disponiveis", "Aproveite e aposte !", "01-fev"));
        mensagensList.add(new MessagePush(R.drawable.ic,"Alerta de segurança", "Conteudo da mensagem 5", "01-fev"));
    return mensagensList;
    }
    private List<MessagePush> gerarMsgNaoLidas() {
        mensagensNaolidas = new ArrayList<>();
        //      massa de dados mensagens nao lidas
        mensagensNaolidas.add(new MessagePush(R.drawable.ic,"Titulo 6", "Conteudo da mensagem 6", "01-fev"));
        mensagensNaolidas.add(new MessagePush(R.drawable.ic,"Titulo 7", "Conteudo da mensagem 8", "09-fev"));
        mensagensNaolidas.add(new MessagePush(R.drawable.ic,"Titulo 8", "Conteudo da mensagem 9","06-fev"));
        mensagensNaolidas.add(new MessagePush(R.drawable.ic, "Titulo 9", "Conteudo da mensagem 10", "03-fev"));
        mensagensNaolidas.add(new MessagePush(R.drawable.ic,"Titulo 10", "Conteudo da mensagem 11", "02-fev"));
        mensagensNaolidas.add(new MessagePush(R.drawable.ic, "Titulo 11", "Conteudo da mensagem 12", "08-fev"));
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



