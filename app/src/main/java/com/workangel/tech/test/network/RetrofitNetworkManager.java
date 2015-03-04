package com.workangel.tech.test.network;

import android.content.Context;
import com.workangel.tech.test.BuildConfig;
import com.workangel.tech.test.R;
import com.workangel.tech.test.database.bean.Employee;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

        /** Create retrofit rest adapter */
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(endPoint)
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
}
