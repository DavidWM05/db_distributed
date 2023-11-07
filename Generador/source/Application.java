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

public class Application {
    //----------|Variables de Instancia|----------
    private static final String WORKER_ADDRESS_1 = "http://localhost:8081/guardar";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8082/guardar";
    private static final String WORKER_ADDRESS_3 = "http://localhost:8083/guardar";    

    //====================|Metodo Principal|====================
    public static void main(String[] args) throws Exception{
        //----------|Variables Locales|----------
        Aggregator aggregator = new Aggregator();
        int numeroCurps = Integer.parseInt(args[0]);                //  Numero de curps
        int serverPort = 8080;                                      //  Puerto del servidor
        WebServer webServer = new WebServer(serverPort,numeroCurps);//  Servidor

        /**
         * Creamos un hilo con el servidor que respondera el
         * numero de curps que se utilizan
         */
        Thread t = new Thread(webServer);
        t.start();

        
        //----------|Envio de workers y numero de curps|----------
        aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3),numeroCurps);

        t.join(0);
    }

}