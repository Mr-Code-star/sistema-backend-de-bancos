### Limits (Operational Limits Management)

El bounded context Limits se encarga de controlar y restringir las operaciones financieras diarias que un usuario puede realizar dentro del sistema bancario. Su objetivo principal es prevenir abusos, errores operativos y comportamientos de riesgo, aplicando límites realistas similares a los que existen en bancos reales.

A diferencia de otros bounded contexts, Limits no expone endpoints públicos para el cliente, ya que sus decisiones se aplican de forma interna durante la ejecución de operaciones como transferencias, pagos y retiros. El usuario no interactúa directamente con Limits; en su lugar, otros módulos consultan este bounded context antes de ejecutar una operación financiera.

---

#### Funcionamiento general de Limits

Limits opera evaluando dos dimensiones principales por usuario y por tipo de operación:

1) **Monto diario acumulado**, es decir, cuánto dinero ha movido el usuario en el día.

2) **Cantidad diaria de operaciones**, es decir, cuántas veces ha realizado una operación específica en el día.
   
Estos controles se aplican de manera independiente para cada tipo de operación, como transferencias, pagos y retiros.

Cada vez que un usuario intenta realizar una operación financiera, el bounded context correspondiente (Transfers, Payments o Accounts) consulta a Limits antes de continuar. Limits evalúa si la operación propuesta haría que el usuario exceda alguno de los límites definidos. Si la operación se encuentra dentro de los límites, Limits autoriza la ejecución y registra el consumo del límite. Si la operación excede los límites, la solicitud es rechazada antes de que se mueva dinero.

---

#### Integración con otros Bounded Contexts

Limits se integra de forma interna con los siguientes módulos:

- **Transfers:** antes de ejecutar una transferencia, se valida que el usuario no haya superado su límite diario de transferencias.

- **Payments:** antes de debitar una cuenta para un pago, se valida que el usuario no exceda los límites diarios de pagos.

- **Accounts:** los retiros también pueden estar sujetos a límites diarios, evitando extracciones excesivas en un solo día.

- **Admin / Compliance:** los administradores pueden resetear límites diarios o definir límites especiales (overrides) para usuarios específicos.

Esta integración garantiza que las reglas de límites se apliquen de forma consistente en todo el sistema.

---

#### Periodicidad y reseteo automático

Los límites en el sistema están definidos con una periodicidad diaria. Esto significa que el consumo de límites se reinicia automáticamente cada día sin intervención manual. El sistema identifica el periodo diario utilizando una clave de fecha (por ejemplo, día UTC actual), de modo que al cambiar de día el usuario puede volver a operar normalmente dentro de los límites establecidos.

Este comportamiento refleja el funcionamiento real de los límites diarios en aplicaciones bancarias.

---

#### Overrides y control administrativo

Aunque los usuarios no interactúan directamente con Limits, el bounded context Admin / Compliance puede modificar su comportamiento mediante acciones administrativas. Los administradores pueden definir overrides de límites para usuarios específicos, aumentando el monto o la cantidad diaria permitida según criterios como verificación KYC, nivel del cliente o decisiones de soporte.

De esta manera, Limits soporta tanto reglas generales del sistema como excepciones controladas y auditables.

---
