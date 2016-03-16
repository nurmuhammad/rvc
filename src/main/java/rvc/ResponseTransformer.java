package rvc;

@FunctionalInterface
public interface ResponseTransformer {
    String transform(Object object) throws Exception;
}
