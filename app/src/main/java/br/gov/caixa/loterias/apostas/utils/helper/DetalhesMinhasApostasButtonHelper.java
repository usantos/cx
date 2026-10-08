package br.gov.caixa.loterias.apostas.utils.helper;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;
import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.view.activity.ResultadoApostaConfirmadaActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroTrevo;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.ApostaFavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ApostaUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MensagemUtils;
import br.gov.caixa.loterias.apostas.utils.PdfUtils;
import br.gov.caixa.loterias.apostas.utils.SelecaoConcursoUtils;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnModalidadesAbertasListener;


public class DetalhesMinhasApostasButtonHelper {
    private AppCompatActivity activity;
    private ComprovanteApostaDTO comprovanteApostaDTO;
    private DetalhesPremioDTO detalhesPremioDTO;
    private IdentificaoDeUmaApostaDas8Modalidades aposta;
    private ParametroSimulacao parametroSimulacao, parametroSimulacaoTrocado;
    private SessaoUsuario sessaoUsuario;
    private Uri fileApostaUri, filePremioUri;

    private RelativeLayout layoutnomeApostaLayout, favoritadoLayout, favoriteBtn, layoutTextoSalveAposta;
    private EditText nomeApostaEditTxt;
    private ApostaFavoritaDTO apostaFavoritaDTO;
    private ApostaFavoritaModel model;
    public DetalhesMinhasApostasButtonHelper(AppCompatActivity activity) {
        this.activity = activity;
        model = new ApostaFavoritaModel(activity);
        sessaoUsuario = SessaoUsuario.getInstance();
    }

    public void setupButtonListeners(boolean isBolao) {
        ConstraintLayout clResgatePremio = activity.findViewById(R.id.clResgatePremio);
        ConstraintLayout clResgatePremio2 = activity.findViewById(R.id.clResgatePremio2);
        RelativeLayout carrinhoApostasBtn = activity.findViewById(R.id.carrinhoApostasBtn);
        RelativeLayout editarApostaBtn = activity.findViewById(R.id.editarApostaBtn);
        RelativeLayout adicionarCarrinhoBtn = activity.findViewById(R.id.adicionarCarrinhoBtn);
        ConstraintLayout comprovanteLayout = activity.findViewById(R.id.comprovanteLayout);
        ImageView salvarApostaImg = activity.findViewById(R.id.salvarApostaImg);

        setAccessibility(carrinhoApostasBtn);

        favoritadoLayout = activity.findViewById(R.id.favoritadoLayout);
        favoriteBtn = activity.findViewById(R.id.favoriteBtn);
        layoutTextoSalveAposta  = activity.findViewById(R.id.layoutTextoSalveAposta);

        layoutnomeApostaLayout  = activity.findViewById(R.id.layoutnomeApostaLayout);
        nomeApostaEditTxt = activity.findViewById(R.id.nomeApostaEditTxt);

        if (isBolao) {
            adicionarCarrinhoBtn.setVisibility(View.GONE);
            favoriteBtn.setVisibility(View.GONE);
            favoritadoLayout.setVisibility(View.GONE);
            editarApostaBtn.setVisibility(View.GONE);
        }

        clResgatePremio.setOnClickListener(v-> startResgateActivity());
        clResgatePremio2.setOnClickListener(v-> startResgateActivity());
        carrinhoApostasBtn.setOnClickListener(v -> carrinhoApostasBtn());
        editarApostaBtn.setOnClickListener(v -> editarApostaBtn());
        adicionarCarrinhoBtn.setOnClickListener(v -> adicionarCarrinhoBtn());
        comprovanteLayout.setOnClickListener(v -> getComprovantePdf());
        salvarApostaImg.setOnClickListener(v -> salvarApostaImg());

        favoriteBtn.setOnClickListener(v-> favoriteBtn());
        favoritadoLayout.setOnClickListener(v -> favoritadoLayout());
        layoutTextoSalveAposta.setOnClickListener(v -> layoutTextoSalveAposta());
    }

