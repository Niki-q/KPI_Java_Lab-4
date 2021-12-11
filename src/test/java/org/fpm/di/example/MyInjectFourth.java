package org.fpm.di.example;

import javax.inject.Inject;

public class MyInjectFourth {
    private final Object dependency;
    @Inject
    MyInjectFourth(MyPrototype myPrototype, Integer integer, MySingleton mySingleton){
        this.dependency = (myPrototype);
    }
    @Inject
    MyInjectFourth(MyPrototype myPrototype){
        this.dependency = myPrototype;
    }
    @Inject
    MyInjectFourth(MyPrototype myPrototype, Integer integer){
        this.dependency = (myPrototype);
    }

}
