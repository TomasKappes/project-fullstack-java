document.getElementById("contactForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const nombre = document.getElementById("nombre").value;
    const email = document.getElementById("email").value;
    const mensaje = document.getElementById("mensaje").value;

    const data = {
        nombre,
        email,
        mensaje,
        build: pcBuild
    };

    const res = await fetch("http://localhost:8080/contacto", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    const mensajeUsuario = document.getElementById("mensaje-usuario");

    if (res.ok) {
        mensajeUsuario.textContent = "Mensaje enviado con éxito. Te responderemos pronto.";
        mensajeUsuario.classList.remove("text-danger");
        mensajeUsuario.classList.add("text-success");

        e.target.reset();
    } else {
        mensajeUsuario.textContent = "Hubo un error enviando la solicitud.";
        mensajeUsuario.classList.remove("text-success");
        mensajeUsuario.classList.add("text-danger");
    }
});
