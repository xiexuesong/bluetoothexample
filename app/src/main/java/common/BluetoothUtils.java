package common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import callback.DeviceCallBack;
import thread.BleServerSocketThread;
import thread.BleSocket;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;

/**
 * Created by admin on 2018/9/12.
 */

public class BluetoothUtils {

    private BluetoothAdapter bluetoothAdapter;

    private List<BluetoothDevice> listDevices;
    private DeviceCallBack deviceCallBack;
    private Context context;
    private BluetoothDevice bluetoothDeviceMatch; //匹配的蓝牙设备
    private BleServerSocketThread bleServerSocketThread = null;
    private BluetoothSocket bluetoothSocket = null;

    private final UUID MY_UUID = UUID
            .fromString("405c9183-6fbf-4ac9-a8a9-e442a3f0de50");

    public BluetoothUtils(DeviceCallBack deviceCallBack, Context context) {
        this.context = context;
        this.deviceCallBack = deviceCallBack;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBroadcast(context);
        initListArray();//初始化可搜索的蓝牙设备集合

        bleServerSocketThread = new BleServerSocketThread(bluetoothAdapter);
        bleServerSocketThread.start();
    }

    private void initListArray() {
        listDevices = new ArrayList<>();//搜索到的设备
    }

    private void initBroadcast(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_DISCOVERY_STARTED);
        filter.addAction(ACTION_DISCOVERY_FINISHED);
        filter.addAction(ACTION_BOND_STATE_CHANGED);
        BlueBroadCastReceiver mReceiver = new BlueBroadCastReceiver();
        context.registerReceiver(mReceiver, filter);
    }

    /**
     * 搜索附近蓝牙设备
     */
    public void searchBlueToothDevice() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 匹配
     */
    public void connectDevice(final BluetoothDevice bluetoothDevice) {
        this.bluetoothDeviceMatch = bluetoothDevice;
        //  bluetoothDevice.createBond();//高于21直接配对
        try {
            if(bluetoothSocket == null) {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }
            Log.i("MDL","connect_state:" + bluetoothSocket.isConnected());
            if(!bluetoothSocket.isConnected()){
                bluetoothSocket.connect();
                Log.i("MDL","conn1111ect_state:" + bluetoothSocket.isConnected());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(){
        if(!bluetoothSocket.isConnected()) {
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("MDL","状态:" + bluetoothSocket.isConnected() );
        OutputStream os = null;
        try {
            os = bluetoothSocket.getOutputStream();
            os.write("小姐姐你好".getBytes());
            Log.i("MDL","状态:" + bluetoothSocket.isConnected());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           /* if (os != null) {
                try {
             //       os.close();
             //       Log.i("MDL","六关闭后:" + bluetoothSocket.isConnected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    /**
     * 得到已经绑定过的蓝牙
     */
    public BluetoothDevice getBindDevice() {
        Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
            return bluetoothDevice;
        }
        return null;
    }

    /**
     * 取消搜索
     */
    public void cancleDiscovery() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }


    }

    private class BlueBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!listDevices.contains(device)) {
                        listDevices.add(device);
                        deviceCallBack.scanResult(device);
                    }
                    break;
                case ACTION_DISCOVERY_STARTED:
                    break;
                case ACTION_DISCOVERY_FINISHED:
                    //搜索结束
                    Log.i("MDL", "搜索结束");
                    break;
                case ACTION_BOND_STATE_CHANGED:
                    if (bluetoothDeviceMatch != null) {
                        Log.i("MDL", "state:" + bluetoothDeviceMatch.getBondState());
                        int state = bluetoothDeviceMatch.getBondState();
                        if (state == 12) {
                            Toast.makeText(context, "配对成功", Toast.LENGTH_SHORT).show();
                            //开启两线程
                            // new BleServerSocketThread(bluetoothAdapter).start();
                            //  new BleSocket(bluetoothDeviceMatch).start();
                        } else if (state == 11) {
                            Toast.makeText(context, "正在配对", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "配对取消", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}

