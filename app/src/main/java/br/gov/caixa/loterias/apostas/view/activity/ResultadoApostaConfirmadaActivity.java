package br.gov.caixa.loterias.apostas.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.ResgatePixActivity;
import br.gov.caixa.loterias.apostas.controllers.JogosConfirmadosPremioMercadoPagoActivity;
import br.gov.caixa.loterias.apostas.controllers.ResgatePremioLotericaActivity;
import br.gov.caixa.loterias.apostas.controllers.ResgatePremioLotericaAgenciaActivity;
import br.gov.caixa.loterias.apostas.controllers.SettingToolbarActivity;
import br.gov.caixa.loterias.apostas.factory.ResultadoApostaConfirmadaFactory;
import br.gov.caixa.loterias.apostas.model.bean.OpcaoResgatePremio;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.OpcaoResgatePremioAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PremiacaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;
import br.gov.caixa.loterias.apostas.viewModel.ResultadoApostaConfirmadaViewModel;

public class ResultadoApostaConfirmadaActivity extends SettingToolbarActivity {

    public final static String DETALHES_PREMIO_DTO_EXTRA = "detalhesPremioDTO";
    public final static String COMPROVANTE_EXTRA = "comprovante";
    public static String DETALHES = "DETALHES";
    public static String COMPROVANTE = "COMPROVANTE";
    public static String CODIGO_RESGATE = "CODIGO_RESGATE";
    public static int RESGATOU_MERCADO_PAGO = 200;
    private RecyclerView rvConcursos, rvResgatePremio;
    private AlertDialog alertDialog;

    private ImageView ivTrevoResultado;
    private TextView tvTituloModalidade, tvConcursos, tvConcurso, tvPremioTitulo, tvEscolhaPremio, tvPodeComemorar;
    private ConstraintLayout clTopbarModalidade, clBackgroundTopbar,
            clFocoConcurso, clTopoResultadoPremio, clMidLayout;

    private TipoApostaLinhaView talvHeader;
    private ResultadoApostaConfirmadaViewModel viewModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_aposta_confirmada);
        init();
    }

    protected void init() {
        getExtras();
        setViews();
        createToolBar();

        montaTipoApostaLinha(viewModel.getDetalhesPremio().getAposta(), viewModel.getEstiloModalidadeMKP().getCorEscura(),
                viewModel.getEstiloModalidadeMKP().getCorFonteFundoEscuro(), viewModel.getEstiloModalidadeMKP().getCorFonteFundoEscuro());

        if (isEspecial()) {
            clTopbarModalidade.setBackground(ContextCompat.getDrawable(this, viewModel.getEstiloModalidadeMKP().getImagemEspecialSimples()));
        } else {
            clTopbarModalidade.setBackgroundColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorClara()));
        }

        clBackgroundTopbar.setBackground(ContextCompat.getDrawable(this, viewModel.getEstiloModalidadeMKP().getTrapezio()));
        ivTrevoResultado.setImageResource(viewModel.getEstiloModalidadeMKP().getTrevoFundoEscuro());

        if (isBolao(viewModel.getDetalhesPremio().getAposta())) {
            clBackgroundTopbar.setBackground(ContextCompat.getDrawable(this, viewModel.getEstiloModalidadeMKP().getTrapezio()));
            ivTrevoResultado.setImageResource(viewModel.getEstiloModalidadeMKP().getTrevoFundoEscuro());
        }

        //cores
        tvConcurso.setTextColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorFonteFundoClaro()));
        tvConcursos.setTextColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorFonteFundoClaro()));
        tvTituloModalidade.setTextColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorFonteFundoClaro()));
        tvPodeComemorar.setTextColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorFonteFundoClaro()));
        tvPremioTitulo.setTextColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorFonteFundoClaro()));
        clTopoResultadoPremio.setBackgroundColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorClara()));
        clMidLayout.setBackgroundColor(ContextCompat.getColor(this, viewModel.getEstiloModalidadeMKP().getCorClara()));

        String descricaoModalidade;
        if (EspecialUtils.isMega30(viewModel.getDetalhesPremio().getAposta().getConcursoAlvo(), viewModel.getDetalhesPremio().getAposta().getTipoConcurso())){
            descricaoModalidade = SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_LINHA, "");
            tvTituloModalidade.setTextSize(20);
        } else {
            descricaoModalidade = ViewUtils.getNomeModalidadePorAposta(viewModel.getDetalhesPremio().getAposta()).toLowerCase();
        }
        tvTituloModalidade.setText(descricaoModalidade);

        String fim = viewModel.getDetalhesPremio().getAposta().getConcursoAlvo().toString();
        if (viewModel.getDetalhesPremio().getAposta().getQuantidadeTeimosinhas() > 0) {
            tvConcursos.setText(R.string.txt_concursos);
            fim = viewModel.getConcursoRange();
        }

        if(viewModel.getDetalhesPremio().getAposta().getConcursoAlvo()>0
                && !viewModel.getDetalhesPremio().getAposta().getConcursoAlvo().toString()
                .equals(viewModel.getDetalhesPremio().getAposta().getConcursoInicial().toString())) {
            fim = viewModel.getDetalhesPremio().getAposta().getConcursoInicial().toString()
                + " - " + viewModel.getDetalhesPremio().getAposta().getConcursoAlvo().toString();
            tvConcursos.setText(R.string.txt_concursos);
        }

        tvConcurso.setText(fim);

        if (viewModel.getDetalhesPremio().getPremio().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(viewModel.getDetalhesPremio().getPremio().getValorLiquido()) == 0)) {
            tvPremioTitulo.setText(getResources().getString(R.string.label_voce_foi_premiado).replace(getResources().getString(R.string.chave_valor_premio_chave), ViewUtils.getMoedaFormat(viewModel.getDetalhesPremio().getPremio().getValorLiquido())));
        }
        else {
            tvPremioTitulo.setText(R.string.label_voce_foi_premiado_exclamacao);
        }

        createListPremiacao();
        trataFormasDeResgate();
