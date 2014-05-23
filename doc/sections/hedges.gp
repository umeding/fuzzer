set terminal pngcairo  enhanced font "arial,10" fontscale 1.0 size 512,280
set output 'hedges.png'
#set clip two
set key title "Degree of membership"
set key inside left top vertical Left reverse enhanced autotitles nobox
set key noinvert samplen 1 spacing 1 width 0 height 0 
set title "Hedges" 
set xrange [ -5.00000 : 5.00000 ] noreverse nowriteback
set yrange [ 0.00000 : 1.00000 ] noreverse nowriteback
unset colorbox
unset xtics
unset ytics
d1(x) = norm(x)**2
d2(x) = norm(x)
d3(x) = norm(x)**0.5
plot d1(x) lt rgb "green" title "concentrated (very)",\
     d2(x) lt rgb "blue" title "normal (unchanged)",\
     d3(x) lt rgb "orange" title "dialated (slightly)"

#pause -1 "hit return to continue"
