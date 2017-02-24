package moe.tristan.HashCode2017;

import lombok.extern.slf4j.Slf4j;
import moe.tristan.HashCode2017.servers.CacheServer;
import moe.tristan.HashCode2017.servers.Video;
import moe.tristan.HashCode2017.users.Endpoint;
import moe.tristan.HashCode2017.users.Request;
import moe.tristan.HashCode2017.util.InputFile;
import moe.tristan.HashCode2017.util.Parser;
import moe.tristan.HashCode2017.util.Serializer;
import moe.tristan.HashCode2017.util.TimeSaving;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                8,
                8,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4));

        log.info("Submitted Kittens!");
        executor.submit(() -> {
            InputFile kittens = Parser.parseFile("kittens.in");
            log.info("Loaded input file Kittens ! -> {}", kittens.toString());
            winHashcode(kittens, "kittens.out");
            log.info("Finished kittens!");
        });

        log.info("Submitted Zoo!");
        executor.submit(() -> {
            InputFile zoo = Parser.parseFile("me_at_the_zoo.in");
            log.info("Loaded input file me_at_the_zoo ! -> {}", zoo.toString());
            winHashcode(zoo, "me_at_the_zoo.out");
            log.info("Finished me_at_the_zoo!");
        });

        log.info("Submitted Trending today!");
        executor.submit(() -> {
            InputFile trendingToday = Parser.parseFile("trending_today.in");
            log.info("Loaded input file trending_today ! -> {}", trendingToday.toString());
            winHashcode(trendingToday, "trending_today.out");
            log.info("Finished trending_today!");
        });

        log.info("Submitted VWS!");
        executor.submit(() -> {
            InputFile videosWorth = Parser.parseFile("videos_worth_spreading.in");
            log.info("Loaded input file videos_worth_spreading ! -> {}", videosWorth.toString());
            winHashcode(videosWorth, "videos_worth_spreading.out");
            log.info("Finished videos_worth_spreading!");
        });

        executor.shutdown();
        log.info("Finished processing.");
    }

    public static void winHashcode(InputFile inputFile, String outputfile) {
        Map<Integer, Endpoint> endpoints = inputFile.getEndpoints();
        List<Video> videos = inputFile.getVideos();
        Set<Request> requests = inputFile.getRequests();

        final Map<Integer, TimeSaving> videoTimeSavings = new HashMap<>();

        final int[] processed = new int[]{0};

        requests.forEach(request -> {
            if (!(endpoints.get(request.getEndpointuid()) == null)) {
                //region GET REQUEST BEST GAIN INFO
                Video video = videos.get(request.getVideouid());
                int multiplicity = request.getMultiplicity();

                int latencyDatacenter;


                latencyDatacenter = endpoints.get(request.getEndpointuid())
                        .getDataserverlatency();


                Map<CacheServer, Integer> cacheServerLatencies =
                        endpoints.get(request.getEndpointuid())
                                .getCacheServerLatencyMap();

                assert cacheServerLatencies != null;

                int bestCacheServerLatency = cacheServerLatencies
                        .values()
                        .stream()
                        .min(Integer::compare)
                        .orElse(Integer.MAX_VALUE);

                CacheServer bestCacheServer = cacheServerLatencies
                        .keySet()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(cacheServerLatencies::containsKey)
                        .filter(server -> cacheServerLatencies.get(server) == bestCacheServerLatency)
                        .filter(Objects::nonNull)
                        .findAny()
                        .orElse(null);

                int gain = (latencyDatacenter - bestCacheServerLatency) * multiplicity;
                //endregion

                //region COMPUTE TIME SAVINGS
                // Check useful
                if (gain > 0) {
                    // Check possible
                    if (bestCacheServer != null && inputFile.getCachesrvsize() - bestCacheServer.getUsedMB() >= 10 + video.getSizeMB()) {
                        if (videoTimeSavings.containsKey(request.getVideouid())) {
                            TimeSaving timeSaving = videoTimeSavings.get(request.getVideouid());
                            timeSaving.getCacheServers().add(bestCacheServer);
                            bestCacheServer.setUsedMB(bestCacheServer.getUsedMB() + video.getSizeMB());
                            int oldtimesave = timeSaving.getTimesaved();
                            timeSaving.setTimesaved(oldtimesave + gain);
                        } else {
                            TimeSaving timeSaving = new TimeSaving(request.getVideouid(), video);
                            timeSaving.getCacheServers().add(bestCacheServer);
                            bestCacheServer.setUsedMB(bestCacheServer.getUsedMB() + video.getSizeMB());
                            timeSaving.setTimesaved(gain);
                            videoTimeSavings.put(request.getVideouid(), timeSaving);
                        }
                    }
                }
                if (++processed[0] % 10000 == 0) {
                    log.info("Processed {} requests.", processed[0]);
                }
                //endregion
            }

        });

        log.info("Processed all {} requests!", processed[0]);


        //region SORT TIME SAVINGS
        final int[] sorted = new int[]{0};
        List<TimeSaving> timeSavings = videoTimeSavings.values()
                .stream()
                .map(t -> {
                    if (++sorted[0] % 100 == 0) {
                        log.info("Sorted through {} timesaving options", sorted[0]);
                    }
                    return t;
                })
                .sorted((t1, t2) -> t1.getTimesaved() >= t2.getTimesaved() ? 1 : -1)
                .collect(Collectors.toList());
        //endregion

        String solution = Serializer.toSolution(timeSavings);
        Path outfile = Paths.get("/Users/Tristan/IdeaProjects/HashCode2017/output/" + outputfile);
        Serializer.writeSolution(outfile, solution);
    }
}
