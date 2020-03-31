# cedar-data-spring
一个易于使用的cedar-data(1.1.5+)框架的Spring集成组件


## 使用

> cn.cedar.data.spring.RegistryCedarData类会扫描@CedarData注解的接口类，将其注册成spring bean

### xml配置
```xml
<!-- 连接池 -->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
    <property name="url" value="jdbc:mysql://127.0.0.1:3306/test"/>
    <property name="username" value="root"/>
    <property name="password" value="**"/>
</bean>

<!-- 注册CedarData -->
<bean id="registryCedarData" class="cn.cedar.data.spring.RegistryCedarData">
    <!-- 扫描CedarData基础类 -->
    <property name="scanPackage" value="org.example"/>
    <!-- 连接池 -->
    <property name="dataSource" ref="dataSource"/>
</bean>
```

### 注解配置
```java
@Configuration
@ComponentScan("org.example")
public class AppConfig {

    @Bean
    public DataSource dataSource(){
        DruidDataSource dataSource=new DruidDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("**");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test");
        return dataSource;
    }

    @Bean
    public RegistryCedarData registryCedarData(){
        RegistryCedarData registryCedarData=new RegistryCedarData();
        registryCedarData.setScanPackage("org.example");
        registryCedarData.setDataSource(dataSource());
        return registryCedarData;
    }

}
```
