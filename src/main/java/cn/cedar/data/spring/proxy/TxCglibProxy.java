package cn.cedar.data.spring.proxy;

import cn.cedar.data.InstanceFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author 413338772@qq.com
 */
public class TxCglibProxy implements MethodInterceptor {

    private Object target;

    public Object getInstance(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object obj=null;
        InstanceFactory.getJdbcManager().setAutoCommit(false);
        try{
            obj=method.invoke(target,objects);
            InstanceFactory.getJdbcManager().commit();
        }catch(Throwable e){
            InstanceFactory.getJdbcManager().rollback();
            throw e;
        }
        return obj;
    }
}
