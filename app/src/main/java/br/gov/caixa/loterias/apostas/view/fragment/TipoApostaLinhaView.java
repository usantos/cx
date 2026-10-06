package br.gov.caixa.loterias.apostas.view.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TipoApostaLinhaAdapter;

public class TipoApostaLinhaView extends LinearLayout {

	public enum TriangleDirection {
		UP, DOWN, NONE
	}

	private RecyclerView recyclerView;
	private ImageView triangleUp;
	private ImageView triangleDown;
	private View flexboxContentContainer;

	public TipoApostaLinhaView(Context context) {
		super(context);
		init(context);
	}
	public TipoApostaLinhaView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public TipoApostaLinhaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_tipo_aposta_linha, this, true);
		recyclerView = findViewById(R.id.flexboxRecycleViewInternal);
		triangleUp = findViewById(R.id.triangle_up);
		triangleDown = findViewById(R.id.triangle_down);
		flexboxContentContainer = findViewById(R.id.flexbox_content_container);
	}

	public List<TipoApostaLinhaEnum> ordenarPorTexto(Context context, List<TipoApostaLinhaEnum> lista) {
		Collections.sort(lista, new Comparator<TipoApostaLinhaEnum>() {
			@Override
			public int compare(TipoApostaLinhaEnum o1, TipoApostaLinhaEnum o2) {
				String texto1 = context.getString(o1.getTextResId());
				String texto2 = context.getString(o2.getTextResId());
				return texto1.compareTo(texto2);
			}
		});
		return lista;
	}

	public void setupView(List<TipoApostaLinhaEnum> itemList, int backgroundColorResId, int textColorResId, int iconColorResId, TipoApostaLinhaView.TriangleDirection direction) {
		this.setupView(itemList, backgroundColorResId, textColorResId, iconColorResId, direction, false);
	}

	public void setupView(List<TipoApostaLinhaEnum> itemList, int backgroundColorResId, int textColorResId, int iconColorResId, TipoApostaLinhaView.TriangleDirection direction, boolean isBoald) {
		ViewCompat.setAccessibilityDelegate(recyclerView, new AccessibilityDelegateCompat(){
			@Override
			public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
				super.onInitializeAccessibilityNodeInfo(host, info);

				info.setCollectionItemInfo(null);
			}
		});

		flexboxContentContainer.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColorResId));

		FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getContext());
		flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
		flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
		recyclerView.setLayoutManager(flexboxLayoutManager);

		TipoApostaLinhaAdapter tipoApostaLinhaAdapter = new TipoApostaLinhaAdapter(getContext(), itemList, textColorResId, iconColorResId, isBoald);
		recyclerView.setAdapter(tipoApostaLinhaAdapter);

		setupTriangle(direction, backgroundColorResId);
	}

	private void setupTriangle(TipoApostaLinhaView.TriangleDirection direction, int backgroundColorRes) {
		if (direction == null) direction = TriangleDirection.NONE;

		int triangleColor = ContextCompat.getColor(getContext(), backgroundColorRes);

		switch (direction) {
			case UP:
				triangleUp.setVisibility(View.VISIBLE);
				triangleDown.setVisibility(View.GONE);
				triangleUp.setColorFilter(new PorterDuffColorFilter(triangleColor, PorterDuff.Mode.SRC_IN));
				break;
			case DOWN:
				triangleUp.setVisibility(View.GONE);
				triangleDown.setVisibility(View.VISIBLE);
				triangleDown.setColorFilter(new PorterDuffColorFilter(triangleColor, PorterDuff.Mode.SRC_IN));
				break;
			case NONE:
			default:
				triangleUp.setVisibility(View.GONE);
				triangleDown.setVisibility(View.GONE);
				break;
		}
	}
}