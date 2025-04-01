package pro.gravit.launcher;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebRuntimeProvider {
    private Server server;
    private static final int PORT = 8080;
    private static final Path HTML_ROOT = Paths.get("runtime/html");

    // Инициализация runtime
    public void init() throws IOException {
        if (!Files.exists(HTML_ROOT)) {
            Files.createDirectories(HTML_ROOT);
            createDefaultHtml();
        }
    }

    // Запуск веб-сервера
    public void startWebServer() throws Exception {
        server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Добавляем сервлет для обработки запросов
        context.addServlet(new ServletHolder(new LauncherServlet()), "/*");

        server.start();
        LogHelper.info("Web server started at http://localhost:" + PORT);
    }

    // Открытие интерфейса в браузере
    public void open() {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + PORT));
        } catch (Exception e) {
            LogHelper.error("Failed to open browser: " + e.getMessage());
        }
    }

    // Остановка веб-сервера
    public void stop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
            LogHelper.info("Web server stopped");
        }
    }

    // Создание начального HTML-файла
    private void createDefaultHtml() throws IOException {
        String htmlContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>GravitLauncher</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; }
                    button { padding: 10px 20px; font-size: 16px; }
                </style>
            </head>
            <body>
                <h1>Welcome to GravitLauncher</h1>
                <button onclick="alert('Launch button clicked!')">Launch Minecraft</button>
                <script>
                    // Здесь можно добавить JS для взаимодействия с бэкендом
                </script>
            </body>
            </html>
            """;
        Files.write(HTML_ROOT.resolve("index.html"), htmlContent.getBytes());
        LogHelper.info("Default HTML created at " + HTML_ROOT.resolve("index.html"));
    }

    // Внутренний сервлет для обработки запросов
    private static class LauncherServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            Path filePath = HTML_ROOT.resolve("index.html");
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(IOHelper.readString(filePath));
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"POST request received\"}");
        }
    }
}
