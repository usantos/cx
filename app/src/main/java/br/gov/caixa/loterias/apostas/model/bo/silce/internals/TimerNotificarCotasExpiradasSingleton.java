package br.gov.caixa.loterias.apostas.model.bo.silce.internals;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.MessagingUtils;


public class TimerNotificarCotasExpiradasSingleton {

    private static TimerNotificarCotasExpiradasSingleton instance;
    private Timer timer;
    private final Handler handler = new Handler();
    private Context context;


    private TimerNotificarCotasExpiradasSingleton(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized TimerNotificarCotasExpiradasSingleton getInstance(Context context){
        if (instance == null){
            instance = new TimerNotificarCotasExpiradasSingleton(context);
        }
        return instance;
    }


    public void iniciarVerificacao() {
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Tarefa
                            VerificaCotasExpiradas();
                        }
                    });
                }
            };

            timer.schedule(task, 0, 60000);; //1 minuto
        }

    }

    public void pararVerificacao() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void VerificaCotasExpiradas() {

        CarrinhoDTO carrinhoDTO = CarrinhoSingleton.getInstance().getCarrinho();
        if (carrinhoDTO == null || carrinhoDTO.getBoloes() == null ||
                carrinhoDTO.getBoloes().size() == 0) {
            return;
        }

        List<IdentificaoDeUmaApostaDas8Modalidades> listBolao = carrinhoDTO.getBoloes();

        for (IdentificaoDeUmaApostaDas8Modalidades bolao : listBolao) {
            String dataHoraExpiracao = bolao.getReservaCotaBolao().getDataHoraExpiracaoReserva();
            if (dataHoraExpiracao != null || dataHoraExpiracao.length() > 0) {
                long tempo_restante_milesec = DateUtils.diffMillisSecondsTimerZone(dataHoraExpiracao);
                if (tempo_restante_milesec > 0 && tempo_restante_milesec < 120000) {
                    notificationCotasPrestesExpirar();
                    break;
                }
            }
        }
    }

    private void notificationCotasPrestesExpirar() {
        //Toast.makeText(context, context.getString(R.string.cotas_prestes_expirar),
        //        Toast.LENGTH_SHORT).show();
        MessagingUtils.criaNotificacaoSobreposta(context, "", context.getString(R.string.cotas_prestes_expirar));
    }
}
