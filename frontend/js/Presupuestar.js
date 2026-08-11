// ============================================================
// Presupuestar.js — Flujo crear → confirmar pedido
// ------------------------------------------------------------
// 1er click:  POST /pedidos/crear      → botón "Confirmar pedido" (verde)
// 2do click:  PUT  /pedidos/confirmar  → botón "Confirmado!" (rojo, disabled)
// Muestra spinner en el botón durante cada fetch.
// ============================================================

let pedidoCreado = false;
let pedidoId = null;

document.getElementById("contactForm").addEventListener("submit", async (e) => {

    e.preventDefault();

    if (!pedidoCreado) {
        await crearPedido();
    } else {
        await confirmarPedido();
    }

});

async function crearPedido() {

    const usuarioId = Number(localStorage.getItem("usuarioId"));
    const token = localStorage.getItem("token");

    const productos = [
        pcBuild.cpu,
        pcBuild.gpu,
        pcBuild.motherboard,
        pcBuild.ram,
        pcBuild.storage
    ];

    // eliminar null
    const productosValidos = productos.filter(p => p !== null);

    const pedidoDetalles = productosValidos.map(producto => ({
        productoId: Number(producto.id),
        cantidad: 1
    }));

    const pedidoDTO = {
        usuarioId: usuarioId,
        pedidosDetalle: pedidoDetalles
    };

    console.log("DTO enviado:", pedidoDTO);

    const boton = document.getElementById("btn-submit");
    const textoOriginal = boton.textContent;

    // Estado de carga (spinner)
    boton.disabled = true;
    boton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {

        const response = await fetch("http://localhost:8080/pedidos/crear", {

            method: "POST",

            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },

            body: JSON.stringify(pedidoDTO)

        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        console.log("Respuesta backend:", data);

        mostrarMensaje("¡Presupuesto generado correctamente!", "success");

        pedidoCreado = true;
        pedidoId = data.idPedido;

        // Flujo: el botón pasa a "Confirmar pedido" (verde)
        boton.disabled = false;
        boton.textContent = "Confirmar pedido";
        boton.style.backgroundColor = "green";

        const resumen = document.getElementById("pc-total");

        resumen.innerHTML = `
            <div class="pedido-resumen-card">
                <h2>Resumen del pedido</h2>
                <p>Total: $${data.valorTotal}</p>
            </div>
        `;

    } catch (error) {

        console.error(error);

        mostrarMensaje(error.message, "error");

        // Restaurar el botón a su estado original
        boton.disabled = false;
        boton.innerHTML = textoOriginal;

    }

}

async function confirmarPedido() {

    const token = localStorage.getItem("token");

    const boton = document.getElementById("btn-submit");
    const textoOriginal = boton.textContent;

    // Estado de carga (spinner)
    boton.disabled = true;
    boton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {

        const response = await fetch(
            `http://localhost:8080/pedidos/confirmar/${pedidoId}`,
            {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            let mensaje = "No se pudo confirmar el pedido";
            try {
                const data = await response.json();
                if (data && data.message) {
                    mensaje = data.message;
                }
            } catch (e) {
                // respuesta sin cuerpo JSON
            }
            throw new Error(mensaje);
        }

        mostrarMensaje("¡Pedido confirmado correctamente!", "success");

        // Flujo: botón final "Confirmado!" (rojo, deshabilitado)
        boton.textContent = "Confirmado!";
        boton.style.backgroundColor = "red";
        boton.disabled = true;

    } catch (error) {

        console.error(error);

        mostrarMensaje(error.message, "error");

        // Restaurar el botón al estado "Confirmar pedido" (verde)
        boton.disabled = false;
        boton.innerHTML = textoOriginal;
        boton.style.backgroundColor = "green";

    }

}