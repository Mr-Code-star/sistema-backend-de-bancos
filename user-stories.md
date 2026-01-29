### EPICS

| Epic ID | Epic                                     |
| ------- | ---------------------------------------- |
| EP-01   | Gestión de Identidad y Seguridad (IAM)   |
| EP-02   | Gestión de Perfil del Cliente            |
| EP-03   | Gestión de Cuentas Bancarias             |
| EP-04   | Transferencias Bancarias                 |
| EP-05   | Pagos a Servicios (Payments & Merchants) |
| EP-06   | Registro Contable (Ledger)               |
| EP-07   | Estados de Cuenta y Reportes             |
| EP-08   | Notificaciones al Usuario                |
| EP-09   | Control de Límites Operativos            |
| EP-10   | Administración y Compliance              |


### User Stories

#### IAM


| ID | Título                     | Descripción                                                         | Criterio de Aceptación                                                                                                                                                     | Relación (Epic ID) |
| ------------------ | -------------------------- | ------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ |
| **US-IAM-01** | API RESTful – Registro de usuario                  | Como usuario quiero registrarme en la plataforma para poder acceder al sistema bancario.     | **Escenario: Registro de usuario**.<br>**Cuando** envío `POST /api/v1/iam/auth/register` con<br>`json`<br>{<br> "email": "[user@mail.com](mailto:user@mail.com)",<br> "password": "Password123!"<br>}<br>**Entonces** el sistema valida los datos, registra al usuario y devuelve `201 Created` con<br>`json`<br>{<br> "userId": "uuid",<br> "email": "[user@mail.com](mailto:user@mail.com)",<br> "status": "ACTIVE"<br>} | EP-01 |
| **US-IAM-02** | API RESTful – Login de usuario                     | Como usuario quiero autenticarme para obtener un token JWT y acceder a endpoints protegidos. | **Escenario: Login exitoso**.<br>**Cuando** envío `POST /api/v1/iam/auth/login` con<br>`json`<br>{<br> "email": "[user@mail.com](mailto:user@mail.com)",<br> "password": "Password123!"<br>}<br>**Entonces** el sistema valida las credenciales y devuelve `200 OK` con<br>`json`<br>{<br> "userId": "uuid",<br> "email": "[user@mail.com](mailto:user@mail.com)",<br> "token": "jwt-token"<br>}                           | EP-01 |
| **US-IAM-03** | API RESTful – Login con MFA                        | Como usuario quiero autenticarme usando MFA para aumentar la seguridad de mi cuenta.         | **Escenario: Login con MFA**.<br>**Dado que** el usuario tiene MFA habilitado.<br>**Cuando** envío `POST /api/v1/iam/auth/login` con<br>`json`<br>{<br> "email": "[user@mail.com](mailto:user@mail.com)",<br> "password": "Password123!",<br> "mfaCode": "123456"<br>}<br>**Entonces** el sistema valida el código MFA y devuelve `200 OK` con un token JWT válido.                                                        | EP-01 |
| **US-IAM-04** | API RESTful – Solicitar recuperación de contraseña | Como usuario quiero solicitar la recuperación de mi contraseña mediante correo electrónico.  | **Escenario: Solicitud de recuperación**.<br>**Cuando** envío `POST /api/v1/iam/password-recovery/request` con<br>`json`<br>{<br> "email": "[user@mail.com](mailto:user@mail.com)"<br>}<br>**Entonces** el sistema responde `200 OK` y, si el usuario existe, envía un correo con un enlace de recuperación.                                                                                                               | EP-01 |
| **US-IAM-05** | API RESTful – Restablecer contraseña               | Como usuario quiero cambiar mi contraseña usando un token de recuperación.                   | **Escenario: Reset de contraseña**.<br>**Dado que** el usuario posee un token válido.<br>**Cuando** envío `POST /api/v1/iam/password-recovery/reset` con<br>`json`<br>{<br> "token": "reset-token",<br> "newPassword": "NewPassword123!"<br>}<br>**Entonces** el sistema actualiza la contraseña y devuelve `200 OK` con<br>`json`<br>{<br> "message": "Password updated"<br>}                                             | EP-01 |
| **US-IAM-06** | API RESTful – Verificar MFA                        | Como usuario quiero verificar mi método MFA para activarlo.                                  | **Escenario: Verificación MFA**.<br>**Cuando** envío `POST /api/v1/iam/users/{id}/verify-mfa` con<br>`json`<br>{<br> "methodId": "uuid",<br> "code": "123456"<br>}<br>**Entonces** el sistema valida el código y devuelve `200 OK`.                                                                                                                                                                                     | EP-01 |
| **US-IAM-07** | API RESTful – Registrar método MFA para usuario | Como usuario quiero registrar un método de autenticación multifactor para proteger mi cuenta con un segundo factor de seguridad. | **Escenario: Registro de método MFA**.<br>**Dado que** el usuario está autenticado.<br>**Cuando** envío `POST /api/v1/iam/users/{id}/mfa-methods` con<br>`json`<br>{<br> "type": "AUTH_APP",<br> "destination": "user@auth-app"<br>}<br>**Entonces** el sistema registra el método MFA en estado no verificado y devuelve `201 Created` con<br>`json`<br>{<br> "methodId": "uuid",<br> "type": "AUTH_APP",<br> "verified": false<br>} | EP-01 |

