package com.eeka.mespad.bluetoothPrint;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.ToastUtil;

import java.lang.reflect.Method;
import java.util.Set;

import zpSDK.zpSDK.zpBluetoothPrinter;

public class BluetoothHelper {
    private static final int DEVICE_TYPE_KEYBOARD = 1344;//蓝牙外输设备
    private static final int DEVICE_TYPE_PRINTER = 1664;//蓝牙打印机

    public static void Print(Activity activity, String content) {
        zpBluetoothPrinter zpSDK = new zpBluetoothPrinter(activity);
        BluetoothDevice device = getBluetoothDevice(activity);
        if (device == null) {
            return;
        }
        if (!zpSDK.connect(device.getAddress())) {
            Toast.makeText(activity, "connect fail------", Toast.LENGTH_LONG).show();
            return;
        }

        zpSDK.pageSetup(576, 100);
        zpSDK.drawText(5, 4, content, 3, 0, 0, false, false);
        zpSDK.drawText(190, 4, content, 3, 0, 0, false, false);
        zpSDK.drawText(390, 4, content, 3, 0, 0, false, false);
        zpSDK.print(0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zpSDK.disconnect();
    }

    private static BluetoothDevice getBluetoothDevice(Activity activity) {
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            ToastUtil.showToast(activity, "该设备不支持蓝牙功能", Toast.LENGTH_LONG);
            return null;
        }

        if (!myBluetoothAdapter.isEnabled()) {
            ToastUtil.showToast(activity, "请打开蓝牙开关,并配对蓝牙打印机...", Toast.LENGTH_LONG);
            SystemUtils.startBluetoothSettingView(activity);
            return null;
        }

        Set<BluetoothDevice> bondedDevices = myBluetoothAdapter.getBondedDevices();
        BluetoothDevice myDevice = null;
        for (BluetoothDevice device : bondedDevices) {
            if (device.getBluetoothClass().getDeviceClass() == DEVICE_TYPE_PRINTER) {
                myDevice = device;
                break;
            }
        }
        if (myDevice == null) {
            ToastUtil.showToast(activity, "未配对蓝牙打印机,请配对蓝牙打印机...", Toast.LENGTH_LONG);
//            SystemUtils.startBluetoothSettingView(activity);
            return null;
        }
        return myDevice;
    }

    /**
     * 是否连接蓝牙扫码设备
     */
    public static boolean isConnectedScannerDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
//            ToastUtil.showToast(context, "该设备不支持蓝牙功能", Toast.LENGTH_LONG);
            return false;
        }

        if (!adapter.isEnabled()) {
//            ToastUtil.showToast(context, "请打开蓝牙开关,并配对相应蓝牙设备.", Toast.LENGTH_LONG);
//            SystemUtils.startBluetoothSettingView(context);
            return false;
        }

        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Set<BluetoothDevice> devices = adapter.getBondedDevices();

                for (BluetoothDevice device : devices) {
                    int deviceClass = device.getBluetoothClass().getDeviceClass();
                    if (deviceClass == DEVICE_TYPE_KEYBOARD) {
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        method.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                        if (isConnected) {
                            Logger.d("已连接蓝牙设备名称：" + device.getName() + ", 型号：" + deviceClass);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
