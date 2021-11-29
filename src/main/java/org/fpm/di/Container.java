package org.fpm.di;


public interface Container {
    <T> T getComponent(Class<T> clazz);
    <T> void createBin(Class<T> clazz);
    <T> void createBin(Class<T> clazz1, Class<? extends T> clazz2);
    <T> void createBin(Class<T> clazz, T instance);
    <T> void printLists();
}
