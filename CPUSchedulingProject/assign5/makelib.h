#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <malloc.h>
#include <pthread.h>

#define PATH "/usr/bin/gcc"

int compile(const char *str1, const char *str2);
