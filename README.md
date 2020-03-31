# cedar-data-spring
一个易于使用的 [cedar-data](https://github.com/cedar12/cedar-data-spring.git) 框架的Spring集成组件

> 需`cedar-data`版本为`1.1.5`及其以上

## 使用

> `cn.cedar.data.spring.RegistryCedarData`类会扫描`@CedarData`注解的接口类，将其注册到`spring`容器中

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

### 事务
> `@Tx`注册成事务管理的类
```java
import cn.cedar.data.spring.annotation.Tx;
import org.springframework.stereotype.Service;

@Tx
@Service
public class TestService{
    
    @Autowired
    private TestDao testDao;    

    /*发生异常将回滚*/    
    public int add(TestBean testBean){
        return testDao.insert(testBean);
    }
    
    /*判断条件回滚*/
    public int addLtOne(TestBean){
        int row=testDao.insert(testBean);
        if(row<1){
            throw new RuntimeException();
        }
        return row;
    }

}
```