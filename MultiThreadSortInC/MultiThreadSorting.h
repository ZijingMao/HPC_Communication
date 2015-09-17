#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <limits.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <pthread.h>

#define THREADNUM	3

void *SortingThread(void *arg);

void *MergeTwoItems(void *arg);
