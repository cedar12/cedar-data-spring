package cn.cedar.data.spring.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 413338772@qq.com
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Tx {
    /**
     * Spring bean 注册名称
     * @return
     */
    String value() default "";

    /**
     * 开启事务的方法名称（正则）
     * @return
     */
    String method() default "";

    /**
     * 开启事务的多方法名称（多正则）
     * @return
     */
    String[] methods() default {""};
}
