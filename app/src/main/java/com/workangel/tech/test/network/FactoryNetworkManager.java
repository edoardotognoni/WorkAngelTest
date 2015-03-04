package com.workangel.tech.test.network;

import android.content.Context;

/**
 * Factory manager to get the right Network Framework
 */
public class FactoryNetworkManager  {
    private static final String TAG = FactoryNetworkManager.class.getSimpleName();

    /**
     * Enum of Network Framework type
     */
    public enum NetworkFramework {
        RETROFIT
    }

    public static FactoryNetworkManagerInterface getInstance(Context context, NetworkFramework framework) {
        switch (framework) {
            case RETROFIT:
                /** Since it's going to be a singleton, we MUST pass the application context or it could leak activities */
                return RetrofitNetworkManager.getInstance(context.getApplicationContext());
            default:
                return null;
        }
    }
}
