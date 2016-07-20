package rvc.ann;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author nurmuhammad
 *
 */

@Target({ TYPE })
@Retention(RUNTIME)
public @interface Controller {

	String url() default "/";

}
