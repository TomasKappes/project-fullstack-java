

const form = document.querySelector("form");

form.addEventListener("submit", async function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    if (!email || !password) {
        mostrarMensaje("Por favor completa todos los campos", "warning");
        return;
    }

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

        // Guardar token (si usás JWT)
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioId", data.usuarioId);

        // Mostrar mensaje de éxito y redirigir
        mostrarMensaje("¡Inicio de sesión correcto!", "success");
        setTimeout(() => { window.location.href = "menu.html"; }, 1500);

    } catch (error) {

        console.error("Error en login:", error);
        mostrarMensaje(error.message,"error")
    }
});