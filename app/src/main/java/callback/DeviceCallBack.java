package callback;

import android.bluetooth.BluetoothDevice;

/**
 * Created by admin on 2018/9/13.
 */

public interface DeviceCallBack {

    void scanResult(BluetoothDevice bluetoothDevice);

}
