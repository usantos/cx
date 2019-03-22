package br.gov.caixa.loterias.apostar.interno.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.CarrinhoApostasListViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;

@EActivity(R.layout.activity_carrinho)
public class CarrinhoActivity extends SettingToolbarActivity {

    @ViewById
    protected ExpandableHeightRecyclerView apostasListaRecycleView;

    @ViewById
    protected TextView valorTotalCarrinho, infoValorMinimo;

    @ViewById
    protected Button botaoAvancarPagamento, botaoFazerNovaAposta;

    @ViewById
    protected LinearLayout limparCarrinhoLinearLayout;

    @Extra
    protected CarrinhoDTO carrinho;

    @Extra
    protected boolean lerCarrinhoLocal;

    @AfterViews
    public void init() {
        ViewUtils.setMoedaFormatHtml(BigDecimal.ZERO, valorTotalCarrinho);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_carrinho)));
        if(lerCarrinhoLocal){
            FirebaseManager.registraEvento(CarrinhoActivity.this ,"ENTROU_CARRINHO_DE_APOSTAS_OFFLINE");
            lerCarrinhoLocal(true);
        }else{
            atualizaLayout(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    public void lerCarrinhoLocal(Boolean animacao) {
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        carrinho = new CarrinhoDTO();
        carrinho.setApostas(crud.readAllIdentificaoDeUmaApostaDas8Modalidades());
        atualizaLayout(animacao);
    }

    public void atualizaLayout(boolean animacao) {
        montarLayout();
        try {
            if (carrinho != null) {
                if (carrinho.getApostas() != null) {
                    configuraRecyclerViewCarrinhoAposta(animacao);
                    setValorTotalCarrinho();
                }
            }  else {
                callWebservice();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        botaoAvancarPagamento.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.avancarFormaPagamento)));

    }

    public void setCarrinho(CarrinhoDTO carrinho) {
        this.carrinho = carrinho;
    }

    @Click(R.id.botaoAvancarPagamento)
    protected void clickBotaoAvancarPagamento() {
        Gson gson = new Gson();
        String json = gson.toJson(carrinho);
        Intent activity = new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class);
        activity.putExtra("CARRINHO", json);
        startActivity(activity);
    }

    @Click(R.id.botaoFazerNovaAposta)
    protected void clickBotaoFazerNovaAposta() {
        setResult(2);
        finish();
    }

    @Click(R.id.limparCarrinhoLinearLayout)
    protected void clickLimparCarrinhoLinearLayout() {
        ViewUtils.alertTitleButtonYesOrNotListener(this, getString(R.string.MA011), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogUtils.show(CarrinhoActivity.this);
                ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);
                apostaBO.limparCarrinho(new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse result) {
                        FirebaseManager.registraEvento(CarrinhoActivity.this ,"LIMPOU_CARRINHO_DE_APOSTAS_ONLINE");
                        DialogUtils.dismiss();
                        limparCarrinhoAtualizarTela();
                        if (result.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( result.getRedirect(), CarrinhoActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
                        if (MensagensNetwork.isUnauthorizedError(error)) {
                            deleteCarrinhoLocal();
                        } else {
                            RedirectNetwork.checkRedirect( error, CarrinhoActivity.this );
                        }
                    }
                });
            }
        });

    }

    private void deleteCarrinhoLocal() {
        FirebaseManager.registraEvento(CarrinhoActivity.this ,"LIMPOU_CARRINHO_DE_APOSTAS_LOCAL");
        DBLoteriasCrud crud = new DBLoteriasCrud(getApplicationContext());
        crud.deleteAll();
        limparCarrinhoAtualizarTela();
    }

    private void limparCarrinhoAtualizarTela() {
        carrinho.getApostas().removeAll(carrinho.getApostas());
        carrinho.setValorTotal(BigDecimal.ZERO);
        atualizaLayout(Boolean.FALSE);
    }

    private void montarLayout() {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        BigDecimal valorCarrinho = sessaoUsuario.getValorMinimimoAposta() == null ? BigDecimal.ZERO : sessaoUsuario.getValorMinimimoAposta();
        ViewUtils.setMoedaFormatHtml(valorCarrinho, infoValorMinimo, getResources().getString(R.string.label_lembrar_valor_minimo).replace("_", "<br>") + " ");
    }

    private void callWebservice() {

        DialogUtils.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(CarrinhoActivity.this);

        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                FirebaseManager.registraEvento(CarrinhoActivity.this ,"ENTROU_CARRINHO_DE_APOSTAS_ONLINE");

                DialogUtils.dismiss();
                carrinho = response.getPayload();
                atualizaLayout(Boolean.TRUE);
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CarrinhoActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    lerCarrinhoLocal(Boolean.TRUE);
                } else {
                    RedirectNetwork.checkRedirect( error, CarrinhoActivity.this );
                }
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(boolean animacao) {
        List<IdentificaoDeUmaApostaDas8Modalidades> listIdentificaoDeUmaApostaDas8Modalidades = new ArrayList<>();
        for (IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades : carrinho.getApostas()) {
            if (identificaoDeUmaApostaDas8Modalidades.getModalidade() != null) {
                listIdentificaoDeUmaApostaDas8Modalidades.add(identificaoDeUmaApostaDas8Modalidades);
            }
        }
        if (carrinho.getApostas().size() > 0) {
            limparCarrinhoLinearLayout.setVisibility(View.VISIBLE);
        } else {
            limparCarrinhoLinearLayout.setVisibility(View.GONE);
        }
        apostasListaRecycleView.setLayoutManager(new LinearLayoutManager(this));
        apostasListaRecycleView.setAdapter(new CarrinhoApostasListViewAdapter(this, (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) listIdentificaoDeUmaApostaDas8Modalidades, this, animacao));
        apostasListaRecycleView.setExpanded(Boolean.TRUE);

    }

    private void setValorTotalCarrinho() {
        BigDecimal valorTotalCarrinho = BigDecimal.ZERO;
        for (IdentificaoDeUmaApostaDas8Modalidades aposta : carrinho.getApostas()) {
            valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());
        }
        ViewUtils.setMoedaFormatHtml(valorTotalCarrinho, this.valorTotalCarrinho);
    }
}
