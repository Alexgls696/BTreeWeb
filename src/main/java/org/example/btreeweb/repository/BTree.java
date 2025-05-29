package org.example.btreeweb.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;


/*
Некоторые свойства B-дерева:

Сбалансированность. Длины любых двух путей от корня до листьев различаются не более, чем на единицу.
Ветвистость. Каждый узел дерева может ссылаться на большое число узлов-потомков.
Упорядоченность ключей. Ключи в каждом узле обычно упорядочены для быстрого доступа к ним.
Структура узла. Каждый узел B-дерева может содержать множество ключей (значений) и потомков. Количество ключей в узле определяется порядком B-дерева.
Указатели на потомков. У каждого узла есть указатели на его дочерние узлы. Количество указателей на потомков всегда на один больше, чем количество ключей в узле.
Диапазон данных в поддеревьях. Для любого заданного узла все ключи в левом поддереве меньше ключей в узле, а все ключи в правом поддереве больше ключей в узле.
Низкая высота дерева. Это свойство достигается за счёт того, что у узлов может быть несколько потомков.
Поддержка операций. B-дерево поддерживает несколько фундаментальных операций, включая поиск, добавление и удаление элементов.
*/

public class BTree {
    private final int MAX_KEYS_COUNT;
    private final int DEGREE;

    private int height = 1;

    public BTree(int degree) {
        this.DEGREE = degree;
        MAX_KEYS_COUNT = DEGREE * 2 - 1;
    }

    private BTreeNode root;

    @Getter
    @Setter
    @ToString
    public class BTreeNode {
        private List<String> keys = new ArrayList<>();
        private List<BTreeNode> children = new ArrayList<>();
        private boolean isLeaf;
        private boolean isSplit;

        //Для корня от 1 до 3 ключей
        //Если корень - лист, то 0 потомков
        //Если корень не лист - 2 - 4 потомка

        /*
        Для любого узла
        Минимальное число ключей t - 1
        Минимальное число потомков: t

        Максимальное число ключей 2t - 1 = 3
        Максимальное число потомков = 2t
         */

        public BTreeNode(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }

        public void addKey(String key) {
            if(!keys.contains(key)) {
                keys.add(key);
                keys = keys.stream()
                        .sorted((s1, s2) -> {
                            if (s1.length() > s2.length()) {
                                return 1;
                            } else {
                                if (s1.length() < s2.length()) {
                                    return -1;
                                }
                            }
                            return s1.compareTo(s2);
                        })
                        .collect(Collectors.toList());
            }
        }

        public boolean isCrowded() {
            return keys.size() >= MAX_KEYS_COUNT;
        }
    }

    private int compare(String s1, String s2) {
        if (s1.length() > s2.length()) {
            return 1;
        } else {
            if (s1.length() < s2.length()) {
                return -1;
            }
            return s1.compareTo(s2);
        }
    }

    private int lengthCompare(String s1, String s2) {
        return s1.length() - s2.length();
    }

    private BTreeNode splitNode(BTreeNode node) {
        int middleIndex = DEGREE / 2;
        var left = new BTreeNode(node.isLeaf);
        var right = new BTreeNode(node.isLeaf);

        for (int i = 0; i < middleIndex; i++) {
            left.addKey(node.keys.get(i));
        }
        for (int i = middleIndex + 1; i < node.keys.size(); i++) {
            right.addKey(node.keys.get(i));
        }

        if (!node.isLeaf) {
            for (int i = 0; i <= middleIndex; i++) {
                left.children.add(node.children.get(i));
            }
            for (int i = middleIndex + 1; i < node.children.size(); i++) {
                right.children.add(node.children.get(i));
            }
        }
        var middleKey = node.keys.get(middleIndex);
        var parent = new BTreeNode(false);
        parent.addKey(middleKey);
        parent.children.add(left);
        parent.children.add(right);

        return parent;
    }

    public void add(String key) {
        if (root == null) {
            root = new BTreeNode(true);
            root.addKey(key);
            return;
        }

        if (root.isCrowded()) {
            BTreeNode newRoot = new BTreeNode(false);
            newRoot.children.add(root);
            root = newRoot;
            root = splitNode(root.children.get(0));
            height++;
        }
        addRecursive(key, root);
    }

