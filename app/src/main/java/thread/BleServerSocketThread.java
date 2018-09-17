package thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 发送数据
 * Created by admin on 2018/9/13.
 */

public class BleServerSocketThread extends Thread {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;
    private InputStream inputStream = null;
    private final UUID MY_UUID = UUID
            .fromString("405c9183-6fbf-4ac9-a8a9-e442a3f0de50");
    private BluetoothSocket bluetoothSocket;

    public BleServerSocketThread(BluetoothAdapter bluetoothAdapter) {
        super();
        this.bluetoothAdapter = bluetoothAdapter;

    }

    @Override
    public void run() {
        super.run();
        try {
            bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothAdapter.getName(), MY_UUID);
            bluetoothSocket = bluetoothServerSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                inputStream = bluetoothSocket.getInputStream();
                byte[] buff = new byte[20];
                int length ;
                int lengthSum  = 0 ;
                Log.i("MDL","师姐你好");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while ((length = inputStream.read(buff)) != -1){
                    byteArrayOutputStream.write(buff,0,length);
                    lengthSum = lengthSum + length;
                    if(lengthSum >= length){
                        break;
                    }
                }
                Log.i("MDL",byteArrayOutputStream.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
