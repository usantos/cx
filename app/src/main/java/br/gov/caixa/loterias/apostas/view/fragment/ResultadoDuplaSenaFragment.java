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
import static br.gov.caixa.loterias.apostas.view.fragment.ResultadoGenericoFragment.RESULTADO_DTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultadoDuplaSenaFragment extends Fragment {

    private RecyclerView rvSegundoResultado;
    private RecyclerView rvPrimeiroResultado;
    private ResultadoConcursoDTO resultado;

    public ResultadoDuplaSenaFragment() {
    }

    public static ResultadoDuplaSenaFragment newInstance(ResultadoConcursoDTO resultado) {
        ResultadoDuplaSenaFragment fragment = new ResultadoDuplaSenaFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULTADO_DTO, resultado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_resultado_dupla_sena, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvPrimeiroResultado = view.findViewById(R.id.rv_primeiro_sorteio);
        rvSegundoResultado = view.findViewById(R.id.rv_segundo_sorteio);

        if (getArguments() != null) {
            resultado = (ResultadoConcursoDTO) getArguments().getSerializable(RESULTADO_DTO);
            if(resultado != null){
                configuraResultadosDuplaSena(resultado);
            }
        }
    }

    private void configuraResultadosDuplaSena(ResultadoConcursoDTO resultado){

        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (resultado.getListaSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }

        this.rvPrimeiroResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), resultado.getListaSorteadosPrimeiroSorteio(),resultado.getConcurso().getModalidade());
        this.rvPrimeiroResultado.setAdapter(numerosResultadosAdapter);

        this.rvSegundoResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), resultado.getNumerosSorteadosSegundoSorteio(), resultado.getConcurso().getModalidade());
        this.rvSegundoResultado.setAdapter(numerosResultadosAdapter);
    }

}