    private void addRecursive(String key, BTreeNode node) {
        if (node.isLeaf) {
            node.addKey(key);
            return;
        }

        int childIndex = 0;
        while (childIndex < node.keys.size() && compare(key, node.keys.get(childIndex)) > 0) {
            childIndex++;
        }

        BTreeNode child = node.children.get(childIndex);

        if (child.isCrowded()) {
            BTreeNode splitResult = splitNode(child);
            node.addKey(splitResult.keys.get(0));
            node.children.remove(childIndex);
            node.children.addAll(childIndex, splitResult.children);
            if (compare(key, splitResult.keys.get(0)) > 0) {
                childIndex++;
            }
            child = node.children.get(childIndex);
        }

        addRecursive(key, child);
    }

    public void remove(String key) {
        if (root == null) {
            return;
        }

        removeRecursive(key, root);

        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
            height--;
        }
    }

    private void removeRecursive(String key, BTreeNode node) {
        int keyIndex = findKeyIndex(node, key);

        if (keyIndex < node.keys.size() && compare(key, node.keys.get(keyIndex)) == 0) {
            if (node.isLeaf) {
                node.keys.remove(keyIndex);
            } else {
                BTreeNode leftChild = node.children.get(keyIndex);
                BTreeNode rightChild = node.children.get(keyIndex + 1);

                if (leftChild.keys.size() >= DEGREE) {
                    String predecessor = getPredecessor(leftChild);
                    node.keys.set(keyIndex, predecessor);
                    removeRecursive(predecessor, leftChild);
                } else if (rightChild.keys.size() >= DEGREE) {
                    String successor = getSuccessor(rightChild);
                    node.keys.set(keyIndex, successor);
                    removeRecursive(successor, rightChild);
                } else {
                    mergeNodes(node, keyIndex, leftChild, rightChild);
                    removeRecursive(key, leftChild);
                }
            }
        } else {
            if (node.isLeaf) {
                return;
            }

            boolean isLastChild = (keyIndex == node.children.size() - 1);
            BTreeNode child = node.children.get(keyIndex);

            if (child.keys.size() < DEGREE) {
                BTreeNode leftSibling = (keyIndex > 0) ? node.children.get(keyIndex - 1) : null;
                BTreeNode rightSibling = (!isLastChild) ? node.children.get(keyIndex + 1) : null;

                if (leftSibling != null && leftSibling.keys.size() >= DEGREE) {
                    borrowFromLeft(node, keyIndex - 1, leftSibling, child);
                } else if (rightSibling != null && rightSibling.keys.size() >= DEGREE) {
                    borrowFromRight(node, keyIndex, child, rightSibling);
                } else {
                    if (leftSibling != null) {
                        mergeNodes(node, keyIndex - 1, leftSibling, child);
                        child = leftSibling;
                    } else {
                        mergeNodes(node, keyIndex, child, rightSibling);
                    }
                }
            }

            removeRecursive(key, child);
        }
    }

