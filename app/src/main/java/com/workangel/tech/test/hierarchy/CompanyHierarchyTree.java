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
    private List<Employee> mEmployees;

    private Map<Integer,Node<Employee>> mTreeMap = new HashMap<>();
    /**
     * Full employees list
     * @param employees
     */
    public CompanyHierarchyTree(List<Employee> employees) {
        mEmployees = employees;
        build();
    }

    /**
     * Builds the Hierarchy Tree. Copy all the Bean into a Map first, then iterate the Bean list setting
     * parents and children of each node
     */
    private void build() {
        //Populate first empty map
        for (Employee employee : mEmployees) {
            Node<Employee> node = new Node<>();
            node.setData(employee);
            mTreeMap.put(employee.get_id(), node);
        }

        //Re - iterate list to build the actual tree with children and parents
        for (Employee employee : mEmployees) {
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
}
