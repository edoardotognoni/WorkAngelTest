package com.workangel.tech.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.CompanyHierarchyTree;
import com.workangel.tech.test.hierarchy.Node;
import de.greenrobot.event.EventBus;

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
    /**
     * Loader constant for the LoaderManager
     */
    public static final int EMPLOYEES_LOADER_ID = 1000;

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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employess_list, container, false);

        mEmployeesListView = (ListView) root.findViewById(R.id.employees_list);
        mDepartmentSpinner = (Spinner) root.findViewById(R.id.department_spinner);
        mEmployeesSearch = (SearchView) root.findViewById(R.id.employees_search);

        mEmployeesList = new ArrayList<>();
        if (getArguments() != null) {
            mEmployeesList = getArguments().getParcelableArrayList(KEY_EMPLOYEES_LIST);
        }

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
                if (mDepartmentEmployeesMap != null && mEmployeesListView.getAdapter() != null) {
                    filter();
                }
                return true;
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
            update(mEmployeesList);
        }
        else {
            //Init loader
            getLoaderManager().restartLoader(EMPLOYEES_LOADER_ID, null, this).forceLoad();
        }


        return root;
    }

    /**
     * Updates UI and buil hierarchy tree and department map
     * @param fullList
     */
    public void update(List<Employee> fullList) {
        if (fullList == null) {
            mEmployeesList = new ArrayList<>();
        }
        else {
            mEmployeesList = fullList;
        }
        buildDepartmentMap(mEmployeesList);

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
        mEmployeesSearch.setQuery(mQueryName, false);
        //Show filter views
        mDepartmentSpinner.setVisibility(View.VISIBLE);
        mEmployeesSearch.setVisibility(View.VISIBLE);


        mCompanyTree = new CompanyHierarchyTree(mEmployeesList);
        filter();
    }


    /**
     * Filter the ListView with department chosen and query text
     */
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

    @Override
    public Loader<List<Employee>> onCreateLoader(int id, Bundle args) {
        return new EmployeesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Employee>> loader, List<Employee> data) {
        //Update Employees list
        update(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Employee>> loader) {

    }
}
