package rvc;

@FunctionalInterface
public interface Filter {

    void handle() throws Throwable;

}