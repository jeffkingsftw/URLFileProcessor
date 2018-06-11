package com.ebay.urlprocessor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Optional;

public class URLProcessor {

    /**
     * Converts the given string into a URL object
     *
     * @param url - String representation of the URL
     *
     * @return - URL Object
     *
     * @throws MalformedURLException - If the given URL is not in the correct format
     */
    public static Optional<URL> getURL( String url ) throws MalformedURLException {
        if( url == null ) {
            return Optional.empty();
        }

        return Optional.of( new URL( url.trim() ) );
    }

    /**
     * Creates an Http GET request and calls the given URL
     * The result is checked for success (response code 200),
     * or failure (default)
     *
     * @param url - URL to be called
     *
     * @return - True if the call succeeded, false if the call failed
     *
     * @throws IOException - If the connection threw an exception
     */
    public static boolean processURLWithGet( Optional<URL> url ) throws IOException {
        boolean result = false;
        if( url.isPresent() ) {
            HttpURLConnection httpConnnection = (HttpURLConnection)url.get().openConnection();
            httpConnnection.setRequestMethod( "GET" );
            httpConnnection.connect();

            int responseCode = httpConnnection.getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK : result = true; break;
                default: result = false;
            }
        }

        return result;
    }

}
