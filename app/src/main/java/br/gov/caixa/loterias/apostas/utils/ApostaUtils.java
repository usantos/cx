package br.gov.caixa.loterias.apostas.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumCharacter;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalheBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmBolao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;

public class ApostaUtils {

	private static IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchidaGenerica(ParametroJogoDTO parametroJogo,
																	 List<Integer> dezenasSelecionadas,
																	 BigDecimal valorTotalAposta,
																	 int qtdConcursoSelecionado) {

		IdentificaoDeUmaApostaDas8Modalidades aposta = new IdentificaoDeUmaApostaDas8Modalidades();

		aposta.setId(Constantes.ZERO_LONG);
		aposta.setModalidade(parametroJogo.getConcurso().getModalidade());

		DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

		String valorTipoConcurso = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
		tipoConcurso.setValor(valorTipoConcurso);
		tipoConcurso.setDescricao(parametroJogo.getConcurso().getTipoConcurso().toString());
		aposta.setTipoConcurso(tipoConcurso);
		aposta.setPartidasLotogol(null);
		aposta.setPartidasLoteca(null);

		DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
		indicadorSurpresinha.setValor(IndicadorSurpresinha.NAO_SURPRESINHA);
		indicadorSurpresinha.setDescricao("");

		aposta.setIndicadorSurpresinha(indicadorSurpresinha);
		aposta.setValor(valorTotalAposta);
		aposta.setConcursoAlvo(parametroJogo.getConcurso().getNumero());
		aposta.setQuantidadeTeimosinhas(qtdConcursoSelecionado);
		aposta.setQuantidadeApostas(1);
		aposta.setEspelho(false);
		aposta.setGerarEspelho(false);

		aposta.setConcursoInicial(0);
		aposta.setSituacao(null);
		aposta.setTroca(false);

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		Date       date       = new Date();

		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
		Date       hour       = new Date();

		aposta.setDataEfetivacao(dateFormat.format(date));
		aposta.setHoraEfetivacao(hourFormat.format(hour));
		aposta.setVinculoEspelho(Constantes.ZERO_LONG);
		aposta.setQuantidadeNumeros(dezenasSelecionadas.size());
		aposta.setSurpresinha(false);

		return aposta;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaPreenchida(ParametroJogoDTO parametroJogo,
																			List<Integer> dezenasSelecionadas,
																			BigDecimal valorTotalAposta,
																			int qtdConcursoSelecionado) {

		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchidaGenerica(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		if (dezenasSelecionadas != null && dezenasSelecionadas.size() > 0) {
			aposta.setNumerosSelecionados(dezenasSelecionadas);
		}

		return aposta;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaTimemania(ParametroJogoDTO parametroJogo,
																		List<Integer> dezenasSelecionadas,
																		BigDecimal valorTotalAposta,
																		int qtdConcursoSelecionado,
																		ParametroEquipe equipeSelecionada){
		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		aposta.setTimeDoCoracao(equipeSelecionada);

		return aposta;
	}

	public static String descricaoCorrigida(DTOEnumLong situacao) {
		if (situacao == null || situacao.getValor() == null) {
			return "";
		}
		SituacaoAposta.EnumSituacaoAposta e = SituacaoAposta.EnumSituacaoAposta.fromCode(situacao.getValor().intValue());
		return e != null ? e.getDescricao() : situacao.getDescricao();
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaDiaDeSorte(ParametroJogoDTO parametroJogo,
																			List<Integer> dezenasSelecionadas,
																			BigDecimal valorTotalAposta,
																			int qtdConcursoSelecionado,
																			ParametroMesDeSorte mesDeSorte){
		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		aposta.setMesDeSorte(mesDeSorte);

		return aposta;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaLotogol(ParametroJogoDTO parametroJogo,
																			List<Integer> dezenasSelecionadas,
																			BigDecimal valorTotalAposta,
																			int qtdConcursoSelecionado,
																		 HashSet<ParametroPartida> partidasSelecionadas){
		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchidaGenerica(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		setApostaLotogol(parametroJogo, aposta, partidasSelecionadas);

		return aposta;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaLoteca(ParametroJogoDTO parametroJogo,
																		 List<Integer> dezenasSelecionadas,
																		 BigDecimal valorTotalAposta,
																		 int qtdConcursoSelecionado,
																		 HashSet<ParametroPartida> partidasSelecionadas){
		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchidaGenerica(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		setApostaLoteca(parametroJogo, aposta, partidasSelecionadas);

		return aposta;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaSuperSete(ParametroJogoDTO parametroJogo,
																		List<Integer> dezenasSelecionadas,
																		BigDecimal valorTotalAposta,
																		int qtdConcursoSelecionado,
																		   ArrayList<ArrayList<Integer>> listaSuperSete){
		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchidaGenerica(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		if (dezenasSelecionadas != null && listaSuperSete.size() > 0) {
			aposta.setNumerosSelecionados(listaSuperSete);
		}

		return aposta;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades getApostaMaisMilionaria(ParametroJogoDTO parametroJogo,
																		   List<Integer> dezenasSelecionadas,
																		   BigDecimal valorTotalAposta,
																		   int qtdConcursoSelecionado,
																				List<Integer> trevosSelecionados){
		IdentificaoDeUmaApostaDas8Modalidades aposta = getApostaPreenchida(parametroJogo, dezenasSelecionadas, valorTotalAposta, qtdConcursoSelecionado);

		aposta.setQuantidadeTrevos(trevosSelecionados.size());
		aposta.setTrevosSelecionados(trevosSelecionados);

		return aposta;
	}

	private static DTOEnumInteger getIndicadorSurpresinha(){
		DTOEnumInteger indicadorSurpresinha = new DTOEnumInteger();
		indicadorSurpresinha.setValor(IndicadorSurpresinha.SURPRESINHA);
		indicadorSurpresinha.setDescricao("");

		return indicadorSurpresinha;
	}

	public static IncluirSurpresinhaDTO getSurpresinha(ParametroJogoDTO parametroJogo, int qtdDezenasPossiveisSelecionado,
															 int qtdSurpresinhas, int qtdConcursoSelecionado, BigDecimal valorTotalAposta) {
		IncluirSurpresinhaDTO aposta = new IncluirSurpresinhaDTO();
		aposta.setModalidade(parametroJogo.getConcurso().getModalidade());
		aposta.setQuantidadeNumeros(qtdDezenasPossiveisSelecionado);
		aposta.setQuantidadeSurpresinhas(qtdSurpresinhas);

		DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();

		String valorTipoConcurso = parametroJogo.getConcurso().getTipoConcurso() == TipoConcursoEnum.NORMAL ? "1" : "2";
		tipoConcurso.setValor(valorTipoConcurso);
		tipoConcurso.setDescricao(parametroJogo.getConcurso().getTipoConcurso().toString());
		aposta.setTipoConcurso(tipoConcurso);

		DTOEnumInteger indicadorSurpresinha = getIndicadorSurpresinha();

		aposta.setIndicadorSurpresinha(indicadorSurpresinha);
		aposta.setConcursoAlvo(parametroJogo.getConcurso().getNumero());
		aposta.setQuantidadeTeimosinhas(qtdConcursoSelecionado);
		aposta.setQuantidadeApostas(1);
		aposta.setValor(valorTotalAposta);
		aposta.setQtdTrevos(0);

		return aposta;
	}

	public static IncluirSurpresinhaDTO getSurpresinhaTimemania(ParametroJogoDTO parametroJogo, int qtdDezenasPossiveisSelecionado,
																int qtdSurpresinhas, int qtdConcursoSelecionado, BigDecimal valorTotalAposta,
																ParametroEquipe equipeSelecionada) {

		IncluirSurpresinhaDTO aposta = getSurpresinha(parametroJogo, qtdDezenasPossiveisSelecionado,
																	qtdSurpresinhas, qtdConcursoSelecionado,
																	valorTotalAposta);

		aposta.setTimeDoCoracao(equipeSelecionada);

		if(aposta.getTimeDoCoracao() == null){
			Random randomGenerator = Utils.getRandom();
			aposta.setTimeDoCoracao(parametroJogo.getEquipes().get(randomGenerator.nextInt(parametroJogo.getEquipes().size())));
		} else {
			aposta.getIndicadorSurpresinha().setValor(IndicadorSurpresinha.SURPRESINHA_NUMERICA);
		}

		return aposta;
	}

	public static IncluirSurpresinhaDTO getSurpresinhaLotomania(ParametroJogoDTO parametroJogo, int qtdDezenasPossiveisSelecionado,
																int qtdSurpresinhas, int qtdConcursoSelecionado, BigDecimal valorTotalAposta,
																boolean geraEspelho) {

		IncluirSurpresinhaDTO aposta = getSurpresinha(parametroJogo, qtdDezenasPossiveisSelecionado,
																	qtdSurpresinhas, qtdConcursoSelecionado,
																	valorTotalAposta);

		aposta.setGerarEspelho(geraEspelho);

		return aposta;
	}

	public static IncluirSurpresinhaDTO getSurpresinhaMaisMilionaria(ParametroJogoDTO parametroJogo, int qtdDezenasPossiveisSelecionado,
																	 int qtdSurpresinhas, int qtdConcursoSelecionado, BigDecimal valorTotalAposta, int qtdTrevos) {

		IncluirSurpresinhaDTO aposta = getSurpresinha(parametroJogo, qtdDezenasPossiveisSelecionado,
																	qtdSurpresinhas, qtdConcursoSelecionado,
																	valorTotalAposta);

		aposta.setQtdTrevos(qtdTrevos);

		return aposta;
	}
	private static void setApostaLotogol(ParametroJogoDTO parametro, IdentificaoDeUmaApostaDas8Modalidades aposta, HashSet<ParametroPartida> partidas) {
		if (parametro.getConcurso().getModalidade() == ModalidadeEnum.LOTOGOL) {
			List<ParametroPartida> partidasSelecionadas = ListaUtils.orderAscPartidas(partidas);
			aposta.setPartidasLotogol(new ArrayList<>());
			for (ParametroPartida partida : partidasSelecionadas) {
				PartidaLotogolDTO lotogolDTO = new PartidaLotogolDTO();
				EquipeDTO         equipeDTO1 = new EquipeDTO();
				EquipeDTO         equipeDTO2 = new EquipeDTO();

				setValorEquipe1DTOEquipe2DTO(partida, equipeDTO1, equipeDTO2);

				lotogolDTO.setEquipe1(equipeDTO1);
				lotogolDTO.setEquipe2(equipeDTO2);

				aposta.getPartidasLotogol().add(lotogolDTO);
			}
		}
	}

	private static void setApostaLoteca(ParametroJogoDTO parametro, IdentificaoDeUmaApostaDas8Modalidades aposta, HashSet<ParametroPartida> partidas) {
		if (parametro.getConcurso().getModalidade() == ModalidadeEnum.LOTECA) {
			List<ParametroPartida> partidasSelecionadas = ListaUtils.orderAscPartidas(partidas);
			aposta.setPartidasLoteca(new ArrayList<>());
			for (ParametroPartida partida : partidasSelecionadas) {
				PartidaLotecaDTO lotecaDTO  = new PartidaLotecaDTO();
				EquipeDTO        equipeDTO1 = new EquipeDTO();
				EquipeDTO        equipeDTO2 = new EquipeDTO();

				setValorEquipe1DTOEquipe2DTO(partida, equipeDTO1, equipeDTO2);

				lotecaDTO.setEquipe1(equipeDTO1);
				lotecaDTO.setEquipe2(equipeDTO2);
				lotecaDTO.setEmpate(partida.isEmpate());
				lotecaDTO.setNumero(partida.getNumero());

				aposta.getPartidasLoteca().add(lotecaDTO);
			}
		}
	}

	private static void setValorEquipe1DTOEquipe2DTO(ParametroPartida partida, EquipeDTO equipeDTO1, EquipeDTO equipeDTO2) {
		equipeDTO1.setPlacar(partida.getEquipe1().getPlacar());
		equipeDTO1.setVitoria(partida.getEquipe1().isSelecionado());
		equipeDTO1.setParametroEquipe(partida.getEquipe1());
		equipeDTO1.setIndicadorSelecao(partida.getEquipe1().getIndicadorSelecao());
		equipeDTO1.setDescricaoCurta(partida.getEquipe1().getDescricaoCurta());
		equipeDTO1.setDescricaoLonga(partida.getEquipe1().getDescricaoLonga());
		equipeDTO1.setNumero(partida.getEquipe1().getNumero());
		equipeDTO1.setNumeroPais(partida.getEquipe1().getNumeroPais());
		equipeDTO1.setPais(partida.getEquipe1().getPais());
		equipeDTO1.setSiglaPais(partida.getEquipe1().getSiglaPais());
		equipeDTO1.setUf(partida.getEquipe1().getUf());
		equipeDTO1.setNomeClass(partida.getEquipe1().getNomeClass());
		equipeDTO1.setNome(partida.getEquipe1().getNome());

		equipeDTO2.setPlacar(partida.getEquipe2().getPlacar());
		equipeDTO2.setVitoria(partida.getEquipe2().isSelecionado());
		equipeDTO2.setParametroEquipe(partida.getEquipe2());
		equipeDTO2.setIndicadorSelecao(partida.getEquipe2().getIndicadorSelecao());
		equipeDTO2.setDescricaoCurta(partida.getEquipe2().getDescricaoCurta());
		equipeDTO2.setDescricaoLonga(partida.getEquipe2().getDescricaoLonga());
		equipeDTO2.setNumero(partida.getEquipe2().getNumero());
		equipeDTO2.setNumeroPais(partida.getEquipe2().getNumeroPais());
		equipeDTO2.setPais(partida.getEquipe2().getPais());
		equipeDTO2.setSiglaPais(partida.getEquipe2().getSiglaPais());
		equipeDTO2.setUf(partida.getEquipe2().getUf());
		equipeDTO2.setNomeClass(partida.getEquipe2().getNomeClass());
		equipeDTO2.setNome(partida.getEquipe2().getNome());
	}
	public static IdentificaoDeUmaApostaDas8Modalidades convertApostaDTOemIdentificaoDeuUmaPostaDas8Modadlidades(ApostaDTO apostaDTO) {
		ParametroJogoDTO parametroJogo = ViewUtils.getParametroSimulacao(SessaoUsuario.getInstance(), apostaDTO.getModalidade()).getParametroJogo();

		switch (apostaDTO.getModalidade()){
			case TIMEMANIA:
				return getApostaTimemania(parametroJogo,
											 apostaDTO.getListaNumerosSelecionados(),
											 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas(),
											apostaDTO.getTimeDoCoracao());
			case DIA_DE_SORTE:
				return getApostaDiaDeSorte(parametroJogo,
											 apostaDTO.getListaNumerosSelecionados(),
											 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas(),
											 apostaDTO.getMesDeSorte());
			case LOTOGOL:
				return getApostaLotogol(parametroJogo,
											 apostaDTO.getListaNumerosSelecionados(),
											 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas(),
										  (HashSet<ParametroPartida>) apostaDTO.getPartidasLotogol());
			case LOTECA:
				return getApostaLoteca(parametroJogo,
											 apostaDTO.getListaNumerosSelecionados(),
											 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas(),
									         new HashSet<>(apostaDTO.getPartidasLoteca()));
			case SUPER_7:
				return getApostaSuperSete(parametroJogo, new ArrayList<>(),
											 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas(),
											 apostaDTO.getMatrizNumerosSelecionados());
			case MAIS_MILIONARIA:
				return getApostaMaisMilionaria(parametroJogo,
											 apostaDTO.getListaNumerosSelecionados(),
											 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas(),
											 apostaDTO.getListaTrevosSelecionados());
			default:
				return getApostaPreenchida(parametroJogo,
												 apostaDTO.getListaNumerosSelecionados(),
												 apostaDTO.getValor(), apostaDTO.getQuantidadeTeimosinhas());

		}
	}
	public static boolean temModalidadeNormaleEspecialAberta(ArrayList<ApostaDTO> listaApostas){
		int                      countModalidade;
		List<ParametroSimulacao> parametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
		if (listaApostas != null) {
			for (ApostaDTO aposta : listaApostas) {
				countModalidade = 0;
				for (int param = 0; param < parametros.size() - 1; param++) {
					ModalidadeEnum modalidade = parametros.get(param).getParametroJogo().getConcurso().getModalidade();
					if (aposta.getModalidade().equals(modalidade)) {
						countModalidade++;
					}
				}
				if (countModalidade >= 2) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getNomeModalidadeNormal(ArrayList<ApostaDTO> listaApostas){
		int                      countModalidade;
		List<ParametroSimulacao> parametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
		if (listaApostas != null) {
			for (ApostaDTO aposta : listaApostas) {
				countModalidade = 0;
				for (int param = 0; param < parametros.size() - 1; param++) {
					ModalidadeEnum modalidade = parametros.get(param).getParametroJogo().getConcurso().getModalidade();
					if (aposta.getModalidade().equals(modalidade)) {
						countModalidade++;
					}
				}
				if (countModalidade >= 2) {
					return ModalidadeEnum.getDescricao(aposta.getModalidade());
				}
			}
		}
		return "";
	}

	public static String getNomeModalidadeEspecial(ArrayList<ApostaDTO> listaApostas){
		int                      countModalidade;
		List<ParametroSimulacao> parametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
		if (listaApostas != null) {
			for (ApostaDTO aposta : listaApostas) {
				countModalidade = 0;
				for (int param = 0; param < parametros.size() - 1; param++) {
					ModalidadeEnum modalidade = parametros.get(param).getParametroJogo().getConcurso().getModalidade();
					if (aposta.getModalidade().equals(modalidade)) {
						countModalidade++;
					}
				}
				if (countModalidade >= 2) {
					return ModalidadeEnum.getDescricaoEspecial(aposta.getModalidade());
				}
			}
		}
		return "";
	}

	public static IdentificaoDeUmaApostaDas8Modalidades converteBolaoEmAposta(CotasBolaoDTO bolao) {
		IdentificaoDeUmBolao aposta = new IdentificaoDeUmBolao();
		aposta.setModalidade(bolao.getModalidade());
		aposta.setConcursoAlvo(bolao.getConcurso());
		aposta.setSubcanalCarrinhoAposta(1);
		aposta.setIndicadorCotaBolao(true);
		aposta.setIdBolao(bolao.getCodigoBolao());
		aposta.setQtdCotasBolao(bolao.getQtdCotasBolao());
		aposta.setValorTarifaServico(bolao.getValorTarifaServico());
		DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();
		tipoConcurso.setValor(bolao.getTipoConcurso().toString());
		tipoConcurso.setDescricao("");
		aposta.setTipoConcurso(tipoConcurso);



		aposta.setValor(atualizaValorAposta(bolao));
		aposta.setDataSorteio(bolao.getDataSorteio());

		return aposta;
	}

	private static BigDecimal atualizaValorAposta(CotasBolaoDTO bolao) {
		Double qtdApostas = Double.valueOf(bolao.getQtdCotasBolao() - 1);
		BigDecimal valor = bolao.getVrUltimaCotaComTarifa().add(BigDecimal.valueOf(bolao.getVrCotaComTarifa().doubleValue() * qtdApostas));
		return valor;
	}
	public static IdentificaoDeUmaApostaDas8Modalidades converteDetalheBolaoEmAposta(String codigoBolao, DetalheBolaoDTO bolao) {
		IdentificaoDeUmBolao aposta = new IdentificaoDeUmBolao();
		aposta.setModalidade(bolao.getModalidade());
		aposta.setConcursoAlvo(bolao.getConcurso());
		aposta.setSubcanalCarrinhoAposta(1);
		aposta.setIndicadorCotaBolao(true);
		aposta.setIdBolao(codigoBolao);
		aposta.setQtdCotasBolao(bolao.getQtdCotaDesejada());
		aposta.setValorTarifaServico(bolao.getValorTarifaServico());
		DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();
		tipoConcurso.setValor(bolao.getTipoConcurso().toString());
		aposta.setTipoConcurso(tipoConcurso);

		BigDecimal qntCotas = new BigDecimal(bolao.getQtdCotaDesejada());

		aposta.setValor(bolao.getVrUltimaCotaComTarifa().multiply(qntCotas));
		aposta.setDataSorteio(bolao.getDataSorteio());

		return aposta;
	}

	public static ApostaDTO converteTipoDeConcurso(ApostaDTO apostaDTO) {
		DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();
		if (apostaDTO.getTipoConcurso().getValor().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.getValor())){
			tipoConcurso.setValor(TipoConcursoEnum.NORMAL.getValor());
		} else {
			tipoConcurso.setValor(TipoConcursoEnum.ESPECIAL.getValor());
			apostaDTO.setQuantidadeTeimosinhas(0);
		}

		tipoConcurso.setDescricao("");
		apostaDTO.setTipoConcurso(tipoConcurso);

		return apostaDTO;
	}

	public static IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> converteTipoDeConcurso(IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> apostaDTO) {
		IdentificaoDeUmaApostaDas8Modalidades clone = IdentificaoDeUmaApostaDas8Modalidades.clone(apostaDTO);
		DTOEnumCharacter tipoConcurso = new DTOEnumCharacter();
		if (clone.getTipoConcurso().getValor().equalsIgnoreCase(TipoConcursoEnum.ESPECIAL.getValor())){
			tipoConcurso.setValor(TipoConcursoEnum.NORMAL.getValor());
		} else {
			tipoConcurso.setValor(TipoConcursoEnum.ESPECIAL.getValor());
			clone.setQuantidadeTeimosinhas(0);
		}

		tipoConcurso.setDescricao("");
		clone.setTipoConcurso(tipoConcurso);

		return clone;
	}

}
