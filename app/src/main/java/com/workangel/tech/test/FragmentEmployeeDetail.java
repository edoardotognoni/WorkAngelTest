package com.workangel.tech.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.*;
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
        TextView employeeAbout = (TextView) root.findViewById(R.id.employee_about);

        Ion.with(employeeAvatar).load(mEmployee.getAvatar());
        employeeName.setText(mEmployee.getLastName() + " " + mEmployee.getFirstName());
        employeeDepartment.setText(mEmployee.getDepartment());
        employeeAbout.setText(mEmployee.getAbout());

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_employee_detail,menu);
        //If employee doesn't have email address,or phone number, make menu items invisible
        if (TextUtils.isEmpty(mEmployee.getEmail())) {
            menu.findItem(R.id.action_send_email).setVisible(false);
        }

        if (TextUtils.isEmpty(mEmployee.getPhone())) {
            menu.findItem(R.id.action_call).setVisible(false);
            menu.findItem(R.id.action_send_sms).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_email:
                sendEmail(mEmployee.getEmail());
                break;
            case R.id.action_send_sms:
                sendSms(mEmployee.getPhone());
                break;
            case R.id.action_call:
                call(mEmployee.getPhone());
                break;
        }
        return true;
    }

    private void sendEmail(String address) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",address, null));
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_request));

        startActivity(Intent.createChooser(intent, getString(R.string.send_mail)));
    }

    private void sendSms(String phone) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null));
        startActivity(intent);
    }

    private void call(String phone) {
        String uri = "tel:" + phone.trim() ;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_EMPLOYEE, mEmployee);
    }
}
