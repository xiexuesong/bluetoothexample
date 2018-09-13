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
    private BluetoothServerSocket bluetoothServerSocket ;
    private InputStream inputStream = null;
    private final UUID MY_UUID = UUID
            .fromString("abcd1234-ab12-ab12-ab12-abcdef123456");

    public BleServerSocketThread(BluetoothAdapter bluetoothAdapter) {
        super();
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @Override
    public void run() {
        super.run();
        try {
            bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(bluetoothAdapter.getName(),MY_UUID);
            BluetoothSocket bluetoothSocket = bluetoothServerSocket.accept();
            inputStream = bluetoothSocket.getInputStream();
            byte[] buff = new byte[1024];
            int length = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((length = inputStream.read(buff))!= -1){
                byteArrayOutputStream.write(buff,0,length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
