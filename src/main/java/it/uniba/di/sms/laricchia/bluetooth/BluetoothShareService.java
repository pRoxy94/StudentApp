package it.uniba.di.sms.laricchia.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BluetoothShareService {

    /*
    Fortemente ispirato alle API di Google,
    questa classe fornisce le funzionalita' necessarie
    per la connessione con altri dispositivi
     */

    private static final String TAG = "BluetoothShareService";
    private static final String appName = "Laricchia";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("dc40a29e-030f-4ee0-8b8e-28aa1c50393f");
    private final BluetoothAdapter btAdapter;
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;

    Context context;
    ProgressDialog mProgressDialog;

    private ConnectedThread connectedThread;

    public BluetoothShareService(Context context) {
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        start();
    }

    /*
    Questo thread si occupa di restare in ascolto per intercettare
    le richieste di connessione provenienti da altri dispositivi.
    Questo thread sara' in esecuzione fintanto che una connessione
    sara' accettata (o finche' sara' cancellata)
     */
    private class AcceptThread extends Thread {

        //Server socket locale
        private final BluetoothServerSocket btServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                //creazione di un nuovo socket in ascolto
                tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using UUID: " + MY_UUID_INSECURE);

            } catch (IOException e) {
                e.printStackTrace();
            }

            btServerSocket = tmp;
        }

        public void run() {
            //Esecuzione
            Log.d(TAG, "AcceptThread Running");

            BluetoothSocket socket = null;


            try {
                //Questa chiamata fara' tornare un risultato
                //se e solo se si otterra' una connessione riuscita
                //oppure un'eccezione
                Log.d(TAG, "run(): RFCOM server socket start");

                socket = btServerSocket.accept();

            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if(socket != null) {
                connected(socket, mDevice);
            }

            Log.i(TAG, "END acceptThread ");

        }

        //Questo metodo verra' chiamato quando il socket verra' chiuso
        //e quindi non si vorranno piu' accettare richieste
        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");

            try {
                btServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: obj ServerSocket.acceptThread closing failed. "
                        + e.getMessage());
            }
        }


    }


    private class ConnectThread extends Thread {
        /*
        Questo thread va in esecuzione mentre si effettua un tentativo
        di connessione esterna con un altro dispositivo. La connessione
        puo' avere successo o fallire.
         */

        /*
        Prende la socket e si connette ad essa
        (quella del thread precedente (AcceptThread))
         */

        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            mDevice = device;
            deviceUUID = uuid;
        }

        //Automatically runs
        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            //Vogliamo ottenere una bluetooth socket
            //per la connessione con il dato dispositivo esterno
            try {
                Log.d(TAG, "ConnectThread: trying to create InsecureRfcommSocket using  (MY) UUID: "
                                + MY_UUID_INSECURE);
                tmp = mDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: could not create InsecureRfcommSocket: "
                        + e.getMessage());
            }

            socket = tmp;

            //Bisogna sempre annullare la discovery prima della connessione
            //poiche' potrebbe rallentare quest'ultima
            btAdapter.cancelDiscovery();

            //Effettuo la connessione al BluetoothSocket

            try {
                //Chiamata bloccante: due possibilita'
                //puo' avere successo o andare in eccezione
                socket.connect();

            } catch(IOException e) {

                //Chiudiamo la socket
                try {
                    socket.close();
                    Log.d(TAG, "Socket closed");
                } catch (IOException ex) {
                    Log.e(TAG, "connectThread: unable to close connection in socket" + e.getMessage());
                }
                //Mostriamo un messaggio nel log degli errori
                Log.d(TAG, "run: connectThread: Could not connect to UUID" + MY_UUID_INSECURE);
            }

            connected(socket, mDevice);
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket");
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel(): close() of socket in ConnectThread failed " + e.getMessage());
            }
        }

        /**
         * Questo metodo inizia un servizio di scambio messaggi.
         * Per la precisione inizializza AcceptThread (non ConnectThread)
         * per iniziare una sessione di ascolto lato server.
         * Questo metodo è chiamato dal metodo di callback
         * BluetoothActivity.onResume()
         */

    }

    public synchronized void start() {
        Log.d(TAG, "Start!");

        //Se un AcceptThread gia' c'e' lo cancelliamo
        //e ne creiamo uno nuovo
        //altrimenti ne crea uno nuovo

        if(connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if(insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread();
            //metodo start() si riferisce al nativo di Thread
            insecureAcceptThread.start();
        }
    }

    /**
     *
     * @param localDevice
     * @param localUUID
     * AcceptThread va in esecuzione e aspetta una connessione esterna.
     * Dopodiche' ConnectThread andra' in esecuzione e fara' dei tentativi
     * di connessione agli AcceptThread di altri dispositivi
     */
    public void startClient(BluetoothDevice localDevice, UUID localUUID) {

        //Inizializzazione ConnectThread
        Log.d(TAG, "startClient: Started.");

        //dialog
        mProgressDialog = ProgressDialog.show(context, "Connecting Bluetooth",
                "Please wait...", true);

        connectThread = new ConnectThread(localDevice, localUUID);
        connectThread.start();
    }

    /**
     * Questo terzo thread si occupa di cio' che avviene una volta effettivamente
     * connessi al dispositivo, quindi manterra' la connessione attiva e gestira'
     * l'invio e la ricezione dei dati utilizzando InputStream e OutputStream
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket btSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        //Gestione dell'input e dell'output alla creazione
        public ConnectedThread(BluetoothSocket localSocket) {
            Log.d(TAG, "startClient: Started.");

            btSocket = localSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //Chiudiamo il dialog precedentemente aperto in fase di connessione
            mProgressDialog.dismiss();

            //ottenimento input e output
            try {
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();
            } catch(IOException e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            //Buffer per lo stream
            byte[] buffer = new byte[1024];

            //byte che tornano dalla lettura con read()
            int bytes;

            //Restiamo in ascolto perpetuamente finche'
            //non viene lanciata un'eccezione
            while(true) {

                //Leggiamo da InputStream
                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "Incoming message: " + incomingMessage);
                    Intent incomingMessageIntent = new Intent("incoming message");
                    incomingMessageIntent.putExtra("Message", incomingMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(incomingMessageIntent);

                } catch (IOException e) {
                    Log.e(TAG, "write: error reading inputStream: " + e.getMessage());
                    break;
                }
            }
        }

        /**
         * Chiamata dalla BluetoothActivity per inviare data dal dispositivo remoto.
         * Nel nostro caso si occupera' di prendere la nostra media
         * @param bytes
         */
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: writing to outputStream: " + text);

            //Scrittura
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                //Errore di scrittura
                Log.e(TAG, "write: error writing to outputStream: " + e.getMessage()); }
        }

        //Nel caso si desideri eliminare la connessinoe con l'altro dispositivo
        //Chiamato dal sistema
        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connected() method Starting...");

        //Start the thread to manage the connection and perform transmission
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    /**
     * Scrive su ConnectedThread
     * in maniera non sincronizzata
     */
    public void write(byte[] out) {
        //Temporary object
        ConnectedThread r;

        //Sincronizzazione di una copia di connected thread
        Log.d(TAG, "write: write() called");
        //Effettuo scrittura
        connectedThread.write(out);
    }
}
