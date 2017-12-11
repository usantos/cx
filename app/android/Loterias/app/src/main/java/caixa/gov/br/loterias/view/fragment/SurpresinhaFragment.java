package caixa.gov.br.loterias.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.view.Inteface.CartelaActivityInterface;
import caixa.gov.br.loterias.view.SimularApostaActivity;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.utils.ViewUtils;

import static java.lang.String.format;

public class SurpresinhaFragment extends Fragment implements CartelaActivityInterface, OnClickListener {

    int typeGameColorLight;
    int typeGameColorDark;

    SurpresinhaGridViewAdapter simularDezenasAdapter;

    Button botaoMenosSurpresinha;
    Button botaoMaisSurpresinha;
    Button botaoAdicionarSurpresinhaAoCarrinho;

    TextView qtdSurpresinhasTextView;
    private ParametroJogoDTO parametroJogo;
    private SimularApostaActivity parentActivity;

    private SurpresinhaFragmentListener surpresinhaFragmentListener;

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

        configuraBotoes(getView());
        configuraListaDezenas(getView());
    }

    public void surpresinhaFragmentBuilder(Bundle dataBundle){
        if (dataBundle != null) {
            //mocks
            qtdDezenasPossiveisSelecionado = dataBundle.getInt("qtdDezenasPossiveisSelecionado");
            numeroConcursoAtual = dataBundle.getInt("numeroConcursoAtual");
            dataSorteioAtual = dataBundle.getString("dataSorteioAtual");

            typeGameColorLight = (int) dataBundle.get("typeGameColorLight");
            typeGameColorDark = (int) dataBundle.get("typeGameColorDark");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = ((SimularApostaActivity)getActivity());
        parametroJogo = parentActivity.getParametroSimulacao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_surpresinha, container, false);
//        configuraListaDezenas(view);
//        verificaSeMostraBotaoLimparAposta(view);
        return inflater.inflate(R.layout.fragment_surpresinha, container, false);
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
            surpresinhaFragmentListener = (SurpresinhaFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SurpresinhaFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        surpresinhaFragmentListener = null;
    }

    // METODOS DE CONFIGURACOES
    private void atualizaDezenaAdapter(){
        simularDezenasAdapter.rows = montaListaDezenasSurpresinha();
        simularDezenasAdapter.notifyDataSetChanged();

        TextView text = getView().findViewById(R.id.valorAposta);
        text.setText(format("R$ %.2f", 4.5 * qtdDezenasPossiveisSelecionado));
    }

    private void configuraBotoes(View fragmentView){
        botaoMenosSurpresinha = fragmentView.findViewById(R.id.btnMenosSurpresinha);
        botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.cinzaescuro));
        botaoMenosSurpresinha.setOnClickListener(this);

        botaoMaisSurpresinha = fragmentView.findViewById(R.id.btnMaisSurpresinha);
        botaoMaisSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        botaoMaisSurpresinha.setOnClickListener(this);

        qtdSurpresinhasTextView = fragmentView.findViewById(R.id.qtdSurpresinhasSelecionadas);
        qtdSurpresinhasTextView.setTextColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));

        botaoAdicionarSurpresinhaAoCarrinho = fragmentView.findViewById(R.id.btnAdicionarSurpresinhaAoCarrinho);
        botaoAdicionarSurpresinhaAoCarrinho.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
        botaoAdicionarSurpresinhaAoCarrinho.setTextColor(Color.WHITE);
        botaoAdicionarSurpresinhaAoCarrinho.setOnClickListener(this);
        botaoAdicionarSurpresinhaAoCarrinho.setText(ViewUtils.configuraButtonText(getActivity(), getString(R.string.adicionarAoCarrinho), 9));

        Button carrinhoImageView = fragmentView.findViewById(R.id.botaoCarrinho);
        carrinhoImageView.setOnClickListener(this);

        TextView textInfoCarrinho = fragmentView.findViewById(R.id.textInfoCarrinho);
        textInfoCarrinho.setOnClickListener(this);
    }

    private void configuraListaDezenas(View fragmentView){
        ExpandableHeightGridView listaDezenas = fragmentView.findViewById(R.id.listaDezenasSurpresinha);

        listaDezenas.setExpanded(true);

        simularDezenasAdapter = new SurpresinhaGridViewAdapter(getActivity(), montaListaDezenasSurpresinha());
        listaDezenas.setAdapter(simularDezenasAdapter);
    }

    private ArrayList<String> montaListaDezenasSurpresinha(){
        ArrayList<String> arrayDezenas = new ArrayList<>();
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
                    botaoMenosSurpresinha.setBackgroundColor(ContextCompat.getColor(getActivity(), typeGameColorLight));
                }
                qtdSurpresinhas++;
            }
            break;

            case R.id.btnAdicionarSurpresinhaAoCarrinho:{
                surpresinhaFragmentListener.adicionarApostaCarrinho();
            }
            break;

            case R.id.botaoCarrinho:
            case R.id.textInfoCarrinho:{
                surpresinhaFragmentListener.abreCarrinhoApostas();
            }
        }

        qtdSurpresinhasTextView.setText(String.format("%d", qtdSurpresinhas));
    }

    // Adapter do gridview das dezenas
    private class SurpresinhaGridViewAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> rows;

        SurpresinhaGridViewAdapter(Context context, ArrayList<String> activitData){
            this.context = context;
            this.rows = activitData;
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
            textView.setTextColor(ContextCompat.getColor(context, typeGameColorLight));

            shape.setCornerRadius(50);
            shape.setStroke(1, Color.GRAY);
            textView.setBackground(shape);
            textView.setText(rows.get(i));
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
    public interface SurpresinhaFragmentListener {
        void adicionarApostaCarrinho();
        void abreCarrinhoApostas();
    }

}
