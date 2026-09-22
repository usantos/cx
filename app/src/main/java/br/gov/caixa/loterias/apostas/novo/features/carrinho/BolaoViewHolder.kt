package br.gov.caixa.loterias.apostas.novo.features.carrinho

import CarrinhoItem
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
import br.gov.caixa.loterias.apostas.utils.DateUtils
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP
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

    fun bind(item: CarrinhoItem.Bolao) {

        cancelarTimer()
        val aposta = item.aposta
        val estilo = EstiloModalidadeMKP(aposta.modalidade)

        tvNome.apply {
            text = ModalidadeEnum.fromString(aposta.modalidade)?.toString()
            setTextColor(
                ContextCompat.getColor(
                    context, estilo.corClara
                )
            )
        }
        tvDescricao.text =
            tvDescricao.context.getString(
                R.string.txt_cota,
                aposta.reservaCotaBolao.numeroCotaReservada,
                aposta.reservaCotaBolao.qtdCotaTotalBolao
            )
        tvConcurso.text = tvConcurso.context.getString(R.string.txt_label_conc, aposta.concursoAlvo)

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

    private fun iniciarTimerCota(
        timeMillis: Long,
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        countDownTimer?.cancel()
        if (timeMillis <= 0L) {
            tvTempo.text = tvTempo.context.getString(R.string.countdown_timer_zero)
            onCotaExpirada(aposta)
            return
        }
        countDownTimer = object : CountDownTimer(timeMillis, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                tvTempo.text = formatarTempo(millisUntilFinished)
            }

            override fun onFinish() {
                tvTempo.text = tvTempo.context.getString(R.string.countdown_timer_zero)
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