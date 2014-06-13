>#Fuzzer Core Algorithm


```
function evaluate() 
do
  while (not all rules evaluated)
  do
    find the fire strength for each rule;
    compute the area size of the inferred control action for each rule;
  end
  
  while (not all output variables done) 
    union the inferred areas for current output variable;
    compute the moment of this area;
    compute the crisp output using COA defuzzification method;
  do
  
  return crisp output variables;
end
```

####**Center of area (COA) method**
The COA strategy generates the center of gravity of the possibility
distribution of a control action. It is widely used in the
implementation of fuzzy logic systems.

