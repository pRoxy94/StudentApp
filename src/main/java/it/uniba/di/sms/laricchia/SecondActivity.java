package it.uniba.di.sms.laricchia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView txtGrade, txtExercise;
    private EditText editVote, editVote2, editVoteExercise, editVoteFinal;
    private Button btnBack;
    private SeekBar seekWritten, seekOral, seekEx;
    private Toolbar tlbExamDetails;
    private int currentWrittenVote = 0;
    private int currentOralVote = 0;
    private double currentExVote = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        txtGrade = (TextView) findViewById(R.id.txtGrade);
        txtExercise = (TextView) findViewById(R.id.txtExercise);
        editVote = (EditText) findViewById(R.id.editVote);
        editVote2 = (EditText) findViewById(R.id.editVote2);
        editVoteExercise = (EditText) findViewById(R.id.editVoteExercise);
        editVoteFinal = (EditText) findViewById(R.id.editVoteFinal);
        btnBack = (Button) findViewById(R.id.btnBack);
        seekWritten = (SeekBar) findViewById(R.id.seekWrittenVote);
        seekOral = (SeekBar) findViewById(R.id.seekOralVote);
        seekEx = (SeekBar) findViewById(R.id.seekExercise);

        tlbExamDetails = (Toolbar) findViewById(R.id.tlbExamDetails);
        tlbExamDetails.setTitle(R.string.strTxtExamDetails);
        tlbExamDetails.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(tlbExamDetails);

        //Recupero dati da Intent
        String examName = getIntent().getExtras().getString("ExamName");
        final String exNum = getIntent().getExtras().getString("ExercisesNum");
        final int exNumInt = Integer.parseInt(exNum);
        //set valore massimo della seekBar exercises in base al num di esercitazioni previste
        seekEx.setMax(exNumInt);


        //TextView personalizzata
        Resources res = getResources();
        String title = getString(R.string.txtGrade, examName);
        //set plurals
        String  exercisesPl = res.getQuantityString(R.plurals.numberOfExercises,exNumInt);
        String exercises = getString(R.string.txtNumberEx, exNum, exercisesPl);
        txtGrade.setText(title);
        txtExercise.setText(exercises);

        //button back main activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });

        //SeekBar Written Vote
        seekWritten.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                currentWrittenVote = progress;
                editVote.setText("" + seekBar.getProgress());
                updateFinalVote();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tlbExamDetails.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //SeekBar Oral Vote
        seekOral.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                currentOralVote = progress;
                editVote2.setText("" + seekBar.getProgress());
                updateFinalVote();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //SeekBar Exercises Vote
        seekEx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                double doubleExNum = (double) exNumInt;
                //aggiorna numero di esercitazioni
                currentExVote = progress / doubleExNum * 3;
                //troncamento cifre decimali double
                currentExVote = Math.floor(currentExVote*10)/10;
                editVoteExercise.setText("" + currentExVote);
                updateFinalVote();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void updateFinalVote() {
        int finalVote = (int) (currentWrittenVote + currentOralVote + currentExVote);
        editVoteFinal.setText("" + finalVote);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
