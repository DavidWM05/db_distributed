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

import networking.WebClient;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }
    
    public void sendTasksToWorkers(List<String> workersAddresses, int numeroCurps) {
        //----------|Variables locales|----------
        List<String> results = new ArrayList();
        int tamWorkers = workersAddresses.size();       //  tamanio de lista trabajadores
        int sleep,tiempoCurp = 63,tiempoTotal = 1008;

        //----------|Tiempo de sleep|----------
        sleep = tiempoTotal - tiempoCurp * numeroCurps;        

        //----------|Objeto Futures|----------
        CompletableFuture<String>[] futures = new CompletableFuture[tamWorkers];        

        /*
        *   Se envian las tareas a los servidores
        */
        while(true){
            int indice = 0;
            try{
                for (int i = 0; i < tamWorkers; i++) {
                    String workerAddress = workersAddresses.get(i); //  Direccion de trabajador
                    String curp = getCURP();                        //  Curps

                    byte[] requestPayload = curp.getBytes();
                    futures[i] = webClient.sendTask(workerAddress, requestPayload);                    

                    indice++;
                }
            }catch (Exception e) {
                System.out.printf("Error: Enviar Socilitudes");
                e.printStackTrace();
            }

            /**
             * Evalúa continuamente si uno de los trabajadores ha terminado para enviar la siguiente
             * tarea.
             */

            try{
                boolean [] banderas = new boolean[tamWorkers];  //  Bandera por cada trabajador
                boolean condicion = true;                       //  Condicion para terminar while
                for(int j = 0; j < tamWorkers;j++ )
                    banderas[j] = true;

                while(condicion){
                    for(int j = 0; j < tamWorkers; j++){
                        boolean isdone = futures[j].isDone();           //  Condicion                  
                        if (true == isdone && indice < numeroCurps){    //  Trabajador disponible | quedan tareas
                            results.add(futures[j].join());                            
                            String workerAddress = workersAddresses.get(j);
                            String curp = getCURP();                    //Curps

                            byte[] requestPayload = curp.getBytes();
                            futures[j] = webClient.sendTask(workerAddress, requestPayload);

                            indice++;   //  incrementamos a la siguiente tarea                                                              
                        } else if (false == isdone && indice < numeroCurps){    //  Trabajador no disponible | quedan tareas                     
                            continue;
                        } else if (true == isdone && indice >= numeroCurps){    //  Trabajador disponible | no quedan  tareas
                            String auxiliar = futures[j].join();
                            if(results.contains(auxiliar)){ banderas[j] = false; }
                            else{ results.add(auxiliar); banderas[j] = false; }                            
                        } else if(false == isdone && indice >= numeroCurps){    //  Trabajador no disponible | no quedan tareas
                            banderas[j] = true;
                        }
                        //  Verificacion de que todos los trabajadores terminaron y no quedan tareas
                        for(int k = 0; k < tamWorkers; k++){
                            if(banderas[k] == false) condicion = false;
                            else{ condicion = true; break; }
                        }
                    }// Fin for workers
                }// Fin while condicion
                Thread.sleep(sleep);
            }catch (Exception e2) {
                System.out.printf("Error: isDone() ");
                e2.printStackTrace();
            }
        }
    }

    //====================|Funcion Generadora|====================
    public String getCURP(){
        String Letra = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Numero = "0123456789";
        String Sexo = "HM";
        String Entidad[] = {"AS", "BC", "BS", "CC", "CS",
                            "CH", "CL", "CM", "DF", "DG",
                            "GT", "GR", "HG", "JC", "MC",
                            "MN", "MS", "NT", "NL", "OC",
                            "PL", "QT", "QR", "SP", "SL",
                            "SR", "TC", "TL", "TS", "VZ",
                            "YN", "ZS"};
        int indice;
        StringBuilder sb = new StringBuilder(18);

        for (int i = 1; i < 5; i++) {
            indice = (int) (Letra.length()* Math.random());
            sb.append(Letra.charAt(indice));        
        }
        for (int i = 5; i < 11; i++) {indice = (int) (Numero.length()* Math.random());
            sb.append(Numero.charAt(indice));        
        }

        indice = (int) (Sexo.length()* Math.random());
        sb.append(Sexo.charAt(indice));        
        sb.append(Entidad[(int)(Math.random()*32)]);

        for (int i = 14; i < 17; i++) {
            indice = (int) (Letra.length()* Math.random());
            sb.append(Letra.charAt(indice));        
        }

        for (int i = 17; i < 19; i++) {
            indice = (int) (Numero.length()* Math.random());
            sb.append(Numero.charAt(indice));        
        }

        return sb.toString();
    }
}
