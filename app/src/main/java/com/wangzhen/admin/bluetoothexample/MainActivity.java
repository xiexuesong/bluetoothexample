package com.wangzhen.admin.bluetoothexample;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import callback.DeviceCallBack;
import callback.OnItemClickListener;
import common.BluetoothUtils;

public class MainActivity extends AppCompatActivity implements DeviceCallBack , OnItemClickListener {

    private RecyclerView recyclerView;
    private BluetoothUtils bluetoothUtils;
    private List<BluetoothDevice> list_device;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycleView);

        initListArray();
        initBlueTooth();
        setListViewAdapter();
    }

    private void setListViewAdapter() {
        deviceAdapter = new DeviceAdapter(this);
        recyclerView.setAdapter(deviceAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


    }

    private void initListArray() {
        list_device = new ArrayList<>();

    }

    private void initBlueTooth() {
        bluetoothUtils = new BluetoothUtils(this, this);
    }


    public void click(View view) {
        switch (view.getId()) {
            case R.id.search_bluetooth:
                bluetoothUtils.searchBlueToothDevice();
                break;
            case R.id.cancel_search:
                bluetoothUtils.cancleDiscovery();
                break;
            default:
                break;
        }
    }

    @Override
    public void scanResult(BluetoothDevice bluetoothDevice) {
        list_device.add(bluetoothDevice);
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        bluetoothUtils.connectDevice(list_device.get(position));

    }

    class DeviceAdapter extends RecyclerView.Adapter{

        private OnItemClickListener onItemClickListener;

        public DeviceAdapter(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview, parent, false);
            return new DeviceHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final DeviceHolder deviceHolder = (DeviceHolder) holder;
            BluetoothDevice bluetoothDevice = list_device.get(position);
            if (bluetoothDevice.getName() == null) {
                deviceHolder.tv_name.setText("空名字");
            }else {
                deviceHolder.tv_name.setText(list_device.get(position).getName());
            }
            if (bluetoothDevice.getAddress() == null) {
                deviceHolder.tv_address.setText("空地址");
            }else {
                deviceHolder.tv_address.setText(list_device.get(position).getAddress());
            }
            if (bluetoothDevice.getUuids() == null) {
                deviceHolder.tv_uuid.setText("空UUID");
            }else {
                deviceHolder.tv_uuid.setText(list_device.get(position).getUuids().toString());
            }
            deviceHolder.constrainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v,position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list_device.size();
        }
    }

    class DeviceHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_address;
        TextView tv_uuid;
        ConstraintLayout constrainLayout;

        public DeviceHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_uuid = itemView.findViewById(R.id.tv_uuid);
            constrainLayout = itemView.findViewById(R.id.constrainLayout);
        }

    }
}
