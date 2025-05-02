Documentación

1. Tecnologías empleadas
    •Lenguaje de programación: se utiliza Java 24, que es su última versión.
    •Framework: la aplicación está construida con Spring Boot 3.
    •Persistencia de datos: se emplea Spring Data JPA junto con Hibernate como proveedor ORM,       permitiendo mapear objetos Java a tablas relacionales y simplificar el acceso a la base de datos mediante repositorios.
    •Base de datos: el sistema utiliza PostgreSQL.
    •Gestión de esquemas: se implementa Liquibase 4 para definir y versionar el esquema de la base de datos mediante archivos de migración YAML, que se ejecutan automáticamente al iniciar la aplicación.
    •Documentación de API: se integra springdoc-openapi-ui, que genera automáticamente documentación interactiva mediante Swagger UI basada en las anotaciones del código.
    •Pruebas automatizadas: se utilizan JUnit 5 para pruebas unitarias y Mockito para simular dependencias durante la ejecución de pruebas de lógica de negocio.
    •Control de versiones: el código fuente está gestionado mediante Git.

2. Arquitectura y patrones
    La solución adopta una arquitectura hexagonal (Ports & Adapters), también conocida como Clean Architecture, para aislar el núcleo de negocio de los detalles de infraestructura y presentación. Este enfoque se materializa en los siguientes principios y patrones:
    1.	Capas definidas
        	Dominio: Contiene los modelos de negocio (Customer, Address, IdentificationType) y las interfaces (ports) que describen las operaciones que el mundo exterior puede realizar sobre el dominio.
        	Aplicación: Orquesta los casos de uso a través de Servicios transaccionales, los cuales son: CustomerService y AddressService. Aquí se aplican las reglas de negocio que combinan entidades y garantizan la consistencia.
        	Infraestructura: Incluye los adaptadores que implementan las interfaces del dominio usando JPA y PostgreSQL, así como los controladores REST y los DTO que transforman las solicitudes y respuestas HTTP.
    2.	Inversión de dependencias
            Las capas superiores dependen de abstracciones, nunca de implementaciones concretas. Así, el dominio define puertos (CustomerInterface, AddressInterface) y la infraestructura provee adaptadores que se conectan a la base de datos sin que el núcleo se vea afectado por cambios tecnológicos.
    3.	Principios SOLID
            Single Responsibility: cada clase tiene un motivo único de cambio (ej. GlobalExceptionHandler solo gestiona errores).
            Open/Closed: para incorporar otro tipo de persistencia (p. ej. MongoDB) bastaría con añadir un nuevo adaptador; las capas superiores permanecerían intactas.
            Liskov, Interface Segregation y Dependency Inversion se respetan al definir contratos pequeños y claros, y al inyectar dependencias mediante constructor.
    4.	Patrón DTO
        Los Data Transfer Objects aíslan el contrato HTTP de las entidades JPA, evitando filtraciones de la capa de persistencia y permitiendo evolución independiente de la API.
    5.	Repositorio como adaptador
        Los repositorios JPA (CustomerJpaRepository, AddressJpaRepository) actúan como adaptadores salientes que materializan los puertos de acceso a datos definidos en el dominio. Su implementación es reemplazable sin tocar la lógica de negocio.
    6.	Transactional Service Layer
        Los servicios de aplicación marcan los límites transaccionales con @Transactional, garantizando atomicidad y coherencia. Validaciones como “único número de identificación” se centralizan aquí.
    7.	Migraciones controladas
        El patrón database-as-code con Liquibase versiona el esquema y permite despliegues repetibles; las migraciones se ejecutan al arrancar la aplicación sin intervención manual.
    8.	Cross-cutting concerns
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
    CEDULA,  // Persona natural (10 dígitos)
    RUC      // Registro Único de Contribuyentes (13 dígitos)
    }
    Persistencia: @Enumerated(EnumType.STRING) guarda el literal (CEDULA | RUC) en la columna identification_type.
    Solo se admiten estos dos valores.
    La longitud y formato concreto del número de identificación pueden validarse en un Validator personalizado si se requiere reforzar la lógica tributaria ecuatoriana.


