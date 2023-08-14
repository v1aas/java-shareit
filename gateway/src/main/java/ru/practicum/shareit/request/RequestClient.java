package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDTO;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Integer userId, RequestDTO requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getRequest(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequest(Integer userId, Integer from, Integer size) {
        return get("/all", (long) userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getRequestById(Integer requestId, Integer userId) {
        return get("/" + requestId, userId);
    }
}