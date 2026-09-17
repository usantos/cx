package br.gov.caixa.loterias.apostas.utils.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.model.enums.RewardMatchMode;
import br.gov.caixa.loterias.apostas.model.ui.LotteryNumberUiModel;

public class LotteryMapper {
    public static List<LotteryNumberUiModel> buildUiList(
            List<String> numbers,
            List<String> rewarded,
            RewardMatchMode matchMode) {

        List<LotteryNumberUiModel> result = new ArrayList<>();

        for (int i = 0; i < numbers.size(); i++) {
            String number = numbers.get(i);
            boolean isRewarded;

            switch (matchMode) {
                case ANY_POSITION:
                        isRewarded = rewarded != null && number != null && rewarded.contains(number);
                        break;
                case SAME_POSITION:
                        isRewarded = rewarded != null && rewarded.size() > i && number != null && number.equals(rewarded.get(i));
                        break;
                case SAME_COLUMN_REPEATING: isRewarded = rewarded != null
                        && !rewarded.isEmpty()
                        && Objects.equals(
                        number,
                        rewarded.get(i % rewarded.size())
                );
                    break;
                default:
                    isRewarded = false;
                    break;
            }

            String accessibility = isRewarded ? number + ", premiado" : number;

            result.add(new LotteryNumberUiModel(
                    number, isRewarded, accessibility
            ));
        }

        return result;
    }
}