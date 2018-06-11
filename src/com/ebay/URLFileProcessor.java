package com.ebay;

import com.ebay.filereader.FileProcessor;

import java.nio.file.Path;
import java.util.Set;

public class URLFileProcessor {
    public static final String FILE_PATH = "../Ebay/inputData";

    public static void runUrlFileProcessor() {
        System.out.println( "Executing..." );

        FileProcessor fileProcessor = new FileProcessor();

        // first make sure any existing files are processed
        processExistingFiles( FILE_PATH, fileProcessor );

        // setup WatchService on folder -> repeat reading files from folder -> process

    }

    /**
     * Finds all files currently in the given folder and processes them
     *
     * @param fileProcessor
     */
    public static void processExistingFiles( String filePath, FileProcessor fileProcessor ) {
        Set<Path> files = fileProcessor.getFiles( filePath );
        fileProcessor.setCustomPoolSize( files.size() );
        fileProcessor.processFiles( files );
        System.out.println( "Finished processing all existing files." );
    }
}