    private void setAccessibility(RelativeLayout carrinhoApostasBtn) {
        ViewCompat.setAccessibilityDelegate(carrinhoApostasBtn, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);

                info.setClassName(android.widget.Button.class.getName());
                info.setClickable(true);
                info.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            }
        });
    }

    public void setupDTO(ComprovanteApostaDTO comprovanteApostaDTO, DetalhesPremioDTO detalhesPremioDTO, IdentificaoDeUmaApostaDas8Modalidades aposta) {
        this.comprovanteApostaDTO = comprovanteApostaDTO;
        this.detalhesPremioDTO = detalhesPremioDTO;
        this.aposta = aposta;

        parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());
        parametroSimulacaoTrocado = ViewUtils.getParametroSimulacao(sessaoUsuario, aposta.getModalidade());

    }

    private void startResgateActivity() {
        Intent intent = IntentUtil.getIntentOrigemDestino(activity, ResultadoApostaConfirmadaActivity.class);
        intent.putExtra(ResultadoApostaConfirmadaActivity.DETALHES_PREMIO_DTO_EXTRA, ((Serializable) detalhesPremioDTO));
        intent.putExtra(ResultadoApostaConfirmadaActivity.COMPROVANTE_EXTRA, ((Serializable) comprovanteApostaDTO));
        activity.startActivity(intent);
    }
    private void carrinhoApostasBtn() {
        AlertDialogUtils.show(activity);

        ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
                }
                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
                activity.startActivity(new Intent(activity, CarrinhoActivity.class));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    activity.startActivity(new Intent(activity, CarrinhoActivity.class).putExtra(LER_CARRINHO_LOCAL, true));
                } else {
                    RedirectNetwork.checkRedirect( error, activity);
                }
            }
        });
    }
    private void editarApostaBtn() {
        AdicionarApostaCarrinhoHelper.checagemModadalidadesAbertas(aposta.getModalidade(), onChegagemModalidadesListener(true));
    }
    private void adicionarCarrinhoBtn() {
        AdicionarApostaCarrinhoHelper.checagemModadalidadesAbertas(aposta.getModalidade(), onChegagemModalidadesListener(false));
    }
    private void goToEditarAposta(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta){
        if (CollectionUtils.isNotEmpty(aposta.getNumerosSelecionados())) {
            Intent intent = IntentUtil.getIntentOrigemDestino(activity, SimulaActivity.class);
            intent.putExtra(SimulaActivity.APOSTA_EXTRA, aposta);
            intent.putExtra("tipoAposta", ((Serializable) aposta.getModalidade()));
            intent.putExtra("modalidade", ((Serializable) (new Gson()).toJson(parametroSimulacaoTrocado.getParametroJogo())));
            intent.putExtra("especial", isEspecial(aposta.getTipoConcurso().getValor()));
            activity.startActivity(intent);
        }
    }

    private OnModalidadesAbertasListener onChegagemModalidadesListener(boolean isEditing){
        return new OnModalidadesAbertasListener() {
            @Override
            public void ambasAbertas(List<ParametroSimulacao> arrayConcursos) {
                SelecaoConcursoUtils.selecionarConcurso(activity, arrayConcursos,
                        (tipoConcurso) -> {
                            IdentificaoDeUmaApostaDas8Modalidades clone = aposta;
                            if (!tipoConcurso.toString().equalsIgnoreCase(aposta.getTipoConcurso().getValor())) {
                                clone = ApostaUtils.converteTipoDeConcurso(aposta);
                            }
                            parametroSimulacaoTrocado = ViewUtils.getParametroSimulacao(sessaoUsuario, clone.getModalidade(), clone.getTipoConcurso().getValor());
                            if (isEditing){
                                goToEditarAposta(clone);
                            } else {
                                adicionaApostaCarrinho(clone);
                            }
                        });
            }

            @Override
            public void ambasFechadas() {
                AlertDialogUtils.dismiss();
                DialogUtils.dialogEntendi(
                        activity,
                        "Modalidade não disponível no momento."
                );
            }

            @Override
            public void unicaModalidade(List<ParametroSimulacao> arrayConcursos, TipoConcursoEnum tipoConcursoEnum) {
                if (tipoConcursoEnum.getValor().equals(aposta.getTipoConcurso().getValor())){
                    if (isEditing){
                        goToEditarAposta(aposta);
                    } else {
                        adicionaApostaCarrinho(aposta);
                    }
                } else {
                    String msg = MensagemUtils.getMensagemConverteModalidade(aposta.getModalidade(), aposta.getTipoConcurso().getValor());

                    DialogUtils.dialogConfirmar(
                            activity,
                            msg,
                            new OnDialogBotaoListener() {
                                @Override
                                public void onButtonClick(DialogInterface dialog, int which) {
                                    IdentificaoDeUmaApostaDas8Modalidades clone =  ApostaUtils.converteTipoDeConcurso(aposta);
                                    parametroSimulacaoTrocado = ViewUtils.getParametroSimulacao(sessaoUsuario, clone.getModalidade(), clone.getTipoConcurso().getValor());
                                    if (isEditing){
                                        goToEditarAposta(clone);
                                    } else {
                                        adicionaApostaCarrinho(clone);
                                    }
                                }
                            }
                    );
                }
            }
        };
    }

    private void adicionaApostaCarrinho(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {

        IdentificaoDeUmaApostaDas8Modalidades<List<List<Integer>>> apostaMatriz;

        if (comprovanteApostaDTO != null) {
            if (comprovanteApostaDTO.getAposta().getModalidade().equals(ModalidadeEnum.SUPER_7)) {
                apostaMatriz = comprovanteApostaDTO.getAposta();
                apostaMatriz.setNumerosSelecionados(comprovanteApostaDTO.getAposta().getMatrizNumerosSelecionados());
            }
        }
        ModalidadeEnum modalidade = (aposta != null && aposta.getModalidade()!= null) ? aposta.getModalidade(): null;
        if (modalidade != null && !modalidade.equals(ModalidadeEnum.LOTECA)){
            if (!modalidade.equals(ModalidadeEnum.SUPER_7)) {
                aposta.setNumerosSelecionados(aposta.getListaNumerosSelecionados());
            } else {
                apostaMatriz = comprovanteApostaDTO.getAposta();
                apostaMatriz.setNumerosSelecionados(comprovanteApostaDTO.getAposta().getMatrizNumerosSelecionados());
                AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(activity, apostaMatriz);
                return;
            }
        }

        AdicionarApostaCarrinhoHelper.incluirApostaCarrinho(activity, aposta);
    }

    private boolean isEspecial(String tipoConcurso){
        return tipoConcurso.equalsIgnoreCase("2");
    }

    private void getComprovantePdf() {
        boolean premioPago = true;
        if (comprovanteApostaDTO != null) {
            premioPago = false;
        }
        if(!premioPago){
            if(fileApostaUri != null){
                PdfUtils.abrirPdf(fileApostaUri, activity);
                fileApostaUri = null;
            } else {
                AlertDialogUtils.show(activity);
                try {
                    ApostaSilceBO.getInstance().baixarComprovanteAposta(aposta.getId(), new RequestListener<String>() {
                        @Override
                        public void onResponse(String  response) {
                            AlertDialogUtils.dismiss();
                            fileApostaUri = PdfUtils.baixarPdfAposta(response, activity);
                            PdfUtils.abrirPdf(fileApostaUri, activity);
                            fileApostaUri = null;
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            RedirectNetwork.checkRedirect(error,activity);
                            error.getLocalizedMessage();
                        }
                    });

                }catch (Exception e){
                    Log.d("ApostaConfirmadaDetAct", "Não foi possível acessar o serviço para baixar comprovante de aposta");
                }
            }
        }else{
            if(filePremioUri != null){
                PdfUtils.abrirPdf(filePremioUri, activity);
                filePremioUri = null;
            } else {
                AlertDialogUtils.show(activity);
                try{
                    ApostaSilceBO.getInstance().baixarComprovantePremio(aposta.getId(), new RequestListener<String>() {
                        @Override
                        public void onResponse(String  response) {
                            AlertDialogUtils.dismiss();
                            filePremioUri = PdfUtils.baixarPdfPremio(response, activity);
                            PdfUtils.abrirPdf(filePremioUri, activity);
                            filePremioUri = null;
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialogUtils.dismiss();
                            RedirectNetwork.checkRedirect(error,activity);
                            error.getLocalizedMessage();
                        }
                    });
                }catch (Exception e){
                    Log.d("ApostaConfirmadaDetAct", "Não foi possível acessar o serviço para baixar comprovante de prêmio");
                }
            }
        }
    }


    private void salvarApostaImg() {
        if (org.apache.commons.lang.StringUtils.isNotEmpty(nomeApostaEditTxt.getText().toString())) {
            AlertDialogUtils.show(activity);

            ParametroJogoDTO parametroJogo;
            apostaFavoritaDTO = new ApostaFavoritaDTO();
            if (parametroSimulacao != null && parametroSimulacao.getParametroJogo() != null) {
                parametroJogo = parametroSimulacao.getParametroJogo();
                DTOEnumInteger modalidade = new DTOEnumInteger();
                modalidade.setDescricao(parametroJogo.getConcurso().getModalidadeDetalhada().getDescricao());
                modalidade.setValor(parametroJogo.getConcurso().getModalidadeDetalhada().getValor());
                apostaFavoritaDTO.setModalidade(modalidade);
            }
            if (nomeApostaEditTxt != null && nomeApostaEditTxt.getText() != null && nomeApostaEditTxt.getText().toString() != null){
                if(nomeApostaEditTxt.getText().toString().length() > 25){
                    apostaFavoritaDTO.setNome(nomeApostaEditTxt.getText().toString().substring(0, 25));
                } else {
                    apostaFavoritaDTO.setNome(nomeApostaEditTxt.getText().toString());
                }
            } else {
                apostaFavoritaDTO.setNome("");
            }
            if(aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                apostaFavoritaDTO.setParametroTrevo(new ParametroTrevo());

                apostaFavoritaDTO.getParametroTrevo().setTrevosSelecionados(aposta.getTrevosSelecionados());
            }
            if(aposta.getModalidade() == ModalidadeEnum.SUPER_7){
                apostaFavoritaDTO.setNumerosSelecionados(aposta.getMatrizNumerosSelecionados());
            } else {
                apostaFavoritaDTO.setNumerosSelecionados(aposta.getListaNumerosSelecionados());
            }
            apostaFavoritaDTO.setTimeDoCoracao(aposta.getTimeDoCoracao());
            apostaFavoritaDTO.setMesDeSorte(aposta.getMesDeSorte());


            model.validaAposta(apostaFavoritaDTO, onValidaApostaListener());

        } else {
            DialogUtils.dialogEntendi(
                    activity,
                    activity.getString(R.string.msg_favor_inserir_nome_aposta)
            );
        }
    }
    private OnSilceListener<NetworkResponse> onValidaApostaListener() {
        return new OnSilceListener<NetworkResponse>() {
            @Override
            public void success(NetworkResponse payload) {
                salvarAposta();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                if(isErroNegocial(error)){
                    DialogUtils.dialogSim(
                            activity,
                            MensagensNetwork.getErrorMessage(error),
                            new OnDialogBotaoListener() {
                                @Override
                                public void onButtonClick(DialogInterface dialog, int which) {
                                    salvarAposta();
                                }
                            }
                    );
                } else {
                    RedirectNetwork.checkRedirect(error, activity);
                };
            }
        };
    }

    private void salvarAposta(){
        model.salvaAposta(apostaFavoritaDTO, new OnSilceListener<NetworkResponse>() {
            @Override
            public void success(NetworkResponse payload) {
                AlertDialogUtils.dismiss();
                favoritadoLayout.setVisibility(View.VISIBLE);
                layoutnomeApostaLayout.setVisibility(View.GONE);
                favoriteBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void favoriteBtn() {
        if (layoutnomeApostaLayout.getVisibility() == View.VISIBLE){
            layoutnomeApostaLayout.setVisibility(View.GONE);
        } else {
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        }
        if(favoritadoLayout.getVisibility() == View.VISIBLE){
            favoritadoLayout.setVisibility(View.INVISIBLE);
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        }
        favoriteBtn.setVisibility(View.VISIBLE);
    }

    private void favoritadoLayout() {
        if(favoritadoLayout.getVisibility() == View.VISIBLE){
            favoritadoLayout.setVisibility(View.INVISIBLE);
            layoutnomeApostaLayout.setVisibility(View.VISIBLE);
            favoriteBtn.setVisibility(View.VISIBLE);
        }
    }

    private void layoutTextoSalveAposta() {
        layoutnomeApostaLayout.setVisibility(View.VISIBLE);
        layoutTextoSalveAposta.setClickable(false);
    }
}