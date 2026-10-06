package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.FiltroMessagePushEnum;
import br.gov.caixa.loterias.apostas.model.model.MensagemPushModel;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnMensagemPushListener;

public class MensagensPushActivity extends LoteriasAppActivity {

    private List<MessagePush> mensagensList;
    private MensagempushAdapter adapter;
    private EditText searchEditText;
    private RadioGroup radioGroupMensagens;
    private RecyclerView recyclerViewMensagens;
    private Button buttonMsgExcluir ;
    private Button buttonMsgLida;
    private TextView listaVazia;

    private MensagemPushModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_msg);

        configToolbar(R.id.toolbar);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.titulo_central_mensagens)));
        ImageView ibAjuda = findViewById(R.id.ib_duvidas);
        ibAjuda.setVisibility(View.GONE);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerViewMensagens = findViewById(R.id.recyclerViewCentralMsg);
        radioGroupMensagens = findViewById(R.id.rg_push);
        buttonMsgExcluir = findViewById(R.id.btn_push_apagar);
        buttonMsgLida = findViewById(R.id.btn_push_msg);
        listaVazia = findViewById(R.id.lista_vazia);

        model = new MensagemPushModel(this);

        //Inicializa listas de mensagens
        mensagensList = model.getAllMensagensMock();

        //Define adaptador inicial
        recyclerViewMensagens.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensagempushAdapter(mensagensList, OnMensagemPushLIstener());
        recyclerViewMensagens.setAdapter(adapter);
        checarEstadoVazio();

        radioGroupMensagens.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_todas) {
                adapter.setFiltro(FiltroMessagePushEnum.TODAS);
            } else if (checkedId == R.id.rb_naolidas) {
                adapter.setFiltro(FiltroMessagePushEnum.NAO_LIDAS);
            }
            adapter.recarregaLista();
            checarEstadoVazio();
        });

        buttonMsgExcluir.setOnClickListener(v ->
                DialogUtils.dialogSim(
                        MensagensPushActivity.this,
                        getResources().getString(R.string.msg_push_excluir),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                adapter.apagarTodas();
                                listaVazia.setVisibility(View.VISIBLE);
                                recyclerViewMensagens.setVisibility(View.GONE);
                            }
                        }
                )
        );

        buttonMsgLida.setOnClickListener(v ->
                DialogUtils.dialogSim(
                        MensagensPushActivity.this,
                        getResources().getString(R.string.msg_push_marcar_lidas),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                adapter.marcarTodasComoLidas();
                            }
                        }
                )
        );

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.mensagensFiltradas(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.mensagensFiltradas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    private OnMensagemPushListener OnMensagemPushLIstener() {
        return new OnMensagemPushListener() {
            @Override
            public void clickItem(int position) {
                adapter.fecharTodosExceto(position);
                adapter.mudarVisibilidade(position);
                model.marcarComoLida(mensagensList, adapter.getMensagem(position));
            }

            @Override
            public void delete(int position) {

                DialogUtils.dialogSim(
                        MensagensPushActivity.this,
                        MensagensPushActivity.this.getResources().getString(R.string.msg_push_excluir_item),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                MessagePush messagePush = adapter.getItemByPosition(position);
                                adapter.deleteMensagem(position);
                                model.deleteMensagem(mensagensList, messagePush);
                            }
                        }

                );
            }

            @Override
            public void read(int position) {
                DialogUtils.dialogSim(
                        MensagensPushActivity.this,
                        MensagensPushActivity.this.getResources().getString(R.string.msg_push_marcar_lida_item),
                        new OnDialogBotaoListener() {
                            @Override
                            public void onButtonClick(DialogInterface dialog, int which) {
                                adapter.marcaMensagemLida(position);
                                model.marcarComoLida(mensagensList, adapter.getItemByPosition(position));
                            }
                        }
                );
            }

            @Override
            public void share(int position) {
                MessagePush messagePush = adapter.getItemByPosition(position);
                if (messagePush != null){
                    shareMessage(messagePush);
                }
            }
        };
    }


    private void shareMessage(MessagePush messagePush) {
        String textToShare = messagePush.getTitulo() + " <br>" + messagePush.getConteudo();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(shareIntent, "Compartilhar mensagem"));
    }

    private void checarEstadoVazio() {
        if (adapter.isListaVazia()) {
            recyclerViewMensagens.setVisibility(View.GONE);
            if (adapter.getFiltro() == FiltroMessagePushEnum.TODAS) {
                listaVazia.setText(getResources().getString(R.string.pesquisa_push_vazia));
            } else {
                listaVazia.setText(getResources().getString(R.string.pesquisa_push_nao_lida));
            }
            listaVazia.setVisibility(View.VISIBLE);
        } else {
            listaVazia.setVisibility(View.GONE);
            recyclerViewMensagens.setVisibility(View.VISIBLE);
        }
    }

}