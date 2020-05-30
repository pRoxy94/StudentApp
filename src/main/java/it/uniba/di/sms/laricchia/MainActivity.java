package it.uniba.di.sms.laricchia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private final String LOG = "Laricchia.MainActivity";
    private Button buttonApply;
    private EditText examName, exNum;
    private Toolbar tlbMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonApply = (Button) findViewById(R.id.btnApply);
        examName = (EditText) findViewById(R.id.editExamName);
        exNum = (EditText) findViewById(R.id.editExNum);

        tlbMain = (Toolbar) findViewById(R.id.tlbMain);
        tlbMain.setTitle(R.string.strTxtExam);
        tlbMain.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(tlbMain);

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strExamName = examName.getText().toString();
                String intExNum = exNum.getText().toString();
                goToSecondActivity(strExamName, intExNum);
            }
        });

        tlbMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.i(LOG, "- OnCreate() started - l'Activity è stata creata. ");
    }

    private void goToSecondActivity(String strExamName, String intExNum){
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("ExamName",strExamName);
        intent.putExtra("ExercisesNum",intExNum);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG, "- OnStart() started - l'Activity è visibile all'utente. ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG, "- OnResume() started - l'Activity è nuovamente disponibile e l'utente può interagire con essa. ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "- OnPause() started - l'Activity passa in secondo piano ma è ancora visibile all'utente ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG, "- OnStop() started - l'Activity non è più visibile all'utente. ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG, "- OnRestart() started - l'Activity sta per essere nuovamente avviata. ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG, "- OnDestroy() started - l'Activity è stata distrutta. ");
    }
}
