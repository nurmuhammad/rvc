package rvc;

@FunctionalInterface
public interface Action {

    Object handle() throws Throwable;

}