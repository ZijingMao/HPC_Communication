#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <pthread.h>
#define PATH "/usr/bin/gcc"

static int count = 0; 
static pthread_mutex_t  countlock = PTHREAD_MUTEX_INITIALIZER;
static int i;

int compile(char *s1, char *s2){
   char *filename;
   pid_t childpid;
   int status;

   printf("%s %s\n", s1, s2);
   if((filename = malloc(strlen(s2) + 3)) == NULL)
      return 0;

   strcpy(filename, s2);
   strcat(filename, ".c");

   childpid = fork();
   if(childpid == -1){
      printf("Fail to fork\n");
      return 0;
   }
	
   if(childpid == 0){			/*Child code*/
      execl(s1, "gcc", "-c", filename, NULL);
      printf("child fail to use execl to compile\n");
      return 0;
   }

   free(filename);

   while(wait(&status) > 0){            /* wait for all children */
      if(WIFEXITED(status) && !WEXITSTATUS(status)){
         return 1;
   }
      else
         return 0;
   }
}


void *threadcompile(void *arg){
   char *filename;
   int error;	
   filename = (char *)(arg);
	
   if(compile(PATH, filename) == 1){
      increment();   
   }
   return NULL;
}

int increment(void) {                  /* increment the counter */
   int error;   
   if (error = pthread_mutex_lock(&countlock))
      return error; 
   count++;
   return pthread_mutex_unlock(&countlock);
}

int clearcount(void){			/*clear counter*/
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

int getvalue(int *valuep){		/*retrieve the counter*/
   int error; 
   if (error = pthread_mutex_lock(&countlock)) 
      return error; 
   *valuep = i;  
   return pthread_mutex_unlock(&countlock); 
}

