package com.maxbill.tool;

import com.maxbill.base.bean.Connect;
import com.maxbill.base.bean.ExcelBean;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {


    /**
     * 导出Excel
     */
    public static Boolean exportExcel(ExcelBean excelBean, String filePath) {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(excelBean.getName());
        sheet.setDefaultColumnWidth(20);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_GENERAL);
        //声明列对象
        HSSFCell cell = null;
        //创建标题
        List<String> titles = excelBean.getTitles();
        for (int i = 0; i < titles.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
            cell.setCellStyle(style);
        }
        //创建内容
        List<Connect> values = excelBean.getRows();
        for (int i = 0; i < values.size(); i++) {
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(values.get(i).getName());
           // row.createCell(1).setCellValue(values.get(i).getHost());
            //row.createCell(2).setCellValue(values.get(i).getPort());
            //row.createCell(3).setCellValue(values.get(i).getPass());
            row.createCell(4).setCellValue(values.get(i).getTime());
        }
        Boolean exportFlag = true;
        try {
            File file = new File(filePath);
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            exportFlag = false;
        }
       /*
        try {
            //输出Excel文件
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            response.setContentType("application/msexcel");
            wb.write(output);
            output.close();
        } catch (Exception e) {
            exportFlag = false;
        }
         */
        return exportFlag;
    }


    /**
     * 导入Excel
     */
    public static List<Connect> importExcel(InputStream in) {
        List<Connect> connectList = new ArrayList<>();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);
            //遍历当前sheet中的所有行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = row.getCell(0).toString();
                String host = row.getCell(1).toString();
                String port = row.getCell(2).toString();
                String pass = row.getCell(3).toString();
                String time = row.getCell(4).toString();
                if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(host) && !StringUtils.isEmpty(port)) {
                    Connect connect = new Connect();
                    connect.setId(KeyUtil.getUUIDKey());
                    connect.setName(name);
                   // connect.setHost(host);
                    //connect.setPort(port);
                    //connect.setPass(pass);
                    connect.setTime(time);
                    if (StringUtils.isEmpty(pass)) {
                      //  connect.setPass("");
                    }
                    if (StringUtils.isEmpty(time)) {
                      //  connect.setPass(DateUtil.formatDateTime(new Date()));
                    }
                    connectList.add(connect);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectList;
    }

}
