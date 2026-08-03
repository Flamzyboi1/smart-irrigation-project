# Smart Irrigation Monitoring System

Spring Boot / Java 21 prototype for an automatic irrigation research project. It provides a field map, coordinates for three sensors, simulated readings, an automatic threshold recommendation and an H2 database.

## Open in Eclipse
1. Install **JDK 21** and Eclipse IDE for Enterprise Java/Web Developers (or Spring Tools).
2. Extract the ZIP.
3. Eclipse: **File -> Import -> Maven -> Existing Maven Projects**.
4. Select the extracted `irrigation-monitoring-system` folder and click Finish.
5. Right-click `IrrigationApplication.java` -> **Run As -> Java Application**.
6. Open `http://localhost:8080`.

## API
- `GET /api/dashboard` returns latest data for all sensors.
- `POST /api/readings` stores a sensor reading.
- `GET /api/sensors/{id}/readings` returns recent readings for one sensor.

Example POST body:
```json
{"sensorId":1,"soilMoisture":22.5,"temperature":21.4,"flowRate":0.8}
```

## Next development steps
- Replace simulated readings with MQTT messages from ESP32 soil-moisture sensors.
- Replace H2 with PostgreSQL.
- Add weather API, authentication, valve commands and WebSocket updates.
