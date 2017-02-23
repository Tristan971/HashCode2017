package moe.tristan.HashCode2017.util;

import moe.tristan.HashCode2017.servers.CacheServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tristan on 23/02/2017.
 */
public class Serializer {
    @NotNull
    public static String toSolution(List<TimeSaving> orderedTimeSavings) {
        final Map<CacheServer, Set<Integer>> cacheServerVideos = new HashMap<>();

        orderedTimeSavings
                .forEach(timeSaving -> {
                    int videouid = timeSaving.getVideoUid();
                    timeSaving.getCacheServers()
                            .forEach(cacheServer -> {
                                if (cacheServer != null) {
                                    cacheServerVideos.putIfAbsent(cacheServer, new HashSet<>());
                                    cacheServerVideos.get(cacheServer).add(videouid);
                                }
                            });
                });

        final StringBuilder solutionBuilder = new StringBuilder();




        Map<Integer, Set<Integer>> cacheServerFinal = new HashMap<>();


        //region MERGE VIDEO CACHE SETS
        cacheServerVideos.keySet()
                .forEach(cacheServer -> {
                    cacheServerFinal.putIfAbsent(cacheServer.getUid(), new HashSet<>());

                    Set<Integer> oldSet = cacheServerFinal.get(cacheServer.getUid());
                    Set<Integer> newSet = cacheServerVideos.get(cacheServer);
                    newSet.addAll(oldSet);

                    cacheServerFinal.put(cacheServer.getUid(), newSet);
                });
        //endregion

        solutionBuilder.append(cacheServerFinal.keySet().size()).append("\n");
        cacheServerFinal.keySet()
                .forEach(cacheUUID -> {
                    String videos = cacheServerFinal.get(cacheUUID)
                            .stream()
                            .map(i -> Integer.toString(i))
                            .collect(Collectors.joining(" "));
                    solutionBuilder.append(cacheUUID).append(" ").append(videos).append("\n");
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
