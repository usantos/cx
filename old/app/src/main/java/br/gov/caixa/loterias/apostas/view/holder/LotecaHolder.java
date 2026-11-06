package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.utils.DescricaoLoteca;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;

public class LotecaHolder extends LoteriasHolder<ParametroPartida> {

	private static final float ELEVATION_SELECTED = 2f;
	private static final float ELEVATION_DEFAULT = 0f;

	public final TextView tvTitulo;
	public final View timeUm;
	public final View timeDois;
	public final View empate;
	public final ImageView imgTimeUm;
	public final ImageView imgTimeDois;
	public final TextView txtTimeUm;
	public final TextView txtTimeDois;
	public final TextView descTimeUm;
	public final TextView descTimeDois;
	public final TextView txtEmpate;
	public final ImageView imgEmpate;
	private LinearLayout containerJogos;

	public LotecaHolder(View itemView) {
		super(itemView);
		tvTitulo = itemView.findViewById(R.id.tv_titulo_jogo);
		ViewCompat.setAccessibilityHeading(tvTitulo,true);
		timeUm = itemView.findViewById(R.id.time_um);
		timeDois = itemView.findViewById(R.id.time_dois);
		empate = itemView.findViewById(R.id.time_empate);
		aplicarAccessibilityCheck(timeUm);
		aplicarAccessibilityCheck(timeDois);
		aplicarAccessibilityCheck(empate);
		containerJogos = itemView.findViewById(R.id.containerJogos);
		configAcessibilidadeContainer(containerJogos);
		imgTimeUm = itemView.findViewById(R.id.img_time_um);
		imgTimeDois = itemView.findViewById(R.id.img_time_dois);
		txtTimeUm = itemView.findViewById(R.id.txt_time_um);
		txtEmpate = itemView.findViewById(R.id.txt_empate);
		txtTimeDois = itemView.findViewById(R.id.txt_time_dois);
		descTimeUm = itemView.findViewById(R.id.desc_time_um);
		descTimeDois = itemView.findViewById(R.id.desc_time_dois);
		imgEmpate = itemView.findViewById(R.id.img_empate);
	}

	private void configAcessibilidadeContainer(View container) {
		ViewCompat.setAccessibilityDelegate(container, new AccessibilityDelegateCompat() {
			@Override
			public void onInitializeAccessibilityNodeInfo(
					@NonNull View host,
					@NonNull AccessibilityNodeInfoCompat info
			) {
				super.onInitializeAccessibilityNodeInfo(host, info);

				info.setCollectionInfo(
						AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(
								1, // 1 linha
								3, // 3 colunas
								false,
								AccessibilityNodeInfoCompat.CollectionInfoCompat.SELECTION_MODE_MULTIPLE
						)
				);
			}
		});
	}
	private void aplicarAccessibilityCheck(View view) {
		ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() {
			@Override
			public void onInitializeAccessibilityNodeInfo(
					@NonNull View host,
					@NonNull AccessibilityNodeInfoCompat info
			) {
				super.onInitializeAccessibilityNodeInfo(host, info);

				info.setClassName(android.widget.CheckBox.class.getName());
				info.setClickable(true);
				info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
			}
		});
	}
	@Override
	public void bind(ParametroPartida item, int position) {
		Context context = itemView.getContext();

		bindTitulo(position,item);
		bindTextos(item);
		bindEscudos(context, item);
		bindEstadoVisual(context, item);
	}

	private void bindTitulo(int position, ParametroPartida item) {
		String jogoTitle = String.format(Locale.getDefault(), "Jogo %02d", position + 1);

		//TODO: VOLTAR COM A LEGENDA//
		if(item != null) {
			jogoTitle += DescricaoLoteca.descricaoPartida(item);
		}

		tvTitulo.setText(jogoTitle);
	}
	private void bindTextos(ParametroPartida item) {
		txtTimeUm.setText(item.getEquipe1().getNome());
		txtTimeDois.setText(item.getEquipe2().getNome());
		txtEmpate.setText(R.string.label_empate);

		bindDescricaoEquipe(descTimeUm, item.getEquipe1());
		bindDescricaoEquipe(descTimeDois, item.getEquipe2());
	}

	private void bindDescricaoEquipe(TextView view, ParametroEquipe equipe) {
		if (equipe != null && !equipe.isSelecionado() && Boolean.FALSE.equals(equipe.getIndicadorSelecao())) {
			view.setText(Utils.getUfTime(equipe));
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}


	private void setColorSeNaoSelecionado(TextView view, ParametroEquipe equipe, int cor) {
		if (equipe != null && Boolean.FALSE.equals(equipe.getIndicadorSelecao())) {
			view.setTextColor(cor);
		}
	}

	private void bindEscudos(Context context, ParametroPartida item) {
		EscudoEquipeUtil.setEscudo(context, item.getEquipe1(), imgTimeUm);
		EscudoEquipeUtil.setEscudo(context, item.getEquipe2(), imgTimeDois);
	}

	public void bindEstadoVisual(Context context, ParametroPartida item) {
		boolean selecionadoTimeUm = item.getEquipe1().isSelecionado();
		boolean selecionadoTimeDois = item.getEquipe2().isSelecionado();
		boolean selecionadoEmpate = item.isEmpate();

		timeUm.setSelected(selecionadoTimeUm);
		timeDois.setSelected(selecionadoTimeDois);
		empate.setSelected(selecionadoEmpate);

		int corSelecionado = ContextCompat.getColor(context, R.color.colorAccent);
		int corNaoSelecionado = ContextCompat.getColor(context, R.color.cinza);

		aplicarCorTexto(txtTimeUm, descTimeUm, item.getEquipe1(), selecionadoTimeUm, corSelecionado, corNaoSelecionado);
		aplicarCorTexto(txtTimeDois, descTimeDois, item.getEquipe2(), selecionadoTimeDois, corSelecionado, corNaoSelecionado);

		txtEmpate.setTextColor(selecionadoEmpate ? corSelecionado : corNaoSelecionado);
		imgEmpate.setImageTintList(
				ColorStateList.valueOf(selecionadoEmpate ? corSelecionado : corNaoSelecionado)
		);

		aplicarElevation(timeUm, selecionadoTimeUm);
		aplicarElevation(timeDois, selecionadoTimeDois);
		aplicarElevation(empate, selecionadoEmpate);
	}

	private void aplicarCorTexto(
			TextView titulo,
			TextView descricao,
			ParametroEquipe equipe,
			boolean selecionado,
			int corSelecionado,
			int corNaoSelecionado
	) {
		int cor = selecionado ? corSelecionado : corNaoSelecionado;
		titulo.setTextColor(cor);
		setColorSeNaoSelecionado(descricao,equipe,cor);
	}

	private void aplicarElevation(View view, boolean selecionado) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			view.setTranslationZ(selecionado ? ELEVATION_SELECTED : ELEVATION_DEFAULT);
		}
	}
}