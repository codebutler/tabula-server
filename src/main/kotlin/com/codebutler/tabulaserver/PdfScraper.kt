package com.codebutler.tabulaserver

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.pdfbox.pdmodel.PDDocument
import technology.tabula.ObjectExtractor
import technology.tabula.detectors.NurminenDetectionAlgorithm
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm

private val okHttpClient = OkHttpClient()
private val extractor = SpreadsheetExtractionAlgorithm()
private val detector = NurminenDetectionAlgorithm()

suspend fun scrapePdf(url: String): ScrapedPdf {
    // Request the PDF
    val pdfResponse = okHttpAsync(okHttpClient.newCall(Request.Builder()
            .url(url)
            .build()))

    // Extract data from PDF
    PDDocument.load(pdfResponse.body()!!.byteStream()).use { doc ->
        val pages = ObjectExtractor(doc).extract()
                .asSequence()
                .map { page ->
                    detector.detect(page)
                            .map { guessRect -> page.getArea(guessRect) }
                            .flatMap { guess -> extractor.extract(guess) }
                }

        // Transform into desired object structure
        return ScrapedPdf(
                pages = pages.map { tables ->
                    ScrapedPdf.Page(
                            tables = tables.map { table ->
                                val headerRow = table.rows.first()
                                val dataRows = table.rows.drop(1)
                                ScrapedPdf.Page.Table(
                                        data = dataRows.map { row ->
                                            headerRow.zip(row)
                                                    .associate { (headerRow, dataRow) ->
                                                        headerRow.text to dataRow.text
                                                    }
                                        }
                                )
                            }
                    )
                }.toList()
        )
    }
}

data class ScrapedPdf(val pages: List<Page>) {
    data class Page(val tables: List<Table>) {
        data class Table(val data: List<Map<String, String>>)
    }
}