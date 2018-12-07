package com.eeka.mespad.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.CutRecordQtyBo;
import com.eeka.mespad.bo.GetLabuDataBo;
import com.eeka.mespad.bo.SaveClothSizeBo;
import com.eeka.mespad.bo.SaveLabuDataBo;
import com.eeka.mespad.bo.StartWorkParamsBo;
import com.eeka.mespad.bo.UpdateSewNcBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.NetUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.view.dialog.SplitCardDialog;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.LogUtil;
import cn.finalteam.okhttpfinal.Part;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 网络交互类
 * Created by Lenovo on 2017/5/12.
 */

public class HttpHelper {
    private static final String STATE = "status";
    private static final String MESSAGE = "message";
    private static boolean IS_COOKIE_OUT;
    private static final String COOKIE_OUT = "SecurityException: Authorization failed.";//cookie过期
    private static String PAD_IP;

    private static String BASE_URL = PadApplication.BASE_URL;

    public static final String login_url = BASE_URL + "login?";
    public static final String logout_url = BASE_URL + "logout?";
    public static final String loginByCard_url = BASE_URL + "loginByCard?";
    public static final String positionLogin_url = BASE_URL + "position/positionLogin?";
    public static final String positionLogout_url = BASE_URL + "position/positionLogout?";
    public static final String getPositionLoginUser_url = BASE_URL + "position/getPositionLoginUser?";
    public static final String queryPositionByPadIp_url = BASE_URL + "position/getPositionContext?";
    public static final String findProcessWithPadId_url = BASE_URL + "cutpad/findPadBindOperations?";
    public static final String viewCutPadInfo_url = BASE_URL + "cutpad/viewCutPadInfor?";
    public static final String startBatchWork_url = BASE_URL + "product/startByProcessLot?";
    public static final String startCustomWork_url = BASE_URL + "product/startByShopOrder?";
    public static final String completeBatchWork_url = BASE_URL + "product/completeByProcessLot?";
    public static final String completeCustomWork_url = BASE_URL + "product/completeByShopOrder?";
    public static final String getWorkOrderList_url = BASE_URL + "cutpad/viewJobOrderList?";
    public static final String saveLabuData = BASE_URL + "cutpad/saveRabData?";
    public static final String getLabuData = BASE_URL + "rabDataCollection/dataInit?";
    public static final String saveOrUpdateLabuData = BASE_URL + "rabDataCollection/saveOrUpdateRabData?";
    public static final String saveLabuDataAndComplete = BASE_URL + "cutpad/saveRabDataAndComplete?";
    public static final String getBadList = BASE_URL + "logNcPad/listNcCodesOnOperation?";
    public static final String saveBadRecord = BASE_URL + "logNcPad/logNc?";
    public static final String signoffByShopOrder = BASE_URL + "product/signoffByShopOrder?";
    public static final String signoffByProcessLot = BASE_URL + "product/signoffByProcessLot?";
    public static final String getSewData = BASE_URL + "sweing/findPadKeyData?";
    public static final String getCardInfo = BASE_URL + "cutpad/cardRecognition?";
    public static final String cutMaterialReturnOrFeeding = BASE_URL + "cutpad/cutMaterialReturnOrFeeding?";
    public static final String hangerBinding = BASE_URL + "hanger/bind?";
    public static final String hangerUnbind = BASE_URL + "hanger/unbind?";
    public static final String getSuspendBaseData = BASE_URL + "bandpad/initial?";
    public static final String getSuspendUndoList = BASE_URL + "bandpad/getSfcs?";
    public static final String getSfcComponents = BASE_URL + "bandpad/getSfcComponents?";
    public static final String getComponentInfo = BASE_URL + "bandpad/getComponentInfo?";
    public static final String findPadKeyDataForNcUI = BASE_URL + "sweing/findPadKeyDataForNcUI?";
    public static final String getProductComponentList = BASE_URL + "logNcPad/listProdComponentsOnCompleteOpers?";
    public static final String getDesignComponentList = BASE_URL + "logNcPad/listDesgComponentByProdComp?";
    public static final String getSewNcCodeList = BASE_URL + "logNcPad/listNcCodesOnDesgComponent?";
    public static final String getProcessWithNcCode = BASE_URL + "logNcPad/listOperationsOnNcCode?";
    public static final String getRepairProcess = BASE_URL + "logNcPad/listOpersByProdComponent?";
    public static final String recordSewNc = BASE_URL + "logNcPad/logNcOnSew?";
    public static final String createCard = BASE_URL + "sweing/bindSfcAndRfid?";
    public static final String findCardInfoBySfcOrHangerId = BASE_URL + "sweing/findCardInfoBySfcOrHangerId?";
    public static final String tellFusingStyleToGST = BASE_URL + "tellFusingStyleToGST?";
    public static final String getClothSize = BASE_URL + "logNcPad/viewGarmentSize?";
    public static final String saveQCClothSizeData = BASE_URL + "logNcPad/saveDcCollect?";
    public static final String initNcForQA = BASE_URL + "logNcPad/initNcForQA?";
    public static final String qaToQc = BASE_URL + "/logNcPad/qaToQc?";
    public static final String getBomInfo = BASE_URL + "sweing/getBomInfo?";
    public static final String getDictionaryData = BASE_URL + "common/getDictionaryData?";
    public static final String automaticPicking = BASE_URL + "cutpad/automaticPicking?";
    public static final String getOutlineInfo = BASE_URL + "syncCraftPic/craficInfo?";
    public static final String findRfidInfo = BASE_URL + "sweing/findRfidInfo?";
    public static final String saveSubcontractInfo = BASE_URL + "sweing/saveSubcontractInfo?";
    public static final String sewSubStart = BASE_URL + "sweing/subcontractStart?";
    public static final String getCutRecordData = BASE_URL + "cutpad/viewCutRecordList?";
    public static final String saveCutRecordData = BASE_URL + "cutpad/saveCutRecord?";
    public static final String getUserInfo = BASE_URL + "cutpad/userCardRecognition?";
    public static final String getStitchInventory = BASE_URL + "stitchPad/getStitchInventory?";
    public static final String getEmbroiderInfor = BASE_URL + "cutpad/viewEmbroiderPadInfor?";
    public static final String getReworkInfo = BASE_URL + "/sweing/findReworkInfoBySfcRef?";
    public static final String getCommonInfoByLogicNo = BASE_URL + "/ReportController/reportViewByLogic?";
    public static final String getPattern = BASE_URL + "/cutpad/viewembroiderPicture?";
    public static final String productOff = BASE_URL + "hanger/productOff?";
    public static final String productOn = BASE_URL + "hanger/productOn?";
    public static final String viewCutPadInforByShopOrder = BASE_URL + "cutpad/viewCutPadInforByInput?";
    public static final String listOffLineReWorkInfo = BASE_URL + "logNcPad/listOffLineReWorkInfo?";
    public static final String splitCard = BASE_URL + "cutpad/subPackage?";
    public static final String offlineSort = BASE_URL + "sort/bindingdefaultSortRfidBySFC?";
    public static final String sortForClothTag = BASE_URL + "sort/sendSortMessageByClothCard?";
    public static final String getQCSize = BASE_URL + "sweing/getShopOrderSize?";
    public static final String replaceBindingsRfid = PadApplication.XMII_URL + "Runner?";
    public static final String checkItemAndSize = PadApplication.XMII_URL + "Runner?";
    private static Context mContext;

