package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ItemListaFaixaPremiacaoBolao extends LinearLayout {

    private boolean alreadyInflated = false;

    private TextView faixaPremio, valorCota, valorTotal;
    private LinearLayout conteudoLinha;

    public static ItemListaFaixaPremiacaoBolao build(Context context) {
        ItemListaFaixaPremiacaoBolao instance = new ItemListaFaixaPremiacaoBolao(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_lista_faixa_premiacao_bolao, this);
        }
        super.onFinishInflate();
        init();
    }

    public void init() {
        this.faixaPremio = findViewById(R.id.faixaPremio);
        this.valorCota = findViewById(R.id.valorCota);
        this.valorTotal = findViewById(R.id.valorTotal);
        this.conteudoLinha = findViewById(R.id.conteudoLinha);
    }

    public void setLayout(FaixaPremiadaDTO faixaPremiadaDTO, int colorBack, int colorText, int position, BigDecimal valorLiquidoPremio) {
        conteudoLinha.setBackgroundColor(colorBack);
        faixaPremio.setTextColor(colorText);
        valorCota.setTextColor(colorText);
        valorTotal.setTextColor(colorText);

        if (faixaPremiadaDTO.getNome() != null && !faixaPremiadaDTO.getNome().isEmpty()){
            faixaPremio.setText(faixaPremiadaDTO.getNome());
            faixaPremio.setTextSize(10);
        } else {
            faixaPremio.setText("-");
        }

        if (faixaPremiadaDTO.getValorPremioCota() != null){
            valorCota.setText(ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorPremioCota()));
        } else if (faixaPremiadaDTO.getValorLiquido() != null){
            valorCota.setText(ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorLiquido()));
        } else {
            valorCota.setText("-");
        }
        if (position == 0) {
            valorTotal.setText(ViewUtils.getMoedaFormat(valorLiquidoPremio));
        }
    }

    public ItemListaFaixaPremiacaoBolao(Context context) {
        super(context);
    }

    public ItemListaFaixaPremiacaoBolao(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemListaFaixaPremiacaoBolao(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
