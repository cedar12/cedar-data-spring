package cn.cedar.data.spring.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 413338772@qq.com
 */
@Target({TYPE,METHOD})
@Retention(RUNTIME)
public @interface Tx {
    String value() default "";
}
