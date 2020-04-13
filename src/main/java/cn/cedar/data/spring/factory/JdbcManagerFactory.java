package cn.cedar.data.spring.factory;

import cn.cedar.data.InstanceFactory;
import cn.cedar.data.JdbcManager;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author 413338772@qq.com
 */
@Deprecated
public class JdbcManagerFactory implements FactoryBean<JdbcManager> {

    @Override
    public JdbcManager getObject() throws Exception {
        return InstanceFactory.getJdbcManager();
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
