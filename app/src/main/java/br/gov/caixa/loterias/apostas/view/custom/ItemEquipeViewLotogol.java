package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DezenaLotogol;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.view.adapter.baseadapter.NumerosLotogolAdapter;
import br.gov.caixa.loterias.apostas.view.listener.LotogolListener;

/**
 * Created by joafilho on 28/12/2017.
 */
public class ItemEquipeViewLotogol extends RelativeLayout {

    private boolean alreadyInflated = false;

    private ImageView itemImage;
    private TextView itemTitle;
    private ExpandableHeightGridView gridViewPlacarTresColunas, gridViewPlacarDuasColunas;

    private ParametroEquipe parametroEquipe;
    private LotogolListener listerner;

    private List<DezenaLotogol> placarListUm;
    private List<DezenaLotogol> placarListDois;

    private NumerosLotogolAdapter numerosLotogolAdapterListUm;
    private NumerosLotogolAdapter numerosLotogolAdapterListDois;

    private View selecionadoAnterior;

    public static ItemEquipeViewLotogol build(Context context) {
        ItemEquipeViewLotogol instance = new ItemEquipeViewLotogol(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    //@AfterViews
    protected void init() {
        this.itemImage = findViewById(R.id.item_image);
        this.itemTitle = findViewById(R.id.item_title);
        this.gridViewPlacarTresColunas = findViewById(R.id.gridViewPlacarTresColunas);
        this.gridViewPlacarDuasColunas = findViewById(R.id.gridViewPlacarDuasColunas);

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
        gridViewPlacarTresColunas.setOnItemClickListener((adapterView, view, i, l) -> clickItem(view));

        placarListDois.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_3, Constantes.TRES_STRING));
        placarListDois.add(new DezenaLotogol(R.drawable.dezena_lotogol_selected_plus, Constantes.PLUS_STRING));

        numerosLotogolAdapterListDois = new NumerosLotogolAdapter(getContext(), placarListDois);
        gridViewPlacarDuasColunas.setAdapter(numerosLotogolAdapterListDois);
        gridViewPlacarDuasColunas.setExpanded(true);
        gridViewPlacarDuasColunas.setOnItemClickListener((adapterView, view, i, l) -> clickItem(view));
    }

    private void clickItem(View view) {

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
