#!/usr/bin/env python

import sys
 
# include results in the variable
results = []
# Read out first line
skipfirstline = True
for line in sys.stdin:
    # Skip the first line
    if skipfirstline:
        skipfirstline = False
        continue
    row = line.split()
    value = 1
    # read the shop ID, date, and consumption
    print( "%d\t%s\t%f\t%d" % (int(row[0]), row[1], float(row[3]), value) )