package com.workangel.tech.test;

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
import com.workangel.tech.test.database.DatabaseManager;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.CompanyHierarchyTree;
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
    public static final int EMPLOYEES_LOADER_ID = 1000;
    /**
     * Key used to retain the list on orientation changes
     */
    public static final String KEY_EMPLOYEES_LIST = "key_employees_list";
    /**
     * Full list of employees
     */
    private List<Employee> mEmployeesList;
    /**
     *
     */
    private Map<String,List<Employee>> mDepartmentEmployeesMap;
    private Spinner mDepartmentSpinner;
    private SearchView mEmployeesSearch;
    private CompanyHierarchyTree mCompanyTree;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employess_list, container, false);

        mEmployeesListView = (ListView) root.findViewById(R.id.employees_list);
        mDepartmentSpinner = (Spinner) root.findViewById(R.id.department_spinner);
        mEmployeesSearch = (SearchView) root.findViewById(R.id.employees_search);

        mDepartmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //When setOnItemSelectedListener is set, it auto calls onItemSelected at first. I don't want
            // this behaviour so I hadd a bit of logic to call the method only when the user
            // actually selects the item
            boolean isLoaded = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLoaded) {
                    isLoaded = true;
                    return;
                }
                String departmentChosen = (String) parent.getItemAtPosition(position);
                List<Employee> employeesForDept = mDepartmentEmployeesMap.get(departmentChosen);
                mEmployeesListView.setAdapter(new EmployeesListAdapter(getActivity(),employeesForDept));
                mEmployeesSearch.setQuery("",false);
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
                EmployeesListAdapter adapter = (EmployeesListAdapter) mEmployeesListView.getAdapter();
                String department = (String) mDepartmentSpinner.getSelectedItem();
                //First filter for department
                List<Employee> employeesForDeprt = mDepartmentEmployeesMap.get(department);
                if (adapter != null) {
                    if (employeesForDeprt == null) {
                        //Just in case
                        employeesForDeprt = new ArrayList<Employee>();
                    }

                    adapter.setEmployees(getEmployeesFilteredForName(employeesForDeprt,newText));
                    adapter.notifyDataSetChanged();

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
                EventBus.getDefault().post(new MainActivity.EventTransactToEmployeeDetailFragment(employee));
            }
        });
        /**
         * If we come from a configuration change, show old data. We don't need to
         * call the API every time we change from portrait to landscape and viceversa
         */
        if (savedInstanceState != null) {
            mEmployeesList = savedInstanceState.getParcelableArrayList(KEY_EMPLOYEES_LIST);
            mEmployeesListView.setAdapter(new EmployeesListAdapter(getActivity(), mEmployeesList));
            buildDepartmentMap(mEmployeesList);
        }
        else {
            //Make the API call to get employees data
            FactoryNetworkManagerInterface networkManagerInterface = FactoryNetworkManager.getInstance(getActivity(),
                                                                                                       FactoryNetworkManager.NetworkFramework.RETROFIT);


            networkManagerInterface.getAllEmployees(new ResultCallback<List<Employee>>() {

                @Override
                public void onSuccess(List<Employee> result) {
                    Log.d(TAG, "Employees downloaded successfully");
                    if (isAdded()) {
                        //Save emplyees to DB
                        saveEmployeesAsync(result);

                    }
                }

                @Override
                public void onError(int errorCode) {
                    if (isAdded()) {
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

            /**
             * While we wait for the API call to finish, we show old data from DB
             */
            getLoaderManager().initLoader(EMPLOYEES_LOADER_ID, null, this).forceLoad();
        }
        return root;
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
                DatabaseManager.getInstance(getActivity()).saveEmployees(employees);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isAdded()) {
                    //Update UI by restarting loader
                    getLoaderManager().restartLoader(EMPLOYEES_LOADER_ID, null, FragmentListEmployees.this).forceLoad();
                }
                super.onPostExecute(aVoid);
            }
        }.execute();

    }



    @Override
    public Loader<List<Employee>> onCreateLoader(int id, Bundle args) {
        return new EmployeesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Employee>> loader, List<Employee> data) {
        mEmployeesList = data;
        if (isAdded()) {
            buildDepartmentMap(mEmployeesList);
            mCompanyTree = new CompanyHierarchyTree(mEmployeesList);
            //Better pick list from Map becuase probably user has already selected a department
            String department = (String) mDepartmentSpinner.getSelectedItem();
            List<Employee> employeesForDeptChosen = mDepartmentEmployeesMap.get(department);
            //Update UI
            EmployeesListAdapter adapter = (EmployeesListAdapter) mEmployeesListView.getAdapter();
            List<Employee> employeesFiltered = getEmployeesFilteredForName(employeesForDeptChosen,mEmployeesSearch.getQuery().toString());
            if (adapter == null) {
                adapter = new EmployeesListAdapter(getActivity(),employeesFiltered);
                mEmployeesListView.setAdapter(adapter);
            }
            else {
                adapter.setEmployees(employeesFiltered);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Employee>> loader) {
        mEmployeesListView.setAdapter(null);
    }


}
