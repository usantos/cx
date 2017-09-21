package caixa.gov.br.loterias.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import caixa.gov.br.loterias.ui.Inteface.CartelaActivityInterface;
import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ColorEnum;
import caixa.gov.br.loterias.utils.ViewUtils;

import static java.lang.String.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartelaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartelaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartelaFragment extends Fragment implements CartelaActivityInterface, OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableHeightGridView listaDezenas;

    private String[] dezenas = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            , "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43"
            , "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66"};
    private ArrayList<String> dezenasSelecionadas = new ArrayList<>();

    ColorEnum typeGameColorLight;
    ColorEnum typeGameColorDark;

    Button botaoFinalizar;

    SimularDezenasAdapter simularDezenasAdapter;

    //mocks
    int qtdDezenasPossiveisSelecionado;
    int numeroConcursoAtual;
    String dataSorteioAtual;

    private OnFragmentInteractionListener mListener;
    private CartelaFragmentListener cartelaFragmentListener;

    public CartelaFragment() {
        // Required empty public constructor
    }

    public static CartelaFragment newInstance(String param1, String param2) {
        CartelaFragment fragment = new CartelaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onActivityCreated(Bundle savedInstanceState) {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartela,container,false);
        configuraListaDezenas(view);
        verificaSeMostraBotaoLimparAposta(view);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cartelaFragmentListener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartelaFragmentListener");
        }

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // METODO DO PROTOCOLO ONCLICK
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.botaoAdicionarCompletarCartela: {
                if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado){
                    ViewUtils.alert(getActivity(), "Botão adicionar ao carrinho de apostas pressionado");
                }else{
                    ViewUtils.alert(getActivity(), "Botão completar com números aleatórios pressionado");
                }
            }
            break;

            case R.id.botaoCarrinho:
            case R.id.textInfoCarrinho:{
                ViewUtils.alert(getActivity(), "Carrinho de apostas pressionado");
            }
            break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // METODOS DE CONFIGURACOES
    private void configuraBotoes(View fragmentView){
        botaoFinalizar = fragmentView.findViewById(R.id.botaoAdicionarCompletarCartela);
        botaoFinalizar.setOnClickListener(this);
        botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
        botaoFinalizar.setText(getString(R.string.completarCartela));
        botaoFinalizar.setTextColor(Color.WHITE);

        Button carrinhoImageView = fragmentView.findViewById(R.id.botaoCarrinho);
        carrinhoImageView.setOnClickListener(this);

        TextView textInfoCarrinho = fragmentView.findViewById(R.id.textInfoCarrinho);
        textInfoCarrinho.setOnClickListener(this);
    }

    private void verificaSeMostraBotaoLimparAposta(View fragmentView){
        if (dezenasSelecionadas.isEmpty()){
            fragmentView.findViewById(R.id.botaoLimparAposta).setVisibility(View.GONE);
        }else{
            fragmentView.findViewById(R.id.botaoLimparAposta).setVisibility(View.VISIBLE);
        }
    }

    private void verificaQtdDezenasJaSelecionadas(){
        if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado){
            botaoFinalizar.setBackgroundColor(Color.parseColor(typeGameColorLight.toString()));
            botaoFinalizar.setText(getString(R.string.adicionarAoCarrinho));
        }else{
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
            botaoFinalizar.setText(getString(R.string.completarCartela));
        }
    }

    private void configuraListaDezenas(View fragmentView){
        listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        listaDezenas.setExpanded(true);
        simularDezenasAdapter = new SimularDezenasAdapter(getActivity());
        listaDezenas.setAdapter(simularDezenasAdapter);

        listaDezenas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (dezenasSelecionadas.contains(dezenas[position])) {
                    dezenasSelecionadas.remove(dezenas[position]);
                } else {
                    if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado) {
                        // VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS
                        int qtdDezenasPossiveisSelecionadoAtual = qtdDezenasPossiveisSelecionado;
                        qtdDezenasPossiveisSelecionado = cartelaFragmentListener.verificaNovoQtdDezenasMax(qtdDezenasPossiveisSelecionado);

                        if (qtdDezenasPossiveisSelecionadoAtual < qtdDezenasPossiveisSelecionado) {
                            dezenasSelecionadas.add(dezenas[position]);
                        } else {
                            ViewUtils.alert(getActivity(), "Número de dezenas máximo selecionadas.");
                        }
                    }else{
                        dezenasSelecionadas.add(dezenas[position]);
                    }
                }

                verificaSeMostraBotaoLimparAposta(getView());
                verificaQtdDezenasJaSelecionadas();
                simularDezenasAdapter.notifyDataSetChanged();
            }
        });
    }

    // Adapter do gridview das dezenas
    private class SimularDezenasAdapter extends BaseAdapter {
        private Context context;

        SimularDezenasAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return dezenas.length;
        }

        @Override
        public Object getItem(int i) {
            return dezenas[i];
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

            if (dezenasSelecionadas.contains(dezenas[i])){
                shape.setColor(Color.parseColor(typeGameColorLight.toString()));
                textView.setTextColor(Color.WHITE);
            }else{
                shape.setColor(Color.WHITE);
                textView.setTextColor(Color.parseColor(typeGameColorLight.toString()));
            }

            shape.setCornerRadius(50);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            textView.setText(dezenas[i]);
            textView.setGravity(Gravity.CENTER);

            return textView;
        }
    }

    // METODOS DA INTERFACE DA ACTIVITY
    @Override
    public void qtdDezenasPossiveisSelecionado(int qtdDezenas) {
        qtdDezenasPossiveisSelecionado = qtdDezenas;
        TextView text = (TextView) getView().findViewById(R.id.valorAposta);
        text.setText(format("R$ %.2f", 4.5 * qtdDezenas));

        verificaQtdDezenasJaSelecionadas();
    }


    // INTERFACE DO FRAGMENT PARA A ACTIVITY
    public interface CartelaFragmentListener {
        public int verificaNovoQtdDezenasMax(int qtdDezenasAtual);
    }
}
