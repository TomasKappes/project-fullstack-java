const form = document.querySelector("form");

form.addEventListener("submit", async function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const username = document.getElementById("username").value;

    if (!email || !password || !username) {
        mostrarMensaje("Por favor completa todos los campos", "warning");
        return;
    }

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
        mostrarMensaje(error.message,"error")
    }
});