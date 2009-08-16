package no.tornado.brap.modification;

import no.tornado.brap.common.ModificationList;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class SetterModificationInterceptor implements MethodInterceptor {
    private final String parentPath;
    private final ModificationList modificationList;

    public SetterModificationInterceptor(String parentPath, ModificationList modificationList) {
        this.parentPath = parentPath;
        this.modificationList = modificationList;
    }

    private String getPropertyName(Method method, Object[] args) {
        if (method.getName().startsWith("set") && args.length == 1) {
            String propertyName = method.getName().substring(3);
            return new String(new char[] {propertyName.charAt(0)}).toLowerCase() + propertyName.substring(1);
        }

        return null;
    }


    public Object invoke(MethodInvocation mi) throws Throwable {
        String propertyName = getPropertyName(mi.getMethod(), mi.getArguments());
        if (propertyName != null)
            modificationList.addModification(("".equals(parentPath) ? "" : parentPath + ".") + propertyName, mi.getArguments()[0]);

        return mi.getMethod().invoke(mi.getThis(), mi.getArguments());
    }
}