package dev.jab125.metahelper.dependencies;

import com.google.gson.JsonObject;
import dev.jab125.metahelper.Main;
import okhttp3.Request;
import okhttp3.Response;

import java.util.List;
import java.util.Map;

import static dev.jab125.metahelper.util.Util.jsonArray;

public class Legacy4J implements Deps {
    public static final String LEGACY4J_CREDITS_URL = "https://api.modrinth.com/v2/project/legacy4j/version";
    @Override
    public JsonObject get(List<String> mcVersions, JsonObject previous) throws Throwable {
        JsonObject obj = new JsonObject();
        JsonObject fabric = new JsonObject();
        JsonObject forge = new JsonObject();
        JsonObject neoforge = new JsonObject();
        JsonObject quilt = new JsonObject();
        Map<String, JsonObject> loaders = Map.of("fabric", fabric, "forge", forge, "neoforge", neoforge, "quilt", quilt);
        Request request = new Request.Builder()
                .url(LEGACY4J_CREDITS_URL)
                .build();
        try (Response response = Main.CLIENT.newCall(request).execute()) {
            List<JsonObject> array = (List<JsonObject>) (Object) jsonArray(response.body().string()).asList();
            for (String s : loaders.keySet()) {
                for (String mcVersion : mcVersions) {
                    try {
                        String dep = array.stream().filter(a -> a.getAsJsonArray("game_versions").asList().stream().map(b -> b.getAsString()).toList().contains(mcVersion) && a.getAsJsonArray("loaders").asList().stream().map(b -> b.getAsString()).toList().contains(s)).findFirst().orElseThrow().getAsJsonPrimitive("id").getAsString().split("\\+")[0];
                        String prefix = "maven.modrinth:legacy4j:";
                        loaders.get(s).addProperty(mcVersion, prefix + dep);
                    } catch (Throwable t) {
                        System.err.println("Failed to fetch version for " + mcVersion);
                    }
                }
            }
        }
        obj.add("fabric", fabric);
        obj.add("forge", forge);
        obj.add("neoforge", neoforge);
        obj.add("quilt", quilt);
        return obj;
    }

    @Override
    public String id() {
        return "legacy4j";
    }
}
