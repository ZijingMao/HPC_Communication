#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <pthread.h>
#include "makelib.h"

static pthread_mutex_t  ilock = PTHREAD_MUTEX_INITIALIZER;

int main(int argc, char *argv[]){ 
   int i;
   int childpid;
   char gccpath[] = "/usr/bin/gcc";
   pthread_t *tid;
   int error;
   int get_count;
   int *return_value;
   int final_i;

   if(argc < 2){
      printf("Error:The number of parameter is less than 2\n");
      return 0;
   }  
	
   printf("makethread written by Qian Huang\n");
   char **run; 
   if((run = malloc(sizeof(char *)*(argc + 3))) == NULL)
      return 0;
   *run = "gcc";

   for(i = 1; i < argc; i++){     
      char *filename = argv[i];
      char *filename_o;
		
      if((filename_o = malloc(strlen(filename) + 3)) == NULL)
         return 0;

      strcpy(filename_o, filename);
      filename_o = strcat(filename_o, ".o");
      *(run+i) = filename_o;  	  
   }

   if((tid = (pthread_t *)calloc(argc - 1, sizeof(pthread_t))) == NULL){
      printf("Failed to allocate space for thread.");
      return 0;
   }

   for(i = 0; i < argc-1; i++){
      
      if (error = pthread_create(tid + i, NULL, threadcompilereturn, argv[i+1])){
         fprintf(stderr, "Failed to create thread: %s\n", strerror(error));
         return 0;
      }	
/*      if (error = pthread_mutex_lock(&ilock)) 
         return error; 
      getvalue(&final_i);
      printf("%d\n", final_i);
      if (error = pthread_mutex_unlock(&ilock)) 
         return error;     
*/   }

   for (i = 0; i < argc-1; i++){
      if(error = pthread_join(tid[i], (void **)&return_value)){
         
         fprintf(stderr, "Failed to join thread: %s\n", strerror(error));
         return 0;
      }
      if (error = pthread_mutex_lock(&ilock)) 
         return error; 
      getvalue(&final_i);
      printf("%d\n", final_i);
      if (error = pthread_mutex_unlock(&ilock)) 
         return error;  

/*      if(*return_value == 1){
         fprintf(stderr, "return value:	%d\n", *return_value);
         return 0;
      }
*/
   }

      *(run+argc) = "-o";
      *(run+argc+1) = argv[1];
      *(run+argc+2) = NULL;

      childpid = fork();
      if(childpid == -1){
         printf("Fail to fork\n");
         return 0;
      }
      
      if(childpid == 0){			
         execv(gccpath, run);
         printf("child fail to use execl to compile the object file\n");
         return 0;
      }

   while(wait(NULL) > 0);
   printf("object files was successfully change to the excutable\n");
 
   for(i=1; i<argc;i++){
      free(*(run+i));
   }
   free(run);
   free(tid);
   return 0;
}
