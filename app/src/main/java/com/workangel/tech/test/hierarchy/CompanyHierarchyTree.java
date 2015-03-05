package com.workangel.tech.test.hierarchy;

import android.util.Log;
import com.workangel.tech.test.database.bean.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tree representing company's hierarchy
 */
public class CompanyHierarchyTree {
    private static final String TAG = CompanyHierarchyTree.class.getSimpleName();

    /**
     * Map of all the tree nodes. Each key is the Employee ID and the node is the
     * actual employee data including its children and parent
     */
    private Map<Integer,Node<Employee>> mTreeMap = new HashMap<>();

    /**
     * Creates and build the hierarchy tree
     * @param employees Employees list
     */
    public CompanyHierarchyTree(List<Employee> employees) {
        build(employees);
    }

    /**
     * Builds the Hierarchy Tree. Copy all the Bean into a Map first, then iterate the Bean list setting
     * parents and children of each node
     */
    private void build(List<Employee> employees) {
        //Populate first empty map
        for (Employee employee : employees) {
            Node<Employee> node = new Node<>();
            node.setData(employee);
            mTreeMap.put(employee.get_id(), node);
        }

        //Re - iterate list to build the actual tree with children and parents
        for (Employee employee : employees) {
            Node<Employee> node = mTreeMap.get(employee.get_id());
            //Set parent into node children
            for (int id : employee.getSubordinates()) {
                Node<Employee> childNode = mTreeMap.get(id);
                childNode.setParent(node);
                List<Node<Employee>> chidlrenList = node.getChildren();
                if (chidlrenList == null) {
                    chidlrenList = new ArrayList<>();
                    node.setChildren(chidlrenList);
                }
                chidlrenList.add(childNode);
            }
        }

        Log.d(TAG,"Tree created");
    }

    public Node<Employee> getNode(int id) {
        return mTreeMap.get(id);
    }
}
