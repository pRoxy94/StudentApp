package it.uniba.di.sms.laricchia.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import it.uniba.di.sms.laricchia.R;
import it.uniba.di.sms.laricchia.database.StudentContract.*;
import it.uniba.di.sms.laricchia.database.StudentOpenHelper;

import java.util.ArrayList;

public class ExamsDoneList extends AppCompatActivity {
    private Toolbar tlbExamsDone;
    private ListView lstExamsDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_done_list);

        //Toolbar
        tlbExamsDone = (Toolbar) findViewById(R.id.tlbExamsDone);
        tlbExamsDone.setNavigationIcon(R.drawable.ic_arrow_back);
        tlbExamsDone.setTitle(R.string.strTxtExamsDone);
        setSupportActionBar(tlbExamsDone);

        tlbExamsDone.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        lstExamsDone = (ListView) findViewById(R.id.lstExamsDone);

        String[] examsList = getResources().getStringArray(R.array.ExamsDoneList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.adapter_list_exams, getAllData());
        lstExamsDone.setAdapter(adapter);
    }

    private String[] getAllData() {
        SQLiteOpenHelper examsOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(ExamsDoneEntry.TABLE_NAME,
                new String[] {ExamsDoneEntry.NAME,
                        ExamsDoneEntry.MARK,
                        ExamsDoneEntry.DATA},
                null,
                null,
                null,
                null,
                null);

        //Porto il cursor in prima posizione
        cursor.moveToFirst();

        //Creo e riempio la lista di esami con elementi stringa acquisiti dal cursor
        ArrayList<String> exams = new ArrayList<>();
        do {
            String tuple = getTuple(cursor);
            exams.add(tuple);
        } while(cursor.moveToNext());

        //converto la lista in array
        String[] examsArray = new String[exams.size()];
        examsArray = exams.toArray(examsArray);

        return examsArray;
    }

    private String getTuple(Cursor c) {

        //Estrazione dati
        String examName = c.getString(c.getColumnIndex(ExamsDoneEntry.NAME));
        String examMark = c.getString(c.getColumnIndex(ExamsDoneEntry.MARK));
        String examData = c.getString(c.getColumnIndex(ExamsDoneEntry.DATA));

        //Costruisco la stringa
        StringBuilder sb = new StringBuilder();
        sb.append(examName);
        sb.append("\n");
        sb.append(examMark);
        sb.append("\n");
        sb.append(examData);
        sb.append("\n");

        //tupla
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * Assegno layout al icon_watchword:
         * 1) recupera un rif ad un inflater di Menu ossia un servizio di sistema in grado di modellare
         * la struttura dell'oggetto Menu in base al codice xml
         */
        MenuInflater inflater = getMenuInflater();
        /**
         * 2) l'azione di inflating viene svolta. Il metodo inflate richiede due parametri:
         *      - la risorsa contenente il layout del icon_watchword
         *      - l'oggetto Menu da configurare
         */
        inflater.inflate(R.menu.statistics, menu);
        // solo se il valore restituito da OnCreateOptionMenu sarà true allora il icon_watchword sarà attivo
        return true;
    }

    //per la gestione del click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.statistics){
            Intent goToStatisticsActivity = new Intent(this,StatisticsActivity.class);
            startActivity(goToStatisticsActivity);
            //finish();
        }
        return false;
    }
}
