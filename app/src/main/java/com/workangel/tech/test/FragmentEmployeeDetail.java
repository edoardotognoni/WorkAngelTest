package com.workangel.tech.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by opossum on 04/03/15.
 */
public class FragmentEmployeeDetail extends Fragment {
    private static final String TAG = FragmentEmployeeDetail.class.getSimpleName();
    /**
     * Key used to retrieve the {@link com.workangel.tech.test.database.bean.Employee employee} from the Bundle
     */
    public static final String KEY_EMPLOYEE = "key_employess";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_employee_detail, container, false);
        return root;
    }
}
