

### ビルド方法
mvn package -P bundle
→ target/dist/KeplerianTelemetry/ に配置される

mvn package -P bundle -DskipTests


### 開発環境での実行方法
mvn spring-boot:run


http://localhost:8080/

ws://localhost:8080/ksd 




java -jar target/telemetry-1.0.0.jar

java -jar telemetry-1.0.0.jar

java -jar target/telemetry-1.0.0.jar --server.port=9090


{
  "messageType": "ObjectList",
  "spaceObjects": [
    { "id": 1, "name": "Sun", "type": "planet", "parentId": 1 },
    ...
  ]
}


{
  "messageType": "Telemetry",
  "currentTime": 999999,
  "spaceObjects": [
    { "id": 1, "position": {...}, "velocity": {...} },
    ...
  ]
}
