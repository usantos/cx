package br.gov.caixa.loterias.core.testing.runner

import org.junit.runner.Runner
import org.junit.runners.Parameterized
import org.junit.runners.Suite
import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner

/** Mantém cada massa e sua instância isoladas, com os mesmos recursos reais dos demais testes de split. */
class DynamicFeatureParameterizedRobolectricTestRunner(testClass: Class<*>) : Suite(testClass, runners(testClass))

private fun parameterValues(testClass: Class<*>): List<Array<out Any?>> {
    val factory = testClass.methods.single { it.isAnnotationPresent(Parameterized.Parameters::class.java) }
    val values = factory.invoke(null) as Iterable<*>
    return values.map { value -> if (value is Array<*>) value else arrayOf(value) }
}

private fun runners(testClass: Class<*>): List<Runner> = parameterValues(testClass).indices.map { index ->
    object : DynamicFeatureRobolectricTestRunner(testClass) {
        override fun validateConstructor(errors: MutableList<Throwable>) = validateOnlyOneConstructor(errors)

        override fun testName(method: FrameworkMethod): String = "${super.testName(method)}[$index]"

        override fun getHelperTestRunner(bootstrappedTestClass: Class<*>): RobolectricTestRunner.HelperTestRunner =
            object : RobolectricTestRunner.HelperTestRunner(bootstrappedTestClass) {
                override fun validateConstructor(errors: MutableList<Throwable>) = validateOnlyOneConstructor(errors)

                override fun createTest(): Any = bootstrappedTestClass.constructors.single()
                    .newInstance(*parameterValues(bootstrappedTestClass)[index])
            }
    }
}
