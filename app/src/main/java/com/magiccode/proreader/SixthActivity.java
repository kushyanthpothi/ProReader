package com.magiccode.proreader;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SixthActivity extends AppCompatActivity {
    Button btn;
    TextView txt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixth);
        btn = findViewById(R.id.pdfbtn);
        txt1 = findViewById(R.id.edit_text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txt1.getText().toString();
                String fileName = "my_pdf_file";
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                TextToPDFConverter.convertTextToPDF(text, fileName, filePath);
                Toast.makeText(SixthActivity.this,"PDF GENERATED",Toast.LENGTH_LONG).show();
            }
        });
    }
    public static class TextToPDFConverter {

        private static final String TAG = "TextToPDFConverter";

        public static void convertTextToPDF(String text, String fileName, String filePath) {
            // create a new document
            Document document = new Document(PageSize.A4);
            File file = new File(filePath);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            document.open();
            try {

                // create a file output stream to write the PDF file
                FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath, fileName + ".pdf"));
                // create a PDF writer instance to write the document to the output stream
                PdfWriter.getInstance(document, fileOutputStream);
                //Creating font style
                Font myfont = new Font(Font.FontFamily.HELVETICA,24,Font.BOLD);
                Paragraph paragraph=new Paragraph();
                paragraph.add(new Paragraph(text, myfont));
                // add the text to the document
                document.add(paragraph);


            } catch (DocumentException | FileNotFoundException e) {
                Log.e(TAG, "Error converting text to PDF", e);
            }
            document.close();
        }
    }

}
