package it.uniba.di.sms.laricchia.bluetooth;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;

import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import it.uniba.di.sms.laricchia.R;

public class BluetoothActivity extends AppCompatActivity {

    public static final UUID MY_UUID_INSECURE = UUID.fromString("dc40a29e-030f-4ee0-8b8e-28aa1c50393f");
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = ".BluetoothActivity";
    ParcelUuid[] deviceUUID;

    private BluetoothAdapter btAdapter;

    private Button btnScan, btnShow, btnSend;
    private Switch stcEnabled, stcVisible;
    private TextView yourAverage, yourFriendsAverage;
    private ListView lstPairedDevices, lstNewDevices;
    private Toolbar tlbBluetooth;
    private String average;

    private ArrayList<BluetoothDevice> devices;
    private ArrayAdapter btFoundArrayAdapter;
    BluetoothShareService btShareService;
    BluetoothDevice btDevice;

    IntentFilter scanIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //toolbar
        tlbBluetooth = (Toolbar) findViewById(R.id.tlbBluetooth);
        tlbBluetooth.setNavigationIcon(R.drawable.ic_arrow_back);
        tlbBluetooth.setTitle(R.string.strTxtBluetooth);

        //Textview
        yourAverage = (TextView) findViewById(R.id.txtYourAverage);
        yourFriendsAverage = (TextView) findViewById(R.id.txtYourFriendsAverage);
        LocalBroadcastManager.getInstance(this).registerReceiver(averageReceiver,
                new IntentFilter("incomingMessage"));

        //retrieve average from statistics activity
        average = getIntent().getExtras().getString("Average");
        yourAverage.setText(getString(R.string.strTxtYourAverage));
        yourAverage.append(" " + average);

        //listview
        lstPairedDevices = (ListView) findViewById(R.id.lstPairedDevices);
        lstNewDevices = (ListView) findViewById(R.id.lstNewDevices);

        //Switch Button Enabled and Visible
        stcEnabled = (Switch) findViewById(R.id.stcEnabled);
        stcVisible = (Switch) findViewById(R.id.stcVisible);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnSend = (Button) findViewById(R.id.btnSend);

        //Adapter Bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        devices = new ArrayList<>();

        btEnabled();

        btDiscoverable();

        //show bt paired devices
        scanBtPairedDevices();

        //scan new devices
        scanNewDevices();

        registerReceiver(scanModeReceiver, scanIntentFilter);

        tlbBluetooth.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String average = getIntent().getExtras().getString("Average");
                byte[] bytes = average.getBytes(Charset.defaultCharset());
                btShareService.write(bytes);

                //Toast.makeText(getApplicationContext(), "Average info sent to device. Giannelli has the highest average! ", Toast.LENGTH_LONG).show();
            }
        });

        //clickable list
        lstNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btAdapter.cancelDiscovery();
                String deviceAddress = devices.get(i).getAddress();
                String deviceName = devices.get(i).getName();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    Log.d(TAG, "Trying to pair with " + deviceName);
                    devices.get(i).createBond();
                    btDevice = devices.get(i);
                    deviceUUID = btDevice.getUuids();
                    btShareService = new BluetoothShareService(BluetoothActivity.this);
                }
            }
        });

        lstPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startConnection();
            }
        });
    }


    public void startConnection() {
        startBTConnection(btDevice, MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");
        btShareService.startClient(device, uuid);

    }
    //BR for average
    BroadcastReceiver averageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //retrieve data average
            String text = intent.getStringExtra("theMessage");
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    };

    //BR for scan mode
    BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int modeValue = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                if(modeValue==BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
                    Toast.makeText(context, getString(R.string.strReceiveConnection),
                            Toast.LENGTH_LONG).show();
                } else if (modeValue==BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Toast.makeText(context, getString(R.string.strDiscModeOk),
                            Toast.LENGTH_LONG).show();
                } else if (modeValue==BluetoothAdapter.SCAN_MODE_NONE) {
                    Toast.makeText(context, getString(R.string.strDiscModeOk),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getString(R.string.strDiscError),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    //BR for pairing mode
    BroadcastReceiver pairingBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //case 1
                if (bd.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                    btDevice = bd;
                }

                //case 2
                if (bd.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                }

                //case 3
                if (bd.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };


    //Broadcast Receiver
    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);

                //List<String> btNames = new ArrayList<>();
                //for (BluetoothDevice btDevice : devices) {
                //    btNames.add(btDevice.getName());
                //}

                btFoundArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.adapter_list_bt_devices, devices);
                lstNewDevices.setAdapter(btFoundArrayAdapter);
                //Toast.makeText(getApplicationContext(), "Device found!", Toast.LENGTH_LONG).show();
            }
        }
    };


    //Scan new devices
    public void scanNewDevices() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                    checkBtPermission();
                    btAdapter.startDiscovery();

                    //Register for broadcasts when a device is discovered
                    IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));

                    registerReceiver(br, intentFilter);
                    //Log.d("Laricchia", "OK");
                }

                if (!btAdapter.isDiscovering()) {
                    checkBtPermission();
                    btAdapter.startDiscovery();

                    //Register for broadcasts when a device is discovered
                    IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));

                    registerReceiver(br, intentFilter);
                    //Log.d("Laricchia", "OK");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBtPermission() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.d("Check permissions", "No need to check permission. SDK version < LOLLIPOP");
        }
    }

    //Check bluetooth enabled/disabled
    public void btEnabled() {
        stcEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(BluetoothActivity.this, R.string.strTxtOn, Toast.LENGTH_SHORT).show();
                    if(btAdapter == null) {
                        Toast.makeText(getApplicationContext(),"Bluetooth not available on this device", Toast.LENGTH_SHORT).show();
                    } else {
                        if(!btAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }
                } else {
                    Toast.makeText(BluetoothActivity.this, R.string.strTxtOff, Toast.LENGTH_SHORT).show();
                    if(btAdapter.isEnabled()) {
                        btAdapter.disable();
                    }
                }
            }
        });
    }

    //set device discoverable to other device
    public void btDiscoverable() {
        stcVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!btAdapter.isDiscovering()){
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),1);
                    Toast.makeText(getApplicationContext(),"Making Device Discoverable",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth enabling cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Scan bluetooth paired devices
    public void scanBtPairedDevices() {
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt = btAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device:bt) {
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.adapter_list_bt_devices, strings);
                    lstPairedDevices.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }
}
