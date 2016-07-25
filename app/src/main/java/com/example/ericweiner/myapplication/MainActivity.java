package com.example.ericweiner.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
public class MainActivity extends Activity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private ListView mListView;
    ArrayList<Contact> contactList;
    Cursor cursor;
    int counter;

    public void sortContacts(ArrayList<Contact> contactList) {
        Collections.sort(contactList);
    }

    public ArrayList<String> contactsToString(List<Contact> contactList, int numContacts) {
        ArrayList<String> retArray = new ArrayList<>();
        for(int i = 0; i < numContacts; i++) {
            Contact c = contactList.get(i);
            retArray.add(c.getName() + "\n" + c.getstrNumbers());
        }
        return retArray;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list);

        // Since reading contacts takes more time, let's run it on a separate thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts();
            }
        }).start();
        // Set onclicklistener to the list item.
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO Do whatever you want with the list data
                Toast.makeText(getApplicationContext(), "item clicked : \n"+contactList.get(position).getNumbers().get(0), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getContacts() {
 //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

        contactList = new ArrayList<>();
        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output;
        final ContentResolver contentResolver = getContentResolver();
        Log.w("!!!!!GETCONTACTS!!!!", "above query");
        Log.w("_ID", CONTENT_URI.toString());
        cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
        Log.w("!!!!!GETCONTACTS!!!!", "below query");
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            Contact oldContact = null;
            Contact currentContact = null;

            ArrayList<String> phoneNumbers;
            while (cursor.moveToNext()) {
                output = new StringBuffer();
                phoneNumbers = new ArrayList<>();
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                if (hasPhoneNumber > 0) {

                    //output.append("\n First Name: " + name);
                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumbers.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
 //                       phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
           //             output.append("\n Phone number: " + phoneNumber);
                    }
                    phoneCursor.close();
                    currentContact = new Contact(name, phoneNumbers);

                    // Read every email id associated with the contact

                }
                // Add the contact to the ArrayList
                if (currentContact != null && !currentContact.equals(oldContact)) {
                    contactList.add(currentContact);
                    oldContact = currentContact;
                }
            }
            // ListView has to be updated using a ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sortContacts(contactList);
                    ArrayList<String> strContacts = contactsToString(contactList, contactList.size());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.text1, strContacts);
                    mListView.setAdapter(adapter);
                }
            });
            // Dismiss the progressbar after 500 millisecondds

        }
    }
}
