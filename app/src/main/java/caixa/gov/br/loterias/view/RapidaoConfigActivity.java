package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bean.TipoAposta;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.RapidaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.RapidaoDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import caixa.gov.br.loterias.model.sp.RapidaoSP;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ApostaFavoritaAdapter;
import caixa.gov.br.loterias.view.adapter.NumerosRapidaoAdapter;
import caixa.gov.br.loterias.view.adapter.TiposApostasAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

import static java.lang.Math.floor;

@EActivity(R.layout.activity_rapidao_config)
@OptionsMenu(R.menu.settings)
public class RapidaoConfigActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    @ViewById
    protected SeekBar seekBarValorMinimoAposta, seekBarValorMaximoAposta, seekBarPremiumMimimun;

    @ViewById
    protected com.blackcat.currencyedittext.CurrencyEditText tituloValorValorMinimoAposta, tituloValorValorMaximoAposta, tituloValorPremiumMinimum;

    @ViewById
    protected ExpandableHeightGridView gridviewNumerosProibidosRapidao, gridviewNumerosObrigatoriosRapidao, gridviewTiposApostasRapidao, gridViewApostaFavorita;

    @ViewById
    protected TextView tituloValorMinimoApostaValorMaximo, tituloValorMaximoApostaValorMaximo, tituloValorMaximoApostaValorMinimo, tituloValorMinimoApostaValorMinimo;

    @ViewById
    protected ScrollView scrollViewRapidaoConfig;

    @ViewById
    protected Toolbar toolbar;

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

        callWebservice();

        scrollViewRapidaoConfig.setFocusableInTouchMode(true);
        scrollViewRapidaoConfig.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        setValue = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

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
//            if (progressSet < 1) {
//                progressSet = 1;
//                setValue = true;
//            }
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

    }

    @Click
    protected void limparValorMinimoAposta() {
        seekBarValorMinimoAposta.setProgress(0);
    }

    @Click
    protected void limparPremioMinimoAposta() {
        seekBarPremiumMimimun.setProgress(1);
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
        final ArrayList<TipoAposta> tipoApostaArrayList = new ArrayList<TipoAposta>();
        for (TipoAposta tipoAposta : tipoApostaLista) {
            if (tipoAposta.getSelect()) {
                tipoApostaArrayList.add(tipoAposta);
            }
        }

        TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(this, tipoApostaArrayList);
        gridviewTiposApostasRapidao.setAdapter(tiposApostasAdapter);
        gridviewTiposApostasRapidao.setExpanded(true);
        gridviewTiposApostasRapidao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == tipoApostaArrayList.size()) {
                    Intent activity = new Intent(RapidaoConfigActivity.this, SelecaoModalidadesRapidaoActivity.class);
                    String arrayAsString = new Gson().toJson(tipoApostaLista);
                    activity.putExtra("tiposAposta", arrayAsString);
                    startActivityForResult(activity, 5);
                }
            }
        });
    }

    private void gridViewNumerosObrigatorios() {
        NumerosRapidaoAdapter numerosRapidaoAdapter = new NumerosRapidaoAdapter(this, listaNumerosObrigatorios, true);
        gridviewNumerosObrigatoriosRapidao.setAdapter(numerosRapidaoAdapter);
        gridviewNumerosObrigatoriosRapidao.setExpanded(true);
        gridviewNumerosObrigatoriosRapidao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == listaNumerosObrigatorios.size()) {
                    Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
                    String arrayAsString = new Gson().toJson(listaNumerosObrigatorios);
                    activity.putExtra("numerosobrigatorios", arrayAsString);
                    arrayAsString = new Gson().toJson(listaNumerosProibidos);
                    activity.putExtra("numerosContrarios", arrayAsString);
                    activity.putExtra("isTipoNrObrigatorios", true);
                    startActivityForResult(activity, 6);
                }
            }
        });
    }

    private void gridViewNumerosProibidos() {
        NumerosRapidaoAdapter numerosRapidaoAdapter2 = new NumerosRapidaoAdapter(this, listaNumerosProibidos, false);
        gridviewNumerosProibidosRapidao.setAdapter(numerosRapidaoAdapter2);
        gridviewNumerosProibidosRapidao.setExpanded(true);
        gridviewNumerosProibidosRapidao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == listaNumerosProibidos.size()) {
                    Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
                    String arrayAsString = new Gson().toJson(listaNumerosProibidos);
                    activity.putExtra("numerosProibidos", arrayAsString);
                    arrayAsString = new Gson().toJson(listaNumerosObrigatorios);
                    activity.putExtra("numerosContrarios", arrayAsString);
                    activity.putExtra("isTipoNrObrigatorios", false);
                    startActivityForResult(activity, 7);
                }
            }
        });
    }

    private void gridViewApostaFavorita() {
        ApostaFavoritaAdapter apostaFavoritaAdapter = new ApostaFavoritaAdapter(this, apostaFavoritaConfiguradasLista, favoritosSalvos);
        gridViewApostaFavorita.setAdapter(apostaFavoritaAdapter);
        gridViewApostaFavorita.setExpanded(true);
        favoritosSelecionados = new ArrayList<>();
        apostaFavoritaAdapter.setOnFavorito(new ApostaFavoritaAdapter.Favorito() {
            @Override
            public void onRemover(ApostaFavoritaDTO aposta) {
                if (favoritosSelecionados.contains(aposta)) {
                    favoritosSelecionados.remove(aposta);
                }
            }
            @Override
            public void onIncluso(ApostaFavoritaDTO aposta) {
                if (!favoritosSelecionados.contains(aposta)) {
                    favoritosSelecionados.add(aposta);
                }
            }
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
                rapidao = response.getPayload();
                carregaApostaFavoritasServer(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, RapidaoConfigActivity.this);
            }
        });
    }

    private void carregaApostaFavoritasServer(final RapidaoDTOResponse response) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);
        apostaBO.getApostasFavoritas(0, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {

            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse result) {
                favoritosSalvos = result.getPayload().getLista();
                loadViewProgress.dismiss();
                processarRegistro(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("s", "");
                favoritosSalvos = new ArrayList<>();
                loadViewProgress.dismiss();
                processarRegistro(response);
            }
        });
    }

    private void processarRegistro(RapidaoDTOResponse response) {
        //                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//                String json = gson.toJson(rapidao);
//                rapidao = gson.fromJson(json, RapidaoDTO.class);
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
            rapidao.setApostasFavoritas(new ArrayList<ApostaFavoritaDTO>());
        }

        if (rapidao.getModalidades() == null) {
            rapidao.setModalidades(new ArrayList<ModalidadeDTO>());
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

        tituloValorValorMinimoAposta.addTextChangedListener(textWatcher);
        tituloValorValorMaximoAposta.addTextChangedListener(textWatcher);
        tituloValorPremiumMinimum.addTextChangedListener(textWatcher);

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
        seekBarValorMaximoAposta.setMax((int) floor(valorMaxApostaCarrinho.doubleValue()));
        seekBarValorMaximoAposta.setProgress(Math.round(valueMaxApostaInt));

        //Premium Minimum
        int valuePremMinInt = (int) floor(rapidao.getValorMinimoPremioPrincipal().doubleValue());
        int valorSalvo = RapidaoSP.obterValorPremioMinimoSalvo(Math.round(valuePremMinInt));
        seekBarPremiumMimimun.setProgress(valorSalvo);

        BigDecimal bigDecimal = new BigDecimal(valorSalvo * 1000000.0);
        if(valorSalvo == 0){
            bigDecimal = new BigDecimal(BigInteger.ZERO);
        }

        tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(bigDecimal));

        seekBarPremiumMimimun.setOnSeekBarChangeListener(this);
        seekBarPremiumMimimun.setMax(100);

        tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorMinimo));
        tituloValorMinimoApostaValorMinimo.setText(ViewUtils.getMoedaFormat(valorMinApostaCarrinho));

        tituloValorMinimoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float) valueMaxApostaInt))));
        tituloValorValorMaximoAposta.setText(ViewUtils.getMoedaFormat(BigDecimal.valueOf(valor)));
        tituloValorMaximoApostaValorMinimo.setText(ViewUtils.getMoedaFormat(valorMinApostaCarrinho));
        tituloValorMaximoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(valorMaxApostaCarrinho));

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
//                case FEDERAL:
//                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.federalclaro, true, item.getValor(), item.getDescricaoEspecial());
//                    break;
            }
            if (tipoAposta != null) {
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase());
                tipoApostaLista.add(tipoAposta);
            }
        }
        gridViewTipoAposta();

    }

    private void postRapidoesWebservice() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);


        final List<ModalidadeDTO> tipoApostaArrayList = new ArrayList<ModalidadeDTO>();
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
        rapidao.setApostasFavoritas(favoritosSelecionados);

        apostaBO.postRapidoes(rapidao, new RequestListener<RapidaoDTOResponse>() {
            @Override
            public void onResponse(RapidaoDTOResponse response) {
                loadViewProgress.dismiss();
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
            String valorSetado = s.toString();
            valorSetado = valorSetado.replace("R$", "");
            valorSetado = valorSetado.replace("$", "");
            valorSetado = valorSetado.replace(".", "");
            valorSetado = valorSetado.replace(",", ".");

            if (tituloValorValorMinimoAposta.getText().hashCode() == s.hashCode()) {
                BigDecimal valorSetadoBigDecimal = new BigDecimal(valorSetado);
                if (valorSetadoBigDecimal.compareTo(rapidao.getValorMaximo()) > 0) {
                    rapidao.setValorMinimo(rapidao.getValorMaximo());
                    tituloValorValorMinimoAposta.setText(ViewUtils.getMoedaFormat(rapidao.getValorMinimo()));
                } else {
                    rapidao.setValorMinimo(new BigDecimal(valorSetado));
                }
                setValue = false;
                seekBarValorMinimoAposta.setProgress((int) floor(Double.parseDouble(valorSetado)));
            } else if (tituloValorValorMaximoAposta.getText().hashCode() == s.hashCode()) {
                BigDecimal valorSetadoBigDecimal = new BigDecimal(valorSetado);
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
                    int valor = Math.round(Float.parseFloat(valorSetado) / 1000000);
                    Log.i("caixa", "aaa => " + valor);
                    RapidaoSP.salvarValorPremioMinimo(valor);
                    seekBarPremiumMimimun.setProgress(valor);
                }
                primeiroCarregamento = false;
            }
            Log.i("TAG_RAPIDAO", "changeText");
        }
    };
}
