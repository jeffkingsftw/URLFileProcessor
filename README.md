# URLFileProcessor

A command line program to process files in a folder and run Http GET calls on each line in the file.

The program uses a relative path to the folder which should be placed in "../Ebay/inputData".  Each file will be processed in parallel, and a custom thread pool will be created to process each GET url.  The thread pool count is determined by the number of files being processed (number of files * 8).  In testing, the fastest average time with the minimum amount of threads per file seemed to be 8 threads, hence the multiple of 8.

The program requires the mockhttp server running that was provided in the instructions.
