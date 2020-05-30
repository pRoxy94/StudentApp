package it.uniba.di.sms.laricchia.database;

import android.provider.BaseColumns;

public class StudentContract {
    //Costruttore. Qualora si istanzi accidentalmente questa classe
    StudentContract() {}

    //Classe interna per tabella studente
    public static final class StudentEntry implements BaseColumns {
        //dichiarazione tabella studente
        public static final String TABLE_NAME = "studente";
        //dichiarazione campi tabella studente
        public static final String ID = "matricola";
        public static final String NAME = "nome";
        public static final String SURNAME = "cognome";
        public static final String BIRTHDAY = "data_di_nascita";
        public static final String SITE = "sito";
        public static final String CELLULAR = "telefono";
        public static final String EMAIL = "email";
        public static final String GENDER = "sesso";
        public static final String EXAMS_DONE = "esami_svolti";
        public static final String TODO_EXAMS = "esami_da_svoglere";
        public static final String AVERAGE = "media";
    }

    public static final class ExamEntry implements BaseColumns {
        //dichiarazione tabella esami
        public static final String TABLE_NAME = "esami_carriera";
        //dichiarazione campi tabella esami
        public static final String ID = "id_esame";
        public static final String NAME = "nome";
        public static final String CFU = "CFU";
        public static final String YEAR = "anno";
        public static final String SEMESTER = "semestre";
    }

    public static final class ExamsDoneEntry implements BaseColumns {
        //dichiarazione tabella esami svolti
        public static final String TABLE_NAME = "esami_sostenuti";

        //dichiarazione campi tabella esami svolti
        public static final String ID = "id_esami_sostenuti";
        public static final String NAME = "nome";
        public static final String DATA = "data";
        public static final String MARK = "voto";
    }
}
