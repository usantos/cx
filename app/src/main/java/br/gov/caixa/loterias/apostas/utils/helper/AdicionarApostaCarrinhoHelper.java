package br.gov.caixa.loterias.apostas.utils.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

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
import br.gov.caixa.loterias.apostas.controllers.ConfirmadoInclusaoBolaoCarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.ConfirmadoInclusaoCarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumCharacter;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmBolao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by joafilho on 10/04/2018.
 * Class helper AdicionarApostaCarrinhoHelper
 */

public class AdicionarApostaCarrinhoHelper {
    private static Activity activity;
    private static IdentificaoDeUmaApostaDas8Modalidades aposta;
    CarrinhoDTO carrinhoDTO;
    /**
     * Conferir resultado de acordo 9.1 CSU07
     *
     * @param actv Activity
     * @param objectAposta Object
     */
    public static void incluirApostaCarrinho(Activity actv, Object objectAposta) {
        activity = actv;
        ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        aposta = getApostaPreenchida(objectAposta);
        AlertDialogUtils.show(activity);
        if (aposta != null && !checarApostasRepetidas(activity, aposta, apostaBO, ApostaOrigem.LISTA_APOSTA)) {
            postAdicionarApostaCarrinhoMinhasApostas(apostaBO);
        }
    }

    public static void incluirApostaCarrinhoMinhasApostas(Activity actv, Object objectAposta) {
        activity = actv;
        ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        AlertDialogUtils.show(activity);
        aposta = getApostaPreenchida(objectAposta);
        if (aposta != null && !checarApostasRepetidas(activity, aposta, apostaBO, ApostaOrigem.LISTA_APOSTA)) {
            postAdicionarApostaCarrinhoMinhasApostas(apostaBO);
        } else {
            AlertDialogUtils.dismiss();
            ViewUtils.alertTitleButton(activity, R.string.label_atencao, actv.getResources().getString(R.string.erro_req), activity.getResources().getString(R.string.ok));
        }
    }

    public static void incluirApostaCarrinhoCartela(Activity actv, IdentificaoDeUmaApostaDas8Modalidades objectAposta) {
        activity = actv;
        ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        aposta = objectAposta;
        AlertDialogUtils.show(activity);
        if (aposta != null) {
            prosseguirInclusaoAposta(actv,objectAposta, apostaBO);
        }
    }

