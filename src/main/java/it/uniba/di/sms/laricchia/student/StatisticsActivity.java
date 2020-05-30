package it.uniba.di.sms.laricchia.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import androidx.appcompat.widget.Toolbar;
import it.uniba.di.sms.laricchia.R;
import it.uniba.di.sms.laricchia.bluetooth.BluetoothActivity;
import it.uniba.di.sms.laricchia.database.StudentContract.*;
import it.uniba.di.sms.laricchia.database.StudentOpenHelper;

public class StatisticsActivity extends AppCompatActivity {

    private static final String DOT = ".";
    private static final String COLON = ", ";
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String ON = " ON ";

    private static final String QUERY = SELECT +
            ExamsDoneEntry.MARK +
            COLON +
            ExamEntry.CFU +
            FROM +
            ExamEntry.TABLE_NAME + INNER_JOIN + ExamsDoneEntry.TABLE_NAME +
            ON +
            ExamEntry.TABLE_NAME + DOT + ExamEntry.NAME
            + "=" +
            ExamsDoneEntry.TABLE_NAME + DOT + ExamsDoneEntry.NAME;

    String weigthedAverage;
    String average;
    String baseDegreeMark;

    TextView txtAverage, txtWeigthedAverage, txtBaseDegreeMark;
    Toolbar tlbStatistics;
    Button btnBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        txtAverage = (TextView) findViewById(R.id.txtAverageValue);
        txtWeigthedAverage = (TextView) findViewById(R.id.txtWeightedAverageValue);
        txtBaseDegreeMark = (TextView) findViewById(R.id.txtBaseDegreeMark);

        btnBluetooth = (Button) findViewById(R.id.btnBluetooth);

        tlbStatistics = (Toolbar) findViewById(R.id.tlbStatistics);
        tlbStatistics.setNavigationIcon(R.drawable.ic_arrow_back);
        tlbStatistics.setTitle(R.string.strTxtStatistics);

        tlbStatistics.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Cursor c = getDataFromDb();

        //creo liste voti e cfu
        ArrayList<Integer> marksList = getListFromColumn(ExamsDoneEntry.MARK, c);
        ArrayList<Integer> cfuList = getListFromColumn(ExamEntry.CFU, c);

        //calcolo medie
        weigthedAverage = getWeightedAverage(cfuList, marksList);
        average = calculateAverage(marksList);
        baseDegreeMark = getDegreeBase(weigthedAverage, average);

        //assegno valori alle textview
        txtAverage.setText(average);
        txtWeigthedAverage.setText(weigthedAverage);
        txtBaseDegreeMark.setText(baseDegreeMark);

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBluetoothActivity();
            }
        });

    }

    private Cursor getDataFromDb() {
        SQLiteOpenHelper examsOpenHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsOpenHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(QUERY, null);

        return cursor;
    }

    public ArrayList<Integer> getListFromColumn(String columnName, Cursor cursor) {
        //Porto il cursore in posizione iniziale
        cursor.moveToFirst();
        ArrayList<Integer> columnData = new ArrayList<>();
        do {
            columnData.add(Integer.parseInt(cursor.getString(cursor.getColumnIndex(columnName))));
        } while(cursor.moveToNext());
        return columnData;
    }

    /*
      La media ponderata si ottiene mediante la divisione di due quantita':
      1) la somma delle moltiplicazioni dei voti coi rispettivi cfu
      2) la somma dei cfu
      Questo metodo si occupa di restituire la media ponderata
    */
    private String getWeightedAverage(ArrayList<Integer> cfuList, ArrayList<Integer> marksList) {

        Iterator cfuIterator = cfuList.iterator();
        Iterator marksIterator = marksList.iterator();

        //Somma delle moltiplicazioni fra voti e cfu e somma dei cfu
        Integer sum = 0, sumCfu = 0;

        while (marksIterator.hasNext()) {

            //ottengo voto e cfu singolo
            int mark = (int) marksIterator.next();
            int cfu = (int) cfuIterator.next();

            //costruisco l'addendo che sara' il dividendo dell'operazione finale
            int addend = (mark * cfu);
            sum += addend;

            //costruisco l'addendo che sara' divisore dell'operazione finale
            sumCfu += cfu;
        }

        //salvo il valore temporaneo restituito
        double WeigthedAverage = (double) (sum / sumCfu);

        return Double.valueOf(WeigthedAverage).toString();
    }

    public String calculateAverage(ArrayList<Integer> marks) {
        Iterator i = marks.iterator();
        Integer sum = 0;
        while(i.hasNext()) {
            sum += (Integer) i.next();
        }
        Double average = (double) (sum / marks.size());
        return average.toString();
    }

    public String getAverage() {
        return this.average;
    }

    public String getDegreeBase(String weigthedAverage, String average) {

        Double tempWeAverage = Double.parseDouble(weigthedAverage);
        Double tempAverage = Double.parseDouble(average);

        //Per il troncamento delle cifre decimali
        DecimalFormat df = new DecimalFormat("#.##");
        double finalGrade = 0;

        //calcolo voto di laurea
        if(tempWeAverage > tempAverage) {
            finalGrade = (tempWeAverage / 3) * 11;
            //Stringa corrispondente al voto finale
            return df.format(finalGrade);
        } else {
            finalGrade = (tempAverage / 3) * 11;
            //Stringa corrispondente al voto finale
            return df.format(finalGrade);
        }
    }

    private void goToBluetoothActivity() {
        Intent goToBluetooth = new Intent(this, BluetoothActivity.class);
        goToBluetooth.putExtra("Average", getAverage());
        startActivity(goToBluetooth);
    }
}
