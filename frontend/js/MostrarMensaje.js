function mostrarMensaje(texto, tipo) {

    const contenedor = document.getElementById("mensaje-ui");

    contenedor.textContent = texto;

    contenedor.className = tipo;

    contenedor.style.display = "block";

    setTimeout(() => {
        contenedor.style.display = "none";
    }, 9000);

}