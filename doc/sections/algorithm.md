>#Fuzzer Implementation

####**High-level objects and relationships**
![High-level](https://github.com/umeding/fuzzer/raw/master/doc/sections/hilevel-objects.png "High-level")


####**Fuzzer Core Algorithm**
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
####**Universe of discourse (UOD)**
For simplicity, all membership ranges are normalized to fall between
`0..255`.

####**Center of area (COA) method**
The COA aka "centroid" strategy generates the center of gravity of the possibility
distribution of a control action. It is widely used in the
implementation of fuzzy logic systems.
```
moment = mass × distance from a point
     y = total moments / total area
```

####**Membership maps**
Inside Fuzzer, the memberships defined for each variable are mapped
into an array across the universe of discourse of the variable (a.k.a.
__membership map__). A membership map size is determined by the
defined range of the  universe of discourse of a fuzzy variable, the
resolution of the sensed variable and the mapping scaler, using the
following equation:
```
MAPsize = 1 + Kmap * (Vmax - Vmin) / Vres
```
Where `MAPsize` is the membership map size, `Vmin` is the minimum
value of universe of discourse of the defined variable, `Vmax` is the
maximum value of the universe of discourse of the defined variable,
`Vres` is the resolution of the defined variable, and `Kmap` is the
mapping scaler. The mapping scaler is designed as a safety factor to
avoid the creation of an unreasonable map size.

Once the map size (`MAPsize`) is determined, explicit or implicit
membership values need to be assigned to each discrete point along a
universe of discourse. So, each fuzzy set may be thought of as a
subarray, with the index of its ith point given by:
```
Vi = Vmin + [(Vmax - Vmin) / (MAPsize - 1)] * i
```

For triangular or trapezoidal membership functions, any 2 points
(&#181;, Vk)
```
&#181;
```

