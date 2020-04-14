package cn.cedar.data.spring;

import cn.cedar.data.annotation.CedarData;
import cn.cedar.data.spring.constant.CedarDataSpringConstant;
import cn.cedar.data.spring.factory.CedarDataSpringProxyFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.Set;

/**
 * @author cedar12 413338772@qq.com
 */
public class    CedarDataBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private static Log log= LogFactory.getLog(CedarDataBeanDefinitionScanner.class);

    private String[] basePackage;

    public CedarDataBeanDefinitionScanner(BeanDefinitionRegistry registry, String[] basePackage) {
        super(registry, false);
        this.basePackage=basePackage;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        this.addFilter();
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            log.error("@CedarData interface is empty");
            return beanDefinitionHolders;
        }
        this.createBeanDefinition(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && metadata.isIndependent();
    }

    private void addFilter() {
        addIncludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className=metadataReader.getClassMetadata().getClassName();
                try {
                    Class<?> cls=Class.forName(className);
                    CedarData cedarData=cls.getAnnotation(CedarData.class);
                    if(cedarData!=null){
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage());
                }
                return false;
            }
        });
    }

    private void createBeanDefinition(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            GenericBeanDefinition definition = ((GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition());
            log.info(definition.getBeanClassName());
            definition.getPropertyValues().add(CedarDataSpringConstant.INTERFACE_CLASS, definition.getBeanClassName());
            definition.setBeanClass(CedarDataSpringProxyFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

}