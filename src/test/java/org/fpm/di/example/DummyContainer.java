package org.fpm.di.example;

import org.fpm.di.Container;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;

public class DummyContainer implements Container {
    public Map<Class, Class> ClassList;
    public Map<Class, Object> InstanceList;
    DummyContainer(){
        this.InstanceList = new HashMap<>();
        this.ClassList =  new HashMap<>();
    }
    <T> boolean isSingleton(Class<T> clazz){ return clazz.isAnnotationPresent(Singleton.class); }
    <T> boolean isContainsClassList(Class<T> clazz){
        return ClassList.containsKey(clazz) || ClassList.containsValue(clazz);
    }
    <T> boolean isContainsInstanceList(Class<T> clazz){
        return InstanceList.containsKey(clazz);
    }
    <T> boolean isClassConstructorHaveAnnotationInject(Class<T> clazz){
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors){
            if (constructor.isAnnotationPresent(Inject.class))
                return true;
        }
        return false;
    }
    <T> Constructor<?> getConstructorInject(Class<T> clazz){
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> chosen_constructor = constructors[0];
        for (Constructor<?> constructor : constructors){
            if (constructor.isAnnotationPresent(Inject.class))
                chosen_constructor = constructor;
        }
        return chosen_constructor;
    }
    @Override
    public <T> void createBin(Class<T> clazz){
        this.ClassList.put(clazz,clazz);
    }
    @Override
    public <T> void createBin(Class<T> clazz1, Class<? extends T> clazz2){
        if (isContainsClassList(clazz1)) {
            this.ClassList.replace(clazz1, clazz1, clazz2);
        }
        else
            this.ClassList.put(clazz1,clazz2);
        this.createBin(clazz2);
    }
    @Override
    public <T> void createBin(Class<T> clazz, T instance){
        this.InstanceList.put(clazz,instance);
    }
    @Override
    public <T> void printLists(){
        System.out.println("Classes");
        this.ClassList.forEach((key, value) -> System.out.println(key + " / " + value));
        System.out.println("Instances");
        this.InstanceList.forEach((key, value) -> System.out.println(key + " / " + value));
    }
    @Override
    public <T> T getComponent(Class<T> clazz) {
        try {
            if (isContainsClassList(clazz)){
                Class<T> ourclazz = ClassList.get(clazz);
                if (isContainsInstanceList(ourclazz))
                    return (T) InstanceList.get(ourclazz);
                if (isSingleton(ourclazz)){
                    T instance = ourclazz.cast(ourclazz.getConstructor().newInstance());
                    InstanceList.put(ourclazz,instance);
                    return instance;
                }
                if (isClassConstructorHaveAnnotationInject(ourclazz)) {
                    System.out.println(getConstructorInject(ourclazz));
                    T instance = ourclazz.cast(
                            getConstructorInject(ourclazz)
                            .newInstance(
                                    getComponent(getConstructorInject(ourclazz)
                                            .getParameterTypes()[0])
                            )
                    );
                    InstanceList.put(ourclazz, instance);
                    return instance;
                }
                else {
                    return ourclazz.cast(ourclazz.getConstructor().newInstance());
                }
            }
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ignored){

        }
        throw new IllegalArgumentException("A class value was entered that was not specified in the configuration");
    }

//
//    public Map<Class, Object> SingletonInstance;
//    public List<Class> ClassList;
//    @Override public <T> T getComponent(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        if (ClassList.contains(clazz)){ return instance; }
//        throw new IllegalArgumentException("A class value was entered that was not specified in the configuration"); }
//    DummyContainer(){ this.InstanceList = new HashMap<>(); this.ClassList = new ArrayList<>(); }
//    public <T> void addItem(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        this.ClassList.add(clazz);
//        T instance;
//        if (clazz.isAnnotationPresent(Singleton.class) && InstanceList.containsKey(clazz)) {
//            instance = (T) InstanceList.get(clazz)[InstanceList.get(clazz).length - 1];
//            System.out.println("instance found in SingleTon list");
//        }
//        else { instance = clazz.cast(clazz.getConstructor().newInstance()); System.out.println("New instance created"); }
//        InstanceList.put(clazz,);
//    }
//    public void printItems(){ System.out.println(this.ClassList); }
//    <T> boolean isSingleton(Class<T> clazz){ return clazz.isAnnotationPresent(Singleton.class); }
}
/**
 Map<Class, Class> Classlist = Hasmap<>()

 Метод создания объекта(clazz ):
 Classlist.has(clazz)
 Если нет, выбросить ошибку ввода
 Если да:
 Обратиться ко значению хешмапа, и поработать с ним, если у этого значения есть поле в InstanceList, то вывести этот инстанс
 Если нет:
 то проверить есть ли у него аннотация синглтон,
 если да:
 то создать объект класса, добавить в InstanceList
 Вывести этот инстанс
 Если нет:
 Проверяем есть ли у него в конструкторах аннотация, и обращаемся по ней
 Если этой аннотации нет - создаём объект класса и не добавляем его никуда, выводим его
 Если аннотация есть - то создаём объект по заданному конструктору и проверить есть ли такие еще
 */
