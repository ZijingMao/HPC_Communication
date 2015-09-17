#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define READY  0
#define RUNNING  1
#define WAITING  2
#define DONE  3


static char stateChars[] = {'r','R','w',0};

/* 1) handle state changes:
         running process completes CPU burst
         running process has quantum expire
         IO complete
   2) do context switch if necessary
         both ready
         one ready and CPU free
   3) append appropriate characters to character arrays
         avoid putting in multiple string terminators
*/
/* assume s1 and s2 point to buffers with enough space to hold the result */
/* assume that the int parameters are strictly greater than 0 */
void fcfs(char *s1, char *s2, int x1, int y1, int z1,
                                            int x2, int y2, int z2) {
   int i;                                   /* next string position (time) */
   int state1 = READY;                            /* start with both ready */
   int state2 = READY;
   int cpuLeft1 = x1;                       /* P1 next CPU burst remaining */
   int cpuLeft2 = x2;                       /* P2 next CPU burst remaining */
   int ioLeft1 = y1;        /* P1 next IO burst remaining, 0 if no more IO */
   int ioLeft2 = y2;        /* P2 next IO burst remaining, 0 if no more IO */
 
   for (i=0; (state1 != DONE) || (state2 != DONE); i++) {
                                /* running process completes its CPU burst */
      if ((state1 == RUNNING) && (cpuLeft1== 0)) {
         if (ioLeft1 == 0) {
            state1 = DONE;
            s1[i] = stateChars[state1];            /* terminate the string */
         }
         else
            state1 = WAITING;
      }  
      else if ((state2 == RUNNING) && (cpuLeft2 == 0) ) {
            if (ioLeft2 == 0) {
               state2 = DONE;
               s1[i] = stateChars[state2];            /* terminate the string */
         }
         else
            state2 = WAITING;
      }    
                                                     /* handle IO complete */
      if ((state1 == WAITING) && (ioLeft1 == 0)) {
         state1 = READY;
         cpuLeft1 = z1;
      }  
      if ((state2 == WAITING) && (ioLeft2 == 0)) {
         state2 = READY;
         cpuLeft2 = z2;
      }  
                                    /* if both ready, depends on algorithm */
      if ( (state1 == READY) && (state2 == READY)) {
         state1 = RUNNING;
      }  
                                     /* handle one ready and CPU available */
      else if ( (state1 == READY) && (state2 != RUNNING)) {
         state1 = RUNNING;
      }  
      else if ( (state2 == READY) && (state1 != RUNNING)) {
         state2 = RUNNING;
      }  
   /* insert chars in string, but avoid putting in extra string terminators */
      if (state1 != DONE)
         s1[i] = stateChars[state1];
      if (state2 != DONE)
         s2[i] = stateChars[state2];
                                                        /* decrement counts */
      if (state1 == RUNNING)
         cpuLeft1--;
      if (state1 == WAITING)
         ioLeft1--;
      if (state2 == RUNNING)
         cpuLeft2--;
      if (state2 == WAITING)
         ioLeft2--;
   }                                               /* end of main for loop */
}//end of fcfs

