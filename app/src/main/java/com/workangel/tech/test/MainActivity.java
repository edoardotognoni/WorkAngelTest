package com.workangel.tech.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.Node;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        });
        getSupportActionBar().setIcon(R.drawable.toolbar_icon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Check if fragment has to be recreated or not
        Fragment fragment =  getSupportFragmentManager().findFragmentById(
            R.id.fragment_container);

        if (fragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new FragmentListEmployees());
            fragmentTransaction.commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //After orientation change, check how many fragments we have
        onBackStackChanged();
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
        bundle.putParcelable(FragmentEmployeeDetail.KEY_NODE, eventTransactToEmployeeDetailFragment.getNode());
        employeeDetail.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.fragment_container, employeeDetail)
                                   .addToBackStack(null)
                                   .commit();
    }

    /**
     * Called when something in the fragments backstack changes. If the fragment backstack has more than 0 elements
     * show the home button
     */
    @Override
    public void onBackStackChanged() {
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backstackCount == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Sending an EventTransactToEmployeeDetailFragment lets you move to the
     * Employee detail. Used to make the fragment transaction
     */
    public static class EventTransactToEmployeeDetailFragment {
        private Employee mEmployee;
        private Node mNode;

        public EventTransactToEmployeeDetailFragment(Employee employee, Node node) {
            mEmployee = employee;
            mNode = node;
        }

        public Employee getEmployee() {
            return mEmployee;
        }

        public Node getNode() {
            return mNode;
        }

        public void setNode(Node node) {
            mNode = node;
        }
    }
}
