package com.workangel.tech.test.network;

import com.workangel.tech.test.database.bean.Employee;

import java.util.List;

/**
 * Created by opossum on 04/03/15.
 */
public interface FactoryNetworkManagerInterface {

    /**
     * Make the API call to get the full Employees list
     * @param responseCallback Callback called in case of success or failure
     */
    public void getAllEmployees(final ResultCallback<List<Employee>> responseCallback);
}
