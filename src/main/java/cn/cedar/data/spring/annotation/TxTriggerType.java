package cn.cedar.data.spring.annotation;

/**
 * 事务触发类型
 */
@Deprecated
public enum TxTriggerType {
    /**
     * 异常触发
     */
    EXCPETION,
    /**
     * 返回值int条件触发
     */
    INT,
    /**
     * 返回值LONG条件触发
     */
    LONG,
    /**
     * 返回值字符条件触发
     */
    STRING
}
