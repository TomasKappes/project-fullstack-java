# 🖥️ PC Builder - Full Stack Application

Aplicación full stack que permite a los usuarios armar configuraciones de PC mediante la selección de componentes compatibles.

Este proyecto se enfoca en el desarrollo de un backend robusto con lógica de negocio real, aplicando buenas prácticas de arquitectura, seguridad y diseño de APIs.

---

## 🚀 Tecnologías

### Backend

* Java
* Spring Boot
* Spring Security
* JWT (JSON Web Tokens)
* JPA / Hibernate
* MySQL
* Maven

### Frontend

* HTML5
* CSS3
* Bootstrap
* JavaScript

---

## 🔐 Seguridad

* Autenticación basada en JWT
* Contraseñas hasheadas con BCrypt
* Protección de endpoints mediante Spring Security

---

## 🧩 Funcionalidades principales

* Registro y login de usuarios
* Autenticación y autorización con JWT
* Creación de configuraciones de PC
* Validación de compatibilidad entre componentes (ej: CPU vs Motherboard)
* Manejo global de excepciones con respuestas estructuradas
* Uso de DTOs para desacoplar entidades de la API
* Datos iniciales precargados (categorías y productos)

---

## 🧠 Lógica de negocio

El sistema incluye validaciones personalizadas para garantizar la compatibilidad entre componentes, evitando combinaciones inválidas dentro de una configuración de PC.

---

## 🏗️ Arquitectura

El backend sigue una arquitectura en capas:

* **Controller** → manejo de requests HTTP
* **Service** → lógica de negocio
* **Repository** → acceso a datos
* **DTO** → transferencia segura de datos

Se aplican principios de separación de responsabilidades y código mantenible.

---

## 🧪 Testing

* Endpoints testeados manualmente con Postman

---

## 🚧 Estado del proyecto

El proyecto se encuentra en desarrollo activo. Próximas mejoras:

* Integración completa con frontend
* Implementación de roles (USER / ADMIN)
* Tests unitarios e integración
* Deploy de la aplicación

---

## 🎯 Objetivo

Este proyecto forma parte de mi formación como desarrollador backend, con foco en:

* Arquitectura profesional
* Seguridad en aplicaciones web
* Buenas prácticas en APIs REST
* Desarrollo de lógica de negocio real
