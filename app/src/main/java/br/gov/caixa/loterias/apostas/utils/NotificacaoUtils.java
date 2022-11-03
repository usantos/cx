package br.gov.caixa.loterias.apostas.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.UltimaNotificacaoSingleton;
import br.gov.caixa.loterias.apostas.view.listener.OnNotificacaoListener;

public class NotificacaoUtils {

	public static PopupWindow showNotificacao(Context context, View viewNotification, AppBarLayout navigationBarListaModalidade, OnNotificacaoListener listener){
		LayoutInflater li = LayoutInflater.from(context);
		viewNotification = li.inflate(R.layout.nova_popup_notificacao, null);
		ImageView img               = viewNotification.findViewById(R.id.imagem_notificacao);
		TextView  tituloNotificacao = viewNotification.findViewById(R.id.titulo_notificacao);
		TextView  corpoNotificacao  = viewNotification.findViewById(R.id.descricao_notificacao);
		Button    btnNotificacao    = viewNotification.findViewById(R.id.fechar_notificacao);


		try {
			final byte[] decodedBytes  = Base64.decode(UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getImagem(), Base64.DEFAULT);
			Bitmap       decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
			img.setImageBitmap(decodedBitmap);
		}catch (Exception e){}

		tituloNotificacao.setText(UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getTitulo());
		corpoNotificacao.setText(UltimaNotificacaoSingleton.getInstance().getUltimaNoficacao().getConteudo());
		btnNotificacao.setOnClickListener(v -> listener.removePopup());

		PopupWindow popupNotification = new PopupWindow(viewNotification, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		popupNotification.showAtLocation(navigationBarListaModalidade, Gravity.CENTER, 0, 0);
		return popupNotification;
	}

}
