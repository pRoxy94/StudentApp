package it.uniba.di.sms.laricchia.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import it.uniba.di.sms.laricchia.database.StudentContract.*;
import androidx.annotation.Nullable;

public class StudentOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    //dichiarazione db
    private static final String DATABASE_NAME = "esami_laricchia.db";

    //creazione tabelle
    private static final String STUDENT_CREATE_TABLE = "CREATE TABLE " + StudentEntry.TABLE_NAME + "("
            + StudentEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StudentEntry.NAME + " TEXT, "
            + StudentEntry.SURNAME + " TEXT, "
            + StudentEntry.BIRTHDAY + " TEXT, "
            + StudentEntry.SITE + " TEXT, "
            + StudentEntry.CELLULAR + " INT, "
            + StudentEntry.EMAIL + " TEXT, "
            + StudentEntry.GENDER + " TEXT, "
            + StudentEntry.EXAMS_DONE + " INTEGER, "
            + StudentEntry.TODO_EXAMS + " INTEGER, "
            + StudentEntry.AVERAGE + " INTEGER)";

    private static final String EXAM_CREATE_TABLE = "CREATE TABLE " + ExamEntry.TABLE_NAME + "("
            + ExamEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ExamEntry.NAME + " TEXT, "
            + ExamEntry.CFU + " INTEGER, "
            + ExamEntry.YEAR + " INTEGER, "
            + ExamEntry.SEMESTER + " INTEGER);";

    private static final String EXAMSDONE_CREATE_TABLE = "CREATE TABLE " + ExamsDoneEntry.TABLE_NAME + "("
            + ExamsDoneEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ExamsDoneEntry.NAME + " TEXT, "
            + ExamsDoneEntry.DATA + " TEXT, "
            + ExamsDoneEntry.MARK + " INT);";

    public StudentOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STUDENT_CREATE_TABLE);
        db.execSQL(EXAM_CREATE_TABLE);
        db.execSQL(EXAMSDONE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
