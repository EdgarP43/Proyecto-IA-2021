/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author USUARIO
 */
public class Archivo {

    ArrayList<Lenguaje> hashemocion = new ArrayList<Lenguaje> ();
    
    ArrayList Lenguajes = new ArrayList();
    public String ruta = "";
    public String carpeta_salida = "";
    public FileReader fr;
    public int caracter = 0;
    public int linea = 0;
    public int columna = 0;
    public ArrayList<String> palabras;
    public String bowactual = "";
    public BigDecimal  DenominadorF = BigDecimal.valueOf(0);
    public String salida = "";
    public Hashtable<String, BigDecimal> probabilidades = new Hashtable<>();
    public Hashtable<String, BigDecimal> probabilidades2 = new Hashtable<>();
   
    //Metodo para cargar el archivo
    public void CargarArchivo(String Ruta, String salida) {
        ruta = Ruta;
        carpeta_salida = salida;
        try {
            fr = new FileReader(ruta);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al intentar abrir el archivo",
                    "Error apertura de archivo", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    //Método para leer el archivo
    public void LeerArchivo() {
        LeerCaracter(fr);
        IgnorarEspacios(fr);

        while (caracter != -1) 
        {
            // Array para guardar palabras de la oracion
            palabras = new ArrayList<>();
            
            // Obtener todas las palabras antes de |
            while ((char) caracter != '|') {
                // Cada palabra se agrega al array
                palabras.add(ObtenerPalabra(fr).toLowerCase().trim());
            }
            LeerCaracter(fr);
            IgnorarEspacios(fr);
            
            // Se obtiene la palabra que esta despues del | (lenguaje)
            bowactual = ObtenerPalabra(fr);
            
            // Crear esa bag of words
            CrearBoW();
            MeterPalabrasEnBoW();
        }
    }
    
    // lee un caracterer
    public void LeerCaracter(FileReader fr) {
        try {
            caracter = fr.read();
            if ((char) caracter == '\n') {
                columna = 0;
                linea++;
            } else {
                columna++;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error de lectura",
                    "Error apertura de archivo", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // hace que el caracterer actual (caracter) sea diferente a un espacio
    public void IgnorarEspacios(FileReader fr) {
        try {
            while ((char) caracter == ' ' | (char) caracter == '\r' | (char) caracter == '\n' | (char) caracter == '\t' | caracter == 65279) {
                LeerCaracter(fr);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de lectura del fichero",
                    "Error apertura de archivo", JOptionPane.ERROR_MESSAGE);
            //System.out.println("Error de lectura del fichero");
            System.exit(1);
        }
    }
    
    // obtiene la palabra entre dos espacios
    public String ObtenerPalabra(FileReader fr) {
        String palabra = "";
        String ORIGINAL = "ÁáÉéÍíÓóÚúÑñÜü";
        String REPLACEMENT= "AaEeIiOoUuNnUu";
        try {
            while ((char) caracter != ' ' & (char) caracter != '\r' & (char) caracter != '\n' & (char) caracter != '\t'  & (char) caracter != '\uffff' & (char) caracter != '|') {
                if ((char) caracter != 44 ) 
                {   
                    if ((char) caracter != 44)
                    {
                      palabra += (char) caracter;
                      LeerCaracter(fr);
                    }else
                    {
                    LeerCaracter(fr);
                    }                    
                }else
                {
                LeerCaracter(fr);
                }

            } 
            IgnorarEspacios(fr);
            return palabra;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error de lectura",
                    "Error apertura de archivo", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return "";
        }
    }
    
    String RutaNueva;
    public void CrearBoW() {
        // no se que estructura vamos a usar para cada bow
        for (int i = 0; i < palabras.size(); i++) {
            System.out.print(palabras.get(i) + " ");
        }
        System.out.print(" → " + bowactual + "\n");
    }
    
    //Ln Ln = new Ln();
    private void MeterPalabrasEnBoW() 
    {
        DenominadorF = DenominadorF.add(BigDecimal.valueOf(1));
        
        if (Lenguajes.contains(bowactual))
        {   int n  = 0;
            for (int i = 0; i < hashemocion.size(); i++) 
            {
                if(hashemocion.get(i).nombre.equals(bowactual))
                {
                   n = i;
                   break;
                }
            }            
            hashemocion.get(n).Denominador = hashemocion.get(n).Denominador+1;
            for (int i = 0; i < palabras.size(); i++) 
            {
                if(hashemocion.get(n).palabras.containsKey(palabras.get(i)))
                {
                    hashemocion.get(n).palabras.replace(palabras.get(i), hashemocion.get(n).palabras.get(palabras.get(i)).add(BigDecimal.valueOf(1)));
                }
                else
                {
                    hashemocion.get(n).palabras.put(palabras.get(i),BigDecimal.valueOf(1));
                }

            }
            
        }
        else
        {
            Lenguajes.add(bowactual);
            hashemocion.add(new Lenguaje());
            hashemocion.get(hashemocion.size()-1).nombre = bowactual;
            int n = hashemocion.size()-1;
            hashemocion.get(n).Denominador = 1;
            for (int i = 0; i < palabras.size(); i++) 
            {
                if(hashemocion.get(n).palabras.containsKey(palabras.get(i)))
                {
                    hashemocion.get(n).palabras.replace(palabras.get(i), hashemocion.get(n).palabras.get(palabras.get(i)).add(BigDecimal.valueOf(1)));
                }
                else
                {
                    hashemocion.get(n).palabras.put(palabras.get(i),BigDecimal.valueOf(1));
                }

            }
            
        }
        
    }
    
    int ProbaActual =0;
    // Método pendiente de evaluar
    public void generarEstadistica(String PalabraEntrada)
    {   // aux suma es las veces totales que ha aparecido esa palabra en todos los datos
        BigDecimal AuxSuma = BigDecimal.valueOf(0);
        for (int i = 0; i < hashemocion.size(); i++) 
        {
            if(hashemocion.get(i).palabras.containsKey(PalabraEntrada))
            {
                AuxSuma = AuxSuma.add(hashemocion.get(i).palabras.get(PalabraEntrada));
            }
        }
        // se va sumnado 
        for (int i = 0; i < hashemocion.size(); i++) 
        {
            // este denominador lo que tiene es cuantas veces esa emocion ha aparecido
            int denominador = hashemocion.get(i).Denominador;
            if(hashemocion.get(i).palabras.containsKey(PalabraEntrada))
            {
                // esta metodo es la ecuarcion de bayes la que dice (p1/p2) = ((p2/p1) * p1)/p2)
                BigDecimal P1 = (hashemocion.get(i).palabras.get(PalabraEntrada));
                P1 = P1.divide(BigDecimal.valueOf(denominador), 100, RoundingMode.HALF_UP);
                BigDecimal P2 = BigDecimal.valueOf(denominador).divide(DenominadorF ,100, RoundingMode.HALF_UP);
                BigDecimal Total = (P1.multiply(P2));
                Total =   Total.divide(AuxSuma.divide(DenominadorF,100, RoundingMode.HALF_UP),100, RoundingMode.HALF_UP);
                
                if (probabilidades.isEmpty()) 
                {
                    probabilidades.put(hashemocion.get(i).nombre, Total);
                }
                else
                {
                   
                    if(probabilidades.containsKey(hashemocion.get(i).nombre))
                    {
                        // esto es para multiplicar si esa emocion ya tenia una probabilidad hay que multiplicarla
                       probabilidades.replace(hashemocion.get(i).nombre, probabilidades.get(hashemocion.get(i).nombre).multiply(Total,MathContext.DECIMAL128));

                    }
                    else
                    {
                        probabilidades.put(hashemocion.get(i).nombre, Total);
                        
                    }

                    
                }
                
            }
           
        }
        ProbaActual++;        
    }
    
    ArrayList<String> Escritor = new ArrayList<>();
    public void lectura(String Ruta) 
    {
        String ORIGINAL = "ÁáÉéÍíÓóÚúÑñÜü";
        String REPLACEMENT= "AaEeIiOoUuNnUu";
    
        try 
        {
          
          FileInputStream fis = new FileInputStream(Ruta);  
          InputStreamReader is = new InputStreamReader(fis,"ISO-8859-1");
          BufferedReader bf = new BufferedReader(is);

//          FileReader fr = new FileReader(Ruta);
//          BufferedReader br = new BufferedReader(fr);

          String linea;
          
          
          
          while((linea = bf.readLine()) != null)
          {
                linea = linea.toLowerCase();
                linea = linea.replace(",", "");
                linea = linea.replace(".", "");
                linea = linea.replace(";", "");
                linea = linea.replace(":", "");
                linea = linea.replace('á', 'a');
                linea = linea.replace('é', 'e');
                linea = linea.replace('í', 'i');
                linea = linea.replace('ó', 'o');
                linea = linea.replace('ú', 'u');
                linea = linea.replace('ñ', 'n');
                
              String[] pa = linea.split(" ");
              for (int i = 0; i < pa.length; i++) 
              {
                  pa[i] = pa[i].toLowerCase().trim();
                  generarEstadistica(pa[i]);   
              }
              // desde aqui solo es para saber que emocion es mayor con normalisacion
              ProbaActual = 0;
               BigDecimal Deno = BigDecimal.valueOf(0);
               Enumeration e = probabilidades.keys();
                while (e.hasMoreElements()) 
                {
                  String key = (String) e.nextElement();
                  Deno = Deno.add(probabilidades.get(key),MathContext.DECIMAL128);
                }
                e = probabilidades.keys();
                String emocionguardada = "";
                BigDecimal Var1 = new BigDecimal(0);
                while (e.hasMoreElements()) 
                {
                   String key = (String) e.nextElement();
                   
                   BigDecimal Var2 = probabilidades.get(key).divide(Deno,20, RoundingMode.HALF_UP);
                   if(probabilidades2.isEmpty())
                   {
                       emocionguardada = key;
                       
                       probabilidades2.put(key, probabilidades.get(key).divide(Deno,20, RoundingMode.HALF_UP));
                       Var1 = probabilidades2.get(emocionguardada);
                   }
                   else if(Var1.compareTo(Var2) == -1)
                   {
                       probabilidades2 = new Hashtable<>();
                       emocionguardada = key ;
                       probabilidades2.put(key, probabilidades.get(key).divide(Deno,20, RoundingMode.HALF_UP));
                   }

                }
                if(!emocionguardada.equals(""))
                {
                    String temp = linea + " | " + emocionguardada +" Con una probabilidad de "+( probabilidades2.get(emocionguardada));
                    System.out.println(temp);
                    salida += "• " + temp + "\r\n \r\n";
                    Escritor.add(linea + " | "+  emocionguardada);
                    probabilidades = new Hashtable<>();
                    probabilidades2 = new Hashtable<>(); 
                }
                else
                {
                    String temp = linea + " | " +"No existe probabilidad , ninguna de las palabras pertenece al conjunto";
                    System.out.println(temp);
                    salida += "• " + temp + "\r\n \r\n";
                }
          }
            
        fr.close();
        
        File archivo = new File("Salida");
        BufferedWriter bw;
        if(archivo.exists()) 
        {
             bw = new BufferedWriter(new FileWriter(archivo));
             for (int i = 0; i < Escritor.size(); i++) 
             {
                bw.write(Escritor.get(i));
                bw.newLine();
            }
        } 
        else 
        {
          bw = new BufferedWriter(new FileWriter(archivo));
             for (int i = 0; i < Escritor.size(); i++) 
             {
                bw.write(Escritor.get(i));
                bw.newLine();
             }
        }
        
        bw.close();
        CargarArchivo("Salida", "Entrada");
        LeerArchivo();
        }
        catch(Exception e) 
        {
          System.out.println("Excepcion leyendo fichero"  + e);
        }
    }
}
