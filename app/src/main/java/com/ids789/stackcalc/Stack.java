/*
    Manage a stack of values
    - hold each value stored on the stack and its unit
    - insert new values onto the stack
    - perform operations on the stack
    - retrieve a list of the values in the stack
 */

package com.ids789.stackcalc;

import android.content.Context;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Vector;

public class Stack {

    private class StackItem {
        Double value;
        Unit unit;

        StackItem(Double value, Unit unit) {
            this.value = value;
            this.unit = unit;
        }

        public String toString() {
            String text = "";
            DecimalFormat format = new DecimalFormat("0.#######");
            text += format.format(value);
            text += "<small><small>" + unit.toString() + "</small></small>";
            return text;
        }
    }

    private Vector<StackItem> stackItems;
    private Context context;

    public Stack(Context context) {
        stackItems = new Vector<StackItem>();
        this.context = context;
    }

    public void push(Unit.UnitType newType) {
        if (stackItems.size() < 1) return;
        StackItem item = stackItems.get(0);
        Double newValue = item.unit.convert(new Unit(newType), item.value);
        if (newValue != null) {
            stackItems.remove(0);
            stackItems.add(0, new StackItem(newValue, new Unit(newType, item.unit.denominator)));
        }
        else {
            Toast.makeText(context,R.string.unit_conversion_error,Toast.LENGTH_SHORT).show();
        }
    }

    public void push(Double value) {
        stackItems.add(0, new StackItem(value, new Unit()));
    }

    public void push(String operator) {
        Double newValue;
        Unit newUnit;
        StackItem item1, item2;
        switch (operator) {
            case "ADD":
                if (stackItems.size() < 2) break;
                item1 = stackItems.get(0);
                item2 = stackItems.get(1);
                newValue = item2.unit.convert(item1.unit, item2.value);  // check if the value is the same or can be converted
                if (newValue != null) {
                    stackItems.remove(0);
                    stackItems.remove(0);
                    stackItems.add(0, new StackItem(newValue + item1.value, item1.unit));
                }
                else {
                    Toast.makeText(context,R.string.unit_mismatch_error,Toast.LENGTH_SHORT).show();
                }
                break;
            case "SUBTRACT":
                if (stackItems.size() < 2) break;
                item1 = stackItems.get(0);
                item2 = stackItems.get(1);
                newValue = item2.unit.convert(item1.unit, item2.value);  // check if the value is the same or can be converted
                if (newValue != null) {
                    stackItems.remove(0);
                    stackItems.remove(0);
                    stackItems.add(0, new StackItem(newValue - item1.value, item1.unit));
                }
                else {
                    Toast.makeText(context,R.string.unit_mismatch_error,Toast.LENGTH_SHORT).show();
                }
                break;
            case "MULTIPLY":
                if (stackItems.size() < 2) break;
                item1 = stackItems.get(0);
                item2 = stackItems.get(1);
                newUnit = item2.unit.multiply(item1.unit);

                // if NUM 2 is a divisor ATTEMPT TO CONVERT NUM 1 to NUM 2 divisor unit
                newValue = null;
                if (newUnit != null) {
                    newValue = item1.value;
                }
                else if (item2.unit.denominator != Unit.UnitType.NONE) {
                    newValue = item1.unit.convert(new Unit(item2.unit.denominator), item1.value);
                    if (newValue != null) {
                        newUnit = new Unit(item2.unit.numerator);
                    }
                }

                if (newUnit != null) {
                    stackItems.remove(0);
                    stackItems.remove(0);
                    stackItems.add(0, new StackItem(item2.value * newValue, newUnit));
                }
                else {
                    Toast.makeText(context,R.string.unit_mismatch_error,Toast.LENGTH_SHORT).show();
                }
                break;
            case "DIVIDE":
                if (stackItems.size() < 2) break;
                item1 = stackItems.get(0);
                item2 = stackItems.get(1);
                newUnit = item2.unit.divide(item1.unit);
                if (newUnit != null) {
                    stackItems.remove(0);
                    stackItems.remove(0);
                    stackItems.add(0, new StackItem(item2.value / item1.value, newUnit));
                }
                else {
                    Toast.makeText(context,R.string.unit_mismatch_error,Toast.LENGTH_SHORT).show();
                }
                break;
            case "UNIT_NONE":
                //convertItem(Unit.UnitType.NONE);
                stackItems.get(0).unit = new Unit();
                break;
        }
    }

    public Double pop() {
        if (stackItems.size() >= 1) {
            Double value = stackItems.get(0).value;
            stackItems.remove(0);
            return value;
        }
        return null;
    }

    public void swap() {
        if (stackItems.size() >= 2) {
            StackItem item1 = stackItems.get(0);
            StackItem item2 = stackItems.get(1);
            stackItems.remove(0);
            stackItems.remove(0);
            stackItems.add(item2);
            stackItems.add(item1);
        }
    }

    public void flip() {
        if (stackItems.size() >= 1) {
            StackItem item = stackItems.get(0);
            stackItems.remove(0);
            stackItems.add(0, new StackItem(1 / item.value, item.unit.flip()));
        }
    }

    public String[] get() {
        String[] items = new String[stackItems.size()];
        for (int i = 0; i < stackItems.size(); i++) {
            items[i] = stackItems.get(i).toString();
        }
        return items;
    }

    public void clear() {
        stackItems.clear();
    }
}
