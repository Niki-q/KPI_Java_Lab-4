package org.fpm.di;

import java.lang.reflect.InvocationTargetException;

public interface Environment {
    Container configure(Configuration configuration);
}
