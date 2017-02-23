package moe.tristan.HashCode2017.util;

import lombok.Data;
import moe.tristan.HashCode2017.servers.CacheServer;
import moe.tristan.HashCode2017.servers.Video;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tristan on 23/02/2017.
 */
@Data
public class TimeSaving {
    private final int videoUid;
    private final Video video;
    private Set<CacheServer> cacheServers = new HashSet<>();
    private int timesaved;
}
