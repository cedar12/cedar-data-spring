package cn.cedar.data.spring;

import cn.cedar.data.InstanceFactory;
import cn.cedar.data.JdbcManager;
import cn.cedar.data.annotation.CedarData;
import cn.cedar.data.spring.annotation.Tx;
import cn.cedar.data.spring.constant.CedarDataSpringConstant;
import cn.cedar.data.spring.factory.CedarDataSpringProxyFactory;
import cn.cedar.data.spring.factory.JdbcManagerFactory;
import cn.cedar.data.spring.factory.TxProxyFactory;
import cn.cedar.data.spring.scanner.CedarDataScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 注册CedarData
 * @author 413338772@qq.com
 */
public class RegistryCedarData implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    /**
     * 扫描@CedarData的基础包
     */
    private String scanPackage="";

    /**
     * 数据库连接池
     */
    private DataSource dataSource;

    /**
     * import最大层数
     */
    private int maxLayer=5;

    /**
     * 是否打印sql
     */
    private boolean displaySql=false;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMaxLayer(int maxLayer) {
        this.maxLayer = maxLayer;
    }
    public int getMaxLayer() {
        return this.maxLayer;
    }

    public boolean isDisplaySql() {
        return displaySql;
    }

    public void setDisplaySql(boolean displaySql) {
        this.displaySql = displaySql;
    }

    public void setCtx(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private ApplicationContext ctx;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        InstanceFactory.setJdbcManager(new JdbcManager(dataSource));
        CedarDataScanner sc=new CedarDataScanner(scanPackage);
        List<Class<?>> list=sc.get();
        Class<?>[] cedarDataClass=new Class<?>[list.size()];
        list.toArray(cedarDataClass);
        InstanceFactory.setMaxLayer(maxLayer);
        InstanceFactory.setDisplaySql(displaySql);
        InstanceFactory.preload(cedarDataClass);
        for (Class cls : list) {
            CedarData cd= (CedarData) cls.getAnnotation(CedarData.class);
            String value=cd.value();
            if(value.trim().isEmpty()){
                String simpleName=cls.getSimpleName();
                value=simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
            }
            setBean(beanDefinitionRegistry,cls, CedarDataSpringProxyFactory.class,value);
        }
        setBean(beanDefinitionRegistry,JdbcManager.class, JdbcManagerFactory.class,CedarDataSpringConstant.JDBC_MANGER);

        for (String beanDefinitionName : beanDefinitionRegistry.getBeanDefinitionNames()) {
            Object bean=ctx.getBean(beanDefinitionName);
            Class<?> cls=bean.getClass();
            Tx tx=(Tx)cls.getAnnotation(Tx.class);
            autowired(bean);
            if(tx!=null){
                beanDefinitionRegistry.removeBeanDefinition(beanDefinitionName);
                String value=tx.value();
                if(value.trim().isEmpty()){
                    String simpleName=cls.getSimpleName();
                    value=simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
                }
                setBean(beanDefinitionRegistry,cls, TxProxyFactory.class,value,bean);
            }
        }

    }
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    private void autowired(Object bean){
        Class<?> cls=bean.getClass();
        for (String definitionName : ctx.getBeanDefinitionNames()) {
            try {
                Field f=cls.getDeclaredField(definitionName);
                Autowired autowired=f.getAnnotation(Autowired.class);
                if(f!=null&&autowired!=null){
                    f.setAccessible(true);
                    f.set(bean,ctx.getBean(definitionName));
                    f.setAccessible(false);
                    return;
                }
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void setBean(BeanDefinitionRegistry beanDefinitionRegistry, Class<?> cls, Class<?> factoryCls, String beanName, Object target){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        if(!beanName.equals(CedarDataSpringConstant.JDBC_MANGER)){
            definition.getPropertyValues().add(CedarDataSpringConstant.INTERFACE_CLASS, definition.getBeanClassName());
            definition.getPropertyValues().add(CedarDataSpringConstant.TARGET, target);
        }
        definition.setBeanClass(factoryCls);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionRegistry.registerBeanDefinition(beanName, definition);
    }

    private void setBean(BeanDefinitionRegistry beanDefinitionRegistry, Class<?> cls, Class<?> factoryCls, String beanName){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        if(!beanName.equals(CedarDataSpringConstant.JDBC_MANGER)){
            definition.getPropertyValues().add(CedarDataSpringConstant.INTERFACE_CLASS, definition.getBeanClassName());
        }
        definition.setBeanClass(factoryCls);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionRegistry.registerBeanDefinition(beanName, definition);
    }

}
