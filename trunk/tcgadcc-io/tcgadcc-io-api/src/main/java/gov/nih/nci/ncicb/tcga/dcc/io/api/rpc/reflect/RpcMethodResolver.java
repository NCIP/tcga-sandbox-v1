/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.reflect;


/**
 * Resolves available RPC methods. 
 * 
 * @author nichollsmc
 */
public class RpcMethodResolver {

//    private Map<String, Method> methodMap = new FastMap<String, Method>();
//    private Object              callableObject;
//
//    public RpcMethodResolver(Object callableObject) {
//        this.callableObject = callableObject;
//        scanMethods();
//    }
//
//    private void scanMethods() {
//        for (Method method : callableObject.getClass().getDeclaredMethods()) {
//            int modifiers = method.getModifiers();
//            Class<?>[] parameterTypes = method.getParameterTypes();
//
//            if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)
//                    && method.getReturnType().equals(JsonObject.class) && parameterTypes.length > 0
//                    && RpcContext.class.isAssignableFrom(parameterTypes[0])) {
//                methodMap.put(method.getName(), method);
//            }
//        }
//    }
//
//    public JsonObject dispatch(String methodName, Object... args) throws NoSuchMethodException,
//            InvocationTargetException, IllegalAccessException {
//        if (methodMap.containsKey(methodName)) {
//            return (JsonObject) methodMap.get(methodName).invoke(callableObject, args);
//        }
//        else {
//            throw new NoSuchMethodException(methodName);
//        }
//    }
//
//    public boolean hasMethod(String name) {
//        return methodMap.containsKey(name);
//    }
//
//    public Set<String> methodNames() {
//        return Collections.unmodifiableSet(methodMap.keySet());
//    }
}
