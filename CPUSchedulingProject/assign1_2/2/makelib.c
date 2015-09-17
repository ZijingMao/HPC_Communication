#include "makelib.h"

int compile(char *str1, char *str2){
	printf("%c %c\n", str1, str2);

	char *str_name = str2 + ".c";

	char *args[] = {"/bin/ls", "-r", "-t", "-l", (char *) 0 };

	int child = fork();

	if(child < 0){
		perror("Fail to fork\n");
		return 0;
	}
	
	if(child == 0){
		execvp(str1, args);
	}

	if(child != r_wait(NULL)){
		perror("Parent failed to wait\n");
		return 0;
	}

	return 1;
}