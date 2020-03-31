package cn.cedar.data.spring.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author 413338772@qq.com
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface TxTrigger {
    /**
     * 触发事务回滚类型
     * @see TxTriggerType
     * @return TxTriggerType[]
     */
    TxTriggerType[] type() default TxTriggerType.EXCPETION;

    /**
     * 异常类.class
     * type=TxTriggerType.EXCPETION
     * @return
     */
    Class<?>[] exception() default Throwable.class;
    /**
     * 正则表达式
     * type=TxTriggerType.STRING
     * @return
     */
    String regexp() default "";

    /**
     * 小于
     * type=TxTriggerType.INT
     * @return int
     */
    int lt() default -1;
    /**
     * 大于 gt
     * type=TxTriggerType.INT
     * @return int
     */
    int gt() default -1;
    /**
     * 等于  eq与lt或gt一起使用eq的优先级高
     * type=TxTriggerType.INT
     * @return int
     */
    int eq() default -1;
}
