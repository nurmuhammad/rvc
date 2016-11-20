package rvc;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ExternalResourceHandler {
    String[] welcomeFiles = {"index.html"};
    String rootFolder;

    public ExternalResourceHandler(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    File getFile(String path2) throws Exception {

        URL url = new URL(path2);


        String inputs[] = new String[]{"/foo", "//foo", "foo/", "foo/bar", "foo/bar/../baz", "foo//bar"};

        for (final String input: inputs) {
            String normalized = FilenameUtils.normalize(input);
            System.out.println(normalized);
        }


        return null;
    }

    public static void main(String[] args) throws Exception {
        new ExternalResourceHandler("").getFile("");
    }

    public void handle(){

    }
}
