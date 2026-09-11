package lv.venta.irrigation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Service
public class SensLogService {
    private final RestClient client;
    private final String baseUrl;
    private final int userId;
    private final String groupName;
    public SensLogService(@Value("${senslog.base-url:https://senslog.lesprojekt.cz/senslogOTS3/rest}") String baseUrl,@Value("${senslog.user-id:29}") int userId,@Value("${senslog.group-name:vestiena}") String groupName){this.baseUrl=baseUrl;this.userId=userId;this.groupName=groupName;this.client=RestClient.builder().baseUrl(baseUrl).build();}
    public ResponseEntity<String> units(){return get("/unit?user_id="+userId);}
    public ResponseEntity<String> latest(){return get("/observation/last?group_name="+groupName);}
    public ResponseEntity<String> observations(Long unitId,Long sensorId,String from,String to){UriComponentsBuilder u=UriComponentsBuilder.fromPath("/observation");if(unitId!=null)u.queryParam("unit_id",unitId);if(sensorId!=null)u.queryParam("sensor_id",sensorId);if(from!=null&&!from.isBlank())u.queryParam("from_time",from);if(to!=null&&!to.isBlank())u.queryParam("to_time",to);return get(u.build().toUriString());}
    public ResponseEntity<String> insert(Map<String,Object> payload){try{String body=client.post().uri("/observation").contentType(MediaType.APPLICATION_JSON).body(payload).retrieve().body(String.class);return ResponseEntity.ok(body==null?"":body);}catch(Exception e){return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());}}
    private ResponseEntity<String> get(String uri){try{String body=client.get().uri(uri).retrieve().body(String.class);return ResponseEntity.ok(body==null?"[]":body);}catch(Exception e){return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());}}
}
