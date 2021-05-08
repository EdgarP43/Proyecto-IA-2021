/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
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

    ArrayList<Lenguaje> hashLenguajes = new ArrayList<> ();
    ArrayList Lenguajes = new ArrayList();
    public String ruta = "";
    public String carpeta_salida = "";
    public FileReader fr;
    public int caracter = 0;
    public int linea = 0;
    public int columna = 0;
    public ArrayList<String> palabras;
    public String bowactual = "";
    public BigDecimal  frasesTotales = BigDecimal.valueOf(0);
    public BigDecimal  palabrasTotales = BigDecimal.valueOf(0);
    public String salida = "";
    public Hashtable<String, BigDecimal> probConjunta = new Hashtable<>();
    public Hashtable<String, BigDecimal> probLenguajes = new Hashtable<>();
   
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
    public void EntrenarArchivo(){
        String[] frase;
        String[] palabrasEnFrase;
        
        BufferedReader br = new BufferedReader(fr);
        String linea = "";
        
        try{
            while((linea = br.readLine()) != null){
                if(linea.contains("|")){
                    linea = linea.toLowerCase().trim();
                    frase = linea.split("\\|");
                    
                    palabras = new ArrayList<>();
                    palabrasEnFrase = frase[0].trim().split(" ");
                    
                    for(String palabra: palabrasEnFrase){
                        if(!palabra.equals("")){
                            palabras.add(palabra.trim());
                            palabrasTotales = palabrasTotales.add(BigDecimal.valueOf(1));
                        }
                    }
                    
                    bowactual = frase[1].trim();
                    
                    CrearBoW();
                    MeterPalabrasEnBoW();
                }
            }
        }catch (IOException e){
            JOptionPane.showMessageDialog(null, "Error al intentar abrir el archivo", "Error apertura de archivo", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EntrenarFrases(String frases){
        String[] frasesEntrenamiento;
        String[] frase;
        String[] palabrasEnFrase;
        
        frasesEntrenamiento = frases.trim().toLowerCase().split("\n");
        
        for (String linea: frasesEntrenamiento) {
            palabras = new ArrayList<>();
            
            if(linea.contains("|")){
                frase = linea.split("\\|");
                palabrasEnFrase = frase[0].trim().split(" ");

                palabrasTotales = palabrasTotales.add(BigDecimal.valueOf(palabrasEnFrase.length));
                bowactual = frase[1].trim();

                for (String palabra: palabrasEnFrase) {
                    if(!palabra.equals("")){
                        palabras.add(palabra);
                    }
                }

                CrearBoW();
                MeterPalabrasEnBoW();
            }
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
        frasesTotales = frasesTotales.add(BigDecimal.valueOf(1));
        
        //Evalua si el lenguaje ya está agregado
        if (Lenguajes.contains(bowactual))
        {   int n  = 0;
            //Se posiciona en el lenguaje adecuado
            for (int i = 0; i < hashLenguajes.size(); i++) 
            {
                if(hashLenguajes.get(i).nombre.equals(bowactual))
                {
                   n = i;
                   break;
                }
            }        
            //Denominador lleva el conteo de frases
            hashLenguajes.get(n).Denominador = hashLenguajes.get(n).Denominador+1;
            //Por cada palabra se evalua si ya está agregada al bow o sino se agrega
            for (int i = 0; i < palabras.size(); i++) 
            {
                if(hashLenguajes.get(n).palabras.containsKey(palabras.get(i)))
                {
                    //Si ya está agregada se aumenta su frecuencia
                    hashLenguajes.get(n).palabras.replace(palabras.get(i), hashLenguajes.get(n).palabras.get(palabras.get(i)).add(BigDecimal.valueOf(1)));
                }
                else
                {
                    //Si no está agregada se agrega al bow
                    hashLenguajes.get(n).palabras.put(palabras.get(i),BigDecimal.valueOf(1));
                }

            }
            
        }
        else
        {
            //Se agrega el lenguaje a la hash de lenguajes
            Lenguajes.add(bowactual);
            hashLenguajes.add(new Lenguaje());
            hashLenguajes.get(hashLenguajes.size()-1).nombre = bowactual;
            int n = hashLenguajes.size()-1;
            hashLenguajes.get(n).Denominador = 1;
            //Realizamos el mismo proceso de arriba solo que con la primera frase de ese idioma
            for (int i = 0; i < palabras.size(); i++) 
            {
                if(hashLenguajes.get(n).palabras.containsKey(palabras.get(i)))
                {
                    hashLenguajes.get(n).palabras.replace(palabras.get(i), hashLenguajes.get(n).palabras.get(palabras.get(i)).add(BigDecimal.valueOf(1)));
                }
                else
                {
                    hashLenguajes.get(n).palabras.put(palabras.get(i),BigDecimal.valueOf(1));
                }

            }
            
        }
        
    }
    
    int ProbaActual =0;
    // Método pendiente de evaluar
    public void GenerarInferencia(String palabraEntrada)
    {   // Veces totales que ha aparecido esa palabra en todos los datos
        BigDecimal contPalabraTotal = BigDecimal.valueOf(0);
        //Por cada lenguaje evalua si la palabra pertenece, si pertenece se suma su frecuencia
        for (int i = 0; i < hashLenguajes.size(); i++) 
        {
            if(hashLenguajes.get(i).palabras.containsKey(palabraEntrada))
            {
                contPalabraTotal = contPalabraTotal.add(hashLenguajes.get(i).palabras.get(palabraEntrada));
            }
        }
        for (int i = 0; i < hashLenguajes.size(); i++) 
        {
            // Cuantas veces ha aparecido el lenguaje
            int contLenguaje = hashLenguajes.get(i).Denominador;
            //Evalua si esa palabra pertenece al lenguaje actual
            if(hashLenguajes.get(i).palabras.containsKey(palabraEntrada))
            {
                // esta metodo es la ecuarcion de bayes la que dice (p1|p2) = ((p2|p1) * p1)/p2)
                //Probabilidad de palabra dado lenguaje: (p2 | p1)
                //Conteo de la palabra en ese lenguaje
                BigDecimal P1 = (hashLenguajes.get(i).palabras.get(palabraEntrada));
                //Se divide dentro del conteo de lenguaje
                P1 = P1.divide(BigDecimal.valueOf(contLenguaje), 100, RoundingMode.HALF_UP);
                //Veces en que ha aparecido el lenguaje dividido las frases totales (P1)
                BigDecimal P2 = BigDecimal.valueOf(contLenguaje).divide(frasesTotales ,100, RoundingMode.HALF_UP);
                //Multiplica los dos terminos del numerador
                BigDecimal Total = (P1.multiply(P2));
                //Lo divide dentro de la probabilidad de la palabra (conteo de la palabra dividido frases totales)
                Total =   Total.divide(contPalabraTotal.divide(palabrasTotales,100, RoundingMode.HALF_UP),100, RoundingMode.HALF_UP);
                
                if (probConjunta.isEmpty()) 
                {
                    probConjunta.put(hashLenguajes.get(i).nombre, Total);
                }
                else
                {
                   
                    if(probConjunta.containsKey(hashLenguajes.get(i).nombre))
                    {
                        //Se multiplican las probabilidades conjuntas
                       probConjunta.replace(hashLenguajes.get(i).nombre, probConjunta.get(hashLenguajes.get(i).nombre).multiply(Total,MathContext.DECIMAL128));
                    }
                    else
                    {
                        probConjunta.put(hashLenguajes.get(i).nombre, Total);
                        
                    }

                    
                }
                
            }
           
        }
        ProbaActual++;        
    }
    
    ArrayList<String> Escritor = new ArrayList<>();
    public void EvaluarArchivo(String Ruta) 
    {
        try 
        {
          fr = new FileReader(Ruta);
          BufferedReader bf = new BufferedReader(fr);

          String linea;
         //Evaluamos la frase completa
          while((linea = bf.readLine()) != null)
          {
              linea = linea.trim().toLowerCase();
              
              //Separamos cada palabra de la frase
              String[] pa = linea.split(" ");
              
              for (int i = 0; i < pa.length; i++) 
              {
                  if(!pa[i].equals("")){
                      pa[i] = pa[i].toLowerCase().trim();
                      //Enviamos palabra al metodo para generar las probConjunta
                      GenerarInferencia(pa[i]);   
                  }
              }
              //Evaluar que lenguaje tiene más elementos
               ProbaActual = 0;
               BigDecimal mayor = BigDecimal.valueOf(0);
               BigDecimal Deno = BigDecimal.valueOf(0);
               Enumeration e = probConjunta.keys();
                while (e.hasMoreElements()) 
                {
                  String key = (String) e.nextElement();
                  Deno = Deno.add(probConjunta.get(key),MathContext.DECIMAL128);
                }
                e = probConjunta.keys();
                String lenguajeGuardado = "";
                BigDecimal Var1 = new BigDecimal(0);
                while (e.hasMoreElements()) 
                {
                   String key = (String) e.nextElement();
                   
                   BigDecimal Var2 = probConjunta.get(key).divide(Deno,20, RoundingMode.HALF_UP);
                   if(probLenguajes.isEmpty())
                   {
                       lenguajeGuardado = key.trim();
                       
                       probLenguajes.put(key, probConjunta.get(key).divide(Deno,20, RoundingMode.HALF_UP));
                       Var1 = probLenguajes.get(lenguajeGuardado);
                   }
                   else
                   {
                       probLenguajes.put(key, probConjunta.get(key).divide(Deno,20, RoundingMode.HALF_UP));
                   }
                }
                ProbaActual = 0;
                    
                Enumeration enume = probLenguajes.keys();
                while (enume.hasMoreElements()) 
                {
                    String llave = (String) enume.nextElement();

                    BigDecimal valor = probLenguajes.get(llave);
                    if(valor.compareTo(mayor) == 1)
                    {
                        mayor = valor;
                        lenguajeGuardado = llave;
                    }
                }
                if(!lenguajeGuardado.equals(""))
                {
                    String temp = linea + " | " + lenguajeGuardado +" con una probabilidad de: "+ mayor.toPlainString();
                    System.out.println(temp);
                    salida += "• " + temp + "\r\n \r\n";
                    Escritor.add(linea + " | "+  lenguajeGuardado);
                    probConjunta = new Hashtable<>();
                    probLenguajes = new Hashtable<>(); 
                }
                else
                {
                    String temp = linea + " | " +"No existe probabilidad , ninguna de las palabras pertenece al conjunto";
                    System.out.println(temp);
                    salida += "# " + temp + "\r\n \r\n";
                }
          }
            
        fr.close();
        
        //Escritura del archivo de salida
        File archivo = new File("Retroalimentacion.txt");
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
            archivo.createNewFile();
            bw = new BufferedWriter(new FileWriter(archivo));
            
            for (int i = 0; i < Escritor.size(); i++) 
            {
               bw.write(Escritor.get(i));
               bw.newLine();
            }
        }
        
        bw.close();
        CargarArchivo("Retroalimentacion.txt", "Entrada");
        EntrenarArchivo();
        }
        catch(Exception e) 
        {
          System.out.println("Excepcion leyendo fichero"  + e);
        }
    }
    
    public void EvaluarFrases(String frasesPrueba){
        String[] frases;
        String[] palabrasEnFrase;
        frases = frasesPrueba.trim().toLowerCase().split("\n");
        
        for (String f: frases) {
            f = f.trim();
            palabrasEnFrase = f.split(" ");
            
            for (String p: palabrasEnFrase){
                if(!p.equals("")){
                    GenerarInferencia(p);
                }
            }
            
            ProbaActual = 0;
            BigDecimal Deno = BigDecimal.valueOf(0);
            Enumeration e = probConjunta.keys();

            while (e.hasMoreElements()) 
            {
              String key = (String) e.nextElement();
              Deno = Deno.add(probConjunta.get(key),MathContext.DECIMAL128);
            }
            e = probConjunta.keys();
            String lenguajeGuardado = "";
            BigDecimal Var1 = new BigDecimal(0);

            while (e.hasMoreElements()) 
            {
               String key = (String) e.nextElement();

               BigDecimal Var2 = probConjunta.get(key).divide(Deno,20, RoundingMode.HALF_UP);
               if(probLenguajes.isEmpty())
               {
                   lenguajeGuardado = key;

                   probLenguajes.put(key, probConjunta.get(key).divide(Deno,20, RoundingMode.HALF_UP));
                   Var1 = probLenguajes.get(lenguajeGuardado);
               }
               else if(Var1.compareTo(Var2) == -1)
               {
                   probLenguajes = new Hashtable<>();
                   lenguajeGuardado = key.trim();
                   probLenguajes.put(key, probConjunta.get(key).divide(Deno,20, RoundingMode.HALF_UP));
               }

            }
            if(!lenguajeGuardado.equals(""))
            {
                String temp = f + " | " + lenguajeGuardado +" con una probabilidad de: " + ( probLenguajes.get(lenguajeGuardado));
                System.out.println(temp);
                salida += "• " + temp + "\r\n \r\n";
                Escritor.add(f + " | "+  lenguajeGuardado);
                probConjunta = new Hashtable<>();
                probLenguajes = new Hashtable<>(); 
            }
            else
            {
                String temp = f + " | " +"No existe probabilidad , ninguna de las palabras pertenece al conjunto";
                System.out.println(temp);
                salida += "# " + temp + "\r\n \r\n";
            }
        }
        
        try{
            File archivo = new File("Retroalimentacion.txt");
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
                archivo.createNewFile();
                bw = new BufferedWriter(new FileWriter(archivo));

                for (int i = 0; i < Escritor.size(); i++) 
                {
                   bw.write(Escritor.get(i));
                   bw.newLine();
                }
            }

            bw.close();
            CargarArchivo("Retroalimentacion.txt", "Entrada");
            EntrenarArchivo();
        }
        catch(Exception e){
          System.out.println("Excepcion leyendo fichero"  + e);
        }
    }
}
