package com.workangel.tech.test.network;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;

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
    public void getEmployees(Callback<Response> responseCallback);


}
