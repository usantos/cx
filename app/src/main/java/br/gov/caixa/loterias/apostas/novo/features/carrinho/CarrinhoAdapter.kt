package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades

class CarrinhoAdapter(
    private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onExcluirBolao: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onExcluirCombo: (ComboApostaDTO) -> Unit,
    private val onCotaExpirada: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onMaisDetalhes: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onMaisDetalhesCombo: (ComboApostaDTO) -> Unit
) : ListAdapter<CarrinhoItem, RecyclerView.ViewHolder>(
    CarrinhoDiffCallback()
) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CarrinhoItem.HeaderFixo -> TYPE_HEADER_FIXED
            is CarrinhoItem.Header -> TYPE_HEADER
            is CarrinhoItem.Bolao -> TYPE_BOLAO
            is CarrinhoItem.Aposta -> TYPE_APOSTA
            is CarrinhoItem.Combo -> TYPE_COMBO
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HEADER_FIXED -> HeaderFixoViewHolder(
                inflater.inflate(
                    R.layout.item_header_carrinho_fixo, parent, false
                )
            )

            TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.item_header_carrinho, parent, false
                )
            )

            TYPE_BOLAO -> BolaoViewHolder(
                inflater.inflate(
                    R.layout.item_bolao_carrinho, parent, false
                ), onExcluirBolao, onCotaExpirada, onMaisDetalhes
            )

            TYPE_APOSTA -> ApostaViewHolder(
                inflater.inflate(
                    R.layout.item_aposta_carrinho, parent, false
                ), onExcluir
            )

            TYPE_COMBO -> ComboViewHolder(
                inflater.inflate(
                    R.layout.item_combo_carrinho, parent, false
                ), onExcluirCombo, onMaisDetalhesCombo
            )

            else -> error("Tipo inválido")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is BolaoViewHolder) {
            holder.cancelarTimer()
        }

        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (val item = getItem(position)) {
            is CarrinhoItem.HeaderFixo -> (holder as HeaderFixoViewHolder)

            is CarrinhoItem.Header -> (holder as HeaderViewHolder).bind(item)

            is CarrinhoItem.Bolao -> (holder as BolaoViewHolder).bind(item)

            is CarrinhoItem.Aposta -> (holder as ApostaViewHolder).bind(item)

            is CarrinhoItem.Combo -> (holder as ComboViewHolder).bind(item)
        }
    }

    private companion object {
        const val TYPE_HEADER_FIXED = 0
        const val TYPE_HEADER = 1
        const val TYPE_BOLAO = 2
        const val TYPE_APOSTA = 3
        const val TYPE_COMBO = 4
    }

    class CarrinhoDiffCallback : DiffUtil.ItemCallback<CarrinhoItem>() {

        override fun areItemsTheSame(
            oldItem: CarrinhoItem, newItem: CarrinhoItem
        ): Boolean {
            return when {
                oldItem is CarrinhoItem.HeaderFixo && newItem is CarrinhoItem.HeaderFixo -> oldItem == newItem
                oldItem is CarrinhoItem.Header && newItem is CarrinhoItem.Header -> oldItem.titulo == newItem.titulo
                oldItem is CarrinhoItem.Bolao && newItem is CarrinhoItem.Bolao -> mesmaAposta(
                    oldItem.aposta, newItem.aposta
                )

                oldItem is CarrinhoItem.Aposta && newItem is CarrinhoItem.Aposta -> mesmaAposta(
                    oldItem.aposta, newItem.aposta
                )

                oldItem is CarrinhoItem.Combo && newItem is CarrinhoItem.Combo -> mesmoCombo(
                    oldItem.combo, newItem.combo
                )

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: CarrinhoItem, newItem: CarrinhoItem
        ): Boolean {
            return oldItem == newItem
        }

        private fun mesmaAposta(
            oldItem: IdentificaoDeUmaApostaDas8Modalidades<*>,
            newItem: IdentificaoDeUmaApostaDas8Modalidades<*>
        ): Boolean = when {
            oldItem.idDB != null && newItem.idDB != null -> oldItem.idDB == newItem.idDB
            oldItem.id != null && newItem.id != null -> oldItem.id == newItem.id
            else -> oldItem === newItem
        }

        private fun mesmoCombo(
            oldItem: ComboApostaDTO, newItem: ComboApostaDTO
        ): Boolean = if (oldItem.id != null && newItem.id != null) {
            oldItem.id == newItem.id
        } else {
            oldItem === newItem
        }
    }
}