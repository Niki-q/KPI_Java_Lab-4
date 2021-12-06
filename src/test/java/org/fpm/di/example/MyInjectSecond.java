package org.fpm.di.example;

import javax.inject.Inject;

public class MyInjectSecond {
    private final Object dependency;
    @Inject
    MyInjectSecond(MyPrototype myPrototype){
        this.dependency = myPrototype;
    }
    @Inject
    MyInjectSecond(MyPrototype myPrototype, Integer integer){
        this.dependency = (myPrototype);
    }
}
