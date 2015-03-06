package com.workangel.tech.test;

import android.support.v4.app.Fragment;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.Node;

import java.util.ArrayList;

public class MainActivityTest extends android.test.ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mMainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
        final Fragment myFragment = mMainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        /**
         * Be sure the fragment exists
         */
        assertNotNull(myFragment);
        if (myFragment instanceof FragmentListEmployees) {
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /**
                     * Test for null cases or empty cases
                     */
                    ((FragmentListEmployees) myFragment).update(null);
                    ((FragmentListEmployees) myFragment).update(new ArrayList<Employee>());
                    /**
                     * Simulate click on a fake Employee
                     */
                    Employee employee = new Employee();
                    Node treeNode = null;
                    assertNotNull(employee);
                    mMainActivity.onEvent(new MainActivity.EventTransactToEmployeeDetailFragment(employee, treeNode));
                    mMainActivity.onEvent(new MainActivity.EventTransactToEmployeeDetailFragment(employee, new Node()));
                    mMainActivity.onEvent(new MainActivity.EventTransactToEmployeeDetailFragment(null, new Node()));
                }
            });
        }
    }


    public void testPreConditions() {
        assertNotNull(mMainActivity);
    }

}