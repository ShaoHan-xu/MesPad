package com.eeka.mespad.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lenovo on 2017/8/23.
 */

public class SewQCDataBo {

    /**
     * shopOrder : GR_SO_MTM_02
     * item : MODA-MAT-02
     * sfcSize : S
     * bomComponent : [{"attributes":{"PART_ID":"A,B"},"description":"裁剪pad查询测试物料清单组件01","name":"GR_BOM_COMP_01"},{"attributes":{"PART_ID":"C,D"},"description":"裁剪pad查询测试物料清单组件02","name":"GR_BOM_COMP_02"},{"attributes":{"PART_ID":"E,F"},"description":"裁剪pad查询测试物料清单组件03","name":"GR_BOM_COMP_03"}]
     * clothingSize : [{"attributes":{"value":"123"},"description":"","name":"P001"},{"attributes":{"value":"456"},"description":"","name":"P002"},{"attributes":{"value":"789"},"description":"","name":"P003"}]
     * sfc : TEST711
     * currentOperation : GR_QF_06
     * soMark : 九分裤，脚口半径不要太大，有收腰功能
     * dailyOutput : 0
     * designComponent : [{"description":"前幅","desgComponents":[{"description":"Q001描述","name":"Q001","qualityStandard":""},{"description":"Q002描述","name":"Q002","qualityStandard":""},{"description":"Q003描述","name":"Q003","qualityStandard":""},{"description":"","name":"","qualityStandard":""},{"description":"stupid","name":"FF","qualityStandard":""}],"name":"QF"}]
     * monthlyOutput : 0
     */

    private String shopOrder;
    private String salesOrder;
    private String item;
    private String itemDesc;
    private String sfcSize;
    private String sfc;
    private List<SewAttr> currentOperation;
    private String soMark;
    private String sfcRef;
    private String reworkFlag;
    private int dailyOutput;
    private int monthlyOutput;
    private List<BomComponentBean> bomComponent;
    //    private List<ClothingSizeBean> clothingSize;
    private List<DesignComponentBean> designComponent;
    private List<String> ncCode;

    public String getSfcRef() {
        return sfcRef;
    }

    public void setSfcRef(String sfcRef) {
        this.sfcRef = sfcRef;
    }

    public String getReworkFlag() {
        return reworkFlag;
    }

    public void setReworkFlag(String reworkFlag) {
        this.reworkFlag = reworkFlag;
    }

    public List<String> getNcCode() {
        return ncCode;
    }

    public void setNcCode(List<String> ncCode) {
        this.ncCode = ncCode;
    }

    public String getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(String salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getSfcSize() {
        return sfcSize;
    }

    public void setSfcSize(String sfcSize) {
        this.sfcSize = sfcSize;
    }

    public String getSfc() {
        return sfc;
    }

    public void setSfc(String sfc) {
        this.sfc = sfc;
    }

    public List<SewAttr> getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(List<SewAttr> currentOperation) {
        this.currentOperation = currentOperation;
    }

    public String getSoMark() {
        return soMark;
    }

    public void setSoMark(String soMark) {
        this.soMark = soMark;
    }

    public int getDailyOutput() {
        return dailyOutput;
    }

    public void setDailyOutput(int dailyOutput) {
        this.dailyOutput = dailyOutput;
    }

    public int getMonthlyOutput() {
        return monthlyOutput;
    }

    public void setMonthlyOutput(int monthlyOutput) {
        this.monthlyOutput = monthlyOutput;
    }

    public List<BomComponentBean> getBomComponent() {
        return bomComponent;
    }

    public void setBomComponent(List<BomComponentBean> bomComponent) {
        this.bomComponent = bomComponent;
    }

//    public List<ClothingSizeBean> getClothingSize() {
//        return clothingSize;
//    }
//
//    public void setClothingSize(List<ClothingSizeBean> clothingSize) {
//        this.clothingSize = clothingSize;
//    }

    public List<DesignComponentBean> getDesignComponent() {
        return designComponent;
    }

    public void setDesignComponent(List<DesignComponentBean> designComponent) {
        this.designComponent = designComponent;
    }

    public static class BomComponentBean {
        /**
         * attributes : {"PART_ID":"A,B"}
         * description : 裁剪pad查询测试物料清单组件01
         * name : GR_BOM_COMP_01
         */

        private AttributesBean attributes;
        private String description;
        private String name;

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class AttributesBean implements Serializable {
            /**
             * PART_ID : A,B
             */

            private String PART_ID;

            public String getPART_ID() {
                return PART_ID;
            }

            public void setPART_ID(String PART_ID) {
                this.PART_ID = PART_ID;
            }
        }
    }

    public static class ClothingSizeBean implements Serializable {
        /**
         * attributes : {"value":"123"}
         * description :
         * name : P001
         */

        private AttributesBeanX attributes;
        private String description;
        private String name;


        public AttributesBeanX getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBeanX attributes) {
            this.attributes = attributes;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class AttributesBeanX implements Serializable {
            /**
             * value : 123
             */

            private float value;
            private float refTolerance;
            private float realTolerance;
            private float finishedSize;

            public float getValue() {
                return value;
            }

            public void setValue(float value) {
                this.value = value;
            }

            public float getRefTolerance() {
                return refTolerance;
            }

            public void setRefTolerance(float refTolerance) {
                this.refTolerance = refTolerance;
            }

            public float getRealTolerance() {
                return realTolerance;
            }

            public void setRealTolerance(float realTolerance) {
                this.realTolerance = realTolerance;
            }

            public float getFinishedSize() {
                return finishedSize;
            }

            public void setFinishedSize(float finishedSize) {
                this.finishedSize = finishedSize;
            }
        }
    }

    public static class DesignComponentBean implements Serializable {
        /**
         * description : 前幅
         * desgComponents : [{"description":"Q001描述","name":"Q001","qualityStandard":""},{"description":"Q002描述","name":"Q002","qualityStandard":""},{"description":"Q003描述","name":"Q003","qualityStandard":""},{"description":"","name":"","qualityStandard":""},{"description":"stupid","name":"FF","qualityStandard":""}]
         * name : QF
         */

        private String description;
        private String name;
        private List<DesgComponentsBean> desgComponents;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<DesgComponentsBean> getDesgComponents() {
            return desgComponents;
        }

        public void setDesgComponents(List<DesgComponentsBean> desgComponents) {
            this.desgComponents = desgComponents;
        }

        public static class DesgComponentsBean implements Serializable {
            /**
             * description : Q001描述
             * name : Q001
             * qualityStandard :
             */

            private String description;
            private String name;
            private String qualityStandard;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getQualityStandard() {
                return qualityStandard;
            }

            public void setQualityStandard(String qualityStandard) {
                this.qualityStandard = qualityStandard;
            }
        }
    }
}
