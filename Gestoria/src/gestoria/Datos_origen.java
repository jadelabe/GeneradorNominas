/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestoria;

import static gestoria.Bancos.Calcular_iban;
import static gestoria.Bancos.recuperarCeldaDouble;
import static gestoria.Bancos.verificar_cuenta;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Usuario
 */
public class Datos_origen {
    
private String ruta;    
private InputStream archivo_origen;
private Workbook libro;
private Sheet hoja1,hoja2;
private Row linea;
private float trabajador_cuota_general;
private  float trabajador_cuota_desempleo;
private  float trabajador_cuota_formacion;
private  float empresario_contingencias_comunes;
private  float empresario_fogasa;
private  float empresario_desempleo;
private float empresario_formacion;
private  float empresario_accidentes_trabajo;


private int inicio_categorias=1;
private int final_categorias=15;   
private String lista_categorias[]=new String[final_categorias-inicio_categorias];
private int lista_salario_base[]=new int[final_categorias-inicio_categorias];
private int lista_complemento[]=new int[final_categorias-inicio_categorias];
private int lista_cotizacion[]=new int[final_categorias-inicio_categorias];

private int inicio_antiguedad=18;
private int final_antiguedad=36;   
private int lista_antiguedad[]=new int[final_antiguedad-inicio_antiguedad];
private int lista_coste_antiguedad[]=new int[final_antiguedad-inicio_antiguedad];

private int inicio_bruto_anual=1;
private int final_bruto_anual=50;   
private int lista_brutos[]=new int[final_bruto_anual-inicio_bruto_anual];
private float lista_retencion[]=new float[final_bruto_anual-inicio_bruto_anual];
private int num_empleados;  




    
    

public Datos_origen(String direccion_archivo) throws FileNotFoundException, IOException, InvalidFormatException{
//cojo la ruta de datos pasada por valor;
    
ruta=direccion_archivo; 
    
//windows   entiede esta ruta de archivo 
//String Ruta = ".\\src\\gestoria\\Practica2_mod.xlsx";
//en mac-linux necesita esta ruta para el archivo 
//ruta = "./src/gestoria/Practica3.xlsx";
// abrimos el archivo de entrada //
archivo_origen = new  FileInputStream(new File(ruta));
//creamos un libro de datos en memoria //
libro = WorkbookFactory.create(archivo_origen);
//cerramos el archivo //
archivo_origen.close();
// accedemos a la hoja 1 por nombre del libro de memoria //
hoja1 = libro.getSheet("Hoja1"); 
hoja2 = libro.getSheet("Hoja2"); 
    



//cuota general trabajador 
linea = hoja2.getRow(17);
this.trabajador_cuota_general= (float) linea.getCell(1).getNumericCellValue();

//cuota desempleo trabajador 
linea = hoja2.getRow(18);
this.trabajador_cuota_desempleo= (float) linea.getCell(1).getNumericCellValue();
//formacion trabajador 
linea = hoja2.getRow(19);
this.trabajador_cuota_formacion= (float) linea.getCell(1).getNumericCellValue();
//contingencias comunes empresario 
linea = hoja2.getRow(20);
this.empresario_contingencias_comunes= (float) linea.getCell(1).getNumericCellValue();
//fogasa empresario 
linea = hoja2.getRow(21);
this.empresario_fogasa= (float) linea.getCell(1).getNumericCellValue();
//desempleo empresario 
linea = hoja2.getRow(22);
this.empresario_desempleo= (float) linea.getCell(1).getNumericCellValue();

//formacion empresario 
linea = hoja2.getRow(23);
this.empresario_formacion= (float) linea.getCell(1).getNumericCellValue();

//accidentes trabajo empresario 
linea = hoja2.getRow(24);
this.empresario_accidentes_trabajo= (float) linea.getCell(1).getNumericCellValue();

//cargar bruto anual y retencion 
for (int i=inicio_bruto_anual;i<final_bruto_anual;i++)
{
linea = hoja2.getRow(i);
lista_brutos[i-inicio_bruto_anual]=(int) linea.getCell(5).getNumericCellValue();
lista_retencion[i-inicio_bruto_anual]= (float) linea.getCell(6).getNumericCellValue();
//System.out.println("["+(i-inicio_bruto_anual)+"] bruto_anual: "+lista_brutos[i-inicio_bruto_anual]+", retencion :"+lista_retencion[i-inicio_bruto_anual]);
}

//cargar antiguedad con trienio 

for (int i=inicio_antiguedad;i<final_antiguedad;i++)
{
linea = hoja2.getRow(i);
lista_antiguedad[i-inicio_antiguedad]=(int) linea.getCell(3).getNumericCellValue();
lista_coste_antiguedad[i-inicio_antiguedad]=(int) linea.getCell(4).getNumericCellValue();
//System.out.println(lista_antiguedad[i-inicio_antiguedad]+","+lista_coste_antiguedad[i-inicio_antiguedad]);
}

//cargar categorias salario base y complementos y cotizacion 
for (int i=inicio_categorias;i<final_categorias;i++)
{
linea = hoja2.getRow(i);
lista_categorias[i-inicio_categorias]=linea.getCell(0).getStringCellValue();
lista_salario_base[i-inicio_categorias]=(int) linea.getCell(1).getNumericCellValue();
lista_complemento[i-inicio_categorias]=(int) linea.getCell(2).getNumericCellValue();
lista_cotizacion[i-inicio_categorias]=(int) linea.getCell(3).getNumericCellValue();
//System.out.println(lista_categorias[i-inicio_categorias]+","+lista_salario_base[i-inicio_categorias]+","+lista_complemento[i-inicio_categorias]+","+lista_cotizacion[i-inicio_categorias]);

}


//definimos un contador de datos para las filas de personas 

int registro = 0;

Iterator filas = hoja1.rowIterator();

Row fila;
//mientras hay fila siguiente 
while(filas.hasNext())
{     
    //paso a  la siguiente fila 
    Row row = (Row)(filas.next());
    //aumento la X de acceso a la fila     
    registro++;
}
num_empleados=registro;




}
 int Get_num_empleados() {
       return this.num_empleados;
    }
    float Get_trabajador_cuota_general() {
       return this.trabajador_cuota_general;
    }
    float Get_trabajador_cuota_desempleo() {
       return this.trabajador_cuota_desempleo;
    }
    float Get_trabajador_cuota_formacion() {
       return this.trabajador_cuota_formacion;
    }
     float Get_empresario_contingencias_comunes() {
       return this.empresario_contingencias_comunes;
    }
      float Get_empresario_fogasa() {
       return this.empresario_fogasa;
    }
    float Get_empresario_desempleo() {
       return this.empresario_desempleo;
    }
    float Get_empresario_formacion() {
       return this.empresario_formacion;
    }
     float Get_empresario_accidentes_trabajo() {
       return this.empresario_accidentes_trabajo;
    }

