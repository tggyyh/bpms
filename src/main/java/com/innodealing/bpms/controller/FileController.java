package com.innodealing.bpms.controller;

import com.innodealing.bpms.appconfig.ftpconfig.FtpClientFactory;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.*;
import com.innodealing.bpms.model.Attachment;
import com.innodealing.bpms.model.AttachmentList;
import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.unit.FtpUtil;
import com.innodealing.bpms.unit.Generate;
import com.innodealing.commons.http.RestResponse;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    BondService bondService;
    @Autowired
    CompanyLinkmanService companyLinkmanService;
    @Autowired
    BankManagerService bankManagerService;
    @Autowired
    AreaService areaService;

    @Autowired
    private FtpClientFactory ftpClientFactory;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public RestResponse<Attachment[]> upload(@RequestParam("file") MultipartFile file[]) {
        RestResponse<Attachment[]> result;
        Attachment[] tmpFile = new Attachment[file.length];
        FTPClient client = null;
        try {
            client = ftpClientFactory.getClient();
            for (int i = 0; i < file.length; i++) {
                String fileName = file[i].getOriginalFilename();
                int lastIndex = fileName.lastIndexOf(".");
                String newName = Generate.generateGUID() + fileName.substring(lastIndex, fileName.length());
                FtpUtil.upload(newName, file[i].getInputStream(), client);
                logger.info("tmpFile:" + ConstantUtil.FILE_URL + newName);
                Attachment at = new Attachment();
                at.setName(fileName);
                at.setType(fileName.substring(fileName.lastIndexOf(".") + 1));
                at.setUrl(ConstantUtil.FILE_URL + newName);
                tmpFile[i] = at;
            }
            FtpUtil.close(client);
            result = RestResponse.Success("上传成功", tmpFile);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            result = RestResponse.Fail("上传失败", tmpFile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = RestResponse.Fail("上传失败", tmpFile);
        }finally {
            if(null != client && client.isConnected()){
                FtpUtil.close(client);
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public RestResponse<Attachment> uploadFile(@RequestParam("file") MultipartFile file) {
        RestResponse<Attachment> result;
        FTPClient client = null;
        Attachment attachment = new Attachment();
        try {
            client = ftpClientFactory.getClient();
            String fileName = file.getOriginalFilename();
            int lastIndex = fileName.lastIndexOf(".");
            String newName = Generate.generateGUID() + fileName.substring(lastIndex, fileName.length());
            FtpUtil.upload(newName, file.getInputStream(), client);
            logger.info("tmpFile:" + ConstantUtil.FILE_URL + newName);
            attachment.setName(fileName);
            attachment.setType(fileName.substring(fileName.lastIndexOf(".") + 1));
            attachment.setUrl(ConstantUtil.FILE_URL + newName);
            FtpUtil.close(client);
            result = RestResponse.Success("上传成功", attachment);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            result = RestResponse.Fail("上传失败", attachment);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = RestResponse.Fail("上传失败", attachment);
        }finally {
            if(null != client && client.isConnected()){
                FtpUtil.close(client);
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public void download(HttpServletRequest request, HttpServletResponse response) {
        OutputStream out = null;
        FTPClient client = null;
        try {
            response.reset();
            response.setCharacterEncoding(request.getCharacterEncoding());
            response.setContentType("application/octet-stream");
            String fileName = new String(request.getParameter("name").getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            String url = request.getParameter("url");
            InputStream in;
            out = response .getOutputStream();
            String alias = url.substring(url.lastIndexOf("/") + 1);
            client = ftpClientFactory.getClient();
            client.enterLocalPassiveMode();
            in = client.retrieveFileStream(alias);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buf)) > 0) {
                out.write(buf, 0, bytesRead);
            }
            in.close();
            out.flush();
            out.close();
            FtpUtil.close(client);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            if(null != out){
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if(null != client && client.isConnected()){
                FtpUtil.close(client);
            }
        }

    }

    //解析
    @ResponseBody
    @RequestMapping(value = "/parse", method = RequestMethod.POST)
    public RestResponse<Bond> parse(@RequestParam("file") MultipartFile file) {
        RestResponse<Bond> result;
        try {
            List<Area> areaList = areaService.findAll();
            String fileName = file.getOriginalFilename();
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Map<Integer, List<String>> data = new HashMap<>();
            Sheet sheet = workbook.getSheetAt(0);

            Cell cell = sheet.getRow(4).getCell(2);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);

            String bondCode = convertStrCell(sheet.getRow(4).getCell(2));
            String companyName = convertStrCell(sheet.getRow(50).getCell(2));

            //发行人信息
            Company company = new Company();

            //读取项目信息即项目负责人
            Bond bond = bondService.findByBondCode(bondCode);
            if(null==bond){
                bond = new Bond();
            }
            bond.setType(convertStrCell(sheet.getRow(1).getCell(2)));
            bond.setCode(bondCode);
            bond.setShortname(convertStrCell(sheet.getRow(2).getCell(2)));
            bond.setName(convertStrCell(sheet.getRow(3).getCell(2)));
            bond.setRateType(convertStrCell(sheet.getRow(5).getCell(2)));
            if(sheet.getRow(7).getCell(2).toString().length()>0){
                bond.setCurrentBalance(new BigDecimal(sheet.getRow(7).getCell(2).getNumericCellValue()));
            }
            bond.setBondManager(convertStrCell(sheet.getRow(8).getCell(2)));
            bond.setInterestPayMode(convertStrCell(sheet.getRow(9).getCell(2)));
            bond.setRepayMode(convertStrCell(sheet.getRow(10).getCell(2)));
            String strPayFrequency = convertStrCell(sheet.getRow(11).getCell(2));
            if(strPayFrequency.equals("按年支付")){
                bond.setPayFrequency(1);
            }else if(strPayFrequency.equals("按季支付")){
                bond.setPayFrequency(0);
            }else{
                bond.setPayFrequency(1);
            }
            if(sheet.getRow(12).getCell(2).toString().length()>0) {

                bond.setTimeLimit(Double.valueOf(convertStrCell(sheet.getRow(12).getCell(2))).intValue());
            }
            bond.setSpecialTimeLimit(convertStrCell(sheet.getRow(13).getCell(2)));
            bond.setValueDate(sheet.getRow(14).getCell(2).getDateCellValue());
            bond.setPayDay(sheet.getRow(15).getCell(2).getDateCellValue());
            bond.setOptionType(convertStrCell(sheet.getRow(16).getCell(2)));
            bond.setExerciseSchedule(convertStrCell(sheet.getRow(17).getCell(2)));
            if(sheet.getRow(18).getCell(2).toString().length()>0){
                bond.setCurrentPeriodAmount(new BigDecimal(sheet.getRow(18).getCell(2).getNumericCellValue()));
            }
            if(sheet.getRow(19).getCell(2).toString().length()>0){
                bond.setRate(new BigDecimal(sheet.getRow(19).getCell(2).getNumericCellValue()));
            }
            if(sheet.getRow(20).getCell(2).toString().length()>0){
                bond.setRateHigh(new BigDecimal(sheet.getRow(20).getCell(2).getNumericCellValue()));
            }
            if(sheet.getRow(21).getCell(2).toString().length()>0){
                bond.setRateLow(new BigDecimal(sheet.getRow(21).getCell(2).getNumericCellValue()));
            }

            if(sheet.getRow(25).getCell(2).toString().length()>0){
                bond.setDocumentAmount(new BigDecimal(sheet.getRow(25).getCell(2).getNumericCellValue()));
            }
            if(sheet.getRow(26).getCell(2).toString().length()>0){
                bond.setStockExchangePrice(Double.valueOf(convertStrCell(sheet.getRow(26).getCell(2))).intValue());
            }
            if(sheet.getRow(27).getCell(2).toString().length()>0){
                bond.setStockExchangeRatio(new BigDecimal(sheet.getRow(27).getCell(2).getNumericCellValue()));
            }

            String stockDate = convertStrCell(sheet.getRow(29).getCell(2));
            if(stockDate.length()>=20){
                String strBeginDate = stockDate.substring(0,10);
                String strEndDate = stockDate.substring(stockDate.length()-10,stockDate.length());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                ParsePosition beginPos = new ParsePosition(0);
                Date beginDate = formatter.parse(strBeginDate, beginPos);
                ParsePosition endPos = new ParsePosition(0);
                Date endDate = formatter.parse(strEndDate, endPos);
                bond.setStockExchangeBegin(beginDate);
                bond.setStockExchangeEnd(endDate);
            }

            bond.setRansomTerm(convertStrCell(sheet.getRow(30).getCell(2)));
            bond.setBackSaleTerm(convertStrCell(sheet.getRow(31).getCell(2)));
            bond.setExchangeStockTerm(convertStrCell(sheet.getRow(32).getCell(2)));
            bond.setWriteDownTerm(convertStrCell(sheet.getRow(33).getCell(2)));
            bond.setBidStockCode(convertStrCell(sheet.getRow(34).getCell(2)));
            bond.setListedPlace(convertStrCell(sheet.getRow(47).getCell(2)));
            bond.setCrossMarket(convertStrCell(sheet.getRow(48).getCell(2)));

            if(sheet.getRow(49).getCell(2).toString().length()>0){
                company.setCompanyCount(Double.valueOf(convertStrCell(sheet.getRow(49).getCell(2))).intValue());
            }
            bond.setCompanyName(companyName);

            company.setName(companyName);
            company.setStockCode(convertStrCell(sheet.getRow(52).getCell(2)));
            company.setIndustryScale(convertStrCell(sheet.getRow(53).getCell(2)));
            company.setIndustryType(convertStrCell(sheet.getRow(54).getCell(2)));
            company.setIndustryBigType(convertStrCell(sheet.getRow(55).getCell(2)));
            company.setEconomicDepartment(convertStrCell(sheet.getRow(56).getCell(2)));
            company.setEconomicDepartmentDetail(convertStrCell(sheet.getRow(57).getCell(2)));
            company.setEconomicSector(convertStrCell(sheet.getRow(58).getCell(2)));
            company.setProvinceName(convertStrCell(sheet.getRow(59).getCell(2)));
            company.setCityName(convertStrCell(sheet.getRow(60).getCell(2)));

            if(sheet.getRow(72).getCell(2).toString().length()>0){
                bond.setScale(new BigDecimal(sheet.getRow(72).getCell(2).getNumericCellValue()));
            }
            if(sheet.getRow(73).getCell(2).toString().length()>0){
                bond.setPrice(new BigDecimal(sheet.getRow(73).getCell(2).getNumericCellValue()));
            }

            bond.setPublishType(convertStrCell(sheet.getRow(76).getCell(2)));
            bond.setFixPriceType(convertStrCell(sheet.getRow(77).getCell(2)));
            bond.setUnderwriterName(convertStrCell(sheet.getRow(84).getCell(2)));
            bond.setViceUnderwriterName(convertStrCell(sheet.getRow(86).getCell(2)));
            bond.setUnderwritingMode(convertStrCell(sheet.getRow(88).getCell(2)));

            company.setRatingCompany1(convertStrCell(sheet.getRow(89).getCell(2)));
            String corporateRating1 = convertStrCell(sheet.getRow(91).getCell(2)).equals("无")?"":convertStrCell(sheet.getRow(91).getCell(2));
            company.setCorporateRating1(corporateRating1);
            company.setRatingCompany2(convertStrCell(sheet.getRow(92).getCell(2)));
            company.setCorporateOrgCode2(convertStrCell(sheet.getRow(93).getCell(2)));
            String corporateRating2 = convertStrCell(sheet.getRow(94).getCell(2)).equals("无")?"":convertStrCell(sheet.getRow(94).getCell(2));
            company.setCorporateRating2(corporateRating2);

            bond.setBondRatingCompany1(convertStrCell(sheet.getRow(95).getCell(2)));
            String rating1 = convertStrCell(sheet.getRow(97).getCell(2)).equals("无")?"":convertStrCell(sheet.getRow(97).getCell(2));
            bond.setRating1(rating1);
            bond.setBondRatingCompany2(convertStrCell(sheet.getRow(98).getCell(2)));
            bond.setBondRatingOrgCode2(convertStrCell(sheet.getRow(99).getCell(2)));
            String rating2 = convertStrCell(sheet.getRow(100).getCell(2)).equals("无")?"":convertStrCell(sheet.getRow(100).getCell(2));
            bond.setRating2(rating2);
            bond.setGuaranteeCompany1(convertStrCell(sheet.getRow(101).getCell(2)));
            String guaranteeMode1 = convertStrCell(sheet.getRow(103).getCell(2)).equals("无")?"":convertStrCell(sheet.getRow(103).getCell(2));
            bond.setGuaranteeMode1(guaranteeMode1);
            bond.setGuaranteeCompany2(convertStrCell(sheet.getRow(104).getCell(2)));
            bond.setGuaranteeOrgCode2(convertStrCell(sheet.getRow(105).getCell(2)));
            String guaranteeMode2 = convertStrCell(sheet.getRow(106).getCell(2)).equals("无")?"":convertStrCell(sheet.getRow(106).getCell(2));
            bond.setGuaranteeMode2(guaranteeMode2);
            bond.setCreditMode(convertStrCell(sheet.getRow(107).getCell(2)));

            //项目负责人
            List<BankManager> bankManagerList = bankManagerService.findByBondCode(bondCode);
            bond.setBankManagerList(bankManagerList);

            //发行人对接人
            List<CompanyLinkman> companyLinkmanList = companyLinkmanService.findByCompanyName(companyName);
            company.setCompanyLinkmanList(companyLinkmanList);

            bond.setCompany(company);

            result = RestResponse.Success("导入成功", bond);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            result = RestResponse.Fail("导入失败", null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = RestResponse.Fail("导入失败", null);
        }
        return result;
    }

    private String convertStrCell(Cell cell){
        String cellValue = null;
        if (null != cell) {
            switch (cell.getCellType()) {                     // 判断excel单元格内容的格式，并对其进行转换，以便插入数据库
                case 0:
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    break;
                case 1:
                    cellValue = cell.getStringCellValue();
                    break;
                case 2:
                    cellValue = cell.getNumericCellValue() + "";
                    // cellValue = String.valueOf(cell.getDateCellValue());
                    break;
                case 3:
                    cellValue = "";
                    break;
                case 4:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case 5:
                    cellValue = String.valueOf(cell.getErrorCellValue());
                    break;
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

    @ResponseBody
    @RequestMapping(value = "/downloadZip", method = RequestMethod.POST)
    public void downloadZip(AttachmentList attachments, HttpServletRequest request, HttpServletResponse response) {
        ZipOutputStream out = null;
        FTPClient client = null;
        try {
            client = ftpClientFactory.getClient();
            response.reset();
            response.setCharacterEncoding(request.getCharacterEncoding());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=file.zip");
            out = new ZipOutputStream(response.getOutputStream());
            for (Attachment at : attachments.getAttachments()) {
//                String fileName = new String(at.getName().getBytes("gb2312"), "ISO8859-1");
                InputStream in;
                String alias = at.getUrl().substring(at.getUrl().lastIndexOf("/") + 1);
                client.enterLocalPassiveMode();
                in = client.retrieveFileStream(alias);
                out.putNextEntry(new ZipEntry(at.getName()));
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buf)) > 0) {
                    out.write(buf, 0, bytesRead);
                }
                out.flush();
                out.closeEntry();
                in.close();
                client.completePendingCommand();
            }
            out.close();
            response.flushBuffer();
            FtpUtil.close(client);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            if(null != out){
                try {
                    out.close();

                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if(null != client && client.isConnected()){
                FtpUtil.close(client);
            }
        }
    }
}

