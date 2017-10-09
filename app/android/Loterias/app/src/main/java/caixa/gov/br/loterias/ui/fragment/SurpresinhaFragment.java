package caixa.gov.br.loterias.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ColorEnum;

public class SurpresinhaFragment extends Fragment implements OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableHeightGridView listaDezenas;
    private ArrayList<String> dezenasSelecionadas = new ArrayList<>();

    ColorEnum typeGameColorLight;
    ColorEnum typeGameColorDark;

    SurpresinhaGridViewAdapter simularDezenasAdapter;

    Button botaoMenosSurpresinha;
    Button botaoMaisSurpresinha;
    Button botaoAdicionarSurpresinhaAoCarrinho;

    TextView qtdSurpresinhasTextView;

    //mocks
    int qtdDezenasPossiveisSelecionado;
    int numeroConcursoAtual;
    String dataSorteioAtual;
    int qtdSurpresinhas = 1;

    public SurpresinhaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            //mocks
            qtdDezenasPossiveisSelecionado = bundle.getInt("qtdDezenasPossiveisSelecionado");
            numeroConcursoAtual = bundle.getInt("numeroConcursoAtual");
            dataSorteioAtual = bundle.getString("dataSorteioAtual");

            typeGameColorLight = (ColorEnum) bundle.get("typeGameColorLight");
            typeGameColorDark = (ColorEnum) bundle.get("typeGameColorDark");
        }

        View view = getView();
        configuraBotoes(view);
        configuraListaDezenas(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surpresinha, container, false);
//        configuraListaDezenas(view);
//        verificaSeMostraBotaoLimparAposta(view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // METODOS DE CONFIGURACOES
    private void configuraBotoes(View fragmentView){
        botaoMenosSurpresinha = fragmentView.findViewById(R.id.btnMenosSurpresinha);
        botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
        botaoMenosSurpresinha.setOnClickListener(this);

        botaoMaisSurpresinha = fragmentView.findViewById(R.id.btnMaisSurpresinha);
        botaoMaisSurpresinha.setBackgroundColor(Color.parseColor(typeGameColorLight.toString()));
        botaoMaisSurpresinha.setOnClickListener(this);

        qtdSurpresinhasTextView = fragmentView.findViewById(R.id.qtdSurpresinhasSelecionadas);
        qtdSurpresinhasTextView.setTextColor(Color.parseColor(typeGameColorLight.toString()));
        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));

        botaoAdicionarSurpresinhaAoCarrinho = fragmentView.findViewById(R.id.btnAdicionarSurpresinhaAoCarrinho);
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(Color.parseColor(typeGameColorLight.toString()));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(Color.WHITE);
    }

    private void configuraListaDezenas(View fragmentView){
        listaDezenas = fragmentView.findViewById(R.id.listaDezenasSurpresinha);
        listaDezenas.setExpanded(true);

        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha());
        listaDezenas.setAdapter(simularDezenasAdapter);
    }

    private ArrayList montaListaDezenasSurpresinha(){
        ArrayList arrayDezenas = new ArrayList();
        for (int i = 0; i < qtdDezenasPossiveisSelecionado; i++) {
            arrayDezenas.add("XX");
        }

        return arrayDezenas;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.btnMenosSurpresinha: {
                if (qtdSurpresinhas > 1){
                    qtdSurpresinhas--;
                    if (qtdSurpresinhas == 1){
                        botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
                    }
                }

            }
            break;

            case R.id.btnMaisSurpresinha:{
                if (qtdSurpresinhas == 1){
                    botaoMenosSurpresinha.setBackgroundColor(Color.parseColor(typeGameColorLight.toString()));
                }
                qtdSurpresinhas++;
            }
            break;
        }

        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));
    }

    // Adapter do gridview das dezenas
    private class SurpresinhaGridViewAdapter extends BaseAdapter {
        private Context context;
        private ArrayList rows;

        SurpresinhaGridViewAdapter(Context context, ArrayList activitData){
            this.context = context;
            rows = activitData;
        }

        @Override
        public int getCount() {
            return rows.toArray().length;
        }

        @Override
        public Object getItem(int i) {
            return rows.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView;
            GradientDrawable shape =  new GradientDrawable();

            if (view == null){
                textView = new TextView(this.context);
                textView.setLayoutParams(new GridView.LayoutParams(80, 80));
            }else{
                textView = (TextView) view;
            }

            shape.setColor(Color.WHITE);
            textView.setTextColor(Color.parseColor(typeGameColorLight.toString()));

            shape.setCornerRadius(50);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            textView.setText(rows.get(i).toString());
            textView.setGravity(Gravity.CENTER);

            return textView;
        }
    }

}
