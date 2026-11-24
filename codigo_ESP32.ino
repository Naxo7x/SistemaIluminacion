#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>

const char* WIFI_SSID     = "";
const char* WIFI_PASSWORD = "";

const String FIREBASE_URL = "https://sistema-iluminacion-63d38-default-rtdb.firebaseio.com/iluminacion.json";

const int PIN_LDR = 34;
const int PIN_LED = 25;

String modoOperacion = "automatico";
int umbralEncender = 250;
int umbralApagar   = 450;
bool estadoLed     = false;

unsigned long ultimoEnvio = 0;

String leerTexto(const String& texto, const String& clave) {
  String buscar = "\"" + clave + "\"";
  int pos = texto.indexOf(buscar);
  if (pos == -1) return "";
  int puntos = texto.indexOf(":", pos);
  int inicio = texto.indexOf("\"", puntos);
  int fin = texto.indexOf("\"", inicio + 1);
  if (inicio == -1 || fin == -1) return "";
  return texto.substring(inicio + 1, fin);
}

int leerNumero(const String& texto, const String& clave, int defecto) {
  String buscar = "\"" + clave + "\"";
  int pos = texto.indexOf(buscar);
  if (pos == -1) return defecto;
  int puntos = texto.indexOf(":", pos);
  int i = puntos + 1;
  while (i < (int)texto.length() && texto[i] == ' ') i++;
  String num = "";
  while (i < (int)texto.length() && isDigit(texto[i])) {
    num += texto[i];
    i++;
  }
  return num.length() > 0 ? num.toInt() : defecto;
}

bool leerBool(const String& texto, const String& clave, bool defecto) {
  String buscar = "\"" + clave + "\"";
  int pos = texto.indexOf(buscar);
  if (pos == -1) return defecto;
  int puntos = texto.indexOf(":", pos);
  String parte = texto.substring(puntos + 1);
  parte.trim();
  if (parte.startsWith("true")) return true;
  if (parte.startsWith("false")) return false;
  return defecto;
}

void conectarWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Conectando al WiFi");
  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED && intentos < 30) {
    delay(500);
    Serial.print(".");
    intentos++;
  }
  Serial.println();
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("WiFi conectado!");
  } else {
    Serial.println("No se pudo conectar al WiFi.");
  }
}

void setup() {
  pinMode(PIN_LED, OUTPUT);
  digitalWrite(PIN_LED, LOW);
  Serial.begin(115200);
  conectarWiFi();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    conectarWiFi();
  }

  int luzActual = analogRead(PIN_LDR);

  if (millis() - ultimoEnvio >= 1500) {
    ultimoEnvio = millis();

    if (WiFi.status() == WL_CONNECTED) {
      WiFiClientSecure client;
      client.setInsecure(); 

      HTTPClient httpLeer;
      httpLeer.begin(client, FIREBASE_URL);
      int codigoLeer = httpLeer.GET();

      Serial.print("Codigo GET Firebase: ");
      Serial.println(codigoLeer);

      if (codigoLeer == 200) {
        String datos = httpLeer.getString();
        Serial.println("Datos recibidos: " + datos);

        String modo = leerTexto(datos, "modo_operacion");
        if (modo.length() > 0) {
          modoOperacion = modo;
        }
        umbralEncender = leerNumero(datos, "umbral_encender", umbralEncender);
        umbralApagar   = leerNumero(datos, "umbral_apagar", umbralApagar);
        estadoLed      = leerBool(datos, "estado_led", estadoLed);

        if (modoOperacion == "manual") {
          digitalWrite(PIN_LED, estadoLed ? HIGH : LOW);
        } else {
          if (luzActual < umbralEncender) {
            estadoLed = true;
          } else if (luzActual > umbralApagar) {
            estadoLed = false;
          }
          digitalWrite(PIN_LED, estadoLed ? HIGH : LOW);
        }

        HTTPClient httpEnviar;
        httpEnviar.begin(client, FIREBASE_URL);
        httpEnviar.addHeader("Content-Type", "application/json");

        String jsonEnviar = "{\"luz_actual\":" + String(luzActual) +
                            ",\"estado_led\":" + String(estadoLed ? "true" : "false") + "}";

        int codigoPatch = httpEnviar.PATCH(jsonEnviar);
        Serial.print("Codigo PATCH Firebase: ");
        Serial.println(codigoPatch);
        httpEnviar.end();
      } else {
        Serial.print("Error HTTP: ");
        Serial.println(httpLeer.errorToString(codigoLeer));
      }
      httpLeer.end();
    }

    Serial.print("Modo: ");
    Serial.print(modoOperacion);
    Serial.print(" | Luz: ");
    Serial.print(luzActual);
    Serial.print(" | LED: ");
    Serial.println(estadoLed ? "ENCENDIDO" : "APAGADO");
  }
}
