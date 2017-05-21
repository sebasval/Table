package tesis.utadeo.table;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmail, registerPass,confirmPass;
    Button buttonRegisterAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerEmail = (EditText) findViewById(R.id.userRegister);
        registerPass = (EditText) findViewById(R.id.passRegister);
        confirmPass = (EditText) findViewById(R.id.passRegisterTwo);

        buttonRegisterAct = (Button) findViewById(R.id.buttonRegister);
        buttonRegisterAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailRegisterAct = registerEmail.getText().toString();
                String passRegisterAct = registerPass.getText().toString();
                String confirmPassAct = confirmPass.getText().toString();
                String dominio = "@table.com";
                if (!TextUtils.isEmpty(emailRegisterAct) && !TextUtils.isEmpty(passRegisterAct) && !TextUtils.isEmpty(confirmPassAct)
                        && passRegisterAct == confirmPassAct){
                    String userComplete = emailRegisterAct + dominio;
                    Registrar(userComplete,passRegisterAct);
                    registerEmail.setText("");
                    registerPass.setText("");
                    confirmPass.setText("");
                    Toast.makeText(RegisterActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    private void Registrar(String email, String pass){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("SESION","usuario creado correctamente");
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.e("SESION",task.getException().getMessage()+"");

                }
            }
        });
    }
}
