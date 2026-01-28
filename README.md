# Sistema de Bancos Platform

## Summary
Este es un mini proyecto de un simulador de sistema de bancos desarrollado con Java, Spring Boot Framework, y Spring Data JPA en MySQL DataBase.
También ilustra la configuración de la documentación open-api y la integración con Swagger UI.

## Features
- RESTful API
- OpenAPI Documentation
- Swagger UI
- Spring Boot Framework
- Spring Data JPA
- Validation
- MySQL Database
- Domain-Driven Design
- Spring Security
  
## Configuracion .env

Crear un archivo de variables de entornos con estas credenciales

```ini
# .env
MAIL_USERNAME=PONER TU CORREO ELECTRONICO;
MAIL_PASSWORD=PONER TU CONTRASEÑA DE APLICACION DE GMAIL;
VONAGE_API_KEY=PONER LA API KEY DE VONAGE;
VONAGE_API_SECRET=PONER LA API SECRET DE VONAGE;
VONAGE_SENDER_ID=SistemaBancos;
JWT_SECRET=PONER TU JWT;
APP_EMAIL_SENDER=PONER TU CORREO ELECTRONICO;
APP_EMAIL_SENDER_NAME=Sistema de Bancos;
JWT_EXP_MINUTES=60

```

## 📌 Características principales

- Autenticación segura con JWT

- MFA (Email y SMS)

- Recuperación de contraseña por email con token temporal

- Gestión de cuentas bancarias (ACTIVE / FROZEN / CLOSED)

- Transferencias internas con idempotency

- Pagos a servicios (agua, luz, internet, universidades)

- Ledger como fuente de verdad contable

- Estados de cuenta en JSON y PDF

- Sistema de notificaciones (in-app, email, SMS)

- Control de límites diarios (monto y cantidad)

- Admin / Compliance con auditoría completa

-----

### IAM (Identity & Access Management)

El bounded context IAM es el encargado de gestionar la identidad, autenticación y seguridad de los usuarios dentro del sistema bancario. Este módulo es responsable de garantizar que solo usuarios legítimos puedan acceder a la plataforma y que las acciones sensibles se encuentren protegidas mediante mecanismos de seguridad como JWT, MFA y recuperación de contraseña.

IAM no maneja operaciones financieras ni datos bancarios; su responsabilidad se limita exclusivamente a la gestión de usuarios y control de acceso.

#### Endpoints 

##### POST `/api/v1/iam/auth/register`:

Este endpoint permite crear un nuevo usuario en el sistema. El usuario proporciona su correo electrónico y contraseña, los cuales son validados y procesados de forma segura. La contraseña nunca se almacena en texto plano, sino que se guarda como un hash utilizando un algoritmo de hashing seguro.

Al finalizar el proceso, el sistema registra al usuario con un estado inicial y deja la cuenta lista para iniciar sesión. Este endpoint representa el punto de entrada de nuevos clientes al sistema bancario.

##### POST `/api/v1/iam/auth/login`:  

Este endpoint autentica a un usuario existente. El usuario envía su correo electrónico y contraseña, los cuales son verificados contra la información almacenada en la base de datos. Si las credenciales son correctas, el sistema genera un JWT (JSON Web Token) que representa la sesión del usuario.

En caso de que el usuario tenga habilitado MFA (Multi-Factor Authentication), el login también requiere la validación de un código adicional generado por el método configurado (Authenticator App, Email o SMS). Solo cuando todas las validaciones son correctas, el sistema devuelve el token JWT junto con la información básica del usuario.

Este token es utilizado posteriormente para acceder a todos los endpoints protegidos del sistema.

##### POST `/api/v1/iam/password-recovery/request`:  

Este endpoint permite iniciar el proceso de recuperación de contraseña cuando un usuario la ha olvidado. El usuario ingresa su correo electrónico y el sistema, sin revelar si dicho correo existe o no, responde de manera genérica.

Si el correo corresponde a un usuario válido, el sistema genera un token temporal con tiempo de expiración limitado (por ejemplo, 15 minutos), lo guarda de forma segura (hasheado) y envía un correo electrónico con un enlace de recuperación que contiene dicho token.

Este diseño evita ataques de enumeración de usuarios y garantiza la seguridad del proceso.

#### POST `/api/v1/iam/password-recovery/reset`:

Este endpoint completa el proceso de recuperación de contraseña. El usuario envía el token recibido por correo junto con una nueva contraseña.

El sistema valida que el token exista, no haya expirado y no haya sido utilizado previamente. Si todas las condiciones se cumplen, la contraseña del usuario es actualizada de forma segura y el token queda marcado como usado, impidiendo su reutilización.

