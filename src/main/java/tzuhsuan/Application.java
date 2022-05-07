package tzuhsuan;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tzuhsuan.http.EndpointHandler;
import tzuhsuan.http.annotation.Controller;
import tzuhsuan.http.annotation.RequestMapping;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class.
 * Using @com.sun.net.httpserver.HttpServer to create an API server.
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        HttpServerProvider httpServerProvider = HttpServerProvider.provider();
        final HttpServer httpServer = httpServerProvider.createHttpServer(new InetSocketAddress(8080), 0);
        ExecutorService executorService = new ThreadPoolExecutor(16, 128, 5000L, TimeUnit.MILLISECONDS, new LinkedTransferQueue<>());
        httpServer.setExecutor(executorService);

        // Scan the APIs in each @Controller and create the HTTP context.
        Reflections reflections = new Reflections("tzuhsuan");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class clazz : classes) {
            Object controller = clazz.getDeclaredConstructor().newInstance();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof RequestMapping) {
                        String endpoint = ((RequestMapping) annotation).endpoint();
                        String requestMethod = ((RequestMapping) annotation).method();
                        httpServer.createContext(endpoint, new EndpointHandler(requestMethod, controller, method));
                    }
                }
            }
        }
        httpServer.start();
        LOG.info("Starting the HTTP server.");
    }
}
