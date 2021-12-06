package org.fpm.di;

public class DummyEnvironment implements Environment {

    @Override
    public Container configure(Configuration configuration){
        Container container = new DummyContainer();
        DummyBinder binder = new DummyBinder(container);
        configuration.configure(binder);
        return container;
    }
}
