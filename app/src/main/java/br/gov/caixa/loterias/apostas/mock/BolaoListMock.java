package br.gov.caixa.loterias.apostas.mock;

import android.os.Build;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.queryparam.FiltroMarketPlace;

public class BolaoListMock {

	List<CotasBolaoDTO> cotas;
	private static BolaoListMock instance;

	private BolaoListMock(){
		this.cotas = initListCotas();
	}

	public static BolaoListMock getInstance(){
		if (instance == null){
			instance = new BolaoListMock();
		}
		return instance;
	}

	private List<CotasBolaoDTO> initListCotas() {
		List<CotasBolaoDTO> list = new ArrayList<>();

		list.addAll(getCotasMunicipioUF());
		list.addAll(getCotasNacional());

		return list;
	}

	private Collection<? extends CotasBolaoDTO> getCotasNacional() {
		List<CotasBolaoDTO> ufs = new ArrayList<>();

		// 16 registrtos por 3 ufs

		for (int i = 1; i <= 16; i++) {
			if (i <= 8) {
				// 8 uf SP
				ufs.add(createCotaBolao(60, 100L));
			} else if (i > 8 && i <= 12) {
				// 4 Parana
				ufs.add(createCotaBolao(70, 120L));
			} else {
				// 4 RJ
				ufs.add(createCotaBolao(80, 130L));
			}
		}

		return ufs;
	}

	private Collection<? extends CotasBolaoDTO> getCotasMunicipioUF() {
		List<CotasBolaoDTO> municipios = new ArrayList<>();

		// 24 municipios DF
		Integer idMun = 10;
		Long idUf = 53L;

		for (int i = 1; i <= 24; i++) {
			if (i <= 14){
				// 14 municipios do usuario
				municipios.add(createCotaBolao(idMun, idUf));
			}
			else
				if (i > 14 && i <= 17){
				// 3 municipios
				municipios.add(createCotaBolao(20, idUf));
			} else if (i > 17 && i <= 19) {
				// 2 municipios
				municipios.add(createCotaBolao(30, idUf));
			} else if (i > 19 && i <= 21) {
				// 2 municipios
				municipios.add(createCotaBolao(40, idUf));
			}
		}

		return municipios;
	}

	@NonNull
	private static CotasBolaoDTO createCotaBolao(Integer idMun, Long idUf) {
		CotasBolaoDTO cota = new CotasBolaoDTO();
		cota.setIdMunicipio(new Long(idMun));
		cota.setNumeroUF(idUf);
		cota.setModalidade(ModalidadeEnum.MEGA_SENA);
		cota.setVrCotaComTarifa(new BigDecimal(10));
		cota.setQtdCotasBolao(1);
		cota.setQtdCotaDisponivel(5);
		cota.setQtdCotaDisponivel(idMun);

		if (idMun.equals(10)){
			cota.setNomeFantasia("Gaminha");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Brasilia");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("DF");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(20)) {
			cota.setNomeFantasia("Santinha");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Brasilia");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("DF");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(30)) {
			cota.setNomeFantasia("Guara");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Brasilia");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("DF");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(40)) {
			cota.setNomeFantasia("BSB");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Brasilia");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("DF");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(50)) {
			cota.setNomeFantasia("Ceilondres");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Brasilia");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("DF");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(60)) {
			cota.setNomeFantasia("Sampa");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Sao Paulo");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("SP");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(70)) {
			cota.setNomeFantasia("Paranaue");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Curitiba");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("PR");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		} else if (idMun.equals(80)) {
			cota.setNomeFantasia("ErreJota");
			MunicipioDTO municipioDTO = new MunicipioDTO();
			municipioDTO.setNome("Rio de Janeiro");

			UnidadeFederacaoDTO uf = new UnidadeFederacaoDTO();
			uf.setSigla("RJ");

			cota.setMunicipio(municipioDTO);
			cota.setUf(uf);
		}

		return  cota;
	}

	public ListaBolaoDTO filtro(FiltroMarketPlace filtro) {
		ListaBolaoDTO payload = new ListaBolaoDTO();
		List<CotasBolaoDTO> listTotal = new ArrayList<>();

		for(CotasBolaoDTO cota: cotas) {
			switch (filtro.getTipoConsulta()) {
				case MUNICIPIO:
					if (cota.getIdMunicipio().intValue() == filtro.getIdMunicipio()) {
						listTotal.add(cota);
					}
					break;
				case UF:
					if (filtro.getIdMunicipio() != cota.getIdMunicipio().intValue() &&
							filtro.getIdUf() == cota.getNumeroUF().intValue()) {
						listTotal.add(cota);
					}
					break;
				case NACIONAL:
					if (filtro.getIdUf() != cota.getNumeroUF().intValue()) {
						listTotal.add(cota);
					}
					break;
				default:
			}
		}

		if (filtro.getQtdMaximaCota() > 0 ){
			int min = filtro.getQtdMinimaCota();
			int max = filtro.getQtdMaximaCota();
			List<CotasBolaoDTO> filtradas = new ArrayList<>();
			for (CotasBolaoDTO cota: listTotal){
				if (cota.getQtdCotaDisponivel() >= min && cota.getQtdCotaDisponivel() <= max){
					filtradas.add(cota);
				}
			}
			listTotal = filtradas;
		}

		int ultimaPagina = listTotal.size() / filtro.getQtdPorPagina();
		if ((ultimaPagina * filtro.getQtdPorPagina()) < listTotal.size()){
			ultimaPagina++;
		}

		List<CotasBolaoDTO> listaFinal = new ArrayList<>();
		if (listTotal.size() > 0){
			int max = ultimaPagina == filtro.getPagina() ? listTotal.size() : (filtro.getQtdPorPagina() * filtro.getPagina());
			int index = (filtro.getPagina() * filtro.getQtdPorPagina()) - filtro.getQtdPorPagina();
			for (int i = index; i < max; i++) {
				listaFinal.add(listTotal.get(i));
			}
		}

		payload.setCotas(listaFinal);
		payload.setTotalRegistros(listTotal.size());
		payload.setPaginaAtual(filtro.getPagina());
		payload.setUltimaPagina(ultimaPagina);

		return payload;
	}
}
