package com.yjl.vertx.base.com.util;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Auther: yinjiaolong
 * @Date: 2019/1/15 23:39
 * @Description:
 */
public class ReflectionsUtil {

    public static <T> Set<Class<? extends T>> getClassesByBaseClass(String packageName, Class<T> clazz) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(clazz);
    }

    public static Set<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(annotation);
    }

    public static <T> Set<Class<? extends T>> getClasses(String packageName, Class<T> clazz, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(clazz).stream().filter(foundClazz -> foundClazz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    public static <T> List<Class<? super T>> getSuperClasses(Class<T> clazz) {
        List<Class<? super T>> classList = new ArrayList<>();
        Class<? super T> temp = clazz;
        while (temp.getSuperclass() != null) {
            classList.add(temp.getSuperclass());
            temp = temp.getSuperclass();
        }
        return classList;
    }

    public static List<Method> getPublicMethods(Class<?> clazz, Class<? extends Annotation> annotation, Class<?> rntType, Class<?>... paramTypes) {
        return Stream.of(clazz.getMethods()).filter(method -> {
            if (!Modifier.isPublic(method.getModifiers())) {
                return false;
            }
            if (annotation != null && method.getAnnotation(annotation) == null) {
                return false;
            }
            if (rntType != null && method.getReturnType() != rntType) {
                return false;
            }
            if (paramTypes.length > 0 && paramTypes.length != method.getParameterCount()) {
                return false;
            }
            Class<?>[] methodParameterTypes = method.getParameterTypes();
            return IntStream.range(0, paramTypes.length).allMatch(index -> paramTypes[index] == methodParameterTypes[index]);
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> T autoCast(Object obj) {
        return (T) obj;
    }

    public static boolean isWrapPrimitive(Class<?> clazz) {
        try {
            return ((Class) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
