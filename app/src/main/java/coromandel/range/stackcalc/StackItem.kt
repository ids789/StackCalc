package coromandel.range.stackcalc

data class StackItem(val value: Double, val unit: Unit) {
    constructor(value: Double): this(value, Unit()) {}
}

data class Unit(var numerator: String, var denominator: String) {
    constructor(type: String) : this(type, "") {
        if (numerator.contains('/', false)) {
            val parts = numerator.split("/")
            numerator = parts[0]
            denominator = parts[1]
        }
    }

    constructor() : this("", "") {}

    override fun equals(other: Any?): Boolean {
        return other is Unit && numerator.equals(other.numerator) && denominator.equals(other.denominator)
    }

    fun isBlank(): Boolean {
        return numerator.isBlank() && denominator.isBlank()
    }

    override fun toString(): String {
        var text = numerator
        if (denominator.isNotBlank()) {
            text += "/" + denominator
        }
        return text
    }
}
