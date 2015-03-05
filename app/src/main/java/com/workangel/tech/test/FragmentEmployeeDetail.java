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
import com.workangel.tech.test.hierarchy.Node;

import java.util.Locale;

/**
 * Created by opossum on 04/03/15.
 */
public class FragmentEmployeeDetail extends Fragment {
    private static final String TAG = FragmentEmployeeDetail.class.getSimpleName();
    /**
     * Key used to retrieve the {@link com.workangel.tech.test.database.bean.Employee employee} from the Bundle
     */
    public static final String KEY_EMPLOYEE = "key_employess";
    public static final String KEY_NODE = "key_tree_node";
    private Employee mEmployee;
    private Node mNode;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employee_detail, container, false);
        if (savedInstanceState != null) {
            mEmployee = savedInstanceState.getParcelable(KEY_EMPLOYEE);
            mNode = savedInstanceState.getParcelable(KEY_NODE);
        }
        else {
            mEmployee = getArguments().getParcelable(KEY_EMPLOYEE);
            mNode = getArguments().getParcelable(KEY_NODE);
        }

        ImageView employeeAvatar = (ImageView) root.findViewById(R.id.employee_avatar);
        TextView employeeName = (TextView) root.findViewById(R.id.employee_name);
        TextView employeeDepartment = (TextView) root.findViewById(R.id.employee_department);
        TextView employeeAbout = (TextView) root.findViewById(R.id.employee_about);
        TextView employeePhone = (TextView) root.findViewById(R.id.phone);
        TextView employeeSms = (TextView) root.findViewById(R.id.sms);
        TextView employeeAddress = (TextView) root.findViewById(R.id.address);
        TextView employeeEmail = (TextView) root.findViewById(R.id.email);

        Ion.with(employeeAvatar).load(mEmployee.getAvatar());
        employeeName.setText(mEmployee.getLastName() + " " + mEmployee.getFirstName());
        employeeDepartment.setText(mEmployee.getDepartment().toUpperCase(Locale.US));
        employeeAbout.setText(mEmployee.getAbout());
        employeePhone.setText(mEmployee.getPhone());
        employeeSms.setText(mEmployee.getPhone());
        employeeAddress.setText(mEmployee.getAddress());
        employeeEmail.setText(mEmployee.getEmail());

        /**
         * Hide views if Employee doesn't have some fields
         */
        View phoneLayout = root.findViewById(R.id.phone_layout);
        View smsLayout = root.findViewById(R.id.sms_layout);
        View emailLayout = root.findViewById(R.id.email_layout);
        View addressLayout = root.findViewById(R.id.address_layout);
        if (TextUtils.isEmpty(mEmployee.getPhone())) {
            phoneLayout.setVisibility(View.GONE);
            smsLayout.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mEmployee.getEmail())) {
            emailLayout.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mEmployee.getAddress())) {
            addressLayout.setVisibility(View.GONE);
        }
        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(mEmployee.getPhone());
            }
        });
        smsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(mEmployee.getPhone());
            }
        });
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(mEmployee.getEmail());
            }
        });
        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(mEmployee.getAddress());
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareContact(mEmployee);
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

    private void showMap(String address) {
        String uri = "geo:0,0?q=" + address;
        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    private void call(String phone) {
        String uri = "tel:" + phone.trim() ;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    /**
     * Share employee detail in a simple text format.
     *
     * @param employee Employee to share
     */
    private void shareContact(Employee employee) {
        //I decided to share only a few data but we can go for a lot more. Like posting on FB or G+, post its Avatar on Instagram,...
        String shareBody = getString(R.string.share_text_args,
                                     employee.getFirstName(),
                                     employee.getLastName(),
                                     employee.getPhone(),
                                     employee.getAddress(),
                                     employee.getEmail(),
                                     employee.getAbout());

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, employee.getLastName() + " " + employee.getFirstName());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_EMPLOYEE, mEmployee);
        outState.putParcelable(KEY_NODE, mNode);
    }
}
