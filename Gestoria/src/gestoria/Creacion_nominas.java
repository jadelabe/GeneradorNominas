/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestoria;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Usuario
 */
public class Creacion_nominas  {
    private static Scanner Teclado=new Scanner(System.in);
    
    //aqui se guarda la fecha de la nomina solicitada posicion 0 mes 1 anio
    private static String[] fecha_introducida={" "," "};

   
    
  
    
    static void Creacion_nominas(String mes,String annio) throws FileNotFoundException, IOException, InvalidFormatException{
        //aplicamos un patron DAO 
        Datos_origen datos= new Datos_origen("./src/gestoria/PracticaIV.xlsx");
        //Leer_fecha_nomina();
        fecha_introducida[0]=mes;
        fecha_introducida[1]=annio;
        //Por si acaso generamos una nomina nueva , sobreescribimos archivo nuevo 
        String ruta_archivo=Crear_nombre_archivo(fecha_introducida);
        FileWriter fichero = new FileWriter(ruta_archivo);
        boolean comentario=false;
        
        //Conseguimos conocer el numero de gente de la base de datos 
        int numero_trabajadores=datos.Get_num_empleados();
        float salario_base=0;
        float complementos=0;
        float bono_antiguedad =0;
        float bruto_anual=0;
      
        
        
        int[] dias_laborales={30,0,0};
        //son los dias trabajados 
        // en caso de baja laboral , 4 en segunda posicion o 16 en la tercera posicion 
        
        
        for (int i=1;i<numero_trabajadores;i++)
        {
            Trabajador persona=new Trabajador(i,datos);
            
        if(comentario) System.out.println("\nCalculando nomina de : "+fecha_introducida[0]+"-"+fecha_introducida[1]);
       if(comentario)  System.out.println("["+i+"] "+datos.Get_trabajador_nombre(i)+" "+datos.Get_trabajador_fecha_alta_mes(i)+"-"+datos.Get_trabajador_fecha_alta_annio(i));
            
         
            if (persona.trabajador_en_activo(fecha_introducida[0],fecha_introducida[1]))
            { 
                if(comentario) System.out.println("["+i+"] Calculando nomina ... ");
            
            salario_base=datos.Get_salario_base(persona.Get_categoria());
          if(comentario)   System.out.println("["+i+"] Salario anual base "+salario_base);
            
            complementos=datos.Get_complementos(persona.Get_categoria());
           if(comentario)   System.out.println("["+i+"] Complementos anual "+complementos);
            
             bono_antiguedad=Calcular_bono_antiguedad(datos,persona,fecha_introducida[0],fecha_introducida[1]);
            
           if(comentario)  System.out.println("["+i+"] Antiguedad anual "+bono_antiguedad);
            
            bruto_anual=salario_base+complementos+bono_antiguedad;
            
           if(comentario)  System.out.println("["+i+"] Bruto anual "+bruto_anual);
           
             if (comentario)System.out.println("COMPARANDO AÑOS  :"+persona.Get_anio_fecha_alta()+"<>"+fecha_introducida[1]);
             
             float bruto_auxiliar=bruto_anual; 
             
           if(persona.Get_anio_fecha_alta().equals(fecha_introducida[1]))
           {
               
             
           bruto_anual=bruto_anual/12;
           
           bruto_anual=bruto_anual*(13-Integer.parseInt(persona.Get_mes_fecha_alta()));
            if (comentario)System.out.println("BRUTO EQUIVALENTE: "+bruto_anual);
           }
            
            float tasa_irpf=datos.Get_irpf(bruto_anual);
            
            
            bruto_anual=bruto_auxiliar;
            
         if(comentario)    System.out.println("["+i+"] cuota irpf "+tasa_irpf);
            
            float prorrata=((bruto_anual/14)*2)/12;
          if(comentario)    System.out.println("["+i+"] prorrata "+prorrata);
          
          if((persona.Get_prorrata().equalsIgnoreCase("no"))) prorrata=0;
            
            float bruto_12=bruto_anual/12;
            
           if(comentario)  System.out.println("["+i+"] base impuestos "+bruto_12);
            
            float cuota_contingencias =(bruto_12/100)*datos.Get_trabajador_cuota_general();
        if(comentario)     System.out.println("["+i+"] Contingencias generales "+cuota_contingencias );
            
            float cuota_desempleo=(bruto_12/100)*datos.Get_trabajador_cuota_desempleo();
         if(comentario)   System.out.println("["+i+"] Desempleo "+cuota_desempleo);
            
            float cuota_formacion = (bruto_12/100)*datos.Get_trabajador_cuota_formacion();
           if(comentario)  System.out.println("["+i+"] formacion "+cuota_formacion);
            
            float base_irpf=0;
          if(comentario)   System.out.println("["+i+"] prorrateo "+persona.Get_prorrata());
            
            if(persona.Get_prorrata().equalsIgnoreCase("si"))
            base_irpf=bruto_12;
            else 
             base_irpf=(salario_base/14)+(complementos/14)+(bono_antiguedad/14);    
            
            float cuota_irpf=(base_irpf/100)*tasa_irpf;
           if(comentario)   System.out.println("["+i+"] irpf "+cuota_irpf +" base :"+base_irpf);
             
            float impuestos=cuota_contingencias+cuota_desempleo+cuota_formacion+cuota_irpf;
           if(comentario)  System.out.println("["+i+"] Impuestos: "+impuestos);
            
            float devengo=base_irpf-impuestos;
          if(comentario)   System.out.println("["+i+"] Liquido a percibir :"+devengo);
          
          
          Determinar_dias_nomina(persona,fecha_introducida,dias_laborales);
          
         
          float base_30=salario_base/14/30;
          float complemeto_30=complementos/14/30;
          
          float antiguedad_30=bono_antiguedad/14/30;
          
          float dia_1=(base_30+complemeto_30+antiguedad_30)/2;
           float dia_2=(base_30+complemeto_30+antiguedad_30)/4;
           float descuento_dia1=0;
           float descuento_dia2=0;
         
          if (dias_laborales[1]!=0)
          {
          descuento_dia1=dia_1*dias_laborales[1];
          }
          if(dias_laborales[2]!=0)
          {
          descuento_dia2=dia_2*dias_laborales[2];
          }
          
          devengo=devengo-descuento_dia1-descuento_dia2;
          
          
           if(comentario)   System.out.println("["+i+"] Descuento baja 50% : "+descuento_dia1);
          
          if(comentario)   System.out.println("["+i+"] Descuento baja 25% : "+descuento_dia2);
          
          
            
            
             //aportaciones de la empresa ; 
            float cuota_empresa_contingencias_comunes=(bruto_12/100)*datos.Get_empresario_contingencias_comunes();
           if(comentario)  System.out.println("["+i+"] empresa_contingencias: "+cuota_empresa_contingencias_comunes);
            float cuota_empresa_desempleo=(bruto_12/100)*datos.Get_empresario_desempleo();
           if(comentario)  System.out.println("["+i+"] cuota_empresa_desempleo: "+cuota_empresa_desempleo);
            float cuota_empresa_formacion=(bruto_12/100)*datos.Get_empresario_formacion();
           if(comentario)  System.out.println("["+i+"] cuota_empresa_formacion: "+cuota_empresa_formacion);
            float cuota_empresa_accidentes=(bruto_12/100)*datos.Get_empresario_accidentes_trabajo();
          if(comentario)   System.out.println("["+i+"] cuota_empresa_accidentes: "+cuota_empresa_accidentes);
            float cuota_empresa_fogasa=(bruto_12/100)*datos.Get_empresario_fogasa();
           if(comentario)  System.out.println("["+i+"] cuota_empresa_fogasa: "+cuota_empresa_fogasa);
            float cuota_empresa=cuota_empresa_contingencias_comunes+cuota_empresa_desempleo+cuota_empresa_formacion+cuota_empresa_accidentes+cuota_empresa_fogasa;
           if(comentario)  System.out.println("["+i+"] total empresa : "+cuota_empresa);
            
            String Nueva_Nomina=persona.Get_empresa_nombre()+";"+persona.Get_empresa_cif()+";"+persona.Get_nombre()+";"+persona.Get_apellido1()+";"+persona.Get_apellido2()+";"+persona.Get_dni()+";"+
                    persona.Get_mes_fecha_alta()+"/"+persona.Get_anio_fecha_alta()+";"+persona.Get_meses_trabajados()+";"+persona.Get_categoria()+";"+tasa_irpf+";"+persona.Get_numcontrato()+";"+
                    salario_base+";"+complementos+";"+bono_antiguedad+";"+bruto_12+";"+base_irpf+";"+datos.Get_trabajador_cuota_general()+";"+datos.Get_trabajador_cuota_desempleo()+";"+datos.Get_trabajador_cuota_formacion()+
                    ";"+prorrata+";"+cuota_irpf+";"+base_irpf+";"+devengo+";"+fecha_introducida[0]+";"+fecha_introducida[1]+";"+persona.Get_correo()+";"+persona.Get_cuenta()+";"+
                    bruto_12+";"+cuota_empresa_contingencias_comunes+";"+cuota_empresa_fogasa+";"+cuota_empresa_desempleo+";"+cuota_empresa_formacion+";"+cuota_empresa_accidentes+";"+dias_laborales[1]+";"+descuento_dia1+";"+dias_laborales[2]+";"+descuento_dia2;
            
            Guardar_en_archivo(ruta_archivo,Nueva_Nomina);
           if(comentario)  System.out.println(Nueva_Nomina);
          
            
            
            if((persona.Get_prorrata().equalsIgnoreCase("no"))
            && ((Integer.parseInt(fecha_introducida[0])==6)||(Integer.parseInt(fecha_introducida[0])==12)))
            {
            // hay paga extra 
                
                 if(comentario) System.out.println("["+i+"] salario_base_extra: "+base_irpf);
                 
                 
            if(persona.Get_anio_fecha_alta().equals(fecha_introducida[1]))
           {
               
             
           base_irpf=base_irpf/6;
           
           if(fecha_introducida[0].equals("12"))
           {
               if (Integer.parseInt(persona.Get_mes_fecha_alta())<=7)
                   base_irpf=base_irpf*6;
               else
               {
                base_irpf=base_irpf*(13-Integer.parseInt(persona.Get_mes_fecha_alta()));
               }
                   
           
           }
           else
           {
           if (persona.Get_mes_fecha_alta().equals("1"))
                   base_irpf=base_irpf*6;
               else
               {
                base_irpf=base_irpf*(7-Integer.parseInt(persona.Get_mes_fecha_alta()));
               }
           
           
           }
           
          
           
            
           }
                 
                 
                 
                 
                 
                float cuota_contingencias2=0;
                if(comentario) System.out.println("["+i+"] extra_contingencias: "+cuota_contingencias2);
                float cuota_desempleo2=0;
               if(comentario)  System.out.println("["+i+"] extra_desempleo: "+cuota_desempleo2);
                float cuota_formacion2=0;
              if(comentario)   System.out.println("["+i+"] extra_formacion: "+cuota_formacion2);
                float cuota_irpf2=cuota_irpf;
                if(comentario) System.out.println("["+i+"] extra_irpf: "+cuota_irpf2);
                float devengo_2=base_irpf-cuota_irpf2;
             if(comentario)    System.out.println("["+i+"] extra_liquido: "+devengo_2);
                
                   String Nueva_Nomina2=persona.Get_empresa_nombre()+";"+persona.Get_empresa_cif()+";"+persona.Get_nombre()+";"+persona.Get_apellido1()+";"+persona.Get_apellido2()+";"+persona.Get_dni()+";"+
                    persona.Get_mes_fecha_alta()+"/"+persona.Get_anio_fecha_alta()+";"+persona.Get_meses_trabajados()+";"+persona.Get_categoria()+";"+tasa_irpf+";"+persona.Get_numcontrato()+";"+
                    salario_base+";"+complementos+";"+bono_antiguedad+";"+bruto_12+";"+base_irpf+";"+cuota_contingencias2+";"+cuota_desempleo2+";"+cuota_formacion2+
                    ";"+0+";"+cuota_irpf+";"+base_irpf+";"+devengo_2+";"+fecha_introducida[0]+";"+fecha_introducida[1]+";"+persona.Get_correo()+";"+persona.Get_cuenta()+";"+
                    bruto_12+";"+0+";"+0+";"+0+";"+0+";"+0+";"+0+";"+0+";"+0+";"+0;
                
                 if(comentario)   System.out.println(Nueva_Nomina2);
                    Guardar_en_archivo(ruta_archivo,Nueva_Nomina2);
            
            
            }
            
           
            
            
            
            
            
            
            }
            
            
            
      
        
        
        
        }
        
        
      /*
        Aqui toca generar las nominas en pdf desde el archivo de texto generado 
        Crear_nominas_pdf(ruta_archivo);
        Las nominas dentro de la misma carpeta nominas . 
        nombres de los archivos 
        nomina normal = annio_nomina + mes_nomina + nombre + dni .pdf
        nomina extra = annio_nomina + mes_nomina + nombre + dni + extra_junio_diciembre .pdf
        
        suerte . 
        
        */  
        
    
    
    
    }
    
