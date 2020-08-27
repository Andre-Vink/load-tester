package nl.ns.infoplus;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import picocli.CommandLine;

import javax.enterprise.context.Dependent;
import io.vertx.mutiny.ext.web.client.WebClient;
import javax.inject.Inject;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CommandLine.Command
public class CallCommand implements Runnable {

    @CommandLine.Option(names = {"-n"}, description = "number of concurrent calls", defaultValue = "100")
    int nocc;

    @CommandLine.Option(names = {"-u", "--url"}, description = "url to be used", defaultValue = "http://localhost:8080/api/stamgegevens/spoorcode/all")
    URI uri;

    private final CallService callService;

    public CallCommand(CallService callService) {
        this.callService = callService;
    }

    @Override
    public void run() {
        callService.calls(nocc, uri);
    }
}

@Dependent
class CallService {

    @Inject
    Vertx vertx;

    void calls(int nocc, URI uri) {
        System.out.println("nocc is: " + nocc + " uri is: " + uri);

        List<CompletableFuture<HttpResponse<String>>> list = new ArrayList<>(nocc);



        for(int i = 0; i < nocc; i++) {
//            final int ii = i;
//            Thread t = new Thread(() -> {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .version(HttpClient.Version.HTTP_2)
                        .GET()
                        .build();

            HttpClient client = HttpClient.newHttpClient();
//                try {
//                    long starttime = System.currentTimeMillis();
                    CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//                    long endtime = System.currentTimeMillis();
//                    long takentime = endtime - starttime;
//                    System.out.println("[" + ii + "] (" + takentime + ") " + response.statusCode());

//                } catch (Exception x) {
//                    x.printStackTrace();
//                }
//            });
            list.add(response);
//            t.start();
        }

        for (int i = 0; i < list.size(); i++) {
            CompletableFuture<HttpResponse<String>> t = list.get(i);
//            try {
            try {
                HttpResponse<String> response = t.get();
                System.out.println("[" + i + "] response = " + response.statusCode());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
//            } catch (InterruptedException x) {
//                x.printStackTrace();
//            }
        }


//        String host = url.getHost();
//        int port = url.getPort();
//        String req = url.getPath();
//
//        List<Uni<String>> list = new ArrayList<>(nocc);
//
//        for(int i=0;i<nocc;i++) {
//            Uni uni = new Times(i).makeUni(host, port , req);
//            list.add(uni);
//        }
//
//        Uni<String> uni = Uni.combine().all().unis(list).combinedWith(results -> {
//            StringBuilder stringBuilder = new StringBuilder();
//            for (Object o : results) {
//                String s = (String) o;
//                stringBuilder.append(s).append("\n");
//            }
//            return stringBuilder.toString();
//        });
//        String s = uni.await().indefinitely();
//
//        System.out.println("resultaat = " + s);
//        System.out.println();
    }

//    class Times {
//
//        long starttime = 0;
//        int nr;
//
//        public Times(int nr) {
//            this.nr = nr;
//        }
//
//        Uni<String>  makeUni(String host, int port, String req) {
//
//            // Use io.vertx.mutiny.ext.web.client.WebClient
//            WebClient client = WebClient.create(vertx, new WebClientOptions().setTrustAll(true));
//
//            Uni<String> uni =
//                    client.get(port, host, req).send()
//                            .onItem().transform(resp -> {
//                        if (resp.statusCode() == 200) {
//                            return resp.bodyAsString();
//                        } else {
//                            return "call resulted in: " + resp.statusCode() + " " + resp.bodyAsString();
//                        }
//                    });
//
//            starttime = System.currentTimeMillis();
//
//            return uni
//                    .onItem().transform(s -> {
//                        long endtime = System.currentTimeMillis();
//                        long timetaken = endtime - starttime;
//                        return "[" + nr + "] (" + starttime + "-" + endtime + "=" + timetaken + ")" + s;
//                    });
//        }
//    }
}
