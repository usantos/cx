import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.novo.features.carrinho.ApostaViewHolder
import br.gov.caixa.loterias.apostas.novo.features.carrinho.BolaoViewHolder
import br.gov.caixa.loterias.apostas.novo.features.carrinho.HeaderViewHolder

class CarrinhoAdapter(
    private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit,
    private val onMaisDetalhes: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit
) : ListAdapter<CarrinhoItem, RecyclerView.ViewHolder>(
    CarrinhoDiffCallback()
) {
    private val items = mutableListOf<CarrinhoItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CarrinhoItem.Header -> TYPE_HEADER
            is CarrinhoItem.Bolao -> TYPE_BOLAO
            is CarrinhoItem.Aposta -> TYPE_APOSTA
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {

            TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.item_header_carrinho,
                    parent,
                    false
                )
            )

            TYPE_BOLAO -> BolaoViewHolder(
                inflater.inflate(
                    R.layout.item_bolao_carrinho,
                    parent,
                    false
                ),
                onExcluir,
                onMaisDetalhes
            )

            TYPE_APOSTA -> ApostaViewHolder(
                inflater.inflate(
                    R.layout.item_aposta_carrinho,
                    parent,
                    false
                ),
                onExcluir
            )

            else -> error("Tipo inválido")
        }
    }


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (val item = items[position]) {

            is CarrinhoItem.Header ->
                (holder as HeaderViewHolder).bind(item)

            is CarrinhoItem.Bolao ->
                (holder as BolaoViewHolder).bind(item)

            is CarrinhoItem.Aposta ->
                (holder as ApostaViewHolder).bind(item)
        }
    }

    private companion object {
        const val TYPE_HEADER = 0
        const val TYPE_BOLAO = 1
        const val TYPE_APOSTA = 2
    }

    class CarrinhoDiffCallback : DiffUtil.ItemCallback<CarrinhoItem>() {

        override fun areItemsTheSame(
            oldItem: CarrinhoItem,
            newItem: CarrinhoItem
        ): Boolean {

            return when {
                oldItem is CarrinhoItem.Header &&
                        newItem is CarrinhoItem.Header ->
                    oldItem.titulo == newItem.titulo

                oldItem is CarrinhoItem.Bolao &&
                        newItem is CarrinhoItem.Bolao ->
                    oldItem.aposta == newItem.aposta

                oldItem is CarrinhoItem.Aposta &&
                        newItem is CarrinhoItem.Aposta ->
                    oldItem.aposta == newItem.aposta

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: CarrinhoItem,
            newItem: CarrinhoItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}