    private static float Calcular_bono_antiguedad(Datos_origen datos,Trabajador persona, String nomina_mes, String nomina_anio) {

        int mes_nomina=Integer.parseInt(nomina_mes);
        int anio_nomina=Integer.parseInt(nomina_anio);
        int mes_alta=Integer.parseInt(persona.Get_mes_fecha_alta());
        int anio_alta=Integer.parseInt(persona.Get_anio_fecha_alta());
        float suplemento=0;
        
         if(anio_nomina-anio_alta==0){suplemento=0;}//System.out.println("< NO TRIENIO > ");
         else if ((anio_nomina-anio_alta) %3==0)
         {//System.out.println("< CAMBIO TRIENIO > ");
          int meses_trienio_anterior=mes_alta-1;
          int  meses_trienio_nuevo=12-meses_trienio_anterior;
           int num_trienio=(anio_nomina-anio_alta)/3;
         float precio_trienio_nuevo=datos.Get_coste_trienio(num_trienio);
         float precio_trienio_anterior=datos.Get_coste_trienio(num_trienio-1);
         suplemento=(meses_trienio_anterior*precio_trienio_anterior*14)/12+(meses_trienio_nuevo*precio_trienio_nuevo*14)/12;
         // System.out.println(num_trienio+" Cobra trienio viejo meses : "+meses_trienio_anterior+" precio:"+precio_trienio_anterior+" Cobra_trienio_nuevo meses : "+meses_trienio_nuevo+" precio "+precio_trienio_nuevo+"Total:"+suplemento);

         }
         else{
          int num_trienio=(anio_nomina-anio_alta)/3;
          float precio_trienio_nuevo=datos.Get_coste_trienio(num_trienio);
           suplemento=(precio_trienio_nuevo*14);
          //  System.out.println(num_trienio+" Cobra_trienio_nuevo meses : "+14+" precio "+precio_trienio_nuevo+"Total:"+suplemento);
          
         
         }
        
        
    return suplemento;
    }

