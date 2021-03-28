/*
    An object to represent a stack-value's unit and facilitate conversions to another unit type
 */

package com.ids789.stackcalc;

import java.util.HashMap;
import java.util.Map;

public class Unit {
    public UnitType numerator;
    public UnitType denominator;

    private Map<UnitType, Map<UnitType,Double>> conversionTable;

    public enum UnitType {
        NONE,
        SECOND,
        MINUTE,
        HOUR,
        KM,
        M,
        NM,
        FT,
        L,
        GAL,
        KG,
        LB
    }

    public Unit() {
        this.numerator = UnitType.NONE;
        this.denominator = UnitType.NONE;
        initConversionTable();
    }

    public Unit(UnitType type) {
        this.numerator = type;
        this.denominator = UnitType.NONE;
        initConversionTable();
    }

    public Unit(UnitType numerator, UnitType denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        initConversionTable();
    }

    private void initConversionTable() {
        conversionTable = new HashMap<UnitType, Map<UnitType, Double>>();
        Map<UnitType, Double> subTable;

        // From SECOND
        subTable = new HashMap<>();
        subTable.put(UnitType.MINUTE, 1.0/60.0);
        subTable.put(UnitType.HOUR, 1.0/3600);
        conversionTable.put(UnitType.SECOND, subTable);

        // From MINUTE
        subTable = new HashMap<>();
        subTable.put(UnitType.SECOND, 60.0);
        subTable.put(UnitType.HOUR, 1.0/60.0);
        conversionTable.put(UnitType.MINUTE, subTable);

        // From HOUR
        subTable = new HashMap<>();
        subTable.put(UnitType.SECOND, 3600.0);
        subTable.put(UnitType.MINUTE, 60.0);
        conversionTable.put(UnitType.HOUR, subTable);

        // From LITRES
        subTable = new HashMap<>();
        subTable.put(UnitType.GAL, 0.264172);
        conversionTable.put(UnitType.L, subTable);

        // From GALLONS
        subTable = new HashMap<>();
        subTable.put(UnitType.L, 3.78541);
        conversionTable.put(UnitType.GAL, subTable);

        // From KILOGRAMS
        subTable = new HashMap<>();
        subTable.put(UnitType.LB, 2.20462);
        conversionTable.put(UnitType.KG, subTable);

        // From POUNDS
        subTable = new HashMap<>();
        subTable.put(UnitType.KG, 0.453592);
        conversionTable.put(UnitType.LB, subTable);

        // From KILOMETERS
        subTable = new HashMap<>();
        subTable.put(UnitType.NM, 0.5399568035);
        subTable.put(UnitType.FT, 3280.84);
        subTable.put(UnitType.M, 1000.0);
        conversionTable.put(UnitType.KM, subTable);

        // From METRES
        subTable = new HashMap<>();
        subTable.put(UnitType.NM, 0.005399568035);
        subTable.put(UnitType.FT, 3.28084);
        subTable.put(UnitType.KM, 0.001);
        conversionTable.put(UnitType.M, subTable);

        // From NAUTICAL MILES
        subTable = new HashMap<>();
        subTable.put(UnitType.KM, 1.852);
        subTable.put(UnitType.M, 1852.0);
        subTable.put(UnitType.FT, 6076.11568);
        conversionTable.put(UnitType.NM, subTable);

        // From FEET
        subTable = new HashMap<>();
        subTable.put(UnitType.KM, 0.0003048);
        subTable.put(UnitType.M, 0.3048);
        subTable.put(UnitType.NM, 0.000164579);
        conversionTable.put(UnitType.FT, subTable);
    }

    private String unitString(UnitType type) {
        switch (type) {
            case NONE: return "";
            case SECOND: return "sec";
            case MINUTE: return "min";
            case HOUR: return "hr";
            case KM: return "km";
            case M: return "m";
            case NM: return "nm";
            case FT: return "Ft";
            case L: return "l";
            case GAL: return "gal";
            case KG: return "kg";
            case LB: return "lb";
        }
        return "";
    }

    public String toString() {
        String text = "";
        if (numerator != UnitType.NONE) {
            text += " " + unitString(numerator);
        }
        if (denominator != UnitType.NONE) {
            text += " / " + unitString(denominator);
        }
        return text;
    }

    public Double convert(Unit newUnit, Double value) {
        // if the new unit is the same or there is no current set unit
        if (this.equals(newUnit) || (this.numerator == UnitType.NONE && this.denominator == UnitType.NONE)) {
            return value;
        }

        // currently don't support converting divisor units
        if ((this.denominator != newUnit.denominator) && (newUnit.denominator != UnitType.NONE)) {
            return null;
        }

        Map<UnitType, Double> table = conversionTable.get(this.numerator);
        if (table.containsKey(newUnit.numerator)) {
            return value * table.get(newUnit.numerator);
        }
        else {
            return null;
        }
    }

    public Boolean equals(Unit unit) {
        return ((this.denominator == unit.denominator) && (this.numerator == unit.numerator));
    }

    public Unit multiply(Unit unitToMultiply) {
        // a unit preserving operation
        if (this.equals(unitToMultiply) ||
                (unitToMultiply.numerator == UnitType.NONE && unitToMultiply.denominator == UnitType.NONE)) {
            return this;
        }
        // cancel out a divisor unit
        else if ((this.denominator == unitToMultiply.numerator) && (unitToMultiply.denominator == UnitType.NONE)) {
            return new Unit(this.numerator);
        }
        // incompatible units
        else {
            return null;
        }
    }

    public Unit divide(Unit unitToDivide) {
        // unit cancelling out operation
        if (this.equals(unitToDivide)) {
            return new Unit();
        }
        // a unit preserving operation
        else if ((unitToDivide.numerator == UnitType.NONE) && (unitToDivide.denominator == UnitType.NONE)) {
            return this;
        }
        // create a divisor
        else if ((this.denominator == UnitType.NONE) & (unitToDivide.denominator == UnitType.NONE)) {
            return new Unit(this.numerator, unitToDivide.numerator);
        }
        // incompatible units
        else {
            return null;
        }
    }

    public Unit flip() {
        return new Unit(this.denominator, this.numerator);
    }
}
