### IAM (Identity & Access Management)

El bounded context IAM es el encargado de gestionar la identidad, autenticación y seguridad de los usuarios dentro del sistema bancario. Este módulo es responsable de garantizar que solo usuarios legítimos puedan acceder a la plataforma y que las acciones sensibles se encuentren protegidas mediante mecanismos de seguridad como JWT, MFA y recuperación de contraseña.

IAM no maneja operaciones financieras ni datos bancarios; su responsabilidad se limita exclusivamente a la gestión de usuarios y control de acceso.

#### Endpoints 

##### POST `/api/v1/iam/auth/register`:

Este endpoint permite crear un nuevo usuario en el sistema. El usuario proporciona su correo electrónico y contraseña, los cuales son validados y procesados de forma segura. La contraseña nunca se almacena en texto plano, sino que se guarda como un hash utilizando un algoritmo de hashing seguro.

Al finalizar el proceso, el sistema registra al usuario con un estado inicial y deja la cuenta lista para iniciar sesión. Este endpoint representa el punto de entrada de nuevos clientes al sistema bancario.

---

##### POST `/api/v1/iam/auth/login`:  

Este endpoint autentica a un usuario existente. El usuario envía su correo electrónico y contraseña, los cuales son verificados contra la información almacenada en la base de datos. Si las credenciales son correctas, el sistema genera un JWT (JSON Web Token) que representa la sesión del usuario.

En caso de que el usuario tenga habilitado MFA (Multi-Factor Authentication), el login también requiere la validación de un código adicional generado por el método configurado (Authenticator App, Email o SMS). Solo cuando todas las validaciones son correctas, el sistema devuelve el token JWT junto con la información básica del usuario.

Este token es utilizado posteriormente para acceder a todos los endpoints protegidos del sistema.

---

##### POST `/api/v1/iam/password-recovery/request`:  

Este endpoint permite iniciar el proceso de recuperación de contraseña cuando un usuario la ha olvidado. El usuario ingresa su correo electrónico y el sistema, sin revelar si dicho correo existe o no, responde de manera genérica.

Si el correo corresponde a un usuario válido, el sistema genera un token temporal con tiempo de expiración limitado (por ejemplo, 15 minutos), lo guarda de forma segura (hasheado) y envía un correo electrónico con un enlace de recuperación que contiene dicho token.

Este diseño evita ataques de enumeración de usuarios y garantiza la seguridad del proceso.

---

#### POST `/api/v1/iam/password-recovery/reset`:

Este endpoint completa el proceso de recuperación de contraseña. El usuario envía el token recibido por correo junto con una nueva contraseña.

El sistema valida que el token exista, no haya expirado y no haya sido utilizado previamente. Si todas las condiciones se cumplen, la contraseña del usuario es actualizada de forma segura y el token queda marcado como usado, impidiendo su reutilización.

---

#### GET `/api/v1/iam/users/{id}`:

Este endpoint permite obtener la información básica de un usuario a partir de su identificador único (userId). Se utiliza principalmente para fines internos del sistema, como validaciones cruzadas entre bounded contexts o consultas administrativas.

El endpoint devuelve datos como el correo electrónico del usuario y su estado actual, sin exponer información sensible como contraseñas o secretos MFA.

---

#### GET `/api/v1/iam/users/{id}/status`:

Este endpoint devuelve exclusivamente el estado actual del usuario, el cual puede ser `ACTIVE`, `BLOCKED` o `PENDING`.

Se utiliza para validar si un usuario puede autenticarse o realizar determinadas acciones dentro del sistema. Por ejemplo, un usuario con estado BLOCKED no debería poder iniciar sesión ni ejecutar operaciones protegidas.

Este endpoint es útil tanto para lógica interna como para escenarios administrativos.

---

#### GET `api/v1/iam/users/by-email`:

Este endpoint permite obtener información de un usuario a partir de su correo electrónico. Se utiliza en escenarios donde el sistema necesita resolver un userId a partir de un email, por ejemplo durante procesos internos de validación, soporte o flujos administrativos.

Este endpoint no debe ser utilizado directamente por usuarios finales, ya que forma parte de la lógica interna del sistema IAM.

---

#### POST `/api/v1/iam/users/{id}/mfa-methods`:

Este endpoint permite registrar un nuevo método de autenticación multifactor para un usuario específico. El método puede ser de tipo `EMAIL` o `SMS`.

Al registrar el método, este queda asociado al usuario pero no se considera válido hasta que sea verificado. Este diseño garantiza que los métodos MFA no puedan ser utilizados sin una validación previa, aumentando la seguridad del sistema.

---

#### GET `/api/v1/iam/users/{id}/mfa-methods`:

Este endpoint devuelve la lista de métodos MFA asociados a un usuario, indicando el tipo de método y si se encuentra verificado o no.

Se utiliza para que el usuario o el sistema puedan conocer qué métodos de autenticación están disponibles y activos en la cuenta, permitiendo una gestión clara de la seguridad.

---

#### POST `/api/v1/iam/users/{id}/verify-mfa`:

Este endpoint permite verificar un método de autenticación multifactor previamente registrado. El usuario envía un código generado por el método configurado (por ejemplo, un código enviado por email o SMS).

El sistema valida el código y, si es correcto, marca el método MFA como verificado. A partir de ese momento, el método puede ser utilizado en procesos de autenticación, como el login con MFA
