package org.fpm.di.example;
import org.fpm.di.Container;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DummyContainer implements Container {
    private Map<Class, Class> ClassList;
    private Map<Class, Object> InstanceList;

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
        return InstanceList.containsKey(clazz);
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
        List<Constructor<?>> list = new LinkedList<>();
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)){
                constructor.setAccessible(true);
                list.add(constructor);
            }
        }
        if (list.size() > 1)
            list = sortListOfConstructors(list);
        return list;
    }

    <T> List<Constructor<?>> sortListOfConstructors(List<Constructor<?>> list) {
        boolean isSorted = false;
        while (!isSorted) {
            isSorted = true;
            for (int i = 0; i <= list.size() - 2; i++) {
                if (list.get(i).getParameterCount() < list.get(i + 1).getParameterCount()) {
                    isSorted = false;
                    Constructor<?> buff = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, buff);
                }
            }
        }
        return list;
    }

    <T> T getInstanceOfInjectConstructor(Class<T> ourclazz) throws InstantiationException {
        List<T> instances = new LinkedList<>();
        for (Constructor<?> constructor : getConstructorsInject(ourclazz)){
            List<Object> arguments = new ArrayList<>();
            for (int i = 0; i <= constructor.getParameterCount() - 1; i++) {
                arguments.add(getComponent(constructor.getParameterTypes()[i]));
            }
            try {
                T instance = ourclazz.cast(
                        constructor.newInstance(arguments.toArray())
                );
                instances.add(instance);
            }catch (InvocationTargetException | InstantiationException | IllegalAccessException ignored){}
        }
        if (instances.isEmpty())
            throw new InstantiationException("The @Inject annotation was specified in the annotation of the class constructors, but none of the constructors with this annotation contains the objects specified in the container configuration in ALL of its arguments");
        return instances.get(0);
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        if (isContainsClassList(clazz)) {
            Class<T> ourclazz = ClassList.get(clazz);
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
        throw new IllegalArgumentException("A class value was entered that was not specified in the configuration");
    }

}

/**NoSuchMethodException - не удалось найти конструктор
 * InvocationTargetException - при создании обьекта конструктор вызвал определенное исключение, которое было обернуто в InvocationTargetException
 * InstantiationException - не удалось обратиться по заданным конструкторам
 * IllegalAccessException - не удаётся получиться доступ к конструктору класса, возможно у конструктора указан не публичный модификатор доступа
 * */


/**От большого аргумента к меньшему потому что это покажет функционал программы в частности возможность перебирать любыми значениями аргументов
 *
 *  И не вызывать дефолтный конструктор так как пользователь фреймворка должен понимать свои действия и выбирать аннотации конструкторов в соответсвии с конфигурацией контейнера или наоборот */
