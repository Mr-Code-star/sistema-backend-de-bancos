### Accounts (Accounts Management)

El bounded context Accounts se encarga de administrar las cuentas bancarias del sistema. Una cuenta representa el contenedor lógico del dinero del cliente y mantiene información esencial como su saldo, moneda, tipo y estado. Este módulo aplica reglas bancarias fundamentales, por ejemplo: no permitir movimientos si la cuenta está congelada, no permitir retiros sin saldo suficiente y mantener consistencia en el manejo del balance.

Accounts trabaja junto con otros bounded contexts. Por ejemplo, Transfers y Payments utilizan cuentas para debitar o acreditar dinero, Ledger registra los movimientos contables derivados de estas operaciones y Limits puede restringir operaciones por día. En todo momento se aplica ownership: un usuario solo puede operar sobre cuentas que le pertenecen, validando el userId extraído desde el JWT.

#### Endpoints 

#### POST `/api/v1/accounts`:

Este endpoint permite crear una nueva cuenta bancaria para el usuario autenticado. La cuenta se crea con un identificador interno (accountId) y un número de cuenta público (accountNumber). Al momento de apertura, la cuenta inicia con estado ACTIVE y balance en cero, lista para recibir depósitos o realizar operaciones posteriores según las reglas del sistema.

El usuario no envía el ownerId en el request; el backend lo toma directamente del token JWT para asegurar que un usuario no pueda crear cuentas a nombre de terceros.

---

#### POST `/api/v1/accounts/{accountId}/deposit`:

Este endpoint permite depositar dinero en una cuenta específica. El sistema valida que la cuenta exista, que pertenezca al usuario autenticado y que la cuenta se encuentre en un estado operativo (ACTIVE). Si la validación es correcta, el balance se incrementa en el monto indicado.

Además, esta operación genera trazabilidad: se registran los movimientos correspondientes en el Ledger y se generan notificaciones para el usuario, dependiendo de las preferencias configuradas en Profile.

---

#### POST `/api/v1/accounts/{accountId}/withdraw`:

Este endpoint permite retirar dinero desde una cuenta específica. Antes de ejecutar la operación, el sistema valida que la cuenta pertenezca al usuario autenticado, que la cuenta esté ACTIVE y que exista saldo suficiente. Si el monto solicitado excede el balance disponible, la operación se rechaza.

Al igual que el depósito, un retiro genera registros contables en el Ledger y puede generar notificaciones, respetando las preferencias del usuario. Si tienes configurado Limits para retiros, este endpoint también queda sujeto a límites diarios.

---

#### GET `/api/v1/accounts/{accountId}`:

Este endpoint devuelve la información de una cuenta bancaria específica, incluyendo su número de cuenta, tipo, estado, moneda y balance. El endpoint está protegido y aplica ownership: solamente el dueño de la cuenta puede acceder a esa información.

Esta consulta se utiliza para mostrar el detalle de una cuenta dentro de la aplicación.

---

#### GET `/api/v1/accounts/my`:

Este endpoint devuelve todas las cuentas asociadas al usuario autenticado. Representa la funcionalidad típica de “Mis cuentas” en un sistema bancario, donde el cliente puede ver todas las cuentas abiertas bajo su usuario, con sus balances y datos principales.

El backend determina el usuario desde el token JWT, sin necesidad de recibir un ownerId en la URL, evitando vulnerabilidades de acceso indebido.

---

#### GET `/api/v1/accounts/by-number/{accountNumber}`:

Este endpoint permite buscar una cuenta a partir de su número de cuenta público. Es útil en escenarios donde se necesita validar una cuenta por su identificador bancario visible (por ejemplo, en transferencias o validaciones internas).

El endpoint se encuentra protegido y aplica ownership: un usuario no puede consultar por número de cuenta información perteneciente a un tercero si no tiene permisos.


