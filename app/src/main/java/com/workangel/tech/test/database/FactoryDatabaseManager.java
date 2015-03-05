package com.workangel.tech.test.database;

import android.content.Context;

/**
 * Factory manager to get the right DB Framework
 */
public class FactoryDatabaseManager {

    /**
     * Enum of DB Framework type
     */
    public enum DatabaseFramework {
        ORMLITE
    }

    public static FactoryDatabaseInterface getInstance(Context context, DatabaseFramework framework) {
        switch (framework) {
            case ORMLITE:
                /** Since it's going to be a singleton, we MUST pass the application context or it could leak activities */
                return DatabaseManager.getInstance(context.getApplicationContext());
            default:
                return null;
        }
    }
}
