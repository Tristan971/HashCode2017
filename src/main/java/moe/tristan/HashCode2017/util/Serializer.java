package moe.tristan.HashCode2017.util;

import moe.tristan.HashCode2017.servers.CacheServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tristan on 23/02/2017.
 */
public class Serializer {
    public static String toSolution(List<TimeSaving> orderedTimeSavings) {
        final Map<CacheServer, Set<Integer>> cacheServerVideos = new HashMap<>();

        orderedTimeSavings.parallelStream()
                .forEach(timeSaving -> {
                    int videouid = timeSaving.getVideoUid();
                    timeSaving.getCacheServers().parallelStream().forEach(cacheServer -> {
                        cacheServerVideos.putIfAbsent(cacheServer, new HashSet<>());
                        cacheServerVideos.get(cacheServer).add(videouid);
                    });
                });

        final StringBuilder solutionBuilder = new StringBuilder();
        solutionBuilder.append(cacheServerVideos.keySet().size()).append("\n");

        cacheServerVideos.keySet().parallelStream()
                .forEach(cacheServer -> {
                    String videos = cacheServerVideos.get(cacheServer)
                            .parallelStream()
                            .map(i -> Integer.toString(i))
                            .collect(Collectors.joining(" "));
                    solutionBuilder.append(cacheServer.getUid()).append(" ").append(videos);
                });

        return solutionBuilder.toString();
    }

    public static void writeSolution(Path path, String solution) {
        try {
            Files.write(path, solution.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
