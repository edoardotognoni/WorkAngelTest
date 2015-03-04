package com.workangel.tech.test;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.network.FactoryNetworkManager;
import com.workangel.tech.test.network.ResultCallback;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FactoryNetworkManager.getInstance(this, FactoryNetworkManager.NetworkFramework.RETROFIT).getAllEmployees(new ResultCallback<List<Employee>>() {

            @Override
            public void onSuccess(List<Employee> result) {
                Log.e(TAG,"Success");
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }


}
