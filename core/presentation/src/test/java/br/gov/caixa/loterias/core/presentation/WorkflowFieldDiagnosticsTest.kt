package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.presentation.state.WorkflowField
import br.gov.caixa.loterias.core.presentation.state.WorkflowFieldKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkflowFieldDiagnosticsTest {
    @Test fun `diagnostics omit field identifiers and labels`() {
        val field = WorkflowField("test-field-id", "Test private label", WorkflowFieldKind.PASSWORD, true)
        val diagnostic = field.toString()
        assertFalse(diagnostic.contains(field.key))
        assertFalse(diagnostic.contains(field.label))
    }

    @Test fun `form binding and field semantics remain intact`() {
        val field = WorkflowField("test-field-id", "Test private label", WorkflowFieldKind.PASSWORD, true)
        assertEquals(field, field.copy())
        assertEquals("test-field-id", field.key)
        assertEquals("Test private label", field.label)
        assertEquals(WorkflowFieldKind.PASSWORD, field.kind)
        assertTrue(field.required)
    }
}
