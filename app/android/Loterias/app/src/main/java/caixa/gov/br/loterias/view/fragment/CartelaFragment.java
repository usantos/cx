package caixa.gov.br.loterias.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.view.Inteface.CartelaActivityInterface;
import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ViewUtils;

import static java.lang.String.*;

public class CartelaFragment extends Fragment implements CartelaActivityInterface, OnClickListener {

    private ArrayList<String> dezenas = new ArrayList<String>();

    private ArrayList<Integer> dezenasSelecionadas = new ArrayList<>();

    int typeGameColorLight;
    int typeGameColorDark;

    Button botaoFinalizar;
    Button botaoLimparAposta;

    SimularDezenasAdapter simularDezenasAdapter;

    //mocks
    int qtdDezenasPossiveisSelecionado;
    int numeroConcursoAtual;
    int qtdCartelasCarrinho = 0;
    String dataSorteioAtual;
    private SimularApostaActivity parentActivity;

    private CartelaFragmentListener cartelaFragmentListener;

    public CartelaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = ((SimularApostaActivity)getActivity());
        ParametroJogoDTO teste = parentActivity.getParametroSimulacao();

        for (int i = 1; i <= parentActivity.getParametroSimulacao().getPrognosticoMaximo() ; i++) {
            dezenas.add("" + i);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configuraBotoes(getView());

        ViewUtils.organizaTabLayout(getActivity(), R.id.cartelaFragmentMainLayout, R.id.listaDezenas, R.id.cartelaFragmentScrollView);

    }

    public void cartelaFragmentBuilder(Bundle dataBundle){
        if (dataBundle != null){
            qtdDezenasPossiveisSelecionado = dataBundle.getInt("qtdDezenasPossiveisSelecionado");
            numeroConcursoAtual = dataBundle.getInt("numeroConcursoAtual");
            dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
                    preencheNumerosAleatorios();
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
                cartelaFragmentListener.abreCarrinhoApostas();
            }
            break;

            case R.id.salvarApostaLayout:{
                RelativeLayout salvarApostaLayout = view.findViewById(R.id.salvarApostaLayout);

                ViewGroup.LayoutParams relativelayoutParams = salvarApostaLayout.getLayoutParams();

                final float scale = getContext().getResources().getDisplayMetrics().density;
                relativelayoutParams.height = (int) (140 * scale + 0.5f);

                salvarApostaLayout.setLayoutParams(relativelayoutParams);
                salvarApostaLayout.setOnClickListener(null);

                salvarApostaLayout.findViewById(R.id.btnSalvarAposta).setOnClickListener(this);
            }
            break;

            case R.id.btnSalvarAposta:{
                RelativeLayout salvarApostaLayout = getView().findViewById(R.id.salvarApostaLayout);

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

    private void preencheNumerosAleatorios(){
        ApostaBO apostaBO = new ApostaBO(getActivity());

        apostaBO.completaJogo(qtdDezenasPossiveisSelecionado, dezenasSelecionadas, 1, 60, new SilceClient.Listener<CompletaJogoRsp>() {
            @Override
            public void onResponse(CompletaJogoRsp response) {
                dezenasSelecionadas.clear();
                dezenasSelecionadas.addAll(response.getPayload());
                simularDezenasAdapter.notifyDataSetChanged();
                verificaQtdDezenasJaSelecionadas();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ViewUtils.alert(getActivity(), "ERRO");
            }
        });
    }

    private void atualizaDezenaAdapter(){
        TextView text = getView().findViewById(R.id.valorAposta);
        text.setText(format("R$ %.2f", 4.5 * qtdDezenasPossiveisSelecionado));

        verificaQtdDezenasJaSelecionadas();
    }

    private void configuraBotoes(View fragmentView){
        botaoFinalizar = fragmentView.findViewById(R.id.botaoAdicionarCompletarCartela);
        botaoFinalizar.setOnClickListener(this);
        botaoFinalizar.setBackgroundColor(ContextCompat.getColor(fragmentView.getContext(), R.color.cinzaescuro));
        botaoFinalizar.setText(ViewUtils.configuraButtonText(fragmentView.getContext(), getString(R.string.completarCartela), 9));


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
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
            botaoFinalizar.setText(ViewUtils.configuraButtonText(getActivity(), getString(R.string.adicionarAoCarrinho), 9));
        }else if (dezenasSelecionadas.size() > qtdDezenasPossiveisSelecionado) {
            dezenasSelecionadas.clear();
            simularDezenasAdapter.notifyDataSetChanged();
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
            botaoFinalizar.setText(ViewUtils.configuraButtonText(getActivity(), getString(R.string.completarCartela), 9));

            getView().findViewById(R.id.salvarApostaLayout).setVisibility(View.GONE);
            getView().findViewById(R.id.botaoLimparAposta).setVisibility(View.GONE);
        } else{
            botaoFinalizar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
            botaoFinalizar.setText(ViewUtils.configuraButtonText(getActivity(), getString(R.string.completarCartela), 9));
        }
    }

    private void configuraListaDezenas(View fragmentView){
        ExpandableHeightGridView listaDezenas = fragmentView.findViewById(R.id.listaDezenas);
        listaDezenas.setExpanded(true);
        simularDezenasAdapter = new SimularDezenasAdapter(getActivity(), dezenas);
        listaDezenas.setAdapter(simularDezenasAdapter);

        listaDezenas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Integer valorSelecionado = Integer.valueOf(dezenas.get(position));

                if (dezenasSelecionadas.contains(valorSelecionado)) {
                    dezenasSelecionadas.remove(valorSelecionado);
                } else {
                    if (dezenasSelecionadas.size() == qtdDezenasPossiveisSelecionado) {
                        // VERIFICA SE TEM OPÇÃO DE SELECIONAR MAIS DEZENAS
                        int qtdDezenasPossiveisSelecionadoAtual = qtdDezenasPossiveisSelecionado;
                        qtdDezenasPossiveisSelecionado = cartelaFragmentListener.verificaNovoQtdDezenasMax(qtdDezenasPossiveisSelecionado);

                        if (qtdDezenasPossiveisSelecionadoAtual < qtdDezenasPossiveisSelecionado) {
                            dezenasSelecionadas.add(valorSelecionado);
                        } else {
                            ViewUtils.alert(getActivity(), "Número de dezenas máximo selecionadas.");
                        }
                    }else{
                        dezenasSelecionadas.add(valorSelecionado);
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
        private ArrayList<String> rows;

        SimularDezenasAdapter(Context context, ArrayList<String> activitData){
            this.context = context;
            this.rows = activitData;
        }

        @Override
        public int getCount() {
            return rows.size();
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

            if (dezenasSelecionadas.contains(Integer.valueOf(rows.get(i)))){
                shape.setColor(ContextCompat.getColor(context, typeGameColorLight));
                textView.setTextColor(Color.WHITE);
            }else{
                shape.setColor(Color.WHITE);
                textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));
            }

            shape.setCornerRadius(50);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            textView.setText(dezenas.get(i));
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
        void abreCarrinhoApostas();
    }
}
