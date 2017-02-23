package moe.tristan.HashCode2017.users;

import moe.tristan.HashCode2017.servers.Video;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tristan on 23/02/2017.
 */
public class Requests {
    private final Map<Map<Video, Endpoint>, Integer> videoRequestsPerEndpoint = new HashMap<>();
}