    private static void Leer_fecha_nomina() {
        
    
    do{
        
    String fecha_teclado=Teclado.nextLine();
    fecha_introducida=fecha_teclado.split("/");
    }while((fecha_introducida.length!=2)||
    ((Integer.parseInt(fecha_introducida[0])<1)||(Integer.parseInt(fecha_introducida[0])>12))||
    ((Integer.parseInt(fecha_introducida[1])<1950)) );
    
    }
    
    
     private static String Crear_nombre_archivo(String[] fecha_introducida) {
       int mes=Integer.parseInt(fecha_introducida[0]);
       String nombre_archivo="";
       
       String nombre_mes="";
       switch(mes){
    
case 1: nombre_mes="Enero"; break;
case 2:nombre_mes="Febrero";break;
case 3: nombre_mes="Marzo"; break;
case 4:nombre_mes="Abril";break;
case 5: nombre_mes="Mayo"; break;
case 6:nombre_mes="Junio";break;
case 7: nombre_mes="Julio"; break;
case 8:nombre_mes="Agosto";break;
case 9: nombre_mes="Septiembre"; break;
case 10:nombre_mes="Octubre";break;
case 11: nombre_mes="Noviembre"; break;
case 12:nombre_mes="Diciembre";break;
 
    }
       nombre_archivo="./src/gestoria/Nominas/"+nombre_mes+fecha_introducida[1]+".txt";
       return nombre_archivo;
    }

