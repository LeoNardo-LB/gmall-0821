package com.atguigu.gmall.index;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
public class TestInteger {

    @Test
    void test() {
        Integer i1 = 1000;
        Integer i2 = 20;
        show(i1, i2);
        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
    }

    void show(Integer i1, Integer i2) {
        i1 = 2000;
        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
    }

    @Test
    void testGroup() {
        List<Person> personList = Arrays.asList(new Person(1, "leonardo", "lanxi"),
                new Person(2, "laozhuang", "lanxi"),
                new Person(3, "xxt", "ln"),
                new Person(4, "xyj", "quzhou"),
                new Person(5, "ljw", "ln"));

        Map<String, List<Person>> collect = personList.stream().collect(Collectors.groupingBy(person -> person.getAddress()));
        System.out.println("collect = " + collect);
    }

}


class Person {

    Integer id;

    String name;

    String address;

    @Override
    public String toString() {
        return "Person{" +
                       "id=" + id +
                       ", name='" + name + '\'' +
                       ", address='" + address + '\'' +
                       '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Person() {
    }

    public Person(Integer id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

}