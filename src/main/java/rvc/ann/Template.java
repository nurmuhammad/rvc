package rvc.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author nurmuhammad
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Template {

    TemplateEngine value() default TemplateEngine.FREEMARKER;

    String viewName() default Constants.NULL_VALUE;

    enum TemplateEngine {
        FREEMARKER, VELOCITY, MUSTACHE, PEBBLE
    }

}