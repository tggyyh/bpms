package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Bond {
    private int id;
    @CompareField(mark = "债券代码")
    private String code;
    @CompareField(mark = "债券全称")
    private String name;
    @CompareField(mark = "债券简称")
    private String shortname;
    @CompareField(mark = "债券类型")
    private String type;
    @CompareField(mark = "发行人机构名称")
    private String companyName;
    @CompareField(mark = "利率类型")
    private String rateType;
    @CompareField(mark = "当前余额（亿元）", type = 3)
    private BigDecimal currentBalance;
    @CompareField(mark = "债券受托管理人/债权代理人")
    private String bondManager;
    @CompareField(mark = "付息方式")
    private String interestPayMode;
    @CompareField(mark = "本金偿还方式")
    private String repayMode;
    @CompareField(mark = "付息频率", type = 2, kind = 4)
    private Integer payFrequency;
    @CompareField(mark = "债券期限（年）")
    private Integer timeLimit;
    @CompareField(mark = "特殊期限（年）")
    private String specialTimeLimit;
    @CompareField(mark = "起息日期", type = 4)
    private Date valueDate;
    @CompareField(mark = "兑付日期", type = 4)
    private Date payDay;
    @CompareField(mark = "选择权种类")
    private String optionType;
    @CompareField(mark = "行权期")
    private String exerciseSchedule;
    @CompareField(mark = "票面金额（元）", type = 3)
    private BigDecimal currentPeriodAmount;
    @CompareField(mark = "票面利率（%）", type = 3)
    private BigDecimal rate;
    @CompareField(mark = "票面利率调整上限（%）", type = 3)
    private BigDecimal rateHigh;
    @CompareField(mark = "票面利率调整下限（%）", type = 3)
    private BigDecimal rateLow;
    @CompareField(mark = "内含特殊条款")
    private String specialTerm;
    @CompareField(mark = "特殊条款类别")
    private String specailTermType;
    @CompareField(mark = "核准总额（亿元）(即批文额度)", type = 3)
    private BigDecimal documentAmount;
    @CompareField(mark = "初始换股价格（元）")
    private Integer stockExchangePrice;
    @CompareField(mark = "初始换股比例（%）", type = 3)
    private BigDecimal stockExchangeRatio;
    @CompareField(mark = "换股期间起始", type = 4)
    private Date stockExchangeBegin;
    @CompareField(mark = "换股期间结束", type = 4)
    private Date stockExchangeEnd;
    @CompareField(mark = "赎回条款")
    private String ransomTerm;
    @CompareField(mark = "回售条款")
    private String backSaleTerm;
    @CompareField(mark = "可交换为股票条款")
    private String exchangeStockTerm;
    @CompareField(mark = "减记条款")
    private String writeDownTerm;
    @CompareField(mark = "转股标的股票代码")
    private String bidStockCode;
    @CompareField(mark = "上市场所")
    private String listedPlace;
    @CompareField(mark = "是否跨市场")
    private String crossMarket;
    @CompareField(mark = "发行规模（亿元）", type = 3)
    private BigDecimal scale;
    @CompareField(mark = "发行价格（元）", type = 3)
    private BigDecimal price;
    @CompareField(mark = "发行方式")
    private String publishType;
    @CompareField(mark = "发行定价方式")
    private String fixPriceType;
    @CompareField(mark = "主承销商名称")
    private String underwriterName;
    @CompareField(mark = "副主承销商名称")
    private String viceUnderwriterName;
    @CompareField(mark = "承销方式")
    private String underwritingMode;
    @CompareField(mark = "债项评级机构名称1")
    private String bondRatingCompany1;
    @CompareField(mark = " 发行时债项评级1")
    private String rating1;
    @CompareField(mark = "债项评级机构名称2")
    private String bondRatingCompany2;
    @CompareField(mark = "债项评级机构组织机构代码2")
    private String bondRatingOrgCode2;
    @CompareField(mark = "发行时债项评级2")
    private String rating2;
    @CompareField(mark = "担保机构名称1")
    private String guaranteeCompany1;
    @CompareField(mark = "担保方式1")
    private String guaranteeMode1;
    @CompareField(mark = "担保机构名称2")
    private String guaranteeCompany2;
    @CompareField(mark = "担保机构组织机构代码2")
    private String guaranteeOrgCode2;
    @CompareField(mark = "担保方式2")
    private String guaranteeMode2;
    @CompareField(mark = "信用增级方式")
    private String creditMode;
    private Date createTime;
    private Date updateTime;
    @CompareField(mark = "")
    private int status;
    @CompareField(mark = "上市日期", type = 4)
    private Date listedDate;

    private Integer matterId;
    @CompareField(mark = "", type = 5)
    private Company company;
    private List<BondMatter> bondMatterList;
    @CompareField(mark = "项目负责人", type = 0, kind = 10)
    private List<BondManager> bondManagerList;
    @CompareField(mark = "监管银行负责人")
    private List<BankManager> bankManagerList;
    private List<CustomMatter> customMatterList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getBondManager() {
        return bondManager;
    }

    public void setBondManager(String bondManager) {
        this.bondManager = bondManager;
    }

    public String getInterestPayMode() {
        return interestPayMode;
    }

    public void setInterestPayMode(String interestPayMode) {
        this.interestPayMode = interestPayMode;
    }

    public String getRepayMode() {
        return repayMode;
    }

    public void setRepayMode(String repayMode) {
        this.repayMode = repayMode;
    }

    public Integer getPayFrequency() {
        return payFrequency;
    }

    public void setPayFrequency(Integer payFrequency) {
        this.payFrequency = payFrequency;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getSpecialTimeLimit() {
        return specialTimeLimit;
    }

    public void setSpecialTimeLimit(String specialTimeLimit) {
        this.specialTimeLimit = specialTimeLimit;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getPayDay() {
        return payDay;
    }

    public void setPayDay(Date payDay) {
        this.payDay = payDay;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public String getExerciseSchedule() {
        return exerciseSchedule;
    }

    public void setExerciseSchedule(String exerciseSchedule) {
        this.exerciseSchedule = exerciseSchedule;
    }

    public BigDecimal getCurrentPeriodAmount() {
        return currentPeriodAmount;
    }

    public void setCurrentPeriodAmount(BigDecimal currentPeriodAmount) {
        this.currentPeriodAmount = currentPeriodAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRateHigh() {
        return rateHigh;
    }

    public void setRateHigh(BigDecimal rateHigh) {
        this.rateHigh = rateHigh;
    }

    public BigDecimal getRateLow() {
        return rateLow;
    }

    public void setRateLow(BigDecimal rateLow) {
        this.rateLow = rateLow;
    }

    public String getSpecialTerm() {
        return specialTerm;
    }

    public void setSpecialTerm(String specialTerm) {
        this.specialTerm = specialTerm;
    }

    public String getSpecailTermType() {
        return specailTermType;
    }

    public void setSpecailTermType(String specailTermType) {
        this.specailTermType = specailTermType;
    }

    public BigDecimal getDocumentAmount() {
        return documentAmount;
    }

    public void setDocumentAmount(BigDecimal documentAmount) {
        this.documentAmount = documentAmount;
    }

    public Integer getStockExchangePrice() {
        return stockExchangePrice;
    }

    public void setStockExchangePrice(Integer stockExchangePrice) {
        this.stockExchangePrice = stockExchangePrice;
    }

    public BigDecimal getStockExchangeRatio() {
        return stockExchangeRatio;
    }

    public void setStockExchangeRatio(BigDecimal stockExchangeRatio) {
        this.stockExchangeRatio = stockExchangeRatio;
    }

    public Date getStockExchangeBegin() {
        return stockExchangeBegin;
    }

    public void setStockExchangeBegin(Date stockExchangeBegin) {
        this.stockExchangeBegin = stockExchangeBegin;
    }

    public Date getStockExchangeEnd() {
        return stockExchangeEnd;
    }

    public void setStockExchangeEnd(Date stockExchangeEnd) {
        this.stockExchangeEnd = stockExchangeEnd;
    }

    public String getRansomTerm() {
        return ransomTerm;
    }

    public void setRansomTerm(String ransomTerm) {
        this.ransomTerm = ransomTerm;
    }

    public String getBackSaleTerm() {
        return backSaleTerm;
    }

    public void setBackSaleTerm(String backSaleTerm) {
        this.backSaleTerm = backSaleTerm;
    }

    public String getExchangeStockTerm() {
        return exchangeStockTerm;
    }

    public void setExchangeStockTerm(String exchangeStockTerm) {
        this.exchangeStockTerm = exchangeStockTerm;
    }

    public String getWriteDownTerm() {
        return writeDownTerm;
    }

    public void setWriteDownTerm(String writeDownTerm) {
        this.writeDownTerm = writeDownTerm;
    }

    public String getBidStockCode() {
        return bidStockCode;
    }

    public void setBidStockCode(String bidStockCode) {
        this.bidStockCode = bidStockCode;
    }

    public String getListedPlace() {
        return listedPlace;
    }

    public void setListedPlace(String listedPlace) {
        this.listedPlace = listedPlace;
    }

    public String getCrossMarket() {
        return crossMarket;
    }

    public void setCrossMarket(String crossMarket) {
        this.crossMarket = crossMarket;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPublishType() {
        return publishType;
    }

    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }

    public String getFixPriceType() {
        return fixPriceType;
    }

    public void setFixPriceType(String fixPriceType) {
        this.fixPriceType = fixPriceType;
    }

    public String getUnderwriterName() {
        return underwriterName;
    }

    public void setUnderwriterName(String underwriterName) {
        this.underwriterName = underwriterName;
    }

    public String getViceUnderwriterName() {
        return viceUnderwriterName;
    }

    public void setViceUnderwriterName(String viceUnderwriterName) {
        this.viceUnderwriterName = viceUnderwriterName;
    }

    public String getUnderwritingMode() {
        return underwritingMode;
    }

    public void setUnderwritingMode(String underwritingMode) {
        this.underwritingMode = underwritingMode;
    }

    public String getBondRatingCompany1() {
        return bondRatingCompany1;
    }

    public void setBondRatingCompany1(String bondRatingCompany1) {
        this.bondRatingCompany1 = bondRatingCompany1;
    }

    public String getRating1() {
        return rating1;
    }

    public void setRating1(String rating1) {
        this.rating1 = rating1;
    }

    public String getBondRatingCompany2() {
        return bondRatingCompany2;
    }

    public void setBondRatingCompany2(String bondRatingCompany2) {
        this.bondRatingCompany2 = bondRatingCompany2;
    }

    public String getBondRatingOrgCode2() {
        return bondRatingOrgCode2;
    }

    public void setBondRatingOrgCode2(String bondRatingOrgCode2) {
        this.bondRatingOrgCode2 = bondRatingOrgCode2;
    }

    public String getRating2() {
        return rating2;
    }

    public void setRating2(String rating2) {
        this.rating2 = rating2;
    }

    public String getGuaranteeCompany1() {
        return guaranteeCompany1;
    }

    public void setGuaranteeCompany1(String guaranteeCompany1) {
        this.guaranteeCompany1 = guaranteeCompany1;
    }

    public String getGuaranteeMode1() {
        return guaranteeMode1;
    }

    public void setGuaranteeMode1(String guaranteeMode1) {
        this.guaranteeMode1 = guaranteeMode1;
    }

    public String getGuaranteeCompany2() {
        return guaranteeCompany2;
    }

    public void setGuaranteeCompany2(String guaranteeCompany2) {
        this.guaranteeCompany2 = guaranteeCompany2;
    }

    public String getGuaranteeOrgCode2() {
        return guaranteeOrgCode2;
    }

    public void setGuaranteeOrgCode2(String guaranteeOrgCode2) {
        this.guaranteeOrgCode2 = guaranteeOrgCode2;
    }

    public String getGuaranteeMode2() {
        return guaranteeMode2;
    }

    public void setGuaranteeMode2(String guaranteeMode2) {
        this.guaranteeMode2 = guaranteeMode2;
    }

    public String getCreditMode() {
        return creditMode;
    }

    public void setCreditMode(String creditMode) {
        this.creditMode = creditMode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getListedDate() {
        return listedDate;
    }

    public void setListedDate(Date listedDate) {
        this.listedDate = listedDate;
    }

    public Integer getMatterId() {
        return matterId;
    }

    public void setMatterId(Integer matterId) {
        this.matterId = matterId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<BondMatter> getBondMatterList() {
        return bondMatterList;
    }

    public void setBondMatterList(List<BondMatter> bondMatterList) {
        this.bondMatterList = bondMatterList;
    }

    public List<BondManager> getBondManagerList() {
        return bondManagerList;
    }

    public void setBondManagerList(List<BondManager> bondManagerList) {
        this.bondManagerList = bondManagerList;
    }

    public List<BankManager> getBankManagerList() {
        return bankManagerList;
    }

    public void setBankManagerList(List<BankManager> bankManagerList) {
        this.bankManagerList = bankManagerList;
    }

    public List<CustomMatter> getCustomMatterList() {
        return customMatterList;
    }

    public void setCustomMatterList(List<CustomMatter> customMatterList) {
        this.customMatterList = customMatterList;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortname != null ? shortname.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (rateType != null ? rateType.hashCode() : 0);
        result = 31 * result + (currentBalance != null ? currentBalance.hashCode() : 0);
        result = 31 * result + (bondManager != null ? bondManager.hashCode() : 0);
        result = 31 * result + (interestPayMode != null ? interestPayMode.hashCode() : 0);
        result = 31 * result + (repayMode != null ? repayMode.hashCode() : 0);
        result = 31 * result + (payFrequency != null ? payFrequency.hashCode() : 0);
        result = 31 * result + (timeLimit != null ? timeLimit.hashCode() : 0);
        result = 31 * result + (specialTimeLimit != null ? specialTimeLimit.hashCode() : 0);
        result = 31 * result + (valueDate != null ? valueDate.hashCode() : 0);
        result = 31 * result + (payDay != null ? payDay.hashCode() : 0);
        result = 31 * result + (optionType != null ? optionType.hashCode() : 0);
        result = 31 * result + (exerciseSchedule != null ? exerciseSchedule.hashCode() : 0);
        result = 31 * result + (currentPeriodAmount != null ? currentPeriodAmount.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        result = 31 * result + (rateHigh != null ? rateHigh.hashCode() : 0);
        result = 31 * result + (rateLow != null ? rateLow.hashCode() : 0);
        result = 31 * result + (specialTerm != null ? specialTerm.hashCode() : 0);
        result = 31 * result + (specailTermType != null ? specailTermType.hashCode() : 0);
        result = 31 * result + (documentAmount != null ? documentAmount.hashCode() : 0);
        result = 31 * result + (stockExchangePrice != null ? stockExchangePrice.hashCode() : 0);
        result = 31 * result + (stockExchangeRatio != null ? stockExchangeRatio.hashCode() : 0);
        result = 31 * result + (stockExchangeBegin != null ? stockExchangeBegin.hashCode() : 0);
        result = 31 * result + (stockExchangeEnd != null ? stockExchangeEnd.hashCode() : 0);
        result = 31 * result + (ransomTerm != null ? ransomTerm.hashCode() : 0);
        result = 31 * result + (backSaleTerm != null ? backSaleTerm.hashCode() : 0);
        result = 31 * result + (exchangeStockTerm != null ? exchangeStockTerm.hashCode() : 0);
        result = 31 * result + (writeDownTerm != null ? writeDownTerm.hashCode() : 0);
        result = 31 * result + (bidStockCode != null ? bidStockCode.hashCode() : 0);
        result = 31 * result + (listedPlace != null ? listedPlace.hashCode() : 0);
        result = 31 * result + (crossMarket != null ? crossMarket.hashCode() : 0);
        result = 31 * result + (scale != null ? scale.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (publishType != null ? publishType.hashCode() : 0);
        result = 31 * result + (fixPriceType != null ? fixPriceType.hashCode() : 0);
        result = 31 * result + (underwriterName != null ? underwriterName.hashCode() : 0);
        result = 31 * result + (viceUnderwriterName != null ? viceUnderwriterName.hashCode() : 0);
        result = 31 * result + (underwritingMode != null ? underwritingMode.hashCode() : 0);
        result = 31 * result + (bondRatingCompany1 != null ? bondRatingCompany1.hashCode() : 0);
        result = 31 * result + (rating1 != null ? rating1.hashCode() : 0);
        result = 31 * result + (bondRatingCompany2 != null ? bondRatingCompany2.hashCode() : 0);
        result = 31 * result + (bondRatingOrgCode2 != null ? bondRatingOrgCode2.hashCode() : 0);
        result = 31 * result + (rating2 != null ? rating2.hashCode() : 0);
        result = 31 * result + (guaranteeCompany1 != null ? guaranteeCompany1.hashCode() : 0);
        result = 31 * result + (guaranteeMode1 != null ? guaranteeMode1.hashCode() : 0);
        result = 31 * result + (guaranteeCompany2 != null ? guaranteeCompany2.hashCode() : 0);
        result = 31 * result + (guaranteeOrgCode2 != null ? guaranteeOrgCode2.hashCode() : 0);
        result = 31 * result + (guaranteeMode2 != null ? guaranteeMode2.hashCode() : 0);
        result = 31 * result + (creditMode != null ? creditMode.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (listedDate != null ? listedDate.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (bondMatterList != null ? bondMatterList.hashCode() : 0);
        result = 31 * result + (bondManagerList != null ? bondManagerList.hashCode() : 0);
        result = 31 * result + (bankManagerList != null ? bankManagerList.hashCode() : 0);
        return result;
    }
}
