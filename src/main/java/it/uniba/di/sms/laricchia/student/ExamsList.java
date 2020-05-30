package it.uniba.di.sms.laricchia.student;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import it.uniba.di.sms.laricchia.R;
import it.uniba.di.sms.laricchia.database.StudentContract.*;
import it.uniba.di.sms.laricchia.database.StudentOpenHelper;


public class ExamsList extends AppCompatActivity {

    private Toolbar tlbExams;
    private ListView listExams;

    private static final String CARDINALITY = "°";
    private static final String CFU = " CFU";
    private static final String ANNO = " anno";
    private static final String SEMESTRE = " semestre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_list);

        //Toolbar
        tlbExams = (Toolbar) findViewById(R.id.tlbExams);
        tlbExams.setNavigationIcon(R.drawable.ic_arrow_back);
        tlbExams.setTitle(R.string.strTxtCareer);
        setSupportActionBar(tlbExams);

        tlbExams.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listExams = (ListView) findViewById(R.id.lstExams);

        //String[] examsList = getResources().getStringArray(R.array.ExamsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.adapter_list_exams, getAllData());
        listExams.setAdapter(adapter);
    }

    private String[] getAllData() {
        SQLiteOpenHelper examsOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(ExamEntry.TABLE_NAME,
                new String[] {ExamEntry.NAME,
                        ExamEntry.CFU,
                        ExamEntry.YEAR,
                        ExamEntry.SEMESTER},
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
        String examName = c.getString(c.getColumnIndex(ExamEntry.NAME));
        String examCFU = c.getString(c.getColumnIndex(ExamEntry.CFU));
        String examYear = c.getString(c.getColumnIndex(ExamEntry.YEAR));
        String examSemester = c.getString(c.getColumnIndex(ExamEntry.SEMESTER));

        //Costruisco la stringa
        StringBuilder sb = new StringBuilder();
        sb.append(examName);
        sb.append("\n");
        sb.append(examCFU);
        sb.append(CFU);
        sb.append("\n");
        sb.append(examYear);
        sb.append(CARDINALITY);
        sb.append(ANNO);
        sb.append("\n");
        sb.append(examSemester);
        sb.append(CARDINALITY);
        sb.append(SEMESTRE);

        //tupla
        return sb.toString();
    }

}
