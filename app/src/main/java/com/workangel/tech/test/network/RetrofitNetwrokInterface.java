package com.workangel.tech.test.network;

import com.workangel.tech.test.database.bean.Employee;
import retrofit.Callback;
import retrofit.http.GET;

import java.util.List;

/**
 * Network manager implementing Singleton pattern. I chose to use this pattern in case we need
 * to switch Network Framework in the future. This is a sort of Factory pattern
 */
public interface RetrofitNetwrokInterface {

    /**
     * REST call to get all employees
     * @param responseCallback Callback called in case of success or failure
     */
    @GET("/get/HH-iNTU")
    public void getEmployees(Callback<List<Employee>> responseCallback);


}
