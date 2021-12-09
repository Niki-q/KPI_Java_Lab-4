package org.fpm.di.example;

import javax.inject.Inject;

public class MyInjectThird {
    private final Object dependency;
    MyInjectThird(MyPrototype myPrototype){
        this.dependency = myPrototype;
    }
    @Inject
    MyInjectThird(MyPrototype myPrototype, Integer integer){
        this.dependency = (myPrototype);
    }
}
