package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosResultadosAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.TrevosResultadosAdapter;

public class ResultadoTrevosFragment extends Fragment {

    public static final String RESULTADO_DTO = "RESULTADO_DTO";

    private ResultadoConcursoDTO resultado;
    private RecyclerView rvNumerosResultado;

    public ResultadoTrevosFragment() { }


    public static ResultadoTrevosFragment newInstance(ResultadoConcursoDTO resultado) {
        ResultadoTrevosFragment fragment = new ResultadoTrevosFragment();
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

        TrevosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;

        this.rvNumerosResultado.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        numerosResultadosAdapter = new TrevosResultadosAdapter(getActivity(), resultado.getTrevosSorteadosPrimeiroSorteio(), resultado.getConcurso().getModalidade());
        this.rvNumerosResultado.setAdapter(numerosResultadosAdapter);
    }

}
