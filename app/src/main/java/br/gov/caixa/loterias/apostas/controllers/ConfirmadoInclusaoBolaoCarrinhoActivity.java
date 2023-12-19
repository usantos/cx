package br.gov.caixa.loterias.apostas.controllers;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.CARRINHO;
import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.RodapeSimulacaoApostaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SubBarraModalidadeFragment;

public class ConfirmadoInclusaoBolaoCarrinhoActivity extends LoteriasAppMarketPlaceActivity {
    private SubBarraModalidadeFragment subBarFragment;
    private RodapeSimulacaoApostaFragment rodapeFragment;
    private ModalidadeEnum modalidade;
    private BigDecimal valorAposta;
    private String concurso;
    private String dataSorteio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_bolao_carrinho);

        configToolbar(R.id.toolbar);
        configBackground(R.color.mega_verde_escuro_mkp);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_apostas bolão_"));

        getExtras();
        setViews();
        startFragments();
    }

    private void startFragments() {
        subBarFragment = FragmentUtils.startFragmentSubBarraModalidade(getSupportFragmentManager(),
                R.id.id_fragment_sub_barra, modalidade, concurso, dataSorteio);
        rodapeFragment = FragmentUtils.startRodapeSimulacaoAPosta(getSupportFragmentManager(),
                R.id.id_fragment_rodape, valorAposta);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getExtras(){
        modalidade = (ModalidadeEnum) getIntent().getSerializableExtra(getResources().getString(R.string.tipoAposta));
        valorAposta = (BigDecimal) getIntent().getSerializableExtra(getResources().getString(R.string.valorAposta));
        concurso = getIntent().getStringExtra("");
        dataSorteio = getIntent().getStringExtra("");
    }

    private void setViews(){

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirmarInclusaoApostaLayoutCarrinhoApostas: {
                AlertDialogUtils.show(this);
                ApostaBO apostaBO = ApostaBO_.getInstance_(this);

                apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse response) {
                        AlertDialogUtils.dismiss();
                        Bundle args = new Bundle();
                        args.putSerializable(CARRINHO, (Serializable) response.getPayload());
                        startActivityForResult(new Intent(ConfirmadoInclusaoBolaoCarrinhoActivity.this, CarrinhoActivity.class).putExtras(args),0);
                        if (response.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ConfirmadoInclusaoBolaoCarrinhoActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialogUtils.dismiss();
                        if (MensagensNetwork.isUnauthorizedError(error)) {
                            startActivityForResult((new Intent(ConfirmadoInclusaoBolaoCarrinhoActivity.this, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true)),0);
                        } else {
                            RedirectNetwork.checkRedirect( error, ConfirmadoInclusaoBolaoCarrinhoActivity.this );
                        }
                    }
                });
            }
            break;
            case R.id.botaoLinearLayoutVoltarPrincipal: {
                PrincipalActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
            }
            break;
        }
    }

}
