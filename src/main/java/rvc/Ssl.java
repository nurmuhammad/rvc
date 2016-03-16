package rvc;

public class Ssl {

    protected String keystoreFile;
    protected String keystorePassword;
    protected String truststoreFile;
    protected String truststorePassword;

    public Ssl(String keystoreFile, String keystorePassword,
                String truststoreFile, String truststorePassword) {
        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    public String keystoreFile() {
        return keystoreFile;
    }

    public String keystorePassword() {
        return keystorePassword;
    }

    public String trustStoreFile() {
        return truststoreFile;
    }

    public String trustStorePassword() {
        return truststorePassword;
    }
}
