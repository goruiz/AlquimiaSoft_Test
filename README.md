Documentación

1. Tecnologías empleadas
   •Lenguaje de programación: se utiliza Java 24, que es su última versión.
   •Framework: la aplicación está construida con Spring Boot 3.
   •Persistencia de datos: se emplea Spring Data JPA junto con Hibernate como proveedor ORM, permitiendo mapear objetos Java a tablas relacionales y simplificar el acceso a la base de datos mediante repositorios.
   •Base de datos: el sistema utiliza PostgreSQL.
   •Gestión de esquemas: se implementa Liquibase 4 para definir y versionar el esquema de la base de datos mediante archivos de migración YAML, que se ejecutan automáticamente al iniciar la aplicación.
   •Documentación de API: se integra springdoc-openapi-ui, que genera automáticamente documentación interactiva mediante Swagger UI basada en las anotaciones del código.
   •Pruebas automatizadas: se utilizan JUnit 5 para pruebas unitarias y Mockito para simular dependencias durante la ejecución de pruebas de lógica de negocio.
   •Control de versiones: el código fuente está gestionado mediante Git.

2. Arquitectura y patrones
   La solución adopta una arquitectura hexagonal (Ports & Adapters), también conocida como Clean Architecture, para aislar el núcleo de negocio de los detalles de infraestructura y presentación. Este enfoque se materializa en los siguientes principios y patrones:

   1. Capas definidas
      Dominio: Contiene los modelos de negocio (Customer, Address, IdentificationType) y las interfaces (ports) que describen las operaciones que el mundo exterior puede realizar sobre el dominio.
      Aplicación: Orquesta los casos de uso a través de Servicios transaccionales, los cuales son: CustomerService y AddressService. Aquí se aplican las reglas de negocio que combinan entidades y garantizan la consistencia.
      Infraestructura: Incluye los adaptadores que implementan las interfaces del dominio usando JPA y PostgreSQL, así como los controladores REST y los DTO que transforman las solicitudes y respuestas HTTP.
   2. Inversión de dependencias
      Las capas superiores dependen de abstracciones, nunca de implementaciones concretas. Así, el dominio define puertos (CustomerInterface, AddressInterface) y la infraestructura provee adaptadores que se conectan a la base de datos sin que el núcleo se vea afectado por cambios tecnológicos.
   3. Principios SOLID
      Single Responsibility: cada clase tiene un motivo único de cambio (ej. GlobalExceptionHandler solo gestiona errores).
      Open/Closed: para incorporar otro tipo de persistencia (p. ej. MongoDB) bastaría con añadir un nuevo adaptador; las capas superiores permanecerían intactas.
      Liskov, Interface Segregation y Dependency Inversion se respetan al definir contratos pequeños y claros, y al inyectar dependencias mediante constructor.
   4. Patrón DTO
      Los Data Transfer Objects aíslan el contrato HTTP de las entidades JPA, evitando filtraciones de la capa de persistencia y permitiendo evolución independiente de la API.
   5. Repositorio como adaptador
      Los repositorios JPA (CustomerJpaRepository, AddressJpaRepository) actúan como adaptadores salientes que materializan los puertos de acceso a datos definidos en el dominio. Su implementación es reemplazable sin tocar la lógica de negocio.
   6. Transactional Service Layer
      Los servicios de aplicación marcan los límites transaccionales con @Transactional, garantizando atomicidad y coherencia. Validaciones como “único número de identificación” se centralizan aquí.
   7. Migraciones controladas
      El patrón database-as-code con Liquibase versiona el esquema y permite despliegues repetibles; las migraciones se ejecutan al arrancar la aplicación sin intervención manual.
   8. Cross-cutting concerns
      Excepciones: GlobalExceptionHandler aplica el patrón Controller Advice, proporcionando respuestas JSON uniformes.
      Documentación: springdoc-openapi genera Swagger UI de forma automática sin contaminar el dominio.

3. Modelo de Base de Datos y esquema físico
   No es necesario crear las tablas manualmente. Al ejecutar la aplicación, Liquibase se encarga de aplicar automáticamente los scripts de migración, generando la estructura de base de datos completa sin intervención adicional.
   El modelo de datos está compuesto por dos entidades principales: customer y address. Cada cliente tiene un identificador único, un tipo de identificación (que puede ser cédula o RUC), un número de identificación, nombre completo, correo electrónico y número de celular. Esta información se almacena en una tabla llamada customer.
   La entidad dirección representa una ubicación asociada a un cliente y se almacena en la tabla address. Cada dirección incluye información como provincia, ciudad, línea de dirección y un indicador booleano que señala si se trata de la dirección principal (matriz). Un cliente puede tener múltiples direcciones, pero solo una de ellas puede ser marcada como matriz.
   Existe una relación uno a muchos entre cliente y direcciones, donde la tabla address contiene una clave foránea que apunta a la tabla customer. Esta relación está configurada con eliminación en cascada, lo que significa que al eliminar un cliente, se eliminan también todas sus direcciones asociadas.
   Los tipos de identificación (cédula y RUC) se representan en el sistema mediante un enumerador (enum) que se almacena como una cadena de texto en la base de datos. Esto permite distinguir entre personas naturales y jurídicas, facilitando la validación y el uso posterior en los procesos de facturación.
   Las migraciones Liquibase que crean estas tablas se encuentran en db/changelog/changes/001-create-customer-and-address.yaml.

