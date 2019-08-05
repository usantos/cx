package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.view.adapter.NumerosResultadosAdapter;

public class ResultadoGenericoFragment extends Fragment {

    public static final String RESULTADO_DTO = "RESULTADO_DTO";

    private ResultadoConcursoDTO resultado;
    private RecyclerView rvNumerosResultado;

    public ResultadoGenericoFragment() { }


    public static ResultadoGenericoFragment newInstance(ResultadoConcursoDTO resultado) {
        ResultadoGenericoFragment fragment = new ResultadoGenericoFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULTADO_DTO, resultado);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resultado_generico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvNumerosResultado = view.findViewById(R.id.rv_numeros_resultado);

        if (getArguments() != null) {
            resultado = (ResultadoConcursoDTO) getArguments().getSerializable(RESULTADO_DTO);
            if(resultado != null){
                configuraResultadoNumeros(resultado);
            }
        }
    }

    private void configuraResultadoNumeros(ResultadoConcursoDTO resultado) {

        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (resultado.getNumerosSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }

        this.rvNumerosResultado.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getActivity(), resultado.getNumerosSorteadosPrimeiroSorteio(), resultado.getConcurso().getModalidade());
        this.rvNumerosResultado.setAdapter(numerosResultadosAdapter);
    }

}
