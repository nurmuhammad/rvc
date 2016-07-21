package rvc.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author nurmuhammad
 */

@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Page {

    String uri() default Constants.NULL_VALUE;

    boolean absolutePath() default false;

    Template.TemplateEngine value() default Template.TemplateEngine.PEBBLE;

    String viewName() default Constants.NULL_VALUE;

}