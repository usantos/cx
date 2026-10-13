package br.gov.caixa.loterias.core.presentation

data class WorkflowSpec(
    val route: String,
    val title: String,
    val description: String,
    val fields: List<WorkflowField> = emptyList(),
    val primaryAction: String = "Continuar",
    val secondaryAction: String? = null,
    val notice: String? = null,
)

data class WorkflowField(
    val key: String,
    val label: String,
    val kind: WorkflowFieldKind = WorkflowFieldKind.TEXT,
    val required: Boolean = false,
)

enum class WorkflowFieldKind { TEXT, NUMBER, CURRENCY, PASSWORD, CHECKBOX }
