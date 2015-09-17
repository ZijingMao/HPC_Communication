#include "makelib.h"

int compile(const char *str1, const char *str2){
	fprintf(stderr, "%s %s\n", str1, str2);
	
	pid_t child;
	pid_t childpid;
	int status;
	char *str_name;

	if((str_name = malloc(strlen(str2) + 1)) == NULL)
		return 0;

	strcpy(str_name, str2);
	strcat(str_name, ".c");

	child = fork();

	if(child < 0){
		perror("Fail to fork\n");
		return 0;
	}
	
	if(child == 0){			/*Child code*/
		execl(str1, "gcc", "-c", str_name, NULL);
		perror("Child failed to execl the command");
		return 0;
	}
	
	free(str_name);

//	if(child != r_wait(NULL)){	/*Parent code*/
//		perror("Parent failed to wait\n");
//		return 0;
//	}

	childpid = r_wait(&status);	/* wait for child to complete and return 1 */	
	if(childpid == -1)
		perror("Parent failed to wait");
	else if(WIFEXITED(status) && !WEXITSTATUS(status)){
		fprintf(stderr,"The child process %d exit normally.\n",childpid);
		return 1;
	}
	else{
		fprintf(stderr, "Child %ld terminated with return status %d\n", (long)childpid, WEXITSTATUS(status));
		return 0;
	}
}

/*A function that restarts wait if interrupted by a signal*/
pid_t r_wait(int *stat_loc){
 	int retval;
	while (((retval = wait(stat_loc)) == -1) && (errno == EINTR)) ;
	return retval;
}
