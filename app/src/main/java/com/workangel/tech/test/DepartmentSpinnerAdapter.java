package com.workangel.tech.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.workangel.tech.test.database.bean.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Spinner adapter for department filtering
 */
public class DepartmentSpinnerAdapter extends BaseAdapter {
    private static final String TAG = DepartmentSpinnerAdapter.class.getSimpleName();
    private Context mContext;
    private Map<String, List<Employee>> mEmployeesDeptMap;
    private List<String> departments;

    /**
     * ViewHolder pattern
     */
    private static class ViewHolder {
        TextView department;
    }

    /**
     * Basic constructor
     * @param context Context
     * @param employeesDeptMap Map where keys are department string and values are
     *                         List of employees per department
     */
    public DepartmentSpinnerAdapter(Context context, Map<String,List<Employee>> employeesDeptMap) {
        mContext = context;
        setEmployeesDeptMap(employeesDeptMap);
    }

    @Override
    public int getCount() {
        return departments.size();
    }

    @Override
    public Object getItem(int position) {
        return departments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Employee> getEmployeesForDept(String dept) {
        return mEmployeesDeptMap.get(dept);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_department_spinner, parent, false);
            holder.department = (TextView) convertView.findViewById(R.id.department_text);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.department.setText(departments.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public Map<String, List<Employee>> getEmployeesDeptMap() {
        return mEmployeesDeptMap;
    }

    public void setEmployeesDeptMap(Map<String, List<Employee>> employeesDeptMap) {
        mEmployeesDeptMap = employeesDeptMap;
        departments = new ArrayList<>(mEmployeesDeptMap.keySet());
    }
}
