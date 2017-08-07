/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestoria;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

import java.io.IOException;

/**
 *
 * @author Jadelabe
 */
public class PaysheetToPDF {

    public static void printPDF(String[] fecha) throws IOException {

        String fileName = getFileName(fecha);
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        String ID="";
        while(scanner.hasNext()){
            String[] data = scanner.nextLine().split(";");
            if(ID.equals(data[5])){
                            generatePDF(data,true);     //extra
            } else {
                            generatePDF(data,false);
            }
            ID = data[5];   
        }
    }

    private static String[] getDate() {

        Scanner in = new Scanner(System.in);

        //aqui se guarda la fecha de la nomina solicitada posicion 0 intMonth 1 anio
        String[] date;
        do {
            String fecha_teclado = in.nextLine();
            date = fecha_teclado.split("/");
        } while ((date.length != 2)
                || ((Integer.parseInt(date[0]) < 1) || (Integer.parseInt(date[0]) > 12))
                || ((Integer.parseInt(date[1]) < 1950)));
        
        return date;
    }
    private static String getFileName(String[] date) {
        int intMonth = Integer.parseInt(date[0]);
        String fileName;

        String month = "";
        switch (intMonth) {
            case 1:month = "Enero";break;
            case 2:month = "Febrero";break;
            case 3:month = "Marzo";break;
            case 4:month = "Abril";break;
            case 5:month = "Mayo";break;
            case 6:month = "Junio";break;
            case 7:month = "Julio";break;
            case 8:month = "Agosto";break;
            case 9:month = "Septiembre";break;
            case 10:month = "Octubre";break;
            case 11:month = "Noviembre";break;
            case 12:month = "Diciembre";break;
        }
        fileName = "./src/gestoria/Nominas/" + month + date[1] + ".txt";
        return fileName;
    }
    
