#include <stdio.h>
#include <stdlib.h> 
#include<string.h>
// For exit()

int main()
{
	FILE *fptr1, *fptr2;
	char filename[100], c;

	printf("Enter the filename to open for reading \n");
	scanf("%s", filename);

	// Open one file for reading
	fptr1 = fopen(filename, "r");
	if (fptr1 == NULL)
	{
		printf("Cannot open file %s \n", filename);
		exit(0);
	}

	printf("Enter the filename to open for writing \n");
	scanf("%s", filename);

	// Open another file for writing
	fptr2 = fopen(filename, "w");
	if (fptr2 == NULL)
	{
		printf("Cannot open file %s \n", filename);
		exit(0);
	}

	// Read contents from file
	c = fgetc(fptr1);
	while (c != EOF)
	{
		fputc(c, fptr2);
		c = fgetc(fptr1);


	}

	printf("\nContents copied to %s", filename);
    fclose(fptr1);
	fclose(fptr2);

	

    char a;
	fptr2 = freopen(filename,"r+",fptr2);     //checking for the vowels and replacing with space
        while ((a = getc(fptr2)) != EOF){
                if (a == 'A' || a == 'E' || a == 'I' || a == 'O' || a == 'U' || a == 'a' || a == 'e' || a == 'i' || a == 'o' || a == 'u'){
		        fseek(fptr2, -1, SEEK_CUR);
			putc(' ', fptr2);
			fseek(fptr2, 1, SEEK_CUR);
		}

		
       	}
		   fclose(fptr2);
		   printf("\n\n");
char d;
   fptr2 = freopen(filename,"r+",fptr2);   //printing the file
	d = fgetc(fptr2);
	while (d != EOF)
	{
		printf("%c",d);
		d = fgetc(fptr2);
	}
	
	return 0;
}