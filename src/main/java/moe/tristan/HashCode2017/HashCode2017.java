package moe.tristan.HashCode2017;

import lombok.extern.slf4j.Slf4j;
import moe.tristan.HashCode2017.servers.CacheServer;
import moe.tristan.HashCode2017.servers.Video;
import moe.tristan.HashCode2017.users.Endpoint;
import moe.tristan.HashCode2017.users.Request;
import moe.tristan.HashCode2017.util.InputFile;
import moe.tristan.HashCode2017.util.Parser;
import moe.tristan.HashCode2017.util.TimeSaving;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * HashCode !! :-)
 */
@Slf4j
@SpringBootApplication
public class HashCode2017 {

    private static ApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(HashCode2017.class);
        // End of initialization //

        Parser parser = context.getBean(Parser.class);
        InputFile kittens = Parser.parseFile("kittens.in");

        log.info("Loaded input file Kittens ! -> {}", kittens.toString());

        winHashcode(kittens);
    }

    public static void winHashcode(InputFile inputFile) {
        Map<Integer, Endpoint> endpoints = inputFile.getEndpoints();
        List<Video> videos = inputFile.getVideos();
        Set<Request> requests = inputFile.getRequests();

        final Map<Integer, TimeSaving> videoTimeSavings = new HashMap<>();

        final int[] processed = new int[] {0};

        requests.parallelStream()
                .forEach(request -> {
                    //region GET REQUEST BEST GAIN INFO
                    Video video = videos.get(request.getVideouid());
                    int multiplicity = request.getMultiplicity();

                    int latencyDatacenter = endpoints.get(request.getEndpointuid())
                            .getDataserverlatency();


                    Map<CacheServer, Integer> cacheServerLatencies =
                            endpoints.get(request.getEndpointuid())
                                    .getCacheServerLatencyMap();

                    int bestCacheServerLatency = cacheServerLatencies
                            .values()
                            .parallelStream()
                            .min(Integer::compare)
                            .orElse(Integer.MAX_VALUE);

                    CacheServer bestCacheServer = cacheServerLatencies
                            .keySet()
                            .parallelStream()
                            .filter(server -> cacheServerLatencies.get(server) == bestCacheServerLatency)
                            .findAny()
                            .orElse(null);

                    int gain = (latencyDatacenter - bestCacheServerLatency) * multiplicity;
                    //endregion

                    // Check useful
                    if (gain > 0) {
                        // Check possible
                        if (inputFile.getCachesrvsize() - bestCacheServer.getUsedMB() >= video.getSizeMB()) {
                            if (videoTimeSavings.containsKey(request.getVideouid())) {
                                TimeSaving timeSaving = videoTimeSavings.get(request.getVideouid());
                                timeSaving.getCacheServers().add(bestCacheServer);
                                int oldtimesave = timeSaving.getTimesaved();
                                timeSaving.setTimesaved(oldtimesave+gain);
                            } else {
                                TimeSaving timeSaving = new TimeSaving(request.getVideouid(), video);
                                timeSaving.getCacheServers().add(bestCacheServer);
                                timeSaving.setTimesaved(gain);
                                videoTimeSavings.put(request.getVideouid(), timeSaving);
                            }
                        }
                    }
                    if (++processed[0] % 100 == 0) {
                        log.info("Processed {} requests.", processed[0]);
                    }
                });

        log.info("Processed all {} requests!", processed[0]);

        final int[] sorted = new int[] {0};
        List<TimeSaving> timeSavings = videoTimeSavings.values()
                .parallelStream()
                .map(t -> {
                    if (++sorted[0] % 100 == 0) {
                        log.info("Sorted through {} timesaving options", sorted[0]);
                    }
                    return t;
                })
                .sorted((t1, t2) -> t1.getTimesaved() >= t2.getTimesaved() ? 1 : -1)
                .collect(Collectors.toList());


    }
}
