package com.workangel.tech.test;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.workangel.tech.test.database.DatabaseManager;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.network.Constants;
import com.workangel.tech.test.network.FactoryNetworkManager;
import com.workangel.tech.test.network.FactoryNetworkManagerInterface;
import com.workangel.tech.test.network.ResultCallback;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.List;

/**
 * Fragment showing the Employees list. It uses the LoaderManager pattern to retrieve
 * data from the DB.
 */
public class FragmentListEmployees extends Fragment implements LoaderManager.LoaderCallbacks<List<Employee>>{
    private static final String TAG = FragmentListEmployees.class.getSimpleName();
    private ListView mRoot;
    public static final int EMPLOYEES_LOADER_ID = 1000;
    /**
     * Key used to retain the list on orientation changes
     */
    public static final String KEY_EMPLOYEES_LIST = "key_employees_list";
    private List<Employee> mEmployeesList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = (ListView) inflater.inflate(R.layout.simple_list_layout, container, false);
        /**
         * Set a click listener to move to the employee detail fragment
         */
        mRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            mRoot.setAdapter(new EmployeesListAdapter(getActivity(),mEmployeesList));
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
                                                 Style.ALERT);
                                break;
                            case Constants.NETWORK_PROBLEM:
                                Crouton.makeText(getActivity(), getString(R.string.error_network_problem),
                                                 Style.ALERT);
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
        return mRoot;
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
            //Update UI
            EmployeesListAdapter adapter = (EmployeesListAdapter) mRoot.getAdapter();
            if (adapter == null) {
                adapter = new EmployeesListAdapter(getActivity(),data);
                mRoot.setAdapter(adapter);
            }
            else {
                adapter.setEmployees(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Employee>> loader) {
    }


}
