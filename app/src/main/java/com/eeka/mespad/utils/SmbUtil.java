package com.eeka.mespad.utils;

import com.alibaba.fastjson.JSON;
import com.eeka.mespad.bo.PositionInfoBo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 * 远程共享文件夹操作类
 */
public class SmbUtil {

    private static String domainIp = "10.7.121.10";
    private static String username = "eeka";
    private static String password = "Gst#2017";
    private static String remoteUrl = "smb://10.7.121.10/eeka/nc_code";

    static {
        String s = SpUtil.get(SpUtil.KEY_NCIMG_INFO, null);
        PositionInfoBo.NCImgInfo imgInfo = JSON.parseObject(s, PositionInfoBo.NCImgInfo.class);
        domainIp = imgInfo.getPICTURE_IP();
        username = imgInfo.getPICTURE_USER();
        password = imgInfo.getPICTURE_PASSWD();
        remoteUrl = imgInfo.getPICTURE_REMOTE();
    }

    public static void createDir(String dir) throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domainIp, username, password);  //先登录验证
        SmbFile fp = new SmbFile(remoteUrl + "//" + dir, auth);
        System.out.println("fieldir+++++++++++++++++++++=" + remoteUrl + "//" + dir);
        // 目录已存在创建文件夹
        if (!fp.exists() || !fp.isDirectory()) {
            // 目录不存在的情况下，则创建
            fp.mkdirs();
        }
    }

    public static void copyDir(String fileName) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File fp = new File(fileName);
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domainIp, username, password);  //先登录验证
            SmbFile remoteFile = new SmbFile(remoteUrl + "//" + fp.getName(), auth);
            System.out.println("remoteFile+++++++++++++++++++++=" + remoteFile);
            in = new BufferedInputStream(new FileInputStream(fp));
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            // 刷新此缓冲的输出流
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把本地磁盘中的文件上传到局域网共享文件下
     *
     * @param localFilePath 本地路径 如：D:/
     */
    public static boolean smbPut(String localFilePath) {
        //从本地读取文件上传到服务主机中
        try {
            File localFile = new File(localFilePath);
            InputStream ins = new FileInputStream(localFile);
            return smbPut(ins, localFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 把本地磁盘中的文件上传到局域网共享文件下
     */
    public static boolean smbPut(InputStream ins, String fileName) {
        boolean success = true;
        //从本地读取文件上传到服务主机中
        OutputStream outs = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domainIp, username, password);  //先登录验证
            SmbFile smbfile = new SmbFile(remoteUrl + File.separator + fileName, auth);
            if (smbfile.exists()) {
                System.out.println("file is exists");
            } else {
                smbfile.connect();
                outs = new SmbFileOutputStream(smbfile);
                byte[] buffer = new byte[4096];
                int len; //读取长度
                while ((len = ins.read(buffer, 0, buffer.length)) != -1) {
                    outs.write(buffer, 0, len);
                }
                outs.flush(); //刷新缓冲的输出流
                System.out.println("图片上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (ins != null)
                    ins.close();
                if (outs != null)
                    outs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
