package cn.cedar.data.spring.factory;

import cn.cedar.data.InstanceFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author 413338772@qq.com
 */
public class CedarDataSpringProxyFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }
    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    @Override
    public T getObject() throws Exception {
        return (T) InstanceFactory.getInstance(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}