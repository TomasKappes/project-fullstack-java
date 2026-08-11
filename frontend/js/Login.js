// ============================================================
// Login.js — Autenticación + validación visual de inputs
// ------------------------------------------------------------
// - Valida campos vacíos y formato de email (clase is-invalid).
// - Muestra spinner en el botón mientras dura el fetch.
// - Guarda token, usuarioId y (si el backend lo devuelve) username.
// ============================================================

const form = document.querySelector("form");

const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// --- Validación visual de inputs ---

function mostrarError(input, mensaje) {
    input.classList.add("is-invalid");
    const feedback = input.closest(".form-floating").querySelector(".invalid-feedback");
    if (feedback) {
        feedback.textContent = mensaje;
    }
}

function limpiarError(input) {
    input.classList.remove("is-invalid");
}

// Al corregir el campo, se quita el estado de error
emailInput.addEventListener("input", () => limpiarError(emailInput));
passwordInput.addEventListener("input", () => limpiarError(passwordInput));

form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    // --- Validación ---
    let valido = true;

    if (!email) {
        mostrarError(emailInput, "El email es obligatorio");
        valido = false;
    } else if (!EMAIL_REGEX.test(email)) {
        mostrarError(emailInput, "Formato de email inválido");
        valido = false;
    }

    if (!password) {
        mostrarError(passwordInput, "La contraseña es obligatoria");
        valido = false;
    }

    if (!valido) {
        return;
    }

    // --- Estado de carga (spinner) ---
    const boton = form.querySelector("button[type='submit']");
    const textoOriginal = boton.textContent;

    boton.disabled = true;
    boton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        console.log("Login exitoso:", data);

        // Guardar token y usuario (si usás JWT)
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioId", data.usuarioId);

        // El backend hoy NO devuelve username (AuthResponse = token + usuarioId).
        // Guardamos el username SOLO si algún día el backend lo incluye.
        if (data.username) {
            localStorage.setItem("username", data.username);
        }

        // Mostrar mensaje de éxito y redirigir
        mostrarMensaje("¡Inicio de sesión correcto!", "success");
        setTimeout(() => { window.location.href = "menu.html"; }, 1500);

    } catch (error) {

        console.error("Error en login:", error);
        mostrarMensaje(error.message, "error");

    } finally {

        // Restaurar el botón (éxito o error)
        boton.disabled = false;
        boton.innerHTML = textoOriginal;

    }
});