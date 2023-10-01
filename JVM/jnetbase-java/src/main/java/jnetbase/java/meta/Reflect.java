package jnetbase.java.meta;

import jnetbase.java.sys.Strings;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

public final class Reflect {

    public static boolean isAsync(Method method) {
        return isAsync(method.getGenericReturnType());
    }

    public static boolean isAsync(Type ret) {
        return ret instanceof ParameterizedType pt
                && pt.getRawType() instanceof Class<?> rc
                && Future.class.isAssignableFrom(rc);
    }

    public static boolean isDelegate(Object del) {
        if (del == null)
            return false;
        if (del.getClass().getName().contains("Lambda"))
            return true;
	    return del instanceof Class<?> c && c.isInterface() && c.getMethods().length == 1;
    }

    public static Type getTaskType(Type taskType, Type defaultArg) {
        var taskArg = taskType instanceof ParameterizedType pt
                ? Arrays.stream(pt.getActualTypeArguments()).findFirst().orElse(null)
                : null;
        return taskArg == null ? defaultArg == null ? Object.class : defaultArg : taskArg;
    }

    public static Method getMethod(BiFunction<Object, Object[], Object> func) {
        return (Method)getField(func.getClass().getDeclaredFields()[0], func);
    }

    public static Object getField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            Object o = field.get(obj);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getTheMethod(Object delegate) {
        return delegate.getClass().getInterfaces()[0].getMethods()[0];
    }

    public static InvocationHandler getProxyHandler(Object obj) {
        try {
            return Proxy.getInvocationHandler(obj);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int getRank(Object value) {
        return getRank(value.getClass());
    }

    public static int getRank(Class<?> type) {
        return type.isArray() ? Strings.countMatches(type.getName(), '[') : 0;
    }

    public static Object invoke(Method method, Object obj, Object[] args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(method.toString(), e);
        }
    }

    public static Method getMethod(Object obj, String name, Class<?>... args) {
        var clazz = obj.getClass();
        try {
            return clazz.getMethod(name, args);
        } catch (Exception e) {
            throw new RuntimeException(clazz.toString(), e);
        }
    }

    public static List<Property> getProperties(Type type)
    {
        return getProperties(extractRawClass(type));
    }

    public static List<Property> getProperties(Class<?> type) {
        var getMap = new HashMap<String, Method>();
        var setMap = new HashMap<String, Method>();
        for (var method : type.getMethods()) {
            var name = method.getName();
            if (name.equalsIgnoreCase("getClass"))
                continue;
            if (Character.isUpperCase(name.charAt(0))) {
                getMap.put(name, method);
                continue;
            }
            if (name.startsWith("get")) {
                getMap.put(name.substring(3), method);
                continue;
            }
            if (name.startsWith("is")) {
                getMap.put(name.substring(2), method);
                continue;
            }
            if (name.startsWith("set")) {
                setMap.put(name.substring(3), method);
                continue;
            }
        }
        var props = new ArrayList<Property>();
        var creator = type.getConstructors()[0];
        for (var params : creator.getParameters()) {
            var parmName = params.getName();
            var getter = getMap.get(parmName);
            var setter = setMap.get(parmName);
            props.add(new Property(parmName, getter, setter));
        }
        return props;
    }

    public static Class<?> extractRawClass(Type type)
    {
        if (type instanceof ParameterizedType pt)
            if (pt.getRawType() instanceof Class<?> pc)
                return pc;
        return (Class<?>) type;
    }

    public static <T> Constructor<T> getConstructor(Type type, Class<?>[] types) {
        return (Constructor<T>)getConstructor(extractRawClass(type), types);
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>[] types) {
        try {
            return clazz.getConstructor(types);
        } catch (NoSuchMethodException e) {
            return (Constructor<T>) clazz.getConstructors()[0];
        }
    }

    public static <T> T getVal(Future<T> task) {
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createNew(Type type) {
        return (T) createNew(extractRawClass(type));
    }

    public static <T> T createNew(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Constructor<T> getFirstConstructor(Type type){
        return (Constructor<T>)getFirstConstructor(extractRawClass(type));
    }

    public static <T> Constructor<T> getFirstConstructor(Class<T> clazz) {
        for (var item : clazz.getConstructors())
            return (Constructor<T>) item;
        return null;
    }

    public static List<Class<?>> getInterfaces(Class<?> type) {
        var interfaces = new LinkedHashSet<Class<?>>();
        getAllInterfaces(type, interfaces);
        return new ArrayList<>(interfaces);
    }

    private static void getAllInterfaces(Class<?> type, Set<Class<?>> found) {
        while (type != null){
            for (var interf : type.getInterfaces())
                if (found.add(interf))
                    getAllInterfaces(interf, found);
            type = type.getSuperclass();
        }
    }
}
