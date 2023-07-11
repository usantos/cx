package br.gov.caixa.loterias.apostas.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class MensagensPushActivity extends LoteriasAppActivity {

    private List<MessagePush> mensagensList;
    private List<MessagePush> unreadMensagensList;
    private List<MessagePush> readMensagensList;
    private MensagempushAdapter mensagempushAdapter;
    private EditText searchEditText;
    private TextView mensagensVazioTextview;
    private RadioGroup radioGroupMensagens;
    private RadioButton rbTodas, rbNaoLidas;
    private GridLayoutManager layoutManager;
    private NestedScrollView scrollViewContentViewMensagens;
    private RecyclerView recyclerViewMensagens;
    private View progressLoadingMore;
    private Button buttonMsgExcluir;
    private Button buttonMsgLida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_msg);
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        recyclerViewMensagens = findViewById(R.id.recyclerViewCentralMsg);
        radioGroupMensagens = findViewById(R.id.rg_push);
        rbTodas = findViewById(R.id.rb_todas);
        rbNaoLidas = findViewById(R.id.rb_naolidas);
        buttonMsgExcluir = findViewById(R.id.btn_push_apagar);
        buttonMsgLida = findViewById(R.id.btn_push_msg);

        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.titulo_central_mensagens)));
        ImageView ibAjuda = findViewById(R.id.ib_duvidas);
        ibAjuda.setVisibility(View.GONE);
        Button btnVoltar = findViewById(R.id.btn_voltar);

        //Inicializa listas de mensagens
        mensagensList = gerarMsgTodasMock();

        unreadMensagensList = getUnreadmensagens();
        readMensagensList = getReadmensagens();

        //Define adaptador inicial
        mensagempushAdapter = new MensagempushAdapter(readMensagensList);
        recyclerViewMensagens.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMensagens.setAdapter(mensagempushAdapter);

        //ativa o swipe
        ItemTouchHelper itemTouchHelper;
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                readMensagensList.remove(position);
                mensagempushAdapter.notifyDataSetChanged();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewMensagens);

        radioGroupMensagens.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_todas) {
                    mensagempushAdapter.setMensagensList(readMensagensList);
                    mensagempushAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.rb_naolidas) {
                    mensagempushAdapter.setMensagensList(unreadMensagensList);
                    mensagempushAdapter.notifyDataSetChanged();
                }
            }
        });

        buttonMsgExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mensagempushAdapter.getPositionLast();
                if (NO_POSITION != -1) {
                    readMensagensList.remove(position);
                    mensagempushAdapter.notifyDataSetChanged();
                }
            }
        });
        buttonMsgLida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = NO_POSITION;

                mensagempushAdapter.notifyDataSetChanged();
            }
        });
//        rbTodas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mensagempushAdapter.setMensagensList(mensagensList);
//                    mensagempushAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//        rbNaoLidas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mensagempushAdapter.setMensagensList(unreadMensagensList);
//                    mensagempushAdapter.notifyDataSetChanged();
//                }
//            }
//        });
        //        radioGroupMensagens.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rb_todas:
//                        filterMessages(false);
//                        break;
//                    case R.id.rb_naolidas:
//                        filterMessages(true);
//                        break;
//                }
//            }
//        });
//
//        rbTodas.setOnClickListener(v -> mensagempushAdapter.setMensagensList(readMensagensList)
//        );
//
//        rbNaoLidas.setOnClickListener(v -> mensagempushAdapter.setMensagensList(unreadMensagensList));
//        mensagempushAdapter.notifyDataSetChanged();

//        rbTodas.setOnClickListener(v -> {
//            mensagempushAdapter.setMensagensList(mensagensList);
//        });
//
//        rbNaoLidas.setOnClickListener(v -> {
//            mensagempushAdapter.setMensagensList(unreadmensagensList);
//        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mensagempushAdapter.mensagensFiltradas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });


    }


    private List<MessagePush>  getUnreadmensagens() {
        List<MessagePush> unreadMessages = new ArrayList<>();
        for (MessagePush message : mensagensList) {
            if (!message.getRead()) {
                unreadMessages.add(message);
            }
        }
        return unreadMessages;
    }

    private List<MessagePush> getReadmensagens() {
        List<MessagePush> readMessages = new ArrayList<>();
        for (MessagePush message : mensagensList) {
            if (message.getRead()) {
                readMessages.add(message);
            }
        }
        return readMessages;
    }


//    private void checarEstadoVazio() {
//        if (mensagempushAdapter.getItemCount() == 0) {
//            Toast.makeText(MensagensPushActivity.this, "Lista de mensagens está vazia", Toast.LENGTH_LONG).show();
//        }
//    }

    private List<MessagePush> gerarMsgTodasMock() {
        mensagensList = new ArrayList<>();
//        massa de dados mensagens lidas
        mensagensList.add(new MessagePush(R.drawable.ic_pagamentos, 1, "Pagamentos", "Aproveite e aposte ! este item dos pagamentos , aproveite aposte , aproveite aposte, aproveite aposte, aproveite aposte", "01-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_logo_money, 2, "Lembretes de sorte", "Aproveite e aposte ! , estou continuando a mensagem para ver como fica o aproveite e aposte ", "02-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_push_dicas_news, 4, "Dicas e Novidades", "Aproveite e aposte ! , estou alogando a mensagem para ver como fica o texto até o final ", "03-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_push_resultados, 3, "Resultados disponiveis", "Aproveite e aposte ! esta mensagem fala sobre resultados disponiveis, aqui voce vai ganhar link para resultados disponiveis, aqui vai botao de resultados", "01-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_push_alertas_seguranca, 5, "Alerta de segurança", "Conteudo de alerta de segurança da mensagem 5, esta msg fala sobre segurança no app, informacoes de como fazer a seguranca no seu celular e dar tudo o que puder", "01-fev", true));
        mensagensList.add(new MessagePush(R.drawable.ic_teimosinha, 0, "Teimosinha", "Conteudo da mensagem Teimosinha, fala sobre tudo o que puder, mostrando as vantagens da teimosinha a todo o vapor", "01-fev", false));
        mensagensList.add(new MessagePush(R.drawable.icon_trevo, 0, "Trevo", "Conteudo da Trevo, aqui estou mostrando as vantagens que o Trevo da sorte pode trazer para os clientes ", "09-fev", false));
        mensagensList.add(new MessagePush(R.drawable.icon_loteria, 0, "Loteria ", "Conteudo da Loteria, aqui vamos na mensagem das loterias e estamos demonstrando o que fazer nesta loteria da CEF", "06-fev", false));
        mensagensList.add(new MessagePush(R.drawable.icon_add_carrinho, 0, "Adicione este combo", "Conteudo da mensagem dos combos , aqui vamos descrever como mostrar o combo das atividades estamos fazendo tudo o que puder", "03-fev", false));
        mensagensList.add(new MessagePush(R.drawable.icon_data_megasena, 0, "Megasena data", "Conteudo da mensagem, acumulou a nova MEGASENA , agora vamos mostrar o prêmio milionário e sua chance de mostrar ", "02-fev", false));
        mensagensList.add(new MessagePush(R.drawable.icon_surpresinha, 0, "Surpresinha", "Conteudo da mensagem surpresinha , aqui voce nao perde tempo na hora de aposta e criar seus jogos estantaneamente fazendo o acerto de tudo ", "08-fev", false));
        return mensagensList;
    }
    }