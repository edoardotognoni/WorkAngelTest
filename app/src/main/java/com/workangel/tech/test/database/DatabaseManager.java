package com.workangel.tech.test.database;

import android.content.Context;
import android.util.Log;
import com.workangel.tech.test.database.bean.Employee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used as a sort of wrapper upon the real DB type and structure. This is useful
 * in the case we need to change the DB type or DB technologies. The app will use only this class
 * to access databases objects.
 *
 * Implements Singleton pattern
 */
public class DatabaseManager implements FactoryDatabaseInterface{
    private static final String TAG = DatabaseManager.class.getSimpleName();

    /** DatabaseManager instance */
    private static DatabaseManager sInstance;
    /** Application context */
    private Context mCtx;
    /** Database helper instance */
    private DatabaseHelper helper;


    /** Get instance as Singleton pattern */
    protected static DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseManager(Context ctx) {
        mCtx = ctx;
        helper = DatabaseHelper.getInstance(ctx);
    }

    /**
     * Get all employess saved on DB
     * @return Full employees list
     */
    public List<Employee> getAllEmployees() {
        try {
            return helper.getEmployeesDao().queryForAll();
        }
        catch (SQLException e) {
            Log.e(TAG,"Can't retrieve DB objects. Nevermind, we'll retreive them from network");
            e.printStackTrace();
        }

        //Better return an empty object to avoid NPE
        return new ArrayList<>();
    }


    /**
     * Persists the employees on DB
     * @param employees Employees list
     */
    public void saveEmployees(final List<Employee> employees) {
        try {
            for (Employee employee : employees) {
                helper.getEmployeesDao().createOrUpdate(employee);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
