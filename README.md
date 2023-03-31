# StackCalc

An android [Reverse Polish Notation ](https://en.wikipedia.org/wiki/Reverse_Polish_notation) calculator app.

* The contents of the stack are shown as a list with controls to reorder, copy or delete items.  
* Each value can be assigned a unit, the calculator can then do seemless unit conversions and track units throughout a calculation to ensure that the calculation is being done correctly as the result has the unit expected.  

![Screenshot](/screenshot.png)

### Usage
- The calculator uses standard reverse polish notation input so an operator performs its operation on the top 1 or 2 values on the stack.   
- To put a number on the stack press either enter or an operator button, pressing an operator button will perform an operation after the value has been inserted.    
- Units
  - A unit button will perform a unit conversion operation on the value at the top of the stack, the value will only be converted if the current unit is compatible (a 'none' unit can be converted to any type).  
  - Add, subtract or multiply operations can be done on values with different units as long as the units are compatible with eachother.  
  - A divide operation can be done between any 2 unit types, the unit is then shown as UNIT1/UNIT2.  The units can be flipped using the flip operator or cancelled out by multiplying by value with the denominator unit.  

### Limitations
- Different units cannot be multiplied together and a unit may only be divided by a single unit type.  
- The only units available are ones relevant to aviation
