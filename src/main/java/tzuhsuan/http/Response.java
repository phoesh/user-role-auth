package tzuhsuan.http;

import com.sun.net.httpserver.Headers;

import java.io.Serializable;
import java.net.http.HttpClient;

public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private T data;
    private String toast = "OK";
    private String redirect;
    private int statusCode = 200;
    private Headers headers;
    private HttpClient.Version version = HttpClient.Version.HTTP_1_1;

    public T getData() {
        return data;
    }

    public Response setData(T data) {
        this.data = data;
        return this;
    }

    public String getToast() {
        return toast;
    }

    public Response setToast(String toast) {
        this.toast = toast;
        return this;
    }

    public String getRedirect() {
        return redirect;
    }

    public Response setRedirect(String redirect) {
        this.redirect = redirect;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Response setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public Response setVersion(HttpClient.Version version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return "Response{" +
                "data=" + data +
                ", toast='" + toast + '\'' +
                ", redirect='" + redirect + '\'' +
                ", statusCode=" + statusCode +
                ", headers=" + headers +
                ", version=" + version +
                '}';
    }
}
