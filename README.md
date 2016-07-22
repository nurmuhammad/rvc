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

Or you can use

Main.java
```java
public class Main {

    public static void main(String[] args) throws Exception {

        RvcServer rvcServer = new RvcServer().port(4567);

        rvcServer.init();

        rvcServer.classes(
                WelcomeController.class
                /*  add more classes here */
                );

        rvcServer.start();
    }

}
```

WelcomeController.java
```java
import rvc.ann.*;

@Controller
public class WelcomeController {

    @GET
    Object index(){
        return "hello world";
    }

    @GET
    @Json
    Object toJson(){
        return "hello, world".split(",");
    }

}

```
Start you code and enter to these urls

http://localhost:4567/to-json

http://localhost:4567/index


Here my real project using this "framework".

https://github.com/nurmuhammad/rvcjava-cms