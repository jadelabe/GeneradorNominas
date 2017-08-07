/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestoria;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

/**
 *
 * @author usuario
 */
public class Bancos {


static void Validar_cuentas_bancarias() throws FileNotFoundException, IOException, InvalidFormatException {

//windows   entiede esta ruta de archivo 
String Ruta = ".\\src\\gestoria\\PracticaIV.xlsx";
//en mac-linux necesita esta ruta para el archivo 
Ruta = "./src/gestoria/PracticaIV.xlsx";
// abrimos el archivo de entrada //
InputStream archivo_origen = new  FileInputStream(new File(Ruta));
//creamos un libro de datos en memoria //
Workbook libro = WorkbookFactory.create(archivo_origen);
//cerramos el archivo //
archivo_origen.close();
// accedemos a la hoja 1 por nombre del libro de memoria //
Sheet hoja = libro.getSheet("Hoja1");   


//definimos un contador de datos para recorrer las filas 
int ultimo = 0;

//iterador que recorre las filas de la hoja 
Iterator filas = hoja.rowIterator();
Row fila;
Cell celda;
String contenido,cuenta_real;

//mientras hay fila siguiente a la actual
while(filas.hasNext()){ 
//sacamos la fila X de memoria 
fila = hoja.getRow(ultimo);
   
        //si es la fila cero son los titulos de las columnas 
        if(ultimo!=0){
                //leemos el contenido completo de la columna 8 de la fila actual 
                //contenido=valor + formatos + extras 
                celda=fila.getCell(8);
                //leemos el valor doble de la celda 
                contenido=recuperarCeldaDouble(celda);

                //calculamos el valor real que debe tener la cuenta 
                cuenta_real=verificar_cuenta(contenido);

                //Mostramos cuentas System.out.println("Origen: "+contenido);System.out.println("Nueva : "+cuenta_real);

                boolean cambio=false;
                //verificamos si la cuenta leida coincide con la real 
                if (cuenta_real.compareTo(contenido)!=0) cambio =true ; 
                //si no coincide , actualizamos el valor 
                if (cambio){Actualizar_cuenta_bancaria(cuenta_real,libro,fila);}
                
               String iban = Calcular_iban(cuenta_real,"ES");
               
               
               //fata guardar en el libro 
               Actualizar_iban(iban,libro,fila);
               

         }
    
//paso a  la siguiente fila 
Row row = (Row)(filas.next());

//aumento la X de acceso a la fila 
ultimo++;
       
}//fin del while filasnext   no hay mas filas 

//acabadas las modificaciones en el libro , 
//guardamos el libro en disco con la ruta indicada 

Grabar_archivo_modificado(Ruta,libro);
 
 }


    //metodo que recupera el valor de una celdanumerica  y lo devuelve en String 

