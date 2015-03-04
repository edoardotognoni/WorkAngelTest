package com.workangel.tech.test.network;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workangel.tech.test.BuildConfig;
import com.workangel.tech.test.R;
import com.workangel.tech.test.database.bean.Employee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrofit Network manager implementing Singleton pattern. This object is used to make all the API calls
 */
public class RetrofitNetworkManager implements FactoryNetworkManagerInterface {
    private static final String TAG = RetrofitNetworkManager.class.getSimpleName();
    /** Singleton instance object */
    private static RetrofitNetworkManager sWorkAngelTestNetworkManager;
    private final Context mContext;
    private final RetrofitNetwrokInterface mRetrofitNetwrokInterface;

    public RetrofitNetworkManager(Context applicationContext) {
        mContext = applicationContext;
        String endPoint = mContext.getResources().getString(R.string.base_url);

        /** Custom converter */
        Gson gson = new GsonBuilder()
            .setDateFormat(Constants.DATE_FORMAT)
            .create();

        /** Create retrofit rest adapter */
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(endPoint)
            .setConverter(new GsonConverter(gson))
            .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
            .build();

        mRetrofitNetwrokInterface = restAdapter.create(RetrofitNetwrokInterface.class);
    }


    /**
     * Get Retrofit network instance
     * @param context Context
     * @return RetrofitNetworkManager instance
     */
    protected static RetrofitNetworkManager getInstance(Context context) {
        if (sWorkAngelTestNetworkManager == null) {
            sWorkAngelTestNetworkManager = new RetrofitNetworkManager(context.getApplicationContext());
        }

        return sWorkAngelTestNetworkManager;
    }

    @Override
    public void getAllEmployees(final ResultCallback<List<Employee>> responseCallback) {
        mRetrofitNetwrokInterface.getEmployees(new Callback<List<Employee>>() {
            @Override
            public void success(List<Employee> employees, Response response) {
                responseCallback.onSuccess(employees);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                switch (error.getKind()) {
                    case NETWORK:
                        responseCallback.onError(Constants.NETWORK_PROBLEM);
                        break;
                    case CONVERSION:
                        responseCallback.onError(Constants.CONVERSION_PROBLEM);
                        break;
                    case HTTP:
                        responseCallback.onError(Constants.NETWORK_PROBLEM);
                        break;
                    case UNEXPECTED:
                        responseCallback.onError(Constants.UNEXPECTED_ERROR);
                        break;
                }
            }
        });
    }

    /**
     * Parse JSON array to get Employees list
     * @return Employees list
     */
    private List<Employee> getEmployeesFromJson(JSONArray employees, ResultCallback<List<Employee>> responseCallback) {
        List<Employee> employeesList = new ArrayList<>();
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        for (int i = 0; i < employees.length(); i++) {
            try {
                JSONObject employee = employees.getJSONObject(i);
                Employee employeeBean = gson.fromJson(employee.toString(),Employee.class);
                if (employee.has("name")) {
                    JSONObject nameObject = employee.getJSONObject("name");
                    if (nameObject.has("first")) {
                        employeeBean.setFirstName(nameObject.getString("first"));
                    }
                    if (nameObject.has("last")) {
                        employeeBean.setLastName(nameObject.getString("last"));
                    }
                }

                employeesList.add(employeeBean);
            }
            catch (JSONException e) {
                // There was a problem in converting Employees JSON
                e.printStackTrace();
                responseCallback.onError(Constants.CONVERSION_PROBLEM);
                return null;
            }
        }

        return employeesList;
    }


}
