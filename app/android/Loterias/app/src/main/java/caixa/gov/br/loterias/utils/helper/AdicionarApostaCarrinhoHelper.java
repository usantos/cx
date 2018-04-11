package caixa.gov.br.loterias.utils.helper;

import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroJogoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.TipoConcursoEnum;
import caixa.gov.br.loterias.model.dao.DBLoteriasCrud;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.DialogUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.ConfirmadoInclusaoCarrinhoActivity;

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
            apostaBO.adicionarApostaNoCarrinho(aposta, new RequestListener<AgrupadorDTOApostaDTOResponse>() {
                @Override
                public void onResponse(AgrupadorDTOApostaDTOResponse result) {
                    DialogUtils.dismiss();

                    Intent activity = new Intent(context, ConfirmadoInclusaoCarrinhoActivity.class);
                    activity.putExtra("tipoAposta", aposta.getModalidade());
                    activity.putExtra("valorAposta", aposta.getValor());
                    activity.putExtra("modalidade", (new Gson()).toJson(ViewUtils.getParametroJogo(SessaoUsuario.getInstance(), aposta.getModalidade()).getParametroJogo()));
                    context.startActivity(activity);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(error);
                }
            });
        }
    }

    public static boolean inserirApostaCarrinhoLocal(Context contextParam, IdentificaoDeUmaApostaDas8Modalidades aposta) {
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
        }
        //TODO subistituir por quantidade máxima
        else if (quantidadeApostaCarrinho > (4 - 1)) {
            ViewUtils.alert(contextParam, contextParam.getString(R.string.MA013));
            return Boolean.FALSE;
        }
        //TODO subistituir por valor máxima
        else if (valorTotalCarrinho.compareTo(BigDecimal.TEN) == 1) {
            ViewUtils.alert(contextParam, contextParam.getString(R.string.MA014));
            return Boolean.FALSE;
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
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroJogo(sessaoUsuario, modalidadeEnum);
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
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroJogo(sessaoUsuario, modalidadeEnum);

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

    private static void setApostaLoteca(IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades, Object object) {

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

    private static void setApostaLotogol(IdentificaoDeUmaApostaDas8Modalidades apostaDas8Modalidades, Object object) {

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
        String msgError = MensagensNetwork.setMensagem(error, context);
        ViewUtils.alert(context, msgError);
    }
}
