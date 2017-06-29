package com.eeka.mespad.bo;

import java.util.List;

/**
 * Created by Lenovo on 2017/6/28.
 */

public class TailorInfoBo {

    /**
     * status : Y
     * result : {"shopOrder":"工单","planCut":"床次","drawPicUrl":"排料图url地址","material":"款式","qty":"数量/件","itemArray":[{"material":"M001","materialUrl":"http://10.8.41.187/50011后袋.jpg"},{"name":"M002","materialUrl":"http://10.8.41.187/50011后片.jpg"},{"name":"M003","materialUrl":"http://10.8.41.187/50011拉链.jpg"}],"sizeArray":[{"size":38,"qty":30},{"size":39,"qty":20}]}
     */

    private String status;
    private TailorResultBean result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TailorResultBean getResult() {
        return result;
    }

    public void setResult(TailorResultBean result) {
        this.result = result;
    }

    public static class TailorResultBean {
        /**
         * shopOrder : 工单
         * planCut : 床次
         * drawPicUrl : 排料图url地址
         * material : 款式
         * qty : 数量/件
         * itemArray : [{"material":"M001","materialUrl":"http://10.8.41.187/50011后袋.jpg"},{"name":"M002","materialUrl":"http://10.8.41.187/50011后片.jpg"},{"name":"M003","materialUrl":"http://10.8.41.187/50011拉链.jpg"}]
         * sizeArray : [{"size":38,"qty":30},{"size":39,"qty":20}]
         */

        private String shopOrder;
        private String planCut;
        private String drawPicUrl;
        private String material;
        private String qty;
        private List<ItemArrayBean> itemArray;
        private List<SizeArrayBean> sizeArray;

        public String getShopOrder() {
            return shopOrder;
        }

        public void setShopOrder(String shopOrder) {
            this.shopOrder = shopOrder;
        }

        public String getPlanCut() {
            return planCut;
        }

        public void setPlanCut(String planCut) {
            this.planCut = planCut;
        }

        public String getDrawPicUrl() {
            return drawPicUrl;
        }

        public void setDrawPicUrl(String drawPicUrl) {
            this.drawPicUrl = drawPicUrl;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public List<ItemArrayBean> getItemArray() {
            return itemArray;
        }

        public void setItemArray(List<ItemArrayBean> itemArray) {
            this.itemArray = itemArray;
        }

        public List<SizeArrayBean> getSizeArray() {
            return sizeArray;
        }

        public void setSizeArray(List<SizeArrayBean> sizeArray) {
            this.sizeArray = sizeArray;
        }

        public static class ItemArrayBean {
            /**
             * material : M001
             * materialUrl : http://10.8.41.187/50011后袋.jpg
             */

            private String material;
            private String materialUrl;

            public String getMaterial() {
                return material;
            }

            public void setMaterial(String material) {
                this.material = material;
            }

            public String getMaterialUrl() {
                return materialUrl;
            }

            public void setMaterialUrl(String materialUrl) {
                this.materialUrl = materialUrl;
            }
        }

        public static class SizeArrayBean {
            /**
             * size : 38
             * qty : 30
             */

            private int size;
            private int qty;
            private String color;

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getQty() {
                return qty;
            }

            public void setQty(int qty) {
                this.qty = qty;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }
    }
}