    private static HttpRequest.HttpRequestBo mCookieOutRequest;//记录cookie过期的请求，用于重新登录后再次请求

    static {
        mContext = PadApplication.mContext;
    }

    /**
     * 根据吊牌走分拣系统
     */
    public static void sortForClothTag(String salesOrder, String hangerId, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SALES_ORDER", salesOrder);
        json.put("SORT_RFID", hangerId);
        params.put("params", json.toString());
        HttpRequest.post(sortForClothTag, params, getResponseHandler(sortForClothTag, callback));
    }

    /**
     * 检查款式与尺码
     */
    public static void checkItemAndSize(String line, String position, String itemSize,HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("Transaction", "EEKA_EXT/TRANS/Z_MES_HANGER_DATA_TO_WMS/TRANSACTION/checkItemAndSize");
        params.put("OutputParameter", "resultJson");
        params.put("Content-Type", "text/json");
        params.put("line", line);
        params.put("position", position);
        params.put("itemSize", itemSize);
        HttpRequest.post(checkItemAndSize, params, getResponseHandler(checkItemAndSize, callback));
    }

    /**
     * 更换衣架
     */
    public static void replaceBindingsRfid(String oldRFID, String newRFID, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("Transaction", "EEKA_EXT/TRANS/Z_MES_HANGER_DATA_TO_WMS/TRANSACTION/updateSortRfid");
        params.put("OutputParameter", "resultJson");
        params.put("Content-Type", "text/json");
        params.put("oldRfid", oldRFID);
        params.put("newRfid", newRFID);
        HttpRequest.post(replaceBindingsRfid, params, getResponseHandler(replaceBindingsRfid, callback));
    }

