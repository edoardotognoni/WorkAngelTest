package com.workangel.tech.test;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.workangel.tech.test.database.FactoryDatabaseInterface;
import com.workangel.tech.test.database.FactoryDatabaseManager;
import com.workangel.tech.test.database.bean.Employee;

import java.util.List;

/**
 * Created by edoardo on 04/03/2015.
 */
public class EmployeesLoader extends AsyncTaskLoader<List<Employee>> {

    /**
     * Create the Loader
     * @param context Context
     */
    public EmployeesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Employee> loadInBackground() {
        FactoryDatabaseInterface dbManager = FactoryDatabaseManager.getInstance(getContext(),
                                                                                FactoryDatabaseManager.DatabaseFramework.ORMLITE);
        List<Employee> employees = dbManager.getAllEmployees();
        return employees;
    }

}
