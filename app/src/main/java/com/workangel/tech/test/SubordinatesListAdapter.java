package com.workangel.tech.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.workangel.tech.test.database.bean.Employee;
import com.workangel.tech.test.hierarchy.Node;

import java.util.List;
import java.util.Locale;

/**
 * Display subordinates Employee
 */
public class SubordinatesListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Node> mChildren;

    private static class ViewHolder {
        TextView subordinateName;
    }
    /**
     * Children to be displayed
     * @param context Context
     * @param children Childrend
     *
     */
    public SubordinatesListAdapter(Context context, List<Node> children) {
        mContext = context;
        mChildren = children;
    }

    @Override
    public int getCount() {
        return mChildren.size();
    }

    @Override
    public Object getItem(int position) {
        return mChildren.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_subordinates,parent,false);
            holder.subordinateName = (TextView) convertView.findViewById(R.id.subordinate_name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Node child = mChildren.get(position);
        Employee employee = child.getData();
        String department = "(" + employee.getDepartment().toUpperCase(Locale.US) + ")";
        holder.subordinateName.setText(employee.getFirstName() + " " + employee.getLastName() + " " + department);
        return convertView;
    }
}
