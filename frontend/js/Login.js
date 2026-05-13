const form = document.querySelector("form");

form.addEventListener("submit", async function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    if (!email || !password) {
        alert("Por favor completa todos los campos");
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

        if (!response.ok) {
            throw new Error("Credenciales incorrectas");
        }

        const data = await response.json();

        console.log("Login exitoso:", data);

        // Guardar token (si usás JWT)
        localStorage.setItem("token", data.token);
        localStorage.setItem("usuarioId", data.usuarioId);

        // Redirigir
        window.location.href = "menu.html";

    } catch (error) {
        console.error("Error en login:", error);
        alert("Email o contraseña incorrectos");
    }
});