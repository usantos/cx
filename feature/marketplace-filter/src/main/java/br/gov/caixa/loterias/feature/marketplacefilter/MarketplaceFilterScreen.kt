package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyDialog
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.domain.retail.Retailer

/** Conversão Compose do `activity_filtra_marketplace.xml`, preservando a geometria legada. */
@Composable
fun MarketplaceFilterScreen(
    state: MarketplaceFilterUiState,
    onAction: (MarketplaceFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var numberDialog by remember { mutableStateOf<NumberDialog?>(null) }
    val showWantedFilters = state.selectedModality?.hidesWantedNumbers != true

    CaixaDSLegacyScaffold(
        title = "Filtros",
        onBack = { onAction(MarketplaceFilterAction.Back) },
        modifier = modifier.fillMaxSize().testTag("marketplace_filter_screen"),
        gradientTopBar = false,
        bottomBar = { LegacyCartSummary() },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            LegacyLabel(
                text = "Filtrar",
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                color = CaixaDS.colors.legacyPrimaryButton,
                fontSize = 14,
            )
            LegacyLabel("Modalidades", Modifier.padding(start = 10.dp, top = 10.dp))
            LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                items(state.modalities) { modality ->
                    ModalityCard(
                        modality = modality,
                        selected = modality == state.selectedModality ||
                            (modality.isAll && state.selectedModality == null),
                        onClick = { onAction(MarketplaceFilterAction.SelectModality(modality)) },
                    )
                }
            }

            if (state.lotteryEnabled) {
                LegacyLabel("Lotéricas", Modifier.padding(start = 10.dp, top = 10.dp))
                LegacyInput(
                    value = state.lottery,
                    onValueChange = { onAction(MarketplaceFilterAction.ChangeLottery(it)) },
                    placeholder = "Nome ou código",
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.padding(start = 12.dp, top = 10.dp, end = 20.dp),
                )
                RetailerSearchFeedback(
                    loading = state.retailerLoading,
                    message = state.retailerMessage,
                    suggestions = state.retailerSuggestions,
                    onSelect = { onAction(MarketplaceFilterAction.SelectRetailer(it)) },
                )
                Text(
                    "Insira o nome da lotérica ou o código somente com os\n" +
                        "números do segundo campo, com 6 dígitos. Exemplo:\n" +
                        "E.Lotérico 10.000004-01, digite 000004.",
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 10.dp, end = 20.dp),
                    color = CaixaDS.colors.legacyGraphite,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }

            TwoFieldRow(
                leftLabel = "Valor Mínimo Cota",
                rightLabel = "Valor Máximo Cota",
                leftValue = state.minimumValue,
                rightValue = state.maximumValue,
                leftPlaceholder = "0,00",
                rightPlaceholder = "0,00",
                keyboardType = KeyboardType.Decimal,
                currency = true,
                onLeftChange = { onAction(MarketplaceFilterAction.ChangeMinimumValue(it)) },
                onRightChange = { onAction(MarketplaceFilterAction.ChangeMaximumValue(it)) },
            )
            TwoFieldRow(
                leftLabel = "Quant. de Cotas Mínima",
                rightLabel = "Quant. de Cotas Máxima",
                leftValue = state.minimumShares,
                rightValue = state.maximumShares,
                leftPlaceholder = "2",
                rightPlaceholder = "100",
                keyboardType = KeyboardType.Number,
                leftError = state.minimumSharesError,
                rightError = state.maximumSharesError,
                onLeftChange = { onAction(MarketplaceFilterAction.ChangeMinimumShares(it)) },
                onRightChange = { onAction(MarketplaceFilterAction.ChangeMaximumShares(it)) },
            )

            LegacyLabel(
                if (state.selectedModality?.baseName.equals("Loteca", ignoreCase = true)) {
                    "Quantidade de palpites"
                } else {
                    "Quantidade de apostas"
                },
                Modifier.padding(start = 10.dp, top = 10.dp),
            )
            NumberGrid(
                options = state.betOptions,
                selected = setOfNotNull(state.selectedBetQuantity),
                modifier = Modifier.padding(start = 12.dp, end = 15.dp),
            ) { onAction(MarketplaceFilterAction.SelectBetQuantity(it)) }

            LegacyDivider()
            LegacyAccordionSelector(
                title = "Quantidade de dezenas",
                testTag = "number_quantity_selector",
                selected = state.selectedNumberQuantity?.let(::listOf).orEmpty(),
                onOpen = { numberDialog = NumberDialog.Quantity },
                onRemove = { onAction(MarketplaceFilterAction.SelectNumberQuantity(null)) },
                onClear = { onAction(MarketplaceFilterAction.SelectNumberQuantity(null)) },
                clearLabel = "Excluir Quantidade",
            )
            LegacyDivider()

            if (showWantedFilters) {
                LegacyAccordionSelector(
                    title = "Números que quero",
                    testTag = "wanted_numbers_selector",
                    selected = state.wantedNumbers,
                    onOpen = { numberDialog = NumberDialog.Wanted },
                    onRemove = { value ->
                        onAction(MarketplaceFilterAction.ChangeWantedNumbers(state.wantedNumbers - value))
                    },
                    onClear = { onAction(MarketplaceFilterAction.ChangeWantedNumbers(emptyList())) },
                    clearLabel = "Excluir números",
                )
                LegacyDivider()
                LegacyAccordionSelector(
                    title = "Números que não quero",
                    testTag = "unwanted_numbers_selector",
                    selected = state.unwantedNumbers,
                    onOpen = { numberDialog = NumberDialog.Unwanted },
                    onRemove = { value ->
                        onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(state.unwantedNumbers - value))
                    },
                    onClear = { onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(emptyList())) },
                    clearLabel = "Excluir números",
                )
                LegacyDivider()
            }

            (state.configurationError ?: state.validationMessage)?.let {
                Text(
                    it,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LegacyActionButton(
                    text = "Limpar filtros",
                    outlined = true,
                    onClick = { onAction(MarketplaceFilterAction.Clear) },
                    modifier = Modifier.padding(start = 20.dp, end = 16.dp).weight(1f),
                )
                LegacyActionButton(
                    text = "Aplicar",
                    outlined = false,
                    enabled = state.canApply,
                    onClick = { onAction(MarketplaceFilterAction.Apply) },
                    modifier = Modifier.padding(start = 16.dp, end = 20.dp).weight(1f),
                )
            }
        }
    }

    numberDialog?.let { dialog ->
        val options = when (dialog) {
            NumberDialog.Quantity -> state.numberOptions
            NumberDialog.Wanted, NumberDialog.Unwanted -> wantedNumberOptions(state.selectedModality)
        }
        val selected = when (dialog) {
            NumberDialog.Quantity -> state.selectedNumberQuantity?.let(::listOf).orEmpty()
            NumberDialog.Wanted -> state.wantedNumbers
            NumberDialog.Unwanted -> state.unwantedNumbers
        }
        val unavailable = when (dialog) {
            NumberDialog.Wanted -> state.unwantedNumbers.toSet()
            NumberDialog.Unwanted -> state.wantedNumbers.toSet()
            NumberDialog.Quantity -> emptySet()
        }
        LegacyNumberSelectionDialog(
            title = when (dialog) {
                NumberDialog.Quantity -> "Quantidade de dezenas"
                NumberDialog.Wanted -> "Números que quero"
                NumberDialog.Unwanted -> "Números que não quero"
            },
            options = options,
            selected = selected,
            unavailable = unavailable,
            singleSelection = dialog == NumberDialog.Quantity,
            onDismiss = { numberDialog = null },
            onConfirm = { values ->
                when (dialog) {
                    NumberDialog.Quantity -> onAction(
                        MarketplaceFilterAction.SelectNumberQuantity(values.firstOrNull()),
                    )
                    NumberDialog.Wanted -> onAction(MarketplaceFilterAction.ChangeWantedNumbers(values))
                    NumberDialog.Unwanted -> onAction(MarketplaceFilterAction.ChangeUnwantedNumbers(values))
                }
                numberDialog = null
            },
        )
    }
}

