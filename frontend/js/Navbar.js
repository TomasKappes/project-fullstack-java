// ============================================================
// Navbar.js — Usuario + cierre de sesión (menu.html)
// ------------------------------------------------------------
// - Muestra el nombre de usuario en la navbar si está guardado.
// - Botón "Cerrar sesión": limpia localStorage y vuelve a login.
// Usa DOMContentLoaded para no depender del orden de los scripts.
// ============================================================

document.addEventListener("DOMContentLoaded", () => {

    // Mostrar el nombre de usuario (o "Usuario" genérico)
    const usernameEl = document.getElementById("navbar-username");
    if (usernameEl) {
        const nombreGuardado = localStorage.getItem("username");
        if (nombreGuardado) {
            usernameEl.textContent = nombreGuardado;
        } else {
            cargarNombreUsuario(usernameEl);
        }
    }

    // Cerrar sesión
    const logoutBtn = document.getElementById("btn-logout");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.removeItem("token");
            localStorage.removeItem("usuarioId");
            localStorage.removeItem("username");
            window.location.href = "login.html";
        });
    }

});

// ============================================================
// cargarNombreUsuario — Obtiene el nombre real del usuario
// ------------------------------------------------------------
// El login (AuthResponse) solo devuelve token + usuarioId, no el
// nombre. Este endpoint GET /users/{id} (protegido) devuelve el
// UsuarioResponseDTO con el campo "nombre". Se guarda en
// localStorage para no repetir el fetch en cada visita.
// ============================================================
async function cargarNombreUsuario(usernameEl) {

    const usuarioId = localStorage.getItem("usuarioId");
    const token = localStorage.getItem("token");

    if (!usuarioId || !token) {
        return; // sin sesión: se queda el texto por defecto
    }

    try {
        const response = await fetch(`http://localhost:8080/users/${usuarioId}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("No se pudo obtener el usuario");
        }

        const data = await response.json();

        if (data.nombre) {
            localStorage.setItem("username", data.nombre);
            usernameEl.textContent = data.nombre;
        }

    } catch (error) {
        console.error("Error al cargar el nombre de usuario:", error);
        // Se queda con "Usuario" por defecto; no rompe la UI
    }

}