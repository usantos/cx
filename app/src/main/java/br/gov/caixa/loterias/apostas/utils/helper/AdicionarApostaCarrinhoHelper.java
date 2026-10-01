package br.gov.caixa.loterias.apostas.utils.helper;

import static br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity.LER_CARRINHO_LOCAL;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumCharacter;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.LotecaActivity;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.activity.SimulaActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;
import br.gov.caixa.loterias.apostas.view.listener.OnModalidadesAbertasListener;

/**
 * Created by joafilho on 10/04/2018.
 * Class helper AdicionarApostaCarrinhoHelper
 */

public class AdicionarApostaCarrinhoHelper {
    private static Activity activity;
    private static IdentificaoDeUmaApostaDas8Modalidades aposta;
    /**
     * Conferir resultado de acordo 9.1 CSU07
     *
     * @param actv Activity
     * @param objectAposta Object
     */
    public static void incluirApostaCarrinho(Activity actv, Object objectAposta) {
        activity = actv;
        if(AdicionarApostaCarrinhoHelper.usuarioEstaSuspenso(activity, 2)) return;
        aposta = getApostaPreenchida(objectAposta);
        AlertDialogUtils.show(activity);
        if (aposta != null && !checarApostasRepetidas(activity, aposta, ApostaOrigem.LISTA_APOSTA)) {
            postAdicionarApostaCarrinhoMinhasApostas();
        }
    }

    public static void incluirApostaCarrinhoMinhasApostas(Activity actv, Object objectAposta) {
        activity = actv;
        AlertDialogUtils.show(activity);
        aposta = getApostaPreenchida(objectAposta);
        if (aposta != null && !checarApostasRepetidas(activity, aposta, ApostaOrigem.LISTA_APOSTA)) {
            postAdicionarApostaCarrinhoMinhasApostas();
        } else {
            AlertDialogUtils.dismiss();
            //ViewUtils.alertTitleButton(activity, R.string.label_atencao, actv.getResources().getString(R.string.erro_req), activity.getResources().getString(R.string.ok));
        }
    }

    public static void incluirApostaCarrinhoCartela(Activity actv, IdentificaoDeUmaApostaDas8Modalidades objectAposta, BarraTituloDTO barraTituloDTO) {
        activity = actv;
        aposta = objectAposta;
        AlertDialogUtils.show(activity);
        if (aposta != null) {
            if (barraTituloDTO != null && (barraTituloDTO.getDataSorteio() != null && barraTituloDTO.getNumeroConcurso() != null)){
                prosseguirInclusaoAposta(actv,objectAposta, barraTituloDTO);
            }else{
                prosseguirInclusaoAposta(actv, objectAposta, null);
            }
        }
    }
    public static void incluirApostaCarrinhoCartela(Activity actv, IdentificaoDeUmaApostaDas8Modalidades objectAposta) {
        activity = actv;
        aposta = objectAposta;
        AlertDialogUtils.show(activity);
        prosseguirInclusaoAposta(actv, objectAposta, null);
    }


