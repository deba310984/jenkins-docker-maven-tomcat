package com.debjit.app;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Serves a small HTML page showing the greeting and which container instance
 * answered — useful for demonstrating the build/deploy pipeline end to end.
 */
@WebServlet(name = "helloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {

    private final transient GreetingService greetingService = new GreetingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String message = greetingService.greet(request.getParameter("name"));
        String host = hostname();

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().printf(
                "<!doctype html>%n"
                        + "<html><head><title>Java + Tomcat</title></head>%n"
                        + "<body style=\"font-family:sans-serif;max-width:40rem;margin:4rem auto\">%n"
                        + "<h1>%s</h1>%n"
                        + "<p>Served by container <strong>%s</strong></p>%n"
                        + "<p>Built with Maven, packaged as a WAR, deployed on Tomcat via Jenkins + Docker.</p>%n"
                        + "</body></html>%n",
                message, host);
    }

    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
