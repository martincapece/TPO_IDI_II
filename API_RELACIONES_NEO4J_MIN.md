# Relaciones Neo4j (mínimas) y operaciones permitidas

Este módulo gestiona solo las relaciones necesarias para recomendaciones. La tabla resume qué puede eliminarse y cómo lo implementamos.

## Relaciones

- SIGUE (Usuario -> Usuario)
  - Puede eliminarse: Sí
  - Endpoints:
    - Crear: POST /api/grafo/sigue?origen={id}&destino={id}
    - Eliminar: DELETE /api/grafo/sigue?origen={id}&destino={id}

- INTERESADO_EN (Usuario -> Categoria) [score]
  - Puede eliminarse: Ajustar score o eliminar si llega a 0
  - Endpoints:
    - Incrementar/crear: POST /api/grafo/interes?usuarioId={id}&categoria={c}&score={n}
    - Ajustar (delta positivo/negativo): PATCH /api/grafo/interes/ajustar?usuarioId={id}&categoria={c}&delta={n}
      - Si el score resultante es <= 0, la relación se elimina automáticamente
    - Eliminar explícitamente: DELETE /api/grafo/interes?usuarioId={id}&categoria={c}

- LE_GUSTO (Usuario -> Contenido)
  - Puede eliminarse: Opcional, permitir "quitar me gusta"
  - Endpoints:
    - Marcar: POST /api/grafo/me-gusto?usuarioId={id}&contenidoId={id}
    - Quitar: DELETE /api/grafo/me-gusto?usuarioId={id}&contenidoId={id}

- VIO (Usuario -> Contenido)
  - Puede eliminarse: No (se mantiene histórico)
  - Endpoints:
    - Registrar: POST /api/grafo/vio?usuarioId={id}&contenidoId={id}&secciones={n}

- EN_CATEGORIA (Contenido -> Categoria)
  - Puede eliminarse: No desde API pública; solo mantenimiento interno/sincronización
  - Endpoints:
    - Asignar: POST /api/grafo/en-categoria?contenidoId={id}&categoria={c}

## Notas técnicas

- Ajuste de interés: `ajustarInteres` hace MERGE de la relación y suma `delta` al `r.score`. Si el resultado es `<= 0`, se borra la relación.
- Todas las operaciones usan consultas Cypher en `GrafoRepository` y están expuestas por `GrafoService` y `GrafoController`.
- Recomendaciones disponibles:
  - GET /api/grafo/recomendar/{usuarioId}?limite={n}
  - GET /api/grafo/relacionado/{contenidoId}?limite={n}
