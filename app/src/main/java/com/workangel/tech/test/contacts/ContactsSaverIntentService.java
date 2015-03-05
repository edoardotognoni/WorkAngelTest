package com.workangel.tech.test.contacts;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import com.workangel.tech.test.database.bean.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by opossum on 05/03/15.
 */
public class ContactsSaverIntentService  extends IntentService {
    private static final String TAG = ContactsSaverIntentService.class.getSimpleName();
    public static final String KEY_EMPLOYEES_LIST = "key_employees_list";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ContactsSaverIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        List<Employee> employees = intent.getParcelableArrayListExtra(KEY_EMPLOYEES_LIST);
        if (employees != null) {
            for (Employee employee : employees) {
                saveInContacts(employee);
            }
        }
    }

    /**
     * Save an employee in your phone contacts list
     * @param employee Employee to be saved
     */
    public  void saveInContacts(Employee employee) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(
            ContactsContract.RawContacts.CONTENT_URI)
                                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                        .build());

        //------------------------------------------------------ Names
        ops.add(ContentProviderOperation.newInsert(
            ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                   ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                        .withValue(
                                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                            employee.getFirstName() + " " + employee.getLastName()).build());

        //------------------------------------------------------ Mobile Number
        ops.add(ContentProviderOperation.
                                            newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, employee.getPhone())
                                        .build());

        //------------------------------------------------------ Email
        ops.add(ContentProviderOperation.
                                            newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, employee.getEmail())
                                        .build());

        //------------------------------------------------------ Department
        ops.add(ContentProviderOperation.
                                            newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE,
                                                   ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, employee.getDepartment())
                                        .build());

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Log.w(TAG,"Saved contact: " + employee.getFirstName() + " " + employee.getLastName());
        }
        catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG,"Can't create contacts. Nevermind");
        }
        catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.e(TAG,"Can't create contacts. Nevermind");
        }
    }
}
