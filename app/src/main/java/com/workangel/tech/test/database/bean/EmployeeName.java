package com.workangel.tech.test.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Employee name made by last and first name
 */
@DatabaseTable
public class EmployeeName {
    private static final String TAG = EmployeeName.class.getSimpleName();

    /**
     * Employee name
     */
    @DatabaseField(generatedId = true)
    private int id;
    private String first;
    private String last;
}
