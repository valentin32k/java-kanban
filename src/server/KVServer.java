package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
	public static final int PORT = 8078;
	private final String apiToken;
	private final HttpServer server;
	private final Map<String, String> data = new HashMap<>();

	public static void main(String[] args) throws IOException {
		KVServer kvServer = new KVServer();
		kvServer.start();
	}

	public KVServer() throws IOException {
		apiToken = generateApiToken();
		server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
		server.createContext("/register", this::register);
		server.createContext("/save", this::save);
		server.createContext("/load", this::load);
	}

	private void load(HttpExchange h) throws IOException {
		try {
			if (!hasAuth(h)) {
				System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
				h.sendResponseHeaders(403, 0);
				return;
			}
			if (!"GET".equals(h.getRequestMethod())) {
				System.out.println("Не корркектный запрос");
				h.sendResponseHeaders(405, 0);
				return;
			}
			String key = h.getRequestURI().getPath().substring("/load/".length());
			if (key.isEmpty()) {
				System.out.println("Key для значения пустой. key указывается в пути: /save/{key}");
				h.sendResponseHeaders(400, 0);
				return;
			}
			String response = data.get(key);
			if (response == null) {
//				А в каких случаях метод get() для HashMap с ключом типа String будет дорогой операцией?
//				Мне что-то кроме случая когда значение initialCapacity = 1 ничего в голову не приходит.
//				И то там O(log n) и только для нескольких значений - O(n)
				System.out.println("Запрошенная запись с ключом " + key + " отсутствует");
				h.sendResponseHeaders(404, 0);
				return;
			}
			h.sendResponseHeaders(200, 0);
			try (OutputStream os = h.getResponseBody()) {
				os.write(response.getBytes(StandardCharsets.UTF_8));
			}
		} finally {
			h.close();
		}
	}

	private void save(HttpExchange h) throws IOException {
		try {
			if (!hasAuth(h)) {
				System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
				h.sendResponseHeaders(403, 0);
				return;
			}
			if ("POST".equals(h.getRequestMethod())) {
				String key = h.getRequestURI().getPath().substring("/save/".length());
				if (key.isEmpty()) {
					System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
					h.sendResponseHeaders(400, 0);
					return;
				}
				String value = readText(h);
//				if (value.isEmpty()) {
//					System.out.println("Value для сохранения пустой. value указывается в теле запроса");
//					h.sendResponseHeaders(400, 0);
//					return;
//				}
				data.put(key, value);
//				System.out.println("Значение для ключа " + key + " успешно обновлено!");
				h.sendResponseHeaders(200, 0);
			} else {
				System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		} finally {
			h.close();
		}
	}

	private void register(HttpExchange h) throws IOException {
		try {
			if ("GET".equals(h.getRequestMethod())) {
				sendText(h, apiToken);
			} else {
				System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
				h.sendResponseHeaders(405, 0);
			}
		} finally {
			h.close();
		}
	}

	public void start() {
		server.start();
	}

	public void stop() {
		server.stop(0);
	}

	private String generateApiToken() {
		return "" + System.currentTimeMillis();
	}

	protected boolean hasAuth(HttpExchange h) {
		String rawQuery = h.getRequestURI().getRawQuery();
		return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
	}

	protected String readText(HttpExchange h) throws IOException {
		return new String(h.getRequestBody().readAllBytes(), UTF_8);
	}

	protected void sendText(HttpExchange h, String text) throws IOException {
		byte[] resp = text.getBytes(UTF_8);
		h.getResponseHeaders().add("Content-Type", "application/json");
		h.sendResponseHeaders(200, resp.length);
		h.getResponseBody().write(resp);
	}
}
