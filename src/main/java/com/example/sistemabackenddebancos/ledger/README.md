### Ledger (Transactions / Accounting Ledger)

El bounded context Ledger representa el libro contable del sistema bancario y funciona como la fuente de verdad para la trazabilidad financiera. A diferencia de Accounts, que mantiene el saldo actual de una cuenta, Ledger registra cada movimiento como una entrada contable inmutable, ya sea un DEBIT (salida) o un CREDIT (entrada). Este enfoque es el que se utiliza en sistemas bancarios reales porque permite auditoría, reconstrucción histórica y generación de reportes como estados de cuenta.

Ledger se alimenta automáticamente desde otros bounded contexts: depósitos y retiros en Accounts, transferencias en Transfers y pagos en Payments. Cada operación genera entradas en el ledger con información como fecha/hora, tipo (DEBIT/CREDIT), fuente (TRANSFER/PAYMENT/DEPOSIT/WITHDRAW), monto, moneda y una reference que permite correlación.

---

#### Endpoints

####  GET `/api/v1/ledger/by-account/{accountId}`:

Este endpoint devuelve el historial de movimientos contables asociados a una cuenta específica. Se utiliza para mostrar el “historial de movimientos” del cliente y también para alimentar la generación de estados de cuenta.

El sistema aplica validación de ownership: únicamente el dueño de la cuenta, identificado por el JWT, puede consultar sus movimientos. En la respuesta se listan las entradas ordenadas por fecha, permitiendo ver de forma auditada todas las operaciones que afectaron el saldo.


####  GET `/api/v1/ledger/by-reference/{reference}`:

Este endpoint devuelve todas las entradas contables asociadas a una misma reference. Se utiliza principalmente para correlacionar operaciones compuestas. Un ejemplo típico es una transferencia interna, donde una sola reference genera dos entradas: un DEBIT en la cuenta origen y un CREDIT en la cuenta destino. De la misma forma, un pago puede ser rastreado por su reference para verificar el asiento contable generado.

Este endpoint también se encuentra protegido por JWT y el sistema valida que el usuario tenga permiso para ver esos registros, verificando ownership sobre las cuentas involucradas en las entradas retornadas.
