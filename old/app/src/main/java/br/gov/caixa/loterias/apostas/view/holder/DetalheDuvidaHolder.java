package br.gov.caixa.loterias.apostas.view.holder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoHeight;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetalheDuvidaHolder extends LoteriasHolder<DuvidaDTO> {


	/*
	 * Valida a sintaxe de hostnames baseada nas regras tradicionais do RFC 1035,
	 * com a flexibilização para labels iniciados por números prevista no RFC 1123.
	 *
	 * A validação é sintática e não garante que o domínio exista ou esteja acessível.
	 */
	private static final Pattern HOSTNAME_VALIDO = Pattern.compile(
			"(?i)^(?=.{1,253}\\z)"
					+ "[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\."
					+ "(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)*"
					+ "[a-z](?:[a-z0-9-]{0,61}[a-z0-9])?\\z"
	);

	private LinearLayout layoutCabecalhoDuvidaDetalhe, layoutItemDetalheDuvida, layoutTextDetalhe;
	private TextView textDuvidaDetalhe, textDetalhe;


	public DetalheDuvidaHolder(View itemView) {
		super(itemView);
		layoutCabecalhoDuvidaDetalhe = itemView.findViewById(R.id.layoutCabecalhoDuvidaDetalhe);
		layoutItemDetalheDuvida = itemView.findViewById(R.id.layoutItemDetalheDuvida);
		layoutTextDetalhe = itemView.findViewById(R.id.layoutTextDetalhe);
		textDuvidaDetalhe = itemView.findViewById(R.id.textDuvidaDetalhe);
		textDetalhe = itemView.findViewById(R.id.textDetalhe);
	}

	@Override
	public void bind(DuvidaDTO item, int position) {
		if (position != 0) {
			layoutItemDetalheDuvida.setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
		}

		textDuvidaDetalhe.setText(item.getPergunta());
		configurarResposta(item.getResposta());

		if (this.layoutCabecalhoDuvidaDetalhe!= null) {
			this.layoutCabecalhoDuvidaDetalhe.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (layoutCabecalhoDuvidaDetalhe.isSelected()) {
						layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.FALSE);
						textDuvidaDetalhe.setTextSize(14);
						textDuvidaDetalhe.setTextColor(itemView.getContext().getResources().getColor(R.color.cinza));
						AnimacaoHeight.collapse(layoutTextDetalhe);

					} else {
						layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.TRUE);
						textDuvidaDetalhe.setTextSize(16);
						textDuvidaDetalhe.setTextColor(itemView.getContext().getResources().getColor(R.color.verdeazul));
						AnimacaoHeight.expand(layoutTextDetalhe);
					}
				}
			});
		}
	}

	/* Método para identificar links na resposta e aplicar a formatação desejada,
	*  incluindo cor azul e sublinhado, além de abrir o link em um navegador ao ser clicado.
	*  (equivalente a um hyperlink).
	*/
	private void configurarResposta(String resposta) {
		if (resposta == null || resposta.trim().isEmpty()) {
			textDetalhe.setText("");
			textDetalhe.setMovementMethod(null);
			textDetalhe.setLinksClickable(false);
			return;
		}

		SpannableString spannableResposta = new SpannableString(resposta);
		Matcher matcher = Patterns.WEB_URL.matcher(resposta);
		boolean temLink = false;

		//Percorre o conteúdo da resposta em busca de URLs e aplica a formatação desejada
		while (matcher.find()) {
			int inicio = matcher.start();
			int fim = matcher.end();
			String urlEncontrada = resposta.substring(inicio, fim);
			String urlParaAbrir = obterUrlSegura(urlEncontrada, inicio, resposta);

			if (urlParaAbrir != null) {
				temLink = true;

				// Aplicar cor azul ao URL
				spannableResposta.setSpan(
						new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.verdeazul)),
						inicio,
						fim,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				);

				// Aplicar ClickableSpan ao URL
				spannableResposta.setSpan(
						new ClickableSpan() {
							@Override
							public void onClick(@NonNull View widget) {
								final android.content.Context context = widget.getContext();
								DialogUtils.dialogConfirmar(
										context,
										context.getString(R.string.hyperlink_generico),
										new OnDialogBotaoListener() {
											@Override
											public void onButtonClick(DialogInterface dialog, int which) {
													Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlParaAbrir));
												if (intent.resolveActivity(context.getPackageManager()) != null) {
													context.startActivity(intent);
												}
											}
										}
								);
							}

							@Override
							public void updateDrawState(@NonNull TextPaint drawState) {
								super.updateDrawState(drawState);
								drawState.setUnderlineText(true);
							}
						},
						inicio,
						fim,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
				);
			}
		}

		textDetalhe.setText(spannableResposta);

		if (temLink) {
			textDetalhe.setMovementMethod(LinkMovementMethod.getInstance());
			textDetalhe.setLinksClickable(true);
			textDetalhe.setHighlightColor(Color.TRANSPARENT);
		} else {
			textDetalhe.setMovementMethod(null);
			textDetalhe.setLinksClickable(false);
		}
	}

	/**
	 * Valida URLs com esquema e domínios sem esquema.
	 * Domínios sem esquema são abertos via HTTPS, sem alterar o texto exibido.
	 */
	private String obterUrlSegura(String url, int inicio, String resposta) {
		// Evita transformar o domínio de um endereço de e-mail em link.
		if (inicio > 0 && resposta.charAt(inicio - 1) == '@') {
			return null;
		}

		Uri uri = Uri.parse(url);
		String scheme = uri.getScheme();
		// Se existe expressamente http ou https no conteúdo retorna a URL como está, pronta para abrir no navegador.
		if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
			String host = uri.getHost();
			return host != null && HOSTNAME_VALIDO.matcher(host).matches() ? url : null;
		}

		// Não permitir ftp://, file://, javascript:// ou qualquer outro esquema.
		if (scheme != null) {
			return null;
		}
		// Se não houver esquema, adiciona https:// e valida o hostname.
		String urlComHttps = "https://" + url;
		String host = Uri.parse(urlComHttps).getHost();

		return host != null && HOSTNAME_VALIDO.matcher(host).matches() ? urlComHttps : null;
	}
}
