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

import java.math.BigInteger;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;

public class WebServer implements Runnable{
    //--------------------|Variables de Instancia|--------------
    private static final String STATUS_ENDPOINT = "/status";//End point status
    private int curpXsegundo;   //Curps por segundo
    private final int port;     //Puerto
    private HttpServer server;  //Servidor basico HTTP
    
    //====================|Metodo Run|====================
    public void run() {
        startServer();
        System.out.println("Servidor escuchando en el puerto " + port);
    }

    //====================|Contructor|====================
    public WebServer(int port,int curpXsegundo) {
        this.port = port;
        this.curpXsegundo = curpXsegundo;
    }

    //====================|Inicio sel servidor|====================
    public void startServer() {
        try {
            /** 
             * Crea una instancia socket TCP que se vincula a una ip llamada create en puerto port
             * '0' es el tamanio de una lista de solicitudes pendientes para mantener en cola de espera
             */            
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
        //  Contextos
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        statusContext.setHandler(this::handleStatusCheckRequest);

        //  Creacion de hilos
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    //====================|Status|====================
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        String responseMessage = "cps="+String.valueOf(curpXsegundo);
        sendResponse(responseMessage.getBytes(), exchange);
    }

    //====================|Respuesta|====================
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
}