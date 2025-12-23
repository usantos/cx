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

public class BarraTituloFiltroLotericaFragment extends Fragment {
    private static final String ARG_FILTRO = "ARG_FILTRO";

    private View view;

    private TextView tvNomeLoterica, tvStringLoterica;
    private FiltroAplicadoMarketplace filtro;


    public BarraTituloFiltroLotericaFragment() {}

    public static BarraTituloFiltroLotericaFragment newInstance(FiltroAplicadoMarketplace filtro) {
        BarraTituloFiltroLotericaFragment fragment = new BarraTituloFiltroLotericaFragment();
        Bundle args     = new Bundle();
        args.putString(ARG_FILTRO, new Gson().toJson(filtro));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filtro = new Gson().fromJson(getArguments().getString(ARG_FILTRO), FiltroAplicadoMarketplace.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_barra_titulo_filtro_loterica, container, false);
        tvNomeLoterica = view.findViewById(R.id.nome_loterica);
        tvStringLoterica = view.findViewById(R.id.string_loterica);

        preencheDados();

        return view;
    }


    private void preencheDados() {
        tvNomeLoterica.setText(filtro.getLotericaDTO().getNomeFantasia().trim());

        String stringLoterica = filtro.getLotericaDTO().getCodigo().trim() + " / " +
                                filtro.getLotericaDTO().getNomeUF().trim() + " - " +
                                UF.siglaFromId(filtro.getLotericaDTO().getIdUF().intValue());

        tvStringLoterica.setText(stringLoterica);
    }
}
