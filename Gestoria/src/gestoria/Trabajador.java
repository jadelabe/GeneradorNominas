/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestoria;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
/**
 *
 * @author Usuario
 */
public class Trabajador {

   
   

  private String num_conrato; 
    
  private String nombre;
  private String apellido1;
  private String apellido2;
  private String dni;
  
 
  private String mes_fecha_alta;
  private String anio_fecha_alta;

  
  private String categoria;
  
  private String empresa_nombre;
  private String empresa_cif;
  
  private String cod_cuenta;

  private String cuenta_completa;
  
 
  
  private String Fecha_baja_laboral;
  private String Fecha_alta_laboral;
  private String horas_extra_forzada;
  private String horas_extra_voluntarias;
  
  private String correo;
  
  private String meses_antiguedad;
  private String mes_nomina;
  private String annio_nomina;
   private String prorrata;
   private String salario_base;
   private String complementos;
   
  private Workbook origen;
  
 

    Trabajador(int i, Datos_origen datos) {
        
    this.num_conrato=String.valueOf(i);
    this.nombre=datos.Get_trabajador_nombre(i);
    this.apellido1=datos.Get_trabajador_apellido1(i);
    this.apellido2=datos.Get_trabajador_apellido2(i);
    this.dni=datos.Get_trabajador_dni(i);
    this.categoria=datos.Get_trabajador_categoria(i);
    
    
    this.empresa_nombre=datos.Get_trabajador_empresa(i);
    this.empresa_cif=datos.Get_trabajador_cif_empresa(i);
    this.prorrata=datos.Get_trabajador_prorrata(i);
    this.correo=datos.Get_trabajador_email(i);
    
    this.mes_fecha_alta=datos.Get_trabajador_fecha_alta_mes(i);
    this.anio_fecha_alta=datos.Get_trabajador_fecha_alta_annio(i);
    this.cod_cuenta=datos.Get_trabajador_cuenta_completa(i);
    
    
   this.Fecha_baja_laboral=datos.Get_trabajador_baja_laboral(i);
   this.Fecha_alta_laboral=datos.Get_trabajador_alta_laboral(i);
   this.horas_extra_forzada=datos.Get_trabajador_extra_forzada(i);
   this.horas_extra_voluntarias=datos.Get_trabajador_extra_voluntaria(i);
   this.prorrata=datos.Get_trabajador_prorrata(i);
       
    }

    Trabajador(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
public String Get_numcontrato(){
return this.num_conrato;
}

    String Get_nombre() {
       return this.nombre;
    }

    String Get_apellido1() {
       return this.apellido1;
    }

    String Get_apellido2() {
       return this.apellido2;
    }

    String Get_dni() {
       return this.dni;
    }

    String Get_categoria() {
       return this.categoria;
    }

    String Get_empresa_nombre() {
       return this.empresa_nombre;
    }

    String Get_empresa_cif() {
        return this.empresa_cif;
    }

    String Get_cuenta() {
        return this.cod_cuenta;
    }

    String Get_prorrata() {
      return this.prorrata;
    }

    String Get_correo() {
       return this.correo;
    }

    String Get_anio_fecha_alta() {
       return this.anio_fecha_alta;
    }

    String Get_mes_fecha_alta() {
       return this.mes_fecha_alta;
    }
    
    String  Get_meses_trabajados() {
        return this.meses_antiguedad;
    }

    void Set_antiguedad(String[] fecha_introducida) {
   
   this.mes_nomina=fecha_introducida[0];
   this.annio_nomina=fecha_introducida[1];
    
    
    int meses_pedidos=((Integer.parseInt(annio_nomina)*12)+Integer.parseInt(mes_nomina));
    
    int total_meses_alta=(Integer.parseInt(this.Get_anio_fecha_alta())*12)+Integer.parseInt(this.Get_mes_fecha_alta());
    
    int meses_trabajados=meses_pedidos-total_meses_alta;
    //final antiguedad 
    
    this.meses_antiguedad=String.valueOf(meses_trabajados);
    
   
    
        
    }

    String Get_mes_nomina() {
       return this.mes_nomina;
    }

    String Get_anio_nomina() {
       return this.annio_nomina;
    }

 Integer Get_salariobase() {

return Integer.parseInt(this.salario_base);
    }

    private static int busca_fila(Sheet sheet, String cellContent) {
    for (Row row : sheet) {
        for (Cell cell : row) {
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
                    return row.getRowNum();  
                }
            }
        }
    }               
    return 0;
}

