package org.example.btreeweb.service;

import java.util.List;

public interface RowsService {
    void add(String row);

    void remove(String row);

    void clear();

    boolean contains(String row);

    List<String> findAll();

    List<String> findBetween(String str1, String str2);

    List<String> findFirstAndLast();

    List<String> findIfEqualLength(String string);

    List<String> findIfLessThan(String string);

    List<String> findIfMoreThan(String string);
}