    private static void Guardar_en_archivo(String ruta_archivo, String Nueva_Nomina) {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
           fichero = new FileWriter(ruta_archivo,true);
            pw = new PrintWriter(fichero);
            pw.println(Nueva_Nomina);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
        
        
        
        
        
        
        
    }

  
    /*Metodo Determinar dias nomina 
    Parametros entrada 
    1-Trabajador que va a ser examinado 
    2-Array fecha_introducida --> posicion 0 es el año --> posicion 1 es el mes de la nomina 
    3-Array dias laborales --> 3 posiciones . 
    
    El metodo trabaja directamente sobre el array de dias laborales , determinando:
    [0]los dias de paga normal (100%) 
    [1]los dias de paga por baja al (50%) 
    [2]los dias de paga por baja al (75%)    
    */
    private static void Determinar_dias_nomina(Trabajador persona, String[] fecha_introducida, int[] dias_laborales) {
        
        int id_persona=Integer.parseInt(persona.Get_numcontrato())+1;
        
        dias_laborales[0]=30;//dias de paga normal 100%
        dias_laborales[1]=0;//dias de paga al 50%
        dias_laborales[2]=0;//dias de paga a 75% 
        
        //formato usado para las fechas 
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         String delimitadores= " ";
         //fecha que guarda la baja alta y nomina 
         Date fecha_baja_date=null,fecha_nomina_date=null,fecha_alta_date=null;
         Calendar cal_fecha_baja=null,cal_fecha_alta=null,cal_nomina=null;
         //lectura desde datos origen 
          String baja_laboral_txt = persona.Get_Fecha_baja_laboral();
          String alta_laboral_txt = persona.Get_Fecha_alta_laboral();
          int anio=0,mes=0,dia=0;
          long empieza_dia=0,acaba_dia=0,total_dia=0;
         
        
        if (!baja_laboral_txt.equalsIgnoreCase("No aplica"))    
        {
      //  System.out.println(id_persona+"-->[EXCL] fecha de baja --> "+baja_laboral_txt);
        
          try {
                       //convertimos la cadena de la baja en una fecha 
                        String[] baja_separada = baja_laboral_txt.split(delimitadores);
                          anio=Integer.parseInt(baja_separada[5]);
                         dia=Integer.parseInt(baja_separada[2]);
                         mes=devolver_mes(baja_separada[1]);
                         fecha_baja_date = sdf.parse(anio+"-"+mes+"-"+dia);
                         
                         // System.out.println(id_persona+"--> Tiene fecha de baja data --> "+fecha_baja_date);
                         
                         
                          //convertimos la cadena de la nomina en una fecha con ultimo dia de mes
                         dia=ultimoDiaMes(fecha_introducida[1],fecha_introducida[0]);
                          fecha_nomina_date=sdf.parse(fecha_introducida[1]+"-"+fecha_introducida[0]+"-"+dia);
                          
                          
                          
                         // System.out.println(id_persona+"--> Tiene fecha de nomina data --> "+fecha_nomina_date);
                          
                         //pasamos los date a calendario para hacer las cuentas  
                           cal_nomina = Calendar.getInstance();
                          cal_nomina.setTime(fecha_nomina_date);
                          

                           cal_fecha_baja = Calendar.getInstance();
                          cal_fecha_baja.setTime(fecha_baja_date);
                          
                          
                        //  System.out.println(id_persona+"-->[DATA] fecha de baja --> "+fecha_baja_date);
                       
                         // System.out.println(id_persona+"-->[CALENDAR] fecha de baja  --> "+cal_fecha_baja.getTime().getDate()+"-"+(cal_fecha_baja.getTime().getMonth()+1)+"-"+(cal_fecha_baja.getTime().getYear()+1900));
                          
                          
                        //  System.out.println(id_persona+"-->[DATA] fecha de nomina --> "+fecha_nomina_date);
                        
                          //  System.out.println(id_persona+"-->[CALENDAR] fecha de nomina  --> "+cal_nomina.getTime().getDate()+"-"+(cal_nomina.getTime().getMonth()+1)+"-"+(cal_nomina.getTime().getYear()+1900));
                           

                    } catch (ParseException ex) {
                        Logger.getLogger(Creacion_nominas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
          
          // System.out.println(id_persona+"-->[EXCL] fecha de alta --> "+alta_laboral_txt);
           
           //comprobamos si existe fecha de alta 
            if (!alta_laboral_txt.equalsIgnoreCase("No aplica"))    
            {
                    try {
                        
                        //troceamos la cadena de la fecha 
                        String[] alta_separada = alta_laboral_txt.split(delimitadores);
                         
                         anio=Integer.parseInt(alta_separada[5]);
                         dia=Integer.parseInt(alta_separada[2]);
                         mes=devolver_mes(alta_separada[1]);
                        
                         //convertimos la cadena de alta en fecha
                         fecha_alta_date = sdf.parse(anio+"-"+mes+"-"+dia);
                         
                        //  System.out.println(id_persona+"-->[DATA] fecha de alta --> "+fecha_alta_date);
                          
                 
                         //pasamos los date a calendario para hacer las cuentas 
                         cal_fecha_alta = Calendar.getInstance();
                        cal_fecha_alta.setTime(fecha_alta_date);
                        
                       // System.out.println(id_persona+"-->[CALENDAR] fecha de alta  --> "+cal_fecha_alta.getTime().getDate()+"-"+(cal_fecha_alta.getTime().getMonth()+1)+"-"+(cal_fecha_alta.getTime().getYear()+1900));
                          
                          

                        
                    } catch (ParseException ex) {
                        Logger.getLogger(Creacion_nominas.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }//fin comprobar fecha alta 
    
        
        
                    
                    

//A este punto tenemos todo en formato fecha , solo fecha_alta_date podria valer null 

//si se cumple que ( fecha_nomina despues de la fecha de baja , o mes fecha nomina es igual al mes de baja Y
//que la fecha de alta es null o la nomina es anterior a la fecha de alta o el mes de nomina coincide con el mes de alta 
//entonces hay que mirar los cambios en los dias laborales 


/*  if(fecha_nomina_date.after(fecha_baja_date)){System.out.println("Fecha Nomina despues de fecha baja  ");}
if ((fecha_nomina_date.getMonth())==(fecha_baja_date.getMonth())){System.out.println("Fecha Nomina en el mismo mes que la baja  ");}
if(fecha_alta_date==null){System.out.println("No hay Fecha Alta ");}
else 
{
if(fecha_nomina_date.before(fecha_alta_date)){System.out.println("Fecha nomina antes Fecha alta ");}
if(fecha_nomina_date.getMonth()==fecha_alta_date.getMonth()){System.out.println("Fecha nomina en el mismo mes que Fecha Alta ");}

}
*/
                    
                    
      if((fecha_nomina_date.after(fecha_baja_date)||fecha_nomina_date.equals(fecha_baja_date))
            
             
      &&((fecha_alta_date==null)||(fecha_nomina_date.before(fecha_alta_date))||(fecha_nomina_date.getMonth()==fecha_alta_date.getMonth())))
       {
       
           if(fecha_nomina_date.getMonth()==fecha_baja_date.getMonth())
            {
                 //System.out.println("\nA.Mes nomina Coincide con mes baja ");
                 
                 //si no hay fecha de alta , o la fecha es de otro mes 
                  if((fecha_alta_date==null)||(fecha_nomina_date.getMonth()!=fecha_alta_date.getMonth()))
                  {
                     // System.out.println("1.No hay alta en este mes ");
                      //System.out.println("Periodo baja actual  "+fecha_baja_date.getDate()+"-"+(fecha_baja_date.getMonth()+1) +" hasta "+fecha_nomina_date.getDate()+"-"+(fecha_nomina_date.getMonth()+1));
                      
                      empieza_dia=cal_fecha_baja.getTimeInMillis()/(1000 * 60 * 60 * 24);
                      acaba_dia=cal_nomina.getTimeInMillis()/(1000 * 60 * 60 * 24);
                      total_dia=(acaba_dia-empieza_dia);
                      total_dia++;
                    
                    
                   //  System.out.println("Total dias= "+total_dia);
                     
                     for (int i=1;i<=total_dia;i++)
                     { if (i<4)dias_laborales[1]++;
                     else if(i<21)dias_laborales[2]++;
                     }
                      
                       
           
                      
                      
                      
                  }
                  //en caso contrario la nomina es el mismo mes que el alta y el mismo mes que la baja 
                  else {
                    //  System.out.println("2.Si hay alta en este mes  ");
                     // System.out.println("Periodo desde "+fecha_baja_date +" hasta "+fecha_alta_date);
                     // System.out.println("Periodo baja actual  "+fecha_baja_date.getDate()+"/"+(fecha_baja_date.getMonth()+1) +" hasta "+fecha_alta_date.getDate()+"/"+(fecha_alta_date.getMonth()+1));
                      
                      empieza_dia=cal_fecha_baja.getTimeInMillis()/(1000 * 60 * 60 * 24);
                      acaba_dia=cal_fecha_alta.getTimeInMillis()/(1000 * 60 * 60 * 24);
                      total_dia=(acaba_dia-empieza_dia);
                     //hay que quitar un dia de el alta 
                     
                    
                    
                    // System.out.println("Total dias= "+total_dia);
                     
                     for (int i=1;i<=total_dia;i++)
                     { if (i<4)dias_laborales[1]++;
                     else if(i<21)dias_laborales[2]++;
                     }
                      
                      
                      
                      
                      
                      
                      
                      
                  }
            }
           else {
              // System.out.println("\nB.Mes nomina distinto de mes baja ");
               
               //si no hay fecha de alta , o la fecha es de otro mes 
                  if((fecha_alta_date==null)||(fecha_nomina_date.getMonth()!=fecha_alta_date.getMonth()))
                  {
                    //  System.out.println("1.No hay alta en este mes ");
                     // System.out.println("Periodo desde "+ 1 +" hasta "+fecha_nomina_date);
                      // System.out.println("Periodo baja actual  1/"+(fecha_nomina_date.getMonth()+1)+" hasta "+fecha_nomina_date.getDate()+"/"+(fecha_nomina_date.getMonth()+1));
                     
                       

                        //CUIDADO --> que hay antes??
                      
                       //guardamos el ultimo dia para no prederlo 
                      int dia_nomina_aux=fecha_nomina_date.getDate();
                      
                      //fijamos la nomina a dia uno para los calculos 
                      fecha_nomina_date.setDate(1);
                       cal_nomina.setTime(fecha_nomina_date);
                      
                       
                      empieza_dia=cal_fecha_baja.getTimeInMillis()/(1000 * 60 * 60 * 24);
                      acaba_dia=cal_nomina.getTimeInMillis()/(1000 * 60 * 60 * 24);
                    
                      //Aqui tenemos los dias ya pagados de la baja 10,40,30....
                      total_dia=(acaba_dia-empieza_dia);
                      
                      // System.out.println("Total dias ya pagados = "+total_dia);
                      
                       //si los dias ya pagados son inferior a 20 hay que ver la distribucion 
                       //que pertoca hasta llegar al ultimo dia del mes 
                      if(total_dia<20)   
                      {
                          total_dia++;
                          
                     for (long i=total_dia;i<=(total_dia+dia_nomina_aux);i++)
                     { if (i<4)dias_laborales[1]++;
                     else if(i<21)dias_laborales[2]++;
                     }
                     
                      }
                      
                      //volvemos a poner el calendario como estaba 
                       fecha_nomina_date.setDate(dia_nomina_aux);
                       cal_nomina.setTime(fecha_nomina_date);
                      
                       
                       
                       
                       
                       
                       
                       
                      
                  }
                  //en caso contrario la nomina es el mismo mes que el alta 
                  else {
                     // System.out.println("2.Si hay alta ");
                     // System.out.println("Periodo desde "+ 1 +" hasta "+fecha_alta_date);
                       //System.out.println("Periodo baja actual  1/"+(fecha_nomina_date.getMonth()+1)+" hasta "+fecha_alta_date.getDate()+"/"+(fecha_alta_date.getMonth()+1));
                  
                         //CUIDADO --> que hay antes??
                      
                       //guardamos el ultimo dia para no prederlo 
                      int dia_nomina_aux=fecha_nomina_date.getDate();
                      
                      //fijamos la nomina a dia uno para los calculos 
                      fecha_nomina_date.setDate(1);
                       cal_nomina.setTime(fecha_nomina_date);
                      
                       
                      empieza_dia=cal_fecha_baja.getTimeInMillis()/(1000 * 60 * 60 * 24);
                      acaba_dia=cal_nomina.getTimeInMillis()/(1000 * 60 * 60 * 24);
                    
                      //Aqui tenemos los dias ya pagados de la baja 10,40,30.... anteriores a este mes
                      total_dia=(acaba_dia-empieza_dia);
                      
                      
                     //  System.out.println("Total dias ya pagados = "+total_dia);
                      
                       //si los dias ya pagados son inferior a 20 hay que ver la distribucion 
                       //que pertoca hasta llegar al ultimo dia del mes 
                      if(total_dia<20)   
                      {
                          total_dia++;
                          int dia_alta=Integer.parseInt(String.valueOf(fecha_alta_date.getDate()));
                          
                          long fin_bucle=total_dia+dia_alta-2;
                          
                          
                     for (long i=total_dia;i<=fin_bucle;i++)
                     { if (i<4)dias_laborales[1]++;
                     else if(i<21)dias_laborales[2]++;
                     }
                     
                      }
                      
                      //volvemos a poner el calendario como estaba 
                       fecha_nomina_date.setDate(dia_nomina_aux);
                       cal_nomina.setTime(fecha_nomina_date);
                  }
           
           
           }
           
           
           
           
       
       
            } 
      else 
       //    System.out.println("No Aplica variacion por baja laboral  ");
        
      //Por ultimo cuadramos la distribucion de los dias 
      dias_laborales[0]=dias_laborales[0]-dias_laborales[1]-dias_laborales[2];
      
         // System.out.println("\n"+id_persona+"-->Resultado dias determinados: \n"
           //       + "Al 100% = "+dias_laborales[0]+ "\n"
             //     + "Al 50% = "+dias_laborales[1]+"\n"
               //   + "Al 75% = "+dias_laborales[2]+"\n"
                 // + "Total dias = "+(dias_laborales[0]+dias_laborales[1]+dias_laborales[2])+"\n");   
      
        
        
        }
        
        
     
    }//final determinar dias nomina 

    private static Integer devolver_mes(String mes) {
               Integer resultado=0;
       switch(mes){
    
case "Jan": 
   resultado=1;
break;
case "Feb":
    resultado=2;
break;
case "Mar": 
    resultado=3;
break;
case "Apr":
   resultado=4;
break;
case "May":
   resultado=5;
break;
case "Jun":
   resultado=6;
break;
case "Jul": 
    resultado=7;
break;
case "Aug":
   resultado=8;
break;
case "Sep": 
  resultado=9;
break;
case "Oct":
   resultado=10;
break;
case "Nov": 
   resultado=11;
break;
case "Dic":
   resultado=12;
break;
 
    }
    
    return resultado;
    
    }

       private static int ultimoDiaMes(String fecha1, String fecha2) {

int mes=Integer.parseInt(fecha2);
int anio=Integer.parseInt(fecha1);

Calendar cal=Calendar.getInstance();
cal.set(anio, mes-1, 1);
return cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

    
    
    
    
}