private String Set_salario_base() {

int salario_base_categoria=0;     
Sheet hoja2 = this.origen.getSheet("Hoja2");   

int fila_categoria=busca_fila(hoja2,this.categoria) ;
Row fila = hoja2.getRow(fila_categoria);
salario_base_categoria=(int) fila.getCell(1).getNumericCellValue();

this.salario_base=String.valueOf(salario_base_categoria);
return salario_base;

}

    private String Set_complementos() {
        
int salario_complementos=0;     
Sheet hoja2 = this.origen.getSheet("Hoja2");   

int fila_categoria=busca_fila(hoja2,this.categoria) ;
Row fila = hoja2.getRow(fila_categoria);
salario_complementos=(int) fila.getCell(2).getNumericCellValue();

this.complementos=String.valueOf(salario_complementos);
        
       
        return complementos;
    }

    Integer Get_complementos() {
        return Integer.parseInt(this.complementos);
    }

    
    Integer Get_coste_trienio(Integer trienio) {
        
    int Costes_trienio[][]=new int[2][20];
        
    Sheet hoja2 = this.origen.getSheet("Hoja2");  

    Iterator filas = hoja2.rowIterator();
  
    Row fila;
      
    int registro_inicio=18;
    int registro_final=36;
    int indice=0;
    do{
 
        
    fila = hoja2.getRow(registro_inicio);
      
    double numero_trienio,coste_trienio;
 
    numero_trienio=fila.getCell(3).getNumericCellValue();
    coste_trienio=fila.getCell(4).getNumericCellValue();
    
    Costes_trienio[0][indice]=(int) numero_trienio;
    Costes_trienio[1][indice]=(int) coste_trienio;

   // System.out.println("contenido < "+Costes_trienio[0][indice]+" >"+" < "+Costes_trienio[1][indice]+" >");
    indice++;

    registro_inicio++;
    
      }while(registro_inicio<registro_final);

    
    for (int i=0;i<Costes_trienio[0].length;i++)
    {
        //System.out.println(Costes_trienio[0][i]+" "+Costes_trienio[1][i]);
        if (Costes_trienio[0][i]==trienio)
        { return Costes_trienio[1][i]; }
    }

      return 0;

    }

    boolean trabajador_en_activo(String nomina_mes, String nomina_anio) {
        
       int anio_alta_contrato,mes_alta_contrato,anio_nomina,mes_nomina;
       anio_alta_contrato=Integer.parseInt(this.anio_fecha_alta);
       mes_alta_contrato=Integer.parseInt(this.mes_fecha_alta);
       anio_nomina=Integer.parseInt(nomina_anio);
       mes_nomina=Integer.parseInt(nomina_mes);
       
       //System.out.println("["+this.num_conrato +"] años trabajados : "+(anio_nomina-anio_alta_contrato));
       //si la resta de años es negativa aun no trabaja 
        if (anio_nomina-anio_alta_contrato<0)return false;
        
        //si la resta es igual 
        else if (anio_nomina-anio_alta_contrato==0)
        {        
        //si es el mismo mes que empieza trabajo o es despues de la nomina no trabaja aun      
        if (mes_alta_contrato>=mes_nomina){ return false;}
        //si es mas pequeño ya tiene derecho a nomina 
        else return true; 
            
        }
        //si la diferencia de años es positiva esta trabajando 
        else return true;
        
    }

    String Get_Fecha_baja_laboral() {
return this.Fecha_baja_laboral;
    }
     String Get_Fecha_alta_laboral() {
return this.Fecha_alta_laboral;
    }
   

    

    
    
    
}
