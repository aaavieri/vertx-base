package com.yjl.vertx.base.com.component;

import com.yjl.vertx.base.com.anno.component.Component;
import com.yjl.vertx.base.com.util.ReflectionsUtil;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentScanner {
    
    public List<Class<?>> getComponents(String onePackage) {
        return this.getComponents(Component.class, new String[]{onePackage});
    }
    
    public List<Class<?>> getComponents(String[] packages) {
        return this.getComponents(Component.class, packages);
    }
    
    public List<Class<?>> getComponents(Class<? extends Annotation> annotation, String onePackage) {
        return this.getComponents(annotation, new String[]{onePackage});
    }
    
    public List<Class<?>> getComponents(Class<? extends Annotation> annotation, String[] packages) {
        return Stream.of(packages).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName,
            annotation).stream()).collect(Collectors.toList());
    }
}
