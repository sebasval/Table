package tesis.utadeo.table;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister, buttonSignIn;
    private EditText ediTextEmail, editTextPass;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("");

        callbackManager = CallbackManager.Factory.create();
        ediTextEmail = (EditText) findViewById(R.id.userLogin);
        editTextPass = (EditText) findViewById(R.id.passLogin);
        loginButton = (LoginButton) findViewById(R.id.loginButton);
        buttonSignIn = (Button) findViewById(R.id.signIn);
        buttonRegister = (Button) findViewById(R.id.register);

        buttonSignIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        if (AccessToken.getCurrentAccessToken() != null){
            goMainScreen();
            Toast.makeText(this, "Inicio de sesion con Facebook", Toast.LENGTH_SHORT).show();
        }

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

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                goMainScreen();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Inicio de sesion cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SignIn(String email, String pass){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("SESION","sesion iniciada correctamente!!");
                }else {
                    Log.e("SESION",task.getException().getMessage()+"");
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña invalidos ó " +
                            "esta cuenta de correo ya existe", Toast.LENGTH_LONG).show();
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
}
