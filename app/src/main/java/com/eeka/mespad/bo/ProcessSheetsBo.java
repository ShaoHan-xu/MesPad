package com.eeka.mespad.bo;

import java.util.List;

public class ProcessSheetsBo {

    /**
     * technologyList : [{"name":"","technologyNumber":"1","technologyRemark":""}]
     * specs : [{"measureMethod":"","patternLooseness":"","sizes":[{"sizeCode":"","sizeValue":""}],"specId":"","specName":"","specNumber":"0","tolerance":""}]
     * patternNumber : CA00012
     * year : 2006
     * craftsman :
     * styleImageURL :
     * season : 春夏季
     * styleName : 西装外套
     * band : I 寒冬
     * designer :
     * styleCode : CA00012
     * planner :
     */

    private String patternNumber;
    private String year;
    private String craftsman;
    private String styleImageURL;
    private String season;
    private String styleName;
    private String band;
    private String designer;
    private String styleCode;
    private String planner;
    private List<TechnologyListBean> technologyList;
    private List<SpecsBean> specs;

    public String getPatternNumber() {
        return patternNumber;
    }

    public void setPatternNumber(String patternNumber) {
        this.patternNumber = patternNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCraftsman() {
        return craftsman;
    }

    public void setCraftsman(String craftsman) {
        this.craftsman = craftsman;
    }

    public String getStyleImageURL() {
        return styleImageURL;
    }

    public void setStyleImageURL(String styleImageURL) {
        this.styleImageURL = styleImageURL;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public String getPlanner() {
        return planner;
    }

    public void setPlanner(String planner) {
        this.planner = planner;
    }

    public List<TechnologyListBean> getTechnologyList() {
        return technologyList;
    }

    public void setTechnologyList(List<TechnologyListBean> technologyList) {
        this.technologyList = technologyList;
    }

    public List<SpecsBean> getSpecs() {
        return specs;
    }

    public void setSpecs(List<SpecsBean> specs) {
        this.specs = specs;
    }

    public static class TechnologyListBean {
        /**
         * name :
         * technologyNumber : 1
         * technologyRemark :
         */

        private String name;
        private String technologyNumber;
        private String technologyRemark;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTechnologyNumber() {
            return technologyNumber;
        }

        public void setTechnologyNumber(String technologyNumber) {
            this.technologyNumber = technologyNumber;
        }

        public String getTechnologyRemark() {
            return technologyRemark;
        }

        public void setTechnologyRemark(String technologyRemark) {
            this.technologyRemark = technologyRemark;
        }
    }

    public static class SpecsBean {
        /**
         * measureMethod :
         * patternLooseness :
         * sizes : [{"sizeCode":"","sizeValue":""}]
         * specId :
         * specName :
         * specNumber : 0
         * tolerance :
         */

        private String measureMethod;
        private String patternLooseness;
        private String specId;
        private String specName;
        private String specNumber;
        private String tolerance;
        private List<SizesBean> sizes;

        public String getMeasureMethod() {
            return measureMethod;
        }

        public void setMeasureMethod(String measureMethod) {
            this.measureMethod = measureMethod;
        }

        public String getPatternLooseness() {
            return patternLooseness;
        }

        public void setPatternLooseness(String patternLooseness) {
            this.patternLooseness = patternLooseness;
        }

        public String getSpecId() {
            return specId;
        }

        public void setSpecId(String specId) {
            this.specId = specId;
        }

        public String getSpecName() {
            return specName;
        }

        public void setSpecName(String specName) {
            this.specName = specName;
        }

        public String getSpecNumber() {
            return specNumber;
        }

        public void setSpecNumber(String specNumber) {
            this.specNumber = specNumber;
        }

        public String getTolerance() {
            return tolerance;
        }

        public void setTolerance(String tolerance) {
            this.tolerance = tolerance;
        }

        public List<SizesBean> getSizes() {
            return sizes;
        }

        public void setSizes(List<SizesBean> sizes) {
            this.sizes = sizes;
        }

        public static class SizesBean {
            /**
             * sizeCode :
             * sizeValue :
             */

            private String sizeCode;
            private String sizeValue;

            public String getSizeCode() {
                return sizeCode;
            }

            public void setSizeCode(String sizeCode) {
                this.sizeCode = sizeCode;
            }

            public String getSizeValue() {
                return sizeValue;
            }

            public void setSizeValue(String sizeValue) {
                this.sizeValue = sizeValue;
            }
        }
    }
}
