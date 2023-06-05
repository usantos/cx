package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class MensagensPushActivity extends AppCompatActivity {

    private List<MessagePush> mensagens;
    private List<MessagePush> mensagensFiltradas;
    private MensagempushAdapter mensagempushAdapter;
    private EditText pesquisaTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_msg);

        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_notificacoes)));
        Toolbar toolbar = findViewById(R.id.toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCentralMsg);
        pesquisaTexto = findViewById(R.id.pesquisaTextoPush);

        //configura o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mensagens = new ArrayList<MessagePush>();
        mensagensFiltradas = new ArrayList<MessagePush>();
        mensagempushAdapter = new MensagempushAdapter(mensagensFiltradas);
        recyclerView.setAdapter(mensagempushAdapter);


        mensagens.add(new MessagePush("Titulo 1","Conteudo da mensagem 1", false));

        mensagens.add(new MessagePush("Titulo 2","Conteudo da mensagem 2", false));
        mensagens.add(new MessagePush("Titulo 3","Conteudo da mensagem 2", false));
        mensagens.add(new MessagePush("Titulo 4","Conteudo da mensagem 2", false));
        mensagens.add(new MessagePush("Titulo 5","Conteudo da mensagem 2", false));
        mensagens.add(new MessagePush("Titulo 6","Conteudo da mensagem 2", false));

        mensagens.add(new MessagePush("Titulo 7","Conteudo da mensagem 7", true));
        mensagens.add(new MessagePush("Titulo 8","Conteudo da mensagem 8", true));
        mensagens.add(new MessagePush("Titulo 9","Conteudo da mensagem 9", true));
        mensagens.add(new MessagePush("Titulo 10","Conteudo da mensagem 10", true));
        mensagens.add(new MessagePush("Titulo 11","Conteudo da mensagem 11", true));
        mensagens.add(new MessagePush("Titulo 12","Conteudo da mensagem 12", true));

        mensagensFiltradas.addAll(mensagens);
        mensagempushAdapter.notifyDataSetChanged();

        pesquisaTexto.addTextChangedListener(new TextWatcher() {
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

            private void mensagensFiltradas(String searchText) {
                for (MessagePush message : mensagens) {
                    if (message.getTitulo().toLowerCase().contains(searchText.toLowerCase()) ||
                            message.getConteudo().toLowerCase().contains(searchText.toLowerCase())) {
                        mensagensFiltradas.add(message);
                    }
                }
            }
        });
    }
}