    public static void adicionarApostaCarrinhoRequest(Activity actv, final ApostaBO apostaBO, IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades) {
        activity = actv;
        aposta = identificaoDeUmaApostaDas8Modalidades;
        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                checarLimiteDiario(apostaBO, aposta, response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });

    }

    public static void incluirApostaCarrinhoSurpresinha(Activity actv, final IncluirSurpresinhaDTO apostaSurpresinha) {
        activity = actv;
        ApostaBO apostaBO = ApostaBO_.getInstance_(activity);
        AlertDialogUtils.show(activity);
        apostaBO.adicionarSurpresinhaNoCarrinho(apostaSurpresinha, new RequestListener<CarrinhoDTOResponse>() {
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
                    RedirectNetwork.checkRedirect(error, (Activity) activity);
                }
            }
        });
    }

    private static void postAdicionarApostaCarrinho(ApostaBO apostaBO) {
        if (SessaoUsuario.getInstance().getSuspensaoTemporariaApostador()) {
            ViewUtils.alertTitleButton(activity, R.string.label_atencao, activity.getString(R.string.autosuspensao_alerta), activity.getString(R.string.btn_entendi));
            return;
        }
        apostaBO.adicionarApostaNoCarrinho(aposta, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse result) {
                AlertDialogUtils.dismiss();
                AppCenterManager.registraEvento("ENTROU_ADICAO_APOSTA_CARRINHO_ONLINE");
                if (CarrinhoSingleton.getInstance().getCarrinho() != null &&
                        CarrinhoSingleton.getInstance().getCarrinho().getApostas() != null) {
                        CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
                } else {
                    if (CarrinhoSingleton.getInstance().getCarrinho() == null) {
                        CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
                        CarrinhoSingleton.getInstance().getCarrinho().setValorTotal(new BigDecimal(0));
                    }
                    if (CarrinhoSingleton.getInstance().getCarrinho().getApostas() == null) {
                        CarrinhoSingleton.getInstance().getCarrinho().setApostas(new ArrayList<>());
                    }
                    CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
                }
                CarrinhoSingleton.getInstance().getCarrinho().atualizaValorTotal();
                telaConfirmarCarrinho(aposta.getModalidade(), aposta, result);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private static void postAdicionarApostaCarrinhoMinhasApostas(ApostaBO apostaBO) {
        if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && aposta.getTrevosSelecionados() != null){
            aposta.setTrevosSelecionados(AppUtils.converteListaInteiros((List<Integer>) aposta.getTrevosSelecionados()));
        }

        if (aposta.getNumerosSelecionados() != null && ((List<Integer>)aposta.getNumerosSelecionados()).size() > 0){
            DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
            indicadorSurpresinha.setValor(IndicadorSurpresinha.NAO_SURPRESINHA);
            indicadorSurpresinha.setDescricao("");
            aposta.setIndicadorSurpresinha(indicadorSurpresinha);
        }

        apostaBO.adicionarApostaNoCarrinho(aposta, new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse result) {
                AlertDialogUtils.dismiss();
                AppCenterManager.registraEvento("ENTROU_ADICAO_APOSTA_CARRINHO_ONLINE");
                CarrinhoSingleton.getInstance().getCarrinho().getApostas().add(aposta);
                activity.startActivity(new Intent(activity, CarrinhoActivity.class));
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private static void checarLimiteDiario
            (final ApostaBO apostaBO, final IdentificaoDeUmaApostaDas8Modalidades aposta, final CarrinhoDTO carrinhoDTO) {
        AlertDialogUtils.dismiss();
        BigDecimal limiteDiario = SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario();
        BigDecimal valorCarrinho = carrinhoDTO.getValorTotal().add(IdentificaoDeUmaApostaDas8Modalidades.getValorApostaPorPrognostico(aposta));
        if (valorCarrinho != null && limiteDiario != null && valorCarrinho.compareTo(limiteDiario) == 1) {
            AlertDialogUtils.dismiss();
            ViewUtils.alertTitleButtonYesOrNotListener(activity, activity.getResources().getString(R.string.MA014), (dialog, which) -> prosseguirInclusaoAposta(activity,aposta,apostaBO));
        } else {
            prosseguirInclusaoAposta(activity,aposta,apostaBO);
        }
    }

    private static void prosseguirInclusaoAposta(Activity activity, IdentificaoDeUmaApostaDas8Modalidades aposta, ApostaBO apostaBO) {
        if(!checarApostasRepetidas(activity, aposta, apostaBO, ApostaOrigem.CARROSEL)){
            postAdicionarApostaCarrinho(apostaBO);
        }
    }

    private static boolean checarApostasRepetidas(Activity activity, IdentificaoDeUmaApostaDas8Modalidades aposta, ApostaBO apostaBO, ApostaOrigem origem) {

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
                apresentaModalApostasRepetidas(apostaBO, origem);
                return true;
            }
        }
        return false;
    }

    private static void apresentaModalApostasRepetidas(ApostaBO apostaBO, ApostaOrigem origem) {
        ViewUtils.alertTitleButtonYesOrNotListener(AdicionarApostaCarrinhoHelper.activity, AdicionarApostaCarrinhoHelper.activity.getResources().getString(R.string.MA015), (dialog, which) -> {
            switch (origem) {
                case CARROSEL:
                    postAdicionarApostaCarrinho(apostaBO);
                    break;
                case LISTA_APOSTA:
                    postAdicionarApostaCarrinhoMinhasApostas(apostaBO);
                    break;
            }
        },(dialog, which) ->{
            AlertDialogUtils.dismiss();
            dialog.dismiss();
        } );
    }


    private static void telaConfirmarCarrinho(ModalidadeEnum modalidade, Object aposta) {
        telaConfirmarCarrinho(modalidade,  aposta, null);
    }

    private static void telaConfirmarCarrinho(ModalidadeEnum modalidadeEnum,  Object aposta, CarrinhoDTOResponse carrinhoDtoResponse) {
        BigDecimal valor = BigDecimal.ZERO;
        Boolean espelho = false;
        IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades = null;
        IncluirSurpresinhaDTO incluirSurpresinhaDTO = null;
        if(aposta instanceof IdentificaoDeUmaApostaDas8Modalidades){
            apostaDas8Modalidades = (IdentificaoDeUmaApostaDas8Modalidades) aposta;
            if(apostaDas8Modalidades != null){
                valor = apostaDas8Modalidades.getValor() != null ? apostaDas8Modalidades.getValor(): valor;
                espelho = apostaDas8Modalidades.getGerarApostaEspelho()!= null ? apostaDas8Modalidades.getGerarApostaEspelho(): espelho;
            }
        }else if(aposta instanceof IncluirSurpresinhaDTO){
            valor = ((IncluirSurpresinhaDTO)aposta).getValor();
            incluirSurpresinhaDTO = (IncluirSurpresinhaDTO) aposta;
        }

        if(modalidadeEnum == ModalidadeEnum.LOTOMANIA && espelho){
            valor = valor.multiply(Constantes.DOIS_BIG_DECIMAL);
        }

        Intent telaConfirmacao;
        if (apostaDas8Modalidades instanceof IdentificaoDeUmBolao){
            telaConfirmacao = new Intent(AdicionarApostaCarrinhoHelper.activity, ConfirmadoInclusaoBolaoCarrinhoActivity.class);
        } else {
            telaConfirmacao = new Intent(AdicionarApostaCarrinhoHelper.activity, ConfirmadoInclusaoCarrinhoActivity.class);
        }
        telaConfirmacao.putExtra("tipoAposta", modalidadeEnum);
        telaConfirmacao.putExtra("valorAposta", valor);
        if(apostaDas8Modalidades != null){
            if (apostaDas8Modalidades instanceof IdentificaoDeUmBolao){
                telaConfirmacao.putExtra(activity.getResources().getString(R.string.extra_numero_concurso_atual), apostaDas8Modalidades.getConcursoAlvo().toString());
                if (apostaDas8Modalidades.getDataSorteio() != null && apostaDas8Modalidades.getDataSorteio().length() >= 5) {
                    telaConfirmacao.putExtra(activity.getResources().getString(R.string.extra_data_sorteio_atual), apostaDas8Modalidades.getDataSorteio().substring(0, 5));
                }
                telaConfirmacao.putExtra(activity.getResources().getString(R.string.extra_data_hora_expiracao_reserva),
                        buscaBolaoCarrinho(((IdentificaoDeUmBolao) aposta).getIdBolao(), carrinhoDtoResponse));
            } else {
                //telaConfirmacao.putExtra("modalidade", (new Gson()).toJson(ViewUtils.getParametroSimulacao(SessaoUsuario.getInstance(), modalidadeEnum, apostaDas8Modalidades).getParametroJogo()));
                ParametroSimulacao parametroSimulacao =  ViewUtils.getParametroSimulacao(SessaoUsuario.getInstance(), modalidadeEnum, apostaDas8Modalidades);
                if (parametroSimulacao != null) {
                    telaConfirmacao.putExtra("modalidade", (new Gson()).toJson(parametroSimulacao.getParametroJogo()));
                } else
                    telaConfirmacao.putExtra("modalidade", "");
            }
        } else {
            if(incluirSurpresinhaDTO != null){
                telaConfirmacao.putExtra("modalidade", (new Gson()).toJson(ViewUtils.getParametroSimulacao(SessaoUsuario.getInstance(), modalidadeEnum, incluirSurpresinhaDTO).getParametroJogo()));

            }
        }

        AdicionarApostaCarrinhoHelper.activity.startActivity(telaConfirmacao);
        if(AdicionarApostaCarrinhoHelper.activity instanceof SimularApostaActivity){
            ((SimularApostaActivity) AdicionarApostaCarrinhoHelper.activity).zerarApostas();
        }
    }

    private static boolean inserirApostaCarrinhoLocal(Context contextParam, IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> aposta) {
        DBLoteriasCrud crud = new DBLoteriasCrud(contextParam);
        int quantidadeApostaCarrinho = crud.qtdIdentificaoDeUmaApostaDas8Modalidades();
        BigDecimal valorTotalCarrinho = crud.sumValorIdentificaoDeUmaApostaDas8Modalidades();
        valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());

        boolean isTipoLotogol = CollectionUtils.isNotEmpty(aposta.getPartidasLotogol());
        boolean isTipoLoteca = CollectionUtils.isNotEmpty(aposta.getPartidasLoteca());


        if (quantidadeApostaCarrinho >= SessaoUsuario.getInstance().getParametrosSimulacao().getQuantidadeMaximaApostasCarrinho()) {
            ViewUtils.alert(contextParam, contextParam.getString(R.string.MA013));
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
}