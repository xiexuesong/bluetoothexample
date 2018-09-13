package thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 *
 * 接收数据
 * Created by admin on 2018/9/13.
 */

public class BleSocket extends Thread {

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream = null;
    private final UUID MY_UUID = UUID
            .fromString("abcd1234-ab12-ab12-ab12-abcdef123456");

    public BleSocket(BluetoothDevice bluetoothDeviceMatch) {
        super();
        this.bluetoothDevice = bluetoothDeviceMatch;
    }

    @Override
    public void run() {
        super.run();
        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            outputStream = bluetoothSocket.getOutputStream();
            byte[] data = new byte[1024];
            outputStream.write("你好世界".getBytes());
            outputStream.flush();
          //  Log.i("MDL",new String(data));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
