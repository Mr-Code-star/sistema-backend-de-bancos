### Profile (Customer Profile Management)

El bounded context Profile se encarga de gestionar la información personal del cliente, sus direcciones y sus preferencias de notificación. Este módulo representa al cliente como persona, separando claramente estos datos de la identidad y seguridad manejadas por IAM y de las operaciones financieras manejadas por otros bounded contexts.

Profile garantiza que cada usuario solo pueda acceder y modificar su propio perfil, utilizando el userId obtenido desde el token JWT para validar ownership

#### Endpoints

#### POST `/api/v1/profiles`:

Este endpoint permite crear el perfil de un cliente autenticado. El sistema asocia automáticamente el perfil al usuario identificado por el token JWT, evitando que un usuario pueda crear perfiles para terceros.

La creación del perfil es un paso necesario para habilitar completamente la experiencia bancaria del cliente, ya que concentra los datos personales requeridos por otros módulos del sistema.

---

#### GET `/api/v1/profile/me`:

Este endpoint devuelve la información completa del perfil asociado al usuario autenticado. Se utiliza para mostrar los datos personales del cliente dentro de la aplicación, como nombres, documentos, direcciones registradas y preferencias actuales.

El acceso está protegido por JWT y el sistema valida que el perfil consultado pertenezca al usuario que realiza la solicitud.

---

#### GET `/api/v1/profiles/{profileId}`:

Este endpoint permite obtener un perfil específico a partir de su identificador. Está pensado principalmente para uso interno del sistema o para escenarios administrativos, donde se requiere acceder al perfil de un cliente sin depender directamente del contexto del usuario autenticado.

El endpoint se encuentra protegido y no permite el acceso indiscriminado a perfiles de otros clientes.

---

#### PUT `/api/v1/profiles/{profileId}`:

Este endpoint permite actualizar la información general del perfil del cliente, como datos personales o información de contacto. El sistema valida que el usuario autenticado sea el propietario del perfil antes de permitir la modificación.

Este endpoint refleja un comportamiento común en sistemas bancarios, donde el cliente puede mantener su información personal actualizada de manera controlada.

---

#### POST `/api/v1/profiles/{profileId}/addresses`:

Este endpoint permite agregar una nueva dirección al perfil del cliente. Cada dirección queda asociada al perfil y puede ser utilizada posteriormente para comunicaciones o validaciones internas.

---

#### PUT `/api/v1/profiles/{profileId}/addresses/{addressId}`:

Este endpoint permite modificar los datos de una dirección previamente registrada. El sistema valida que la dirección pertenezca al perfil indicado y que el usuario tenga permisos para realizar la modificación.

---

#### DELETE `/api/v1/profiles/{profileId}/addresses/{addressId}`:

Este endpoint permite eliminar una dirección del perfil del cliente. Se utiliza cuando una dirección deja de ser válida o necesaria. El sistema mantiene la integridad del perfil y evita eliminar direcciones que no pertenezcan al cliente autenticado.

---

#### PUT `/api/v1/profiles/{profileId}/addresses/{addressId}/primary`:

Este endpoint permite establecer una dirección específica como dirección principal del perfil. El sistema garantiza que solo exista una dirección principal por perfil, actualizando automáticamente el estado de las demás direcciones si es necesario.

---

#### GET `/api/v1/profiles/me/preferences/notifications`:

Este endpoint devuelve las preferencias actuales del cliente relacionadas con las notificaciones. Las preferencias indican qué canales (in-app, email, SMS) están habilitados para cada tipo de evento, como transferencias, pagos o alertas de seguridad.

Estas preferencias son consumidas por el bounded context Notifications para decidir cómo entregar los mensajes al cliente.

---

#### PUT `/api/v1/profiles/me/preferences/notifications`:

Este endpoint permite al cliente modificar sus preferencias de notificación. El usuario puede habilitar o deshabilitar canales específicos según sus necesidades, personalizando la forma en que recibe información del sistema.
