package com.example.library;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.commons.csv.CSVFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvToExcelConverterTest {

  @TempDir Path tempDir;

  @Test
  void convertCsvToExcel() throws IOException, InvalidFormatException {
    // テスト用のCSVデータを作成
    String csvContent =
        """
                名前,年齢,都道府県
                山田太郎,30,東京都
                鈴木花子,25,大阪府
                佐藤一郎,35,福岡県
                """;

    // CSVファイルの作成
    File csvFile = tempDir.resolve("test.csv").toFile();
    try (Writer writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8))) {
      writer.write(csvContent);
    }

    // Excelファイルの出力先
    File excelFile = tempDir.resolve("test.xlsx").toFile();

    // 変換の実行
    CsvToExcelConverter converter = new CsvToExcelConverter();
    converter.convert(csvFile, excelFile);

    // 変換結果の検証
    try (Workbook workbook = new XSSFWorkbook(excelFile)) {
      Sheet sheet = workbook.getSheetAt(0);

      // ヘッダーの検証
      Row headerRow = sheet.getRow(0);
      assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("名前");
      assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("年齢");
      assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("都道府県");

      // データの検証
      Row dataRow1 = sheet.getRow(1);
      assertThat(dataRow1.getCell(0).getStringCellValue()).isEqualTo("山田太郎");
      assertThat(dataRow1.getCell(1).getStringCellValue()).isEqualTo("30");
      assertThat(dataRow1.getCell(2).getStringCellValue()).isEqualTo("東京都");

      // スタイルの検証
      CellStyle headerStyle = headerRow.getCell(0).getCellStyle();
      assertThat(headerStyle.getFillForegroundColor())
          .isEqualTo(IndexedColors.GREY_25_PERCENT.getIndex());
      assertThat(headerStyle.getBorderBottom()).isEqualTo(BorderStyle.THIN);
    }
  }

  @Test
  void convertWithCustomFormat() throws IOException, InvalidFormatException {
    // カスタムフォーマットのCSVデータを作成
    String csvContent =
        """
                "名前";"年齢";"都道府県"
                "山田太郎";"30";"東京都"
                """;

    // CSVファイルの作成
    File csvFile = tempDir.resolve("custom.csv").toFile();
    try (Writer writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8))) {
      writer.write(csvContent);
    }

    // Excelファイルの出力先
    File excelFile = tempDir.resolve("custom.xlsx").toFile();

    // カスタムフォーマットで変換
    CSVFormat customFormat =
        CSVFormat.DEFAULT.builder().setDelimiter(';').setQuote('"').setHeader().build();

    CsvToExcelConverter converter = new CsvToExcelConverter(customFormat, StandardCharsets.UTF_8);
    converter.convert(csvFile, excelFile);

    // 変換結果の検証
    try (Workbook workbook = new XSSFWorkbook(excelFile)) {
      Sheet sheet = workbook.getSheetAt(0);
      Row dataRow = sheet.getRow(1);
      assertThat(dataRow.getCell(0).getStringCellValue()).isEqualTo("山田太郎");
      assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("30");
      assertThat(dataRow.getCell(2).getStringCellValue()).isEqualTo("東京都");
    }
  }
}
