package org.example.btreeweb.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/*
Некоторые свойства B-дерева:

Сбалансированность. Длины любых двух путей от корня до листьев различаются не более, чем на единицу.  23
Ветвистость. Каждый узел дерева может ссылаться на большое число узлов-потомков.  3
Упорядоченность ключей. Ключи в каждом узле обычно упорядочены для быстрого доступа к ним.  3
Структура узла. Каждый узел B-дерева может содержать множество ключей (значений) и потомков. Количество ключей в узле определяется порядком B-дерева.  21
Указатели на потомков. У каждого узла есть указатели на его дочерние узлы. Количество указателей на потомков всегда на один больше, чем количество ключей в узле.  2
Диапазон данных в поддеревьях. Для любого заданного узла все ключи в левом поддереве меньше ключей в узле, а все ключи в правом поддереве больше ключей в узле.  2
Низкая высота дерева. Это свойство достигается за счёт того, что у узлов может быть несколько потомков.  2
Поддержка операций. B-дерево поддерживает несколько фундаментальных операций, включая поиск, добавление и удаление элементов. 2
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

    private boolean remove(String key, BTreeNode node) {
        boolean result = false;
        for(int i = 0; i < node.children.size(); i++) {
            var child = node.children.get(i);
            if(child.keys.contains(key)){
                if(child.keys.size() >= DEGREE){
                    result = child.keys.remove(key);
                }else{
                    String brotherKey = checkLeftBrother(node.children.get(i-1));
                    if(brotherKey==null){
                        brotherKey = checkRightBrother(node.children.get(i+1));
                    }
                    if(brotherKey==null){ //Если оба брата не имеют достаточного числа ключей
                        String parentKey = node.keys.get(i-1);
                        node.keys.remove(i-1);

                    }else{
                        child.addKey(brotherKey);
                    }
                }
                break;
            }
            result = remove(key, child);
        }
        return result;
    }
    public boolean remove(String key){
        boolean result = false;
        if(root==null){
            return false;
        }
        return remove(key,root);
    }

    public void clear(){
        root = null;
    }
}
