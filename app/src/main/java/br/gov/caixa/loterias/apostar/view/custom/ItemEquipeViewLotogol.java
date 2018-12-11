package br.gov.caixa.loterias.apostar.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DezenaLotogol;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostar.utils.Constantes;
import br.gov.caixa.loterias.apostar.view.listener.LotogolListener;
import br.gov.caixa.loterias.apostar.view.adapter.NumerosLotogolAdapter;

/**
 * Created by joafilho on 28/12/2017.
 */
@EViewGroup(R.layout.item_lotogol)
public class ItemEquipeViewLotogol extends RelativeLayout {

    @ViewById
    ImageView itemImage;

    @ViewById
    TextView itemTitle;

    @ViewById
    ExpandableHeightGridView gridViewPlacarTresColunas, gridViewPlacarDuasColunas;

    private ParametroEquipe parametroEquipe;
    private LotogolListener listerner;

    private List<DezenaLotogol> placarListUm;
    private List<DezenaLotogol> placarListDois;

    private NumerosLotogolAdapter numerosLotogolAdapterListUm;
    private NumerosLotogolAdapter numerosLotogolAdapterListDois;

    private View selecionadoAnterior;

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
        placarListUm.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_0, Constantes.ZERO_STRING));
        placarListUm.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_1, Constantes.UM_STRING));
        placarListUm.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_2, Constantes.DOIS_STRING));
        numerosLotogolAdapterListUm = new NumerosLotogolAdapter(getContext(), placarListUm);
        gridViewPlacarTresColunas.setAdapter(numerosLotogolAdapterListUm);
        gridViewPlacarTresColunas.setExpanded(true);
        gridViewPlacarTresColunas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickItem(adapterView, view);
            }
        });

        placarListDois.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_3, Constantes.TRES_STRING));
        placarListDois.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_plus, Constantes.PLUS_STRING));

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

        if (view.getTag().toString().equals(parametroEquipe.getPlacar())) {
            parametroEquipe.setPlacar(StringUtils.EMPTY);
            selecionadoAnterior = null;
        } else {
            if (StringUtils.isNotEmpty(parametroEquipe.getPlacar())) {
                parametroEquipe.setPlacar(view.getTag().toString());
                tirarSelecaoAnterior();
                selecionarViewSetAnterior(view);
            } else {
                parametroEquipe.setPlacar(view.getTag().toString());
                selecionarViewSetAnterior(view);
            }
        }
        listerner.partidaSelecionada();
    }

    private void selecionarViewSetAnterior(View view) {
        view.setSelected(Boolean.TRUE);
        selecionadoAnterior = view;
    }

    private void tirarSelecaoAnterior() {
        selecionadoAnterior.setSelected(Boolean.FALSE);
    }
}