   public static String recuperarCeldaDouble(Cell cell){
        
                double ncuentadouble = cell.getNumericCellValue();
                NumberToTextConverter n = null ; 
                String ncuentastring = n.toText(ncuentadouble);    
                return ncuentastring;
        }
   
   
    public static String verificar_cuenta(String cuenta_corriente){
        //declaramos variables necesarias 
        String cuenta="",cc="",sucursal="",entidad="";

        //calculamos el tamaño de la cadena 
        int tamanio=cuenta_corriente.length();
        
        //troceamos el dato de entrada 
        //verificamos la cuenta ultimos 10 caracteres 
        cuenta=cuenta_corriente.substring(tamanio-10,tamanio);
        
        //quitamos los 10 ultimos caracteres ya leidos 
        tamanio=tamanio-10;
        //leemos la el codigo de control ultimos 2 
        cc=cuenta_corriente.substring(tamanio-2,tamanio);
        //quitamos los ultimos 2 
        tamanio=tamanio-2;
        //leemos la sucursal de los ultimos 4 
        sucursal=cuenta_corriente.substring(tamanio-4,tamanio);
        //quitamos los ultimos 4 
        tamanio=tamanio-4;
        //el resto de numeros que quede son la entidad 
        entidad=cuenta_corriente.substring(0,tamanio);

        //puede ser que la entidad no tenga 4 numeros 
        //rrellenamos los numeros que la completen 
        while (entidad.length()!=4){ entidad="0"+entidad;}

        //la parte de informacion de la cuenta es la compuesta por 
        //la entidad y la sucursal a la que pertenece 
        //para que sean 10 numeros , ampliamos 2 ceros 
        String parte_info="0"+"0"+entidad+sucursal;

        //ponderacion por cada posicion 
        int pondera[]={1,2,4,8,5,10,9,7,3,6};

        //variables de resultados en sus pesos 
        int resultado_cuenta[]=new int[10];
        int resultado_info[]=new int[10];


        int ncuenta;
        int ninfo;
        int sumatorio_ninfo=0;
        int sumatorio_ncuenta=0;

    for (int i=0;i<10;i++){

            //pasamos a entero el digito de la posicion 
            ninfo=(int)(parte_info.charAt(i))-48;     
            ncuenta =(int)(cuenta.charAt(i))-48;   

            //calculamos su ponderacion en funcion de la posicion que ocupa 
            resultado_cuenta[i]=pondera[i]*ncuenta;
            resultado_info[i]=pondera[i]*ninfo;

            //Acumulamos al sumatorio la ponderacion 
            sumatorio_ninfo+=resultado_info[i];
            sumatorio_ncuenta+=resultado_cuenta[i];

    }

    //buscamos el modulo 11 
    //dividimos entre 11 para recoger el resto 
    int resto_ncuenta=sumatorio_ncuenta%11;
    int resto_ninfo=sumatorio_ninfo%11;

    //a 11 le quitamos el resto anterior
    int c1=11-resto_ninfo;
    int c2=11-resto_ncuenta;

    //comprobacion de unos resultados concretos 0 y 1 
    if (c1==11)c1=0;
    else if (c1==10)c1=1;
    if (c2==11)c2=0;
    else if (c2==10)c2=1;

    //componemos el codigo control que debe ser asignado 
    String cc2=""+String.valueOf(c1)+""+String.valueOf(c2);

    //recomponemos la nueva cuenta corriente , segun la verificacion 
    String nuevo_cc=entidad+sucursal+cc2+cuenta;

    //devolvemos la cuenta comprobada y actualizada si fuera preciso 
    return nuevo_cc;
 }

    
    //metodo que actualiza el valor de una celda en el libro de memoria 
    public static void Actualizar_cuenta_bancaria(String cuenta_real, Workbook libro, Row fila) {
        //necesitamos guardar con un formato concreto 
        //para que no lo ponga como exponencial 
        
        CellStyle cellStyle = libro.createCellStyle();
        DataFormat hssfDataFormat = libro.createDataFormat();
        
        //asignamos formato de numero largo sin decimales 
        cellStyle.setDataFormat(hssfDataFormat.getFormat("##"));
        
        //asignamos un color para ver los que cambian 
       // cellStyle.setFillBackgroundColor(IndexedColors.CORAL.getIndex());
       // cellStyle.setFillPattern(CellStyle.ALIGN_FILL); 
        
        //parseamos la cadena de la cuenta que toca poner en la celda 
        Double numero_nuevo_ccc=(Double.parseDouble(cuenta_real));
        
        //creamos la nueva celda 
        Cell nueva = fila.createCell(8);
        
        //asignamos estilo 
        nueva.setCellStyle(cellStyle);
        
        //asignamos el tipo de celda 
        nueva.setCellType(Cell.CELL_TYPE_NUMERIC);
        
        //asignamos dato 
        nueva.setCellValue(new Double(cuenta_real));

   }

    
    //Metodo que vuelca los datos del libro de memoria en el archivo para guardar los datos modificados 
    
    public static void Grabar_archivo_modificado(String Ruta, Workbook libro) throws FileNotFoundException, IOException {
        //ABRIMOS EL ARCHIVO PARA VOLCADO 
        FileOutputStream Archivo_escritura = new FileOutputStream(Ruta);

        //VOLCAMOS INFORMACION 
        libro.write(Archivo_escritura);

        //cerramos el libro de memoria 
        libro.close();


        //CERRAMOS ARCHIVO 
        Archivo_escritura.close();  
    }

