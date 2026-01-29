### Statements / Reports (Account Statements)

El bounded context Statements se encarga de generar el estado de cuenta de una cuenta bancaria a partir del Ledger, que es la fuente de verdad contable del sistema. Su objetivo no es registrar movimientos ni modificar balances, sino consultar, resumir y presentar la información financiera de una cuenta de forma estructurada, tal como lo hace un banco real.

Statements calcula correctamente el saldo inicial (opening balance), total de créditos, total de débitos y saldo final (closing balance), además de listar los movimientos del periodo solicitado. A diferencia de mostrar simplemente el saldo actual, este bounded context permite reconstruir el comportamiento financiero histórico del cliente y entregar un documento utilizable para reportes, auditoría y consulta personal.

Todos los endpoints del módulo están protegidos con JWT y aplican validación de ownership, garantizando que solo el dueño de la cuenta pueda descargar o consultar sus estados de cuenta.

---

#### Endpoints

####  GET `/api/v1/statements/by-account/{accountId}`:

Este endpoint genera un estado de cuenta en formato JSON para una cuenta específica en un periodo definido por parámetros de fecha (por ejemplo from y to). El sistema toma las entradas contables del Ledger en ese rango y calcula el saldo inicial considerando todas las operaciones anteriores al inicio del rango, logrando un comportamiento realista de “opening balance”.

La respuesta incluye un resumen financiero del periodo y una lista detallada de movimientos, lo cual permite mostrar estados de cuenta directamente en una interfaz web o móvil sin necesidad de descargar un archivo.

---

#### GET `/api/v1/statements/by-account/{accountId}/pdf`:

Este endpoint genera y retorna un archivo PDF con el estado de cuenta de una cuenta bancaria para un rango de fechas solicitado. El PDF representa el documento bancario típico que un cliente puede descargar desde una banca por internet: incluye información del periodo, datos de cuenta, resumen de saldos y movimientos en forma tabular.

Este endpoint está orientado a escenarios de reporte y evidencia documental, permitiendo que el cliente conserve su historial en un formato portable.

---

#### GET `/api/v1/statements/by-account/{accountId}/pdf/all`:

Este endpoint genera un PDF de estado de cuenta sin requerir un rango de fechas. En lugar de limitarse a un periodo, el sistema incluye todos los movimientos históricos registrados en el Ledger para esa cuenta. En este caso, el saldo inicial se interpreta como cero y el saldo final se calcula como el acumulado total de créditos y débitos registrados.

Este endpoint es útil para demostraciones, auditorías internas o para clientes que desean descargar el historial completo de operaciones de su cuenta.
