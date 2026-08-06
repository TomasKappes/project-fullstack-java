function mostrarMensaje(texto, tipo) {

    const contenedor = document.getElementById("mensaje-ui");

    contenedor.textContent = "";

    contenedor.className = tipo;

    const icono = document.createElement("span");
    icono.className = "mensaje-icono";
    icono.textContent = tipo === "success" ? "\u2713" : tipo === "warning" ? "\u26A0" : "\u2715";

    const textoSpan = document.createElement("span");
    textoSpan.textContent = texto;

    contenedor.appendChild(icono);
    contenedor.appendChild(textoSpan);

    contenedor.style.display = "flex";

    setTimeout(() => {
        contenedor.style.display = "none";
    }, 9000);

}