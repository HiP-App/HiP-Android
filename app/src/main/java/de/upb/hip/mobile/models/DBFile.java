package de.upb.hip.mobile.models;

/**
 * A base class for all files that are loaded from the DB
 */
public abstract class DBFile {

    //The ID of the DB document the file is attached to is required for retrieving it from the DB.
    private final int mDocumentId;

    public DBFile(int mDocumentId) {
        this.mDocumentId = mDocumentId;
    }

    protected int getDocumentId() {
        return mDocumentId;
    }
}
