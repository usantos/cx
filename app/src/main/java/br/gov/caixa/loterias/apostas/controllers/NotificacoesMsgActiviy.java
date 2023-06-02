package br.gov.caixa.loterias.apostas.controllers;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

class NotificacoesMsgActivity extends Activity {

    private RecyclerView recyclerViewCentral;
    private MensagempushAdapter messageAdapter;
    private List<String> mensagens;
    private List<String> mensagensNaolidas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.central_mensagens_push);
        recyclerViewCentral = findViewById(R.id.recyclerViewCentralMsg);
        recyclerViewCentral.setLayoutManager(new LinearLayoutManager(this));

        mensagens = new ArrayList<>();
        mensagensNaolidas = new ArrayList<>();

        mensagens.add("Mensagem 1");
        mensagens.add("Mensagem 2");
        mensagens.add("Mensagem 3");
        mensagens.add("Mensagem 4");
        mensagens.add("Mensagem 5");
        mensagens.add("Mensagem 6");

        mensagensNaolidas.add("Mensagem 7");
        mensagensNaolidas.add("Mensagem 8");
        mensagensNaolidas.add("Mensagem 9");
        mensagensNaolidas.add("Mensagem 10");
        mensagensNaolidas.add("Mensagem 11");
        mensagensNaolidas.add("Mensagem 12");

        messageAdapter = new MensagempushAdapter(mensagens, mensagensNaolidas);


    }
}
