package org.fpm.di;

import java.util.Map;

public class DummyBinder implements Binder {

    private final Map<Class, Class> class_list;
    private final Map<Class, Object> instance_list;

    @Override
    public <T> void bind(Class<T> clazz) {
        synchronized (class_list){
            this.class_list.put(clazz,clazz);
        }
    }

    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        synchronized (class_list) {
            if (class_list.containsKey(clazz))
                throw new IllegalArgumentException("In the configuration, this class has already been designated (this class has more than one relationship)");
            if (isContainsClassList(clazz)) {
                this.class_list.replace(clazz, clazz, implementation);
            } else
                this.class_list.put(clazz, implementation);
        }
        this.bind(implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        synchronized (instance_list) {
            this.instance_list.put(clazz, instance);
            if (!class_list.containsValue(clazz))
                this.bind(clazz);
        }
    }

    DummyBinder(Map<Class, Class> ClassList, Map<Class, Object> InstanceList) {
        this.class_list = ClassList;
        this.instance_list = InstanceList;
    }

    <T> boolean isContainsClassList(Class<T> clazz){
        synchronized (class_list) {
            return class_list.containsKey(clazz) || class_list.containsValue(clazz);
        }
    }
}
