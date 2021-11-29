package org.fpm.di.example;

import org.fpm.di.Container;

public class DummyContainer implements Container {
    @Override
    public <T> T getComponent(Class<T> clazz) {
        return null;
    }
}
