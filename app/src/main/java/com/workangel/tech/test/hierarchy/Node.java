package com.workangel.tech.test.hierarchy;

import java.util.List;

/**
 * Node of a hierarchy tree. Each Node holds a reference to its parent and its children
 */
public class Node<T> {
    private static final String TAG = Node.class.getSimpleName();
    /**
     * Node data
     */
    private T data;
    /**
     * Parent node
     */
    private Node<T> parent;
    /**
     * Children nodes
     */
    private List<Node<T>> children;



    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
