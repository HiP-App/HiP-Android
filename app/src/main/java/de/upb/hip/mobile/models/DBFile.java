package de.upb.hip.mobile.models;

import java.io.Serializable;

/**
 * A base class for all files that are loaded from the DB
 */
public abstract class DBFile implements Serializable {

    //The ID of the DB document the file is attached to is required for retrieving it from the DB.
    private final int mDocumentId;
    private final String mFilename;

    public DBFile(int mDocumentId, String filename) {
        this.mDocumentId = mDocumentId;
        this.mFilename = filename;
    }

    public int getDocumentId() {
        return mDocumentId;
    }

    public String getFilename() {
        return mFilename;
    }
}