    /**
     * 获取质检尺寸
     */
    public static void getQCSize(String shopOrder, String size, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("shopOrder", shopOrder);
        params.put("sizeCode", size);
        HttpRequest.post(getQCSize, params, getResponseHandler(getQCSize, callback));
    }

    /**
     * 线下分拣
     */
    public static void offlineSort(HttpCallback callback) {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("STATION_ID", contextInfo.getPOSITION());
        json.put("LINE_ID", contextInfo.getLINE_CATEGORY());
        params.put("params", json.toString());
        HttpRequest.post(offlineSort, params, getResponseHandler(offlineSort, callback));
    }

    /**
     * 分包制卡
     */
    public static void splitCard(SplitCardDialog.SplitCardDataBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
//        JSONObject json = new JSONObject();
//        json.put("processLotBo", data.getProcessLotBo());
//        json.put("lotInfos", data.getLotInfos());
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(splitCard, params, getResponseHandler(splitCard, callback));
    }

    /**
     * 根据PAD的IP地址查询站点的相关信息
     */
    public static void initData(HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        params.put("params", json.toJSONString());
        HttpRequest.post(queryPositionByPadIp_url, params, getResponseHandler(queryPositionByPadIp_url, callback));
    }

    /**
     * 登录
     *
     * @param user     账号
     * @param pwd      密码
     * @param callback 回调
     */
    public static void login(String user, String pwd, HttpCallback callback) {
        RequestParams params = new RequestParams();
        params.put("j_username", user);
        params.put("j_password", pwd);
        HttpRequest.post(login_url, params, getResponseHandler(login_url, callback));
    }

    /**
     * 卡号密码登录
     *
     * @param cardId   卡号
     * @param pwd      密码
     * @param callback 回调
     */
    public static void loginByCard(String cardId, String pwd, HttpCallback callback) {
        RequestParams params = new RequestParams();
        params.put("cardId", cardId);
        params.put("passwd", pwd);
        HttpRequest.post(loginByCard_url, params, getResponseHandler(loginByCard_url, callback));
    }

    /**
     * 登出
     */
    public static void logout(HttpCallback callback) {
        RequestParams params = getBaseParams();
        HttpRequest.post(logout_url, params, getResponseHandler(logout_url, callback));
    }

    /**
     * 站位登录
     *
     * @param cardId   卡号
     * @param callback 回调
     */
    public static void positionLogin(String cardId, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("CARD_ID", cardId);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(positionLogin_url, params, getResponseHandler(positionLogin_url, callback));
    }

    /**
     * 站点登出
     *
     * @param cardId   卡号
     * @param callback 回调
     */
    public static void positionLogout(String cardId, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("CARD_ID", cardId);
        params.put("params", json.toJSONString());
        HttpRequest.post(positionLogout_url, params, getResponseHandler(positionLogout_url, callback));
    }

    /**
     * 获取当前站位登录的人员
     */
    public static void getPositionLoginUsers(HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        params.put("params", json.toJSONString());
        HttpRequest.post(getPositionLoginUser_url, params, getResponseHandler(getPositionLoginUser_url, callback));
    }

    /**
     * 查询当前平板绑定的工序
     */
    public static void findProcessWithPadId(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(findProcessWithPadId_url, params, getResponseHandler(findProcessWithPadId_url, callback));
    }

    /**
     * 根据RFID卡号获取信息、定制订单/批量订单/员工
     *
     * @param cardId 卡号
     */
    public static void getCardInfo(String cardId, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("RFID", cardId);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(getCardInfo, params, getResponseHandler(getCardInfo, callback));
    }

    /**
     * 获取拉布、裁剪数据
     *
     * @param orderType 订单类型 P=批量、S=定制、W=缝制返修配片
     */
    public static void viewCutPadInfo(String orderType, String orderNum, String resourceBO, String RI, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("RFID", orderNum);
        json.put("ORDER_TYPE", orderType);
        json.put("RI", RI);
        json.put("RESOURCE_BO", resourceBO);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(viewCutPadInfo_url, params, getResponseHandler(viewCutPadInfo_url, callback));
    }

