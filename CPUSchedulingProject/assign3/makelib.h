#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <malloc.h>

int compile(const char *str1, const char *str2);

pid_t r_wait(int *stat_loc);
