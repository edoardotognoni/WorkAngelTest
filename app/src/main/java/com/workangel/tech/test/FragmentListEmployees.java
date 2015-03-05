package com.workangel.tech.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.workangel.tech.test.contacts.ContactsSaverIntentService;
import com.workangel.tech.test.database.FactoryDatabaseInterface;
import com.workangel.tech.test.database.FactoryDatabaseManager;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.CompanyHierarchyTree;
import com.workangel.tech.test.hierarchy.Node;
import com.workangel.tech.test.network.Constants;
import com.workangel.tech.test.network.FactoryNetworkManager;
import com.workangel.tech.test.network.FactoryNetworkManagerInterface;
import com.workangel.tech.test.network.ResultCallback;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.*;

/**
 * Fragment showing the Employees list. It uses the LoaderManager pattern to retrieve
 * data from the DB.
 */
public class FragmentListEmployees extends Fragment implements LoaderManager.LoaderCallbacks<List<Employee>>{
    private static final String TAG = FragmentListEmployees.class.getSimpleName();
    private ListView mEmployeesListView;
    private int mDepartmentSpinnerSelection = 0;
    private String mQueryName = "";

    public static final int EMPLOYEES_LOADER_ID = 1000;
    public static final String KEY_DOWNLOADED_ONCE = "key_downloaded_once";
    /**
     * Keys used to retain the list on orientation changes
     */
    public static final String KEY_EMPLOYEES_LIST = "key_employees_list";
    public static final String KEY_SPINNER_DEPT_SELECTION = "key_spinner_selection";
    public static final String KEY_QUERY_NAME = "key_query_name";
    /**
     * Full list of employees
     */
    private List<Employee> mEmployeesList;
    /**
     * Map where keys are department strings and values are their respective employees
     */
    private Map<String,List<Employee>> mDepartmentEmployeesMap;
    private Spinner mDepartmentSpinner;
    private SearchView mEmployeesSearch;
    /**
     * This object represents the company's hierarchy tree.
     */
    private CompanyHierarchyTree mCompanyTree;
    private ProgressDialog mLoadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employess_list, container, false);

        mEmployeesListView = (ListView) root.findViewById(R.id.employees_list);
        mDepartmentSpinner = (Spinner) root.findViewById(R.id.department_spinner);
        mEmployeesSearch = (SearchView) root.findViewById(R.id.employees_search);

        //Create a loading dialog for the first time we open the app
        mLoadingDialog = new ProgressDialog(getActivity());
        mLoadingDialog.setMessage(getString(R.string.loading));
        mLoadingDialog.setCancelable(false);


        mDepartmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //When setOnItemSelectedListener is set, it auto calls onItemSelected at first. I don't want
            // this behaviour so I hadd a bit of logic to call the method only when the user
            // actually selects the item
            boolean isLoaded = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDepartmentSpinnerSelection = position;
                if (!isLoaded) {
                    isLoaded = true;
                    return;
                }
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEmployeesSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQueryName = newText;
                //After orientation changes this method is called before everything. We'll wait for
                // the instance to be restored
                if (mDepartmentEmployeesMap != null) {
                    filter();
                }
                return true;
            }
        });

        /**
         * Set a click listener to move to the employee detail fragment
         */
        mEmployeesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Employee employee = (Employee) parent.getItemAtPosition(position);
                Node treeNode = mCompanyTree.getNode(employee.get_id());
                EventBus.getDefault().post(new MainActivity.EventTransactToEmployeeDetailFragment(employee,treeNode));
            }
        });
        /**
         * If we come from a configuration change, don't make a new call. We don't need to
         * call the API every time we change from portrait to landscape and viceversa
         */
        if (savedInstanceState != null) {
            mEmployeesList = savedInstanceState.getParcelableArrayList(KEY_EMPLOYEES_LIST);
            mDepartmentSpinnerSelection = savedInstanceState.getInt(KEY_SPINNER_DEPT_SELECTION);
            mQueryName = savedInstanceState.getString(KEY_QUERY_NAME);
        }
        else {
            if (!UserDataManager.getDownloadedOnce(getActivity())) {
                //We still don't have available data. Show a loading dialog until we get some
                mLoadingDialog.show();
            }

            //Make the API call to get employees data
            FactoryNetworkManagerInterface networkManagerInterface = FactoryNetworkManager.getInstance(getActivity(),
                                                                                                       FactoryNetworkManager.NetworkFramework.RETROFIT);


            networkManagerInterface.getAllEmployees(new ResultCallback<List<Employee>>() {

                @Override
                public void onSuccess(List<Employee> result) {
                    Log.d(TAG, "Employees downloaded successfully");
                    if (isAdded()) {
                        //Save that we downloaded at least once
                        UserDataManager.setDownloadedOnce(getActivity(), true);

                        //Save contacts?
                        if (!UserDataManager.getStopAskingForSavingContacts(getActivity())) {
                            askToSaveContacts(result);
                        }
                        //Save emplyees to DB
                        saveEmployeesAsync(result);
                    }
                }

                @Override
                public void onError(int errorCode) {
                    if (isAdded()) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }

                        switch (errorCode) {
                            case Constants.CONVERSION_PROBLEM:
                            case Constants.UNEXPECTED_ERROR:
                                Crouton.makeText(getActivity(), getString(R.string.data_invalid_contact_server_admin),
                                                 Style.ALERT).show();
                                break;
                            case Constants.NETWORK_PROBLEM:
                                Crouton.makeText(getActivity(), getString(R.string.error_network_problem),
                                                 Style.ALERT).show();
                                break;
                        }
                    }
                }
            });

        }

        /**
         * While we wait for the API call to finish, we show old data from DB
         */
        getLoaderManager().initLoader(EMPLOYEES_LOADER_ID, null, this).forceLoad();

        return root;
    }


    private void filter() {
        //First filter for department
        String department = (String) mDepartmentSpinner.getItemAtPosition(mDepartmentSpinnerSelection);
        List<Employee> employeesForDeptChosen = mDepartmentEmployeesMap.get(department);

        //Get employee for text query filter
        List<Employee> employeeFiltered = getEmployeesFilteredForName(employeesForDeptChosen, mQueryName);

        //Update UI
        EmployeesListAdapter adapter = (EmployeesListAdapter) mEmployeesListView.getAdapter();
        if (adapter == null) {
            adapter = new EmployeesListAdapter(getActivity(),employeeFiltered);
            mEmployeesListView.setAdapter(adapter);
        }
        else {
            adapter.setEmployees(employeeFiltered);
            adapter.notifyDataSetChanged();
        }
    }
    /**
     * Build a map where keys are Department String and values are Lists of all
     * the Employees of that department
     * @param fullEmployeesList
     */
    private void buildDepartmentMap(List<Employee> fullEmployeesList) {
        mDepartmentEmployeesMap = new LinkedHashMap<>();
        //Here I add the ALL department key
        mDepartmentEmployeesMap.put(getString(R.string.all_departments),fullEmployeesList);
        for (Employee employee : fullEmployeesList) {
            List<Employee> employeesForDept = mDepartmentEmployeesMap.get(employee.getDepartment());
            if (employeesForDept == null) {
                employeesForDept = new ArrayList<>();
                mDepartmentEmployeesMap.put(employee.getDepartment(),employeesForDept);
            }

            employeesForDept.add(employee);
        }

        //Populate department spinner withouth losing previous selection
        DepartmentSpinnerAdapter adapter = (DepartmentSpinnerAdapter) mDepartmentSpinner.getAdapter();
        if (adapter == null) {
            adapter = new DepartmentSpinnerAdapter(getActivity(),mDepartmentEmployeesMap);
            mDepartmentSpinner.setAdapter(adapter);
        }
        else {
            adapter.setEmployeesDeptMap(mDepartmentEmployeesMap);
            adapter.notifyDataSetChanged();
        }

        //If we come from a configuration change, we set the old values to the spinner and searchview
        //At first launch this set the selection to 0 and set an empty string. Sounds good to me.
        mDepartmentSpinner.setSelection(mDepartmentSpinnerSelection);
        mEmployeesSearch.setQuery(mQueryName,false);
        //Show filter views
        mDepartmentSpinner.setVisibility(View.VISIBLE);
        mEmployeesSearch.setVisibility(View.VISIBLE);
    }

    /**
     * Filter the employees list and returns a list of employees filtered by name
     * @param employees Employees list input
     * @param filter String filter
     * @return List employees filtered
     */
    private List<Employee> getEmployeesFilteredForName(List<Employee> employees, String filter) {
        if (TextUtils.isEmpty(filter)) {
            return employees;
        }
        else {
            List<Employee> filteredEmployees = new ArrayList<>();
            for (Employee  employee : employees) {
                String name = (employee.getLastName() + " " + employee.getFirstName()).toUpperCase(Locale.US);
                if (name.contains(filter.toUpperCase(Locale.US))) {
                    filteredEmployees.add(employee);
                }
            }
            return filteredEmployees;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_EMPLOYEES_LIST,
                                        (java.util.ArrayList<? extends android.os.Parcelable>) mEmployeesList);
        outState.putInt(KEY_SPINNER_DEPT_SELECTION, mDepartmentSpinnerSelection);
        outState.putString(KEY_QUERY_NAME, mQueryName);
    }

    /**
     * Saves new employees on a background thread and restart the loader. It's important
     * to make it run on a Bg thread otherwise it could freeze the UI. And we don't want this.
     * @param employees Employees
     */
    private void saveEmployeesAsync(final List<Employee> employees) {
        //Use AsyncTask because its onPostExecute method is useful in this case. It runs on the UI thread
        // so we can easily update contents
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                FactoryDatabaseInterface dbManager = FactoryDatabaseManager.getInstance(getActivity(),
                                                                                        FactoryDatabaseManager
                                                                                            .DatabaseFramework.ORMLITE);

                dbManager.saveEmployees(employees);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isAdded()) {
                    //Update UI by restarting loader
                    getLoaderManager().restartLoader(EMPLOYEES_LOADER_ID, null, FragmentListEmployees.this).forceLoad();
                    if (mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                }
                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    /**
     * Shows a dialog to the user which will ask if he wants employees to be
     * saved on its contacts
     * @param employees Employees to be saved
     */
    private void askToSaveContacts(final List<Employee> employees) {
        View confirmationDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_ask_save_contact, null);
        final CheckBox dontAskAgain = (CheckBox) confirmationDialogView.findViewById(R.id.dont_ask_again);
        AlertDialog confirmationDialog = new AlertDialog.Builder(getActivity())
            .setView(confirmationDialogView)
            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontAskAgain.isChecked()) {
                        UserDataManager.setStopAskingForSavingContacts(getActivity(),true);
                    }
                    //Starts service which will save contacts
                    Intent intent = new Intent(getActivity(), ContactsSaverIntentService.class);
                    intent.putParcelableArrayListExtra(ContactsSaverIntentService.KEY_EMPLOYEES_LIST,
                                                       (ArrayList<? extends android.os.Parcelable>) employees);
                    getActivity().startService(intent);
                }
            })
            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontAskAgain.isChecked()) {
                        UserDataManager.setStopAskingForSavingContacts(getActivity(),true);
                    }
                }
            })
            .create();

        confirmationDialog.show();

    }


    @Override
    public Loader<List<Employee>> onCreateLoader(int id, Bundle args) {
        return new EmployeesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Employee>> loader, List<Employee> data) {
        mEmployeesList = data;
        if (isAdded()) {
            //Build map Department-Employees
            buildDepartmentMap(mEmployeesList);
            mCompanyTree = new CompanyHierarchyTree(mEmployeesList);
            //Filter for department and name if something was set before
            filter();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Employee>> loader) {
        mEmployeesListView.setAdapter(null);
    }


}
