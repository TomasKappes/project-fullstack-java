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
        usernameEl.textContent = localStorage.getItem("username") || "Usuario";
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