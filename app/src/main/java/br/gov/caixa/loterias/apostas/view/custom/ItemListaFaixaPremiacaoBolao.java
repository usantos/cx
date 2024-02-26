package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

@EViewGroup(R.layout.item_lista_faixa_premiacao_bolao)
public class ItemListaFaixaPremiacaoBolao extends LinearLayout {

    @ViewById
    protected TextView faixaPremio, valorCota, valorTotal;
    @ViewById
    protected LinearLayout conteudoLinha;

    @AfterViews
    public void init() {
    }

    public void setLayout(FaixaPremiadaDTO faixaPremiadaDTO, int color) {
        conteudoLinha.setBackgroundColor(color);

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
        valorTotal.setText(ViewUtils.getMoedaFormat(faixaPremiadaDTO.getValorLiquido()));
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
