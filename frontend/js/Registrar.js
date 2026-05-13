const form = document.querySelector("form");

form.addEventListener("submit", async function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const username = document.getElementById("username").value;

    if (!email || !password || !username) {
        alert("Por favor completa todos los campos");
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


 if (!response.ok) {
            const errorText = await response.text();
            alert("Error: " + errorText);
            return;
        }

        alert("Usuario registrado correctamente");
        window.location.href = "login.html";

    } catch (error) {
        console.error("Error en registro:", error);
        alert("No se pudo registrar el usuario");
    }
});