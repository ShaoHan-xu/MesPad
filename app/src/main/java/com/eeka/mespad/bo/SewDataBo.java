package com.eeka.mespad.bo;

import java.io.Serializable;
import java.util.List;

/**
 * 缝制数据
 * Created by Lenovo on 2017/8/9.
 */

public class SewDataBo implements Serializable {

    /**
     * currentOpeationInfos : [{"attributes":{"MAT_URL":"http://10.8.41.187/ML01.jpg"},"description":"裁剪pad查询测试物料清单组件01","name":"GR_BOM_COMP_01"},{"attributes":{"MAT_URL":"http://10.8.41.187/ML01.jpg"},"description":"裁剪pad查询测试物料清单组件02","name":"GR_BOM_COMP_02"},{"attributes":{"MAT_URL":"http://10.8.41.187/ML01.jpg"},"description":"裁剪pad查询测试物料清单组件03","name":"GR_BOM_COMP_03"},{"attributes":{"OPERATION_INSTRUCTION":"1、在PAD里阅读系统传送的生产工艺、排料方案以及拉布床次、拉布层数等信息资料。 \\n 2、取排料图（定制订单无排料图）。\\n 3、面料正反面确认。 \\n 4、根据拉布作业标准进行拉布作业。\\n 5、将拉布数据录入系统。","QUALITY_REQUIREMENT":"1、详细了解、阅读相关资料，准确掌握生产信息。\\n 2、拿取正确的排料图。\\n 3、确保面料正反面运用正确。\\n 4、做到\u201c3齐一准一平\u201d(上、下口齐、主边齐；接头准确；布面自然平服）。\\n 5、数据录入及时、准确。"},"description":"拉布工序","name":"GC-OP-LABU"}]
     * item : GR_MAT_01
     * lastLineCategory : 1000
     * lastOperation : GC-OP-CAIJIAN
     * lastPosition : 1
     * salesOrder : 123456789
     * sfc : TEST672
     * shopOrder : GR_SO_MTM_01
     * soRemark : 面料尽裁，用红色的线
     * workEfficiency : 0.0684
     */

    private String item;
    private String itemDesc;
    private String lastLineCategory;
    private String lastPosition;
    private String salesOrder;
    private String sfc;
    private String size;
    private String shopOrder;
    private String soRemark;
    private String workEfficiency;
    private List<SewAttr> currentOpeationInfos;
    private List<SewAttr> lastOperations;
    private List<SewAttr> nextOperation;
    private List<SewAttr> colorItems;// 用料图

    public List<SewAttr> getNextOperation() {
        return nextOperation;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public void setNextOperation(List<SewAttr> nextOperation) {
        this.nextOperation = nextOperation;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLastLineCategory() {
        return lastLineCategory;
    }

    public void setLastLineCategory(String lastLineCategory) {
        this.lastLineCategory = lastLineCategory;
    }

    public List<SewAttr> getLastOperations() {
        return lastOperations;
    }

    public void setLastOperations(List<SewAttr> lastOperations) {
        this.lastOperations = lastOperations;
    }

    public String getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(String lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(String salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getSfc() {
        return sfc;
    }

    public void setSfc(String sfc) {
        this.sfc = sfc;
    }

    public String getShopOrder() {
        return shopOrder;
    }

    public void setShopOrder(String shopOrder) {
        this.shopOrder = shopOrder;
    }

    public String getSoRemark() {
        return soRemark;
    }

    public void setSoRemark(String soRemark) {
        this.soRemark = soRemark;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWorkEfficiency() {
        return workEfficiency;
    }

    public void setWorkEfficiency(String workEfficiency) {
        this.workEfficiency = workEfficiency;
    }

    public List<SewAttr> getCurrentOpeationInfos() {
        return currentOpeationInfos;
    }

    public void setCurrentOpeationInfos(List<SewAttr> currentOpeationInfos) {
        this.currentOpeationInfos = currentOpeationInfos;
    }

    public List<SewAttr> getColorItems() {
        return colorItems;
    }

    public void setColorItems(List<SewAttr> colorItems) {
        this.colorItems = colorItems;
    }

    public static class SewAttr {
        /**
         * attributes : {"MAT_URL":"http://10.8.41.187/ML01.jpg"}
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

        public static class AttributesBean {
            /**
             * MAT_URL : http://10.8.41.187/ML01.jpg
             */
            private String MAT_URL;
            private String SOP_URL;
            private String VIDEO_URL;
            private String QUALITY_REQUIREMENT;//质量要求
            private String OPERATION_INSTRUCTION;//工艺说明

            public String getVIDEO_URL() {
                return VIDEO_URL;
            }

            public void setVIDEO_URL(String VIDEO_URL) {
                this.VIDEO_URL = VIDEO_URL;
            }

            public String getMAT_URL() {
                return MAT_URL;
            }

            public void setMAT_URL(String MAT_URL) {
                this.MAT_URL = MAT_URL;
            }

            public String getSOP_URL() {
                return SOP_URL;
            }

            public void setSOP_URL(String SOP_URL) {
                this.SOP_URL = SOP_URL;
            }

            public String getQUALITY_REQUIREMENT() {
                return QUALITY_REQUIREMENT;
            }

            public void setQUALITY_REQUIREMENT(String QUALITY_REQUIREMENT) {
                this.QUALITY_REQUIREMENT = QUALITY_REQUIREMENT;
            }

            public String getOPERATION_INSTRUCTION() {
                return OPERATION_INSTRUCTION;
            }

            public void setOPERATION_INSTRUCTION(String OPERATION_INSTRUCTION) {
                this.OPERATION_INSTRUCTION = OPERATION_INSTRUCTION;
            }
        }
    }
}
