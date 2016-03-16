# rvc

Getting started
---------------

```java

public class HelloWorld {
    public static void main(String[] args) {
        new RvcServer()
             .before(() -> {
                Response.get().gzip();
             })
             .get("/hello", () -> "Hello world!")
             .start();
    }
}
```