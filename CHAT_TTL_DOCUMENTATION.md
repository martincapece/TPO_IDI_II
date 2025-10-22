# 📋 Documentación TTL del Chat de Streaming

## ✅ Implementación Completada

Se ha implementado **TTL (Time To Live)** para los chats de streaming almacenados en Redis.

---

## ⏱️ **Configuración del TTL**

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| **Expiración Post-Finalización** | **5 minutos** | Tiempo que permanecen los mensajes después de finalizar el streaming |
| **Límite de Mensajes** | 500 mensajes | Máximo de mensajes almacenados simultáneamente |
| **Almacenamiento** | Redis | Base de datos en memoria (volátil) |

---

## 🔧 **Comportamiento del Sistema**

### **Durante el Streaming (En Vivo)**
- ✅ Los mensajes se almacenan en Redis sin expiración
- ✅ Se mantienen hasta 500 mensajes (los más recientes)
- ✅ Los mensajes más antiguos se eliminan automáticamente al superar el límite

### **Al Finalizar el Streaming**
Cuando se llama al endpoint `POST /api/streamings/{id}/finalizar`:
1. **Se activa el TTL de 5 minutos** para el chat completo
2. Todos los mensajes del chat expirarán **exactamente 5 minutos después**
3. Redis elimina automáticamente la clave completa del chat

### **Después de 5 Minutos**
- ⚠️ **Todos los mensajes del chat se eliminan permanentemente**
- ⚠️ **No hay recuperación posible** (Redis los borra)
- ✅ Libera memoria automáticamente

---

## 📡 **Endpoints Disponibles**

### **1. Enviar Mensaje al Chat**
```http
POST /api/streamings/{streamId}/chat
Content-Type: application/json

{
  "text": "¡Hola! Gran streaming 🎮"
}
```

**Respuesta:**
```json
{
  "userId": "68f931a3adb4d1400e1198f2",
  "username": "hola",
  "text": "¡Hola! Gran streaming 🎮",
  "timestamp": "2025-10-22T18:10:30.123"
}
```

---

### **2. Obtener Mensajes del Chat**
```http
GET /api/streamings/{streamId}/chat?limit=50
```

**Respuesta:**
```json
[
  {
    "userId": "68f931a3adb4d1400e1198f2",
    "username": "hola",
    "text": "¡Hola! Gran streaming 🎮",
    "timestamp": "2025-10-22T18:10:30.123"
  },
  {
    "userId": "789abc456def",
    "username": "maria",
    "text": "¡Excelente contenido!",
    "timestamp": "2025-10-22T18:11:45.456"
  }
]
```

---

### **3. Finalizar Streaming (Activa TTL de 5 minutos)**
```http
POST /api/streamings/{id}/finalizar
```

**Respuesta:**
```json
{
  "id": "68f3e22e0ca0cacb1b73dcd3",
  "chatId": "DASDA456465465SADA",
  "titulo": "Mi streaming",
  "enVivo": false,
  "fechaFin": "2025-10-22T18:15:00.000+00:00"
}
```

⚠️ **Importante:** Después de este endpoint, el chat expirará en **5 minutos**.

---

### **4. Consultar TTL del Chat (Tiempo Restante)**
```http
GET /api/streamings/{streamId}/chat/ttl
```

**Respuesta cuando el streaming está activo:**
```json
{
  "chatId": "DASDA456465465SADA",
  "streamId": "68f3e22e0ca0cacb1b73dcd3",
  "status": "Sin expiración (streaming activo)",
  "ttl": -1
}
```

**Respuesta cuando el streaming finalizó:**
```json
{
  "chatId": "DASDA456465465SADA",
  "streamId": "68f3e22e0ca0cacb1b73dcd3",
  "status": "Chat expirará en:",
  "ttlSeconds": 267,
  "ttlMinutes": 4.45,
  "expirationConfigured": "5 minutos después de finalizar"
}
```

**Respuesta cuando el chat ya expiró:**
```json
{
  "chatId": "DASDA456465465SADA",
  "streamId": "68f3e22e0ca0cacb1b73dcd3",
  "status": "Chat no existe",
  "ttl": null
}
```

---

### **5. Limpiar Chat Manualmente (Opcional)**
```http
DELETE /api/streamings/{streamId}/chat
```

**Respuesta:**
```json
{
  "message": "Chat de Streaming limpiado correctamente",
  "storage": "Redis"
}
```

