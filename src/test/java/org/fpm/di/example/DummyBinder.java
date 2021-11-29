package org.fpm.di.example;

import org.fpm.di.Binder;
import org.fpm.di.Container;

public class DummyBinder implements Binder {
    private Container container;
    @Override
    public <T> void bind(Class<T> clazz) {
        container.createBin(clazz);
    }

    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        container.createBin(clazz,implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        container.createBin(clazz,instance);
    }
    DummyBinder(Container container){ this.container = container; }
}
