package ramune314159265.spigotserverlistsmapi;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;

public class ServerList {
	public static HashMap<String, ServerData> servers;

	public static HashMap<String, ServerData> get() {
		servers = new HashMap<>();
		String hostName = "http://localhost:9000";
		String url = hostName + "/api/v1/servers/";
		try {
			HttpClient client = HttpClient.newBuilder()
					.version(HttpClient.Version.HTTP_1_1)
					.build();
			HttpRequest req = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("Accept", "application/json")
					.build();
			HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
			Gson gson = new Gson();
			ServerData[] serverList = gson.fromJson(res.body(), ServerData[].class);
			Arrays.stream(serverList).toList().forEach(server -> ServerList.servers.put(server.id, server));
			return servers;
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
