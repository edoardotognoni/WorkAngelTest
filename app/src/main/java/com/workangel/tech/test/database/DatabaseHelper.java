package com.workangel.tech.test.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.workangel.tech.test.database.bean.Employee;

/**
 * Class manager to access Database beans. This class is built up upon ORMLite Helper.
 * This implements the Singleton pattern
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    // name of the database file
    private static final String DATABASE_NAME = "work_angel.sqlite";
    private Context mContext;
    /** This is used to identify DB version. When DB needs to be changed, this must be increased in order for {@link #onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)} to be called */
    private static final int DATABASE_VERSION = 1;
    private Dao<Employee, Integer> mEmployeesDao = null;

    private static DatabaseHelper mDatabaseHelper;

    /**
     * Get DatabaseHelper instance
     * @param context Context
     * @return DatabaseHelper
     */
    protected static DatabaseHelper getInstance(Context context) {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return mDatabaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Initializes DB. Get's called at first creation of the DB
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Employee.class);
        }
        catch (java.sql.SQLException e) {
            throw new RuntimeException("Database can't be created!", e);
        }

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            //Do all the changes that has been implemented on version 2 of the DB
        }

    }

    public Dao<Employee, Integer> getEmployeesDao() {
        if (null == mEmployeesDao) {
            try {
                mEmployeesDao = getDao(Employee.class);
            }
            catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return mEmployeesDao;
    }
}

