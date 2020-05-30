package it.uniba.di.sms.laricchia.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.widget.Toolbar;

import it.uniba.di.sms.laricchia.DatePickerFragment;
import it.uniba.di.sms.laricchia.R;
import it.uniba.di.sms.laricchia.database.StudentContract.*;
import it.uniba.di.sms.laricchia.database.StudentOpenHelper;
import it.uniba.di.sms.laricchia.sensor.SensorListActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;

import java.util.Calendar;

public class StudentCardActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Button btnGoToSensors, btnSave;
    private ToggleButton btnModify;
    private TextView name, surname, birthday, site, cellular, email, examsDone, examsToDo, average;
    private RadioButton rdbMale, rdbFemale;
    private Toolbar tlbStudent;
    private RadioGroup rdgGender;
    public static final String PREFS_NAME = "MyPrefsFile";

    StudentOpenHelper studentHelper, examsHelper, examsDoneHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_card);
        tlbStudent = (Toolbar) findViewById(R.id.tlbStudent);
        tlbStudent.setTitle(R.string.strTxtStudentCard);
        tlbStudent.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(tlbStudent);

        tlbStudent.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //Initialize. We need an editor to edit and save changes in shared preferences
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        btnModify = (ToggleButton) findViewById(R.id.tgbModify);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnGoToSensors = (Button) findViewById(R.id.btnSensors);
        name = (TextView) findViewById(R.id.editTextName);
        surname = (TextView) findViewById(R.id.editTextSurname);
        birthday = (TextView) findViewById(R.id.editTextBirthday);
        site = (TextView) findViewById(R.id.editTextSite);
        cellular = (TextView) findViewById(R.id.editTextCellular);
        email = (TextView) findViewById(R.id.editTextEmail);
        examsDone = (TextView) findViewById(R.id.editTextExamsDone);
        examsToDo = (TextView) findViewById(R.id.editTextExamsToDo);
        average = (TextView) findViewById(R.id.editTextAverage);

        rdgGender = (RadioGroup) findViewById(R.id.rdgGender);
        rdbMale = (RadioButton) findViewById(R.id.rdbMale);
        rdbFemale = (RadioButton) findViewById(R.id.rdbFemale);

        //set editText invisible
        name.setVisibility(View.INVISIBLE);
        surname.setVisibility(View.INVISIBLE);
        birthday.setVisibility(View.INVISIBLE);
        site.setVisibility(View.INVISIBLE);
        cellular.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        examsDone.setVisibility(View.INVISIBLE);
        examsToDo.setVisibility(View.INVISIBLE);
        average.setVisibility(View.INVISIBLE);
        rdbFemale.setVisibility(View.INVISIBLE);
        rdbMale.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);


        //clickable editText
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        //Listener Toggle Button
        btnModify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    name.setVisibility(View.VISIBLE);
                    surname.setVisibility(View.VISIBLE);
                    birthday.setVisibility(View.VISIBLE);
                    site.setVisibility(View.VISIBLE);
                    cellular.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    examsDone.setVisibility(View.VISIBLE);
                    examsToDo.setVisibility(View.VISIBLE);
                    average.setVisibility(View.VISIBLE);
                    rdbFemale.setVisibility(View.VISIBLE);
                    rdbMale.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                } else {
                    name.setVisibility(View.INVISIBLE);
                    surname.setVisibility(View.INVISIBLE);
                    birthday.setVisibility(View.INVISIBLE);
                    site.setVisibility(View.INVISIBLE);
                    cellular.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.INVISIBLE);
                    examsDone.setVisibility(View.INVISIBLE);
                    examsToDo.setVisibility(View.INVISIBLE);
                    average.setVisibility(View.INVISIBLE);
                    rdbFemale.setVisibility(View.INVISIBLE);
                    rdbMale.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    birthday.setText("");
                    name.setText("");
                    surname.setText("");
                    birthday.setText("");
                    site.setText("");
                    cellular.setText("");
                    email.setText("");
                    examsDone.setText("");
                    examsToDo.setText("");
                    average.setText("");
                    //clear selection from radio group
                    rdgGender.clearCheck();
                    //clear data from editor
                    clearData(editor);
                }
            }
        });

        //Listener go to sensor list activity
        btnGoToSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSensorsListActivity();
            }
        });

        //Listener Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(editor);
                saveDataToInternal();
                saveDataToExternal();
                saveStudentDataOnDb();
                saveDataExamsOnDb();
                saveDataExamsDoneOnDb();
            }
        });
    }

    //Save data on Database esami_laricchia.db
    public void saveStudentDataOnDb() {
        studentHelper = new StudentOpenHelper(this);
        //gets data in write mode
        SQLiteDatabase db = studentHelper.getWritableDatabase();
        insertStudentData(db);
    }

    public void insertStudentData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(StudentEntry.NAME, name.getText().toString());
        values.put(StudentEntry.SURNAME, surname.getText().toString());
        values.put(StudentEntry.BIRTHDAY, birthday.getText().toString());
        values.put(StudentEntry.SITE, site.getText().toString());
        values.put(StudentEntry.CELLULAR, cellular.getText().toString());
        values.put(StudentEntry.EMAIL, email.getText().toString());
        values.put(StudentEntry.GENDER, rdgGender.toString());
        values.put(StudentEntry.EXAMS_DONE, examsDone.getText().toString());
        values.put(StudentEntry.TODO_EXAMS, examsToDo.getText().toString());
        values.put(StudentEntry.AVERAGE, average.getText().toString());

        //Il secondo parametro solitamente è null per prevenire casi di insert con parametri vuoti
        long newRow = db.insert(StudentEntry.TABLE_NAME,
                null,
                values);
    }

    public void insertExamsData(SQLiteDatabase db) {
        //get Resources from array string in examsArray.xml
        String[] arrayExamsName = getResources().getStringArray(R.array.ExamsList);
        String[] arrayExamsCFU = getResources().getStringArray(R.array.CFUList);
        String[] arrayExamsYear = getResources().getStringArray(R.array.YearList);
        String[] arrayExamsSemester = getResources().getStringArray(R.array.SemesterList);
        //save array length
        final int length = 21;

        for(int i=0; i<length;i++) {
            ContentValues cv = new ContentValues();
            cv.put(ExamEntry.NAME, arrayExamsName[i]);
            cv.put(ExamEntry.CFU, arrayExamsCFU[i]);
            cv.put(ExamEntry.YEAR, arrayExamsYear[i]);
            cv.put(ExamEntry.SEMESTER, arrayExamsSemester[i]);
            long newRow = db.insert(ExamEntry.TABLE_NAME,null,cv);
        }
    }

    public void saveDataExamsOnDb(){
        examsHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsHelper.getWritableDatabase();
        insertExamsData(db);
    }

    public void insertExamsDoneData(SQLiteDatabase db) {
        //get resources from array string in examsDoneArray.xml
        String[] arrayExamsDone = getResources().getStringArray(R.array.ExamsDoneList);
        String[] arrayExamsMark = getResources().getStringArray(R.array.MarkList);
        String[] arrayExamsData = getResources().getStringArray(R.array.DataList);
        final int lengthExDone = 18;

        for(int i=0; i<lengthExDone; i++) {
            ContentValues cv2 = new ContentValues();
            cv2.put(ExamsDoneEntry.NAME, arrayExamsDone[i]);
            cv2.put(ExamsDoneEntry.MARK, arrayExamsMark[i]);
            cv2.put(ExamsDoneEntry.DATA, arrayExamsData[i]);
            long newRow = db.insert(ExamsDoneEntry.TABLE_NAME,null,cv2);
        }
    }

    public void saveDataExamsDoneOnDb() {
        examsDoneHelper = new StudentOpenHelper(this);
        SQLiteDatabase db = examsDoneHelper.getWritableDatabase();
        insertExamsDoneData(db);
    }

    //Save data to Shared Preferences
    public void saveData(SharedPreferences.Editor editor) {
        editor.putString(getString(R.string.strName), name.getText().toString());
        editor.putString(getString(R.string.strTxtSurname), surname.getText().toString());
        editor.putString(getString(R.string.txtBirthday), birthday.getText().toString());
        editor.putString(getString(R.string.strTextSite), site.getText().toString());
        editor.putString(getString(R.string.strTextCellPhone), cellular.getText().toString());
        editor.putString(getString(R.string.strTextEmail), email.getText().toString());
        editor.putString(getString(R.string.strTxtExamsDone), examsDone.getText().toString());
        editor.putString(getString(R.string.strTextExamsToDo), examsToDo.getText().toString());
        editor.putString(getString(R.string.strTextAverageExamsDone), average.getText().toString());
        if(rdbMale.isEnabled()) {
            editor.putString(getString(R.string.strRdbFemale), rdbFemale.getText().toString());
        } else {
            editor.putString(getString(R.string.strRdbMale), rdbMale.getText().toString());
        }
        editor.commit();
        //Toast.makeText(this, getString(R.string.strSavedData), Toast.LENGTH_LONG).show();
    }

    //Retrieve data
    public void retrieveData(SharedPreferences settings) {
        settings.getString(getString(R.string.strName), null);
        settings.getString(getString(R.string.strTxtSurname), null);
        settings.getString(getString(R.string.txtBirthday), null);
        settings.getString(getString(R.string.strTextSite), null);
        settings.getString(getString(R.string.strTextCellPhone), null);
        settings.getString(getString(R.string.strTextEmail), null);
        settings.getString(getString(R.string.strTxtExamsDone), null);
        settings.getString(getString(R.string.strTextExamsToDo), null);
        settings.getString(getString(R.string.strTextAverageExamsDone), null);
        settings.getString(getString(R.string.strRdbFemale), null);
        settings.getString(getString(R.string.strRdbMale), null);
    }

    //Clear data from editor (Shared Preferences)
    public void clearData(SharedPreferences.Editor editor) {
        editor.clear();
        editor.apply();
    }

    //Save data to internal memory
    public void saveDataToInternal() {
        try {
            String FILENAME = "myPrefFile";
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            String fM;
            //check Radio Button value
            if(rdbMale.isEnabled()) {
                fM = rdbFemale.getText().toString();
            } else {
                fM = rdbMale.getText().toString();
            }
            String string = name.getText().toString() + " " + surname.getText().toString() + " " +
                            birthday.getText().toString() + " " + site.getText().toString() + " " +
                            cellular.getText().toString() + " " + email.getText().toString() + " " +
                            examsDone.getText().toString() + " " + examsToDo.getText().toString() + " " +
                            average.getText().toString() + " " + fM;
            /**
             * getBytes():
             * Encodes this String into a sequence of bytes using the platform's
             * default charset, storing the result into a new byte array.
             */
            fos.write(string.getBytes());
            fos.close();
            //Toast.makeText(this, getString(R.string.strSavedData), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
    }

    //Save data to external memory
    public void saveDataToExternal() {
        String FILENAME = "myPrefFile";
        /**
         * Environment: Provides access to environment variables.
         * MEDIA_MOUNTED: Storage state if the media is present and mounted at its
         * mount point with read/write access.
         *
         * getExternalStorageDirectory() is deprecated from API 29.
         * Use File getExternalFilesDir(String type):
         * return: File
         * type: null for root, else other (Environment.DIRECTORY_MUSIC)
         */
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);

        /**
         * getExternalStorageState(): Returns the current state of the primary
         * shared/external storage media.
         */
        String state = Environment.getExternalStorageState();
        //check status of external device: mounted/unmounted
        if(!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        File file = new File(myDir, FILENAME);
        try {
            /**
             * second argument of FileOutputStream constructor indicates whether to append or
             * create new file if one exists
             */
            FileOutputStream fos = new FileOutputStream(file);
            String fM;
            //check Radio Button value
            if(rdbMale.isEnabled()) {
                fM = rdbFemale.getText().toString();
            } else {
                fM = rdbMale.getText().toString();
            }
            String string = name.getText().toString() + " " + surname.getText().toString() + " " +
                    birthday.getText().toString() + " " + site.getText().toString() + " " +
                    cellular.getText().toString() + " " + email.getText().toString() + " " +
                    examsDone.getText().toString() + " " + examsToDo.getText().toString() + " " +
                    average.getText().toString() + " " + fM;
            fos.write(string.getBytes());
            fos.close();
            Toast.makeText(this, getString(R.string.strSavedData), Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            Log.e("ERROR", e.toString());
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        updateLabel(c);
    }

    //update textEdit birthday with date picker input
    private void updateLabel(Calendar c) {
        String s = DateFormat.getDateInstance().format(c.getTime());
        birthday.setText(s);
    }

    //Intent to SensorListActivity
    private void goToSensorsListActivity() {
        Intent goToSensor = new Intent(this, SensorListActivity.class);
        startActivity(goToSensor);
        finish();
    }

    private void goToExamsListActivity() {
        Intent goToExamsListActivity = new Intent(this, ExamsList.class);
        startActivity(goToExamsListActivity);
    }

    private void goToExamsDoneList() {
        Intent goToExamsDoneListActivity = new Intent(this, ExamsDoneList.class);
        startActivity(goToExamsDoneListActivity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_exams, menu);
        return true;
    }

    //gestione click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_ExamsList) {
            goToExamsListActivity();
        } else {
            goToExamsDoneList();
        }
        return true;
    }
}