    public static void incluirApostaCarrinhoSurpresinha(Activity actv, final IncluirSurpresinhaDTO apostaSurpresinha) {
        activity = actv;
        AlertDialogUtils.show(activity);
        //Servico Nuvem (obrigatorio)
        apostaSurpresinha.setSurpresinha(true);
        ServicoFactoryUtil.getApostaService().adicionarSurpresinhaNoCarrinho(apostaSurpresinha, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                AlertDialogUtils.dismiss();
                CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());
                telaConfirmarCarrinho(apostaSurpresinha.getModalidade(), apostaSurpresinha);
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), (Activity) activity);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    inserirSurpresinhaCarrinhoLocal(apostaSurpresinha);
                    telaConfirmarCarrinho(apostaSurpresinha.getModalidade(), apostaSurpresinha);
                } else {
                    RedirectNetwork.checkRedirect(error, activity);
                }
            }
        });
    }



    private static void postAdicionarApostaCarrinho(BarraTituloDTO barraTituloDTO) {

        final BarraTituloDTO finalBarraTituloDTO = barraTituloDTO;

        ServicoFactoryUtil.getApostaService().adicionarApostaNoCarrinho(aposta, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse result) {
                AlertDialogUtils.dismiss();
                AppCenterManager.registraEvento("ENTROU_ADICAO_APOSTA_CARRINHO_ONLINE");
                if (CarrinhoSingleton.getInstance().getCarrinho() != null &&
                        CarrinhoSingleton.getInstance().getCarrinho().getApostas() != null) {
                    CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
                } else {
                    if (CarrinhoSingleton.getInstance().getCarrinho() == null) {
                        CarrinhoSingleton.getInstance().zerarCarrinho();
                    }
                    if (CarrinhoSingleton.getInstance().getCarrinho().getApostas() == null) {
                        CarrinhoSingleton.getInstance().getCarrinho().setApostas(new ArrayList<>());
                    }
                    CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
                }
                CarrinhoSingleton.getInstance().getCarrinho().atualizaValorTotal();
                if (finalBarraTituloDTO != null && (finalBarraTituloDTO.getDataSorteio() != null && finalBarraTituloDTO.getNumeroConcurso() != null)){
                    telaConfirmarCarrinho(aposta.getModalidade(), aposta, result, finalBarraTituloDTO);

                } else {
                    telaConfirmarCarrinho(aposta.getModalidade(), aposta, result, null);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error, finalBarraTituloDTO);
            }
        });
    }

    private static void postAdicionarApostaCarrinhoMinhasApostas() {
        if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && aposta.getTrevosSelecionados() != null){
            aposta.setTrevosSelecionados(AppUtils.converteListaInteiros((List<Integer>) aposta.getTrevosSelecionados()));
        }

        if (aposta.getNumerosSelecionados() != null && ((List<Integer>)aposta.getNumerosSelecionados()).size() > 0){
            DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
            indicadorSurpresinha.setValor(IndicadorSurpresinha.NAO_SURPRESINHA);
            indicadorSurpresinha.setDescricao("");
            aposta.setIndicadorSurpresinha(indicadorSurpresinha);
        }
        ServicoFactoryUtil.getApostaService().adicionarApostaNoCarrinho(aposta, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse result) {
                try {
                    AlertDialogUtils.dismiss();
                    AppCenterManager.registraEvento("ENTROU_ADICAO_APOSTA_CARRINHO_ONLINE");
                    CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
                    CarrinhoSingleton.getInstance().getCarrinho().setValorTotal(result.getPayload().getValorTotal());
                } catch (Exception e){
                    Log.d("", e.getLocalizedMessage());
                }

                DialogUtils.dialogEntendiListener(activity, activity.getString(R.string.added_bet_to_cart), (dialog, which) -> {
                    if (activity instanceof MinhasApostasActivity) {
                        ((MinhasApostasActivity) activity).outDialogEntendiListener();
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private static void prosseguirInclusaoAposta(Activity activity, IdentificaoDeUmaApostaDas8Modalidades aposta, BarraTituloDTO barraTituloDTO) {
        if(!checarApostasRepetidas(activity, aposta, ApostaOrigem.CARROSEL)){
            if (barraTituloDTO != null && (barraTituloDTO.getDataSorteio() != null && barraTituloDTO.getNumeroConcurso() != null)){
                postAdicionarApostaCarrinho(barraTituloDTO);
            }
            else {
                postAdicionarApostaCarrinho(null);
            }
        }
    }

    private static boolean checarApostasRepetidas(Activity activity, IdentificaoDeUmaApostaDas8Modalidades aposta, ApostaOrigem origem) {
        if (isBolao(aposta)) {
            return false;
        }

        ArrayList<IdentificaoDeUmaApostaDas8Modalidades> apostas = new ArrayList<>();
        if (DadosUsuarioBO.checarUsuarioLogado(activity)) {
            if(CarrinhoSingleton.getInstance().getCarrinho() != null &&
                    CarrinhoSingleton.getInstance().getCarrinho().getApostas() != null){
                apostas = (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) CarrinhoSingleton.getInstance().getCarrinho().getApostas();
            }
        } else {
            DBLoteriasCrud crud = new DBLoteriasCrud(activity);
            apostas = (ArrayList<IdentificaoDeUmaApostaDas8Modalidades>) crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
        }

        for (IdentificaoDeUmaApostaDas8Modalidades ap: apostas) {
            if(aposta.getTimeDoCoracao() == null){
                aposta.setTimeDoCoracao(new ParametroEquipe());
            }
            if(aposta.getMesDeSorte() == null){
                aposta.setMesDeSorte(new ParametroMesDeSorte());
            }
            if(ap.equals(aposta)){
                apresentaModalApostasRepetidas(origem);
                return true;
            }
        }
        return false;
    }

    private static void apresentaModalApostasRepetidas(ApostaOrigem origem) {
        DialogUtils.dialogSimNao(AdicionarApostaCarrinhoHelper.activity, activity.getResources().getString(R.string.MA015),
                new OnDialogDoisBotoesListener() {
                    @Override
                    public void PositiveButton(DialogInterface dialog, int which) {
                        switch (origem) {
                            case CARROSEL:
                                postAdicionarApostaCarrinho(null);
                                break;
                            case LISTA_APOSTA:
                                postAdicionarApostaCarrinhoMinhasApostas();
                                break;
                        }
                    }

                    @Override
                    public void NegativeButton(DialogInterface dialog, int which) {
                        AlertDialogUtils.dismiss();
                    }
                }
        );
    }

    private static void telaConfirmarCarrinho(ModalidadeEnum modalidade, Object aposta) {
        telaConfirmarCarrinho(modalidade,  aposta, null, null);
    }
    private static void telaConfirmarCarrinho(ModalidadeEnum modalidade, Object aposta, BarraTituloDTO barraTituloDTO) {
        telaConfirmarCarrinho(modalidade,  aposta, null, barraTituloDTO);
    }

    private static void telaConfirmarCarrinho(ModalidadeEnum modalidadeEnum,  Object aposta, CarrinhoDTOResponse carrinhoDtoResponse, BarraTituloDTO barraTituloDTO) {
        if (AdicionarApostaCarrinhoHelper.activity instanceof SimulaActivity) {
            ((SimulaActivity) AdicionarApostaCarrinhoHelper.activity)
                    .mostrarPopupApostaAdicionada();
        }else if(AdicionarApostaCarrinhoHelper.activity instanceof LotecaActivity){
            ((LotecaActivity) AdicionarApostaCarrinhoHelper.activity)
                    .mostrarPopupApostaAdicionada();
        }else {
            if (AdicionarApostaCarrinhoHelper.activity instanceof OnCompraBolaoListener) {
                goToCarrinho();
            } else if(AdicionarApostaCarrinhoHelper.activity instanceof SimularApostaActivity) {
                ((SimularApostaActivity<?>) AdicionarApostaCarrinhoHelper.activity).mostrarPopupApostaAdicionada(
                        carrinhoDtoResponse
                );
            }
        }
    }
    private static void goToCarrinho(){
        ServicoFactoryUtil.getApostaService().buscaCarrinho(
                new RequestListener<CarrinhoDTOResponse>() {
                    @Override
                    public void onResponse(CarrinhoDTOResponse response) {
                        CarrinhoSingleton.getInstance().setCarrinho(response.getPayload());

                        Intent intent = new Intent(
                                activity,
                                CarrinhoActivity.class
                        );

                        if (activity instanceof OnCompraBolaoListener) {
                            ((OnCompraBolaoListener) activity)
                                    .compraSucesso(intent);
                        } else {
                            activity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent(
                                activity,
                                CarrinhoActivity.class
                        );

                        intent.putExtra(LER_CARRINHO_LOCAL, true);
                        if (activity instanceof OnCompraBolaoListener) {
                            ((OnCompraBolaoListener) activity)
                                    .compraSucesso(intent);
                        } else {
                            activity.startActivity(intent);
                        }
                    }
                });
    }

    private static boolean inserirApostaCarrinhoLocal(Context contextParam, IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
        DBLoteriasCrud crud = new DBLoteriasCrud(contextParam);
        int quantidadeApostaCarrinho = crud.qtdIdentificaoDeUmaApostaDas8Modalidades();

        boolean isTipoLotogol = CollectionUtils.isNotEmpty(aposta.getPartidasLotogol());
        boolean isTipoLoteca = CollectionUtils.isNotEmpty(aposta.getPartidasLoteca());


        if (SessaoUsuario.getInstance().getParametrosSimulacao() != null &&
                SessaoUsuario.getInstance().getParametrosSimulacao().getQuantidadeMaximaApostasCarrinho() != null &&
                quantidadeApostaCarrinho >= SessaoUsuario.getInstance().getParametrosSimulacao().getQuantidadeMaximaApostasCarrinho()) {
            DialogUtils.dialogEntendi(contextParam, contextParam.getString(R.string.MA013));
            return Boolean.FALSE;
        } else {
            if (aposta.getModalidade() == ModalidadeEnum.LOTOMANIA && aposta.getGerarApostaEspelho() && aposta.getNumerosSelecionados() != null) {
                IdentificaoDeUmaApostaDas8Modalidades apostaEspelho = IdentificaoDeUmaApostaDas8Modalidades.clone(aposta);
                List<Integer> numerosEspelho = new ArrayList<>();

                for (int i = 1; i <= 100; i++) {
                    if (!aposta.getNumerosSelecionados().contains(i)) {
                        numerosEspelho.add(i);
                    }
                }

                apostaEspelho.setNumerosSelecionados(numerosEspelho);

                apostaEspelho.setEspelho(true);
                aposta.setGerarEspelho(true);
                Long idAposta = crud.insertIdentificaoDeUmaApostaDas8Modalidades(aposta);

                ContentValues cv = new ContentValues();
                cv.put("vinculo_espelho", idAposta + aposta.getConcursoAlvo());
                crud.update(cv, "_ID="+ idAposta);

                Long idApostaEspelho = crud.insertIdentificaoDeUmaApostaDas8Modalidades(apostaEspelho);

                cv = new ContentValues();
                cv.put("vinculo_espelho", idAposta + aposta.getConcursoAlvo());
                crud.update(cv, "_ID="+ idApostaEspelho);
            } else {
                Long id = crud.insertIdentificaoDeUmaApostaDas8Modalidades(aposta);

                if (isTipoLotogol) {
                    for (PartidaLotogolDTO partidaLotogolDTO : aposta.getPartidasLotogol()) {
                        crud.inserirPartidaLotogol(partidaLotogolDTO, id);
                    }
                } else if (isTipoLoteca) {
                    for (PartidaLotecaDTO partidaLotecaDTO : aposta.getPartidasLoteca()) {
                        crud.inserirPartidaLoteca(partidaLotecaDTO, id);
                    }
                }
            }
            AppCenterManager.registraEvento("ENTROU_ADICAO_APOSTA_CARRINHO_OFFLINE");

            return Boolean.TRUE;
        }
    }

    private static IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida(Object objectAposta) {
        if (objectAposta instanceof ApostaDTO) {
            return getApostaPreenchidaFromApostaDTO((ApostaDTO) objectAposta);
        } else if (objectAposta instanceof IdentificaoDeUmaApostaDas8Modalidades) {
            return getApostaPreenchidaFromIdentificaoDeUmaApostaDas8Modalidades((IdentificaoDeUmaApostaDas8Modalidades) objectAposta);
        } else {
            return null;
        }
    }

    private static IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchidaFromApostaDTO(ApostaDTO apostaDTO) {
        ModalidadeEnum modalidadeEnum = apostaDTO.getModalidade();
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);
        IdentificaoDeUmaApostaDas8Modalidades aposta = null;

        if (parametroSimulacao != null && parametroSimulacao.getParametroJogo() != null) {

            aposta = new IdentificaoDeUmaApostaDas8Modalidades();

            aposta.setId(Constantes.ZERO_LONG);
            aposta.setModalidade(parametroSimulacao.getParametroJogo().getConcurso().getModalidade());

            DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

           /* String valorTipoConcurso = parametroSimulacao.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
            tipoConcurso.setValor(valorTipoConcurso);
            tipoConcurso.setDescricao(parametroSimulacao.getParametroJogo().getConcurso().getTipoConcurso().toString());*/
            aposta.setTipoConcurso(apostaDTO.getTipoConcurso());
            if(apostaDTO.getModalidade() == ModalidadeEnum.SUPER_7){
                aposta.setNumerosSelecionados(apostaDTO.getMatrizNumerosSelecionados());
            } else {
                aposta.setNumerosSelecionados(apostaDTO.getListaNumerosSelecionados());

            }

            if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
                aposta.setTrevosSelecionados(apostaDTO.getListaTrevosSelecionados());
            }
            DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
            indicadorSurpresinha.setValor(IndicadorSurpresinha.NAO_SURPRESINHA);
            indicadorSurpresinha.setDescricao("");

            aposta.setIndicadorSurpresinha(indicadorSurpresinha);
            aposta.setValor(apostaDTO.getValor());
            aposta.setConcursoAlvo(parametroSimulacao.getParametroJogo().getConcurso().getNumero());
            aposta.setQuantidadeTeimosinhas(apostaDTO.getQuantidadeTeimosinhas());
            aposta.setQuantidadeApostas(1);
            aposta.setEspelho(false);

            aposta.setTimeDoCoracao(apostaDTO.getTimeDoCoracao());
            aposta.setMesDeSorte(apostaDTO.getMesDeSorte());

            aposta.setConcursoInicial(0);
            aposta.setSituacao(null);
            aposta.setTroca(false);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = new Date();

            DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Date hour = new Date();

            aposta.setDataEfetivacao(dateFormat.format(date));
            aposta.setHoraEfetivacao(hourFormat.format(hour));
            aposta.setVinculoEspelho(Constantes.ZERO_LONG);
            aposta.setQuantidadeNumeros(apostaDTO.getQuantidadeNumeros());
            aposta.setSurpresinha(false);

            setApostaLoteca(aposta, apostaDTO);
            setApostaLotogol(aposta, apostaDTO);
        }

        return aposta;
    }

    private static IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchidaFromIdentificaoDeUmaApostaDas8Modalidades(IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades) {
        ModalidadeEnum modalidadeEnum = apostaDas8Modalidades.getModalidade();
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        if (parametroSimulacao != null && parametroSimulacao.getParametroJogo() != null) {

            ParametroJogoDTO parametroJogo = parametroSimulacao.getParametroJogo();

            apostaDas8Modalidades.setId(Constantes.ZERO_LONG);

            DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

            /*String valorTipoConcurso = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
            tipoConcurso.setValor(valorTipoConcurso);
            tipoConcurso.setDescricao(parametroJogo.getConcurso().getTipoConcurso().toString());
            apostaDas8Modalidades.setTipoConcurso(tipoConcurso);*/

            apostaDas8Modalidades.setConcursoAlvo(parametroJogo.getConcurso().getNumero());
            apostaDas8Modalidades.setQuantidadeApostas(1);
            apostaDas8Modalidades.setSurpresinha(false);
            apostaDas8Modalidades.setSituacao(null);
            apostaDas8Modalidades.setTroca(false);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = new Date();

            DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            Date hour = new Date();

            apostaDas8Modalidades.setDataEfetivacao(dateFormat.format(date));
            apostaDas8Modalidades.setHoraEfetivacao(hourFormat.format(hour));
        }

        return apostaDas8Modalidades;
    }

    private static void inserirSurpresinhaCarrinhoLocal(IncluirSurpresinhaDTO aposta) {
        IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades = new IdentificaoDeUmaApostaDas8Modalidades();
        apostaDas8Modalidades.setModalidade(aposta.getModalidade());
        apostaDas8Modalidades.setQuantidadeNumeros(aposta.getQuantidadeNumeros());
        apostaDas8Modalidades.setQuantidadeSurpresinhas(aposta.getQuantidadeSurpresinhas());
        apostaDas8Modalidades.setTipoConcurso(aposta.getTipoConcurso());
        apostaDas8Modalidades.setIndicadorSurpresinha(aposta.getIndicadorSurpresinha());
        apostaDas8Modalidades.setConcursoAlvo(aposta.getConcursoAlvo());
        apostaDas8Modalidades.setQuantidadeTeimosinhas(aposta.getQuantidadeTeimosinhas());
        apostaDas8Modalidades.setQuantidadeApostas(aposta.getQuantidadeApostas());
        if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
            apostaDas8Modalidades.setQuantidadeTrevos(aposta.getQtdTrevos());
        }
        apostaDas8Modalidades.setTimeDoCoracao(aposta.getTimeDoCoracao());
        apostaDas8Modalidades.setSurpresinha(true);
        apostaDas8Modalidades.setGerarEspelho(aposta.getGerarEspelho());

        BigDecimal valorIndividual = aposta.getValor().divide(new BigDecimal(aposta.getQuantidadeSurpresinhas()));
        for (int contador = 0; contador < aposta.getQuantidadeSurpresinhas(); contador++) {
            apostaDas8Modalidades.setValor(valorIndividual);
            if (aposta.getGerarEspelho() != null && aposta.getGerarEspelho()) {
                apostaDas8Modalidades.setValor(valorIndividual.divide(Constantes.DOIS_BIG_DECIMAL));
                apostaDas8Modalidades.setEspelho(aposta.getGerarEspelho());
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(activity, apostaDas8Modalidades);
                apostaDas8Modalidades.setEspelho(Boolean.FALSE);
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(activity, apostaDas8Modalidades);
            } else if (CollectionUtils.isNotEmpty(aposta.getListaTimeDoCoracao())) {
                apostaDas8Modalidades.setTimeDoCoracao(aposta.getListaTimeDoCoracao().get(contador));
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(activity, apostaDas8Modalidades);
            } else {
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(activity, apostaDas8Modalidades);
            }
        }
    }

    private static void setApostaLoteca(IdentificaoDeUmaApostaDas8Modalidades
                                                apostaDas8Modalidades, Object object) {
        List<PartidaLotecaDTO> partidaLotecaList = null;
        if (object instanceof ApostaDTO) {
            ApostaDTO aposta = (ApostaDTO) object;
            partidaLotecaList = aposta.getPartidasLoteca();
        } else if (object instanceof IdentificaoDeUmaApostaDas8Modalidades) {
            IdentificaoDeUmaApostaDas8Modalidades aposta = (IdentificaoDeUmaApostaDas8Modalidades) object;
            partidaLotecaList = aposta.getPartidasLoteca();
        }
        apostaDas8Modalidades.setPartidasLoteca(partidaLotecaList);
    }

    private static void setApostaLotogol(IdentificaoDeUmaApostaDas8Modalidades
                                                 apostaDas8Modalidades, Object object) {
        List<PartidaLotogolDTO> partidaLotogolList = null;
        if (object instanceof ApostaDTO) {
            ApostaDTO aposta = (ApostaDTO) object;
            partidaLotogolList = aposta.getPartidasLotogol();
        } else if (object instanceof IdentificaoDeUmaApostaDas8Modalidades) {
            IdentificaoDeUmaApostaDas8Modalidades aposta = (IdentificaoDeUmaApostaDas8Modalidades) object;
            partidaLotogolList = aposta.getPartidasLotogol();
        }
        apostaDas8Modalidades.setPartidasLotogol(partidaLotogolList);
    }

    private static void onError(VolleyError error) {
        AlertDialogUtils.dismiss();
        if (MensagensNetwork.isUnauthorizedError(error)) {
            if (AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(activity, aposta)) {
                telaConfirmarCarrinho(aposta.getModalidade(), aposta);
            }
        } else {
            RedirectNetwork.checkRedirect(error, (Activity) activity);
        }
    }
    private static void onError(VolleyError error, BarraTituloDTO barraTituloDTO) {
        AlertDialogUtils.dismiss();
        if (MensagensNetwork.isUnauthorizedError(error)) {
            if (AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(activity, aposta)) {
                telaConfirmarCarrinho(aposta.getModalidade(), aposta, barraTituloDTO);
            }
        } else {
            RedirectNetwork.checkRedirect(error, (Activity) activity);
        }
    }

    private enum ApostaOrigem {
        CARROSEL, LISTA_APOSTA;
    }

    private static String buscaBolaoCarrinho(String bolaoId, CarrinhoDTOResponse carrinhoDtoResponse) {
        List<IdentificaoDeUmaApostaDas8Modalidades> listBoloes = carrinhoDtoResponse.getPayload().getBoloes();

        for (IdentificaoDeUmaApostaDas8Modalidades bolao: listBoloes) {
            if (bolao.getReservaCotaBolao().getCodigoBolaoReserva().equals(bolaoId)) {
                return bolao.getReservaCotaBolao().getDataHoraExpiracaoReserva();
            }
        }
        return null;
    }

    public static Boolean usuarioEstaSuspenso(Activity actv) {
        return usuarioEstaSuspenso(actv, 0);
    }

    public static Boolean usuarioEstaSuspenso(Activity actv, int acaoPosDismiss) {
        if (SessaoUsuario.getInstance().getSuspensaoTemporariaApostador()) {

            DialogUtils.dialogEntendiListener(actv, actv.getString(R.string.autosuspensao_alerta_nova),
                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (acaoPosDismiss) {
                                case 0:
                                    actv.finish();
                                    return;
                                case 1:
                                    //PrincipalActivity_.intent(actv).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
                                    Intent intent = IntentUtil.getIntentOrigemDestino(actv, PrincipalActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    actv.startActivity(intent);

                                    actv.finish();
                                    return;
                                default:
                                    return;
                            }
                        }
                    }
            );
            return true;
        }
        return false;
    }

    public static void checagemModadalidadesAbertas(ModalidadeEnum modalidadeEnum, OnModalidadesAbertasListener listener){
        List<ParametroSimulacao> arrayConcursos = new ArrayList<>();

        ParametrosSimulacao paramsSimulacao = SessaoUsuario.getInstance().getParametrosSimulacao();
        if (paramsSimulacao == null) {
            listener.ambasFechadas();
            return;
        }

        List<ParametroSimulacao> parametroSimulacaoList = paramsSimulacao.getParametros();
        if (parametroSimulacaoList == null) {
            listener.ambasFechadas();
            return;
        }

        for (ParametroSimulacao concurso : parametroSimulacaoList) {
            if (concurso.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum) {
                arrayConcursos.add(concurso);
            }
        }

        if (arrayConcursos.size() > 1) {
            listener.ambasAbertas(arrayConcursos);
        } else {
            if (arrayConcursos.isEmpty()){
                listener.ambasFechadas();
            } else {
                listener.unicaModalidade(arrayConcursos, arrayConcursos.get(0).getParametroJogo().getConcurso().getTipoConcurso());
            }
        }
    }

    private static boolean isBolao(IdentificaoDeUmaApostaDas8Modalidades aposta) {
        return aposta != null && aposta.getIndicadorCotaBolao() != null && aposta.getIndicadorCotaBolao();
    }
}