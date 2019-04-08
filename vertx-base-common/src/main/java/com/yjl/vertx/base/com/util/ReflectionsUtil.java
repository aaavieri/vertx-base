package com.yjl.vertx.base.com.util;

import io.vertx.core.Future;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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

    public static boolean compareType(Type type1, Type type2, boolean strict) {
        if (type1 instanceof ParameterizedType && type2 instanceof ParameterizedType) {
            ParameterizedType paramType1 = autoCast(type1);
            ParameterizedType paramType2 = autoCast(type2);
            if (!compareType(paramType1.getRawType(), paramType2.getRawType(), strict)) {
                return false;
            }
            if (paramType1.getActualTypeArguments().length != paramType2.getActualTypeArguments().length) {
                return false;
            }
            return IntStream.range(0, paramType1.getActualTypeArguments().length)
                .allMatch(i -> compareType(paramType1.getActualTypeArguments()[i], paramType2.getActualTypeArguments()[i], strict));
        } else if (type1 instanceof Class && type2 instanceof Class) {
            Class clazz1 = autoCast(type1);
            Class clazz2 = autoCast(type2);
            return clazz1.equals(clazz2) || (!strict && clazz2.isAssignableFrom(clazz1));
        } else if (type1 instanceof GenericArrayType && type2 instanceof GenericArrayType) {
            GenericArrayType arrayType1 = autoCast(type1);
            GenericArrayType arrayType2 = autoCast(type2);
            return compareType(arrayType1.getGenericComponentType(), arrayType2.getGenericComponentType(), strict);
        } else if (type1 instanceof TypeVariable && type2 instanceof TypeVariable) {
            TypeVariable typeVariable1 = autoCast(type1);
            TypeVariable typeVariable2 = autoCast(type2);
            return typeVariable1.equals(typeVariable2);
        } else {
            return type1.equals(type2);
        }
    }

    public static boolean isFutureParamType(Type type) {
        if (type instanceof ParameterizedType) {
            return ReflectionsUtil.<ParameterizedType>autoCast(type).getRawType().equals(Future.class);
        } else {
            return false;
        }
    }

    public static Type getFutureActuleParamType(Type type) {
        if (isFutureParamType(type)) {
            return ReflectionsUtil.<ParameterizedType>autoCast(type).getActualTypeArguments()[0];
        } else {
            throw null;
        }
    }
}
