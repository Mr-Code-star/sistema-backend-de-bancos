### Payments (Payment Management)

El bounded context Payments se encarga de procesar pagos realizados por el cliente desde una cuenta bancaria hacia un proveedor de servicios o comercio. En un escenario real, esto equivale a pagar servicios como luz, agua, internet o pensiones universitarias, utilizando saldo disponible en una cuenta.

Payments implementa un flujo completo y realista: valida ownership de la cuenta origen, aplica control de límites diarios (Limits), consulta a un proveedor externo para validar el pago, debita el dinero si el proveedor aprueba la operación, registra el movimiento contable en el Ledger y notifica al usuario según sus preferencias configuradas en Profile. Además, utiliza idempotencia mediante reference para evitar pagos duplicados si el cliente reintenta la misma operación.

---

#### POST `/api/v1/payments`:

Este endpoint permite crear un pago desde una cuenta del usuario autenticado hacia un merchant identificado por merchantCode. El request incluye una reference única (idempotency key), el fromAccountId, la categoría/tipo de pago, la moneda, el monto y un customerRef que representa el identificador del cliente en el proveedor (por ejemplo, código de alumno en universidades o número de suministro en luz/agua).

Antes de ejecutar el pago, el sistema valida que la cuenta exista y pertenezca al usuario autenticado, verifica que la moneda sea compatible y aplica el control de límites diarios. Luego, se realiza una validación con el “merchant externo simulado” (MerchantResource). Si el merchant responde éxito, recién se ejecuta el débito sobre la cuenta y se registra un asiento contable tipo DEBIT en el Ledger con fuente PAYMENT. Finalmente, el pago queda marcado como COMPLETED y se generan notificaciones al usuario. Si el merchant rechaza o se detecta saldo insuficiente, el pago se marca como FAILED y se notifica el motivo.

---

#### GET `/api/v1/payments/by-reference/{reference}`:

Este endpoint permite consultar el estado de un pago a partir de su reference. Su finalidad principal es soportar idempotencia y reintentos seguros: si el cliente vuelve a enviar la misma solicitud de pago con la misma reference, el sistema no ejecuta un segundo débito, sino que devuelve la operación ya registrada.

Este endpoint también es útil para mostrar al cliente el resultado de un pago luego de una reconexión o reintento.

---

#### GET `/api/v1/payments/by-account/{accountId}`:

Este endpoint devuelve el historial de pagos realizados desde una cuenta específica. Se utiliza para mostrar el “historial de pagos” dentro de la aplicación bancaria. El sistema valida ownership para evitar que un usuario consulte pagos de cuentas ajenas.

---

### Merchant (Merchant / External Provider Simulation)

En el proyecto, el componente MerchantResource representa un proveedor externo simulado. En un banco real, los proveedores de servicios (ENEL, SEDAPAL, Movistar, universidades) suelen exponer servicios de validación o confirmación para aceptar pagos. En este proyecto, esa integración se simula con endpoints internos para mantener un entorno controlado y reproducible.

Es importante entender que el usuario final no debería consumir directamente estos endpoints en producción; en el sistema, estos endpoints son consumidos por el gateway (HttpMerchantGateway) como si se tratara de un servicio externo.

---

####  GET `/api/v1/merchant/categories`:

Este endpoint devuelve las categorías disponibles de proveedores, por ejemplo: ELECTRICITY, WATER, INTERNET, UNIVERSITY. Se utiliza para poblar el catálogo de pagos y facilitar que el usuario seleccione el tipo de servicio.

---

####  GET `/api/v1/merchant/merchants`:

Este endpoint devuelve la lista de empresas disponibles para una categoría dada. Por ejemplo, en electricidad puede devolver ENEL y Luz del Sur. Sirve para que el sistema muestre opciones al usuario y permita seleccionar un merchantCode válido.

---

####  POST `/api/v1/merchant/pay`:

Este endpoint simula la validación del pago por parte del proveedor. Recibe información como merchantCode, reference, customerRef, moneda y monto, y responde con éxito o fallo. La lógica simula reglas por categoría, por ejemplo validación de formato del código alumno para universidades o número de suministro para servicios.

Payments utiliza esta respuesta para decidir si procede con el débito real y el registro contable, reproduciendo el comportamiento típico de integración con proveedores externos.
