package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.MessagePush;
import br.gov.caixa.loterias.apostas.model.enums.CategoriaMessagePushEnum;

public class MensagemPushModel {

	private Activity activity;

	public MensagemPushModel(Activity activity) {
		this.activity = activity;
	}

	public List<MessagePush> getAllMensagensMock() {
		List<MessagePush> mensagensList = new ArrayList<>();

		Long id = 1L;

		mensagensList.add(new MessagePush(id, R.drawable.ic_pagamentos, CategoriaMessagePushEnum.PAGAMENTOS_RESGATES, "Pagamentos", "Aproveite e aposte ! este item dos pagamentos , aproveite aposte , aproveite aposte, aproveite aposte, aproveite aposte", "hoje", false));
		mensagensList.add(new MessagePush(id++, R.drawable.ic_logo_money, CategoriaMessagePushEnum.LEMBRETES_SORTE, "Lembretes de sorte", "Aproveite e aposte ! , estou continuando a mensagem para ver como fica o aproveite e aposte ", "ontem", false));
		mensagensList.add(new MessagePush(id++, R.drawable.ic_push_resultados, CategoriaMessagePushEnum.RESULTADOS_DISPONIVEIS, "Resultados disponiveis", "Aproveite e aposte ! esta mensagem fala sobre resultados disponiveis, aqui voce vai ganhar link para resultados disponiveis, aqui vai botao de resultados", "25 Mar", false));
		mensagensList.add(new MessagePush(id++, R.drawable.ic_push_dicas_news, CategoriaMessagePushEnum.DICAS_NOVIDADES, "Dicas e Novidades", "Aproveite e aposte ! , estou alogando a mensagem para ver como fica o texto até o final ", "03 Mar", false));
		mensagensList.add(new MessagePush(id++, R.drawable.ic_push_alertas_seguranca, CategoriaMessagePushEnum.ALERTA_SEGURANCA, "Alerta de segurança", "Conteudo de alerta de segurança da mensagem 5, esta msg fala sobre segurança no app, informacoes de como fazer a seguranca no seu celular e dar tudo o que puder", "01 Fev", false));

		return mensagensList;
	}

	public void deleteMensagem(List<MessagePush> mensagensList, MessagePush messagePush) {
		if (mensagensList != null && messagePush != null){
			mensagensList.remove(messagePush);
		}
	}

	public void marcarComoLida(List<MessagePush> mensagensList, MessagePush mensagem) {
		if (mensagensList != null && mensagem != null){
			for (MessagePush push : mensagensList){
				if (push.getId().equals(mensagem.getId())){
					mensagem.setRead(true);
					break;
				}
			}
		}
	}
}
