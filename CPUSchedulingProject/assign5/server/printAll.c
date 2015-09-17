#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include "printA.h"
#include "printB.h"

int main(int argc, char *argv[]) {
   printA();
   printB();

   return 0;
}
