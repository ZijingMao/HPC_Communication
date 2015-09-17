#include "makelib.h"

int main(int argc, char *argv[]){
	if(argc < 2){
		fprintf (stderr, "Usage: %s command filename1 [filename2] ...\n", argv[0]);
		return 0;
	}

	int i;
	int return_value;
	int child;
	char path[] = "/usr/bin/gcc";	
	char **snew;

	if((snew = (char **)malloc(sizeof(char *)*(argc + 3))) == NULL){
		perror("Unable to allocate exec arguments");
		return 0;
	}
	
	*snew = path;
	//*(snew + 1) = "testreadline.o";
	//*(snew + 2) = "readline.o";
	for(i = 1; i < argc; i++){
		const char *str_name = argv[i];
		char *name_o;
		
		if((name_o = malloc(strlen(str_name) + 1)) == NULL)
			return 0;

		strcpy(name_o, str_name);
		strcat(name_o, ".o");

		*(snew + i) = name_o;
	}
	*(snew + argc) = "-o";
	*(snew + argc + 1) = argv[1];
	*(snew + argc + 2) = 0;

	//char *snew[] = {"gcc", "testreadline.o", "readline.o" ,"-o", "testreadline", 0};
	
	fprintf(stderr, "makeserial written by Zijing Mao\n");

	for(i = 1; i < argc; i++){
		return_value = compile(path, argv[i]);

		fprintf(stderr, "%s\n", argv[i]);		
		fprintf(stderr, "%s %d\n", argv[i], return_value);

		if(return_value == 0){
			perror("Compile failed to compile an object");
			return 0;
		}
	}

	child = fork();
	
	if(child < 0){
		perror("Fail to fork\n");
		return 0;
	}

	if(child == 0){			/*Child code*/
		execvp(path, snew);
		perror("Child failed to exec the command");
		return 0;
	}

	free(*snew);
	free(snew);

	while(r_wait(NULL) > 0);	/* wait for all children */

	return 1;
}


