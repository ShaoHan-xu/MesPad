package com.eeka.mespad.bluetoothPrint;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eeka.mespad.bo.BatchLabuRecordPrintBo;
import com.eeka.mespad.bo.BatchSplitPackagePrintBo;
import com.eeka.mespad.bo.PostBatchRecordLabuBo;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.ToastUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.SplitCardDialog;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import zpSDK.zpSDK.zpBluetoothPrinter;

public class BluetoothHelper {
    private static final int DEVICE_TYPE_KEYBOARD = 1344;//蓝牙外输设备
    private static final int DEVICE_TYPE_PRINTER = 1664;//蓝牙打印机

    public static void printLabuInfo(Activity activity, BatchLabuRecordPrintBo data) {
        if (data == null) {
            ErrorDialog.showAlert(activity, "打印内容不能为空");
            return;
        }
        BluetoothDevice device = getBluetoothDevice(activity);
        if (device == null) {
            return;
        }
        zpBluetoothPrinter zpSDK = new zpBluetoothPrinter(activity);

        if (!zpSDK.connect(device.getAddress())) {
            if (!zpSDK.connect(device.getAddress())) {
                ToastUtil.showToast(activity, "蓝牙打印机连接失败!", Toast.LENGTH_LONG);
                return;
            }
        }

        StringBuilder sizeCode = new StringBuilder();
        StringBuilder layer = new StringBuilder();
        for (PostBatchRecordLabuBo.CutSizeBean item : data.getSizeList()) {
            int lays = item.getLayers();
            String code = item.getSizeCode();
            sizeCode.append(code).append(" ");
            if (lays < 10) {
                layer.append("0");
            }
            layer.append(lays).append(" ");
        }

        String rabOrder = data.getRabOrder().replace(data.getShopOrder(), "");
        zpSDK.pageSetup(576, 180);
        zpSDK.drawText(10, 0, "码数：" + sizeCode.toString(), 3, 0, 0, false, false);
        zpSDK.drawText(10, 35, "层数：" + layer.toString(), 3, 0, 0, false, false);
        zpSDK.drawText(10, 70, "款号：" + data.getItem(), 3, 0, 0, false, false);
        zpSDK.drawText(10, 105, "订单号：" + data.getShopOrder(), 3, 0, 0, false, false);
        zpSDK.drawText(10, 140, "拉布单号：" + rabOrder, 3, 0, 0, false, false);

        BatchLabuRecordPrintBo print = new BatchLabuRecordPrintBo();
        print.setMatType(data.getMatType());
        print.setRabOrder(data.getRabOrder());
        String qrCodeText = JSON.toJSONString(print);
        zpSDK.drawQrCode(370, 5, qrCodeText, 0, 5, 0);

        zpSDK.print(0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zpSDK.disconnect();
    }

    public static void printSubPackageInfo(Activity activity, BatchSplitPackagePrintBo data) {
        if (data == null) {
            ErrorDialog.showAlert(activity, "打印内容不能为空");
            return;
        }
        BluetoothDevice device = getBluetoothDevice(activity);
        if (device == null) {
            return;
        }
        zpBluetoothPrinter zpSDK = new zpBluetoothPrinter(activity);

        if (!zpSDK.connect(device.getAddress())) {
            if (!zpSDK.connect(device.getAddress())) {
                ToastUtil.showToast(activity, "蓝牙打印机连接失败!", Toast.LENGTH_LONG);
                return;
            }
        }

        zpSDK.pageSetup(576, 180);
        zpSDK.drawText(20, 0, "包号：" + data.getSubPackageSeq(), 3, 0, 0, false, false);
        zpSDK.drawText(160, 0, "码数：" + data.getSizeCode(), 4, 0, 0, false, false);
        zpSDK.drawText(20, 35, "件数：" + data.getSubPackageQty(), 3, 0, 0, false, false);
        zpSDK.drawText(20, 70, "卡号：" + data.getRfid(), 3, 0, 0, false, false);
        zpSDK.drawText(20, 105, "订单号：" + data.getShopOrder(), 3, 0, 0, false, false);
        zpSDK.drawText(20, 140, "款号：" + data.getItem(), 3, 0, 0, false, false);

        String qrCodeText = data.getRfid();
        zpSDK.drawQrCode(370, 10, qrCodeText, 0, 6, 0);

        zpSDK.print(0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zpSDK.disconnect();
    }

    public static void printSubCardInfo(Activity activity, SplitCardDialog.SplitCardDataBo data) {
        if (data == null || data.getLotInfos() == null) {
            ErrorDialog.showAlert(activity, "打印内容不能为空");
            return;
        }
        BluetoothDevice device = getBluetoothDevice(activity);
        if (device == null) {
            return;
        }
        zpBluetoothPrinter zpSDK = new zpBluetoothPrinter(activity);

        List<SplitCardDialog.SplitCardItemBo> lotInfos = data.getLotInfos();
        int size = lotInfos.size();
        for (int i = 0; i < size; i++) {
            if (!zpSDK.connect(device.getAddress())) {
                if (!zpSDK.connect(device.getAddress())) {
                    ToastUtil.showToast(activity, "connect fail------", Toast.LENGTH_LONG);
                    return;
                }
            }

//            zpSDK.pageSetup(576, 400);
//            SplitCardDialog.SplitCardItemBo cardItemBo = lotInfos.get(i);
//            zpSDK.drawText(80, 350, "工单号：" + data.getShopOrder(), 3, 1, 0, false, false);
//            zpSDK.drawText(160, 350, "款号：" + data.getItem(), 3, 1, 0, false, false);
//            zpSDK.drawText(240, 350, "码数：" + data.getSize(), 3, 1, 0, false, false);
//            zpSDK.drawText(320, 350, "分包号：" + cardItemBo.getNumber(), 3, 1, 0, false, false);
//            zpSDK.drawText(400, 350, "子卡号：" + cardItemBo.getCardId(), 3, 1, 0, false, false);
//            zpSDK.drawText(480, 350, "子卡件数：" + cardItemBo.getSubqty(), 3, 1, 0, false, false);

            zpSDK.pageSetup(576, 180);
            SplitCardDialog.SplitCardItemBo cardItemBo = lotInfos.get(i);
            zpSDK.drawText(30, 10, "工单号：" + data.getShopOrder(), 3, 0, 0, false, false);
            zpSDK.drawText(30, 60, "款号：" + data.getItem(), 3, 0, 0, false, false);
            zpSDK.drawText(320, 60, "分包号：" + cardItemBo.getNumber(), 3, 0, 0, false, false);
            zpSDK.drawText(30, 110, "码数：" + data.getSize(), 3, 0, 0, false, false);
            zpSDK.drawText(320, 110, "子卡件数：" + cardItemBo.getSubqty(), 3, 0, 0, false, false);

            zpSDK.print(0, 1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            zpSDK.disconnect();
        }
    }

    public static void print(Activity activity, String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(activity, "打印内容不能为空", Toast.LENGTH_SHORT);
            return;
        }
        BluetoothDevice device = getBluetoothDevice(activity);
        if (device == null) {
            return;
        }
        zpBluetoothPrinter zpSDK = new zpBluetoothPrinter(activity);
        if (!zpSDK.connect(device.getAddress())) {
            if (!zpSDK.connect(device.getAddress())) {
                ToastUtil.showToast(activity, "connect fail------", Toast.LENGTH_LONG);
                return;
            }
        }

        zpSDK.pageSetup(576, 114);
        zpSDK.drawText(7, 4, content, 3, 0, 0, false, false);
        zpSDK.drawText(195, 4, content, 3, 0, 0, false, false);
        zpSDK.drawText(390, 4, content, 3, 0, 0, false, false);
        zpSDK.print(0, 0);
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
