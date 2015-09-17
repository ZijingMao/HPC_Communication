#include "makelib.h"

int main(int argc, char *argv[]){
	if(argc < 2){
		fprintf (stderr, "Usage: %s command filename1 [filename2] ...\n", argv[0]);
		return 0;
	}

	int i;
	int final_count;
	int error;
	int child;
	char path[] = "/usr/bin/gcc";	
	char **snew;
	pthread_t *tid;
	
	if((snew = (char **)malloc(sizeof(char *)*(argc + 3))) == NULL){
		perror("Unable to allocate exec arguments");
		return 0;
	}
	
	*snew = path;
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

	fprintf(stderr, "makethread written by Zijing Mao\n");
	
	if((tid = (pthread_t *)calloc(argc - 1, sizeof(pthread_t))) == NULL){
		perror("Failed to allocate.");
		return 0;
	}

	for(i = 0; i < argc-1; i++){
		if (error = pthread_create(tid + i, NULL, threadcompile, argv[i+1])){
			fprintf(stderr, "Failed to create thread: %s\n", strerror(error));
			return 0;
		}
		fprintf(stderr, "%s\n", argv[i]);		
	}

	for (i = 0; i < argc-1; i++){
		if(error = pthread_join(tid[i], NULL)){
			fprintf(stderr, "Failed to create thread: %s\n", strerror(error));
			return 0;
		}
	}

	getcount(&final_count);

	fprintf(stderr, "Count is 	%d\n", final_count);

	child = fork();

	if(child < 0){
		perror("Fail to fork\n");
		return 0;
	}

	if(child == 0){			/* Child code */
		execvp(path, snew);
		perror("Child failed to exec the command");
		return 0;
	}

	for(i = 1; i < argc; i++){	/* free all the allocated memory */
		free(*(snew + i));
	}
	free(snew);

	while(r_wait(NULL) > 0);	/* wait for all children */

	free(tid);
	return 1;
}


