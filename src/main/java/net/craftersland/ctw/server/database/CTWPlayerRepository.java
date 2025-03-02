package net.craftersland.ctw.server.database;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CTWPlayerRepository {

    private final Map<UUID, CTWPlayer> ctwPlayers = new ConcurrentHashMap<>();

    public void save(UUID uuid, CTWPlayer ctwPlayer) {
        ctwPlayers.put(uuid, ctwPlayer);
    }

    public void remove(UUID uuid) {
        ctwPlayers.remove(uuid);
    }

    public CTWPlayer get(UUID uuid) {

        Objects.requireNonNull(uuid, "El UUID " + uuid + " no está registrado en el repositorio.");

        return ctwPlayers.get(uuid);
    }

    public Collection<CTWPlayer> get() {
        return ctwPlayers.values();
    }

    public Map<UUID, CTWPlayer> getRepository() {
        return ctwPlayers;
    }
}
