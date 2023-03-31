package coromandel.range.stackcalc

import org.junit.Test

import org.junit.Assert.*

class OperationsUnitTest {
    @Test
    fun unit_conversions() {
        var result: Operation.Result

        // basic
        result = Operation.exec("MIN", listOf(StackItem(1.0)), true)
        assertEquals("Assign a unit", "MIN", result.items[0].unit.numerator)

        // time
        result = Operation.exec("SEC", listOf(StackItem(1.0, Unit("MIN"))), true)
        assertEquals(60.0, result.items[0].value, 0.1)

        // length
        result = Operation.exec("ft", listOf(StackItem(1.0, Unit("Km"))), true)
        assertEquals(3280.84, result.items[0].value, 0.1)

        // volume
        result = Operation.exec("L", listOf(StackItem(1.0, Unit("GAL"))), true)
        assertEquals(3.78541, result.items[0].value, 0.1)

        // rate
        result = Operation.exec("Km/hr", listOf(StackItem(1.0)), true)
        assertEquals("Km", result.items[0].unit.numerator)
        assertEquals("hr", result.items[0].unit.denominator)

        // invalid
        result = Operation.exec("SEC", listOf(StackItem(10.0, Unit("KM"))), true)
        assertEquals("Impossible unit conversion", Operation.ResultType.CONVERSION_ERROR, result.type)
    }
}