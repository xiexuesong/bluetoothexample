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
import java.util.ArrayList;
import java.util.List;
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

public class BluetoothUtils extends BluetoothGattCallback {

    private BluetoothAdapter bluetoothAdapter;

    private List<BluetoothDevice> listDevices;
    private DeviceCallBack deviceCallBack;
    private Context context;
    private BluetoothDevice bluetoothDeviceMatch; //匹配的蓝牙设备

    private final UUID MY_UUID = UUID
            .fromString("abcd1234-ab12-ab12-ab12-abcdef123456");

    public BluetoothUtils(DeviceCallBack deviceCallBack, Context context) {
        this.context = context;
        this.deviceCallBack = deviceCallBack;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBroadcast(context);
        initListArray();//初始化可搜索的蓝牙设备集合
        new BleServerSocketThread(bluetoothAdapter).start();
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
    /*    bluetoothAdapter.getBluetoothLeScanner().startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice bluetoothDevice = result.getDevice();
                if(!listDevices.contains(bluetoothDevice)){
                    ScanRecord scanRecord = result.getScanRecord();
                    BleAdvertisedData bleAdvertisedData = BleUtil.parseAdertisedData(scanRecord.getBytes());
                    Log.i("MDL","name:" + bleAdvertisedData.getName() + " uuid:" + bleAdvertisedData.getUuids());
                    listDevices.add(bluetoothDevice);
                    deviceCallBack.scanResult(bluetoothDevice);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.i("MDL","size:" + results.size());
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.i("MDL","errorCode:" + errorCode);
            }
        });*/
    }

    /**
     * 匹配
     */
    public void connectDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDeviceMatch = bluetoothDevice;
      //  bluetoothDevice.createBond();//高于21直接配对
        try {
            BluetoothSocket bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            OutputStream os = bluetoothSocket.getOutputStream();
            os.write("师姐你好".getBytes("utf-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    if(bluetoothDeviceMatch != null) {
                        Log.i("MDL", "state:" + bluetoothDeviceMatch.getBondState());
                        int state = bluetoothDeviceMatch.getBondState();
                        if (state == 12) {
                            Toast.makeText(context, "配对成功", Toast.LENGTH_SHORT).show();
                            //开启两线程
                            new BleServerSocketThread(bluetoothAdapter).start();
                            new BleSocket(bluetoothDeviceMatch).start();
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

