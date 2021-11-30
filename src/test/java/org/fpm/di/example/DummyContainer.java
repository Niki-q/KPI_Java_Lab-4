package org.fpm.di.example;
import org.fpm.di.Container;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    <T> List<Constructor<?>> getConstructorsInject(Class<T> clazz){
        List<Constructor<?>> list = new LinkedList<>();
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors){
            if (constructor.isAnnotationPresent(Inject.class))
                 list.add(constructor);
        }
        if (list.size()>1)
            list = sortListOfConstructors(list);
        list.add(constructors[0]);
        return list;
    }
    <T> List<Constructor<?>> sortListOfConstructors(List<Constructor<?>> list){
        boolean isSorted = false;
        while (!isSorted){
            isSorted = true;
            for (int i = 0; i <= list.size()-2; i++){
                if (list.get(i).getParameterCount() < list.get(i+1).getParameterCount()){
                    isSorted = false;
                    Constructor<?> buff = list.get(i);
                    list.set(i,list.get(i+1));
                    list.set(i+1,buff);
                }
            }
        }
        return list;
    }
    <T> T getInstanceOfInjectConstructor(Class<T> ourclazz, Constructor<?> constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Object> arguments = new ArrayList<>();
        for (int i =0; i<= constructor.getParameterCount()-1; i++){
            arguments.add(getComponent(constructor.getParameterTypes()[i]));
        }
        T instance = ourclazz.cast(
                constructor.newInstance(arguments.toArray())
        );
        InstanceList.put(ourclazz, instance);
        return instance;
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
                    for (Constructor<?> constructor : getConstructorsInject(ourclazz)) {
                        try {
                            return getInstanceOfInjectConstructor(ourclazz, constructor);
                        } catch (Exception ignored) {}
                    }
                }
                else {
                    return ourclazz.cast(ourclazz.getConstructor().newInstance());
                }
            }
        }
        catch (IllegalAccessException e){
            String comment = "It is not possible to get access to the class constructor, perhaps the constructor has a non-public access modifier";
            printPersonalException(e,comment);
        }
        catch (InstantiationException e){
            String comment = "Failed to access the given constructors";
            printPersonalException(e,comment);
        }
        catch (InvocationTargetException e){
            String comment = "While creating the object, the constructor threw a specific exception which was wrapped in an InvocationTargetException";
            printPersonalException(e,comment);
        }
        catch (NoSuchMethodException e){
            String comment = "Could not find constructor";
            printPersonalException(e,comment);
        }
        throw new IllegalArgumentException("A class value was entered that was not specified in the configuration");
    }
    void printPersonalException(Exception e, String message){
        System.out.println("Program comment of this exception:\n");
        System.out.println(message);
        System.out.println("Thrown exception text:\n");
        System.out.println(e.getMessage());
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


