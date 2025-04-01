package pro.gravit.launcher.runtime;

import java.nio.file.Path;

public class WebRuntimeProvider implements RuntimeProvider {
    private static final Path runtimeDir = DirBridge.getRuntimeDir();

    @Override
    public void preLoad() throws Exception {
        System.out.println("Загрузка ресурсов WEB Runtime из " + runtimeDir);
        // Здесь можно добавить загрузку ресурсов
    }

    @Override
    public void init() throws Exception {
        System.out.println("Инициализация WEB Runtime...");
        // Инициализация конфигурации
    }

    @Override
    public void start() throws Exception {
        System.out.println("WEB Runtime запущен");
        // В оригинале запускалась JavaFX-сцена, теперь это просто лог
    }
}
