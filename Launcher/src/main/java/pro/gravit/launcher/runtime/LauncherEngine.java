package pro.gravit.launcher;

import pro.gravit.launcher.client.ClientLauncherProcess;
import pro.gravit.utils.helper.LogHelper;

import java.util.concurrent.atomic.AtomicBoolean;

public class LauncherEngine {
    public static ClientParams clientParams;
    public static RuntimeModuleManager modulesManager;
    public final boolean clientInstance;
    private final AtomicBoolean started = new AtomicBoolean(false);
    public HtmlRuntimeProvider runtimeProvider;

    // Конструктор
    public LauncherEngine(boolean clientInstance) {
        this.clientInstance = clientInstance;
    }

    // Основной метод запуска
    public void start(String[] args) throws Exception {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Engine already started");
        }

        LogHelper.info("Starting LauncherEngine...");
        initModules();
        runtimeProvider = new HtmlRuntimeProvider();
        runtimeProvider.init();
        runtimeProvider.startWebServer();
        processArgs(args);
    }

    // Инициализация модулей
    private void initModules() {
        modulesManager = new RuntimeModuleManager();
        modulesManager.loadModules();
    }

    // Обработка аргументов и запуск
    private void processArgs(String[] args) throws Exception {
        if (clientInstance) {
            ClientLauncherProcess.launch(clientParams);
        } else {
            runtimeProvider.open();
        }
    }

    // Остановка движка
    public void stop() throws Exception {
        if (runtimeProvider != null) {
            runtimeProvider.stop();
        }
    }

    // Точка входа
    public static void main(String[] args) throws Exception {
        LauncherEngine engine = new LauncherEngine(true);
        try {
            engine.start(args);
        } catch (Exception e) {
            LogHelper.error("LauncherEngine failed: " + e.getMessage());
            throw e;
        }
    }
}