    /**
     * 退料/补料接口
     *
     * @param json TYPE:2=补料,3=退料
     *             SHOP_ORDER = 订单号
     *             ITEM_INFOS = 退/补料对象列表，对象：ITEM = 物料号，QTY = 数量，REASON_CODE = 原因代码
     */
    public static void saveMatReturnOrFeeding(JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(cutMaterialReturnOrFeeding, params, getResponseHandler(cutMaterialReturnOrFeeding, callback));
    }

    /**
     * 批量订单开始
     *
     * @param paramsBo 参数
     */
    public static void startBatchWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(startBatchWork_url, params, 60 * 1000, getResponseHandler(startBatchWork_url, callback));
    }

    /**
     * 定制订单开始
     *
     * @param paramsBo 参数
     */
    public static void startCustomWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(startCustomWork_url, params, 60 * 1000, getResponseHandler(startCustomWork_url, callback));
    }

    /**
     * 定制订单完成
     *
     * @param paramsBo 参数
     */
    public static void completeCustomWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(completeCustomWork_url, params, 60 * 1000, getResponseHandler(completeCustomWork_url, callback));
    }

    /**
     * 批量订单完成
     *
     * @param paramsBo 参数
     */
    public static void completeBatchWork(StartWorkParamsBo paramsBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(paramsBo));
        HttpRequest.post(completeBatchWork_url, params, 60 * 1000, getResponseHandler(completeBatchWork_url, callback));
    }

    /**
     * 获取作业订单列表
     */
    public static void getWorkOrderList(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(getWorkOrderList_url, params, getResponseHandler(getWorkOrderList_url, callback));
    }

    /**
     * 获取拉布数据
     */
    public static void getLabuData(GetLabuDataBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(getLabuData, params, getResponseHandler(getLabuData, callback));
    }

    /**
     * 保存拉布数据
     */
    public static void saveLabuData(SaveLabuDataBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveOrUpdateLabuData, params, getResponseHandler(saveOrUpdateLabuData, callback));
    }

    /**
     * 获取不良数据列表
     */
    public static void getBadList(HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(getBadList, params, getResponseHandler(getBadList, callback));
    }

    /**
     * 保存不良数据
     */
    public static void saveBadRecord(@NonNull JSONObject json, HttpCallback callback) {
        json.put("PAD_ID", PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(saveBadRecord, params, getResponseHandler(saveBadRecord, callback));
    }

    /**
     * 注销在制品-定制订单
     *
     * @param json json包含参数：SHOP_ORDER_BO、RESOURCE_BO、OPERATION_BO
     */
    public static void signoffByShopOrder(@NonNull JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(signoffByShopOrder, params, getResponseHandler(signoffByShopOrder, callback));
    }

    /**
     * 注销在制品-批量订单
     *
     * @param json json包含参数：{'PROCESS_LOTS':[""],'OPERATION_BO':'','RESOURCE_BO':'','SHOP_ORDER_BO':''}
     */
    public static void signoffByProcessLot(@NonNull JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(signoffByProcessLot, params, getResponseHandler(signoffByProcessLot, callback));
    }

    /**
     * 衣架绑定
     */
    public static void hangerBinding(String partId, String washLabel, String subcontract, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PART_ID", partId);
        json.put("PAD_IP", PAD_IP);
        json.put("WATER_MARK_NUMBER", washLabel);
        json.put("SUBCONTRACT", subcontract);
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        HttpRequest.post(hangerBinding, params, getResponseHandler(hangerBinding, callback));
    }

    /**
     * 衣架解绑
     *
     * @param json {"HANGER_ID":"E34A3499","SFC":"19357930010001","PART_ID":"ZH"}
     *             HANGER_ID:衣架ID；
     *             SFC：工票号；
     *             PART_ID：生产部件编码
     *             参数输入(HANGER_ID) 或 (SFC与PART_ID) 必须输入其一
     */
    public static void hangerUnbind(JSONObject json, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", json.toJSONString());
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers != null && positionUsers.size() != 0) {
            params.put("userid", positionUsers.get(0).getUSER());
        }
        HttpRequest.post(hangerUnbind, params, getResponseHandler(hangerUnbind, callback));
    }

    /**
     * 获取缝制数据
     *
     * @param rfid 卡号
     */
    public static void getSewData(String rfid, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("rfId", rfid);
        params.put("padIp", PAD_IP);
        HttpRequest.post(getSewData, params, getResponseHandler(getSewData, callback));
    }

    /**
     * 获取上裁工序基础数据
     *
     * @param positionBo 站位bo
     */
    public static void getSuspendBaseData(String positionBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("POSITION_BO", positionBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(getSuspendBaseData, params, getResponseHandler(getSuspendBaseData, callback));
    }

    /**
     * 获取上裁工序待作业清单
     *
     * @param operationBo 工序bo
     * @param workCenter  工作中心
     */
    public static void getSuspendUndoList(String operationBo, String workCenter, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("OPERATION_BO", operationBo);
        json.put("WORK_CENTER", workCenter);
        params.put("params", json.toJSONString());
        HttpRequest.post(getSuspendUndoList, params, getResponseHandler(getSuspendUndoList, callback));
    }

    /**
     * 获取缝制质检主界面数据
     */
    public static boolean findPadKeyDataForNcUI(String RFID, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("rfId", RFID);
        params.put("padIp", PAD_IP);
//        params.put("userId", "REX");
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        if (positionUsers != null && positionUsers.size() != 0) {
            params.put("userId", positionUsers.get(0).getUSER());
        } else {
            return false;
        }
        HttpRequest.post(findPadKeyDataForNcUI, params, getResponseHandler(findPadKeyDataForNcUI, callback));
        return true;
    }

    /**
     * 通过工票号查询出其对应的部件
     *
     * @param operationBo 工序bo
     * @param positionBo  工作中心
     * @param RFID        RFID卡号
     */
    public static void getSfcComponents(String operationBo, String positionBo, String RFID, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("OPERATION_BO", operationBo);
        json.put("POSITION_BO", positionBo);
        json.put("RFID", RFID);
        json.put("PAD_IP", PAD_IP);
        params.put("params", json.toJSONString());
        HttpRequest.post(getSfcComponents, params, getResponseHandler(getSfcComponents, callback));
    }

    /**
     * 获取部件对应的图片
     *
     * @param sfc       工票号
     * @param component 部件
     */
    public static void getComponentPic(String shopOrder, String sfc, String component, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SHOP_ORDER", shopOrder);
        json.put("SFC", sfc);
        json.put("COMPONENT", component);
        params.put("params", json.toJSONString());
        HttpRequest.post(getComponentInfo, params, getResponseHandler(getComponentInfo, callback));
    }

    /**
     * 根据SFC已完成的工序带出其生产部件集合
     */
    public static void getProductComponentList(String sfcBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SFC_BO", sfcBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(getProductComponentList, params, getResponseHandler(getProductComponentList, callback));
    }

    /**
     * 根据生产部件查询设计部件
     */
    public static void getDesignComponentList(String productComponent, String sfcBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("PROD_COMPONENT", productComponent);
        json.put("SFC_BO", sfcBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(getDesignComponentList, params, getResponseHandler(getDesignComponentList, callback));
    }

    /**
     * 根据设计部件获取不良代码列表
     */
    public static void getSewNcCodeList(String designComponent, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("DESG_COMPONENT", designComponent);
        params.put("params", json.toJSONString());
        HttpRequest.post(getSewNcCodeList, params, getResponseHandler(getSewNcCodeList, callback));
    }

    /**
     * 根据不良代码获取工序列表
     */
    public static void getProcessWithNcCode(String productComponent, String designComponent, String sfcBo, String ncCodeBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("PROD_COMPONENT", productComponent);
        json.put("DESG_COMPONENT", designComponent);
        json.put("SFC_BO", sfcBo);
        json.put("NC_CODE_BO", ncCodeBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(getProcessWithNcCode, params, getResponseHandler(getProcessWithNcCode, callback));
    }

    /**
     * 查询返工工序列表
     */
    public static void getRepairProcess(String produceComponent, String sfcBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_ID", PAD_IP);
        json.put("PROD_COMPONENT", produceComponent);
        json.put("SFC_BO", sfcBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(getRepairProcess, params, getResponseHandler(getRepairProcess, callback));
    }

    /**
     * 保存缝制质检不良数据
     */
    public static void recordSewNc(UpdateSewNcBo data, HttpCallback callback) {
        data.setPadIp(PAD_IP);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(recordSewNc, params, getResponseHandler(recordSewNc, callback));
    }

    /**
     * 制卡
     */
    public static void createCard(String sfc, String rfId, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("padIp", PAD_IP);
        params.put("sfc", sfc);
        params.put("rfId", rfId);
        HttpRequest.post(createCard, params, getResponseHandler(createCard, callback));
    }

    /**
     * 获取卡片信息
     *
     * @param sfc      任选一个参数
     * @param hangerId 任选一个参数
     */
    public static void findCardInfoBySfcOrHangerId(String sfc, String hangerId, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("hangerId", hangerId);
        params.put("sfc", sfc);
        HttpRequest.post(findCardInfoBySfcOrHangerId, params, getResponseHandler(findCardInfoBySfcOrHangerId, callback));
    }

    /**
     * 选择粘朴方式
     */
    public static void tellFusingStyleToGST(String processLot, String stickyCode, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PROCESS_LOT_BO", processLot);
        json.put("FUSE_STYLE", stickyCode);
        params.put("params", json.toJSONString());
        HttpRequest.post(tellFusingStyleToGST, params, getResponseHandler(tellFusingStyleToGST, callback));
    }

    /**
     * 获取成衣数据信息
     */
    public static void getClothSize(String SFC, String operationBo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SFC", SFC);
        json.put("OPERATION_BO", operationBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(getClothSize, params, getResponseHandler(getClothSize, callback));
    }

    /**
     * 保存成衣数据
     */
    public static void saveClothSizeData(SaveClothSizeBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveQCClothSizeData, params, getResponseHandler(saveQCClothSizeData, callback));
    }

    public static void qaToQc(String sfc, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SFC", sfc);
        json.put("PAD_IP", PAD_IP);
        params.put("params", json.toJSONString());
        HttpRequest.post(qaToQc, params, getResponseHandler(qaToQc, callback));
    }

    /**
     * 缝制站去质检站
     *
     * @param type 去质检=NC2QC，去QA=NC2QA
     */
    public static void initNcForQA(String sfc, String resourceBo, String type, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SFC", sfc);
        json.put("ncCode", type);
        json.put("ResourceBO", resourceBo);
        params.put("params", json.toJSONString());
        HttpRequest.post(initNcForQA, params, getResponseHandler(initNcForQA, callback));
    }

    /**
     * 搜索工单对应的bom
     */
    public static void getBomInfo(String sfc, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("ordernumber", sfc);
        HttpRequest.post(getBomInfo, params, getResponseHandler(getBomInfo, callback));
    }

    /**
     * 获取字典数据
     * code:
     * ZpType = 粘朴数据
     * BlReason = 补料数据
     * TlReason = 退料数据
     */
    public static void getDictionaryData(String code, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("code", code);
        params.put("params", json.toJSONString());
        HttpRequest.post(getDictionaryData, params, getResponseHandler(getDictionaryData, callback));
    }

    /**
     * 自动拣选
     *
     * @param shopOrder    订单号
     * @param itemCode     款号
     * @param locationType 库位类型 20=裁剪段，30=上裁段
     */
    public static void autoPicking(String shopOrder, String itemCode, String locationType, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("referDocCode", shopOrder);
        json.put("itemCode", itemCode);
        json.put("LocationTypeCode", locationType);
        params.put("params", json.toJSONString());
        HttpRequest.post(automaticPicking, params, getResponseHandler(automaticPicking, callback));
    }

    /**
     * 获取线稿图数据
     *
     * @param sfc sfc
     */
    public static void getOutlineInfo(String sfc, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("SFC", sfc);
        params.put("params", json.toJSONString());
        HttpRequest.post(getOutlineInfo, params, getResponseHandler(getOutlineInfo, callback));
    }

    /**
     * 获取RFID卡数据
     */
    public static void findRfidInfo(String rfid, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("rfId", rfid);
        HttpRequest.post(findRfidInfo, params, getResponseHandler(findRfidInfo, callback));
    }

    /**
     * 保存外协数据
     */
    public static void saveSubcontractInfo(String rfids, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("rfIds", rfids);
        HttpRequest.post(saveSubcontractInfo, params, getResponseHandler(saveSubcontractInfo, callback));
    }

    /**
     * 内协开始
     */
    public static void sewSubStart(String rfid, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("rfId", rfid);
        params.put("padIp", PAD_IP);
        HttpRequest.post(sewSubStart, params, getResponseHandler(sewSubStart, callback));
    }

    /**
     * 获取裁剪段记录数据
     */
    public static void getCutRecordData(String rfid, String shopOrder, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("RFID", rfid);
        json.put("SHOP_ORDER", shopOrder);
        json.put("padIp", PAD_IP);
        params.put("params", json.toJSONString());
        HttpRequest.post(getCutRecordData, params, getResponseHandler(getCutRecordData, callback));
    }

    /**
     * 保存裁剪段记录数据
     */
    public static void saveCutRecordData(CutRecordQtyBo data, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("padIp", PAD_IP);
        params.put("params", JSON.toJSONString(data));
        HttpRequest.post(saveCutRecordData, params, getResponseHandler(saveCutRecordData, callback));
    }

    /**
     * 通过RFID卡号获取用户ID和名字
     */
    public static void getUserInfo(String cardNum, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("RFID", cardNum);
        json.put("padIp", PAD_IP);
        params.put("params", json.toJSONString());
        HttpRequest.post(getUserInfo, params, getResponseHandler(getUserInfo, callback));
    }

    /**
     * 获取线色数据
     */
    public static void getStitchInventory(String orderNo, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("orderNo", orderNo);
        HttpRequest.post(getStitchInventory, params, getResponseHandler(getStitchInventory, callback));
    }

    /**
     * 获取绣花信息
     */
    public static void getEmbroiderInfor(String SFC, String processLotBo, String theme, HttpCallback callback) {
        RequestParams params = getBaseParams();
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("SFC", SFC);
        json.put("PROCESS_LOT_BO", processLotBo);
        json.put("THEME", theme);
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(getEmbroiderInfor, params, getResponseHandler(getEmbroiderInfor, callback));
    }

    /**
     * 获取返修信息
     */
    public static void getReworkInfo(String sfcRef, HttpCallback callback) {
        RequestParams params = getBaseParams();
        params.put("sfcRef", sfcRef);
        HttpRequest.post(getReworkInfo, params, getResponseHandler(getReworkInfo, callback));
    }

    /**
     * 获取纸样图案
     */
    public static void getPattern(String shopOrder, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("SHOP_ORDER", shopOrder);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(getPattern, params, getResponseHandler(getPattern, callback));
    }

    /**
     * 通过工单号获取工单信息
     *
     * @param type SHOP_ORDER,SFC
     */
    public static void viewCutPadInfoByShopOrder(String type, String value, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("PAD_IP", PAD_IP);
        json.put("TYPE", type);
        json.put("INPUT", value);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(viewCutPadInforByShopOrder, params, getResponseHandler(viewCutPadInforByShopOrder, callback));
    }

    /**
     * 获取返修工序详情
     */
    public static void listOffLineReWorkInfo(String sfc, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("SFC", sfc);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(listOffLineReWorkInfo, params, getResponseHandler(listOffLineReWorkInfo, callback));
    }

    /**
     * 成衣上架
     */
    public static void productOn(String hangerId, String washLabel, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("HANGER_ID", hangerId);
        json.put("WATER_MARK_NUMBER", washLabel);
        json.put("PAD_IP", getPadIp());
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        json.put("USER_ID", positionUsers.get(0).getUSER());
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(productOn, params, getResponseHandler(productOn, callback));
    }

    /**
     * 成衣下架
     */
    public static void productOff(String hangerId, String SFC, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("HANGER_ID", hangerId);
        json.put("SFC", SFC);
        json.put("PAD_IP", getPadIp());
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        json.put("USER_ID", positionUsers.get(0).getUSER());
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(productOff, params, getResponseHandler(productOff, callback));
    }

    public static void getCutMatInfoPic(String logicNo, String salesOrder, HttpCallback callback) {
        getCommonInfoByLogicNo(logicNo, null, null, null, null, salesOrder, callback);
    }

    /**
     * 通过logicNo获取通用信息
     *
     * @param LOGIC_NO 袋口尺寸=query.cadSizeInfo
     *                 腰头尺寸=query.cadSizeYTInfo
     *                 条格面料裁剪确认单=query.cutConfirm
     */
    public static void getCommonInfoByLogicNo(String LOGIC_NO, String shopOrder, String sfc, String sizeCode, String operation, String salesOrder, HttpCallback callback) {
        JSONObject json = new JSONObject();
        json.put("SITE", SpUtil.getSite());
        json.put("LOGIC_NO", LOGIC_NO);
        JSONObject json1 = new JSONObject();
        json1.put("SHOP_ORDER", shopOrder);
        json1.put("SALES_ORDER", salesOrder);
        json1.put("OPERATIONS", operation);
        json1.put("SFC", sfc);
        json1.put("SIZE_CODE", sizeCode);
        json.put("PARAMS", json1);
        RequestParams params = getBaseParams();
        params.put("params", JSON.toJSONString(json));
        HttpRequest.post(getCommonInfoByLogicNo, params, getResponseHandler(getCommonInfoByLogicNo, callback));
    }

    /**
     * 获取固定请求参数<br>
     */
    private static RequestParams getBaseParams() {
        PAD_IP = getPadIp();
        RequestParams params = new RequestParams();
        String site = SpUtil.getSite();
        if (!TextUtils.isEmpty(site)) {
            params.put("site", site);
        }
        String cookie = SpUtil.getCookie();
        if (!TextUtils.isEmpty(cookie)) {
            params.addHeader("Cookie", cookie);
        }
        return params;
    }

    public static String getPadIp() {
        PAD_IP = NetUtil.getHostIP();
        return PAD_IP;
    }

    public static boolean isSuccess(JSONObject json) {
        return "Y".equals(json.getString(STATE));
    }

    public static String getMessage(JSONObject json) {
        return json.getString(MESSAGE);
    }

    public static String getResultStr(JSONObject json) {
        JSONObject result = json.getJSONObject("result");
        if (result != null && !result.isEmpty())
            return result.toString();
        return null;
    }

    /**
     * 获取请求响应Handler
     */
    public static BaseHttpRequestCallback getResponseHandler(final String url, final HttpCallback callback) {
        final BaseHttpRequestCallback response = new BaseHttpRequestCallback<JSONObject>() {

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                //无网络或者后台出错
                if (callback != null)
                    callback.onFailure(url, errorCode, msg);
            }

            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                boolean success = isSuccess(jsonObject);
                if (!success) {
                    String message = jsonObject.getString("message");
                    LogUtil.writeToFile(LogUtil.LOGTYPE_HTTPFAIL, url + "\n" + message);
                }
                //登录的时候保存cookie
                if (login_url.contains(url)) {
                    if (headers != null) {
                        StringBuilder cookies = new StringBuilder();
                        List<String> values = headers.values("set-cookie");
                        for (String cookie : values) {
                            cookies.append(cookie).append(";");
                        }
                        if (!TextUtils.isEmpty(cookies)) {
                            SpUtil.saveCookie(cookies.substring(0, cookies.lastIndexOf(";")));
                        }
                    }
                    if (IS_COOKIE_OUT) {
                        if (success) {
                            IS_COOKIE_OUT = false;
                            //cookie过期后重新登录成功，继续执行之前的请求
                            if (mCookieOutRequest != null) {
                                RequestParams params = getBaseParams();
                                RequestParams lastParams = mCookieOutRequest.getParams();
                                List<Part> formParams = lastParams.getFormParams();
                                for (Part p : formParams) {
                                    params.put(p.getKey(), p.getValue());
                                }
                                HttpRequest.post(mCookieOutRequest.getUrl(), params, mCookieOutRequest.getCallback());
                            }
                            return;
                        }
                    }
                } else if (!success) {
                    String message = jsonObject.getString("message");
                    if (!TextUtils.isEmpty(message)) {
                        if (message.contains(COOKIE_OUT)) {//cookie失效，重新登录获取新的cookie
                            IS_COOKIE_OUT = true;

                            //记录cookie失效前的请求，重新登录后重新请求
                            HttpRequest.HttpRequestBo lastRequest = HttpRequest.mLastRequest;
                            mCookieOutRequest = new HttpRequest.HttpRequestBo();
                            mCookieOutRequest.setMethod(lastRequest.getMethod());
                            mCookieOutRequest.setUrl(lastRequest.getUrl());
                            mCookieOutRequest.setParams(lastRequest.getParams());
                            mCookieOutRequest.setCallback(lastRequest.getCallback());

                            UserInfoBo loginUser = SpUtil.getLoginUser();
                            login(loginUser.getUSER(), loginUser.getPassword(), callback);
                            return;
                        }
                    }
                }
                if (callback != null)
                    callback.onSuccess(url, jsonObject);
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);
                if (!NetUtil.isNetworkAvalible(mContext)) {
                    Toast.makeText(mContext, "网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                    return;
                } else if (response == null) {
                    Toast.makeText(mContext, "连接错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestBody body = httpResponse.request().body();
                Buffer buffer = new Buffer();
                try {
                    body.writeTo(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buffer.flush();
                buffer.close();
                String s = buffer.readString(Charset.forName("UTF-8"));
                String decode = URLDecoder.decode(s);
                LogUtil.writeToFile(LogUtil.LOGTYPE_HTTPRESPONSE, url + decode + "\n       " + response);
                Logger.d(response);
            }
        };
        return response;
    }

}
