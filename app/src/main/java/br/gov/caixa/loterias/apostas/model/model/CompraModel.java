package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UltimaCompraDTO;
import br.gov.caixa.loterias.apostas.model.dao.crud.UltimaCompraCRUD;
import br.gov.caixa.loterias.apostas.utils.DateUtils;

public class CompraModel {

	private Activity activity;

	public CompraModel(Activity activity) {
		this.activity = activity;
	}

	public void salvaUltimaCompra(BigDecimal valor, Date date){
		UltimaCompraCRUD crud = new UltimaCompraCRUD(activity);
		String stringDate     = DateUtils.getDateToString(date, DateUtils.PATTERN_DD_MM_YYYY_HH_MM);
		if (crud.lerUltimaCompra() != null){
			crud.deletaUltimaCompra();
		}
		crud.inserirUltimaCompra(new UltimaCompraDTO(valor, stringDate));
	}
}
