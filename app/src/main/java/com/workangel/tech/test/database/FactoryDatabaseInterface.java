package com.workangel.tech.test.database;

import com.workangel.tech.test.database.bean.Employee;

import java.util.List;

/**
 * Factory interface for the DB framework. The DB framework
 * must implements all the below methods
 */
public interface FactoryDatabaseInterface {

    /**
     * Return all the Employees saved on the DB
     * @return Employees list
     */
    public List<Employee> getAllEmployees();

    /**
     * Persist Employees on the DB
     * @param employees Employees to be saved
     */
    public void saveEmployees(final List<Employee> employees);
}
