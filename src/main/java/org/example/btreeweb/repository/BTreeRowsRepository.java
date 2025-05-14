package org.example.btreeweb.repository;

import lombok.RequiredArgsConstructor;
import org.example.btreeweb.exception.FailedToAddKeyException;
import org.example.btreeweb.exception.FailedToRemoveKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BTreeRowsRepository implements RowsRepository {

    private final BTree bTree;

    @Override
    public void add(String row) {
        try {
            bTree.add(row);
        }catch (Exception e){
            throw new FailedToAddKeyException(e.getMessage());
        }
    }

    @Override
    public void remove(String row) {
        try{
            bTree.remove(row);
        }catch (Exception exception) {
            throw new FailedToRemoveKeyException(exception.getMessage());
        }
    }

    @Override
    public void clear() {
        bTree.clear();
    }

    @Override
    public boolean contains(String row) {
        return bTree.isExists(row);
    }


    @Override
    public List<String> findAll() {
        return bTree.getAll();
    }

    @Override
    public List<String> findBetween(String str1, String str2) {
        return bTree.findBetween(str1, str2);
    }

    @Override
    public List<String> findFirstAndLast() {
        return bTree.findFirstAndLast();
    }

    @Override
    public List<String> findIfEqualLength(String string) {
        return bTree.findIfEqualLength(string);
    }

    @Override
    public List<String> findIfLessThan(String string) {
        return bTree.findIfLessThan(string);
    }

    @Override
    public List<String> findIfMoreThan(String string) {
        return bTree.findIfMoreThan(string);
    }
}
