### Admin / Compliance (Backoffice & Control)

El bounded context Admin / Compliance representa el conjunto de funcionalidades administrativas y de control interno del sistema bancario. Su objetivo es permitir que un usuario con rol ADMIN pueda ejecutar acciones que un cliente normal no debería poder realizar, tales como congelar cuentas, cerrar cuentas, restablecer límites diarios y administrar límites especiales por usuario.

Este bounded context es clave desde el punto de vista bancario porque introduce conceptos reales de compliance, seguridad operativa y auditoría. Toda acción administrativa queda registrada en un log de auditoría, de modo que sea posible rastrear quién realizó una acción, sobre qué recurso, en qué momento y con qué razón.

Todos los endpoints de Admin están protegidos con JWT y requieren que el usuario tenga ROLE_ADMIN. Si el token pertenece a un usuario normal, el sistema devuelve acceso denegado.

---

#### Endpoints

#### PUT `/api/v1/admin/accounts/{accountId}/freeze`:

Este endpoint permite al administrador cambiar el estado de una cuenta a FROZEN. Congelar una cuenta implica que el cliente no podrá realizar operaciones financieras sobre ella, como transferencias, pagos o retiros. Esta acción se utiliza en escenarios reales de banca como sospecha de fraude, investigaciones internas o medidas preventivas por seguridad.

Al ejecutarse, además de actualizar el estado de la cuenta, el sistema registra una acción administrativa en el historial de auditoría.

---

#### PUT `/api/v1/admin/accounts/{accountId}/unfreeze`:

Este endpoint permite al administrador revertir el congelamiento de una cuenta y devolverla a estado ACTIVE. Una vez desbloqueada, la cuenta recupera la capacidad de operar normalmente, permitiendo nuevamente pagos, transferencias y retiros.

Al igual que el congelamiento, esta acción queda registrada en el log de auditoría para trazabilidad.

---

#### PUT `/api/v1/admin/accounts/{accountId}/close`:

Este endpoint permite al administrador cerrar una cuenta bancaria, cambiando su estado a CLOSED. Una cuenta cerrada se considera inhabilitada de forma permanente desde el punto de vista operativo. En el sistema, el cierre debe respetar reglas bancarias como la imposibilidad de cerrar cuentas con saldo distinto de cero, lo cual evita inconsistencias contables.

Esta acción se usa por ejemplo cuando un cliente solicita cierre formal o cuando el banco decide cerrar una cuenta por política interna. El evento también queda auditado.

----

#### DELETE `/api/v1/admin/limits/{userId}/today`:

Este endpoint permite al administrador restablecer el consumo de límites diarios de un usuario para el día actual. En la práctica, esto significa que el usuario podrá volver a realizar operaciones que anteriormente fueron bloqueadas por exceder límites diarios de monto o cantidad.

Este endpoint es especialmente útil en soporte y compliance cuando se requiere habilitar nuevamente a un cliente por excepción aprobada. La operación se aplica al día actual (periodo diario) y puede considerarse un “reset operativo” de los límites.

---

#### PUT ´/api/v1/admin/limits/{userId}/override´:

Este endpoint permite al administrador definir límites especiales para un usuario específico. En lugar de utilizar los límites por defecto del sistema, el usuario tendrá valores personalizados para una operación determinada, por ejemplo mayor monto diario permitido para transferencias si el cliente tiene un nivel superior de verificación o KYC.

Este mecanismo es típico en banca real, donde clientes verificados o premium pueden acceder a límites más altos. La acción se registra en auditoría como parte del control de compliance.

---

#### GET `/api/v1/admin/actions`:

Este endpoint devuelve el historial de acciones administrativas registradas por el sistema. Permite listar eventos como congelamiento, desbloqueo, cierre de cuentas, reset de límites y creación de overrides.

Este endpoint forma la base de un dashboard administrativo, proporcionando visibilidad completa para auditoría y revisión interna, lo cual es esencial en sistemas financieros reales. Su finalidad es garantizar trazabilidad y responsabilidad sobre las acciones realizadas por administradores.

---
