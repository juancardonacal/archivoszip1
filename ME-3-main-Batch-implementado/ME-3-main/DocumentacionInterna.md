# Documentacion interna

## Procesamiento por lotes (Batch)

La opcion 21 del menu implementa el requisito de la guia: leer un archivo CSV con el
formato `idEstudiante,codigoMateria`, encolar todas las solicitudes y procesarlas en el
mismo orden de llegada.

### Flujo implementado

1. El usuario ingresa la ruta del archivo CSV.
2. `cargarSolicitudesDesdeCsv` valida que el archivo exista y que cada linea tenga dos
   campos obligatorios.
3. Cada linea valida se transforma en una `SolicitudInscripcion` y se agrega a una
   `MiColaDatos<SolicitudInscripcion>`.
4. `procesarArchivoBatch` desencola una solicitud a la vez, respetando FIFO.
5. `procesarSolicitudBatch` valida que existan el estudiante y la materia antes de llamar
   a la logica normal de inscripcion.
6. Al terminar, se muestra el resumen con inscripciones exitosas y fallidas.

### Decisiones de implementacion

- Se usa `MiColaDatos` en lugar de `Queue` de Java para el Batch, porque el proyecto ya
  trae una cola enlazada propia y la rubrica suma valor cuando las estructuras se
  implementan manualmente.
- El Batch no registra cada inscripcion en la pila de deshacer. La razon es que una carga
  masiva puede contener muchas solicitudes y la guia pide procesarlas como lote; registrar
  cada una como operacion manual haria confuso el historial de deshacer/rehacer.
- Si una materia esta llena, `inscribirEstudianteEnMateria` mantiene el comportamiento
  general del sistema: agrega al estudiante a la cola de espera de la materia y reporta la
  solicitud como fallida por falta de cupo.
- Se acepta una primera linea de encabezado `idEstudiante,codigoMateria`. Tambien se limpia
  la marca BOM (`\uFEFF`) para evitar errores cuando el CSV se guarda como UTF-8 desde
  Excel u otros editores.

### Formato esperado del CSV

```csv
idEstudiante,codigoMateria
EST001,MATE01
EST002,MATE02
```

Cada solicitud se imprime con el formato `[actual/total] ID -> MATERIA -> Resultado`, y
las fallidas incluyen el motivo para cumplir el requisito de trazabilidad del enunciado.