void sjf(char *s1, char *s2, int x1, int y1, int z1, 
                             int x2, int y2, int z2) {
   int i;                                   /* next string position (time) */
   int state1 = READY;                            /* start with both ready */
   int state2 = READY;
   int cpuLeft1 = x1;                       /* P1 next CPU burst remaining */
   int cpuLeft2 = x2;                       /* P2 next CPU burst remaining */
   int ioLeft1 = y1;        /* P1 next IO burst remaining, 0 if no more IO */
   int ioLeft2 = y2;        /* P2 next IO burst remaining, 0 if no more IO */
   for (i=0; (state1 != DONE) || (state2 != DONE); i++) {
                                /* running process completes its CPU burst */
      if ((state1 == RUNNING) && (cpuLeft1== 0)) {
         if (ioLeft1 == 0) {
            state1 = DONE;
            s1[i] = stateChars[state1];            /* terminate the string */
         }
         else
            state1 = WAITING;
      }  
      else if ((state2 == RUNNING) && (cpuLeft2 == 0) ) {
         if (ioLeft2 == 0) {
            state2 = DONE;
            s2[i] = stateChars[state2];            /* terminate the string */
         }
         else
            state2 = WAITING;
      }  
                                                    /* handle IO complete */
      if ((state1 == WAITING) && (ioLeft1 == 0)) {
         state1 = READY;
         cpuLeft1 = z1;
      } 
      if ((state2 == WAITING) && (ioLeft2 == 0)) {
         state2 = READY;
         cpuLeft2 = z2;
      } 
                                    /* if both ready, depends on algorithm */
      if ( (state1 == READY) && (state2 == READY)) {
         if(cpuLeft1 <= cpuLeft2){
            state1 = RUNNING;
         }
         else
            state2 = RUNNING;
      }  
                                     /* handle one ready and CPU available */
      else if ( (state1 == READY) && (state2 != RUNNING)) {
         state1 = RUNNING;
      }
      else if ( (state2 == READY) && (state1 != RUNNING)) {
         state2 = RUNNING;
      }
      /* insert chars in string, but avoid putting in extra string terminators */
      if (state1 != DONE)
         s1[i] = stateChars[state1];
      if (state2 != DONE)
         s2[i] = stateChars[state2];
      /* decrement counts */
      /* OK to decrement even if nothing running */
      if (state1 == RUNNING)
         cpuLeft1--;
      if (state1 == WAITING)
         ioLeft1--;
      if (state2 == RUNNING)
         cpuLeft2--;
      if (state2 == WAITING)
         ioLeft2--;
   }
}//End of SJF

void psjf(char *s1, char *s2, int x1, int y1, int z1,
                                            int x2, int y2, int z2) {
   int i;                                   /* next string position (time) */
   int state1 = READY;                            /* start with both ready */
   int state2 = READY;
   int cpuLeft1 = x1;                       /* P1 next CPU burst remaining */
   int cpuLeft2 = x2;                       /* P2 next CPU burst remaining */
   int ioLeft1 = y1;        /* P1 next IO burst remaining, 0 if no more IO */
   int ioLeft2 = y2;        /* P2 next IO burst remaining, 0 if no more IO */

   for (i=0; (state1 != DONE) || (state2 != DONE); i++) {
                                /* running process completes its CPU burst */
      if ((state1 == RUNNING) && (cpuLeft1== 0)) {
         if (ioLeft1 == 0) {
            state1 = DONE;
            s1[i] = stateChars[state1];            /* terminate the string */
         }
         else
            state1 = WAITING;
      }  
      else if ((state2 == RUNNING) && (cpuLeft2 == 0) ) {
         if (ioLeft2 == 0) {
            state2 = DONE;
            s2[i] = stateChars[state2];            /* terminate the string */
         }
         else
            state2 = WAITING;
      }  
                                     /* running process has quantum expire */
                                         /* handle IO complete */
      if ((state1 == WAITING) && (ioLeft1 == 0)) {
         cpuLeft1 = z1;
         if (cpuLeft1 < cpuLeft2) {
            state1 = RUNNING;
	    state2 = READY;
         }
	 else 
            state1 = READY;
      }  
      if ((state2 == WAITING) && (ioLeft2 == 0)) {
         cpuLeft2 = z2;
         if (cpuLeft2<cpuLeft1) {
	    state2=RUNNING;
	    state1=READY;
	 }
	 else 
            state2 = READY;
      }  
                            /* if both ready, depends on algorithm */
      if ((state1 == READY) && (state2 == READY)) {
         if(cpuLeft1 <= cpuLeft2){
            state1 = RUNNING;
         }
         else
            state2 = RUNNING;
      }  
                                     /* handle one ready and CPU available */
      else if ( (state1 == READY) && (state2 != RUNNING)) {
         state1 = RUNNING;
      }  
      else if ( (state2 == READY) && (state1 != RUNNING)) {
         state2 = RUNNING;
      } 
      
   /* insert chars in string, but avoid putting in extra string terminators */
      if (state1 != DONE)
         s1[i] = stateChars[state1];
      if (state2 != DONE)
         s2[i] = stateChars[state2];
                                                        /* decrement counts */
      if (state1 == RUNNING)
         cpuLeft1--;
      if (state1 == WAITING)
         ioLeft1--;
      if (state2 == RUNNING)
         cpuLeft2--;
      if (state2 == WAITING)
         ioLeft2--;
   }                                             
}//end of psjf

