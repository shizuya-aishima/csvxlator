package com.example.library;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvToExcelConverter {
    private static final Logger logger = LoggerFactory.getLogger(CsvToExcelConverter.class);

    private final CSVFormat csvFormat;
    private final Charset charset;

    /**
     * デフォルトのコンストラクタ。
     * CSV形式はRFC4180準拠、文字コードはUTF-8を使用します。
     */
    public CsvToExcelConverter() {
        this(CSVFormat.RFC4180.builder().setHeader().build(), StandardCharsets.UTF_8);
    }

    /**
     * カスタム設定でコンバーターを作成します。
     *
     * @param csvFormat CSV形式の設定
     * @param charset   文字コード
     */
    public CsvToExcelConverter(CSVFormat csvFormat, Charset charset) {
        this.csvFormat = csvFormat;
        this.charset = charset;
    }

    /**
     * CSVファイルをExcelファイルに変換します。
     *
     * @param csvFile  入力CSVファイル
     * @param excelFile 出力Excelファイル
     * @throws IOException 入出力エラーが発生した場合
     */
    public void convert(File csvFile, File excelFile) throws IOException {
        logger.info("Converting CSV to Excel: {} -> {}", csvFile.getName(), excelFile.getName());
        
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), charset));
             CSVParser parser = csvFormat.parse(reader);
             Workbook workbook = new XSSFWorkbook()) {
            
            Sheet sheet = workbook.createSheet("Sheet1");
            
            // ヘッダーの作成
            List<String> headers = parser.getHeaderNames();
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
                // 固定の列幅を設定（文字数 * 256）
                sheet.setColumnWidth(i, 15 * 256);
            }
            
            // データの書き込み
            int rowNum = 1;
            for (CSVRecord record : parser) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(record.get(i));
                }
            }
            
            // Excelファイルの保存
            try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
            }
        }
        
        logger.info("Conversion completed successfully");
    }

    /**
     * ヘッダー行のスタイルを作成します。
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 入力ストリームからExcelファイルに変換します。
     *
     * @param inputStream 入力CSVストリーム
     * @param excelFile   出力Excelファイル
     * @throws IOException 入出力エラーが発生した場合
     */
    public void convert(InputStream inputStream, File excelFile) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            File tempFile = File.createTempFile("csv", ".tmp");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                reader.transferTo(new OutputStreamWriter(out, charset));
            }
            convert(tempFile, excelFile);
            tempFile.delete();
        }
    }
} 