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

The membership for each fuzzy variable is usually defined as a
normalized value between 0.0 and 1.0, however, in Fuzzer we are
scaling them to a larger integer value for simplicity and numerical
stability (though this has not been tested). For example, the range of
`[0.0, 1.0]` for a defined membership is scaled into a range
`[0, 255]`. 

__Example__
Here is a fuzzy set with triangular shapes:
```
    output veloc(-5 .. 5 step 0.1) {
        nb = {-5,1} {-2,1} {-1,0};
        z = {-2,1} {0,1} {1,0};
        pb = S[1,3,5](x);
    }
```
The membership mapping for the fuzzy set `nb` looks as follows. The
mapping scalar is `Kmap` is `1` as the allowable maximum map size is
`256`. The computed map size is `101` in this case. The map of the
fuzzy set `nb` can be calculated across the defined universe of
discourse:
```java
    private final int[] veloc$nb = {
         255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
         255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
         255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
         255, 230, 204, 179, 153, 128, 102,  77,  51,  26,
           0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
           0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
           0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
           0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
           0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
           0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
           0
    };
}
```

