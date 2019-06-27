package br.gov.caixa.loterias.apostar.interno.utils.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumCharacter;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostar.interno.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostar.interno.utils.Constantes;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.ConfirmadoInclusaoCarrinhoActivity;
import br.gov.caixa.loterias.apostar.interno.view.SimularApostaActivity;

/**
 * Created by joafilho on 10/04/2018.
 * Class helper AdicionarApostaCarrinhoHelper
 */

public class AdicionarApostaCarrinhoHelper {
    private static Context context;
    private static IdentificaoDeUmaApostaDas8Modalidades aposta;

    /**
     * Conferir resultado de acordo 9.1 CSU07
     *
     * @param contextParam Context
     * @param objectAposta Object
     */
    public static void incluirApostaCarrinho(Context contextParam, Object objectAposta) {
        context = contextParam;
        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
        aposta = getApostaPreenchida(objectAposta);
        DialogUtils.show(context);
        if (aposta != null) {
            postAdicionarApostaCarrinho(apostaBO);
        }
    }

    public static void incluirApostaCarrinhoCartela(Context contextParam, IdentificaoDeUmaApostaDas8Modalidades objectAposta) {
        context = contextParam;
        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
        aposta = objectAposta;
        DialogUtils.show(context);
        if (aposta != null) {
            if (KeycloakBO_.getInstance_(context).getAccessToken() == null || KeycloakBO_.getInstance_(context).getAccessToken().isEmpty()) {
                postAdicionarApostaCarrinho(apostaBO);
            } else {
                adicionarApostaCarrinhoRequest(context, apostaBO, aposta);
            }
        }
    }

