package br.gov.caixa.loterias.apostas.view.fragment;

        import android.content.res.Resources;
        import android.os.Bundle;
        import android.util.TypedValue;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import androidx.core.content.ContextCompat;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import org.jetbrains.annotations.NotNull;

        import java.util.ArrayList;
        import java.util.List;

        import br.gov.caixa.loterias.apostas.R;
        import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConfiguracaoNumeros;
        import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Dezena;
        import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
        import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
        import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;

public class TrevosSurpresinhaFragment extends Fragment {

    private static final String LISTA_TREVOS = "LISTA_TREVOS";
    private static final String CONFIGURACAO = "CONFIGURACAO";
    private static final String SHAPE = "SHAPE";
    private static final String TITULO_LISTA = "TITULO_LISTA";
    private static final int QUANTIDADE_COLUNA_LISTA = 6;
    private String listaTrevosSurpresinha;
    private View view = null;
    private RecyclerView listViewNumeros;
    private ListaDezenaRecyclerView numerosAdapter;
    private List<String> listaTrevosList;
    private ConfiguracaoNumeros configuracao;
    private ShapeConfig shapeConfig;

    private TextView titulo;
    private String tituloTexto;

    public TrevosSurpresinhaFragment() { }

    public static TrevosSurpresinhaFragment newInstance(String titulo,
                                                        List<String> trevosSurpresinha,
                                                        ConfiguracaoNumeros configuracao, ShapeConfig shapeConfig) {
        TrevosSurpresinhaFragment fragment = new TrevosSurpresinhaFragment();
        Bundle args     = new Bundle();

        if (trevosSurpresinha != null && !trevosSurpresinha.isEmpty()){
            args.putString(LISTA_TREVOS, String.valueOf(trevosSurpresinha));
        } else {
            args.putString(LISTA_TREVOS, "");
        }

        if (titulo != null && !titulo.isEmpty()){
            args.putString(TITULO_LISTA, titulo);
        } else {
            args.putString(TITULO_LISTA, "");
        }
        args.putSerializable(CONFIGURACAO,configuracao);
        args.putSerializable(SHAPE,shapeConfig);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listaTrevosSurpresinha = getArguments().getString(LISTA_TREVOS);
            tituloTexto = getArguments().getString(TITULO_LISTA);
            Gson gson = new Gson();
            TypeToken<List<String>> tokenString          = new TypeToken<List<String>>() {};
            if(listaTrevosSurpresinha!= null && listaTrevosSurpresinha.length() > 0){
                listaTrevosList = gson.fromJson(listaTrevosSurpresinha, tokenString.getType());
            } else {
                listaTrevosList = new ArrayList<>();
            }

            configuracao = (ConfiguracaoNumeros) getArguments().getSerializable(CONFIGURACAO);
            shapeConfig = (ShapeConfig) getArguments().getSerializable(SHAPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_numeros, container, false);
        setaViews(view);
        setaMetodos();
        return view;
    }

    private void setaViews(View view){

        titulo = view.findViewById(R.id.lista_titulo);
        if (tituloTexto == null || tituloTexto.isEmpty()){
            titulo.setVisibility(View.GONE);
        } else {
            titulo.setText(tituloTexto);
        }

        //titulo.setTextColor(getActivity().getResources().getColor(R.color.branco));
        titulo.setTextColor(ContextCompat.getColor(getActivity(), configuracao.getCorTextoCirculo()));

        listViewNumeros = view.findViewById(R.id.ehgv_numeros);

        float     dip = getQtdColunasTrevos(listaTrevosList) * 38f + 40f;
        Resources r   = getActivity().getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.height = px + 55;
        view.setLayoutParams(param);
    }

    private void setaMetodos (){
        numerosAdapter = new ListaDezenaRecyclerView(true, getDezenas(), new ArrayList<>(), getDezenaConfig(), null);

        listViewNumeros.setAdapter(numerosAdapter);

        listViewNumeros.setLayoutManager(new GridLayoutManager(getContext(), QUANTIDADE_COLUNA_LISTA));
        listViewNumeros.setNestedScrollingEnabled(false);
    }

    private DezenaConfig getDezenaConfig() {
        if (this.shapeConfig == null){
            shapeConfig = new ShapeConfig(R.color.branco, configuracao.getCorTextoCirculoSelecionado());
        }

        return new DezenaConfig(false, configuracao.getCorTextoCirculo(),
                R.layout.item_dezena_detalhe,
                shapeConfig,false);
    }

    @NotNull
    private List<Dezena> getDezenas() {
        List<Dezena> dezenas = new ArrayList<>();
        for (int i = 0; i < listaTrevosList.size(); i++) {
            dezenas.add(new Dezena("XX", Boolean.FALSE, "XX"));
        }
        return dezenas;
    }

    private int getQtdColunasTrevos(List<String> list){
        int qtdColunas = list.size() / QUANTIDADE_COLUNA_LISTA;
        int restante          = list.size() - (qtdColunas * QUANTIDADE_COLUNA_LISTA);
        if (restante > 0){
            qtdColunas++;
        }
        if (qtdColunas >= 4){
            qtdColunas += 3;
        } else {
            qtdColunas++;
        }
        return qtdColunas;
    }

}