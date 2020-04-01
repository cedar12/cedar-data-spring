# cedar-data-spring
一个易于使用的 [cedar-data](https://github.com/cedar12/cedar-data.git) 框架的Spring集成组件

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
1.1版本
1. `@Tx`注解新增`method`、`methods`参数
    - `method`参数指定方法被事务管理（正则表达式）
    - `methods`参数同上（多正则表示）
2. 新增`@TxTrigger`注解
    - `type`参数指定触发事务回滚类型（可多个类型）（默认值：`TxTriggerType.EXCPETION` 可选值：`TxTriggerType.EXCPETION`|`TxTriggerType.INT`|`TxTriggerType.STRING`）
    - `exception` 类型需有`TxTriggerType.EXCPETION`才生效，参数指定触发事务回滚的异常.class（默认值：Throwable.class(所有异常都回滚)）
    - `lt` 类型需有`TxTriggerType.INT`才生效，方法返回值需int类型，参数指定方法返回值小于`lt`指定值触发回滚（可和`lt`结合使用，默认值：-1，-1不生效）
    - `gt` 类型需有`TxTriggerType.INT`才生效，方法返回值需int类型，参数指定方法返回值大于`gt`指定值触发回滚（可给`gt`结合使用，默认值：-1，-1不生效）
    - `eq` 类型需有`TxTriggerType.INT`才生效，方法返回值需int类型，参数指定方法返回值等于`eq`指定值触发回滚(`eq`优先级比`lt`，`gt`高，默认值：-1，-1不生效)
    - `regexp` 类型需有`TxTriggerType.STRING`才生效，方法返回值需String类型，参数指定方法返回值匹配该正常表达式触发回滚

1.1.6版本
1. cglib包依赖替换成spring-core包依赖
2. 修复`@Tx`指定方法不标注`@TxTrigger`报错问题