    public static String Calcular_iban(String cuenta_real, String cod_pais) {
        
        //la cuenta real debe tener 20 digitos 
        //ES UNA CUENTA VALIDA , incluir los ceros delante 
        while(cuenta_real.length()!=20)
            cuenta_real="0"+cuenta_real;
        
        //cuenta de prueba buena y verificada 
        //String bancaja="20771125011100118110";
        //cuenta_real=bancaja;
        
        //sustituimos el codigopais por los numeros que necesita 
        String[][] Letras_control={
        {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"},
        {"10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35"}};

        //BUSCO EL VALOR DE LA LETRA 
        String letra1=String.valueOf(cod_pais.charAt(0));
        String letra2=String.valueOf(cod_pais.charAt(1));
       
        
        int num_1=0;
        int num_2=0;
        /// cogemos el valor de ponderacion 
        for (int i=0;i<Letras_control[0].length;i++)
        { 
        //System.out.println("Comprobando "+ Letras_control[0][i] );
        if (Letras_control[0][i].compareTo(letra1)==0)
        {  
        num_1=Integer.parseInt(Letras_control[1][i]);
        //System.out.println("1.Buscando "+letra1+" Encontrado en "+ i + "Guardado "+ num_1);
        }
        
        }
        
        
        //cogemos el valor de ponderacion 
        for (int i=0;i<Letras_control[0].length;i++)
        {
        if (Letras_control[0][i].compareTo(letra2)==0)
        { num_2=Integer.parseInt(Letras_control[1][i]);
          //  System.out.println("2.Buscando "+letra2+" Encontrado en "+ i + "Guardado "+ num_2);
        }
        }
        
        //CREO LA NUEVA CUENTA con las letras y los 00 detras 
        String nuevo_iban=cuenta_real+String.valueOf(num_1)+String.valueOf(num_2)+"00";
        
        
      
         BigInteger cuenta_larga = new BigInteger(nuevo_iban);
        //  System.out.println("paso_1: CUENTA A CALCULAR "+nuevo_iban);
         
         
         //necesario biginteger para el gran valor de la cuenta 
         
         BigInteger modulo=new BigInteger("97");
         //dividimos la cuenta etre 97 
         BigInteger division=cuenta_larga.mod(modulo);
         
         //pasamos el resto a integer 
         int resto=division.intValue();
         
         //restamos a 98 para conocer el resultado que son los digitos de control 
          int resultado=98-resto;

          
       
       // System.out.println("paso_3: calculo del resultado mod 97 "+resultado );

      
      
        
       
        //ensamblamos la cuenta real 
        String cuenta_entera=cuenta_real+String.valueOf(num_1)+String.valueOf(num_2);
        
        //cuidado si el resto fuera de 1-9 necesito un 0 
        if (String.valueOf(resultado).length()==1)
            cuenta_entera=cuenta_entera+"0"+resultado;
        else 
            cuenta_entera=cuenta_entera+resultado;
            
       
        // ensamblamos la cuenta final 
        BigInteger cuenta_final=new BigInteger(cuenta_entera);
        
        //hacemos la division de comprobacion para verificar es correcto 
        BigInteger Comprobacion=cuenta_final.mod(modulo);
        
        
      //  System.out.println( String.format(cuenta_entera,"##"));
        
      //  System.out.println("Comprobacion division = 1 = "+Comprobacion );
        
       //  cuenta_entera=letra1+letra2+resultado+cuenta_real;

       //  System.out.println("Cuenta resultado = "+cuenta_entera );
   
        //preparamos la devolucion del resultado 
        
         String devolver=letra1+letra2;
         
        if (String.valueOf(resultado).length()==1)
            devolver=devolver+"0";
        
      
        devolver=devolver+resultado;
        
        
      //  System.out.println("Devolvemos:"+devolver);
        return devolver;
    }

    public static void Actualizar_iban(String iban, Workbook libro, Row fila) {
     
        
        CellStyle cellStyle = libro.createCellStyle();
     
        //asignamos un color para ver los que cambian 
       // cellStyle.setFillBackgroundColor(IndexedColors.CORAL.getIndex());
       // cellStyle.setFillPattern(CellStyle.ALIGN_FILL); 
        
        //creamos la nueva celda 
        Cell nueva = fila.createCell(9);
        
        //asignamos estilo 
        nueva.setCellStyle(cellStyle);
        
        //asignamos el tipo de celda 
        nueva.setCellType(Cell.CELL_TYPE_STRING);
        
        //asignamos dato 
        nueva.setCellValue(iban);
        
        
        
    }

   
    
}
