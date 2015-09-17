#include "makelib.h"

int main(int argc, char *argv[]){
	if(argc != 3){
		printf("Numeric number error.\n");
		return 0;
	}

	int return_value;
	char *path = "C:\Users\Zijing\Documents\Visual Studio 2010\Projects\Assign4\Assign4";

	printf("makeserial written by Zijing Mao\n%c %c", argv[1], argv[2]);

	return_value = compile(path, argv[1]);

	printf("%c %d", argv[1], return_value);

	return 1;
}
