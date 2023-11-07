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
    //----------|Variables Locales|----------
    private WebClient webClient;

    //==========|Menu del Usuario|==========
    public Aggregator() {
        this.webClient = new WebClient();
    }

    //==========|sendTasksToWorkers|==========
    public List<String> sendTasksToWorkers(List<String> workersAddresses, String solicitud){
        //----------|Variables locales|----------
        List<String> results = new ArrayList();
        int tamWorkers = workersAddresses.size();   //tamanio de lista trabajadores

        //----------|Objeto Futures|----------
        CompletableFuture<String>[] futures = new CompletableFuture[tamWorkers];
   
        //----------|Envio de Solicitudes|----------
        try{
            for (int i = 0; i < tamWorkers; i++) {
                String workerAddress = workersAddresses.get(i); //Direccion de trabajador
                byte[] requestPayload = solicitud.getBytes();

                futures[i] = webClient.sendTask(workerAddress, requestPayload);
            }
        }catch (Exception e) {
            System.out.printf("Error: Enviar Socilitudes");
            e.printStackTrace();
        }

        /*
        * Evalúa continuamente si uno de los trabajadores ha terminado para enviar la siguiente
        * tarea.
        */
        try{
            boolean [] banderas = new boolean[tamWorkers];//Bandera por cada trabajador
            boolean condicion = true;//Condicion para terminar while
            for(int j = 0; j < tamWorkers;j++ )
                banderas[j] = true;

            while(condicion){
                for(int j = 0; j < tamWorkers; j++){
                    boolean isdone = futures[j].isDone();//Condicion                  
                    
                    if(true == isdone)//Trabajador no disponible
                        banderas[j] = false;
                    
                    //Verificacion de que todos los trabajadores terminaron y no quedan tareas
                    for(int k = 0; k < tamWorkers; k++){
                        if(banderas[k] == false)
                            condicion = false;
                        else{
                            condicion = true;
                            break;
                        }
                    }
                }//Fin for workers
            }//Fin while condiciony
        }catch (Exception e2) {
            System.out.printf("Error: isDone() ");
            e2.printStackTrace();
        }

        for (int i = 0; i < tamWorkers; i++)
            results.add(futures[i].join());

        return results;
    }
}