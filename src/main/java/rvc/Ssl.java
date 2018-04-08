package rvc;

public class Ssl {

    String keystoreFile;
    String keystorePassword;
    String keyManagerPassword;
    String truststoreFile;
    String truststorePassword;
    boolean needsClientCert;

    public Ssl(String keystoreFile, String keystorePassword, String keyManagerPassword,
                String truststoreFile, String truststorePassword) {
        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.keyManagerPassword = keyManagerPassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    public Ssl(String keystoreFile, String keystorePassword, String keyManagerPassword,
               String truststoreFile, String truststorePassword, boolean needsClientCert) {
        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.keyManagerPassword = keyManagerPassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
        this.needsClientCert = needsClientCert;
    }

    public String keystoreFile() {
        return keystoreFile;
    }

    public String keystorePassword() {
        return keystorePassword;
    }

    public String keyManagerPassword() {
        return keyManagerPassword;
    }

    public String trustStoreFile() {
        return truststoreFile;
    }

    public String trustStorePassword() {
        return truststorePassword;
    }

    public boolean needsClientCert(){
        return needsClientCert;
    }
}
