package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroEquipe;
import caixa.gov.br.loterias.utils.interfaces.LotogolListener;
import caixa.gov.br.loterias.view.adapter.NumerosLotogolAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

/**
 * Created by joafilho on 28/12/2017.
 */
@EViewGroup(R.layout.item_lotogol)
public class ItemEquipeViewLotogol extends LinearLayout {

    @ViewById
    ImageView itemImage;

    @ViewById
    TextView itemTitle;

    @ViewById
    ExpandableHeightGridView gridViewPlacarTresColunas, gridViewPlacarDuasColunas;

    private ParametroEquipe parametroEquipe;
    private LotogolListener listerner;

    private List<String> placarListUm;
    private List<String> placarListDois;

    private NumerosLotogolAdapter numerosLotogolAdapterListUm;
    private NumerosLotogolAdapter numerosLotogolAdapterListDois;

    private View selecionadoAnterior;

    private static final String VALORES_PLACAR_LINHA_UM = "0_1_2";
    private static final String VALORES_PLACAR_LINHA_DOIS = "3_+";

    @AfterViews
    protected void init() {
        placarListUm = new ArrayList<>();
        placarListDois = new ArrayList<>();
        criarListaPlacar();
    }

    public ItemEquipeViewLotogol(Context context) {
        super(context);
    }

    public ItemEquipeViewLotogol(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView getItemImage() {
        return itemImage;
    }

    public TextView getItemTitle() {
        return itemTitle;
    }

    public void setParametroEquipe(ParametroEquipe parametroEquipe) {
        this.parametroEquipe = parametroEquipe;
    }

    public void setListerner(LotogolListener listerner) {
        this.listerner = listerner;
    }

    private void criarListaPlacar() {
        String[] arrayValor = VALORES_PLACAR_LINHA_UM.split("_");
        for (int i = 0; i < arrayValor.length; i++) {
            placarListUm.add(arrayValor[i]);
        }
        numerosLotogolAdapterListUm = new NumerosLotogolAdapter(getContext(), placarListUm);
        gridViewPlacarTresColunas.setAdapter(numerosLotogolAdapterListUm);
        gridViewPlacarTresColunas.setExpanded(true);
        gridViewPlacarTresColunas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickItem(adapterView, view);
            }
        });

        arrayValor = VALORES_PLACAR_LINHA_DOIS.split("_");
        for (int i = 0; i < arrayValor.length; i++) {
            placarListDois.add(arrayValor[i]);
        }

        numerosLotogolAdapterListDois = new NumerosLotogolAdapter(getContext(), placarListDois);
        gridViewPlacarDuasColunas.setAdapter(numerosLotogolAdapterListDois);
        gridViewPlacarDuasColunas.setExpanded(true);
        gridViewPlacarDuasColunas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickItem(adapterView, view);
            }
        });
    }

    private void clickItem(AdapterView<?> adapterView, View view) {
        LinearLayout layoutCircle = (LinearLayout) view.findViewById(R.id.linearLayoutNumerosPlacar);
        TextView valorPlacar = (TextView) view.findViewById(R.id.tituloPlacar);

        if (valorPlacar.getText().toString().equals(parametroEquipe.getPlacar())) {
            parametroEquipe.setPlacar(StringUtils.EMPTY);
            layoutCircle.setBackgroundResource(R.drawable.custom_border_cinza_lotogol_circle);
            valorPlacar.setTextColor(getResources().getColor(R.color.lotogolEscuroToolBar));
            selecionadoAnterior = null;
        } else {
            if (StringUtils.isNotEmpty(parametroEquipe.getPlacar())) {
                parametroEquipe.setPlacar(valorPlacar.getText().toString());
                tirarSelecaoAnterior();
                layoutCircle.setBackgroundResource(R.drawable.circle_numeros_azul_escuro_toolbar);
                valorPlacar.setTextColor(getResources().getColor(R.color.branco));
                selecionadoAnterior = view;
            } else {
                parametroEquipe.setPlacar(valorPlacar.getText().toString());
                layoutCircle.setBackgroundResource(R.drawable.circle_numeros_azul_escuro_toolbar);
                valorPlacar.setTextColor(getResources().getColor(R.color.branco));
                selecionadoAnterior = view;
            }
        }
        listerner.partidaSelecionada();
    }

    public void tirarSelecaoAnterior() {
//        if(selecionadoAnterior != null) {
            LinearLayout layoutCircle = (LinearLayout) selecionadoAnterior.findViewById(R.id.linearLayoutNumerosPlacar);
            TextView valorPlacar = (TextView) selecionadoAnterior.findViewById(R.id.tituloPlacar);

            layoutCircle.setBackgroundResource(R.drawable.custom_border_cinza_lotogol_circle);
            valorPlacar.setTextColor(getResources().getColor(R.color.lotogolEscuroToolBar));
//        }
    }
}
