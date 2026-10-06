package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;
import org.jetbrains.annotations.NotNull;

public class DezenaHolder extends LoteriasHolder<Dezena> implements  View.OnClickListener {

	private ToggleButton txtNumero;

	private Context context;
	private DezenaConfig config;
	private OnItemClickListener listener;

	public DezenaHolder(View itemView, Context context, DezenaConfig config, OnItemClickListener listener) {
		super(itemView);
		this.context = context;
		this.config = config;
		this.listener = listener;

		txtNumero = itemView.findViewById(R.id.tv_item_numero);
	}

	@Override
	public void bind(Dezena dezena, int position) {
		atualizaShape(dezena);

		if(config.isLabelDezena()){
			if (dezena.getValue().length() == 1) {
				txtNumero.setText(String.format("0%s", dezena.getLabel()));
				txtNumero.setTextOn(String.format("0%s", dezena.getLabel()));
				txtNumero.setTextOff(String.format("0%s", dezena.getLabel()));
			} else {
				txtNumero.setText(dezena.getLabel());
				txtNumero.setTextOn(dezena.getLabel());
				txtNumero.setTextOff(dezena.getLabel());
			}
		}else {
			txtNumero.setText(dezena.getLabel());
			txtNumero.setTextOn(dezena.getLabel());
			txtNumero.setTextOff(dezena.getLabel());
		}
		txtNumero.setTypeface(ViewUtils.getFontCaixaStdBold(context));

		//txtNumero.setOnCheckedChangeListener(null);//Acessibilidade
		txtNumero.setButtonDrawable(null);//Acessibilidade - Remove o comportamento visual padrao do ToggleButton

		if (config.isClickable()){
			txtNumero.setOnClickListener(this);
			txtNumero.setClickable(true);//Acessibilidade
			txtNumero.setChecked(dezena.isSelected());//Acessibilidade
			configurarAcessibilidadeVolante();
		}else {
			txtNumero.setOnClickListener(null);
			txtNumero.setClickable(false); //Acessibilidade
		}

	}

	private void atualizaShape(Dezena dezena) {
		Drawable shape;
		if (dezena.isSelected()) {
			if (config.isShapePadrao()) {
				shape = VectorUtils.getShape(config.getDrawableSelecionado(), config.getColor());
				if (config.getShapeConfig().getTextoSelecionadoColor() > 0){
					txtNumero.setTextColor(ContextCompat.getColor(context, config.getShapeConfig().getTextoSelecionadoColor()));
				} else {
					txtNumero.setTextColor(ContextCompat.getColor(context, R.color.branco));
				}
			}else {
				shape = AppCompatResources.getDrawable(context, config.getDrawableSelecionado());
				//txtNumero.setTextColor(config.getColor());
				txtNumero.setTextColor(ContextCompat.getColor(context, config.getShapeConfig().getTextoSelecionadoColor()));
			}
		}  else {
			if (!config.isShapePadrao()
					&& config.getDrawable() == R.drawable.ic_trevo) {

				shape = AppCompatResources.getDrawable(
						context,
						config.getDrawable()
				);

				txtNumero.setTextColor(
						ContextCompat.getColor(
								context,
								config.getShapeConfig().getTextoColor()
						)
				);

			}
			else{
				shape = VectorUtils.getShape(config.getDrawable(), config.getColorDefault());
				txtNumero.setTextColor(ContextCompat.getColor(context, config.getColorDefault()));
			}
		}
		txtNumero.setBackground(shape);
	}

	public void atualizaDezena(Dezena dezena) {
		atualizaShape(dezena);
	}

	@Override
	public void onClick(View v) {
		listener.itemClick(this, getAbsoluteAdapterPosition());
	}

	private void configurarAcessibilidadeVolante(){

		ViewCompat.setAccessibilityDelegate(txtNumero, new AccessibilityDelegateCompat(){
			@Override
			public void onInitializeAccessibilityNodeInfo(@NotNull View host, @NonNull AccessibilityNodeInfoCompat info) {
				super.onInitializeAccessibilityNodeInfo(host, info);

				boolean isChecked = txtNumero.isChecked();
				String labelNumero = txtNumero.getText().toString();

				//Remove a verbalização do texto visível
				info.setText(null);

				info.setRoleDescription("Botão");
				info.setStateDescription(labelNumero + " " + (isChecked ? "Selecionado" : "Não selecionado"));

				AccessibilityNodeInfoCompat.AccessibilityActionCompat clickAction = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK;
				String clickActionVerbalizacao = isChecked ?  "Desselecionar" : "Selecionar" ;

				AccessibilityNodeInfoCompat.AccessibilityActionCompat clickPadrao = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(clickAction.getId(), clickActionVerbalizacao);
				info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
				info.addAction(clickPadrao);
			}
		});
	}

}
