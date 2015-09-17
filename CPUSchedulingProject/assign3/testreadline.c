#include <stdio.h>
#include <errno.h>
#include <unistd.h>

void main(int argc, char *argv[]) {
   int bytesread;
   char mybuf[100];

   /*引用了STDIN_FILENO表示标准输入,同样,标准出入用STDOUT_FILENO,标准出错用STDERR_FILENO*/
   bytesread = readline(STDIN_FILENO, mybuf, sizeof(mybuf));

   fprintf(stderr, "%d\n", bytesread);
}
