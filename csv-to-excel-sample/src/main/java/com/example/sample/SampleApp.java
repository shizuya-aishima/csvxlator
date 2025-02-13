package com.example.sample;

import com.example.library.CsvToExcelConverter;
import org.apache.commons.csv.CSVFormat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleApp {
    public static void main(String[] args) {
        try {
            // サンプルCSVファイルの作成
            Path currentDir = Paths.get("").toAbsolutePath();
            File outputDir = new File(currentDir.toFile(), "output");
            outputDir.mkdirs();

            // 基本的な使用例
            File csvFile1 = new File(outputDir, "sample1.csv");
            createSampleCsv1(csvFile1);
            
            CsvToExcelConverter converter1 = new CsvToExcelConverter();
            converter1.convert(csvFile1, new File(outputDir, "output1.xlsx"));

            // カスタム設定での使用例
            File csvFile2 = new File(outputDir, "sample2.csv");
            createSampleCsv2(csvFile2);
            
            CSVFormat customFormat = CSVFormat.DEFAULT
                .builder()
                .setDelimiter(';')
                .setQuote('"')
                .setHeader()
                .build();
            
            CsvToExcelConverter converter2 = new CsvToExcelConverter(customFormat, StandardCharsets.UTF_8);
            converter2.convert(csvFile2, new File(outputDir, "output2.xlsx"));

            System.out.println("変換が完了しました。出力ディレクトリ: " + outputDir.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createSampleCsv1(File file) throws Exception {
        String content = """
                名前,年齢,都道府県,職業
                山田太郎,30,東京都,エンジニア
                鈴木花子,25,大阪府,デザイナー
                佐藤一郎,35,福岡県,マネージャー
                田中美咲,28,北海道,医師
                """;
        java.nio.file.Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
    }

    private static void createSampleCsv2(File file) throws Exception {
        String content = """
                "名前";"年齢";"都道府県";"趣味"
                "山田太郎";"30";"東京都";"読書,旅行"
                "鈴木花子";"25";"大阪府";"料理;写真"
                "佐藤一郎";"35";"福岡県";"スポーツ;音楽"
                """;
        java.nio.file.Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
    }
} 