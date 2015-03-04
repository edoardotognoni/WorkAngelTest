package com.workangel.tech.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.ion.Ion;
import com.workangel.tech.test.database.bean.Employee;

import java.util.List;

/**
 * Adapter for the Employees List. Every row show an Employee
 */
public class EmployeesListAdapter extends BaseAdapter {

    /**
     * ViewHolder pattern for the adapter. It permits you to recycle views
     * rather than re-create them from scratch
     */
    private static class ViewHolder {
        TextView employeeName;
        TextView employeeDepartment;
        ImageView employeeAvatar;
    }

    private Context mContext;
    private List<Employee> mEmployees;

    /**
     * Basic constructor for the adapter
     * @param context Context
     * @param employees Employees list
     */
    public EmployeesListAdapter(Context context, List<Employee> employees) {
        mContext = context;
        mEmployees = employees;
    }

    @Override
    public int getCount() {
        return mEmployees.size();
    }

    @Override
    public Object getItem(int position) {
        return mEmployees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_employees_list,parent,false);
            holder.employeeAvatar = (ImageView) convertView.findViewById(R.id.employee_avatar);
            holder.employeeDepartment = (TextView) convertView.findViewById(R.id.employee_department);
            holder.employeeName = (TextView) convertView.findViewById(R.id.employee_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Set employees data
        Employee employee = mEmployees.get(position);
        holder.employeeName.setText(employee.getLastName() + " " + employee.getFirstName());
        holder.employeeDepartment.setText(mContext.getString(R.string.department_arg,employee.getDepartment()));
        Ion.with(holder.employeeAvatar).load(employee.getAvatar());
        return convertView;
    }

    public List<Employee> getEmployees() {
        return mEmployees;
    }

    public void setEmployees(List<Employee> employees) {
        mEmployees = employees;
    }
}
