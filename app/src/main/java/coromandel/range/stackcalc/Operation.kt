package coromandel.range.stackcalc

import android.util.Log
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.pow

class Operation {
    enum class ResultType {
        SUCCESS, INSUFFICIENT_ITEMS, CONVERSION_ERROR, UNIT_ERROR, MATHS_ERROR
    }

    data class Result(val type: ResultType, val items: List<StackItem>, val itemsTaken: Int) {
        constructor(type: ResultType): this(type, listOf(), 0) {}
        constructor(type: ResultType, item: StackItem, itemsTaken: Int): this(type, listOf(item), itemsTaken) {}
    }

    companion object {
        fun exec(name: String, stack: List<StackItem>, useDegrees: Boolean): Result {
            when (name) {
                "ADD" -> {
                    if (stack.size < 2) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    // cannot add non-unit value to a unit value, or values with different denominators
                    if (stack[0].unit.isBlank() && !stack[1].unit.isBlank() || !stack[0].unit.denominator.equals(stack[1].unit.denominator)) {
                        return Result(ResultType.UNIT_ERROR)
                    }
                    // attempt to convert numerators
                    val convValue = convert(stack[0].value, stack[0].unit, stack[1].unit)
                    return if (convValue == null) {
                        Result(ResultType.UNIT_ERROR)
                    } else {
                        val newValue = stack[1].value + convValue
                        Result(ResultType.SUCCESS, StackItem(newValue, stack[1].unit), 2)
                    }
                }
                "SUBTRACT" -> {
                    if (stack.size < 2) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    // cannot add non-unit value to a unit value, or values with different denominators
                    if (stack[0].unit.isBlank() && !stack[1].unit.isBlank() || !stack[0].unit.denominator.equals(stack[1].unit.denominator)) {
                        return Result(ResultType.UNIT_ERROR)
                    }
                    // attempt to convert numerators
                    val convValue = convert(stack[0].value, stack[0].unit, stack[1].unit)
                    return if (convValue == null) {
                        Result(ResultType.UNIT_ERROR)
                    } else {
                        val newValue = stack[1].value - convValue
                        Result(ResultType.SUCCESS, StackItem(newValue, stack[1].unit), 2)
                    }
                }
                "MULTIPLY" -> {
                    if (stack.size < 2) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }

                    var convValue: Double? = null
                    var finalUnit: Unit? = null

                    if (stack[0].unit.isBlank()) {
                        finalUnit = stack[1].unit
                        convValue = stack[0].value
                    } else if (stack[1].unit.denominator.isNotBlank() && stack[0].unit.denominator.isBlank()) {
                        // multiplication canceling out a denominator
                        finalUnit = Unit(stack[1].unit.numerator)
                        convValue = convert(stack[0].value, stack[0].unit, Unit(stack[1].unit.denominator))
                    }

                    return if (convValue == null) {
                        Result(ResultType.UNIT_ERROR)
                    } else {
                        val newValue = stack[1].value * convValue
                        Result(ResultType.SUCCESS, StackItem(newValue, finalUnit!!), 2)
                    }
                }
                "DIVIDE" -> {
                    if (stack.size < 2) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }

                    var convValue: Double? = null
                    var finalUnit: Unit? = null

                    if (stack[0].unit.isBlank()) {
                        finalUnit = stack[1].unit
                        convValue = stack[0].value
                    } else if (stack[0].unit.denominator.isBlank() && stack[1].unit.denominator.isBlank()) {
                        convValue = convert(stack[0].value, stack[0].unit, stack[1].unit)
                        if (convValue == null) {
                            // division creating a rate unit
                            finalUnit = Unit(stack[1].unit.numerator, stack[0].unit.numerator)
                            convValue = stack[0].value
                        } else {
                            finalUnit = Unit()
                        }
                    }

                    return if (convValue == null) {
                        Result(ResultType.UNIT_ERROR)
                    } else {
                        val newValue = stack[1].value / convValue
                        Result(ResultType.SUCCESS, StackItem(newValue, finalUnit!!), 2)
                    }
                }
                "SIN" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    var angle = stack[0].value
                    if (useDegrees) {
                        angle = toRadians(angle)
                    }
                    var value = kotlin.math.sin(angle)
                    if (kotlin.math.abs(value) < 0.00000000001) {
                        value = 0.0
                    }
                    val newItem = StackItem(value, stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "ASIN" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    var angle = kotlin.math.asin(stack[0].value)
                    if (useDegrees) {
                        angle = toDegrees(angle)
                    }
                    val newItem = StackItem(angle, stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "COS" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    var angle = stack[0].value
                    if (useDegrees) {
                        angle = toRadians(angle)
                    }
                    var value = kotlin.math.cos(angle)
                    if (kotlin.math.abs(value) < 0.00000000001) {
                        value = 0.0
                    }
                    val newItem = StackItem(value, stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "ACOS" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    var angle = kotlin.math.acos(stack[0].value)
                    if (useDegrees) {
                        angle = toDegrees(angle)
                    }
                    val newItem = StackItem(angle, stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "TAN" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    var angle = stack[0].value
                    if (useDegrees) {
                        angle = toRadians(angle)
                    }
                    var value = kotlin.math.tan(angle)
                    if (kotlin.math.abs(value) < 0.00000000001) {
                        value = 0.0
                    }
                    val newItem = StackItem(value, stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "ATAN" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    var angle = kotlin.math.atan(stack[0].value)
                    if (useDegrees) {
                        angle = toDegrees(angle)
                    }
                    val newItem = StackItem(angle, stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "PI" -> {
                    return Result(ResultType.SUCCESS, StackItem(Math.PI), 0)
                }
                "POWER" -> {
                    if (stack.size < 2) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    if (!stack[0].unit.isBlank()) {
                        return Result(ResultType.UNIT_ERROR)
                    }
                    val newItem = StackItem(stack[1].value.pow(stack[0].value), stack[1].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "LOG" -> {
                    if (stack.size < 2) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    if (!stack[0].unit.isBlank()) {
                        return Result(ResultType.UNIT_ERROR)
                    }
                    val newItem = StackItem(kotlin.math.log(stack[1].value, stack[0].value), stack[1].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "ABS" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    val newItem = StackItem(kotlin.math.abs(stack[0].value), stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "SQRT" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    val newItem = StackItem(kotlin.math.sqrt(stack[0].value), stack[0].unit)
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "SPLIT" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    if (!stack[0].unit.denominator.isBlank() || !(stack[0].unit.numerator in listOf("hr", "min", "sec"))) {
                        return Result(ResultType.UNIT_ERROR)
                    }

                    val result = mutableListOf<StackItem>()
                    var part: Int

                    val hours = convert(stack[0].value, stack[0].unit, Unit("hr"))!!
                    Log.e("SPLIT", "HR: " + hours)
                    part = hours.toInt()
                    if (part > 0) {
                        result.add(StackItem(part.toDouble(), Unit("hr")))
                    }

                    val mins = convert(hours - part, Unit("hr"), Unit("min"))!!
                    Log.e("SPLIT", "MIN: " + mins)
                    part = mins.toInt()
                    if (part > 0) {
                        result.add(StackItem(part.toDouble(), Unit("min")))
                    }

                    val secs = convert(mins - part, Unit("min"), Unit("sec"))!!
                    Log.e("SPLIT", "SEC: " + secs)
                    part = secs.toInt()
                    if (part > 0) {
                        result.add(StackItem(part.toDouble(), Unit("sec")))
                    }

                    return Result(ResultType.SUCCESS, result.reversed(), 1)
                }
                "FLIP" -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    if (stack[0].unit.denominator.isBlank()) {
                        return Result(ResultType.UNIT_ERROR)
                    }
                    val newItem = StackItem(1.0 / stack[0].value, Unit(stack[0].unit.denominator, stack[0].unit.numerator))
                    return Result(ResultType.SUCCESS, newItem, 1)
                }
                "CLEAR_UNIT" -> {
                    return if (stack.size < 1) {
                        Result(ResultType.INSUFFICIENT_ITEMS)
                    } else {
                        Result(ResultType.SUCCESS, listOf(StackItem(stack[0].value)), 1)
                    }
                }
                else -> {
                    if (stack.size < 1) {
                        return Result(ResultType.INSUFFICIENT_ITEMS)
                    }
                    val newUnit = Unit(name)
                    if (stack[0].unit.denominator.isNotBlank()) {
                        if (newUnit.denominator.isNotBlank()) {
                            return Result(ResultType.UNIT_ERROR)
                        } else {
                            newUnit.denominator = stack[0].unit.denominator
                        }
                    }
                    val convertedValue = convert(stack[0].value, stack[0].unit, newUnit)
                    return if (convertedValue == null) {
                        Result(ResultType.CONVERSION_ERROR)
                    } else {
                        Result(ResultType.SUCCESS, StackItem(convertedValue, newUnit), 1)
                    }
                }
            }
        }

        fun convert(value: Double, oldUnit: Unit, newUnit: Unit): Double? {
            // if the new unit is the same or there is no current set unit
            if (oldUnit.equals(newUnit) || oldUnit.isBlank()) {
                return value
            }

            // currently don't support converting divisor units
            if (newUnit.denominator.isNotBlank() && !oldUnit.denominator.equals(newUnit.denominator)) {
                return null
            }

            val fromUnit = oldUnit.numerator.toUpperCase()
            val toUnit = newUnit.numerator.toUpperCase()

            // 1. lookup what the base unit is and convert to it
            val baseUnit = when (fromUnit) {
                "SEC", "MIN", "HR" -> "SEC"
                "KM", "M", "NMI", "MI", "FT", "IN" -> "M"
                "KG", "G", "LBS" -> "G"
                "L", "GAL", "QRT" -> "L"
                else -> return null
            }
            val baseFactor = when (fromUnit) {
                "SEC" -> 1.00000
                "MIN" -> 60.0000
                "HR" -> 3600.00
                "KM" -> 1000.0
                "M" -> 1.00000
                "NMI" -> 1852.00
                "MI" -> 1609.34
                "FT" -> 0.304800
                "IN" -> 0.0253999
                "KG" -> 1000.00
                "G" -> 1.00000
                "LBS" -> 453.592
                "L" -> 1.00000
                "GAL" -> 3.78541
                "QRT" -> 0.946353
                else -> return null
            }

            // 2. convert from the base unit to the desired unit
            val finalFactor = when (baseUnit) {
                "SEC" -> {
                    when (toUnit) {
                        "MIN" -> 1.0 / 60.0000
                        "HR" -> 1.0 / 3600.00
                        "SEC" -> 1.00000
                        else -> return null
                    }
                }
                "M" -> {
                    when (toUnit) {
                        "NMI" -> 0.000539957
                        "FT" -> 3.28084
                        "KM" -> 0.001000000
                        "MI" -> 0.000621371
                        "IN" -> 39.3701
                        "M" -> 1.00000
                        else -> return null
                    }
                }
                "G" -> {
                    when (toUnit) {
                        "LBS" -> 2.20462
                        "KG" -> 0.001000000
                        "G" -> 1.00000
                        else -> return null
                    }
                }
                "L" -> {
                    when (toUnit) {
                        "GAL" -> 0.264172
                        "L" -> 1.00000
                        "QRT" -> 1.05669
                        else -> return null
                    }
                }

                else -> return null
            }

            val result = value * baseFactor * finalFactor

            var bd = BigDecimal(result)
            bd = bd.round(MathContext(5))
            return bd.toDouble()
        }
    }
}