    public static void adicionarApostaCarrinhoRequest(Context contextParam, final ApostaBO apostaBO, IdentificaoDeUmaApostaDas8Modalidades identificaoDeUmaApostaDas8Modalidades) {
        context = contextParam;
        aposta = identificaoDeUmaApostaDas8Modalidades;
        apostaBO.buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoDTOResponse response) {
                checarLimiteDiario(apostaBO, aposta, response.getPayload());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, (Activity) context);
            }
        });

    }

    public static void incluirApostaCarrinhoSurpresinha(Context contextParam, final IncluirSurpresinhaDTO apostaSurpresinha) {
        context = contextParam;
        ApostaBO apostaBO = ApostaBO_.getInstance_(context);
        DialogUtils.show(context);
        apostaBO.adicionarSurpresinhaNoCarrinho(apostaSurpresinha, new RequestListener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse response) {
                DialogUtils.dismiss();
                telaConfirmarCarrinho(apostaSurpresinha.getModalidade(), apostaSurpresinha);
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), (Activity) context);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                if (MensagensNetwork.isUnauthorizedError(error)) {
                    inserirSurpresinhaCarrinhoLocal(apostaSurpresinha);
                    telaConfirmarCarrinho(apostaSurpresinha.getModalidade(), apostaSurpresinha);
                } else {
                    RedirectNetwork.checkRedirect(error, (Activity) context);
                }
            }
        });
    }

    private static void postAdicionarApostaCarrinho(ApostaBO apostaBO) {
        apostaBO.adicionarApostaNoCarrinho(aposta, new RequestListener<AgrupadorDTOApostaDTOResponse>() {
            @Override
            public void onResponse(AgrupadorDTOApostaDTOResponse result) {
                DialogUtils.dismiss();
                FirebaseManager.registraEvento(context ,"ENTROU_ADICAO_APOSTA_CARRINHO_ONLINE");

                telaConfirmarCarrinho(aposta.getModalidade(), aposta);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        });
    }

    private static void checarLimiteDiario
            (final ApostaBO apostaBO, final IdentificaoDeUmaApostaDas8Modalidades aposta, final CarrinhoDTO carrinhoDTO) {
        if (SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario()
                .compareTo(carrinhoDTO.getValorTotal()
                        .add(IdentificaoDeUmaApostaDas8Modalidades.getValorApostaPorPrognostico(aposta)))
                < 0) {
            DialogUtils.dismiss();
            ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA014), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checarApostasRepetidas(apostaBO, aposta, carrinhoDTO);
                }
            });
        } else {
            checarApostasRepetidas(apostaBO, aposta, carrinhoDTO);
        }
    }

    private static void checarApostasRepetidas(final ApostaBO apostaBO, final IdentificaoDeUmaApostaDas8Modalidades aposta, final CarrinhoDTO carrinhoDTO) {
        if (IdentificaoDeUmaApostaDas8Modalidades.checkExisteUmaApostaIgualCarrinho(aposta, carrinhoDTO)) {
            DialogUtils.dismiss();
            ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.MA015), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogUtils.show(context);
                    postAdicionarApostaCarrinho(apostaBO);
                }
            });
        } else {
            postAdicionarApostaCarrinho(apostaBO);
        }
    }

    private static void telaConfirmarCarrinho(ModalidadeEnum modalidadeEnum,  Object aposta) {
        BigDecimal valor = BigDecimal.ZERO;
        Boolean espelho = false;

        if(aposta instanceof IdentificaoDeUmaApostaDas8Modalidades){
            valor = ((IdentificaoDeUmaApostaDas8Modalidades)aposta).getValor();
            espelho = ((IdentificaoDeUmaApostaDas8Modalidades)aposta).getGerarApostaEspelho();
        }else if(aposta instanceof IncluirSurpresinhaDTO){
            valor = ((IncluirSurpresinhaDTO)aposta).getValor();
        }

        if(modalidadeEnum == ModalidadeEnum.LOTOMANIA && espelho){
            valor = valor.multiply(Constantes.DOIS_BIG_DECIMAL);
        }

        Intent activity = new Intent(context, ConfirmadoInclusaoCarrinhoActivity.class);
        activity.putExtra("tipoAposta", modalidadeEnum);
        activity.putExtra("valorAposta", valor);
        activity.putExtra("modalidade", (new Gson()).toJson(ViewUtils.getParametroSimulacao(SessaoUsuario.getInstance(), modalidadeEnum).getParametroJogo()));
        context.startActivity(activity);
        if((Activity)context instanceof SimularApostaActivity){
            ((SimularApostaActivity)context).zerarApostas();
        }
    }

    private static boolean inserirApostaCarrinhoLocal(Context contextParam, IdentificaoDeUmaApostaDas8Modalidades aposta) {
        DBLoteriasCrud crud = new DBLoteriasCrud(contextParam);
        int quantidadeApostaCarrinho = crud.qtdIdentificaoDeUmaApostaDas8Modalidades();
        BigDecimal valorTotalCarrinho = crud.sumValorIdentificaoDeUmaApostaDas8Modalidades();
        valorTotalCarrinho = valorTotalCarrinho.add(aposta.getValor());

        boolean isTipoLotogol = CollectionUtils.isNotEmpty(aposta.getPartidasLotogol());
        boolean isTipoLoteca = CollectionUtils.isNotEmpty(aposta.getPartidasLoteca());

        //TODO Verificar os parametros que indicam aposta igual
        if (crud.existIdentificaoDeUmaApostaDas8Modalidades(aposta) && (!isTipoLotogol && !isTipoLoteca)) {
            ViewUtils.alert(contextParam, contextParam.getString(R.string.MA015));
            return Boolean.FALSE;
        } else if (quantidadeApostaCarrinho >= SessaoUsuario.getInstance().getParametrosSimulacao().getQuantidadeMaximaApostasCarrinho()) {
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
            FirebaseManager.registraEvento(context ,"ENTROU_ADICAO_APOSTA_CARRINHO_OFFLINE");


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

            String valorTipoConcurso = parametroSimulacao.getParametroJogo().getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
            tipoConcurso.setValor(valorTipoConcurso);
            tipoConcurso.setDescricao(parametroSimulacao.getParametroJogo().getConcurso().getTipoConcurso().toString());
            aposta.setTipoConcurso(tipoConcurso);
            aposta.setNumerosSelecionados(apostaDTO.getNumerosSelecionados());

            DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
            indicadorSurpresinha.setValor(1);
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

            String valorTipoConcurso = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
            tipoConcurso.setValor(valorTipoConcurso);
            tipoConcurso.setDescricao(parametroJogo.getConcurso().getTipoConcurso().toString());
            apostaDas8Modalidades.setTipoConcurso(tipoConcurso);

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
        apostaDas8Modalidades.setTimeDoCoracao(aposta.getTimeDoCoracao());
        apostaDas8Modalidades.setSurpresinha(true);
        apostaDas8Modalidades.setGerarEspelho(aposta.getGerarEspelho());

        BigDecimal valorIndividual = aposta.getValor().divide(new BigDecimal(aposta.getQuantidadeSurpresinhas()));
        for (int contador = 0; contador < aposta.getQuantidadeSurpresinhas(); contador++) {
            apostaDas8Modalidades.setValor(valorIndividual);
            if (aposta.getGerarEspelho() != null && aposta.getGerarEspelho()) {
                apostaDas8Modalidades.setValor(valorIndividual.divide(Constantes.DOIS_BIG_DECIMAL));
                apostaDas8Modalidades.setEspelho(aposta.getGerarEspelho());
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(context, apostaDas8Modalidades);
                apostaDas8Modalidades.setEspelho(Boolean.FALSE);
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(context, apostaDas8Modalidades);
            } else if (CollectionUtils.isNotEmpty(aposta.getListaTimeDoCoracao())) {
                apostaDas8Modalidades.setTimeDoCoracao(aposta.getListaTimeDoCoracao().get(contador));
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(context, apostaDas8Modalidades);
            } else {
                AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(context, apostaDas8Modalidades);
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
        DialogUtils.dismiss();
        if (MensagensNetwork.isUnauthorizedError(error)) {
            if (AdicionarApostaCarrinhoHelper.inserirApostaCarrinhoLocal(context, aposta)) {
                telaConfirmarCarrinho(aposta.getModalidade(), aposta);
            }
        } else {
            String msgError = MensagensNetwork.setMensagem(error, context);
            ViewUtils.alert(context, msgError);
        }
    }
}
