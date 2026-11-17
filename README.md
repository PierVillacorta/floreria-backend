# 🌸 Proyecto Florería - API Backend

Servicio backend encargado de gestionar la lógica de negocio, autenticación (tradicional y OAuth con Google), 
control de acceso por roles, administración de catálogo e inventario, y procesamiento de órdenes de compra (boletas) para la plataforma de la florería.

---

## 👥 Integrantes
* **Pier Villacorta**
* **Elizabeth Troncoso**

---

## 🛠️ Tecnologías Utilizadas
* **Backend:** Spring Boot (Spring Web, Spring Data JPA, Spring Security)
* **Lenguaje:** Java 21
* **Autenticación y Seguridad:** JWT (JSON Web Tokens) + Google OAuth2
* **Documentación API:** Swagger / OpenAPI 3
* **Contenedores y Despliegue:** Docker & Docker Compose
* **Base de Datos:** PostgreSQL

---

## 🗄️ Arquitectura y Módulos del Sistema

El backend expone una API RESTful segura con persistencia en base de datos y control de permisos basado en roles (`CLIENTE`, `SELLER`, `ADMIN`):

* **🔐 Autenticación (`Auth`):** Registro de clientes, inicio de sesión con credenciales tradicionales y autenticacion mediante cuenta de Google, entregando un token **JWT** para las peticiones protegidas.
* **👥 Administración de Usuarios (`Users`):** Gestion completa de cuentas (creación con rol específico, edición de nombre/rol/estado y eliminacion) restringida al rol `ADMIN`.
* **🏷️ Categorías (`Categories`):** Organización del catálogo de flores y arreglos, con validacion de integridad al eliminar (solo permite borrar categorías sin productos asociados).
* **💐 Catálogo e Inventario (`Products`):** Consulta pública del catálogo y gestión administrativa (creación, actualización de datos/stock y eliminación).
* **🧾 Órdenes y Boletas (`Orders`):** Procesamiento de compras para clientes autenticados, historial personal (`/me`) y monitoreo global de ventas para roles `ADMIN` y `SELLER`.

---

## 📂 Estructura del Proyecto

```text
src/main/java/com/floreria/backend/
├── config/          # Configuración de Spring Security, JWT, CORS y OpenAPI/Swagger
├── controller/      # Controladores REST (/auth, /categories, /users, /products, /orders)
├── dto/             # Objetos de transferencia de datos (Request / Response)
├── exception/       # Manejo global de excepciones y respuestas HTTP
├── model/           # Entidades JPA y roles del sistema
├── repository/      # Interfaces de acceso a datos (Spring Data JPA)
└── service/         # Lógica de negocio y validaciones transaccionales
```

---

## 🐳 Instalación y Ejecución con Docker

1. **Clonar el repositorio:**
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd proyecto-floreria-backend
   ```

2. **Configurar variables de entorno:**
   Crea un archivo `.env` en la raíz del proyecto con la configuración de la base de datos, JWT y Google OAuth :
   Hay un ejemplo en el archivo `.env.example` para hacer este paso
   ```env
   DB_NAME=floreria_db
   DB_USER=admin
   DB_PASSWORD=secretpassword
   JWT_SECRET=tu_clave_secreta_jwt
   GOOGLE_CLIENT_ID=tu_client_id_de_google
   ```

4. **Levantar los contenedores:**
   ```bash
   docker-compose up --build -d
   ```

5. **Acceder a Swagger UI:**
   Una vez levantado el servicio, puedes probar todos los endpoints e ingresar tu token JWT en el botón **Authorize** desde:
   `http://localhost:8080/swagger-ui/index.html`
