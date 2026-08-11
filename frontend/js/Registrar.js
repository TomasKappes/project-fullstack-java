// ============================================================
// Registrar.js — Registro + validación visual de inputs
// ------------------------------------------------------------
// - Valida campos vacíos y formato de email (clase is-invalid).
// - Muestra spinner en el botón mientras dura el fetch.
// ============================================================

const form = document.querySelector("form");

const usernameInput = document.getElementById("username");
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
usernameInput.addEventListener("input", () => limpiarError(usernameInput));
emailInput.addEventListener("input", () => limpiarError(emailInput));
passwordInput.addEventListener("input", () => limpiarError(passwordInput));

form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const username = usernameInput.value.trim();
    const email = emailInput.value.trim();
    const password = passwordInput.value;

    // --- Validación ---
    let valido = true;

    if (!username) {
        mostrarError(usernameInput, "El nombre de usuario es obligatorio");
        valido = false;
    }

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
        const response = await fetch("http://localhost:8080/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password,
                email: email
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        mostrarMensaje("¡Usuario registrado correctamente!", "success");
        setTimeout(() => { window.location.href = "login.html"; }, 1500);

    } catch (error) {
        console.error("Error en registro:", error);
        mostrarMensaje(error.message, "error");
    } finally {
        boton.disabled = false;
        boton.innerHTML = textoOriginal;
    }
});