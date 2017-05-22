package tesis.utadeo.table;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister, buttonSignIn;
    private EditText ediTextEmail, editTextPass;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("");

        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mGoogleBtn = (SignInButton)findViewById(R.id.googleBtn);
        ediTextEmail = (EditText) findViewById(R.id.userLogin);
        editTextPass = (EditText) findViewById(R.id.passLogin);
        buttonSignIn = (Button) findViewById(R.id.signIn);
        buttonRegister = (Button) findViewById(R.id.register);

        buttonSignIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    Toast.makeText(LoginActivity.this, "Inicio  de sesion con usuario y contraseña", Toast.LENGTH_SHORT).show();
                    Log.i("SESION","sesion iniciada con email " + user.getEmail() );
                    goMainScreen();
                }else {
                    Log.i("SESION","sesion cerrada");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });


    }

    private void signInGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void SignIn(String email, String pass){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("SESION","sesion iniciada correctamente!!");
                }else {
                    Log.e("SESION",task.getException().getMessage()+"");
                    Toast.makeText(LoginActivity.this, "Usuario y contraseña invalidos ó " +
                            "esta cuenta ya esta en uso", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("Iniciando Sesión");
            mProgress.show();
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                mProgress.dismiss();
                checkUserExist();
            }
        }


    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        goMainScreen();
                    } else {
                        Toast.makeText(LoginActivity.this, "No hay un usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn:
                    String emailSigIn = ediTextEmail.getText().toString();
                    String passSigIn = editTextPass.getText().toString();

                if (emailSigIn.isEmpty() && passSigIn.isEmpty()){
                    Toast.makeText(this, "Debes ingresar un usuario y contraseña", Toast.LENGTH_SHORT).show();
                }else if (emailSigIn.isEmpty()){
                    Toast.makeText(this, "Debes ingresar un usuario", Toast.LENGTH_SHORT).show();
                }else if (passSigIn.isEmpty()){
                    Toast.makeText(this, "Debes ingresar una contraseña", Toast.LENGTH_SHORT).show();
                }else {
                    SignIn(emailSigIn, passSigIn);
                    ediTextEmail.setText("");
                    editTextPass.setText("");
                }

                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener!=null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                        mProgress.show();
                    }
                });
    }
}
