package rvc.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Nurmuhammad
 */

@Retention(RUNTIME)
@Target(METHOD)
public @interface Cacheable {
    long expire(); // life time in milliseconds
}
