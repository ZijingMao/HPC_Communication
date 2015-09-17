#!/usr/bin/env python

import sys
 
last_key_store = None
last_key_month = None
# total consumption of current sex
current_sum = 0
# total count of current sex
current_cnt = 0
# current max value
current_max = 0
# current min value
current_min = 0
# the consumption mean of different sex
current_mean = 0
# which month has the largest sales performance
# by comparing the average of each month
best_month = 1
# what is the best store
best_store = 1
# what is the best average for the month
best_sale_perf = 0

for input_line in sys.stdin:
   input_line = input_line.strip()
   store, date, money, value = input_line.split("\t")
   money = float(money)
   value = int(value)
   store = int(store)
   # read the month information from date
   month = int(date[0:2])
   if last_key_store == store:  # if still in the same store
       if last_key_month == month:   # if still in the same month
          # update current sum and current count for current sex
          current_sum += money
          current_cnt += value
          # compare to get the maximum and minimun money
          if current_max < money:
	           current_max = money
          if current_min > money:
	           current_min = money
       else:
          if last_key_month:
             current_mean = current_sum/current_cnt
             # if the best sale performance is smaller than current performance
             # change the best month to current month
             if best_sale_perf < current_sum:
                best_month = month - 1
                best_store = store
                best_sale_perf = current_sum
             print( "%s\t%s\t%f\t%d\t%f\t%f\t%f" % (last_key_store, last_key_month, current_sum, current_cnt, current_max, current_min, current_mean) )
          current_cnt = value
          current_sum = money
          current_max = money
          current_min = money
          last_key_month = month
   else:
       if last_key_month:
           current_mean = current_sum/current_cnt
           # if the best sale performance is smaller than current performance
           # change the best month to current month
           if best_sale_perf < current_sum:
              best_month = month - 1
              best_store = store - 1
              print best_store
              best_sale_perf = current_sum
           print( "%s\t%s\t%f\t%d\t%f\t%f\t%f" % (last_key_store, last_key_month, current_sum, current_cnt, current_max, current_min, current_mean) )
       current_cnt = value
       current_sum = money
       current_max = money
       current_min = money
       last_key_store = store
       last_key_month = month

if last_key_store == store:
   current_mean = current_sum/current_cnt
   if best_sale_perf < current_sum:
       best_month = month - 1
       best_store = store
       best_sale_perf = current_sum
   print( "%s\t%s\t%f\t%d\t%f\t%f\t%f" % (last_key_store, last_key_month, current_sum, current_cnt, current_max, current_min, current_mean) )

# print out the best month and the best sale performance
print( "The best store is %d. In the store, the best month is %d, which has an sale performance is %f dollars." % (best_store, best_month, best_sale_perf) )
