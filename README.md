# KeplerianTelemetry

Keplerian Space Discovery（以下KSD）のテレメトリを受信するSpring Boot サーバ。  
WebSocket 経由で KSDから各天体／宇宙機の軌道情報（カルテシアン要素・ケプラー要素）を収集し、REST API で提供する。

本リポジトリはサーバおよび Web クライアントの**参照実装**である。WebSocket / REST API の仕様に準拠していれば、サーバ・クライアントともに独自実装に置き換えることができる。

---

## 目次

- [ビルド方法](#ビルド方法)
- [実行方法](#実行方法)
- [Web ダッシュボード](#web-ダッシュボード)
- [WebSocket API](#websocket-api)
- [REST API](#rest-api)
- [データモデル](#データモデル)

---

## ビルド方法

**前提条件:** Maven 3、Java 25

### 開発用ビルド

```bash
mvn package
```

### 配布用バンドルビルド（JRE 同梱）

```bash
mvn package -P bundle
```

`target/dist/KeplerianTelemetry/` 以下に以下が生成される。

| ファイル / フォルダ | 内容 |
|---|---|
| `telemetry-1.0.0.jar` | アプリケーション JAR |
| `start.bat` | 起動スクリプト（Windows） |
| `jdk-25.0.3.9/` | 同梱 Eclipse Adoptium JDK |
| `LICENSES/` | ライセンスファイル |

テストをスキップする場合:

```bash
mvn package -P bundle -DskipTests
```

---

## 実行方法

### 開発環境（ホットリロードあり）

```bash
mvn spring-boot:run
```

### JAR を直接実行

```bash
java -jar target/telemetry-1.0.0.jar
```

### ポートを変更して起動

```bash
java -jar target/telemetry-1.0.0.jar --server.port=9090
```

### 配布バンドルを実行（Windows）

```
target\dist\KeplerianTelemetry\start.bat
```

### 設定

`src/main/resources/application.properties` で変更可能。

| プロパティ | デフォルト値 | 説明 |
|---|---|---|
| `server.port` | `8080` | リスンポート |
| `logging.level.net.keplerian.telemetry.websocket.KsdWebSocketHandler` | `INFO` | WebSocket ハンドラのログレベル |

---

## Web ダッシュボード

`src/main/resources/static/index.html` は参照実装として同梱されている。起動後、ブラウザで `http://localhost:8080/` にアクセスすると確認できる。

- 接続状態インジケーター（緑 / 赤）
- 登録オブジェクト数・シミュレーション時刻のリアルタイム表示
- タイプ別フィルタ
- 直交座標要素・ケプラー要素を含む一覧テーブル
- REST API を 10 秒ごとにポーリングして自動更新

### カスタムクライアントの実装

独自の HTML クライアントを実装する場合、`index.html` を参考にしながら REST API を利用するだけでよい。

- **データ取得:** `GET /api/objects` を任意の間隔でポーリングする
- **単体取得:** `GET /api/objects/{id}` で特定オブジェクトのみ取得できる
- **ポーリングの副作用:** `GET /api/objects` を呼び出すたびにサーバが KSD へ `QueryTelemetry` を送信するため、ポーリング間隔がそのままテレメトリの更新頻度になる

WebSocket への直接接続は不要で、REST API だけで完結する。`index.html` を `static/` フォルダに置けばサーバから配信されるが、別ホストで動かして CORS なしで利用することも可能（サーバは全オリジンを許可している）。

---

## WebSocket API

以下に定めるメッセージ仕様に準拠すれば、本サーバを独自実装のサーバに置き換えることができる。KSD はサーバの実装に依存せず、メッセージの種別・フォーマットのみに依存する。

**エンドポイント:** `ws://localhost:8080/ksd`

### 接続シーケンス

```
KSD                                  サーバ
    |                                   |
    |--- (接続確立) ------------------->|
    |                                   |
    |<-- QueryObjects ------------------|  オブジェクト情報を要求
    |                                   |
    |--- ObjectList ------------------->|  オブジェクト情報を返す
    |                                   |
    |<-- QueryTelemetry ----------------|  テレメトリを要求
    |                                   |
    |--- Telemetry -------------------->|  テレメトリを返す
    |                                   |
    :  以降、REST API の /api/objects 呼  :
    :  び出しごとに QueryTelemetry が    :
    :  送信される                        :
```

---

### サーバ → KSD メッセージ

#### QueryObjects

接続確立時にサーバが送信する。オブジェクトメタデータ（`ObjectList`）を要求する。

```json
{
  "messageType": "QueryObjects"
}
```

#### QueryTelemetry

テレメトリ（`Telemetry`）を要求する。接続確立時および REST `/api/objects` 呼び出し時にKSDへ送信される。

```json
{
  "messageType": "QueryTelemetry"
}
```

---

### KSD → サーバ メッセージ

#### ObjectList

宇宙オブジェクトのメタデータを送信する。

```json
{
  "messageType": "ObjectList",
  "spaceObjects": [
    {
      "id": 1,
      "name": "Sun",
      "type": "planet",
      "parentId": null
    },
    {
      "id": 3,
      "name": "Earth",
      "type": "planet",
      "parentId": 1
    },
    {
      "id": 5,
      "name": "ISS",
      "type": "satellite",
      "parentId": 3
    }
  ]
}
```

| フィールド | 型 | 説明 |
|---|---|---|
| `messageType` | string | 固定値 `"ObjectList"` |
| `spaceObjects[].id` | number | オブジェクト固有 ID |
| `spaceObjects[].name` | string | オブジェクト名 |
| `spaceObjects[].type` | string | 種別（`"planet"`, `"satellite"` 等） |
| `spaceObjects[].parentId` | number \| null | 親オブジェクトの ID。なければ `null` |

#### Telemetry

各オブジェクトの現在の軌道状態を送信する。

```json
{
  "messageType": "Telemetry",
  "currentTime": 1609459200,
  "spaceObjects": [
    {
      "id": 3,
      "cart": {
        "pos": { "x": 1.496e11, "y": 0.0, "z": 0.0 },
        "vel": { "x": 0.0,      "y": 29784.0, "z": 0.0 }
      },
      "kep": {
        "ep":   1609459200,
        "a":    1.496e11,
        "e":    0.0167086,
        "i":    0.0,
        "raan": 0.0,
        "argp": 102.9373,
        "ma":   100.4646
      }
    }
  ]
}
```

| フィールド | 型 | 説明 |
|---|---|---|
| `messageType` | string | 固定値 `"Telemetry"` |
| `currentTime` | number | シミュレーション時刻（Unix 秒） |
| `spaceObjects[].id` | number | オブジェクト ID |
| `spaceObjects[].cart.pos` | Vector3 | 位置（メートル） |
| `spaceObjects[].cart.vel` | Vector3 | 速度（m/s） |
| `spaceObjects[].kep.ep` | number | エポック（Unix 秒） |
| `spaceObjects[].kep.a` | number | 長半径（メートル） |
| `spaceObjects[].kep.e` | number | 離心率 |
| `spaceObjects[].kep.i` | number | 軌道傾斜角（度） |
| `spaceObjects[].kep.raan` | number | 昇交点赤経（度） |
| `spaceObjects[].kep.argp` | number | 近点引数（度） |
| `spaceObjects[].kep.ma` | number | 平均近点角（度） |

> **注意:** KSDが送出する `nan`、`-nan(ind)`、`inf`、`-inf` 等の非数値はサーバ側で JSON の `null` に変換される。

---

## REST API

**ベース URL:** `http://localhost:8080/api`

---

### GET /api/objects

登録されているすべての宇宙オブジェクトの情報とテレメトリを取得する。  
呼び出しと同時に、KSDへ `QueryTelemetry` が送信される。

**リクエスト**

```
GET /api/objects
```

**レスポンス（200 OK）**

```json
{
  "currentTime": 1609459200,
  "objects": [
    {
      "id": 1,
      "name": "Sun",
      "type": "planet",
      "parentId": null,
      "cart": {
        "pos": { "x": 0.0, "y": 0.0, "z": 0.0 },
        "vel": { "x": 0.0, "y": 0.0, "z": 0.0 }
      },
      "kep": {
        "ep": 1609459200,
        "a": 0.0,
        "e": 0.0,
        "i": 0.0,
        "raan": 0.0,
        "argp": 0.0,
        "ma": 0.0
      }
    },
    {
      "id": 3,
      "name": "Earth",
      "type": "planet",
      "parentId": 1,
      "cart": {
        "pos": { "x": 1.496e11, "y": 0.0, "z": 0.0 },
        "vel": { "x": 0.0, "y": 29784.0, "z": 0.0 }
      },
      "kep": {
        "ep": 1609459200,
        "a": 1.496e11,
        "e": 0.0167086,
        "i": 0.0,
        "raan": 0.0,
        "argp": 102.9373,
        "ma": 100.4646
      }
    }
  ]
}
```

---

### GET /api/objects/{id}

指定 ID の宇宙オブジェクトを取得する。

**リクエスト**

```
GET /api/objects/3
```

**レスポンス（200 OK）**

```json
{
  "id": 3,
  "name": "Earth",
  "type": "planet",
  "parentId": 1,
  "cart": {
    "pos": { "x": 1.496e11, "y": 0.0, "z": 0.0 },
    "vel": { "x": 0.0, "y": 29784.0, "z": 0.0 }
  },
  "kep": {
    "ep": 1609459200,
    "a": 1.496e11,
    "e": 0.0167086,
    "i": 0.0,
    "raan": 0.0,
    "argp": 102.9373,
    "ma": 100.4646
  }
}
```

**レスポンス（404 Not Found）**

指定 ID が存在しない場合。ボディなし。

---

## データモデル

### Vector3

| フィールド | 型 | 説明 |
|---|---|---|
| `x` | double | X 成分 |
| `y` | double | Y 成分 |
| `z` | double | Z 成分 |

### CartesianElements

| フィールド | 型 | 説明 |
|---|---|---|
| `pos` | Vector3 | 位置（メートル） |
| `vel` | Vector3 | 速度（m/s） |

### KeplerianElements

| フィールド | 型 | 説明 |
|---|---|---|
| `ep` | long | エポック（Unix 秒） |
| `a` | double | 長半径（メートル） |
| `e` | double | 離心率（0〜1） |
| `i` | double | 軌道傾斜角（度） |
| `raan` | double | 昇交点赤経（度） |
| `argp` | double | 近点引数（度） |
| `ma` | double | 平均近点角（度） |

### TelemetryResponse（REST レスポンス）

| フィールド | 型 | 説明 |
|---|---|---|
| `currentTime` | Long | 現在のシミュレーション時刻（Unix 秒） |
| `objects` | SpaceObject[] | 全オブジェクトの配列 |

### SpaceObject

| フィールド | 型 | 説明 |
|---|---|---|
| `id` | long | 固有 ID |
| `name` | string | オブジェクト名 |
| `type` | string | 種別 |
| `parentId` | Long \| null | 親オブジェクト ID |
| `cart` | CartesianElements | 直交座標要素 |
| `kep` | KeplerianElements | ケプラー要素 |
