package org.fpm.di.example;

import javax.inject.Inject;
import java.util.ArrayList;

public class MyInjectFirst {
    private final Object dependency;
     MyInjectFirst(MyPrototype myPrototype){
        this.dependency = myPrototype;
    }
    @Inject
    MyInjectFirst(MyPrototype myPrototype, MySingleton mySingleton){
        this.dependency = (mySingleton);
    }
}
