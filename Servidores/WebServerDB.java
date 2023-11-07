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

public class WebServerDB {
    //----------|Variables de Instancia|----------
    private static final String GUARDAR_ENDPOINT = "/guardar";      //  End point guardar
    private static final String CONSULTAR_ENDPOINT = "/consultar";  //  End point guardar
    private final int port;     //Puerto generator
    private HttpServer server;  //Servidor basico HTTP
    private List<String> db;    //Base de Datos

    //====================|Contructor|====================
    public WebServerDB(int port) {
        //Se inicializa con el puerto ingresado
        this.port = port;
        db = new ArrayList<>();
    }

    //====================|Inicio del servidor|====================
    public void startServer() {
        try {            
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
        //----------|Contextos|----------
        HttpContext guardarContext = server.createContext(GUARDAR_ENDPOINT);
        HttpContext consultarContext = server.createContext(CONSULTAR_ENDPOINT);

        guardarContext.setHandler(this::handleGuardarRequest);
        consultarContext.setHandler(this::handleConsultarRequest);

        //----------|Creacion de hilos|----------
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    //====================|ENDPOINT Guardar|====================
    private void handleGuardarRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        //----------|Variables Locales|----------
        Headers headers = exchange.getRequestHeaders();
        boolean isDebugMode = false;

        //----------|Modo Debug|----------
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")){
            isDebugMode = true;
        }

        //----------|Proceso de Guardado|----------
        long startTime = System.nanoTime();//Inicio de tiempo para calculos

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = guardarCurps(requestBytes);

        long finishTime = System.nanoTime();//Fin de tiempo para calculos

        if (isDebugMode) {
            long time = finishTime - startTime; //Tiempo procesamiento en nanos
            long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
            long miliseconds = TimeUnit.NANOSECONDS.toMillis(time);
            miliseconds = miliseconds - seconds*1000;
            
            String debugMessage = String.format("La operación tomó %d nanosegundos = %d segundos %d milisegundos.",time, seconds, miliseconds);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }
        //----------|Respuesta|----------
        sendResponse(responseBytes, exchange);
    }

    //====================|Guardar Curps|====================|
    private byte[] guardarCurps(byte[] requestBytes){
        //----------|Variables Locales|----------        
        String bodyString = new String(requestBytes);
        
        //----------|Guardado de Curp|----------
        db.add(bodyString);
        return String.format("[Curp Guardado]").getBytes();
    }

    //====================|ENDPOINT Consultar|====================
    private void handleConsultarRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        //----------|Variables Locales|----------
        Headers headers = exchange.getRequestHeaders();

        //----------|Proceso de Guardado|----------
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = consultaCurps(requestBytes);
        
        //----------|Respuesta|----------
        sendResponse(responseBytes, exchange);
    }

    //====================|Consulta|====================|
    private byte[] consultaCurps(byte[] requestBytes){
        //----------|Variables Locales|----------
        String bodyString = new String(requestBytes);
        String[] seleccion = bodyString.split(",");
        String respuesta = "";

        //----------|Opciones|----------
        switch(seleccion[0]){
            case "0":   respuesta = "r="+String.valueOf(db.size());//Numero de registros
            break;

            case "1":   respuesta = sexoBD();//Total de Sexos
            break;

            case "2":   respuesta = entidadBD(seleccion[1]);//Entidad
            break;
        }

        return String.format("[%s]",respuesta).getBytes();
    }

    //====================|Sexos Curps|====================|
    private String sexoBD(){
        //----------|Variables Locales|----------        
        int hombre = 0,mujer = 0;
        List<String> auxiliar = db;

        //----------|Obtener Conteo|----------
        for(int i = 0; i < auxiliar.size(); i++){
            String curp = auxiliar.get(i);
            if(curp.charAt(10) == 'H')
                hombre++;
            else
                mujer++;
        }

        return String.format("Hom=%d | Muj=%d",hombre,mujer);
    }

    //====================|Entidad Curps|====================|
    private String entidadBD(String buscar){
        //----------|Variables Locales|----------        
        int entidad = 0;
        List<String> auxiliar = db;

        //----------|Obtener Conteo|----------
        for(int i = 0; i < auxiliar.size(); i++){
            String curp = auxiliar.get(i);
            if(curp.substring(11,13).equals(buscar))
                entidad++;
            else
                continue;
        }

        return String.format("O=%d",entidad);
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
    //====================|Metodo Principal|====================
    public static void main(String[] args) {
        //----------|Variables de Locales|----------
        int serverPort = 8080;  //Puerto Default del servidor
        if (args.length == 1) { //Se pasa por linea de comandos
            serverPort = Integer.parseInt(args[0]);
        }

        WebServerDB webServer = new WebServerDB(serverPort);
        //Inicializa la configuracion del servidor
        webServer.startServer();
        //Imprime puerto en el cual esta escuchando el servidor
        System.out.println("\t -> Servidor escuchando en el puerto ["+ serverPort+"]");
    }
}