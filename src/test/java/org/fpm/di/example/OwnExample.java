package org.fpm.di.example;

import org.fpm.di.Container;
import org.fpm.di.DummyEnvironment;
import org.fpm.di.Environment;
import org.junit.Before;
import org.junit.Test;

public class OwnExample {
    private Container container;

    @Before
    public void setUp() {
        Environment env = new DummyEnvironment();
        container = env.configure(new MyOwnConfiguration());
    }
    @Test
    public void ownTest1() {
        container.getComponent(MyInjectFirst.class);
    }
    @Test
    public void ownTest2() {
        container.getComponent(MyInjectSecond.class);
    }
    @Test(expected = RuntimeException.class)
    public void ownTest3() {
        container.getComponent(MyInjectThird.class);
    }
}