⚠️ **Importante:** Esto elimina **inmediatamente** todos los mensajes sin esperar el TTL.

---

## 🎯 **Casos de Uso**

### **Caso 1: Streaming Activo**
```
1. Usuario crea streaming → enVivo = true
2. Usuarios envían mensajes → Se almacenan en Redis
3. Chat funciona normalmente → Sin TTL (no expira)
4. Límite: 500 mensajes máximo
```

### **Caso 2: Finalizar Streaming**
```
1. Creador finaliza streaming → POST /finalizar
2. Sistema activa TTL de 5 minutos automáticamente
3. Usuarios aún pueden ver mensajes por 5 minutos
4. Después de 5 minutos → Redis elimina todo el chat
```

### **Caso 3: Consultar Tiempo Restante**
```
1. Usuario consulta → GET /chat/ttl
2. Sistema devuelve tiempo restante en segundos
3. Usuario puede ver cuánto tiempo queda antes de que expire
```

---

## 📊 **Arquitectura Técnica**

### **Patrón de Almacenamiento**
- **Clave Redis:** `chat:{chatId}:messages`
- **Tipo de dato:** Lista de Redis (LPUSH/RPUSH)
- **Serialización:** JSON con Jackson + JavaTimeModule
- **TTL:** Configurado con `EXPIRE` command

### **Código Clave**

**StreamingChatService.java:**
```java
private static final long CHAT_TTL_AFTER_END_MINUTES = 5;

public void setExpirationOnStreamEnd(String chatId) {
    String redisKey = "chat:" + chatId + ":messages";
    redisTemplate.expire(redisKey, CHAT_TTL_AFTER_END_MINUTES, TimeUnit.MINUTES);
}
```

**StreamingController.java:**
```java
@PostMapping("/{id}/finalizar")
public ResponseEntity<Streaming> finalizarStreaming(@PathVariable String id) {
    return streamingService.finalizarStreaming(id)
            .map(streaming -> {
                streamingChatService.setExpirationOnStreamEnd(streaming.getChatId());
                return ResponseEntity.ok(streaming);
            })
            .orElse(ResponseEntity.notFound().build());
}
```

---

## ⚙️ **Personalización del TTL**

Si necesitas cambiar el tiempo de expiración, edita esta constante en `StreamingChatService.java`:

```java
// Cambiar de 5 a X minutos
private static final long CHAT_TTL_AFTER_END_MINUTES = 5;
```

**Valores recomendados:**
- **1 minuto**: Para pruebas rápidas
- **5 minutos**: Balance ideal (implementado)
- **15 minutos**: Para streamings con mucha interacción post-evento
- **30 minutos**: Para conservar conversaciones más tiempo

---

## 🔍 **Monitoreo y Debugging**

### **Verificar en Redis CLI**
```bash
# Conectar a Redis
redis-cli

# Ver todas las claves de chat
KEYS chat:*

# Ver TTL de un chat específico
TTL chat:DASDA456465465SADA:messages

# Ver mensajes de un chat
LRANGE chat:DASDA456465465SADA:messages 0 -1
```

### **Interpretación de TTL en Redis**
- **-1**: Sin expiración (streaming activo)
- **-2**: Clave no existe (chat expirado o eliminado)
- **> 0**: Segundos restantes hasta expiración

---

## ⚠️ **Consideraciones Importantes**

### **Volatilidad de Redis**
- ✅ **Ventaja:** Extremadamente rápido para lecturas/escrituras
- ⚠️ **Desventaja:** Datos se pierden si Redis se reinicia (sin persistencia configurada)
- ⚠️ **Desventaja:** TTL elimina datos permanentemente (sin recuperación)

### **Comparación con MongoDB (Content Chats)**
| Característica | Redis (Streaming) | MongoDB (Content) |
|----------------|-------------------|-------------------|
| Persistencia | ❌ Volátil | ✅ Permanente |
| TTL Automático | ✅ 5 minutos | ❌ No expira |
| Velocidad | 🚀 Ultra rápida | 🐢 Más lenta |
| Uso | Chats temporales | Chats permanentes |
| Límite | 500 mensajes | Ilimitado |

---

## 🎉 **Resumen**

✅ **TTL de 5 minutos** implementado correctamente  
✅ Se activa automáticamente al finalizar el streaming  
✅ Endpoint para consultar tiempo restante disponible  
✅ Límite de 500 mensajes activo  
✅ Documentación completa generada  

**¡El sistema de chat con TTL está listo para producción!** 🚀
