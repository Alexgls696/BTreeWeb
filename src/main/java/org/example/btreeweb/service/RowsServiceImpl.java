package org.example.btreeweb.service;

import lombok.RequiredArgsConstructor;
import org.example.btreeweb.repository.RowsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RowsServiceImpl implements RowsService {

    private final RowsRepository rowsRepository;

    @Override
    public void add(String row) {
        rowsRepository.add(row);
    }

    @Override
    public void remove(String row) {

    }

    @Override
    public void clear() {
        rowsRepository.clear();
    }

    @Override
    public boolean contains(String row) {
        return rowsRepository.contains(row);
    }

    @Override
    public List<String> findAll() {
        return rowsRepository.findAll();
    }

    @Override
    public List<String> findBetween(String str1, String str2) {
        return rowsRepository.findBetween(str1, str2);
    }

    @Override
    public List<String> findFirstAndLast() {
        return rowsRepository.findFirstAndLast();
    }

    @Override
    public List<String> findIfEqualLength(String string) {
        return rowsRepository.findIfEqualLength(string);
    }

    @Override
    public List<String> findIfLessThan(String string) {
        return rowsRepository.findIfLessThan(string);
    }

    @Override
    public List<String> findIfMoreThan(String string) {
        return rowsRepository.findIfMoreThan(string);
    }
}
