package com.workangel.tech.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import com.workangel.tech.test.database.bean.Employee;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Check if fragment has to be recreated or not
        FragmentListEmployees fragment = (FragmentListEmployees) getSupportFragmentManager().findFragmentById(
            R.id.fragment_container);

        if (fragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new FragmentListEmployees());
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Register to eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Unregister from eventbus
        EventBus.getDefault().unregister(this);
    }


    /**
     * Called by EventBus when someone fires an {@link com.workangel.tech.test.MainActivity.EventTransactToEmployeeDetailFragment}
     * @param eventTransactToEmployeeDetailFragment Event
     */
    public void onEvent(EventTransactToEmployeeDetailFragment eventTransactToEmployeeDetailFragment) {
        Fragment employeeDetail = new FragmentEmployeeDetail();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FragmentEmployeeDetail.KEY_EMPLOYEE, eventTransactToEmployeeDetailFragment.getEmployee());
        employeeDetail.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, employeeDetail)
                                   .addToBackStack(null)
                                   .commit();
    }
    /**
     * Sending an EventTransactToEmployeeDetailFragment lets you move to the
     * Employee detail. Used to make the fragment transaction
     */
    public static class EventTransactToEmployeeDetailFragment {
        private Employee mEmployee;

        public EventTransactToEmployeeDetailFragment(Employee employee) {
            mEmployee = employee;
        }

        public Employee getEmployee() {
            return mEmployee;
        }
    }
}
