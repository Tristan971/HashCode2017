package moe.tristan.HashCode2017.servers;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tristan on 23/02/2017.
 */
@Data
public class Video {
    private final int sizeMB;
    private final Set<CacheServer> cacheServers = new HashSet<>();
}
