#include "MultiThreadSorting.h"

struct integerSort{	/* Used to pass a number to thread */
	int *num;	/* Store numbers here */
	int amount;	/* Store the amount of numbers here */
};

struct mergeParameters{	/* Used to pass parameters to merging thread */		
   int firstHalf; 	/* Store the length of first array here */
   int secondHalf;	/* Store the length of second array here */
   int *intArray;	/* Store the whole array here */
   int *intFirstHalf;	/* Store the first half array here */
   int *intSecondHalf;	/* Store the second half array here */
};

void *SortingThread(void *arg){
	struct integerSort *integerArray;
	integerArray = (struct integerSort *)(arg);

	int *num = (*integerArray).num;
	int amount = (*integerArray).amount;

	/* Using bubble sort algorithm */
	int i = 0;
	int j = 0;
	int tmp;

	for(i = 0; i < amount-1; i++){
		for(j = i+1; j < amount; j++){
			if(num[i] > num[j]){				
				tmp = num[j];
				num[j] = num[i];
				num[i] = tmp;
			}
		}
	}

	return num;
}

void *MergeTwoItems(void *arg){
	/* Initialize parameters */
	struct mergeParameters *mergePara;
	mergePara = (struct mergeParameters *)(arg);	

	int firstHalf = (*mergePara).firstHalf;
	int secondHalf = (*mergePara).secondHalf;
	int *intArray = (*mergePara).intArray;
	int *intFirstHalf = (*mergePara).intFirstHalf;
	int *intSecondHalf = (*mergePara).intSecondHalf;

	int l = firstHalf + secondHalf - 1;
	int i = firstHalf - 1;
	int j = secondHalf - 1;

	/* Start merging two arrays */
	while(i >= 0 && j >= 0){
		if(intFirstHalf[i] > intSecondHalf[j]){
			intArray[l--] = intFirstHalf[i--];
		}
		else{
			intArray[l--] = intSecondHalf[j--];
		}
	}

	while(i >= 0){
		intArray[l--] = intFirstHalf[i--];
	}
	
	while(j >= 0){
		intArray[l--] = intSecondHalf[j--];
	}

	return intArray;
}

int main(int argc, char *argv[]) {
	if(argc < 2){
		fprintf (stderr, "Usage: num [num] ...\n");
		return 0;
	}

	pthread_t *tid;
	int argsLength = argc - 1;
	int firstHalf = argsLength/2;
	int secondHalf = argsLength - firstHalf;
	int i = 0;
	int error;

	int *integerArray;
	int *intFirstHalf;
	int *intSecondHalf;
	if((integerArray = (int *)malloc(sizeof(int)*argsLength)) == NULL
		|| (intFirstHalf = (int *)malloc(sizeof(int)*firstHalf)) == NULL
		|| (intSecondHalf = (int *)malloc(sizeof(int)*secondHalf)) == NULL){
		perror("Unable to allocate array to be sorted");
		return 0;
	}
	
	/* Convert string to number and store them in array, 
	 separate array into two parts */
	for(i = 0; i < argsLength; i++){
		integerArray[i] = atoi(argv[i+1]);
		//fprintf(stderr, "%d\n", integerArray[i]);
		if(i < firstHalf){
			intFirstHalf[i] = integerArray[i];
		}
		else{
			intSecondHalf[i-firstHalf] = integerArray[i];
		}
	}

	/* Allocate two thread id */
	if((tid = (pthread_t *)calloc(THREADNUM, sizeof(pthread_t))) == NULL){
		perror("Failed to allocate.");
		return 0;
	}

	/* Init parameters */
	struct integerSort intSortFirst, intSortSecond;
	intSortFirst.num = intFirstHalf;
	intSortFirst.amount = firstHalf;
	intSortSecond.num = intSecondHalf;
	intSortSecond.amount = secondHalf;

	/* Create threads */
	if ((error = pthread_create(tid + 0, NULL, SortingThread, &(intSortFirst))) 
	|| (error = pthread_create(tid + 1, NULL, SortingThread, &(intSortSecond)))){
		fprintf(stderr, "Failed to create thread: %d\n", strerror(error));
			return 0;
	}

	if((error = pthread_join(tid[0], (void **)&intFirstHalf)) 
	|| (error = pthread_join(tid[1], (void **)&intSecondHalf))){
		perror("Failed to join thread.\n");
		return 0;
	}

	/* Test */
	/*	
	for(i = 0; i < firstHalf; i++){
		fprintf(stderr, "%d\n", intFirstHalf[i]);
	}
	*/

	//MergeTwoItems(integerArray, intFirstHalf, intSecondHalf, 
	//		firstHalf, secondHalf);

	/* Init parameters for merging thread */
	struct mergeParameters mergePara;
	mergePara.intArray = integerArray;
	mergePara.intFirstHalf = intFirstHalf;
	mergePara.intSecondHalf = intSecondHalf;
	mergePara.firstHalf = firstHalf;
	mergePara.secondHalf = secondHalf;

	if ((error = pthread_create(tid + 2, NULL, MergeTwoItems, &(mergePara)))){
		fprintf(stderr, "Failed to create thread: %d\n", strerror(error));
			return 0;
	}

	if((error = pthread_join(tid[2], (void **)&integerArray))){
		perror("Failed to join thread.\n");
		return 0;
	}
	
	fprintf(stderr, "After sorting:\n");
	for(i = 0; i < argsLength; i++){
		fprintf(stderr, "%d ", integerArray[i]);
	}
	fprintf(stderr, "\n");

	free(integerArray);
	free(intFirstHalf);
	free(intSecondHalf);
	free(tid);
	return 1;
}








