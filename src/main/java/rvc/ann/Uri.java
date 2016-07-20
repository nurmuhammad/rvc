package rvc.ann;

/**
 * @author nurmuhammad
 */

public @interface Uri {
    String value() default Constants.NULL_VALUE;

    boolean absolutePath() default false;
}