   String Get_trabajador_nombre(int fila){
       linea = hoja1.getRow(fila);
       String nombre=linea.getCell(0).getStringCellValue();
       return nombre;
   }
    String Get_trabajador_apellido1(int fila){
       linea = hoja1.getRow(fila);
       String ape1=linea.getCell(1).getStringCellValue();
       return ape1;
   }
     String Get_trabajador_apellido2(int fila){
       linea = hoja1.getRow(fila);
       String ape2=linea.getCell(2).getStringCellValue();
       return ape2;
   }
      String Get_trabajador_dni(int fila){
       linea = hoja1.getRow(fila);
       String dni=linea.getCell(3).getStringCellValue();
       return dni;
   }
       String Get_trabajador_fecha_alta(int fila){
       linea = hoja1.getRow(fila);
       String fecha=linea.getCell(4).getDateCellValue().toString();
       return fecha;
   }
       String Get_trabajador_fecha_alta_mes(int fila){
       linea = hoja1.getRow(fila);
      return  String.valueOf(linea.getCell(4).getDateCellValue().getMonth()+1);
       
   }
       String Get_trabajador_fecha_alta_annio(int fila){
       linea = hoja1.getRow(fila);
       String fecha_alta=linea.getCell(4).getDateCellValue().toString();
       String fecha[]=fecha_alta.split(" ");
       return fecha[5];
       
   }
       String Get_trabajador_categoria(int fila){
       linea = hoja1.getRow(fila);
       String categoria=linea.getCell(5).getStringCellValue();
       return categoria;
   }
       String Get_trabajador_empresa(int fila){
       linea = hoja1.getRow(fila);
       String empresa=linea.getCell(6).getStringCellValue();
       return empresa;
   }
       String Get_trabajador_cif_empresa(int fila){
       linea = hoja1.getRow(fila);
       String cif=linea.getCell(7).getStringCellValue();
       return cif;
   }
       String Get_trabajador_cod_cuenta(int fila){
       linea = hoja1.getRow(fila);
       String cuenta=linea.getCell(8).getStringCellValue();
       return cuenta;
   }
       String Get_trabajador_iban(int fila){
       linea = hoja1.getRow(fila);
       String iban=linea.getCell(9).getStringCellValue();
       return iban;
   }
       String Get_trabajador_cuenta_completa(int fila){
       linea = hoja1.getRow(fila);
       String completa=verificar_cuenta(recuperarCeldaDouble(linea.getCell(8)));
       String cod_iban = Calcular_iban(completa,"ES");
       String cuenta_completa=cod_iban+completa;
       return cuenta_completa;
       
   }
       String Get_trabajador_prorrata(int fila){
       linea = hoja1.getRow(fila);
       String sino=linea.getCell(10).getStringCellValue();
       return sino;
   }
       String Get_trabajador_baja_laboral(int fila){
       linea = hoja1.getRow(fila);
       String fecha;
       try {fecha=linea.getCell(11).getDateCellValue().toString();} 
       catch (NullPointerException e) { 
       fecha="No aplica";
       }
       return fecha;
   }
       String Get_trabajador_alta_laboral(int fila){
       linea = hoja1.getRow(fila);
       String fecha;
       try{fecha=linea.getCell(12).getDateCellValue().toString();}
       catch (NullPointerException e) { 
       fecha="No aplica";
       }
       return fecha;
   }
       String Get_trabajador_extra_voluntaria(int fila){
       linea = hoja1.getRow(fila);
       String horas;
       try{horas=String.valueOf(linea.getCell(13).getNumericCellValue());}
       catch (NullPointerException e) { 
       horas="No aplica";
       }
       return horas;
   }
       String Get_trabajador_extra_forzada(int fila){
       linea = hoja1.getRow(fila);
       String horas;
       try{ horas=String.valueOf(linea.getCell(14).getNumericCellValue());}
       catch (NullPointerException e) { 
       horas="No aplica";
       }
       return horas;
   }
       String Get_trabajador_email(int fila){
       linea = hoja1.getRow(fila);
       String correo=linea.getCell(15).getStringCellValue();
       return correo;
   }

    int Get_salario_base(String categoria) {
  
        int sueldo_base=0;
        for (int i=0;i<lista_categorias.length;i++)
            {
                if(lista_categorias[i].equalsIgnoreCase(categoria))
                sueldo_base=lista_salario_base[i];
            }
       return sueldo_base;
    }

    int Get_complementos(String categoria) {
     int complemento=0;
        for (int i=0;i<lista_categorias.length;i++)
            {
                if(lista_categorias[i].equalsIgnoreCase(categoria))
                complemento=lista_complemento[i];
            }
       return complemento;   
    
    }

    int Get_coste_trienio(int num_trienio) {

        int coste=0;
        
for (int i=0;i<lista_antiguedad.length;i++)
{
if (lista_antiguedad[i]==num_trienio)
    coste=lista_coste_antiguedad[i];

}
return coste;
    }

    float Get_irpf(float bruto_anual) {
        
float cuota=0;


for (int i=0;i<lista_brutos.length;i++)
{
if(lista_brutos[i]>bruto_anual)
{
cuota=lista_retencion[i];
break;
}
}   

        return cuota;
}
       

       
    

}
