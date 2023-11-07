/* 
 *  ===================================================
 *   _       _   _   __        __   _____    _____
 *  | |     | | | |  \ \      / /  |_   _|  |  __ \
 *  | |     | | | |   \ \    / /     | |    | |  \ \
 *  | |     | | | |    \ \  / /      | |    | |   | |
 *  | |__   | |_| |     \ \/ /      _| |_   | |__/ /
 *  |____|  |_____|      \__/      |_____|  |_____/
 * 
 *  ====================================================  
 */

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Application {
    private static String WORKER_ADDRESS_0 = "http://localhost:8080/status";
    private static String WORKER_ADDRESS_1 = "http://localhost:8081/consultar";
    private static String WORKER_ADDRESS_2 = "http://localhost:8082/consultar";
    private static String WORKER_ADDRESS_3 = "http://localhost:8083/consultar";

    public static void main(String[] args) {
        //----------|Variables Locales|----------
        Aggregator aggregator = new Aggregator();
        List<String> results;
        Scanner in = new Scanner(System.in);

        //----------|Menu del Usuario|----------
        String menu = "\t====================|CONSULTAS DE BASE DE DATOS DISTRIBUIDA|====================\n\n"+
                      "\t1. CURPs por segundo se están generando.\n"+
                      "\t2. Registros totales en la base de datos.\n"+
                      "\t3. Registros en cada servidor.\n"+
                      "\t4. Bytes ocupa la base de datos y cada servidor.\n"+
                      "\t5. Total de varones y mujeres en la base de datos.\n"+
                      "\t6. Registros existen para una entidad.\n"+
                      "\t7. Salir.\n";
        boolean bucle = true;
        while(bucle){
            String opcion;
            System.out.println("\n"+menu);
            System.out.printf("\t -> opcion: ");
            opcion = in.nextLine();
            int i = 1,total = 0,hombre = 0,mujer = 0,indexH = 0,indexM = 0, index = 0;

            System.out.println("\t=============|"+opcion+"|=============");
            switch(opcion){
                case "1"://Curps por segundo
                        results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_0),"0");
                        
                        for (String result : results){
                            index = result.indexOf("cps=");
                            System.out.println("\tcurp/s = "+result.substring(index+4,result.length()));
                        }
                break;
                case "2"://Registros totales
                        results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3),"0,0");

                        total=0;
                        for (String result : results){
                            index = result.indexOf("r=");
                            String numero = result.substring(index+2,result.length()-1);
                            total+=Integer.parseInt(numero);
                        }
                        System.out.println("\tDatos Totales = "+total);
                        in.nextLine();
                break;
                case "3"://Registro de cada servidor
                        results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3),"0,0");

                        i = 1;
                        for (String result : results){
                            index = result.indexOf("r=");
                            String numero = result.substring(index+2,result.length()-1);

                            System.out.println("\tBase de Datos "+i+" = "+numero);
                            i++;
                        }
                        in.nextLine();
                break;
                case "4"://Byte que ocupa la bd total e individual
                        results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3),"0,0");

                        i=1;
                        total=0;

                        for (String result : results){
                            index = result.indexOf("r=");
                            String numero = result.substring(index+2,result.length()-1);
                            total+=Integer.parseInt(numero);

                            System.out.println("\tBase de Datos "+i+" = "+(Integer.parseInt(numero)*18)+" bytes");
                            i++;
                        }
                        System.out.println("\t==========================");
                        System.out.println("\tCurps Totales = "+(total*19)+" bytes");
                        in.nextLine();
                break;
                case "5"://Total de varones y mujeres
                        results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3),"1,0");
                        hombre = 0;
                        mujer = 0;
                        i = 0;

                        for (String result : results){
                            indexH = result.indexOf("Hom=");
                            indexM = result.indexOf("Muj=");

                            String numeroH = result.substring(indexH+4,result.indexOf(" |"));
                            String numeroM = result.substring(indexM+4,result.length()-1);

                            hombre+=Integer.parseInt(numeroH);
                            mujer+=Integer.parseInt(numeroM);

                            System.out.println("\tBase de Datos "+(i+1)+" -> Hombres "+numeroH+" - Mujeres "+numeroM);
                            i++;
                        }
                        System.out.println("\t==========================");
                        System.out.println("\tTotal Hombres = "+hombre+" | Total Mujeres "+mujer); //Hom=%d | Muj=%d
                break;
                case "6"://Registros para una entidad
                        System.out.printf("\t -> Entidad: ");
                        String entidad = in.nextLine();
                        String solicitud = "2,"+entidad;
                        total = 0;

                        results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3),solicitud);

                        for (String result : results){
                            index = result.indexOf("O=");
                            String numero = result.substring(index+2,result.length()-1);
                            total+=Integer.parseInt(numero);
                        }
                        System.out.println("\tTotal de Ocurrencias = "+total);
                        in.nextLine();
                break;
                case "7"://Salir del menu
                        System.out.println("\t[Saliendo]..........");
                        bucle = false;
                break;
            }
        }
    }
}