5. Entidades de dominio
    Cómo ya se mencionó, el sistema está compuesto por dos entidades de dominio principales: Customer y Address.
    La entidad Customer representa a una persona natural o jurídica registrada en el sistema con fines de facturación. Cada cliente debe tener una única dirección matriz y puede contar con cero o más direcciones adicionales, lo que permite reflejar sucursales, oficinas o puntos de contacto alternativos.
    La entidad Address describe una dirección física asociada a un cliente. Esta puede ser la matriz o una sucursal, y se diferencia mediante el atributo booleano main, que indica si se trata de la dirección principal. Esta estructura permite modelar de forma flexible los distintos puntos de atención o facturación de un cliente.
6. Contratos REST
    El sistema expone una serie de API’s REST que permiten gestionar clientes y sus direcciones, utilizando solicitudes en formato JSON.
    
    GET /api/customers?query={texto}
    Permite buscar clientes por nombre o número de identificación. Devuelve un listado de coincidencias junto con sus respectivas direcciones principales.
    Código de respuesta: 200 OK.
    
    POST /api/customers
    Crea un nuevo cliente junto con su dirección matriz. Se requiere enviar un objeto JSON con los datos del cliente y la dirección. Valida que no exista otro cliente con el mismo número de identificación.
    Códigos de respuesta: 201 Created si se guarda correctamente, 409 Conflict si ya existe.
    Cuerpo esperado: CustomerCreateRequest.
    
    PUT /api/customers/{id}
    Actualiza los datos de un cliente existente. No modifica las direcciones. También valida la unicidad del número de identificación.
    Códigos de respuesta: 200 OK, 404 Not Found si el cliente no existe, 409 Conflict si hay duplicidad.
    Cuerpo esperado: CustomerUpdateRequest.
    
    DELETE /api/customers/{id}
    Elimina un cliente por su ID, incluyendo todas sus direcciones asociadas.
    Códigos de respuesta: 204 No Content si se elimina correctamente, 404 Not Found si no se encuentra el cliente.
    
    POST /api/customers/{customerId}/addresses
    Añade una nueva dirección adicional para el cliente especificado.
    Códigos de respuesta: 201 Created si se guarda correctamente, 404 Not Found si el cliente no existe.
    Cuerpo esperado: AddressCreateRequest.
    
    GET /api/customers/{customerId}/addresses
    Devuelve todas las direcciones asociadas a un cliente, incluyendo la matriz y las adicionales.
    Código de respuesta: 200 OK.


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

    1.	Pruebas de la capa de aplicación (src/test/application)
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


    2.	Pruebas de los controladores REST (src/test/infraestructure/web/Controllers)
        
        AddressControllerTest y CustomerControllerTest usan @WebMvcTest junto con MockMvc y @MockBean para simular los servicios. Estas pruebas ejercitan los endpoints HTTP y comprueban:
        Códigos de respuesta (por ejemplo, 200 OK, 201 Created, 204 No Content).
        Formato y contenido de las respuestas JSON (identificadores, valores de campos).
        Correcta interpretación de parámetros de ruta y de consulta, así como el manejo de los objetos de petición.

    Con esta combinación de tests unitarios en la capa de servicio y tests de integración ligera en la capa de web, se asegura que la lógica de negocio se comporte según lo esperado y que los endpoints REST expongan el contrato definido.

10. Ejecución local
    1.	Requisitos: Java 24, PostgreSQL con base llamada “mi_negocio”, usuario “postgres”, contraseña: “pass123”. En esta sección se debe cambiar el nombre de la base de datos, el usuario y contraseña correspondientes de acuerdo a la máquina local en la que se ejecute, para hacer este cambio debe acceder al script “application.properties” e ir a las líneas siguientes:

    spring.datasource.url=jdbc:postgresql://localhost:5432/mi_negocio
    spring.datasource.username=postgres
    spring.datasource.password=pass123

    2.	Para poder acceder a Swagger UI se debe ir a la ruta: http://localhost:8080/swagger-ui.html.