// Вспомогательные методы

    private int findKeyIndex(BTreeNode node, String key) {
        int index = 0;
        while (index < node.keys.size() && compare(key, node.keys.get(index)) > 0) {
            index++;
        }
        return index;
    }

    private String getPredecessor(BTreeNode node) {
        while (!node.isLeaf) {
            node = node.children.get(node.children.size() - 1);
        }
        return node.keys.get(node.keys.size() - 1);
    }

    private String getSuccessor(BTreeNode node) {
        while (!node.isLeaf) {
            node = node.children.get(0);
        }
        return node.keys.get(0);
    }

    private void borrowFromLeft(BTreeNode parent, int parentKeyIndex, BTreeNode leftSibling, BTreeNode child) {
        child.keys.add(0, parent.keys.get(parentKeyIndex));
        parent.keys.set(parentKeyIndex, leftSibling.keys.remove(leftSibling.keys.size() - 1));
        if (!child.isLeaf) {
            child.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
        }
    }

    private void borrowFromRight(BTreeNode parent, int parentKeyIndex, BTreeNode child, BTreeNode rightSibling) {
        child.keys.add(parent.keys.get(parentKeyIndex));

        parent.keys.set(parentKeyIndex, rightSibling.keys.remove(0));

        if (!child.isLeaf) {
            child.children.add(rightSibling.children.remove(0));
        }
    }

    private void mergeNodes(BTreeNode parent, int parentKeyIndex, BTreeNode left, BTreeNode right) {
        left.keys.add(parent.keys.remove(parentKeyIndex));

        left.keys.addAll(right.keys);
        if (!left.isLeaf) {
            left.children.addAll(right.children);
        }

        parent.children.remove(parentKeyIndex + 1);
    }



    private void findIfLessThan(String string, BTreeNode node, List<String> list) {
        for (var str : node.keys) {
            if (compare(str, string) < 0) {
                list.add(str);
            }
        }
        for (var child : node.children) {
            findIfLessThan(string, child, list);
        }
    }

    public List<String> findIfLessThan(String string) {
        List<String> rows = new ArrayList<>();
        if (root != null) {
            findIfLessThan(string, root, rows);
        }
        return rows;
    }


    private void findIfMoreThan(String string, BTreeNode node, List<String> list) {
        for (var str : node.keys) {
            if (compare(str, string) > 0) {
                list.add(str);
            }
        }
        for (var child : node.children) {
            findIfMoreThan(string, child, list);
        }
    }

    public List<String> findIfMoreThan(String string) {
        List<String> rows = new ArrayList<>();
        if (root != null) {
            findIfMoreThan(string, root, rows);
        }
        return rows;
    }


    private void findIfEqualLength(String string, BTreeNode node, List<String> list) {
        for (var str : node.keys) {
            if (lengthCompare(str, string) == 0) {
                list.add(str);
            }
        }
        for (var child : node.children) {
            findIfEqualLength(string, child, list);
        }
    }

    public List<String> findIfEqualLength(String string) {
        List<String> rows = new ArrayList<>();
        if (root != null) {
            findIfEqualLength(string, root, rows);
        }
        return rows;
    }

    private void findBetween(String str1, String str2, BTreeNode node, List<String> list) {
        for (var str : node.keys) {
            if (compare(str, str1) > 0 && compare(str, str2) < 0) {
                list.add(str);
            }
        }
        for (var child : node.children) {
            findBetween(str1, str2, child, list);
        }
    }

    public List<String> findBetween(String str1, String str2) {
        List<String> rows = new ArrayList<>();
        if (root != null) {
            findBetween(str1, str2, root, rows);
        }
        return rows;
    }



    private String findFirst(BTreeNode node){
        if(node.isLeaf){
            return node.keys.get(0);
        }else{
            return findFirst(node.children.get(0));
        }
    }

    private String findLast(BTreeNode node) {
        if(node.isLeaf){
            return node.keys.get(node.keys.size()-1);
        }else{
            return findLast(node.children.get(node.children.size()-1));
        }
    }

    public List<String>findFirstAndLast(){
        List<String> rows = new ArrayList<>();
        if (root == null) {
            return rows;
        }
        rows.add(findFirst(root));
        rows.add(findLast(root));
        return rows;
    }


    private void print(BTreeNode node) {
        if (node.isLeaf) {
            node.keys.forEach(System.out::println);
        } else {
            for (int i = 0; i < node.children.size(); i++) {
                print(node.children.get(i));
                if (i < node.keys.size()) {
                    System.out.println(node.keys.get(i));
                }
            }
        }
    }

    public void print() {
        print(root);
    }


    private boolean isExists(String str, BTreeNode node) {
        boolean exists = false;
        if(node.isLeaf){
            exists = node.keys.contains(str);
        }else{
            for(var child : node.children) {
                if(child.keys.contains(str)){
                    return true;
                }
                exists =  isExists(str, child);
                if(exists){
                    return true;
                }
            }
        }
        return exists;
    }

    public boolean isExists(String str) {
        if(root==null){
            return false;
        }
        return isExists(str,root);
    }


    //В порядке возрастания элементов
    private void getAll(BTreeNode node, List<String>lines) {
        if (node.isLeaf) {
            lines.addAll(node.keys);
        } else {
            for (int i = 0; i < node.children.size(); i++) {
                getAll(node.children.get(i),lines);
                if (i < node.keys.size()) {
                    lines.add(node.keys.get(i));
                }
            }
        }
    }

    public List<String>getAll(){
        List<String>lines =  new ArrayList<>();
        if(root==null){
            return lines;
        }
        getAll(root,lines);
        return lines;
    }

    public Iterator<String>iterator(){
        return new Iterator<String>() {
            private final List<String>lines = getAll();

            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < lines.size();
            }

            @Override
            public String next() {
                return lines.get(index++);
            }
        };
    }


    private String checkLeftBrother(BTreeNode node){
        String key = null;
        if(node.keys.size() >= DEGREE){
            key = node.keys.get(node.keys.size()-1);
            node.keys.remove(node.keys.size()-1);
        }
        return key;
    }

    private String checkRightBrother(BTreeNode node){
        String key = null;
        if(node.keys.size() >= DEGREE){
            key = node.keys.get(0);
            node.keys.remove(0);
        }
        return key;
    }


    public void clear(){
        root = null;
    }
}
