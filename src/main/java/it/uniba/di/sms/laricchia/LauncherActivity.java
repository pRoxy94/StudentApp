package it.uniba.di.sms.laricchia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import it.uniba.di.sms.laricchia.cannon.CannonApp;
import it.uniba.di.sms.laricchia.favouriteresearch.MainFavoriteSearches;
import it.uniba.di.sms.laricchia.flagquiz.FlagQuizGame;
import it.uniba.di.sms.laricchia.snake.Snake;
import it.uniba.di.sms.laricchia.student.StudentCardActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LauncherActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 1;
    private Button btnMain, btnAlarm, btnContacts, btnStudent, btnFlagQuiz, btnCannon, btnSnake, btnFavRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        btnMain = (Button) findViewById(R.id.btnMainActivity);
        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnContacts = (Button) findViewById(R.id.btnContacts);
        btnStudent = (Button) findViewById(R.id.btnStudentActivity);
        btnFlagQuiz = (Button) findViewById(R.id.btnFlagQuiz);
        btnCannon = (Button) findViewById(R.id.btnCannon);
        btnSnake = (Button) findViewById(R.id.btnSnake);
        btnFavRes = (Button) findViewById(R.id.btnFavRes);

        Toolbar tlb = (Toolbar) findViewById(R.id.toolbar);
        tlb.setLogo(R.mipmap.ic_launcher_round);
        tlb.setTitle("Laricchia");
        tlb.setSubtitle("657914");
        setSupportActionBar(tlb);

        tlb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //Listener Button Main Activity
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();
            }
        });

        //Listener Button Alarm
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlarmActivity();
            }
        });

        //Listener Button Contacts and permissions
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check permissions
                if(ContextCompat.checkSelfPermission(LauncherActivity.this, Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(LauncherActivity.this,
                            Manifest.permission.READ_CONTACTS)) {
                            //We inform users of the usefulness of the permissions
                            new AlertDialog.Builder(LauncherActivity.this)
                                    .setTitle("Permission needed")
                                    .setMessage("This permission is necessary to read your contacts.")
                                    //What happens when ok button is pressed
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ActivityCompat.requestPermissions(LauncherActivity.this,
                                                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
                                        }
                                    })
                                    //What happens when cancel button is pressed
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    //crea e mostra il dialog
                                    .create().show();
                    } else {
                        ActivityCompat.requestPermissions(LauncherActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
                    }
                } else
                    getContacts();
            }
        });

        //Listener Button Student Card
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStudentCardActivity();
            }
        });

        //Listener Button Flag Quiz
        btnFlagQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFlagQuizActivity();
            }
        });

        //Listener Button CannonApp
        btnCannon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCannonAppActivity();
            }
        });

        //Listener Button Snake
        btnSnake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSnakeAppActivity();
            }
        });

        //Listener Button Favourite Research
        btnFavRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFavResAppActivity();
            }
        });
    }

    private void goToFavResAppActivity() {
        Intent goToFavResAppActivity = new Intent(this, MainFavoriteSearches.class);
        startActivity(goToFavResAppActivity);
        //finish();
    }

    private void goToFlagQuizActivity() {
        Intent goToFlagQuizActivity = new Intent(this, FlagQuizGame.class);
        startActivity(goToFlagQuizActivity);
        //finish();
    }

    private void goToCannonAppActivity() {
        Intent goToCannonAppActivity = new Intent(this, CannonApp.class);
        startActivity(goToCannonAppActivity);
        //finish();
    }

    private void goToSnakeAppActivity() {
        Intent goToSnakeAppActivity = new Intent(this, Snake.class);
        startActivity(goToSnakeAppActivity);
        //finish();
    }


    /**
     * Collegamento tra l'activity e il icon_watchword. Prende come argomento un riferimento ad un oggetto Menu che
     * non dobbiamo preoccuparci di istanziare poichè sarà fatto dal sistema.
     * Il metodo verrà invocato una sola volta ovvero alla creazione del icon_watchword, contestualmente alla creazione dell'activity.
     */
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
        inflater.inflate(R.menu.icon_watchword,menu);
        // solo se il valore restituito da OnCreateOptionMenu sarà true allora il icon_watchword sarà attivo
        return true;
    }

    //per la gestione del click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.msg){
            String msg = getString(R.string.strWatchword);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Check requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Intent for contacts
    private void getContacts() {
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        startActivity(intent);
    }

    //Intent for Alarm Activity
    private void goToAlarmActivity(){
        Intent goToAlarm = new Intent(this, AlarmActivity.class);
        startActivity(goToAlarm);
        finish();
    }

    //Intent for Main Activity
    private void goToMainActivity() {
        Intent goToMain = new Intent(this, MainActivity.class);
        startActivity(goToMain);
        finish();
    }

    //Intent for Student Card Activity
    private void goToStudentCardActivity(){
        Intent goToStudentCard = new Intent(this, StudentCardActivity.class);
        startActivity(goToStudentCard);
        finish();
    }
}
