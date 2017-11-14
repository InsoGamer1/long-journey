/**
 * @file	finder.c
 * @author	Mujtaba Raheman
 * @date	12 - May - 2015
 * @breif	Program to recursively search for a file and from each instance of the file search for a keyword form the file and generate a log report.
 * @Usage	<application dir_name> <directory path> <file name(REGEXP)> <keyword>
 * @bug		Nil
 */

#include <unistd.h>
#include <sys/types.h>
#include <dirent.h>
#include <stdio.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<string.h>
#include <stdlib.h>


#define DEF_FILENAME "SOURCES.txt"                           // default values: replace 'em if u want
#define DEF_KEYWORD "tests"                                  // default values: replace 'em if u want


void print_rec(const char *dir_name, int level , char *filename , char *keyword ,FILE * res_file , char *right , unsigned short option)
{
    DIR *dir;
    char *lineptr = malloc( sizeof(char)*1024);
    struct dirent *entry;
    struct stat st_buf;
    char *cmd = NULL ;

    if (!(dir = opendir(dir_name)))
        return;
    if (!(entry = readdir(dir)))
        return;

    do {
            char path[1024];
            path[snprintf(path, sizeof(path)-1, "%s/%s", dir_name, entry->d_name)] = 0;
            if ( !strcmp( entry->d_name , ".") || !strcmp( entry->d_name , ".."))
                continue;
            stat(path, &st_buf );
            if ( S_ISDIR(st_buf.st_mode) == 0 && S_ISREG(st_buf.st_mode) == 1 ){
                switch(option){
                case 0:
                    if ( !strcmp(entry->d_name , filename) ){
                            printf("path::%s\n", path);
		                    fprintf( res_file ,"path:\t%s\n\n",path);
		                    FILE *fp ;
		                    fp = fopen( path , "r");
		                    while( fgets(lineptr , 1024 , fp) != NULL ){
	                            	cmd = strstr(lineptr , keyword);
        		            	if ( cmd != NULL ){
                                    printf("found in %s\n", path);
                                    fprintf(res_file ,"\t%s\n",cmd);
		                       	}
        	        	    }
	        	            fclose(fp);
		                }
				break;
                case 1:
				if ( strstr(entry->d_name , filename) ){
 		                    printf("path::%s\n", path);
		                    fprintf( res_file ,"path:\t%s\n\n",path);
		                    FILE *fp ;
		                    fp = fopen( path , "r");
		                    while( fgets(lineptr , 1024 , fp) != NULL ){
	                            	cmd = strstr(lineptr , keyword);
        		            	if ( cmd != NULL ){
                                    printf("found in %s\n", path);
                                    fprintf(res_file ,"\t%s\n",cmd);
		                       	}
        	        	    }
	        	            fclose(fp);
		                }
				break;
                case 2:
				if ( strstr(entry->d_name , filename) && strstr(entry->d_name , right) ){
 		                    printf("path::%s\n", path);
		                    fprintf( res_file ,"path:\t%s\n\n",path);
		                    FILE *fp ;
		                    fp = fopen( path , "r");
		                    while( fgets(lineptr , 1024 , fp) != NULL ){
	                            	cmd = strstr(lineptr , keyword);
        		            	if ( cmd != NULL ){
                                    printf("found in %s\n", path);
                                    fprintf(res_file ,"\t%s\n",cmd);
		                       	}
        	        	    }
	        	            fclose(fp);
		                }
				break;
                case 3:
				printf("path::%s\n", path);
                fprintf( res_file ,"path:\t%s\n\n",path);
		        FILE *fp ;
		        fp = fopen( path , "r");
		        while( fgets(lineptr , 1024 , fp) != NULL ){
                    cmd = strstr(lineptr , keyword);
                    if ( cmd != NULL ){
                        printf("found in %s\n", path);
                        fprintf(res_file ,"\t%s\n",cmd);
                    }
                }
                fclose(fp);
				break;
                defualt:
                    printf("Invalid syntax\n");
				break;
                }
            }
            else
                print_rec(path, level + 1 , filename , keyword , res_file , right , option);
    } while (entry = readdir(dir));
    closedir(dir);
}




/* option=0 : exact match for filename
   option=1 : filename with '*' either at beginning or at the end
   option=2 : filename with '*' in midst of the word
   option=3 : for all file '-all'
*/



int main ( int argc, char **argv )
{
    FILE *res_file;
    int i;
    unsigned short option ;
    char *res = malloc( 128);
    char *right = NULL ;
    int len =0;
    if ( !(argc>1 && argc <5)){
        printf("\tUsage:\t[path] [filename] [keyword]\n");
        printf("filename can be REGEXP , for all file, give -all\n");
        printf("default:\tfilename = SOURCES.txt and keyword = tests\n");
        return 0;
    }
    if ( (res_file = fopen( "./result.txt" , "w+"))  == NULL){
            printf("file %s\n",res);
        perror("Output Log");
        return 0;
    }
    if ( argv[2] != NULL){		                                 //filename
        len = strlen( argv[2]);
        if ( !strcmp(argv[2] , "-all") ){
            option  = 3 ;				                                          // check all
        }
        else if ( strchr( argv[2] , '*')){
            if ( argv[2][0] == '*'){
                option = 1;
                i= 1;
                while ( argv[2][i-1]){
                    argv[2][i-1] = argv[2][i];
                    i++;
                }
            }
            else if ( argv[2][len-1] == '*'){
                argv[2][len-1]= '\0' ;
                option = 1;
            }
            else {
                right=strchr( argv[2] , '*')+1;
                *(strchr( argv[2] , '*')) = '\0' ;
                option = 2;		                                              /////////////
            }
        }
        else
            option = 0;		                                              // exact cmp
    }
    if ( argc == 2 ){
        print_rec(argv [ 1 ], 1 , DEF_FILENAME ,DEF_KEYWORD  , res_file , right , option);
    }
    else if ( argc == 3 ){
        print_rec(argv [ 1 ], 1 , argv[2] , DEF_KEYWORD  , res_file ,right , option);
    }

    else
        print_rec(argv [ 1 ], 1 , argv[2] , argv[3]  , res_file , right , option);
    fclose(res_file);
    printf("\nResult log file is saved in  result.txt \n" );
    return 0;
}