3.2. Uso del enum IdentificationType
public enum IdentificationType {
CEDULA, // Persona natural (10 dígitos)
RUC // Registro Único de Contribuyentes (13 dígitos)
}
Persistencia: @Enumerated(EnumType.STRING) guarda el literal (CEDULA | RUC) en la columna identification_type.
Solo se admiten estos dos valores.
La longitud y formato concreto del número de identificación pueden validarse en un Validator personalizado si se requiere reforzar la lógica tributaria ecuatoriana.

5. Entidades de dominio
   Cómo ya se mencionó, el sistema está compuesto por dos entidades de dominio principales: Customer y Address.
   La entidad Customer representa a una persona natural o jurídica registrada en el sistema con fines de facturación. Cada cliente debe tener una única dirección matriz y puede contar con cero o más direcciones adicionales, lo que permite reflejar sucursales, oficinas o puntos de contacto alternativos.
   La entidad Address describe una dirección física asociada a un cliente. Esta puede ser la matriz o una sucursal, y se diferencia mediante el atributo booleano main, que indica si se trata de la dirección principal. Esta estructura permite modelar de forma flexible los distintos puntos de atención o facturación de un cliente.

6. API's REST

   1. Buscar y obtener un listado de clientes

   GET /api/customers?query={texto}

   Descripción
   Permite buscar clientes por número de identificación o por nombre, devolviendo todos los que coincidan parcial o totalmente con el texto proporcionado. Incluye los datos de la dirección matriz de cada cliente.

   Parámetros de consulta

   query (opcional): texto a buscar en el campo identificationNumber o en fullName. Si no se especifica, retorna todos los clientes.

   Ejemplo de respuesta exitosa (200 OK)

   [
   {
   "id": "uuid-customer",
   "identificationType": "CEDULA",
   "identificationNumber": "0912345678",
   "fullName": "Juan Pérez",
   "email": "juan@ejemplo.com",
   "mobileNumber": "0999999999",
   "mainAddress": {
   "id": "uuid-direccion",
   "province": "Pichincha",
   "city": "Quito",
   "addressLine": "Av. Amazonas 300",
   "main": true
   },
   "extraAddresses": [ "direcciones" ]
   }
   ]

   2. Crear un nuevo cliente con dirección matriz

   POST /api/customers

   Descripción
   Registra un nuevo cliente junto con su dirección matriz. Valida que no exista otro cliente con el mismo número de identificación.

   Cuerpo de la petición (CustomerCreateRequest)

   {
   "identificationType": "RUC",
   "identificationNumber": "1799999999001",
   "fullName": "Comercial El Éxito S.A.",
   "email": "ventas@elexito.com",
   "mobileNumber": "0999999999",
   "address": {
   "province": "Pichincha",
   "city": "Quito",
   "addressLine": "Av. Amazonas N34-77 y La Niña",
   "main": true
   }
   }
   Respuestas

   201 Created: cliente creado correctamente. Retorna el objeto CustomerDto con todos sus datos y la dirección matriz.

   409 Conflict: ya existe un cliente con ese número de identificación.

   3. Editar los datos de un cliente

   PUT /api/customers/{id}

   Descripción
   Actualiza los datos básicos de un cliente existente (no modifica direcciones). Verifica que el nuevo número de identificación, si cambia, no duplique a otro cliente.

   Parámetros de ruta

   id: UUID del cliente a modificar.

   Cuerpo de la petición (CustomerUpdateRequest)

   {
   "identificationType": "CEDULA",
   "identificationNumber": "0912345678",
   "fullName": "Juan Pérez S.A.",
   "email": "ventas@juan.com",
   "mobileNumber": "022345678"
   }
   Respuestas

   200 OK: retorna el CustomerDto actualizado.

   404 Not Found: no existe un cliente con ese ID.

   409 Conflict: el nuevo número de identificación ya está en uso por otro cliente.

   4. Eliminar un cliente

   DELETE /api/customers/{id}

   Descripción
   Elimina un cliente y todas sus direcciones asociadas.

   Parámetros de ruta

   id: UUID del cliente a eliminar.

   Respuestas

   204 No Content: eliminación exitosa.

   404 Not Found: no existe un cliente con ese ID.

   5. Registrar una nueva dirección para un cliente

   POST /api/customers/{customerId}/addresses

   Descripción
   Agrega una dirección adicional a un cliente existente. Si se marca main: true, la nueva dirección pasa a ser la matriz y la anterior matriz queda como extra.

   Parámetros de ruta

   customerId: UUID del cliente al que se le agregará la dirección.

   Cuerpo de la petición (AddressCreateRequest)

   {
   "province": "Guayas",
   "city": "Guayaquil",
   "addressLine": "Av. 9 de Octubre 100",
   "main": false
   }
   Respuestas

   201 Created: retorna el AddressDto con los datos de la dirección guardada.

   404 Not Found: no existe un cliente con ese customerId.

   6. Listar las direcciones de un cliente

   GET /api/customers/{customerId}/addresses

   Descripción
   Devuelve todas las direcciones asociadas a un cliente, incluyendo la matriz y las adicionales.

   Parámetros de ruta

   customerId: UUID del cliente cuyas direcciones se desean consultar.

   Ejemplo de respuesta exitosa (200 OK)

   [
   {
   "id": "uuid-direccion-matriz",
   "province": "Pichincha",
   "city": "Quito",
   "addressLine": "Av. Amazonas 300",
   "main": true
   },
   {
   "id": "uuid-otra-direccion",
   "province": "Guayas",
   "city": "Guayaquil",
   "addressLine": "Av. 9 de Octubre 100",
   "main": false
   }
   ]

