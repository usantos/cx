package br.gov.caixa.loterias.apostas.novo.features.carrinho

import CarrinhoItem
import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.utils.DateUtils
import java.text.NumberFormat
import java.util.Locale

class BolaoViewHolder(
    itemView: View,
    private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onCotaExpirada: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onMaisDetalhes: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val tvNome =
        itemView.findViewById<TextView>(R.id.tvTitulo)

    private val tvDescricao =
        itemView.findViewById<TextView>(R.id.tvDescricao)

    private val tvTempo =
        itemView.findViewById<TextView>(R.id.tvTempo)

    private val tvConcurso =
        itemView.findViewById<TextView>(R.id.tvConcurso)

    private val tvValor =
        itemView.findViewById<TextView>(R.id.tvValor)

    private val btnExcluir =
        itemView.findViewById<ImageView>(R.id.btnExcluir)

    private val btnMaisDetalhes =
        itemView.findViewById<TextView>(R.id.btnMaisDetalhes)

    private var countDownTimer: CountDownTimer? = null

    @SuppressLint("SetTextI18n")
    fun bind(item: CarrinhoItem.Bolao) {

        cancelarTimer()
        val aposta = item.aposta

        tvNome.text = aposta.getModalidade()?.name
        tvDescricao.text =
            "cota reservada nº ${aposta.reservaCotaBolao.numeroCotaReservada} + de ${aposta.reservaCotaBolao.qtdCotaTotalBolao}}"
        tvConcurso.text = "Conc. ${aposta.concursoAlvo}"

        tvValor.text = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            .format(aposta.reservaCotaBolao.vrCotaReservada)

        val timeMillis = aposta.reservaCotaBolao
            ?.dataHoraExpiracaoReserva
            ?.let(DateUtils::diffMillisSecondsTimerZone)
            ?: 0L

        iniciarTimerCota(timeMillis, aposta)

        btnExcluir.setOnClickListener {
            onExcluir(aposta)
        }

        btnMaisDetalhes.setOnClickListener {
            onMaisDetalhes(aposta)
        }
    }

    private fun iniciarTimerCota(timeMillis: Long, aposta: IdentificaoDeUmaApostaDas8Modalidades<*>) {
        countDownTimer?.cancel()
        if (timeMillis <= 0L) {
            tvTempo.text = "00:00"
            onCotaExpirada(aposta)
            return
        }
        countDownTimer = object : CountDownTimer(timeMillis, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                tvTempo.text = formatarTempo(millisUntilFinished)
            }

            override fun onFinish() {
                tvTempo.text = "00:00"
                onCotaExpirada(aposta)
            }
        }.start()
    }

    private fun formatarTempo(millis: Long): String {
        val totalSeconds = millis / 1_000L

        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    fun cancelarTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }
}