package moe.tristan.HashCode2017.users;

import lombok.Data;
import moe.tristan.HashCode2017.servers.CacheServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tristan on 23/02/2017.
 */
@Data
public class Endpoint {
    private final int dataserverlatency;
    private final Map<CacheServer, Integer> cacheServerLatencyMap = new HashMap<>();
}
