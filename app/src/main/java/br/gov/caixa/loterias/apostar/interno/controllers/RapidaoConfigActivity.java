package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RapidaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RapidaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.sp.RapidaoSP;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FavoritasRapidaoRecyclerAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.NumerosRapidaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.TiposApostasAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

import static java.lang.Math.floor;

@EActivity(R.layout.activity_rapidao_config)
@OptionsMenu(R.menu.settings)
public class RapidaoConfigActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    @ViewById
    protected SeekBar seekBarValorMinimoAposta, seekBarValorMaximoAposta, seekBarPremiumMimimun;

    @ViewById
    protected com.blackcat.currencyedittext.CurrencyEditText tituloValorValorMinimoAposta, tituloValorValorMaximoAposta, tituloValorPremiumMinimum;

    @ViewById
    protected ExpandableHeightGridView gridviewNumerosProibidosRapidao, gridviewNumerosObrigatoriosRapidao, gridviewTiposApostasRapidao;

    @ViewById
    protected RecyclerView gridViewApostaFavorita;

    @ViewById
    protected TextView tituloValorMinimoApostaValorMaximo, tituloValorMaximoApostaValorMaximo, tituloValorMaximoApostaValorMinimo, tituloValorMinimoApostaValorMinimo, tituloValorMinimoPremiumMiminum;

    @ViewById
    protected ScrollView scrollViewRapidaoConfig;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected Button btnAddRemoverNumsObrigatorios, btnAddRemoverNumsProibidos, btnAddRemoverApostasFav;

    private AlertDialog loadViewProgress;
    private List<ApostaFavoritaDTO> favoritosSelecionados, favoritosSalvos;
    private List<TipoAposta> tipoApostaLista = new ArrayList<>();
    private List<ApostaFavoritaDTO> apostaFavoritaConfiguradasLista = new ArrayList<>();
    private List<Integer> listaNumerosObrigatorios = new ArrayList<>();
    private List<Integer> listaNumerosProibidos = new ArrayList<>();
    private RapidaoDTO rapidao;
    private BigDecimal valorMinApostaCarrinho, valorMaxApostaCarrinho;
    private boolean primeiroCarregamento = true;
    private boolean setValue;

    @AfterViews
    protected void init() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
        valorMinApostaCarrinho = sessaoUser.getValorMinimimoAposta();
        valorMaxApostaCarrinho = sessaoUser.getValorMaximoAposta();
        tituloValorMinimoApostaValorMinimo.setText(ViewUtils.getMoedaFormat(valorMinApostaCarrinho));

        callWebservice();

        scrollViewRapidaoConfig.setFocusableInTouchMode(true);
        scrollViewRapidaoConfig.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        setValue = true;


        tituloValorValorMinimoAposta.setLocale(new Locale("pt", "BR"));
        tituloValorValorMaximoAposta.setLocale(new Locale("pt", "BR"));
        tituloValorPremiumMinimum.setLocale(new Locale("pt", "BR"));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int valorMinAposta = (int) Math.floor(valorMinApostaCarrinho.doubleValue());
        int progressSet = progress;

        if (seekBar == seekBarValorMinimoAposta) {
            if (progressSet < valorMinAposta) {
                progressSet = valorMinAposta;
                setValue = true;
            } else if (progressSet > seekBarValorMaximoAposta.getProgress() && seekBarValorMaximoAposta.getProgress() >= 1) {
                rapidao.setValorMinimo(rapidao.getValorMaximo());
                tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(rapidao.getValorMinimo()));
                progressSet = seekBarValorMaximoAposta.getProgress();
            }
            if (setValue || primeiroCarregamento) {
                BigDecimal valorProgress = new BigDecimal(Float.toString((float) progressSet));
                rapidao.setValorMinimo(valorProgress);
                tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
            }
        }
        if (seekBar == seekBarValorMaximoAposta) {
            if (progressSet < valorMinAposta) {
                progressSet = valorMinAposta;
                setValue = true;
            } else if (progressSet < seekBarValorMinimoAposta.getProgress() && seekBarValorMinimoAposta.getProgress() >= 1) {
                BigDecimal valorProgress = new BigDecimal(Float.toString((float) progressSet));
                rapidao.setValorMaximo(valorProgress);
                rapidao.setValorMinimo(rapidao.getValorMaximo());
                tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(rapidao.getValorMinimo()));
                seekBarValorMinimoAposta.setProgress(progressSet);
            }
            if (setValue || primeiroCarregamento) {
                BigDecimal valorProgress = new BigDecimal(Float.toString((float) progressSet));
                rapidao.setValorMaximo(valorProgress);
                tituloValorValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
            }
        }
        if (seekBar == seekBarPremiumMimimun) {
            if (setValue || primeiroCarregamento) {
                int progressFormatMilhoes = progressSet * 1000000;
                BigDecimal valorProgress = new BigDecimal(Float.toString((float) progressFormatMilhoes));
                rapidao.setValorMinimoPremioPrincipal(valorProgress);
                tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(valorProgress));
            }
        }
        setValue = true;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == seekBarValorMinimoAposta) {

        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == seekBarValorMinimoAposta) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 5) {
            String arrayAsString = data.getExtras().getString("tiposAposta");
            tipoApostaLista = Arrays.asList(new Gson().fromJson(arrayAsString, TipoAposta[].class));
            gridViewTipoAposta();
        }

        if (resultCode == RESULT_OK && requestCode == 6) {
            String arrayAsString = data.getExtras().getString("numerosRapidao");
            listaNumerosObrigatorios = Arrays.asList(new Gson().fromJson(arrayAsString, Integer[].class));
            gridViewNumerosObrigatorios();
        }

        if (resultCode == RESULT_OK && requestCode == 7) {
            String arrayAsString = data.getExtras().getString("numerosRapidao");
            listaNumerosProibidos = Arrays.asList(new Gson().fromJson(arrayAsString, Integer[].class));
            gridViewNumerosProibidos();
        }

         if (resultCode == RESULT_OK && requestCode == 8) {
            String arrayAsString = data.getExtras().getString("apostasSalvas");
            apostaFavoritaConfiguradasLista = new ArrayList<>(Arrays.asList(new Gson().fromJson(arrayAsString, ApostaFavoritaDTO[].class)));
            rapidao.setApostasFavoritas(apostaFavoritaConfiguradasLista);
            gridViewApostaFavorita();
        }
    }

    @Click
    protected void limparValorMinimoAposta() {

        seekBarValorMinimoAposta.setProgress(0);
    }

    @Click
    protected void limparPremioMinimoAposta() {
        setValue = true;
        seekBarPremiumMimimun.setProgress(0);
    }

    @Click
    protected void limparValorMaximoAposta() {
        seekBarValorMaximoAposta.setProgress(0);
    }

    @Click
    protected void salvarApostaRapidao() {
        postRapidoesWebservice();
    }

    @OptionsItem(R.id.action_settings)
    protected void actionSettings() {
        abrirTermosUso();
    }

    private void gridViewTipoAposta() {
        final ArrayList<TipoAposta> tipoApostaArrayList = new ArrayList<>();
        for (TipoAposta tipoAposta : tipoApostaLista) {
            if (tipoAposta.getSelect()) {
                tipoApostaArrayList.add(tipoAposta);
            }
        }

        for (int i = 0; i < tipoApostaArrayList.size(); i++) {
            if (ModalidadeEnum.fromInteger(tipoApostaArrayList.get(i).getValor()) == ModalidadeEnum.LOTOGOL || ModalidadeEnum.fromInteger(tipoApostaArrayList.get(i).getValor()) == ModalidadeEnum.LOTECA ) {
                tipoApostaArrayList.remove(i);
            }
        }

        TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(this, tipoApostaArrayList);
        gridviewTiposApostasRapidao.setAdapter(tiposApostasAdapter);
        gridviewTiposApostasRapidao.setExpanded(true);

        gridviewTiposApostasRapidao.setOnItemClickListener((parent, v, position, id) -> {
            if (position == tipoApostaArrayList.size()) {
                FirebaseManager.registraEvento(RapidaoConfigActivity.this ,"ENTROU_LISTA_MODALIDADE_CONFIG_RAPIDAO");
                Intent activity = new Intent(RapidaoConfigActivity.this, SelecaoModalidadesRapidaoActivity.class);
                String arrayAsString = new Gson().toJson(tipoApostaLista);
                activity.putExtra("tiposAposta", arrayAsString);
                startActivityForResult(activity, 5);
            }
        });
    }

    public String treatValorPremium(BigDecimal premium) {
        if ((premium.divide(new BigDecimal(1000000))).compareTo(new BigDecimal(1.0)) == 0) {
            return "1 milhão";
        } else
            return premium.divide(new BigDecimal(1000000)) + " milhões";
    }


    private void gridViewNumerosObrigatorios() {
        NumerosRapidaoAdapter numerosRapidaoAdapter = new NumerosRapidaoAdapter(this, listaNumerosObrigatorios, true);
        gridviewNumerosObrigatoriosRapidao.setAdapter(numerosRapidaoAdapter);
        gridviewNumerosObrigatoriosRapidao.setExpanded(true);

        btnAddRemoverNumsObrigatorios.setOnClickListener( (View v) -> {
            Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
            String arrayAsString = new Gson().toJson(listaNumerosObrigatorios);
            activity.putExtra("numerosobrigatorios", arrayAsString);
            arrayAsString = new Gson().toJson(listaNumerosProibidos);
            activity.putExtra("numerosContrarios", arrayAsString);
            activity.putExtra("isTipoNrObrigatorios", true);
            startActivityForResult(activity, 6);
        });

    }

    private void gridViewNumerosProibidos() {
        NumerosRapidaoAdapter numerosRapidaoAdapter2 = new NumerosRapidaoAdapter(this, listaNumerosProibidos, false);
        gridviewNumerosProibidosRapidao.setAdapter(numerosRapidaoAdapter2);
        gridviewNumerosProibidosRapidao.setExpanded(true);

        btnAddRemoverNumsProibidos.setOnClickListener( (View v) -> {
            FirebaseManager.registraEvento(RapidaoConfigActivity.this ,"ENTROU_LISTA_FAVORITAS_CONFIG_RAPIDAO");
            Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
            String arrayAsString = new Gson().toJson(listaNumerosProibidos);
            activity.putExtra("numerosProibidos", arrayAsString);
            arrayAsString = new Gson().toJson(listaNumerosObrigatorios);
            activity.putExtra("numerosContrarios", arrayAsString);
            activity.putExtra("isTipoNrObrigatorios", false);
            startActivityForResult(activity, 7);
        });
    }

    private void gridViewApostaFavorita() {
        favoritosSelecionados = new ArrayList<>();

        LinearLayoutManager layoutManager;

        FavoritasRapidaoRecyclerAdapter apostaFavoritaAdapter  = new FavoritasRapidaoRecyclerAdapter(RapidaoConfigActivity.this,RapidaoConfigActivity.this, rapidao.getApostasFavoritas(), null, true);
        layoutManager = new LinearLayoutManager(RapidaoConfigActivity.this);

        gridViewApostaFavorita.setHasFixedSize(true);
        gridViewApostaFavorita.setAdapter(apostaFavoritaAdapter);
        gridViewApostaFavorita.setLayoutManager(layoutManager);

        favoritosSelecionados = new ArrayList<>();

        btnAddRemoverApostasFav.setOnClickListener((View v) -> {
            Intent activity = new Intent(RapidaoConfigActivity.this, RapidaoApostaFavoritasSelecaoActivity.class);
            String arrayAsString = new Gson().toJson(rapidao.getApostasFavoritas());
            activity.putExtra("apostasFavoritas", arrayAsString);

            startActivityForResult(activity, 8);
        });
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void callWebservice() {

        loadViewProgress = LoadingViewLoterias.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);

        apostaBO.getRapidoes(new RequestListener<RapidaoDTOResponse>() {

            @Override
            public void onResponse(final RapidaoDTOResponse response) {
                loadViewProgress.dismiss();
                rapidao = response.getPayload();
                FirebaseManager.registraEvento(RapidaoConfigActivity.this ,"ENTROU_CONFIGURACOES_RAPIDAO");
                processarRegistro(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, RapidaoConfigActivity.this);
            }
        });
    }


    private void processarRegistro(RapidaoDTOResponse response) {
        if (rapidao.getValorMaximo() == null) {
            rapidao.setValorMaximo(SessaoUsuario.getInstance().parametrosSimulacao.getValorLimiteDiario());
        }
        if (rapidao.getValorMinimo() == null) {
            rapidao.setValorMinimo(SessaoUsuario.getInstance().parametrosSimulacao.getValorMinimoCarrinho());
        }
        if (rapidao.getValorMinimoPremioPrincipal() == null) {
            rapidao.setValorMinimoPremioPrincipal(new BigDecimal("0.0"));
        }
        if (rapidao.getApostasFavoritas() == null) {
            rapidao.setApostasFavoritas(new ArrayList<>());
        }

        if (rapidao.getModalidades() == null) {
            rapidao.setModalidades(new ArrayList<>());
        }

        if (rapidao.getPrognosticosObrigatorios() != null) {
            listaNumerosObrigatorios = rapidao.getPrognosticosObrigatorios();
        }
        if (rapidao.getPrognosticosProibidos() != null) {
            listaNumerosProibidos = rapidao.getPrognosticosProibidos();
        }

        apostaFavoritaConfiguradasLista = rapidao.getApostasFavoritas();

        atualizaLayout();
        if (response.getRedirect() != null) {
            RedirectNetwork.checkRedirectSucesso(response.getRedirect(), RapidaoConfigActivity.this);
        }
    }

    private void atualizaLayout() {

        double valor = 1000000;
        BigDecimal valorMinimo = rapidao.getValorMinimo();
        if (rapidao.getValorMaximo() != null) {
            valor = rapidao.getValorMaximo().doubleValue();
        }
        int valueMaxApostaInt = (int) floor(valor);
        int valueMinApostaInt = (int) floor(rapidao.getValorMinimo().doubleValue());

        seekBarValorMinimoAposta.setProgress(Math.round(valueMinApostaInt));
        seekBarValorMinimoAposta.setOnSeekBarChangeListener(this);
        seekBarValorMinimoAposta.setMax(valueMaxApostaInt);
        seekBarValorMaximoAposta.setProgress(Math.round(valueMaxApostaInt));

        seekBarValorMaximoAposta.setOnSeekBarChangeListener(this);
        seekBarValorMaximoAposta.setMax((int) floor(valorMaxApostaCarrinho != null ? valorMaxApostaCarrinho.doubleValue(): 0.0));
        seekBarValorMaximoAposta.setProgress(Math.round(valueMaxApostaInt));

        //Premium Minimum
        int valuePremMinInt = (int) floor(rapidao.getValorMinimoPremioPrincipal().doubleValue());
        int valorSalvo = RapidaoSP.obterValorPremioMinimoSalvo(Math.round(valuePremMinInt));
        seekBarPremiumMimimun.setProgress(valorSalvo / 1000000);

        BigDecimal bigDecimal = new BigDecimal(valorSalvo);
        if (valorSalvo == 0) {
            bigDecimal = BigDecimal.ZERO;
        }

        tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(bigDecimal));
        tituloValorMinimoPremiumMiminum.setText("0 milhão");

        seekBarPremiumMimimun.setOnSeekBarChangeListener(this);
        seekBarPremiumMimimun.setMax(100);

        tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorMinimo));
        tituloValorMinimoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float) valueMaxApostaInt))));

        tituloValorValorMaximoAposta.setText(ViewUtils.getMoedaFormat(BigDecimal.valueOf(valor)));
        tituloValorMaximoApostaValorMinimo.setText(ViewUtils.getMoedaFormat(valorMinApostaCarrinho));
        tituloValorMaximoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(valorMaxApostaCarrinho));

        tituloValorValorMinimoAposta.addTextChangedListener(textWatcher);
        tituloValorValorMaximoAposta.addTextChangedListener(textWatcher);
        tituloValorPremiumMinimum.addTextChangedListener(textWatcher);

        montarListaEstilo();
        gridViewNumerosObrigatorios();
        gridViewNumerosProibidos();
        gridViewApostaFavorita();
        setValue = true;
    }

    private void montarListaEstilo() {
        List<ModalidadeDTO> modalides = rapidao.getModalidades();
        for (ModalidadeDTO item : modalides) {
            TipoAposta tipoAposta = null;
            switch (ModalidadeEnum.fromInteger(item.getValor())) {
                case QUINA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.quinaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotecaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotogolclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotofacilclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotomaniaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.megasenaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.timemaniaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.duplasenaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DIA_DE_SORTE:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.diasorteclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
            }
            if (tipoAposta != null) {
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale("pt", "BR")));
                tipoApostaLista.add(tipoAposta);
            }
        }
        gridViewTipoAposta();

    }

    private void postRapidoesWebservice() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);


        final List<ModalidadeDTO> tipoApostaArrayList = new ArrayList<>();
        for (TipoAposta tipoAposta : tipoApostaLista) {
            if (tipoAposta.getSelect() != null && tipoAposta.getSelect()) {
                ModalidadeDTO modalidadeDTO = new ModalidadeDTO();
                modalidadeDTO.setDescricao(tipoAposta.getTitulo());
                modalidadeDTO.setDescricaoEspecial(tipoAposta.getDescricaoEspecial());
                modalidadeDTO.setValor(tipoAposta.getValor());
                tipoApostaArrayList.add(modalidadeDTO);
            }
        }
        rapidao.setModalidades(tipoApostaArrayList);
        rapidao.setPrognosticosObrigatorios(listaNumerosObrigatorios);
        rapidao.setPrognosticosProibidos(listaNumerosProibidos);

        apostaBO.postRapidoes(rapidao, new RequestListener<RapidaoDTOResponse>() {
            @Override
            public void onResponse(RapidaoDTOResponse response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(RapidaoConfigActivity.this ,"EFETUOU_CONFIGURACOES_RAPIDAO_SUCESSO");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, RapidaoConfigActivity.this);
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //after text changed
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String valorSetado = s.toString().replaceAll("\\s","");
            valorSetado = valorSetado.replace("R$", "");
            valorSetado = valorSetado.replace("$", "");
            valorSetado = valorSetado.replace(".", "");
            valorSetado = valorSetado.replace(",", ".");

            BigDecimal valorSetadoBigDecimal = new BigDecimal(valorSetado);

            if (tituloValorValorMinimoAposta.getText().hashCode() == s.hashCode()) {
                if (valorSetadoBigDecimal.compareTo(rapidao.getValorMaximo()) > 0) {
                    rapidao.setValorMinimo(rapidao.getValorMaximo());
                    tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(rapidao.getValorMaximo()));
                } else {
                    rapidao.setValorMinimo(new BigDecimal(valorSetado));
                }
                setValue = false;
                seekBarValorMinimoAposta.setProgress((int) floor(Double.parseDouble(valorSetado)));
            } else if (tituloValorValorMaximoAposta.getText().hashCode() == s.hashCode()) {
                if (valorSetadoBigDecimal.compareTo(valorMaxApostaCarrinho) > 0) {
                    tituloValorValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorMaxApostaCarrinho));
                    valorSetado = valorMaxApostaCarrinho.toString();
                } else {
                    rapidao.setValorMaximo(valorSetadoBigDecimal);
                    tituloValorMinimoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(valorSetadoBigDecimal));
                }
                setValue = false;
                seekBarValorMaximoAposta.setProgress((int) floor(Double.parseDouble(valorSetado)));
                seekBarValorMinimoAposta.setMax((int) floor(Double.parseDouble(valorSetado)));
            } else if (tituloValorPremiumMinimum.getText().hashCode() == s.hashCode()) {
                if (!primeiroCarregamento) {
                    BigDecimal valorMaximoBarra = new BigDecimal(String.valueOf(seekBarPremiumMimimun.getMax() * 1000000));
                    if (valorSetadoBigDecimal.compareTo(valorMaximoBarra) > 0) {
                        RapidaoSP.salvarValorPremioMinimo(seekBarPremiumMimimun.getMax() * 1000000);
                        seekBarPremiumMimimun.setProgress(seekBarPremiumMimimun.getMax());
                        tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(valorMaximoBarra));
                    } else {
                        int valor = Math.round(Float.parseFloat(valorSetado));
                        RapidaoSP.salvarValorPremioMinimo(valor);
                        setValue = false;
                        seekBarPremiumMimimun.setProgress(valor  / 1000000);
                    }
                }
                primeiroCarregamento = false;
            }
        }
    };
}
