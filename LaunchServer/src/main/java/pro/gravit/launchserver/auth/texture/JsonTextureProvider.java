package pro.gravit.launchserver.auth.texture;

import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pro.gravit.launcher.HTTPRequest;
import pro.gravit.launcher.Launcher;
import pro.gravit.launcher.profiles.Texture;
import pro.gravit.utils.helper.SecurityHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JsonTextureProvider extends TextureProvider {
    private static final Type MAP_TYPE = new TypeToken<Map<String, JsonTexture>>() {
    }.getType();
    private transient final Logger logger = LogManager.getLogger();
    public String url;

    @Override
    public void close() {
        //None
    }

    @Override
    public Texture getCloakTexture(UUID uuid, String username, String client) {
        logger.warn("Ineffective get cloak texture for {}", username);
        return getAssets(uuid, username, client).get("CAPE");
    }

    @Override
    public Texture getSkinTexture(UUID uuid, String username, String client) {
        logger.warn("Ineffective get skin texture for {}", username);
        return getAssets(uuid, username, client).get("SKIN");
    }

    @Override
    public Map<String, Texture> getAssets(UUID uuid, String username, String client) {
        try {
            var result = HTTPRequest.jsonRequest(null, "GET", new URL(RequestTextureProvider.getTextureURL(url, uuid, username, client)));
            Map<String, JsonTexture> map = Launcher.gsonManager.gson.fromJson(result, MAP_TYPE);
            return JsonTexture.convertMap(map);
        } catch (IOException e) {
            logger.error("JsonTextureProvider", e);
            return new HashMap<>();
        }
    }

    public record JsonTexture(String url, String hash, Map<String, String> metadata) {
        public Texture toTexture() {
            return new Texture(url, hash == null ? null : SecurityHelper.fromHex(hash), metadata);
        }

        public static Map<String, Texture> convertMap(Map<String, JsonTexture> map) {
            if (map == null) {
                return new HashMap<>();
            }
            Map<String, Texture> res = new HashMap<>();
            for(var e : map.entrySet()) {
                res.put(e.getKey(), e.getValue().toTexture());
            }
            return res;
        }
    }
}
