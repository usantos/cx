package br.gov.caixa.loterias.apostas.utils;


import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SuperSeteEnum;

public class AutomacaoUtils {

	public static void aplicaIdViewSuperSete(SuperSeteEnum coluna, int i, TextView textView) {
		switch (coluna){
			case COLUNA_1:
				setIdColuna1(i, textView);
				break;
			case COLUNA_2:
				setIdColuna2(i, textView);
				break;
			case COLUNA_3:
				setIdColuna3(i, textView);
				break;
			case COLUNA_4:
				setIdColuna4(i, textView);
				break;
			case COLUNA_5:
				setIdColuna5(i, textView);
				break;
			case COLUNA_6:
				setIdColuna6(i, textView);
				break;
			case COLUNA_7:
				setIdColuna7(i, textView);
				break;
		}
	}

	private static void setIdColuna1(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol1Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol1Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol1Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol1Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol1Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol1Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol1Lin7);
		}
	}

	private static void setIdColuna2(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol2Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol2Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol2Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol2Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol2Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol2Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol2Lin7);
		}
	}

	private static void setIdColuna3(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol3Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol3Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol3Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol3Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol3Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol3Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol3Lin7);
		}
	}

	private static void setIdColuna4(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol4Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol4Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol4Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol4Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol4Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol4Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol4Lin7);
		}
	}

	private static void setIdColuna5(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol5Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol5Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol5Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol5Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol5Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol5Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol5Lin7);
		}
	}

	private static void setIdColuna6(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol6Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol6Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol6Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol6Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol6Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol6Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol6Lin7);
		}
	}

	private static void setIdColuna7(int i, TextView textView) {
		if (i == 0){
			textView.setId(R.id.idSuperSeteCol7Lin1);
		} else if(i == 1){
			textView.setId(R.id.idSuperSeteCol7Lin2);
		} else if (i == 2){
			textView.setId(R.id.idSuperSeteCol7Lin3);
		} else if (i == 3){
			textView.setId(R.id.idSuperSeteCol7Lin4);
		} else if (i == 4){
			textView.setId(R.id.idSuperSeteCol7Lin5);
		} else if (i == 5){
			textView.setId(R.id.idSuperSeteCol7Lin6);
		} else if (i == 6){
			textView.setId(R.id.idSuperSeteCol7Lin7);
		}
	}

	public static SuperSeteEnum getSuperSeteEnumPorColuna(int index) {
		switch (index){
			case 0:
				return SuperSeteEnum.COLUNA_1;
			case 1:
				return SuperSeteEnum.COLUNA_2;
			case 2:
				return SuperSeteEnum.COLUNA_3;
			case 3:
				return SuperSeteEnum.COLUNA_4;
			case 4:
				return SuperSeteEnum.COLUNA_5;
			case 5:
				return SuperSeteEnum.COLUNA_6;
			default:
				return SuperSeteEnum.COLUNA_7;
		}
	}
}
