package cn.cedar.data.spring.proxy;

import cn.cedar.data.InParams;
import cn.cedar.data.InstanceFactory;
import cn.cedar.data.spring.annotation.Tx;
import cn.cedar.data.spring.annotation.TxTrigger;
import cn.cedar.data.spring.annotation.TxTriggerType;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author 413338772@qq.com
 */
public class TxCglibProxy implements MethodInterceptor {

    private Object target;

    private List<String> txMethods=new ArrayList<>();

    public Object getInstance(Object target) {
        this.target = target;
        Tx tx=target.getClass().getAnnotation(Tx.class);
        String m=tx.method();
        if(!m.trim().isEmpty()){
            txMethods.add(m.trim());
        }
        String[] ms=tx.methods();
        for (String s : ms) {
            if(!s.trim().isEmpty()){
                txMethods.add(s.trim());
            }
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    private boolean hasTx(String methodName){
        boolean isHasTx=false;
        if(txMethods.isEmpty()){
            isHasTx=true;
        }
        for (String txMethod : txMethods) {
            isHasTx=Pattern.matches(txMethod,methodName);
            if(isHasTx){
                break;
            }
        }
        return isHasTx;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        boolean isHasTx=hasTx(method.getName());
        Object obj=null;
        if(!isHasTx){
            return method.invoke(target,objects);
        }
        TxTrigger tt=method.getAnnotation(TxTrigger.class);
        TxTriggerType[] triggerTypes=new TxTriggerType[]{TxTriggerType.EXCPETION};
        Class<?>[] tariggerExcpetion=null;
        int lt=-1,gt=-1,eq=-1;
        String regexp="";
        if(tt!=null){
            triggerTypes=tt.type();
            for (TxTriggerType triggerType : triggerTypes) {
                if(triggerType==TxTriggerType.EXCPETION){
                    tariggerExcpetion=tt.exception();
                }else if(triggerType==TxTriggerType.INT){
                    lt=tt.lt();
                    gt=tt.gt();
                    eq=tt.eq();
                }else if(triggerType==TxTriggerType.STRING){
                    regexp=tt.regexp();
                }
            }
        }
        boolean intIsNotTrigger=true,stringIsNotTrigger=true;
        InstanceFactory.getJdbcManager().setAutoCommit(false);
        try{
            obj=method.invoke(target,objects);
            intIsNotTrigger=intTriggerTypeParer(obj,lt,gt,eq);
            stringIsNotTrigger=stringTriggerTypeParer(obj,regexp);
            if(intIsNotTrigger&&stringIsNotTrigger){
                InstanceFactory.getJdbcManager().commit();
            }
        }catch (InvocationTargetException e){
            boolean isTrigger=false;
            for (TxTriggerType triggerType : triggerTypes) {
                if(triggerType==TxTriggerType.EXCPETION){
                    isTrigger=true;
                }
            }
            if(isTrigger){
                exceptionHandler(e,tariggerExcpetion,intIsNotTrigger,stringIsNotTrigger);
            }
            throw e.getTargetException();
        }
        return obj;
    }

    private void exceptionHandler(InvocationTargetException e,Class<?>[] tariggerExcpetion,boolean intIsNotTrigger,boolean stringIsNotTrigger){
        boolean isTrggir=false;
        if(tariggerExcpetion==null){
            InstanceFactory.getJdbcManager().rollback();
        }else {
            for (Class<?> excp : tariggerExcpetion) {
                if (excp == Throwable.class || e.getTargetException().getClass() == excp) {
                    InstanceFactory.getJdbcManager().rollback();
                    isTrggir = true;
                    break;
                }
            }
            if (isTrggir) {
                if (intIsNotTrigger && stringIsNotTrigger) {
                    InstanceFactory.getJdbcManager().commit();
                } else {
                    InstanceFactory.getJdbcManager().rollback();
                }
            }
        }
    }

    private boolean intTriggerTypeParer(Object obj,int lt,int gt,int eq){
        if(obj!=null&&InParams.isInt(obj)){
            int val= (int) obj;
            if(eq!=-1&&val==eq){
                InstanceFactory.getJdbcManager().rollback();
                return false;
            }else if(lt!=-1&&gt!=-1){
                if(val<lt&&val>gt){
                    InstanceFactory.getJdbcManager().rollback();
                }
                return false;
            }else if(lt!=-1&&val<lt){
                InstanceFactory.getJdbcManager().rollback();
                return false;
            }else if(gt!=-1&&val>gt){
                InstanceFactory.getJdbcManager().rollback();
                return false;
            }
        }
        return true;
    }

    private boolean stringTriggerTypeParer(Object obj,String regexp){
        if(obj!=null&&InParams.isString(obj)&&!regexp.trim().isEmpty()){
            boolean isMatch=Pattern.matches(regexp,String.valueOf(obj));
            if(isMatch){
                InstanceFactory.getJdbcManager().rollback();
            }
        }
        return true;
    }

}
