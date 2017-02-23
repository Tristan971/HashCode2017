package moe.tristan.HashCode2017.util;

import lombok.Data;
import moe.tristan.HashCode2017.servers.CacheServer;
import moe.tristan.HashCode2017.servers.Video;
import moe.tristan.HashCode2017.users.Endpoint;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Created by tristan on 23/02/2017.
 */
@Data
public class InputFile {

    private int nbvideos;
    private int nbendpoints;
    private int nbreqdesc;
    private int nbcachesrv;
    private int cachesrvsize;

    private List<Video> videos;
    private List<Endpoint> endpoints;

    public InputFile(List<String> inputLines) {
        String[] curline;

        curline = inputLines.get(0).split(" ");
        loadGeneralInfo(curline);
        inputLines.remove(0);

        curline = inputLines.get(0).split(" ");
        loadVideos(curline);
        inputLines.remove(0);

        loadEndpoints(inputLines);



    }

    private void loadGeneralInfo(String[] firstLine) {
        this.nbvideos = parseInt(firstLine[0]);
        this.nbendpoints = parseInt(firstLine[1]);
        this.nbreqdesc = parseInt(firstLine[2]);
        this.nbcachesrv = parseInt(firstLine[3]);
        this.cachesrvsize = parseInt(firstLine[4]);
    }

    private void loadVideos(String[] secondLine) {
        Arrays.stream(secondLine)
                .map(Integer::parseInt)
                .filter(size -> size <= cachesrvsize)
                .map(Video::new)
                .forEach(videos::add);
    }

    private void loadEndpoints(List<String> inputLines) {
        String[] curLine;

        for (int i = 0; i < this.nbendpoints; i++) {
            curLine = inputLines.get(0).split(" ");
            int endpointntuid = parseInt(curLine[0]);
            int connectedcaches = parseInt(curLine[1]);

            //region filter out disconnected endpoints
            //Skip nonconnected endpoints. They'll use cache anyway
            if (connectedcaches == 0) {
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

    private void loadReqDescs() {

    }
}
