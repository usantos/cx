package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class BarraTituloBolaoFragment extends Fragment {
    private static final String ARG_MODALIDADE = "ARG_MODALIDADE";
    private static final String ARG_CONCURSO = "ARG_CONCURSO";
    private static final String ARG_SORTEIO = "ARG_SORTEIO";
    private static final String ARG_ESPECIAL = "AR_ESPECIAL";
    private static final String ARG_COR_BACK = "AR_COR_BACK";
    private static final String ARG_COR_BACK_PREMIO = "AR_COR_BACK_PREMIO";
    private static final String ARG_VALOR_PREMIO = "ARG_VALOR_PREMIO";

    private View view;
    private TextView tvModalidade, tvConcurso, tvSorteio, labelConcurso, labelSorteio, tvValorPremio, tvValorPremioPorExtenso, labelPremioEstimado;
    private ConstraintLayout backgroundModalidade, backgroundPremio;
    private String concurso, sorteio;
    private BigDecimal valorPremio;
    private boolean isEspecial;
    private int corBackground, corBackgroundPremio;
    private ModalidadeEnum modalidade;
    private EstiloModalidadeMKP estilo;

    private RelativeLayout layoutTrevo;
    private LinearLayout premioMkp;

    private ImageView trevo;

    public BarraTituloBolaoFragment() {}

    public static BarraTituloBolaoFragment newInstance(ModalidadeEnum modalidade, String concuso, String sorteio, BigDecimal valorPremio, boolean isEspecial, int corBackground, int corBackgroundPremio) {
        BarraTituloBolaoFragment fragment = new BarraTituloBolaoFragment();
        Bundle args     = new Bundle();
        args.putString(ARG_MODALIDADE, new Gson().toJson(modalidade));
        args.putSerializable(ARG_CONCURSO, concuso);
        args.putSerializable(ARG_SORTEIO, sorteio);
        args.putSerializable(ARG_VALOR_PREMIO,valorPremio);
        args.putBoolean(ARG_ESPECIAL, isEspecial);
        args.putInt(ARG_COR_BACK, corBackground);
        args.putInt(ARG_COR_BACK_PREMIO, corBackgroundPremio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            concurso = getArguments().getString(ARG_CONCURSO);
            sorteio = getArguments().getString(ARG_SORTEIO);
            valorPremio = (BigDecimal) getArguments().getSerializable(ARG_VALOR_PREMIO);
            isEspecial = getArguments().getBoolean(ARG_ESPECIAL);
            modalidade = new Gson().fromJson(getArguments().getString(ARG_MODALIDADE), ModalidadeEnum.class);
            corBackground = getArguments().getInt(ARG_COR_BACK);
            corBackgroundPremio = getArguments().getInt(ARG_COR_BACK_PREMIO);
            //estilo = new EstiloModalidadeMKP(modalidade);
            estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(modalidade, Integer.valueOf(concurso), isEspecial);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_barra_titulo_bolao, container, false);
        layoutTrevo = view.findViewById(R.id.layout_trevo_mkp);
        trevo = view.findViewById(R.id.trevo_mkp);
        tvModalidade = view.findViewById(R.id.modalidade_mkp);
        tvConcurso = view.findViewById(R.id.numero_concurso_mkp);
        tvSorteio = view.findViewById(R.id.data_sorteio_mkp);
        tvValorPremio = view.findViewById(R.id.valor_premio_mkp);
        tvValorPremioPorExtenso = view.findViewById(R.id.valor_premio_mkp_por_extenso);
        backgroundModalidade = view.findViewById(R.id.container_modalidade_mkp);
        backgroundPremio = view.findViewById(R.id.container_premio_estimado_mkp);

        labelConcurso = view.findViewById(R.id.label_concurso_mkp);
        labelSorteio = view.findViewById(R.id.label_sorteio_mkp);
        labelPremioEstimado = view.findViewById(R.id.label_premio_mkp);
        premioMkp = view.findViewById(R.id.group_premio_mkp);
        ViewCompat.setAccessibilityHeading(premioMkp,true);
        if(modalidade.equals(ModalidadeEnum.LOTECA)){
            labelSorteio.setText(R.string.label_resultado);
        }

        preencheDados();
        aplicaEstilo();
        aplicaEstiloFonte();

        return view;
    }

    private void aplicaEstiloFonte() {

        if(getContext() == null) return;

        trevo.setImageDrawable(ContextCompat.getDrawable(getContext(), estilo.getTrevoFundoEscuro()));
        tvModalidade.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
        tvConcurso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
        tvSorteio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
        tvValorPremio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
        tvValorPremioPorExtenso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));
        labelConcurso.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
        labelSorteio.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoClaro()));
        labelPremioEstimado.setTextColor(ContextCompat.getColor(getContext(), estilo.getCorFonteFundoEscuro()));

    }

    private void aplicaEstilo() {

        if(getContext() == null) return;

        if (isEspecial) {
            //if(estilo.getFundoEspecial() != -1) backgroundModalidade.setBackground(AppCompatResources.getDrawable(getContext(), estilo.getFundoEspecial()));
            if(estilo.getImagemEspecialSimples() != -1) backgroundModalidade.setBackground(AppCompatResources.getDrawable(getContext(), estilo.getImagemEspecialSimples()));
            if(estilo.getTrapezioEspecial() != -1) layoutTrevo.setBackground(ContextCompat.getDrawable(getContext(), estilo.getTrapezioEspecial()));
        }else{
            layoutTrevo.setBackground(ContextCompat.getDrawable(getContext(), estilo.getTrapezio()));
            backgroundModalidade.setBackgroundColor(ContextCompat.getColor(getContext(), corBackground));
        }
        backgroundPremio.setBackground(AppCompatResources.getDrawable(getContext(), corBackgroundPremio));
    }


    private void preencheDados() {
        if (isEspecial){
            if(modalidade.equals(ModalidadeEnum.LOTOFACIL)) {
                //TextViewUtils.mudarTamanhoPorPorcentagem(tvModalidade, -10);
                tvModalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade).toLowerCase());
            } else if (EspecialUtils.isLotecaPais(modalidade, Integer.valueOf(concurso), isEspecial)) {
                //tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, ""));
                tvModalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_LINHA, ""));
            } else {
                tvModalidade.setText(ModalidadeEnum.getDescricaoEspecial(modalidade).toLowerCase());
            }
        } else {
            tvModalidade.setText(ModalidadeEnum.fromString(modalidade).toLowerCase());
        }
        ViewCompat.setAccessibilityHeading(tvModalidade,true);
        tvConcurso.setText(concurso);
        tvSorteio.setText(sorteio);
        tvValorPremio.setText(ViewUtils.getMoedaFormatComCentavos(valorPremio, 2));
        String valorEstimadoPorExtenso = ViewUtils.getMoedaFormatPorExtenso(valorPremio);
        if (!valorEstimadoPorExtenso.isEmpty()){
            tvValorPremioPorExtenso.setText(String.format("(%s)",valorEstimadoPorExtenso));
            tvValorPremioPorExtenso.setVisibility(View.VISIBLE);
        } else {
            tvValorPremioPorExtenso.setVisibility(View.GONE);
        }
    }
}
