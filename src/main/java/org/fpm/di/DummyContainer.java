package org.fpm.di;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DummyContainer implements Container {
    private final Map<Class, Class> ClassList;
    private final Map<Class, Object> InstanceList;

    Map<Class, Class> getClassList(){
        return this.ClassList;
    }
    Map<Class, Object> getInstanceList(){
        return this.InstanceList;
    }

    DummyContainer() {
        this.InstanceList = new HashMap<>();
        this.ClassList = new HashMap<>();
    }

    <T> boolean isSingleton(Class<T> clazz) {
        return clazz.isAnnotationPresent(Singleton.class);
    }

    <T> boolean isContainsClassList(Class<T> clazz) {
        return ClassList.containsKey(clazz) || ClassList.containsValue(clazz);
    }

    <T> boolean isContainsInstanceList(Class<T> clazz) {
        synchronized (InstanceList) {
            return InstanceList.containsKey(clazz);
        }
    }

    <T> boolean isClassConstructorHaveAnnotationInject(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class))
                return true;
        }
        return false;
    }

    <T> List<Constructor<?>> getConstructorsInject(Class<T> clazz) {
        List<Constructor<?>> list = new ArrayList<>();
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)){
                constructor.setAccessible(true);
                list.add(constructor);
            }
        }
        if (list.size() > 1) {
            list.sort(Comparator.comparingInt(Constructor::getParameterCount));
            Collections.reverse(list);
        }
        return list;
    }
    <T> T getInstanceOfInjectConstructor(Class<T> ourclazz){
        List<T> instances = new LinkedList<>();
        for (Constructor<?> constructor : getConstructorsInject(ourclazz)){
            List<Object> arguments = new ArrayList<>();
            try {
                for (int i = 0; i <= constructor.getParameterCount() - 1; i++) {
                    arguments.add(this.getComponent(constructor.getParameterTypes()[i]));
                }
                T instance = ourclazz.cast(
                        constructor.newInstance(arguments.toArray())
                );
                instances.add(instance);
            }catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ignored){}
        }
        if (instances.isEmpty()) {
            throw new RuntimeException("The @Inject annotation was specified in the annotation of the class constructors, but none of the constructors with this annotation contains the objects specified in the container configuration in ALL of its arguments");
        }
        return instances.get(0);
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        if (isContainsClassList(clazz)) {
            Class<T> ourclazz = ClassList.get(clazz);
            synchronized (InstanceList){
                if (isContainsInstanceList(ourclazz))
                    return (T) InstanceList.get(ourclazz);
                try {
                    if (isSingleton(ourclazz)) {
                        T instance = ourclazz.cast(ourclazz.getDeclaredConstructor().newInstance());
                        InstanceList.put(ourclazz, instance);
                        return instance;
                    }
                    if (isClassConstructorHaveAnnotationInject(ourclazz)) {
                        return getInstanceOfInjectConstructor(ourclazz);
                    }
                    return ourclazz.cast(ourclazz.getDeclaredConstructor().newInstance());
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException("A class value was entered that was not specified in the configuration");
    }
}

