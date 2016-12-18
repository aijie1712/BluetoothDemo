package com.emba.bluetoothdemo.utils;

import android.os.Environment;

import com.emba.bluetoothdemo.model.EventModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 41369 on 2016/12/18.
 */

public class DbUtils {
    public static void save(String event) {
        EventModel eventModel = new EventModel();
        eventModel.setEvent(event);
        eventModel.setTime(System.currentTimeMillis());
        eventModel.save();
    }

    /**
     *
     * @return
     */
    public static String createExcel() {
        // 创建文档
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);

        List<EventModel> data = DataSupport.findAll(EventModel.class);
        HSSFSheet sheet = workbook.createSheet("日志");
        int accelerometerSize = data.size();
        if (accelerometerSize > 0) {
            for (short i = 0; i < accelerometerSize; i++) {
                EventModel eventModel = data.get(i);
                Object[] values = {new Date(eventModel.getTime()), eventModel.getEvent()};
                insertRow(sheet, i, values, cellStyle);
            }
        }

        // 保存文档
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp";
        new File(dirPath).mkdirs();
        File file = new File(dirPath, "sensor_test.xls");
        FileOutputStream fos;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 插入一行数据
     *
     * @param sheet        插入数据行的表单
     * @param rowIndex     插入的行的索引
     * @param columnValues 要插入一行中的数据，数组表示
     * @param cellStyle    该格中数据的显示样式
     */

    private static void insertRow(HSSFSheet sheet, short rowIndex,
                                  Object[] columnValues, HSSFCellStyle cellStyle) {
        HSSFRow row = sheet.createRow(rowIndex);
        int column = columnValues.length;
        for (short i = 0; i < column; i++) {
            createCell(row, i, columnValues[i], cellStyle);
        }
    }

    /**
     * 在一行中插入一个单元值
     *
     * @param row         要插入的数据的行
     * @param columnIndex 插入的列的索引
     * @param cellValue   该cell的值：如果是Calendar或者Date类型，就先对其格式化
     * @param cellStyle   该格中数据的显示样式
     */
    private static void createCell(HSSFRow row, short columnIndex, Object cellValue,
                                   HSSFCellStyle cellStyle) {
        HSSFCell cell = row.createCell(columnIndex);
        // 如果是Calender或者Date类型的数据，就格式化成字符串
        if (cellValue instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String value = format.format(cellValue);
            HSSFRichTextString richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        } else if (cellValue instanceof Calendar) {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String value = format.format(((Calendar) cellValue).getTime());
            HSSFRichTextString richTextString = new HSSFRichTextString(value);
            cell.setCellValue(richTextString);
        } else {
            HSSFRichTextString richTextString = new HSSFRichTextString(cellValue.toString());
            cell.setCellValue(richTextString);
        }
        cell.setCellStyle(cellStyle);
    }
}
