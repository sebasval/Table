package tesis.utadeo.table;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Publish extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Comentario");
    }
}
