# CsvXlator

CSVファイルをExcelファイルに変換する軽量で柔軟なJavaライブラリです。
シンプルなAPIと豊富なカスタマイズオプションで、CSVからExcelへの変換を簡単に実現します。

![Build Status](https://github.com/shizuya-aishima/csvxlator/workflows/Java%20CI%20with%20Gradle/badge.svg)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/shizuya-aishima/csvxlator)](https://github.com/shizuya-aishima/csvxlator/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 特徴

- CSVファイルをExcelファイル（.xlsx）に変換
- ヘッダー行の自動認識とスタイル適用（グレー背景、太字、罫線）
- カスタムCSV形式のサポート（区切り文字、引用符など）
- 文字コードの指定が可能（UTF-8、Shift-JISなど）
- 列幅の自動調整
- ストリームからの読み込みをサポート
- SLF4Jによるログ出力

## 要件

- Java 21以上
- Gradle 8.12.1以上

## インストール方法

### Gradle

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/shizuya-aishima/csvxlator")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation 'io.github.shizuya-aishima:csvxlator:0.1.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/shizuya-aishima/csvxlator</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.shizuya-aishima</groupId>
        <artifactId>csvxlator</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

## 使用方法

### 1. 基本的な使用例（UTF-8、カンマ区切り）

```java
import com.example.library.CsvToExcelConverter;
import java.io.File;

// デフォルト設定（RFC4180準拠、UTF-8）でコンバーターを作成
CsvToExcelConverter converter = new CsvToExcelConverter();

// CSVファイルをExcelに変換
File csvFile = new File("input.csv");
File excelFile = new File("output.xlsx");
converter.convert(csvFile, excelFile);
```

入力CSVファイル例（input.csv）:
```csv
名前,年齢,都道府県
山田太郎,30,東京都
鈴木花子,25,大阪府
```

### 2. カスタム設定での使用例（Shift-JIS、セミコロン区切り）

```java
import org.apache.commons.csv.CSVFormat;
import java.nio.charset.Charset;

// カスタムCSV形式を指定
CSVFormat customFormat = CSVFormat.DEFAULT
    .builder()
    .setDelimiter(';')      // セミコロン区切り
    .setQuote('"')          // ダブルクォートで囲む
    .setHeader()            // 1行目をヘッダーとして扱う
    .build();

// Shift-JISでの読み込み
CsvToExcelConverter converter = new CsvToExcelConverter(
    customFormat, 
    Charset.forName("SHIFT-JIS")
);

converter.convert(csvFile, excelFile);
```

入力CSVファイル例（input.csv）:
```csv
"名前";"年齢";"都道府県"
"山田太郎";"30";"東京都"
"鈴木花子";"25";"大阪府"
```

### 3. 入力ストリームからの変換

```java
import java.io.InputStream;
import java.io.FileInputStream;

// 入力ストリームからの変換
try (InputStream input = new FileInputStream("input.csv")) {
    converter.convert(input, new File("output.xlsx"));
}
```

### 4. 生成されるExcelファイルの特徴

- ヘッダー行のスタイル
  - 背景色: グレー（25%）
  - フォント: 太字
  - 罫線: 上下左右に細線
- データ行
  - 自動的に列幅を調整
  - セル値は文字列として保存

## エラーハンドリング

主な例外と対処方法：

```java
try {
    converter.convert(csvFile, excelFile);
} catch (IOException e) {
    // ファイルの読み書きエラー
    logger.error("ファイルの処理中にエラーが発生しました: " + e.getMessage());
} catch (IllegalArgumentException e) {
    // CSVフォーマットが不正
    logger.error("不正なCSVフォーマット: " + e.getMessage());
}
```

## ログ出力

SLF4Jを使用してログを出力します：

```
INFO: Converting CSV to Excel: input.csv -> output.xlsx
INFO: Conversion completed successfully
```