    private static void generatePDF(String[] data, boolean extra){
        Document document = new Document();
                
        try {
            if(extra){
           PdfWriter.getInstance(document,
               new FileOutputStream("./src/gestoria/Nominas/" + data[24] + "_" + data[23] + "_" + data[5] + "_extra.pdf"));
            } else {
                PdfWriter.getInstance(document,
               new FileOutputStream("./src/gestoria/Nominas/" + data[24] + "_" + data[23] + "_" + data[5] + ".pdf"));
            }
           document.open();

            Paragraph companyInfo = new Paragraph();
            companyInfo.add(new Paragraph(data[0]));
            companyInfo.add(new Paragraph("\n"+ data[1]));
            companyInfo.add(new Paragraph("\nNº de contrato: "+ data[10]));
            PdfPTable tableCompany = new PdfPTable(1);
            PdfPCell cell1 = new PdfPCell(new Paragraph(companyInfo));
            tableCompany.addCell(cell1);
            tableCompany.setWidthPercentage(45);
            tableCompany.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(tableCompany);
            
            
            Paragraph workerInfo = new Paragraph();
            workerInfo.add( new Paragraph("Destinatario: "+ data[2]+" "+data[3]+" "+data[4]+" "));
            workerInfo.add(new Paragraph("\n"));
            workerInfo.add(new Paragraph("\n E-mail: "+ data[25]));
            workerInfo.add(new Paragraph("\n DNI: "+ data[5]));
            workerInfo.add(new Paragraph("\n Categoría: "+ data[8]));
            workerInfo.add(new Paragraph("\n Antigüedad: "+ data[6]));
            PdfPTable tableWorker = new PdfPTable(1);
            PdfPCell cell2 = new PdfPCell(new Paragraph(workerInfo));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableWorker.addCell(cell2);
            tableWorker.setWidthPercentage(45);
            tableWorker.setHorizontalAlignment(Element.ALIGN_RIGHT);
            document.add(tableWorker);
            
            Paragraph date = new Paragraph("\n Periodo liquidado  " + data[23] + "/" + data[24] + "\n\n", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            Font bfBold12 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font bf12 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            
            float[] columnWidths = {9f, 3f, 3f, 3f, 3f};
            PdfPTable table = new PdfPTable(columnWidths);
            Paragraph paragraph = new Paragraph();
            //Cabecera
            insertCell(table, "", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "cant.", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "Imp. Unit.", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "Dev.", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(table, "Deducc.", Element.ALIGN_LEFT, 1, bfBold12);
            table.setHeaderRows(1);
            insertCell(table, "-----------------------------------------------------"
                    + "--------------------------------------------------", Element.ALIGN_CENTER, 5, bfBold12);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            
            int cant = 30;
            float salBase = Float.parseFloat(data[11]);
            df.format(salBase);
            float compl = Float.parseFloat(data[12]);
            df.format(compl);
            float antig = Float.parseFloat(data[13]);
            df.format(antig);
            float prorrata = Float.parseFloat(data[19]);
            df.format(prorrata);
            
            //Salario Base
            insertCell(table, "Salario base", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, cant + " días", Element.ALIGN_RIGHT, 1, bf12);                            //cant
            insertCell(table, df.format((salBase/14)/cant), Element.ALIGN_RIGHT, 1, bf12);              //imp unit
            insertCell(table, df.format(salBase/14), Element.ALIGN_RIGHT, 1, bf12);                     //dev
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                        //deduc.
            
                        //Prorrata
                        if(prorrata!=0){
            insertCell(table, "Prorrata", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, cant + " días", Element.ALIGN_RIGHT, 1, bf12);                            //cant
            insertCell(table, df.format((prorrata/14)/cant), Element.ALIGN_RIGHT, 1, bf12);              //imp unit
            insertCell(table, df.format(prorrata/14), Element.ALIGN_RIGHT, 1, bf12);                     //dev
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                        //deduc.
                        }
            //Complemento
            insertCell(table, "Complemento", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, cant + " días", Element.ALIGN_RIGHT, 1, bf12);                            //cant
            insertCell(table, df.format((compl/14)/cant), Element.ALIGN_RIGHT, 1, bf12);                //imp unit
            insertCell(table, df.format(compl/14), Element.ALIGN_RIGHT, 1, bf12);                       //dev
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                        //deduc.
            
            //Antigüedad
            insertCell(table, "Antigüedad", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, cant + " días", Element.ALIGN_RIGHT, 1, bf12);                            //cant
            insertCell(table, df.format((antig/14)/cant), Element.ALIGN_RIGHT, 1, bf12);                //imp unit
            insertCell(table, df.format(antig/14), Element.ALIGN_RIGHT, 1, bf12);                       //dev
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                        //deduc.
            
            insertCell(table, "", Element.ALIGN_CENTER, 5, bfBold12);

            float contGen = Float.parseFloat(data[16]);
            df.format(contGen);
            float desempleo = Float.parseFloat(data[17]);
            df.format(desempleo);
            float cuotaForm = Float.parseFloat(data[18]);
            df.format(cuotaForm);
            float irpf = Float.parseFloat(data[9]);
            df.format(irpf);
            float salBruto = Float.parseFloat(data[14]);
            df.format(salBruto);
            float baseIRPF = Float.parseFloat(data[21]);
            df.format(baseIRPF);
            Float deducc[] = {(salBruto*contGen/100), (salBruto*desempleo/100), (salBruto*cuotaForm/100),(baseIRPF*irpf/100)};
            
            //Contingencias Generales
            insertCell(table, "Contingencias Generales", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, data[16] + "%", Element.ALIGN_RIGHT, 1, bf12);                        //cant
            insertCell(table, "("+ df.format(salBruto) + ")", Element.ALIGN_RIGHT, 1, bf12);        //imp unit
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                    //dev
            insertCell(table, df.format(deducc[0]), Element.ALIGN_RIGHT, 1, bf12);                  //deduc.
           
            //Desempleo
            insertCell(table, "Desempleo", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, data[17] + "%", Element.ALIGN_RIGHT, 1, bf12);                        //cant
            insertCell(table, "("+ df.format(salBruto) + ")", Element.ALIGN_RIGHT, 1, bf12);        //imp unit
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                    //dev
            insertCell(table, df.format(deducc[1]), Element.ALIGN_RIGHT, 1, bf12);                  //deduc.
            
            //Cuota formación
            insertCell(table, "Cuota formación", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, data[18] + "%", Element.ALIGN_RIGHT, 1, bf12);                        //cant
            insertCell(table, "("+ df.format(salBruto) + ")", Element.ALIGN_RIGHT, 1, bf12);        //imp unit
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                    //dev
            insertCell(table, df.format(deducc[2]), Element.ALIGN_RIGHT, 1, bf12);                  //deduc.
            
            //IRPF
            insertCell(table, "IRPF", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, data[9] + "%", Element.ALIGN_RIGHT, 1, bf12);                         //cant
            insertCell(table, "("+ df.format(baseIRPF) + ")", Element.ALIGN_RIGHT, 1, bf12);        //imp unit
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                    //dev
            insertCell(table, df.format(deducc[3]), Element.ALIGN_RIGHT, 1, bf12);                  //deduc.

            int dias = Integer.parseInt(data[33]);
            float dias75 = Float.parseFloat(data[36]);
            df.format(dias75);
            float dias50 = Float.parseFloat(data[34]);
            df.format(dias50);
            if (dias != 0) {
                insertCell(table, "Baja 50%", Element.ALIGN_LEFT, 1, bf12);
                insertCell(table, data[33] + "días", Element.ALIGN_RIGHT, 1, bf12);                                 //cant
                insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                //imp unit
                insertCell(table, "-" + df.format(dias50), Element.ALIGN_RIGHT, 1, bf12);                                    //dev
                insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                //deduc.
            }
            dias = Integer.parseInt(data[35]);
            if (dias != 0) {
                insertCell(table, "Baja 75%", Element.ALIGN_LEFT, 1, bf12);
                insertCell(table, data[35] + "días", Element.ALIGN_RIGHT, 1, bf12);                         //cant
                insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                        //imp unit
                insertCell(table, "-" + df.format(dias75), Element.ALIGN_RIGHT, 1, bf12);                            //dev
                insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                        //deduc.
            }
            insertCell(table, "-----------------------------------------------------"
                    + "--------------------------------------------------", Element.ALIGN_CENTER, 5, bfBold12);
            
            insertCell(table, "Total Deducciones", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                                //cant
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                                //imp unit
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                                //dev
            insertCell(table, df.format(deducc[0]+deducc[1]+deducc[2]+deducc[3]), Element.ALIGN_RIGHT, 1, bf12);                //deduc.
            
            insertCell(table, "Total Devengos", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                                //cant
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                                //imp unit
            insertCell(table, df.format(Float.parseFloat(data[15])), Element.ALIGN_RIGHT, 1, bf12);                             //dev
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);                                                                //deduc.
            
            insertCell(table, "-----------------------------------------------------"
                    + "--------------------------------------------------", Element.ALIGN_CENTER, 5, bfBold12);

            Font bfUnderline12 = new Font(Font.FontFamily.HELVETICA, 12, Font.UNDERLINE);
            
            insertCell(table, "", Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Liquido a Percibir", Element.ALIGN_RIGHT, 2, bfUnderline12);
            insertCell(table, df.format(Float.parseFloat(data[22])), Element.ALIGN_RIGHT, 1, bf12);
            
            insertCell(table, "", Element.ALIGN_CENTER, 5, bfBold12);
            
            insertCell(table, "-----------------------------------------------------"
                    + "--------------------------------------------------", Element.ALIGN_CENTER, 5, bfBold12);
            
            insertCell(table, "A ingresar en cuenta:", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, data[26], Element.ALIGN_RIGHT, 3, bf12);
            insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);

            paragraph.add(table);
            document.add(paragraph);
            
            document.add(new Paragraph("\n\n"));
            
            float[] columnWidths2 = {14f, 3f};
            table = new PdfPTable(columnWidths2);
            paragraph = new Paragraph();
            
            bf12.setColor(BaseColor.GRAY);
            
            insertCell(table, "EMPRESARIO", Element.ALIGN_LEFT, 2, bf12);
            table.setHeaderRows(1);
            insertCell(table, "-----------------------------------------------------"
                    + "--------------------------------------------------", Element.ALIGN_CENTER, 5, bf12);
            
            insertCell(table, "Contingencias comunes empresario", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, df.format(Float.parseFloat(data[28])), Element.ALIGN_RIGHT, 1, bf12);
            insertCell(table, "Desempleo empresario", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, df.format(Float.parseFloat(data[30])), Element.ALIGN_RIGHT, 1, bf12);
            insertCell(table, "Formacion empresario", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, df.format(Float.parseFloat(data[31])), Element.ALIGN_RIGHT, 1, bf12);
            insertCell(table, "Accidentes trabajo empresario", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, df.format(Float.parseFloat(data[32])), Element.ALIGN_RIGHT, 1, bf12);
            insertCell(table, "FOGASA empresario", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, df.format(Float.parseFloat(data[29])), Element.ALIGN_RIGHT, 1, bf12);
            
            insertCell(table, "-----------------------------------------------------"
                    + "--------------------------------------------------", Element.ALIGN_CENTER, 5, bf12);
            
            insertCell(table, "Total empresario", Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, df.format(Float.parseFloat(data[28])+Float.parseFloat(data[29])+Float.parseFloat(data[30])+Float.parseFloat(data[31])+Float.parseFloat(data[32])), Element.ALIGN_RIGHT, 1, bf12);
            
            paragraph.add(table);
            document.add(paragraph);
            document.close();
        } catch(FileNotFoundException | DocumentException e){
        }
    }
    private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font){
  
        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
         //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
         //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);
 }
     
}
