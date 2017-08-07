/********************************
 *  Integrantes del Grupo:      *
 *      -Javier de Lama Bermejo  *
 *      -Patricia Tur Bañon      *
 ********************************/

package gestoria;

import java.io.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ID {

    @SuppressWarnings("ConvertToTryWithResources")
    public static void checkID() throws FileNotFoundException, IOException, InvalidFormatException {

        try {
            Sheet s1;
            Workbook wb;
            InputStream rFile = new FileInputStream(new File("./src/gestoria/PracticaIV.xlsx"));
            wb = WorkbookFactory.create(rFile);
            CreationHelper dump = wb.getCreationHelper();
            s1 = wb.getSheet("Hoja1");
            parseID(s1, wb, dump);

        } catch (IOException | InvalidFormatException | EncryptedDocumentException e) {
            System.out.println(e);
        }

    }

    private static boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static void parseID(Sheet s, Workbook wb, CreationHelper dump) throws FileNotFoundException {

        Row r;
        Cell c;
        String type;
        type = "ERROR";
        int last = s.getLastRowNum();

        for (int i = 1; i <= last; i++) {   //Recorremos por filas comprobando cada ID
            r = s.getRow(i);
            c = r.getCell(3);   //base 0
            String id = c.toString();
            if (!c.toString().isEmpty()) {
                type = iDType(id);
            }
            if (!"ERROR".equals(type)) {      //Tipo valido
                char controlDigit = validateID(id, type, c);
                if (controlDigit != '1') { //Letra incorrecta
                    c.setCellValue(dump.createRichTextString((id.substring(0, id.length() - 1) + controlDigit))); //Guardamos en memoria el valor del nuevo ID
                    try ( //Excribimos en el archivo
                            FileOutputStream wFile = new FileOutputStream("./src/gestoria/PracticaIV.xlsx")) {
                        wb.write(wFile);
                        wFile.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }

    private static String iDType(String id) {
        String type = "ERROR";
        if (id.length() != 9) {
            return type;        //Longitud diferente
        }
        String first = Character.toString(id.charAt(0));
        String last = Character.toString(id.charAt((id.length()) - 1));

        if (!isStringInt(last)) {    //Letra al final =? string
            if (!isStringInt(first)) {   //Letra al inicio =? string
                if (first.equals("X") || first.equals("Y") || first.equals("Z")) {  //Primera letra valida
                    type = "NIE";
                } else {    //Primera letra invalida
                    type = "ERROR";
                }
            } else {
                type = "NIF";           //Letra al final pero no al inicio
            }
        }
        return type;
    }

    private static char validateID(String id, String type, Cell c) {
        if (id.isEmpty()) {   // Sin ID
            return '1';         //Lo tomamos como valido y no hacemos nada con el
        }
        if (type.equals("NIE")) {
            String first = Character.toString(id.charAt(0));
            switch (first) {
                case "X":
                    first = "0";
                    break;
                case "Y":
                    first = "1";
                    break;
                case "Z":
                    first = "2";
                    break;
            }
            id = first + id.substring(1);       //Reemplazamos en el ID
        }

        String numericID = id.substring(0, id.length() - 1);
        int ID = Integer.parseInt(numericID);

        //Calculamos la letra utilizando mod23(ID) y devolviendo la letra que le corresponde
        String charSet = "TRWAGMYFPDXBNJZSQVHLCKE";
        int mod = ID % 23;
        if (!validControlDigit(charSet.charAt(mod), id)) {
            return charSet.charAt(mod);
        } else {
            return '1'; //Digito de control valido, no hacer nada
        }
    }

    private static boolean validControlDigit(char controlDigit, String id) {
        return id.charAt((id.length()) - 1) == controlDigit;
    }
}