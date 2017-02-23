package moe.tristan.HashCode2017.util;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import moe.tristan.HashCode2017.servers.CacheServer;
import moe.tristan.HashCode2017.servers.Video;
import moe.tristan.HashCode2017.users.Endpoint;
import moe.tristan.HashCode2017.users.Request;

import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * Created by tristan on 23/02/2017.
 */
@Data
@Slf4j
@ToString(exclude = {"videos", "requests", "endpoints"})
public class InputFile {

    private int nbvideos;
    private int nbendpoints;
    private int nbreqdesc;
    private int nbcachesrv;
    private int cachesrvsize;

    private List<Video> videos = new ArrayList<>();
    private List<Endpoint> endpoints = new ArrayList<>();
    private Set<Request> requests = new HashSet<>();


    public InputFile(List<String> inputLines) {
        log.info("Loading new input file with lines from {} "+inputLines);
        String[] curline;

        curline = inputLines.get(0).split(" ");
        loadGeneralInfo(curline);
        inputLines.remove(0);
        log.info("Finished loading general informations.");

        curline = inputLines.get(0).split(" ");
        loadVideos(curline);
        inputLines.remove(0);
        log.info("Finished loading videos");

        loadEndpoints(inputLines);
        log.info("Finished loading endpoints");

        inputLines.forEach(this::loadReqDescs);
        log.info("Finished loading request descriptions");
    }

    private void loadGeneralInfo(String[] firstLine) {
        this.nbvideos = parseInt(firstLine[0]);
        this.nbendpoints = parseInt(firstLine[1]);
        this.nbreqdesc = parseInt(firstLine[2]);
        this.nbcachesrv = parseInt(firstLine[3]);
        this.cachesrvsize = parseInt(firstLine[4]);

        log.info("Loaded general infos!\n" +
                "\tnbvideos({}), " +
                "\tnbendpoints({}), " +
                "\tnbreqdesc({}), " +
                "\tnbcachesrv({}), " +
                "\tcachesrvsize({})",
                nbvideos,
                nbendpoints,
                nbreqdesc,
                nbcachesrv,
                cachesrvsize
        );
    }

    private void loadVideos(String[] secondLine) {
        final int[] loaded = {0};
        Arrays.stream(secondLine)
                .map(Integer::parseInt)
                .filter(size -> size <= cachesrvsize)
                .map(Video::new)
                .forEachOrdered(video -> {
                    loaded[0]++;
                    if (loaded[0]%1000==0) {
                        log.info("Loaded {} videos", loaded[0]);
                    }
                    videos.add(video);
                });
    }

    private void loadEndpoints(List<String> inputLines) {
        String[] curLine;

        for (int i = 0; i < this.nbendpoints; i++) {
            if (i % 100 == 0) {
                log.info("Loaded {} endpoints", i);
            }
            curLine = inputLines.get(0).split(" ");
            int endpointntuid = parseInt(curLine[0]);
            int connectedcaches = parseInt(curLine[1]);

            //region filter out disconnected endpoints
            //Skip nonconnected endpoints. They'll use cache anyway
            if (connectedcaches == 0) {
                log.info("Endpoint {} was not connected to any cache server", Arrays.toString(curLine));
                inputLines.remove(0);
                continue;
            }
            //endregion

            //region Load the endpoint
            Endpoint endpoint = new Endpoint(endpointntuid);
            for (int j = 0; j < connectedcaches; j++) {
                String[] connectionStr = inputLines.get(1).split(" ");
                int cacheuid = parseInt(connectionStr[0]);
                int latency = parseInt(connectionStr[1]);

                endpoint.getCacheServerLatencyMap().put(
                        new CacheServer(cacheuid),
                        latency
                );

                inputLines.remove(1);
            }
            this.endpoints.add(endpoint);
            //endregion

            inputLines.remove(0);
        }
    }

    private void loadReqDescs(String reqLine) {
        String[] lineArr = reqLine.split(" ");
        int videouid = parseInt(lineArr[0]);
        int endpointuid = parseInt(lineArr[1]);
        int numrequests = parseInt(lineArr[2]);

        Request req = Request.builder()
                .videouid(videouid)
                .endpointuid(endpointuid)
                .multiplicity(numrequests)
                .build();

        this.requests.add(req);
    }
}