7. Validaciones y manejo de errores
   Se utiliza Bean Validation (jakarta.validation).
   Clase GlobalExceptionHandler que transforma las excepciones en respuestas JSON uniformes:

   {
   "timestamp": "2025-05-01T19:20:31",
   "status": 409,
   "error": "Conflict",
   "message": "Customer with identification already exists",
   "path": "/api/customers"
   }

8. Pruebas automatizadas
   Unitarias (TDD) con Mockito:
   CustomerServiceTest valida reglas de unicidad y actualización.
   AddressServiceTest comprueba que no se puedan registrar direcciones para clientes inexistentes.

   Integración:
   @DataJpaTest con Testcontainers PostgreSQL.
   @SpringBootTest + MockMvc para verificar los contratos REST y la serialización de enums.

   El proyecto incluye dos tipos de pruebas, que se encuentran en la ruta src/test:

   1. Pruebas de la capa de aplicación (src/test/application)
      CustomerServiceTest:

      Utiliza Mockito para simular el CustomerInterface y validar los casos de uso de CustomerService.
      La búsqueda (search) devuelve la lista esperada.
      La creación (create) registra un nuevo cliente cuando el número de identificación es único, y lanza IllegalStateException si ya existe.
      La actualización (update) modifica correctamente los campos, detecta cuando el cliente no existe (IllegalArgumentException) o cuando el nuevo número de identificación ya está en uso (IllegalStateException).
      El borrado (delete) invoca la eliminación en el puerto correspondiente.

      AddressServiceTest:
      Simula tanto CustomerInterface como AddressInterface para probar AddressService.
      Verifica que addAddress agrega direcciones solo si el cliente existe, y lanza IllegalArgumentException en caso contrario.
      Comprueba que listAddresses retorna la lista de direcciones asociadas a un cliente.

   2. Pruebas de los controladores REST (src/test/infraestructure/web/Controllers)

      AddressControllerTest y CustomerControllerTest usan @WebMvcTest junto con MockMvc y @MockBean para simular los servicios. Estas pruebas ejercitan los endpoints HTTP y comprueban:
      Códigos de respuesta (por ejemplo, 200 OK, 201 Created, 204 No Content).
      Formato y contenido de las respuestas JSON (identificadores, valores de campos).
      Correcta interpretación de parámetros de ruta y de consulta, así como el manejo de los objetos de petición.

   Con esta combinación de tests unitarios en la capa de servicio y tests de integración ligera en la capa de web, se asegura que la lógica de negocio se comporte según lo esperado y que los endpoints REST expongan el contrato definido.

9. Ejecución local

   1. Requisitos: Java 24, PostgreSQL con base llamada “mi_negocio”, usuario “postgres”, contraseña: “pass123”. En esta sección se debe cambiar el nombre de la base de datos, el usuario y contraseña correspondientes de acuerdo a la máquina local en la que se ejecute, para hacer este cambio debe acceder al script “application.properties” e ir a las líneas siguientes:

   spring.datasource.url=jdbc:postgresql://localhost:5432/mi_negocio
   spring.datasource.username=postgres
   spring.datasource.password=pass123

   2. Para poder acceder a Swagger UI se debe ir a la ruta: http://localhost:8080/swagger-ui.html.
