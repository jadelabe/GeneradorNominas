/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestoria;

import java.io.*;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author usuario
 */
public class Correos {


static void Creacion_cuenta_correo() throws FileNotFoundException, IOException, InvalidFormatException {

 //windows   entiede esta ruta de archivo 
String Ruta = ".\\src\\gestoria\\PracticaIV.xlsx";
//en mac-linux necesita esta ruta para el archivo 
Ruta = "./src/gestoria/PracticaIV.xlsx";

// abrimos el archivo de entrada //
InputStream archivo_origen = new  FileInputStream(new File(Ruta));

//creamos un libro de datos en memoria //
Workbook libro = WorkbookFactory.create(archivo_origen);

//asistente configuracion de volcado 
 CreationHelper volcado_datos = libro.getCreationHelper();

//cerramos el archivo //
archivo_origen.close();

// accedemos a la hoja 1 del libro de memoria //
Sheet hoja = libro.getSheet("Hoja1");   
//hoja por nombre 

//definimos un contador de datos para las filas 
int ultimo = 0;

//iterador que recorre las filas de la hoja 
Iterator filas = hoja.rowIterator();
Row fila;
Cell celda;
String contenido;

//mientras hay fila siguiente 
while(filas.hasNext())
{
    String nombre,ape1,ape2,correo,usuario,empresa;
    
    int repetido=0;
    //conocer si ya tenemos ese correo 
    
    fila = hoja.getRow(ultimo);
    //sacamos la fila X de memoria 
    
    //si es cero son los titulos de las columnas 
    if(ultimo!=0){
        
    // recojo el nombre y apellidos 
    nombre=fila.getCell(0).getStringCellValue();
    ape1=fila.getCell(1).getStringCellValue();
    ape2=fila.getCell(2).getStringCellValue();
    empresa =fila.getCell(6).getStringCellValue();
    empresa=empresa.replace(".","").replace(" ","").toLowerCase();
    
    //creamos el usuario 
    usuario=(nombre.substring(0,2)+ape1.substring(0,2)+ape2.substring(0,2)).toLowerCase();
     
     //buscamos en los anteriores si ahy una coincidencia , 
     //desde 1 hasta ultimo-1  
     for (int i=1;i<ultimo;i++)
     {
        Row fila_2 = hoja.getRow(i);
        String nombre_2=fila_2.getCell(0).getStringCellValue();
        String ape1_2=fila_2.getCell(1).getStringCellValue();
        String  ape2_2=fila_2.getCell(2).getStringCellValue();
        String empresa_2 =fila_2.getCell(6).getStringCellValue();
        empresa_2=empresa_2.replace(".","").replace(" ","").toLowerCase();
        String usuario_2=(nombre_2.substring(0,2)+ape1_2.substring(0,2)+ape2_2.substring(0,2)).toLowerCase();
        
        if(usuario.equalsIgnoreCase(usuario_2))
        {repetido++;}
   }  
     
     //si el numero es de un digito añadimos el 0 
    
     if(repetido<10)
             correo=""+usuario+"0"+repetido+"@"+empresa+".es";//tecnoproyectsl.es";
     else 
           correo=""+usuario+""+repetido+"@"+empresa+".es";//tecnoproyectsl.es";
     
     //quitamos los acentos 
    correo=correo.replace('á','a').replace('é','e').replace('í','i').replace('ó','o').replace('ú','u');
         
    //mostramos por pantalla los datos para ver que son correctos 
   // System.out.println("["+ultimo+"] "+nombre+" "+ape1+" "+ape2+" "+correo+"["+repetido+"]");
    
   
   /*modificamos archivo de memoria  */   
      
       //COLUMNA QUE QUEREMOS ESCRIBIR la fila asignada es la que estamos trabajando , ultima 
       Cell nueva = fila.createCell(15);
       
       //AÑADIMOS CONTENIDO en la memoria , cuidado que no esta en el archivo todavia 
       nueva.setCellValue(volcado_datos.createRichTextString(correo));
       
    
    }
    
    //paso a  la siguiente fila 
    Row row = (Row)(filas.next());
    
    //aumento la X de acceso a la fila 
    
    ultimo++;
       
}
        //no hay mas filas 
 
        //ABRIMOS EL ARCHIVO PARA VOLCADO 
       FileOutputStream Archivo_escritura = new FileOutputStream(Ruta);
       
        //VOLCAMOS INFORMACION 
       libro.write(Archivo_escritura);
       
       //cerramos el libro de memoria 
       libro.close();
       
       
       //CERRAMOS ARCHIVO 
       Archivo_escritura.close();   

    
}
}
