package edu.kit.informatik.pcc.service.videoprocessing;

import edu.kit.informatik.pcc.service.data.Account;

import java.io.File;

/**
 * Created by Josh Romanowski on 18.01.2017.
 */
public class EditingContext {
    private File encVid;
    private File encKey;
    private File encMetadata;
    private File decVid;
    private File decMetadata;
    private File anonymizedVid;
    private File vidWithMeta;
    private Account account;

    public EditingContext(Account account) {
        this.account = account;

        // TODO: create files
    }

    public File getEncVid() {
        return encVid;
    }

    public File getEncKey() {
        return encKey;
    }

    public File getEncMetadata() {
        return encMetadata;
    }

    public File getDecVid() {
        return decVid;
    }

    public File getDecMetadata() {
        return decMetadata;
    }

    public File getAnonymizedVid() {
        return anonymizedVid;
    }

    public File getVidWithMeta() {
        return vidWithMeta;
    }

    public Account getAccount() {
        return account;
    }
}
