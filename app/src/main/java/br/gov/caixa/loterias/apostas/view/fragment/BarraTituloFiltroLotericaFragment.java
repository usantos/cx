package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UF;
import br.gov.caixa.loterias.apostas.view.custom.BotaoFavoritar;

public class BarraTituloFiltroLotericaFragment extends Fragment {
    private static final String ARG_FILTRO = "ARG_FILTRO";

    private static final String ARG_ESTADO_INICIAL = "ARG_ESTADO_INICIAL";

    private View view;

    private TextView tvNomeLoterica, tvStringLoterica;
    private FiltroAplicadoMarketplace filtro;

    private boolean estadoInicial;

    private BotaoFavoritar botaoFavoritar;



    public BarraTituloFiltroLotericaFragment() {}

    public static BarraTituloFiltroLotericaFragment newInstance(FiltroAplicadoMarketplace filtro, boolean estadoInicial) {
        BarraTituloFiltroLotericaFragment fragment = new BarraTituloFiltroLotericaFragment();
        Bundle args     = new Bundle();
        args.putString(ARG_FILTRO, new Gson().toJson(filtro));
        args.putBoolean(ARG_ESTADO_INICIAL, estadoInicial);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            filtro = new Gson().fromJson(getArguments().getString(ARG_FILTRO), FiltroAplicadoMarketplace.class);
            estadoInicial = args.getBoolean(ARG_ESTADO_INICIAL, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_barra_titulo_filtro_loterica, container, false);
        tvNomeLoterica = view.findViewById(R.id.nome_loterica);
        tvStringLoterica = view.findViewById(R.id.string_loterica);

        botaoFavoritar = view.findViewById(R.id.btn_favoritar_mkp);

        botaoFavoritar.setState(estadoInicial);

//        botaoFavoritar.setOnStateChangeListener(isFavoritado -> {
//            if(listener != null){
//                listener.onFavoritarClick(position, isFavoritado);
//            }
//        });

        preencheDados();

        return view;
    }


    private void preencheDados() {
        //botaoFavoritar.setState(bolao.isLotericaFavorita());

        tvNomeLoterica.setText(filtro.getLotericaDTO().getNomeFantasia().trim());

        String stringLoterica = //filtro.getLotericaDTO().getCodigo().trim() + " / " +
                                UF.nomeFromId(filtro.getLotericaDTO().getIdUF().intValue()) + " - " +
                                UF.siglaFromId(filtro.getLotericaDTO().getIdUF().intValue());

        tvStringLoterica.setText(stringLoterica);
    }
}
