package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class MensagensPushActivity extends LoteriasAppActivity {

    private List<MessagePush> mensagensList;
    private List<MessagePush> mensagensFiltradas;
    private MensagempushAdapter mensagempushAdapter;
    private EditText searchEditText;

    private LinearLayoutManager layoutManager;
    private NestedScrollView scrollViewContentViewMensagens;
    private RecyclerView recyclerViewMensagens;
    private View progressLoadingMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_msg);
//        setaViews();
        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.titulo_central_mensagens)));

        Button btnVoltar = findViewById(R.id.btn_voltar);

        RecyclerView recyclerViewMensagens = findViewById(R.id.recyclerViewCentralMsg);
        EditText searchEditText = (EditText) findViewById(R.id.SearchEditText);
        //configura o RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMensagens.setLayoutManager(layoutManager);
//        scrollViewContentViewMensagens = findViewById(R.id.scrollViewContentViewPush);
//        searchEditText = findViewById(R.id.search_msgpush);
        mensagensList = new ArrayList<MessagePush>();
        mensagensFiltradas = new ArrayList<MessagePush>();
        mensagempushAdapter = new MensagempushAdapter(mensagensFiltradas);
        recyclerViewMensagens.setAdapter(mensagempushAdapter);
//        massa de dados teste
        mensagensList.add(new MessagePush("Titulo 1", "Conteudo da mensagem 1", false));
        mensagensList.add(new MessagePush("Titulo 2", "Conteudo da mensagem 2", false));
        mensagensList.add(new MessagePush("Titulo 3", "Conteudo da mensagem 3", false));
        mensagensList.add(new MessagePush("Titulo 4", "Conteudo da mensagem 4", false));
        mensagensList.add(new MessagePush("Titulo 5", "Conteudo da mensagem 5", false));
        mensagensList.add(new MessagePush("Titulo 6", "Conteudo da mensagem 6", true));
        mensagensList.add(new MessagePush("Titulo 7", "Conteudo da mensagem 8", true));
        mensagensList.add(new MessagePush("Titulo 8", "Conteudo da mensagem 9", true));
        mensagensList.add(new MessagePush("Titulo 9", "Conteudo da mensagem 10", true));
        mensagensList.add(new MessagePush("Titulo 10", "Conteudo da mensagem 11", true));
        mensagensList.add(new MessagePush("Titulo 11", "Conteudo da mensagem 12", true));

        mensagensFiltradas.addAll(mensagensList);
        mensagempushAdapter.notifyDataSetChanged();

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
    }

    private void mensagensFiltradas(String searchEditText) {
        mensagensFiltradas.clear();

        if (searchEditText.isEmpty()) {
            mensagensFiltradas.addAll(mensagensList);
        } else {
            for (MessagePush message : mensagensList) {
                if (message.getTitulo().toLowerCase().contains(searchEditText.toLowerCase()) ||
                        message.getConteudo().toLowerCase().contains(searchEditText.toLowerCase())) {
                    mensagensFiltradas.add(message);
                }
            }
            mensagempushAdapter.notifyDataSetChanged();
        }
    }
}