void rr(char *s1, char *s2, int quantum, int x1, int y1, int z1, 
                                         int x2, int y2, int z2) {
   int i;                                   /* next string position (time) */
   int state1 = READY;                            /* start with both ready */
   int state2 = READY;
   int cpuLeft1 = x1;                       /* P1 next CPU burst remaining */
   int cpuLeft2 = x2;                       /* P2 next CPU burst remaining */
   int ioLeft1 = y1;        /* P1 next IO burst remaining, 0 if no more IO */
   int ioLeft2 = y2;        /* P2 next IO burst remaining, 0 if no more IO */
   int qleft;                                         /* quantum remaining */

   for (i=0; (state1 != DONE) || (state2 != DONE); i++) {
                                /* running process completes its CPU burst */
      if ((state1 == RUNNING) && (cpuLeft1== 0)) {
         if (ioLeft1 == 0) {
            state1 = DONE;
            s1[i] = stateChars[state1];            /* terminate the string */
         }
         else
            state1 = WAITING;
      }  
      else if ((state2 == RUNNING) && (cpuLeft2 == 0) ) {
         if (ioLeft2 == 0) {
            state2 = DONE;
            s2[i] = stateChars[state2];            /* terminate the string */
         }
         else
            state2 = WAITING;      
      }  
                                     /* running process has quantum expire */
      if ((state1 == RUNNING) && (qleft == 0) ) {
         state1 = READY;
         if(state2 == READY){
            state2 = RUNNING;
            qleft =quantum;
         }
      }  
      if ((state2 == RUNNING) && (qleft == 0) ) {
         state2 = READY;
         if(state1 == READY){
            state1 = RUNNING;
            qleft =quantum;
         }
      }  
                                                     /* handle IO complete */
      if ((state1 == WAITING) && (ioLeft1 == 0)) {
          state1 = READY;
          cpuLeft1 = z1;
      }  
      if ((state2 == WAITING) && (ioLeft2 == 0)) {
          state2 = READY;
          cpuLeft2 = z2;         
      }  
                                    /* if both ready, depends on algorithm */
      if ( (state1 == READY) && (state2 == READY)) {
          state1 = RUNNING;
          qleft = quantum;
      }  
                                     /* handle one ready and CPU available */
      else if ( (state1 == READY) && (state2 != RUNNING)) {
         state1 = RUNNING;
         qleft = quantum;
      }  
      else if ( (state2 == READY) && (state1 != RUNNING)) {
         state2 = RUNNING;
         qleft = quantum;
      }  
   /* insert chars in string, but avoid putting in extra string terminators */
      if (state1 != DONE)
         s1[i] = stateChars[state1];
      if (state2 != DONE)
         s2[i] = stateChars[state2];
                                                        /* decrement counts */
      qleft--;                   /* OK to decrement even if nothing running */
      if (state1 == RUNNING)
         cpuLeft1--;
      if (state1 == WAITING)
         ioLeft1--;
      if (state2 == RUNNING)
         cpuLeft2--;
      if (state2 == WAITING)
         ioLeft2--;
   }                                             
}// End of void RR

void display(char *heading, char *s1, char *s2){
   int i,nr1,nr2,sl1,sl2,sl;
   float nR1,nR2;
   printf("\n");
   printf("%s",heading);

   printf("%s\n",s1);
   printf("%s\n",s2);
   sl1=strlen(s1);
   nr1=0;
   nR1=0;
   for(i=0;i<sl1;i++){
      if(s1[i] == 'r'){
         nr1++;
      }
      if(s1[i] == 'R'){
         nR1++;
      }
   }

   sl2=strlen(s2);
   nr2=0;
   nR2=0;
   for(i=0;i<sl2;i++){
      if(s2[i] == 'r'){
         nr2++;
      }
      if(s2[i] == 'R'){
         nR2++;
      }
   }

   if(sl1>sl2){
      sl=sl1;
   }
   else {
      sl=sl2;
   }
   printf("%d %d %.1f %.5f\n",nr1,nr2,(float)(nr1+nr2)/2,(nR1+nR2)/sl);
}