//        RateUtils.avaliarAplicativo(this, true);
        setResult(RESULT_FIRST_USER);
    }

    private boolean isEspecial() {
        return (viewModel.getDetalhesPremio().getAposta().getTipoConcurso().getValor().equalsIgnoreCase("2"));
    }

    private void montaTipoApostaLinha(IdentificaoDeUmaApostaDas8Modalidades aposta,
                                      int backGroundColorRes, int textColorRes,
                                      int iconColorRes) {

        List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

        if (aposta.getTroca()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
        }
        if (aposta.getIndicadorCotaBolao()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
        }
        if (aposta.getCombo()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
        }
        if (aposta.getEspelho()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
        }
        if (aposta.getSurpresinha()) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
        }
        if (aposta.getQuantidadeTeimosinhas() > 0) {
            listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
        }

        if (!listTipoApostaLinha.isEmpty()) {
            talvHeader.setVisibility(View.VISIBLE);
            talvHeader.setupView(listTipoApostaLinha,
                    backGroundColorRes, textColorRes, iconColorRes,
                    TipoApostaLinhaView.TriangleDirection.NONE);
        } else {
            talvHeader.setVisibility(View.GONE);
        }
    }

    private void getExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            DetalhesPremioDTO detalhesPremio = null;
            ComprovanteApostaDTO comprovanteAposta = null;
            if (extras_.containsKey(DETALHES_PREMIO_DTO_EXTRA)) {
                detalhesPremio = ((DetalhesPremioDTO) extras_.getSerializable(DETALHES_PREMIO_DTO_EXTRA));
            }
            if (extras_.containsKey(COMPROVANTE_EXTRA)) {
                comprovanteAposta = ((ComprovanteApostaDTO) extras_.getSerializable(COMPROVANTE_EXTRA));
            }
            viewModel = new ViewModelProvider(this,
                    new ResultadoApostaConfirmadaFactory(detalhesPremio, comprovanteAposta, SessaoUsuario.getInstance()))
                    .get(ResultadoApostaConfirmadaViewModel.class);
        }
    }

    private void setViews() {
        this.clTopbarModalidade = findViewById(R.id.clTopbarModalidade);
        this.clBackgroundTopbar = findViewById(R.id.clBackgroundTopbar);
        this.clFocoConcurso = findViewById(R.id.clFocoConcurso);
        this.clTopoResultadoPremio = findViewById(R.id.clTopoResultadoPremio);
        this.clMidLayout = findViewById(R.id.clMidLayout);
        this.tvTituloModalidade = findViewById(R.id.tvTituloModalidade);
        this.tvConcursos = findViewById(R.id.tvConcursos);
        this.tvConcurso = findViewById(R.id.tvFaixaConcursos);
        this.tvPremioTitulo = findViewById(R.id.tvPremioTitulo);
        this.tvPodeComemorar = findViewById(R.id.tvPodeComemorar);
        this.ivTrevoResultado = findViewById(R.id.ivTrevoResultado);
        this.talvHeader = findViewById(R.id.talvHeader);
        this.rvConcursos = findViewById(R.id.rvConcursos);
        this.rvResgatePremio = findViewById(R.id.rvResgatePremio);
    }

    protected void apresentaOrientacoesResgate(Boolean isLoterica) {
        alertDialog = LoadingViewLoterias.show(getContext());
        ApostaSilceBO.getInstance().getApostaGerarCodigoResgate(viewModel.getDetalhesPremio().getAposta().getId(), new RequestListener<CodigoResgateDTOResponse>() {
            @Override
            public void onResponse(CodigoResgateDTOResponse result) {
                alertDialog.dismiss();
                if(isLoterica){
                    Intent it = new Intent(ResultadoApostaConfirmadaActivity.this, ResgatePremioLotericaActivity.class);
                    it.putExtra(DETALHES, viewModel.getDetalhesPremio());
                    it.putExtra(COMPROVANTE, viewModel.getComprovanteAposta());
                    it.putExtra(CODIGO_RESGATE, result.getPayload());
                    startActivity(new Intent(it));
                }else {
                    Intent intent = IntentUtil.getIntentOrigemDestino((Activity) getContext(), ResgatePremioLotericaAgenciaActivity.class);
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.CODIGO_RESGATE_DTO_EXTRA, result.getPayload());
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.IS_LOTERICA_EXTRA, isLoterica);
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.DETALHES_PREMIO_DTO_EXTRA, viewModel.getDetalhesPremio());
                    intent.putExtra(ResgatePremioLotericaAgenciaActivity.COMPROVANTE_EXTRA, viewModel.getComprovanteAposta());
                    getContext().startActivity(intent);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                String msgError = MensagensNetwork.setMensagem(error, ResultadoApostaConfirmadaActivity.this);
                DialogUtils.dialogEntendi(
                        getContext(),
                        msgError
                );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESGATOU_MERCADO_PAGO && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    private void trataFormasDeResgate() {
        ArrayList<OpcaoResgatePremio> list = new ArrayList<>();
        OpcaoResgatePremio header = new OpcaoResgatePremio();
        list.add(header);
        for (int i = 0; i < viewModel.getDetalhesPremio().getMeiosResgate().size(); i++){
            TipoPagamentoDTO meioPagamento = viewModel.getDetalhesPremio().getMeiosResgate().get(i);
            if (meioPagamento.getId().equals(MeioPagamentoUtil.PIX)) {
                list.add(new OpcaoResgatePremio(ContextCompat.getDrawable(getContext(), R.drawable.icone_do_pix),
                        "PIX", "Receba na hora", v -> {
                    Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, ResgatePixActivity.class);
                    intent.putExtra(ResgatePixActivity.ARG_VALOR, viewModel.getDetalhesPremio().getPremio().getValorLiquido());
                    intent.putExtra(ResgatePixActivity.ARG_ID_APOSTA, viewModel.getDetalhesPremio().getAposta().getId());
                    startActivity(intent);
                }));
            } else if (meioPagamento.getId().equals(MeioPagamentoUtil.MERCADO_PAGO)) {
                list.add(new OpcaoResgatePremio(ContextCompat.getDrawable(getContext(), R.drawable.logo_mercado_pago),
                        "Mercado Pago","Receba na hora", v -> {
                    Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, JogosConfirmadosPremioMercadoPagoActivity.class);
                    intent.putExtra( getResources().getString(R.string.extra_valor_premio), ViewUtils.getMoedaFormat(viewModel.getDetalhesPremio().getPremio().getValorLiquido()) );
                    intent.putExtra( getResources().getString(R.string.extra_id_aposta), viewModel.getDetalhesPremio().getAposta().getId() );
                    startActivityForResult(intent,RESGATOU_MERCADO_PAGO);
                }));
            } else if (meioPagamento.getId().equals(MeioPagamentoUtil.LOTERICA)) {
                list.add(new OpcaoResgatePremio(ContextCompat.getDrawable(getContext(), R.drawable.icon_loteria),
                        "Lotérica", "Retire em qualquer lotérica", v -> {
                    apresentaOrientacoesResgate(true);
                }));
            } else if (meioPagamento.getId().equals(MeioPagamentoUtil.AGENCIA)) {
                list.add(new OpcaoResgatePremio(ContextCompat.getDrawable(getContext(), R.drawable.icon_caixa),
                        "Agência Caixa", "Valores acima da isenção do IR", v -> {
                    apresentaOrientacoesResgate(false);
                }));
            }
        }

        rvResgatePremio.setLayoutManager(new LinearLayoutManager(this));
        rvResgatePremio.setAdapter(new OpcaoResgatePremioAdapter(list));
    }

    private boolean isBolao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta != null && aposta.getIndicadorCotaBolao() != null && aposta.getIndicadorCotaBolao();
    }

    private void createListPremiacao() {
        PremiacaoAdapter adapter = new PremiacaoAdapter();
        adapter.submitList(viewModel.getListItemLoteria());
        adapter.submitColor(viewModel.getEstiloModalidadeMKP());
        rvConcursos.setLayoutManager(new LinearLayoutManager(this));
        rvConcursos.setAdapter(adapter);
    }

    private void createToolBar() {
        setTitle(ViewUtils.textCaixaSTDBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
    }

    private Context getContext() {
        return ResultadoApostaConfirmadaActivity.this;
    }

}
