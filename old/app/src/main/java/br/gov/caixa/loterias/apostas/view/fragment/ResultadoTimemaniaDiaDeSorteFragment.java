package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.NumerosResultadosAdapter;

import static br.gov.caixa.loterias.apostas.view.fragment.ResultadoGenericoFragment.RESULTADO_DTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultadoTimemaniaDiaDeSorteFragment extends Fragment {

    private ResultadoConcursoDTO resultado;
    RecyclerView rvNumerosResultado;
    ImageView ivTimeCoracao;
    TextView tvTitulo;
    TextView tvDescricao;
    private EstiloModalidadeMKP estilo;

    public ResultadoTimemaniaDiaDeSorteFragment() {
    }

    public static ResultadoTimemaniaDiaDeSorteFragment newInstance(ResultadoConcursoDTO resultado) {
        ResultadoTimemaniaDiaDeSorteFragment fragment = new ResultadoTimemaniaDiaDeSorteFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULTADO_DTO, resultado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resultado_timemania, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvNumerosResultado = view.findViewById(R.id.rv_numeros_resultado);
        ivTimeCoracao = view.findViewById(R.id.iv_time_coracao);
        tvTitulo = view.findViewById(R.id.tv_titulo);
        tvDescricao = view.findViewById(R.id.tv_descricao);

        if (getArguments() != null) {
            resultado = (ResultadoConcursoDTO) getArguments().getSerializable(RESULTADO_DTO);
            if(resultado != null){
                estilo = new EstiloModalidadeMKP(resultado.getConcurso().getModalidade());
                configuraResultadoTimeMania(resultado);
            }
        }
    }

    private void configuraResultadoTimeMania(ResultadoConcursoDTO resultado){

        if (resultado.getConcurso().getModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
            ivTimeCoracao.setVisibility(View.GONE);
            tvTitulo.setTextColor(getResources().getColor(estilo.getCorFonteFundoEscuro()));
            tvTitulo.setText(R.string.label_mes_desejado);
            tvDescricao.setTextColor(getResources().getColor(estilo.getCorFonteFundoEscuro()));
            if (resultado.getPremiacaoMesDeSorte() != null
                    && resultado.getPremiacaoMesDeSorte().getMesDeSorte() != null) {
                tvDescricao.setText(resultado.getPremiacaoMesDeSorte().getMesDeSorte().getNome());
            }else{
                tvDescricao.setText(R.string.label_tres_interrogacoes);
            }
        } else {
            tvDescricao.setText(resultado.getPremiacaoTimeDoCoracao().getEquipe().getNome() + "/" +
                    resultado.getPremiacaoTimeDoCoracao().getEquipe().getUf());
        }

        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (resultado.getListaSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }

        this.rvNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), resultado.getListaSorteadosPrimeiroSorteio(),resultado.getConcurso().getModalidade());
        this.rvNumerosResultado.setAdapter(numerosResultadosAdapter);

    }

}
