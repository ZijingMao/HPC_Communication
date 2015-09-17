#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <malloc.h>
#include <pthread.h>

#define PATH "/usr/bin/gcc"

void *threadcompile(void *arg);

void *threadcompilereturn(void *arg);

int getcount(int *countp);

int clearcount();

int compile(const char *str1, const char *str2);

pid_t r_wait(int *stat_loc);
