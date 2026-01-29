### Transfers (Transfer Management)

El bounded context Transfers se encarga de gestionar las transferencias internas entre cuentas dentro del sistema bancario. Este módulo implementa un flujo completo y realista de transferencia, incluyendo validaciones bancarias, control de idempotencia, registro contable y notificaciones al cliente.

Transfers no solo crea un registro de transferencia, sino que orquesta el movimiento real del dinero: debita la cuenta origen, acredita la cuenta destino, registra las entradas contables correspondientes en el Ledger y notifica al usuario según sus preferencias configuradas en Profile. Adicionalmente, se integra con Limits para evitar que un usuario exceda límites diarios de transferencias.

Un aspecto clave del módulo es la idempotencia, lo que significa que si el cliente reintenta la misma transferencia (por ejemplo por red o doble click) usando la misma reference, el sistema no ejecuta el cobro dos veces y devuelve el mismo resultado previamente registrado.

---

#### Enpoints

#### POST `/api/v1/transfers`:

Este endpoint permite crear una transferencia entre dos cuentas del sistema. El usuario envía la cuenta de origen, la cuenta de destino, el monto, la moneda y una reference única que funciona como clave de idempotencia.

Antes de ejecutar la transferencia, el sistema valida que la cuenta de origen pertenezca al usuario autenticado (ownership), que ambas cuentas existan, que estén en estado operativo y que la moneda sea consistente. Luego aplica el control de límites diarios y, si todo es válido, realiza el débito y crédito correspondientes.

Al finalizar, el sistema registra la transferencia como COMPLETED si la operación fue exitosa, o como FAILED si se detecta algún error como saldo insuficiente, cuentas inválidas, moneda incorrecta o límites excedidos. Una transferencia exitosa genera dos entradas contables en el Ledger, una como DEBIT en la cuenta origen y otra como CREDIT en la cuenta destino, ambas correlacionadas con la misma reference.

---

#### GET `/api/v1/transfers/{transferId}`:

Este endpoint devuelve la información detallada de una transferencia específica, incluyendo origen, destino, monto, moneda, estado y motivo de fallo si existiera.

El acceso se encuentra protegido por JWT y el sistema valida que el usuario autenticado tenga permisos para visualizarla, normalmente verificando que la transferencia esté asociada a una cuenta perteneciente al usuario.

---

#### GET `/api/v1/transfers/by-account/{accountId}`:

Este endpoint devuelve el historial de transferencias relacionadas con una cuenta específica. Se incluyen transferencias en las que la cuenta participó como origen o como destino.

El sistema aplica ownership para evitar filtración de información: únicamente el dueño de la cuenta puede consultar su historial. Este endpoint es útil para mostrar movimientos de transferencias dentro de la aplicación bancaria y para auditar el comportamiento transaccional del usuario.

---


