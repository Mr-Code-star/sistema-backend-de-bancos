###  Notifications (User Notifications)

El bounded context Notifications se encarga de gestionar la comunicación hacia el usuario respecto a eventos relevantes del sistema bancario. Su objetivo es garantizar que el cliente reciba información importante sobre operaciones y cambios de estado, como transferencias realizadas o fallidas, pagos completados, depósitos, retiros y acciones administrativas como congelamiento de cuentas.

Este módulo funciona de forma desacoplada respecto a los bounded contexts financieros. En lugar de que cada operación implemente su propia lógica de envío, el sistema centraliza la creación y entrega de notificaciones. Además, respeta las preferencias definidas por el cliente en Profile, lo que permite elegir canales como in-app, email o SMS según el tipo de evento.

Las notificaciones se almacenan como registros en base de datos (in-app) y pueden ser marcadas como leídas o archivadas, manteniendo historial y trazabilidad. Todos los endpoints están protegidos con JWT y validan ownership: un usuario solo puede visualizar o modificar sus propias notificaciones.

---

#### Endpoints

#### GET ´/api/v1/notifications/my´:

Este endpoint devuelve la lista de notificaciones asociadas al usuario autenticado. Se utiliza para mostrar la bandeja de notificaciones dentro de la aplicación bancaria. La respuesta incluye información como título, contenido, canal, tipo de evento, estado (UNREAD/READ/ARCHIVED), reference de correlación (por ejemplo, reference de una transferencia o pago) y fecha de creación.

El sistema determina el recipientId desde el JWT, evitando que un usuario consulte notificaciones ajenas.

---

#### PUT ´/api/v1/notifications/{id}/read´:

Este endpoint permite marcar una notificación como READ. En sistemas bancarios esto se usa para controlar el estado de “no leídas”, eliminando indicadores de alerta sin borrar el historial. La notificación se mantiene disponible para consulta futura, ya que la banca real suele conservar trazabilidad.

El sistema valida ownership: únicamente el dueño de la notificación puede cambiar su estado.

---

#### PUT ´/api/v1/notifications/{id}/archive´:

Este endpoint permite mover una notificación al estado ARCHIVED. Archivar no significa eliminar, sino ocultar la notificación de la bandeja principal, manteniendo el registro para auditoría e historial. Esto es importante en sistemas bancarios, donde los eventos relevantes no se eliminan, sino que se conservan como evidencia.

Al igual que el endpoint de lectura, el sistema valida que la notificación pertenezca al usuario autenticado.
