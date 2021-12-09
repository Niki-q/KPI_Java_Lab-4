package org.fpm.di;

public class DummyEnvironment implements Environment {

    @Override
    public Container configure(Configuration configuration){
        DummyContainer container = new DummyContainer();
        DummyBinder binder = new DummyBinder(container.getClassList(),container.getInstanceList());
        configuration.configure(binder);
        return container;
    }
}
