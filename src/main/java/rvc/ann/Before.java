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
public @interface Before {
    String value() default "*";
    boolean absolutePath() default false;
}