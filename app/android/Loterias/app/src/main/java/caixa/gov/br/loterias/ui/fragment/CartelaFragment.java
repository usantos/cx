package caixa.gov.br.loterias.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.data.CompletaJogoRsp;
import caixa.gov.br.loterias.bo.silce.internals.SilceConnection;
import caixa.gov.br.loterias.bo.silce.internals.SilceSingleton;
import caixa.gov.br.loterias.ui.Inteface.CartelaActivityInterface;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.SimularApostaActivity;
import caixa.gov.br.loterias.ui.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ColorEnum;
import caixa.gov.br.loterias.utils.ViewUtils;

import static java.lang.String.*;

public class CartelaFragment extends Fragment implements CartelaActivityInterface, OnClickListener {

    private ExpandableHeightGridView listaDezenas;

    private String[] dezenas = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            , "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43"
            , "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66"};
    private ArrayList<String> dezenasSelecionadas = new ArrayList<>();

    ColorEnum typeGameColorLight;
    ColorEnum typeGameColorDark;

    Button botaoFinalizar;
    Button botaoLimparAposta;

    SimularDezenasAdapter simularDezenasAdapter;

    //mocks
    int qtdDezenasPossiveisSelecionado;
    int numeroConcursoAtual;
    int qtdCartelasCarrinho = 0;
    String dataSorteioAtual;

    private CartelaFragmentListener cartelaFragmentListener;

    public CartelaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configuraBotoes(getView());
    }

    public void cartelaFragmentBuilder(Bundle dataBundle){
        if (dataBundle != null){
            qtdDezenasPossiveisSelecionado = dataBundle.getInt("qtdDezenasPossiveisSelecionado");
            numeroConcursoAtual = dataBundle.getInt("numeroConcursoAtual");
            dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

            typeGameColorLight = (ColorEnum) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (ColorEnum) dataBundle.get("typeGameColorDark");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartela,container,false);
        configuraListaDezenas(view);
        verificaSeMostraBotaoLimparAposta(view);
        verificaSeMostraSalvarApostaView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        atualizaDezenaAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cartelaFragmentListener = (CartelaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartelaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cartelaFragmentListener = null;
    }

    // METODO DO PROTOCOLO ONCLICK
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.botaoAdicionarCompletarCartela: {
                if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado){
                    TextView count = getView().findViewById(R.id.textViewCarrinhoCartelaCount);

                    qtdCartelasCarrinho++;

                    count.setText(String.format("%d", qtdCartelasCarrinho));

                    cartelaFragmentListener.adicionarApostaCarrinho();
                } else{


                    ApostaBO apostaBO = new ApostaBO(getActivity());

                    apostaBO.completaJogo(6, Arrays.asList(15), 1, 60, new SilceClient.Listener<CompletaJogoRsp>() {
                        @Override
                        public void onResponse(CompletaJogoRsp response) {
                            ViewUtils.alert(getActivity(), response.toString());
                        }
                    }, new SilceClient.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ViewUtils.alert(getActivity(), "TESTE");
                        }
                    });

                }
            }
            break;

            case R.id.botaoLimparAposta:{
                dezenasSelecionadas.clear();
                verificaSeMostraBotaoLimparAposta(getView());
                verificaQtdDezenasJaSelecionadas();
                simularDezenasAdapter.notifyDataSetChanged();
            }
            break;

            case R.id.botaoCarrinho:
            case R.id.textInfoCarrinho:{
                ViewUtils.alert(getActivity(), "Carrinho de apostas pressionado");
            }
            break;

            case R.id.salvarApostaLayout:{
                RelativeLayout salvarApostaLayout = (RelativeLayout) view.findViewById(R.id.salvarApostaLayout);

                ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                final float scale = getContext().getResources().getDisplayMetrics().density;
                relativelayoutParams.height = (int) (140 * scale + 0.5f);

                salvarApostaLayout.setLayoutParams(relativelayoutParams);
                salvarApostaLayout.setOnClickListener(null);

                salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
            }
            break;

            case R.id.btnSalvarAposta:{
                RelativeLayout salvarApostaLayout = (RelativeLayout) getView().findViewById(R.id.salvarApostaLayout);

                salvarApostaLayout.setBackgroundResource(R.color.verdeazul);
                ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                final float scale = getContext().getResources().getDisplayMetrics().density;
                relativelayoutParams.height = (int) (64 * scale + 0.5f);

                salvarApostaLayout.setLayoutParams(relativelayoutParams);

                TextView tituloSalvarAposta = getView().findViewById(R.id.textoSalveEstaAposta);
                tituloSalvarAposta.setText("Aposta salva :)");
                tituloSalvarAposta.setGravity(Gravity.CENTER);

                ImageView starSalvarApostaImageView = getView().findViewById(R.id.iconStarSalvar);
                starSalvarApostaImageView.setBackgroundResource(R.drawable.icon_favoritado);
            }

        }
    }

    // METODOS DE CONFIGURACOES
    private void atualizaDezenaAdapter(){
        TextView text = getView().findViewById(R.id.valorAposta);
        text.setText(format("R$ %.2f", 4.5 * qtdDezenasPossiveisSelecionado));

        verificaQtdDezenasJaSelecionadas();
    }

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

        botaoLimparAposta = fragmentView.findViewById(R.id.botaoLimparAposta);
        botaoLimparAposta.setOnClickListener(this);
    }

    private void verificaSeMostraBotaoLimparAposta(View fragmentView){
        if (dezenasSelecionadas.isEmpty()){
            fragmentView.findViewById(R.id.botaoLimparAposta).setVisibility(View.GONE);
        }else{
            fragmentView.findViewById(R.id.botaoLimparAposta).setVisibility(View.VISIBLE);
        }
    }

    private void verificaSeMostraSalvarApostaView(View fragmentView){
        if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado){
            RelativeLayout salvarApostaLayout = fragmentView.findViewById(R.id.salvarApostaLayout);

            salvarApostaLayout.setVisibility(View.VISIBLE);
            final float scale = getContext().getResources().getDisplayMetrics().density;
            salvarApostaLayout.getLayoutParams().height = (int) (64 * scale + 0.5f);
            salvarApostaLayout.setOnClickListener(this);
        }else{
            fragmentView.findViewById(R.id.salvarApostaLayout).setVisibility(View.GONE);
        }
    }

    private void verificaQtdDezenasJaSelecionadas(){
        if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado){
            botaoFinalizar.setBackgroundColor(Color.parseColor(typeGameColorLight.toString()));
            botaoFinalizar.setText(getString(R.string.adicionarAoCarrinho));
        }else if (dezenasSelecionadas.size() > qtdDezenasPossiveisSelecionado) {
            dezenasSelecionadas.clear();
            simularDezenasAdapter.notifyDataSetChanged();
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
            botaoFinalizar.setText(getString(R.string.completarCartela));

            getView().findViewById(R.id.salvarApostaLayout).setVisibility(View.GONE);
            getView().findViewById(R.id.botaoLimparAposta).setVisibility(View.GONE);
        } else{
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
            botaoFinalizar.setText(getString(R.string.completarCartela));
        }
    }

    private void configuraListaDezenas(View fragmentView){
        listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        listaDezenas.setExpanded(true);
        simularDezenasAdapter = new SimularDezenasAdapter(getActivity(), dezenas);
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

                verificaSeMostraSalvarApostaView(getView());
                verificaSeMostraBotaoLimparAposta(getView());
                verificaQtdDezenasJaSelecionadas();
                simularDezenasAdapter.notifyDataSetChanged();
            }
        });
    }

    // Adapter do gridview das dezenas
    private class SimularDezenasAdapter extends BaseAdapter {
        private Context context;
        private String[] rows;

        SimularDezenasAdapter(Context context, String[] activitData){
            this.context = context;
            this.rows = activitData;
        }

        @Override
        public int getCount() {
            return rows.length;
        }

        @Override
        public Object getItem(int i) {
            return rows[i];
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

            if (dezenasSelecionadas.contains(rows[i])){
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

        if (getView() != null){
            atualizaDezenaAdapter();
        }
    }

    // INTERFACE DO FRAGMENT PARA A ACTIVITY
    public interface CartelaFragmentListener {
        int verificaNovoQtdDezenasMax(int qtdDezenasAtual);
        void adicionarApostaCarrinho();
    }
}
