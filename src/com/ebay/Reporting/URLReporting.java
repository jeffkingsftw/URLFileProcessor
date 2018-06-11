package com.ebay.Reporting;

public class URLReporting {
    private int urlSucceeds = 0;
    private int urlFailures = 0;

    private String filename;

    public URLReporting( String filename ) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public int getUrlSucceeds() {
        return this.urlSucceeds;
    }

    public void incrementSucceeds() {
        this.urlSucceeds++;
    }

    public int getUrlFailures() {
        return this.urlFailures;
    }

    public void incrementFailures() {
        this.urlFailures++;
    }

    public int getTotal() {
        return getUrlSucceeds() + getUrlFailures();
    }

    public String toString() {
        return "File: " + this.getFilename()
                + " called " + this.getUrlSucceeds() + " URLs successfully, and "
                + this.getUrlFailures() + " URLs unsuccessfully"
                + " out of " + this.getTotal() + " total calls.";
    }
}
