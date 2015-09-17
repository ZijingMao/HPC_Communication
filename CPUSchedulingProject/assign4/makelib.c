#include "makelib.h"

static int count = 0;

static int i;

static pthread_mutex_t countlock = PTHREAD_MUTEX_INITIALIZER;

void *threadcompilereturn(void *arg){
	char *filename;
	
	int error;
	
	i = 1;

	filename = (char *)(arg);
	
	if(compile(PATH, filename) == 1){
		perror("Compiled");

		if(error = pthread_mutex_lock(&countlock)){
			perror("Failed to lock");
			return;
		}
		i = 0;
		pthread_mutex_unlock(&countlock);
		return &i;		
	}
	return &i;
}

void *threadcompile(void *arg){
	char *filename;
	int error;
	
	filename = (char *)(arg);
	
	if(compile(PATH, filename) == 1){
		perror("Compiled");

		if(error = pthread_mutex_lock(&countlock)){
			perror("Failed to lock");
			return;
		}
		count++;
		pthread_mutex_unlock(&countlock);
	}
	
	return NULL;
}

int compile(const char *str1, const char *str2){		
	pid_t child;
	pid_t childpid;
	int status;
	char *str_name;

	if((str_name = malloc(strlen(str2) + 3)) == NULL)
		return 0;

	strcpy(str_name, str2);
	strcat(str_name, ".c");

	fprintf(stderr, "%s %s\n", str1, str2);
	
	child = fork();

	if(child < 0){
		perror("Fail to fork");
		return 0;
	}
	
	if(child == 0){			/* Child code */
		execl(str1, "gcc", "-c", str_name, NULL);
		
		perror("Child failed to execl the command");
		return 0;
	}
	
	free(str_name);		/* free the memory allocated before */

	childpid = r_wait(&status);	/* wait for child to complete and return 1 */	
	if(childpid == -1)
		perror("Parent failed to wait");
	else if(WIFEXITED(status) && !WEXITSTATUS(status)){
		fprintf(stderr,"The child process %d exit normally.\n", childpid);
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

int clearcount(){			/*clear counter*/
	int error; 
   	if (error = pthread_mutex_lock(&countlock)) 
     		return error; 
   	count = 0;  
   	return pthread_mutex_unlock(&countlock); 
}

int getcount(int *countp){		/*retrieve the counter*/
	int error; 
   	if (error = pthread_mutex_lock(&countlock)) 
     		return error; 
   	*countp = count;  
   	return pthread_mutex_unlock(&countlock); 
}
