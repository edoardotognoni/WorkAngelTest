package com.workangel.tech.test.database.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Basic bean representing an Employee
 */
@DatabaseTable
public class Employee {
    private static final String TAG = Employee.class.getSimpleName();

    /**
     * Employee's ID
     */
    @DatabaseField(id = true)
    private int _id;

    @DatabaseField
    private String firstName;

    @DatabaseField
    private String lastName;

    @DatabaseField
    private String about;

    @DatabaseField
    private String phone;

    @DatabaseField
    private String avatar;

    @DatabaseField
    private String address;

    @DatabaseField
    private String email;

    @DatabaseField
    private Date dob;

    @DatabaseField
    private String department;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private int[] subordinates;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int[] getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(int[] subordinates) {
        this.subordinates = subordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;

        Employee employee = (Employee) o;

        if (_id != employee._id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _id;
    }
}
