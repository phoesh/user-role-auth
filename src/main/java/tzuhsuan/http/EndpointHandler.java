package tzuhsuan.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tzuhsuan.util.JSONUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class EndpointHandler implements HttpHandler {

    private String requestMethod;
    private Object controller;
    private Method method;

    public EndpointHandler(String requestMethod, Object controller, Method method) {
        this.requestMethod = requestMethod;
        this.controller = controller;
        this.method = method;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (requestMethod.equals(exchange.getRequestMethod())) {
            Headers responseHeader = exchange.getResponseHeaders();
            responseHeader.set("Content-Type", "application/json;charset=utf-8");

            OutputStream outputStream = exchange.getResponseBody();
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                bufferedReader.lines().forEach(line -> stringBuilder.append(line));
                Method m = controller.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                Response response = (Response) m.invoke(controller, stringBuilder.toString());
                String json = JSONUtil.beanToJson(response);

                exchange.sendResponseHeaders(response.getStatusCode(), json.length());
                outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                System.out.println(e);
                exchange.sendResponseHeaders(500, 0);
            }
            outputStream.close();
        } else {
            // Return HTTP status code 404 if the request method is not used.
            exchange.sendResponseHeaders(404, 0);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.close();
        }
    }
}
