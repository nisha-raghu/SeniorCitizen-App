package project.master.cmpe295.seniorcitizenapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.view.View;

/**
 * Created by nisha on 4/2/2018.
 */

public class CareTakerActivity extends AppCompatActivity {
    private static final String TAG = "AddToDatabase";

    private Button mAddToDB;

    private EditText mNewName;
    private EditText mPhone;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addcaretaker);
        //declare variables in oncreate
        mAddToDB = (Button) findViewById(R.id.buttonSaveCareTaker);
        mNewName = (EditText) findViewById(R.id.editTextCareTakerName);
        mPhone = (EditText) findViewById(R.id.editTextCarePhoneNumber);

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(CareTakerActivity.this, "Successfully signed in with:" + user.getEmail(), Toast.LENGTH_SHORT).show();
                    //toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(CareTakerActivity.this, "Successfully signed out.", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mAddToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database.");
                String newName = mNewName.getText().toString();
                String phone =mPhone.getText().toString();
                if(!newName.equals("") && !phone.equals("")){
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    myRef.child(userID).child("Caretaker").child(newName).setValue(phone);
                    Toast.makeText(CareTakerActivity.this, "Adding " + newName + " to database...", Toast.LENGTH_SHORT).show();

                //    myRef.child(userID).child("Food").child("Favorite Foods").child(newName).setValue("true");

                    // toastMessage("Adding " + newFood + " to database...");
                    //reset the text
                    mNewName.setText("");
                    mPhone.setText("");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
