package dev.jab125.metahelper.dependencies;

import com.google.gson.JsonObject;
import dev.jab125.metahelper.Main;
import okhttp3.Request;
import okhttp3.Response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static dev.jab125.metahelper.util.Util.jsonArray;

public class RoughlyEnoughItems implements Deps {
    public static final String ROUGHLY_ENOUGH_ITEMS_URL = "https://api.modrinth.com/v2/project/rei/version";
    @Override
    public Map<String, Object> get(List<String> mcVersions) throws Throwable {
        Map<String, Object> obj = new LinkedHashMap<>();
        Map<String, String> fabric = new LinkedHashMap<>();
        Map<String, String> forge = new LinkedHashMap<>();
        Map<String, String> neoforge = new LinkedHashMap<>();
        Map<String, Map<String, String>> loaders = Map.of("fabric", fabric, "forge", forge, "neoforge", neoforge);
        Request request = new Request.Builder()
                .url(ROUGHLY_ENOUGH_ITEMS_URL)
                .build();
        try (Response response = Main.CLIENT.newCall(request).execute()) {
            List<JsonObject> array = (List<JsonObject>) (Object) jsonArray(response.body().string()).asList();
            for (String s : loaders.keySet()) {
                for (String mcVersion : mcVersions) {
                    try {
                        String dep;
                        if ("1.16.5".equals(mcVersion) && ("forge".equals(s) || "fabric".equals(s))) {
                            dep = "6.5.433";
                        } else {
                            dep = array.stream().filter(a -> a.getAsJsonArray("game_versions").asList().stream().map(b -> b.getAsString()).toList().contains(mcVersion) && a.getAsJsonArray("loaders").asList().stream().map(b -> b.getAsString()).toList().contains(s)).findFirst().orElseThrow().getAsJsonPrimitive("version_number").getAsString().split("\\+")[0];
                        }
                        loaders.get(s).put(mcVersion, "me.shedaniel:RoughlyEnoughItems-" + s + ":" + dep);
                    } catch (Throwable t) {
                        System.err.println("Failed to fetch version for " + mcVersion);
                    }
                }
            }
        }
        obj.put("fabric", fabric);
        obj.put("forge", forge);
        obj.put("neoforge", neoforge);
        return obj;
    }

    @Override
    public String id() {
        return "roughly-enough-items";
    }
}
