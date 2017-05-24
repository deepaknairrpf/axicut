package com.example.dingu.axicut.Admin.user;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dingu.axicut.R;
import com.example.dingu.axicut.UserMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddUser extends AppCompatActivity {


    UserMode userMode ;  // An enum of user modes
    boolean radioButtonChecked = false;
    ProgressDialog progress;

    private FirebaseAuth mAuth;
    private DatabaseReference mdatabaseRefUsers;

    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    Button addUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        mAuth = FirebaseAuth.getInstance();
        mdatabaseRefUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        progress = new ProgressDialog(this);

        nameField = (EditText)findViewById(R.id.name);
        emailField = (EditText)findViewById(R.id.email);
        passwordField = (EditText)findViewById(R.id.password);
        addUser = (Button)findViewById(R.id.signUp);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {

        final String name = nameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        final FirebaseAuth tempAuth;
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://axicut-3fe9b.firebaseio.com/")
                .setApiKey("AIzaSyDKjXmCJ377LALkI87aIX8fa9Km-_OcF68")
                .setApplicationId("axicut-3fe9b").build();
        FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(),firebaseOptions,"axicut");
         tempAuth=FirebaseAuth.getInstance(myApp);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && radioButtonChecked) {
            progress.setMessage("Adding new user..");
            progress.show();
            tempAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(email,name,userMode);
                        mdatabaseRefUsers.push().setValue(user);
                        progress.dismiss();
                        tempAuth.signOut();
                        onBackPressed();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.dismiss();
//                    Log.e("App","Error sign up " + e);
                    Toast.makeText(getApplicationContext(), "Oops : Error - " + e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    public void onProfileRadioButtonClicked (View view) {
        // Is the button now checked?
        radioButtonChecked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_inward:
                if (radioButtonChecked)
                    userMode = UserMode.INWARD;
                break;
            case R.id.radio_design:
                if (radioButtonChecked)
                    userMode = UserMode.DESIGN;
                break;
            case R.id.radio_production:
                if (radioButtonChecked)
                    userMode = UserMode.PRODUCTION;
                break;
            case R.id.radio_admin:
                if (radioButtonChecked)
                    userMode = UserMode.ADMIN;
                break;

        }
    }
}