private enum class NumberDialog { Quantity, Wanted, Unwanted }

@Composable
private fun LegacyLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CaixaDS.colors.legacyText,
    fontSize: Int = 12,
) {
    Text(text, modifier, color = color, fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun ModalityCard(
    modality: MarketplaceFilterModalityUi,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val modalityColor = when (modality.baseName) {
        "Mega-Sena" -> CaixaDSColorTokens.MegaSena
        "Lotofácil" -> CaixaDSColorTokens.Lotofacil
        "Quina" -> CaixaDSColorTokens.Quina
        "+Milionária" -> CaixaDSColorTokens.MaisMilionaria
        "Dupla Sena" -> CaixaDSColorTokens.DuplaSena
        "Timemania" -> CaixaDSColorTokens.Timemania
        "Dia de Sorte" -> CaixaDSColorTokens.DiaDeSorte
        "Super Sete" -> CaixaDSColorTokens.SuperSete
        "Loteca" -> CaixaDSColorTokens.Orange700
        else -> CaixaDS.colors.legacyPrimaryButton
    }
    Card(
        modifier = Modifier.padding(8.dp).size(110.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDS.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).background(modalityColor),
                contentAlignment = Alignment.Center,
            ) {
                if (modality.isAll) {
                    Text(
                        "Todas",
                        color = CaixaDS.colors.onBrand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    CaixaDSLotteryMark(
                        modifier = Modifier.size(33.dp),
                        lightColor = CaixaDS.colors.onBrand,
                        accentColor = CaixaDS.colors.onBrand,
                    )
                }
                if (selected) {
                    Box(
                        Modifier.align(Alignment.TopEnd).padding(5.dp).size(18.dp)
                            .background(CaixaDS.colors.surface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            CaixaDSIcons.Confirm,
                            contentDescription = "Selecionada",
                            tint = modalityColor,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
            if (!modality.isAll) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(33.dp).background(modalityColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modality.name,
                        color = CaixaDS.colors.onBrand,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun RetailerSearchFeedback(
    loading: Boolean,
    message: String?,
    suggestions: List<Retailer>,
    onSelect: (Retailer) -> Unit,
) {
    if (loading) {
        Box(
            Modifier.fillMaxWidth().height(38.dp).padding(end = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = CaixaDS.colors.legacyFilterBlue,
                strokeWidth = 2.dp,
            )
        }
    }
    message?.let {
        Text(
            it,
            modifier = Modifier.padding(start = 12.dp, top = 6.dp, end = 20.dp),
            color = CaixaDS.colors.legacyGraphite,
            fontSize = 12.sp,
        )
    }
    suggestions.take(6).forEach { retailer ->
        Row(
            modifier = Modifier.fillMaxWidth().height(42.dp)
                .padding(start = 12.dp, end = 20.dp)
                .clickable { onSelect(retailer) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                retailerDisplayLabel(retailer),
                modifier = Modifier.weight(1f),
                color = CaixaDS.colors.legacyGraphite,
                fontSize = 13.sp,
                maxLines = 1,
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 12.dp, end = 20.dp),
            color = CaixaDS.colors.legacyFilterLine,
        )
    }
}

@Composable
private fun TwoFieldRow(
    leftLabel: String,
    rightLabel: String,
    leftValue: String,
    rightValue: String,
    leftPlaceholder: String,
    rightPlaceholder: String,
    keyboardType: KeyboardType,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit,
    currency: Boolean = false,
    leftError: String? = null,
    rightError: String? = null,
) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Column(Modifier.weight(1f).padding(start = 10.dp, end = 15.dp)) {
            LegacyLabel(leftLabel)
            LegacyInput(
                leftValue,
                onLeftChange,
                leftPlaceholder,
                keyboardType,
                currency = currency,
                modifier = Modifier.padding(top = 10.dp),
            )
            LegacyFieldError(leftError)
        }
        Column(Modifier.weight(1f).padding(end = 20.dp)) {
            LegacyLabel(rightLabel)
            LegacyInput(
                rightValue,
                onRightChange,
                rightPlaceholder,
                keyboardType,
                currency = currency,
                modifier = Modifier.padding(top = 10.dp),
            )
            LegacyFieldError(rightError)
        }
    }
}

@Composable
private fun LegacyFieldError(message: String?) {
    message?.let {
        Text(
            it,
            modifier = Modifier.padding(top = 3.dp),
            color = MaterialTheme.colorScheme.error,
            fontSize = 10.sp,
            lineHeight = 12.sp,
        )
    }
}

@Composable
private fun LegacyInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    currency: Boolean = false,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().height(48.dp).border(
            1.dp,
            CaixaDS.colors.legacyGraphite,
            RoundedCornerShape(4.dp),
        ).padding(horizontal = 8.dp),
        textStyle = TextStyle(color = CaixaDS.colors.legacyGraphite, fontSize = 14.sp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        decorationBox = { inner ->
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                if (currency) {
                    Text("R$", color = CaixaDS.colors.legacyGraphite, fontSize = 14.sp)
                    Spacer(Modifier.width(5.dp))
                }
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = CaixaDS.colors.legacyGraphite, fontSize = 14.sp)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
private fun NumberGrid(
    options: List<Int>,
    selected: Set<Int>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
) {
    val rows = ((options.size + 5) / 6).coerceAtLeast(1)
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier.fillMaxWidth().height((rows * 45).dp),
        userScrollEnabled = false,
    ) {
        items(options) { value -> NumberCell(value, value in selected) { onClick(value) } }
    }
}

@Composable
private fun NumberCell(value: Int, selected: Boolean, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(45.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.size(29.dp).clip(CircleShape)
                .then(
                    if (selected) Modifier.background(CaixaDS.colors.legacyPrimaryButton)
                    else Modifier.border(1.dp, CaixaDS.colors.legacyDisabledControl, CircleShape),
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                value.toString(),
                color = if (selected) CaixaDS.colors.onBrand else CaixaDS.colors.legacyGraphite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun LegacyAccordionSelector(
    title: String,
    testTag: String,
    selected: List<Int>,
    onOpen: () -> Unit,
    onRemove: (Int) -> Unit,
    onClear: () -> Unit,
    clearLabel: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(42.dp).testTag(testTag).clickable(onClick = onOpen)
            .padding(start = 10.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegacyLabel(title, Modifier.weight(1f))
        Icon(
            CaixaDSIcons.Add,
            contentDescription = "Adicionar $title",
            tint = CaixaDS.colors.legacyFilterBlue,
            modifier = Modifier.size(22.dp),
        )
    }
    if (selected.isNotEmpty()) {
        SelectedNumberGrid(selected, onRemove)
        LegacyDeleteButton(clearLabel, onClear)
    }
}

@Composable
private fun SelectedNumberGrid(values: List<Int>, onRemove: (Int) -> Unit) {
    val rows = ((values.size + 5) / 6).coerceAtLeast(1)
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = Modifier.fillMaxWidth().padding(end = 20.dp).height((rows * 45).dp),
        userScrollEnabled = false,
    ) {
        items(values) { value ->
            Box(Modifier.fillMaxWidth().height(45.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier.size(29.dp).background(CaixaDS.colors.legacyPrimaryButton, CircleShape)
                        .clickable { onRemove(value) },
                    // A célula inteira remove o valor, como o item do RecyclerView legado.
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        value.toString(),
                        color = CaixaDS.colors.onBrand,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        Modifier.align(Alignment.TopEnd).size(12.dp)
                            .background(CaixaDS.colors.surface, CircleShape)
                            .border(1.dp, CaixaDS.colors.legacyFilterBlue, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            CaixaDSIcons.Close,
                            contentDescription = null,
                            tint = CaixaDS.colors.legacyFilterBlue,
                            modifier = Modifier.size(9.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegacyDeleteButton(text: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier.width(127.dp).height(34.dp),
            shape = RoundedCornerShape(17.dp),
            border = BorderStroke(1.dp, CaixaDS.colors.legacyFilterBlue),
            colors = ButtonDefaults.buttonColors(
                containerColor = CaixaDS.colors.surface,
                contentColor = CaixaDS.colors.legacyFilterBlue,
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun LegacyDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp),
        thickness = 2.dp,
        color = CaixaDS.colors.legacyFilterLine,
    )
}

@Composable
private fun LegacyActionButton(
    text: String,
    outlined: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = if (outlined) BorderStroke(1.dp, CaixaDS.colors.legacyFilterBlue) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) CaixaDS.colors.surface else CaixaDS.colors.legacyPrimaryButton,
            contentColor = if (outlined) CaixaDS.colors.legacyFilterBlue else CaixaDS.colors.onBrand,
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LegacyCartSummary() {
    Row(
        modifier = Modifier.fillMaxWidth().height(36.dp).background(CaixaDS.colors.legacyFilterBlue),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "R$ 00,00",
            modifier = Modifier.padding(start = 8.dp),
            color = CaixaDS.colors.onBrand,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Valor atual do carrinho",
            modifier = Modifier.padding(start = 8.dp).weight(1f),
            color = CaixaDS.colors.onBrand,
            fontSize = 12.sp,
        )
        Box(
            modifier = Modifier.fillMaxHeight().width(72.dp).background(CaixaDS.colors.legacyCartDark),
            contentAlignment = Alignment.Center,
        ) {
            val iconColor = CaixaDS.colors.onBrand
            Canvas(Modifier.size(24.dp)) {
                drawLine(iconColor, start = center.copy(x = size.width * .2f), end = center.copy(x = size.width * .78f), strokeWidth = 2.dp.toPx())
                drawLine(iconColor, start = center.copy(x = size.width * .3f, y = size.height * .25f), end = center.copy(x = size.width * .4f, y = size.height * .7f), strokeWidth = 2.dp.toPx())
                drawCircle(iconColor, radius = 2.dp.toPx(), center = center.copy(x = size.width * .43f, y = size.height * .82f))
                drawCircle(iconColor, radius = 2.dp.toPx(), center = center.copy(x = size.width * .72f, y = size.height * .82f))
            }
            Box(
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 5.dp, end = 12.dp).size(16.dp)
                    .background(CaixaDS.colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("0", color = CaixaDS.colors.legacyCartDark, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Modal de tela cheia equivalente à antiga `QueroNaoQueroActivity`. */
@Composable
private fun LegacyNumberSelectionDialog(
    title: String,
    options: List<Int>,
    selected: List<Int>,
    unavailable: Set<Int>,
    singleSelection: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit,
) {
    var current by remember(title, selected) { mutableStateOf(selected) }
    var conflictingNumber by remember(title) { mutableStateOf<Int?>(null) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(CaixaDS.colors.surface)
                .statusBarsPadding().navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(39.dp)
                    .background(CaixaDS.colors.legacyFilterBlue),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    title,
                    modifier = Modifier.padding(start = 24.dp).weight(1f),
                    color = CaixaDS.colors.onBrand,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier.fillMaxHeight().clickable(onClick = onDismiss).padding(end = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        CaixaDSIcons.Close,
                        contentDescription = null,
                        tint = CaixaDS.colors.onBrand,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(
                        "Fechar",
                        modifier = Modifier.padding(start = 7.dp),
                        color = CaixaDS.colors.onBrand,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 10.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(options) { value ->
                    NumberCell(value, value in current) {
                        if (value in unavailable) {
                            conflictingNumber = value
                        } else {
                            current = if (singleSelection) {
                                if (value in current) emptyList() else listOf(value)
                            } else if (value in current) {
                                current - value
                            } else {
                                current + value
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 16.dp)) {
                LegacyDialogButton("Cancelar", true, onDismiss, Modifier.weight(1f).padding(end = 5.dp))
                LegacyDialogButton(
                    "Confirmar",
                    false,
                    { onConfirm(current.sorted()) },
                    Modifier.weight(1f).padding(start = 5.dp),
                )
            }
        }
    }
    if (conflictingNumber != null) {
        CaixaDSLegacyDialog(
            title = "Atenção",
            message = "Não é possível selecionar um número já escolhido no outro filtro.",
            positiveLabel = "Entendi",
            onPositive = { conflictingNumber = null },
        )
    }
}

@Composable
private fun LegacyDialogButton(
    text: String,
    outlined: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(31.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (outlined) BorderStroke(1.dp, CaixaDS.colors.legacyPrimaryButton) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (outlined) CaixaDS.colors.surface else CaixaDS.colors.legacyPrimaryButton,
            contentColor = if (outlined) CaixaDS.colors.legacyPrimaryButton else CaixaDS.colors.onBrand,
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(text, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun MarketplaceFilterPreview() {
    val mega = MarketplaceFilterModalityUi(
        id = 2,
        name = "Mega-Sena",
        contestType = 1,
        numberOptions = (6..20).toList(),
        maximumNumber = 60,
    )
    MarketplaceFilterScreen(
        state = MarketplaceFilterUiState(
            modalities = listOf(allModalities, mega),
            selectedModality = mega,
            betOptions = (3..8).toList(),
            numberOptions = (6..20).toList(),
            selectedBetQuantity = 4,
            selectedNumberQuantity = 7,
            wantedNumbers = listOf(7, 12),
        ),
        onAction = {},
    )
}
