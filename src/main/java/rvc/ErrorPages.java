package rvc;

import java.util.ArrayList;
import java.util.List;

public class ErrorPages {

    List<ErrorPage> pages = new ArrayList<>();

    void add(String page, int code, String domain) {
        pages.add(new ErrorPage(page, code, domain));
    }

    ErrorPage findMatch(int code, String serverName) {
        for (ErrorPage errorPage : pages) {
            if (errorPage.code == code && Route.matchDomain(errorPage.domain, serverName)) {
                return errorPage;
            }
        }
        return null;
    }

    public class ErrorPage {
        String page;
        int code;
        String domain;

        public ErrorPage(String page, int code, String domain) {
            this.page = page;
            this.code = code;
            this.domain = domain;
        }
    }

}
