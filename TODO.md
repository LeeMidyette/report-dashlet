## Mejoras en la funcionalidad ##
  * Exportar datos de la tabla a fichero.
  * Permitir guardar los resultados (gráfico y tabla) en un espacio de Alfresco.
  * Ofrecer un filtrado orientado a sitios, en lugar de cualquier ruta en el repositorio.
  * Poner la etiqueta de cada mes en el gráfico de línea al agrupar por fecha.
  * Permitir hacer drill-down en el gráfico de nube de puntos al agrupar por fecha. Se podrá pinchar en un punto (agrupación mes/año) para desglosarla en días.
  * Permitir que existan múltiples filtros de la misma propiedad, y que estos entre sí sean de tipo OR.
  * Permitir gráficas de agrupaciones comparativas entre fecha de modificación y creación.

## Mejoras en la implementación y usabilidad del dashlet ##
  * Ajaxify las urls para evitar reinicio al recargar la página.
  * Permitir añadir configuración persistente básica al dashlet, como por ejemplo el filtro de búsqueda manual por defecto.
  * Mejorar el código javascript, orientándolo a objetos, y utilizando jQuery Controller para aislar y asignar funcionalidad.
  * Añadir más idiomas a la localización.

## Mejoras en los webscripts de Alfresco ##
  * Mejorar la eficiencia en el conteo en la implementación de los webscripts de alfresco.
  * Cachear los resultados del webscript.
  * Localizar los mensajes de error devueltos por los webscripts y los mensajes de error de las excepciones.