package com.workangel.tech.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.ion.Ion;
import com.workangel.tech.test.database.bean.Employee;

/**
 * Created by opossum on 04/03/15.
 */
public class FragmentEmployeeDetail extends Fragment {
    private static final String TAG = FragmentEmployeeDetail.class.getSimpleName();
    /**
     * Key used to retrieve the {@link com.workangel.tech.test.database.bean.Employee employee} from the Bundle
     */
    public static final String KEY_EMPLOYEE = "key_employess";
    private Employee mEmployee;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employee_detail, container, false);
        if (savedInstanceState != null) {
            mEmployee = savedInstanceState.getParcelable(KEY_EMPLOYEE);
        }
        else {
            mEmployee = getArguments().getParcelable(KEY_EMPLOYEE);
        }

        ImageView employeeAvatar = (ImageView) root.findViewById(R.id.employee_avatar);
        TextView employeeName = (TextView) root.findViewById(R.id.employee_name);
        TextView employeeDepartment = (TextView) root.findViewById(R.id.employee_department);

        Ion.with(employeeAvatar).load(mEmployee.getAvatar());
        employeeName.setText(mEmployee.getLastName() + " " + mEmployee.getFirstName());
        employeeDepartment.setText(mEmployee.getDepartment());
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_EMPLOYEE, mEmployee);
    }
}
