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
