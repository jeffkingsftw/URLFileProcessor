package com.ebay.filereader;

import com.ebay.Reporting.URLReporting;
import com.ebay.urlprocessor.URLProcessor;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class FileProcessor {

    private ForkJoinPool pool = null;

    /**
     * Creates a new ForkJoinPool object with a custom thread amount
     * The thread amount is determined by the number of files to be processed
     * Int testing, the number of threads that provided the fastest time
     * with the minimum amount of threads used was 8 threads per 1 file. So
     * this method sets the number of threads to be 8 * the number of files
     * The default amount of threads is 4 (assuming a 4 core processor)
     *
     * @param numberOfFiles - Amount of files being processed
     */
    public void setCustomPoolSize(int numberOfFiles ) {
        int threads = numberOfFiles > 0 ? numberOfFiles * 8 : 4;

        pool = new ForkJoinPool( threads );
    }

    /**
     * Finds all files in the given file path and returns them
     * in a Set of Path objects
     *
     * @param filePath - File path to search
     *
     * @return - A set of file paths
     */
    public Set<Path> getFiles( String filePath ) {
        Path path = Paths.get( filePath );
        try {
            return Files.list( path ).collect( Collectors.toSet() );
        } catch( IOException ioe ) {
            return new HashSet<Path>();
        }
    }

    /**
     * Processes each file in the given set and reports file statistics
     *
     * @param files - Files to be processed
     */
    public void processFiles( Set<Path> files ) {
        files.parallelStream()
                .forEach( filePath -> {
                    URLReporting fileReporting = new URLReporting( filePath.getFileName().toString() );

                    final long startTime = System.currentTimeMillis();

                    processLinesInFile( filePath, fileReporting );

                    final long elapsedTime = System.currentTimeMillis() - startTime;

                    System.out.println( fileReporting.toString() );
                    System.out.println( "Done processing " + fileReporting.getFilename() + ".  Process took " + elapsedTime + " milliseconds" );
                });
    }

    /**
     * Processes each line in the given file
     * The lines are processed using a custom thread pool and parallel streams
     * If the method setCustomPoolSize() is not called prior to calling this method,
     * the default amount of threads will be used
     *
     * @param filePath - Path of the file
     * @param fileReporting - Object to keep track of statistics
     */
    public void processLinesInFile(Path filePath, URLReporting fileReporting ) {
        if( pool == null ) {
            setCustomPoolSize( 0 );
        }

        try {
            pool.submit(
                    () -> getFileStream(filePath)
                            .parallel()
                            .forEach(line -> {
                                processUrl(line, fileReporting );
                            })
                ).get();
        } catch ( ExecutionException | InterruptedException e ) {
            System.out.println( "Error processing lines in file: " + filePath.getFileName() );
        }
    }

    /**
     * Reads the given .gz file and returns a Stream object for the file
     *
     * @param path - Path of the file
     *
     * @return - A Stream object of the file's contents
     */
    public Stream<String> getFileStream( Path path ) {
        InputStream fileIs = null;
        BufferedInputStream bufferedIs = null;
        GZIPInputStream gzipIs = null;

        try {
            fileIs = Files.newInputStream( path );
            // Even though GZIPInputStream has a buffer it reads individual bytes
            // when processing the header, better add a buffer in-between
            bufferedIs = new BufferedInputStream( fileIs );
            gzipIs = new GZIPInputStream( bufferedIs );
        } catch( IOException e ) {
            closeSafely( gzipIs );
            closeSafely( bufferedIs );
            closeSafely( fileIs );
            throw new UncheckedIOException( e );
        }

        BufferedReader reader = new BufferedReader( new InputStreamReader( gzipIs ) );
        return reader.lines().onClose( () -> closeSafely( reader ) );
    }

    /**
     * Closes the given Closable object
     *
     * @param closeable - Object to safely close
     */
    private static void closeSafely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch( IOException e ) {
                // Ignore
            }
        }
    }

    /**
     * Processes the given URL with an Http GET call
     * The call is checked for success or failure, and reports statistics
     * every 10000 attempts
     *
     * @param urlPath - URL to call
     * @param fileReporting - Object for reporting statistics
     */
    public void processUrl( String urlPath, URLReporting fileReporting ) {
        try {
            Optional<URL> url = URLProcessor.getURL( urlPath );
            boolean wasSuccessful = URLProcessor.processURLWithGet( url );
            if( wasSuccessful ) {
                fileReporting.incrementSucceeds();
            } else {
                fileReporting.incrementFailures();
            }
        } catch( IOException e ) {
            fileReporting.incrementFailures();
        }

        if( fileReporting.getTotal() != 0 && fileReporting.getTotal() % 10000 == 0 ) {
            System.out.println( fileReporting.toString() );
        }
    }
}
