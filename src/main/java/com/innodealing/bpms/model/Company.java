
package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class Company {
    private int id;
    private String name;
    @CompareField(mark = "发行人简称")
    private String shortname;
    @CompareField(mark = "债券发行人个数")
    private int companyCount;
    @CompareField(mark = "发行人企业规模")
    private String industryScale;
    @CompareField(mark = "发行人简称")
    private String economicSector;
    @CompareField(mark = "发行人行业（门类）", type = 2, kind = 6)
    private String industryType;
    @CompareField(mark = "发行人行业（大类）", type = 2, kind = 6)
    private String industryBigType;
    private String industryTypeName;
    private String industryBigTypeName;
    @CompareField(mark = "发行人国民经济部门（一级分类）", type = 2, kind = 7)
    private String economicDepartment;
    @CompareField(mark = "发行人国民经济部门（明细分类）", type = 2, kind = 7)
    private String economicDepartmentDetail;
    private String economicDepartmentName;
    private String economicDepartmentDetailName;
    @CompareField(mark = "上市公司股票代码")
    private String stockCode;
    @CompareField(mark = "地域：省", type = 2, kind = 8)
    private String provinceCode;
    @CompareField(mark = "地域：市", type = 2, kind = 8)
    private String cityCode;
    @CompareField(mark = "主体评级机构名称1")
    private String ratingCompany1;
    @CompareField(mark = "发行时主体评级1")
    private String corporateRating1;
    @CompareField(mark = "主体评级机构名称2")
    private String ratingCompany2;
    @CompareField(mark = "发行时主体评级2")
    private String corporateRating2;
    @CompareField(mark = "主体评级机构组织机构代码2")
    private String corporateOrgCode2;
    private int status;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String provinceName;
    private String cityName;
    private Integer matterId;
    private int proNum1;
    private int proNum2;

    private List<CompanyMatter> companyMatterList;
    @CompareField(mark = "发行人对接人")
    private List<CompanyLinkman> companyLinkmanList;
    private List<CustomMatter> customMatterList;

    private List<Bond> bondList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public int getCompanyCount() {
        return companyCount;
    }

    public void setCompanyCount(int companyCount) {
        this.companyCount = companyCount;
    }

    public String getIndustryScale() {
        return industryScale;
    }

    public void setIndustryScale(String industryScale) {
        this.industryScale = industryScale;
    }

    public String getEconomicSector() {
        return economicSector;
    }

    public void setEconomicSector(String economicSector) {
        this.economicSector = economicSector;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getIndustryBigType() {
        return industryBigType;
    }

    public void setIndustryBigType(String industryBigType) {
        this.industryBigType = industryBigType;
    }

    public String getIndustryTypeName() {
        return industryTypeName;
    }

    public void setIndustryTypeName(String industryTypeName) {
        this.industryTypeName = industryTypeName;
    }

    public String getIndustryBigTypeName() {
        return industryBigTypeName;
    }

    public void setIndustryBigTypeName(String industryBigTypeName) {
        this.industryBigTypeName = industryBigTypeName;
    }

    public String getEconomicDepartmentName() {
        return economicDepartmentName;
    }

    public void setEconomicDepartmentName(String economicDepartmentName) {
        this.economicDepartmentName = economicDepartmentName;
    }

    public String getEconomicDepartmentDetailName() {
        return economicDepartmentDetailName;
    }

    public void setEconomicDepartmentDetailName(String economicDepartmentDetailName) {
        this.economicDepartmentDetailName = economicDepartmentDetailName;
    }

    public String getEconomicDepartment() {
        return economicDepartment;
    }

    public void setEconomicDepartment(String economicDepartment) {
        this.economicDepartment = economicDepartment;
    }

    public String getEconomicDepartmentDetail() {
        return economicDepartmentDetail;
    }

    public void setEconomicDepartmentDetail(String economicDepartmentDetail) {
        this.economicDepartmentDetail = economicDepartmentDetail;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getRatingCompany1() {
        return ratingCompany1;
    }

    public void setRatingCompany1(String ratingCompany1) {
        this.ratingCompany1 = ratingCompany1;
    }

    public String getCorporateRating1() {
        return corporateRating1;
    }

    public void setCorporateRating1(String corporateRating1) {
        this.corporateRating1 = corporateRating1;
    }

    public String getRatingCompany2() {
        return ratingCompany2;
    }

    public void setRatingCompany2(String ratingCompany2) {
        this.ratingCompany2 = ratingCompany2;
    }

    public String getCorporateRating2() {
        return corporateRating2;
    }

    public void setCorporateRating2(String corporateRating2) {
        this.corporateRating2 = corporateRating2;
    }

    public String getCorporateOrgCode2() {
        return corporateOrgCode2;
    }

    public void setCorporateOrgCode2(String corporateOrgCode2) {
        this.corporateOrgCode2 = corporateOrgCode2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<CompanyMatter> getCompanyMatterList() {
        return companyMatterList;
    }

    public void setCompanyMatterList(List<CompanyMatter> companyMatterList) {
        this.companyMatterList = companyMatterList;
    }

    public List<CompanyLinkman> getCompanyLinkmanList() {
        return companyLinkmanList;
    }

    public void setCompanyLinkmanList(List<CompanyLinkman> companyLinkmanList) {
        this.companyLinkmanList = companyLinkmanList;
    }

    public Integer getMatterId() {
        return matterId;
    }

    public void setMatterId(Integer matterId) {
        this.matterId = matterId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getProNum1() {
        return proNum1;
    }

    public void setProNum1(int proNum1) {
        this.proNum1 = proNum1;
    }

    public int getProNum2() {
        return proNum2;
    }

    public void setProNum2(int proNum2) {
        this.proNum2 = proNum2;
    }

    public List<CustomMatter> getCustomMatterList() {
        return customMatterList;
    }

    public void setCustomMatterList(List<CustomMatter> customMatterList) {
        this.customMatterList = customMatterList;
    }

    public List<Bond> getBondList() {
        return bondList;
    }

    public void setBondList(List<Bond> bondList) {
        this.bondList = bondList;
    }


    @Override
    public int hashCode() {
        int result = shortname != null ? shortname.hashCode() : 0;
        result = 31 * result + companyCount;
        result = 31 * result + (industryScale != null ? industryScale.hashCode() : 0);
        result = 31 * result + (economicSector != null ? economicSector.hashCode() : 0);
        result = 31 * result + (industryType != null ? industryType.hashCode() : 0);
        result = 31 * result + (industryBigType != null ? industryBigType.hashCode() : 0);
        result = 31 * result + (economicDepartment != null ? economicDepartment.hashCode() : 0);
        result = 31 * result + (economicDepartmentDetail != null ? economicDepartmentDetail.hashCode() : 0);
        result = 31 * result + (stockCode != null ? stockCode.hashCode() : 0);
        result = 31 * result + (provinceCode != null ? provinceCode.hashCode() : 0);
        result = 31 * result + (cityCode != null ? cityCode.hashCode() : 0);
        result = 31 * result + (ratingCompany1 != null ? ratingCompany1.hashCode() : 0);
        result = 31 * result + (corporateRating1 != null ? corporateRating1.hashCode() : 0);
        result = 31 * result + (ratingCompany2 != null ? ratingCompany2.hashCode() : 0);
        result = 31 * result + (corporateRating2 != null ? corporateRating2.hashCode() : 0);
        result = 31 * result + (corporateOrgCode2 != null ? corporateOrgCode2.hashCode() : 0);
        result = 31 * result + (companyLinkmanList != null ? companyLinkmanList.hashCode() : 0);
        return result;
    }
}
