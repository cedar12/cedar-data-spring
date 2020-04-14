package cn.cedar.data.spring;

import cn.cedar.data.InstanceFactory;
import cn.cedar.data.JdbcManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;

/**
 * @author cedar12 413338772@qq.com
 */
public class CedarDataSpringRegister implements BeanDefinitionRegistryPostProcessor {
    private String[] basePackage;
    private JdbcTemplate jdbcTemplate;
    private int maxLayer;
    private boolean displaySql;

    public void setScanPackage(String scanPackage) {
        this.basePackage = scanPackage.trim().split(",");
    }

    public void setMaxLayer(int maxLayer) {
        this.maxLayer = maxLayer;
    }

    public void setDisplaySql(boolean displaySql) {
        this.displaySql = displaySql;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        InstanceFactory.setJdbcManager(new JdbcTemplateManager(jdbcTemplate));
    }

    private static final String EVN="cedar-data-spring";

    public CedarDataSpringRegister(){
        InstanceFactory.setEnv(EVN);
    }

    public CedarDataSpringRegister(String scanPackage,int maxLayer,boolean displaySql,String env) {
        InstanceFactory.setEnv(env);
        this.basePackage=scanPackage.split(",");
        this.maxLayer=maxLayer;
        this.displaySql=displaySql;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (basePackage==null||basePackage.length==0) {
            return;
        }
        InstanceFactory.setMaxLayer(maxLayer);
        JdbcTemplateManager.displaySql=displaySql;
        CedarDataBeanDefinitionScanner scanner = new CedarDataBeanDefinitionScanner(registry,basePackage);
        scanner.doScan(basePackage);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    public static class PreloadJdbcManger extends JdbcManager {
        @Override
        public Connection getConnection() {
            return null;
        }
    }

}