package com.workangel.tech.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.workangel.tech.test.contacts.ContactsSaverIntentService;
import com.workangel.tech.test.database.FactoryDatabaseInterface;
import com.workangel.tech.test.database.FactoryDatabaseManager;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.Node;
import com.workangel.tech.test.network.Constants;
import com.workangel.tech.test.network.FactoryNetworkManager;
import com.workangel.tech.test.network.FactoryNetworkManagerInterface;
import com.workangel.tech.test.network.ResultCallback;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_DOWNLOADED_ONCE = "key_downloaded_once";
    private ProgressDialog mLoadingDialog;

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

        //Create a loading dialog for the first time we open the app
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setMessage(getString(R.string.loading));
        mLoadingDialog.setCancelable(false);

        //Check if fragment has to be recreated or not
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            FragmentListEmployees fragmentListEmployees = new FragmentListEmployees();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragmentListEmployees);
            fragmentTransaction.commit();

            if (!UserDataManager.getDownloadedOnce(this)) {
                //We still don't have available data. Show a loading dialog until we get some
                mLoadingDialog.show();
            }

            //Make the API call to get employees data
            FactoryNetworkManagerInterface networkManagerInterface = FactoryNetworkManager.getInstance(this,
                                                                                                       FactoryNetworkManager.NetworkFramework.RETROFIT);


            networkManagerInterface.getAllEmployees(new ResultCallback<List<Employee>>() {

                @Override
                public void onSuccess(List<Employee> result) {
                    Log.d(TAG, "Employees downloaded successfully");
                    //Save that we downloaded at least once
                    UserDataManager.setDownloadedOnce(MainActivity.this, true);

                    //Save contacts?
                    if (!UserDataManager.getStopAskingForSavingContacts(MainActivity.this)) {
                        askToSaveContacts(result);
                    }
                    //Save employees to DB
                    saveEmployeesAsync(result);
                }

                @Override
                public void onError(int errorCode) {
                    if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }

                    switch (errorCode) {
                        case Constants.CONVERSION_PROBLEM:
                        case Constants.UNEXPECTED_ERROR:
                            Crouton.makeText(MainActivity.this, getString(R.string.data_invalid_contact_server_admin),
                                             Style.ALERT).show();
                            break;
                        case Constants.NETWORK_PROBLEM:
                            Crouton.makeText(MainActivity.this, getString(R.string.error_network_problem),
                                             Style.ALERT).show();
                            break;
                    }
                }
            });
        }
        else {
            //Check for tablet in landscape
            if (findViewById(R.id.fragment_container_2) != null) {
                //Clear back stack
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (fragment instanceof FragmentEmployeeDetail) {
                    FragmentListEmployees fragmentListEmployees = new FragmentListEmployees();
                    FragmentEmployeeDetail detail = new FragmentEmployeeDetail();
                    detail.setArguments(fragment.getArguments());
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragmentListEmployees);
                    fragmentTransaction.replace(R.id.fragment_container_2, detail);
                    fragmentTransaction.commit();
                }
            }
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //After orientation change, check how many fragments we have
        onBackStackChanged();
    }

    /**
     * Saves new employees on a background thread and restart the loader. It's important
     * to make it run on a Bg thread otherwise it could freeze the UI. And we don't want this.
     *
     * @param employees Employees
     */
    private void saveEmployeesAsync(final List<Employee> employees) {
        //Use AsyncTask because its onPostExecute method is useful in this case. It runs on the UI thread
        // so we can easily update contents
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                FactoryDatabaseInterface dbManager = FactoryDatabaseManager.getInstance(MainActivity.this,
                                                                                        FactoryDatabaseManager
                                                                                            .DatabaseFramework.ORMLITE);

                if (employees != null) {
                    dbManager.saveEmployees(employees);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //Update FragmentListEmployees if it's in the top, otherwise it will load
                //new data from the DB by itself
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment instanceof FragmentListEmployees) {
                    ((FragmentListEmployees) fragment).update(employees);
                }
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    /**
     * Shows a dialog to the user which will ask if he wants employees to be
     * saved on its contacts
     *
     * @param employees Employees to be saved
     */
    private void askToSaveContacts(final List<Employee> employees) {
        View confirmationDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ask_save_contact, null);
        final CheckBox dontAskAgain = (CheckBox) confirmationDialogView.findViewById(R.id.dont_ask_again);
        AlertDialog confirmationDialog = new AlertDialog.Builder(this)
            .setView(confirmationDialogView)
            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontAskAgain.isChecked()) {
                        UserDataManager.setStopAskingForSavingContacts(MainActivity.this, true);
                    }
                    //Starts service which will save contacts
                    Intent intent = new Intent(MainActivity.this, ContactsSaverIntentService.class);
                    intent.putParcelableArrayListExtra(ContactsSaverIntentService.KEY_EMPLOYEES_LIST,
                                                       (ArrayList<? extends Parcelable>) employees);
                    startService(intent);
                }
            })
            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontAskAgain.isChecked()) {
                        UserDataManager.setStopAskingForSavingContacts(MainActivity.this, true);
                    }
                }
            })
            .create();

        confirmationDialog.show();

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
     * Called by EventBus when someone fires an {@link com.workangel.tech.test.MainActivity
     * .EventTransactToEmployeeDetailFragment}
     *
     * @param eventTransactToEmployeeDetailFragment Event
     */
    public void onEvent(EventTransactToEmployeeDetailFragment eventTransactToEmployeeDetailFragment) {
        View fragmentContainer2 = findViewById(R.id.fragment_container_2);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        /**
         * Check if we are running on a tablet in landscape. If so, just put the next fragment
         * in the other container
         */
        int resource = R.id.fragment_container;
        if (fragmentContainer2 != null) {
            //If we are on tablets in landscape we don't need to add to backstack
            resource = R.id.fragment_container_2;
        }
        else {
            transaction.addToBackStack(null);
        }
        Fragment employeeDetail = new FragmentEmployeeDetail();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FragmentEmployeeDetail.KEY_EMPLOYEE, eventTransactToEmployeeDetailFragment.getEmployee());
        bundle.putParcelable(FragmentEmployeeDetail.KEY_NODE, eventTransactToEmployeeDetailFragment.getNode());
        employeeDetail.setArguments(bundle);
        transaction.replace(resource, employeeDetail)
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

        /**
         * Constructor
         *
         * @param employee Employee to be displayed
         * @param node     Node representing the Employee in the Hierarchy tree
         */
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
