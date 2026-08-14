// ============================================================
// Presupuestar.js — Flujo crear → confirmar pedido
// ------------------------------------------------------------
// 1er click:  POST /pedidos/crear      → botón "Confirmar pedido" (verde)
// 2do click:  PUT  /pedidos/confirmar  → botón "Confirmado!" (verde, disabled)
// Muestra spinner en el botón durante cada fetch.
// ============================================================

let pedidoCreado = false;
let pedidoId = null;

// ============================================================
// Estados visuales del botón #btn-submit
// ------------------------------------------------------------
// Declarar los estados en un solo lugar evita duplicar strings
// y garantiza que las 3 fases compartan el mismo aspecto base
// (btn-lg px-5 shadow-sm), cambiando solo el color mediante
// clases Bootstrap (btn-primary azul / btn-success verde).
// El estado "confirmado" usa el MISMO verde que "presupuestado":
// solo cambia el texto y se deshabilita.
// ============================================================

const ESTADO_BOTON_INICIAL = {
    clases: "btn btn-primary btn-lg px-5 shadow-sm",
    texto: "Verificar componentes"
};

const ESTADO_BOTON_PRESUPUESTADO = {
    clases: "btn btn-success btn-lg px-5 shadow-sm",
    texto: "Confirmar pedido"
};

const ESTADO_BOTON_CONFIRMADO = {
    clases: "btn btn-success btn-lg px-5 shadow-sm",
    texto: "Confirmado!"
};

// Spinner reutilizable durante los fetch (mismo markup que antes)
const SPINNER_HTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

// ============================================================
// Helper: cambia el estado visual del botón de forma centralizada
// ------------------------------------------------------------
// - clases:        string con las clases Bootstrap a aplicar
//                  (reemplaza TODO el className del botón)
// - texto:         texto visible del botón
// - deshabilitado: true/false para disabled
// - esHTML:        true si "texto" contiene HTML (ej. spinner);
//                  con false se usa textContent (más seguro)
// ============================================================
function cambiarEstadoBoton(clases, texto, deshabilitado, esHTML = false) {
    const boton = document.getElementById("btn-submit");

    boton.className = clases;
    boton.disabled = deshabilitado;

    if (esHTML) {
        boton.innerHTML = texto;
    } else {
        boton.textContent = texto;
    }
}

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

    // Estado de carga (spinner) — mantiene el azul de la fase inicial
    cambiarEstadoBoton(ESTADO_BOTON_INICIAL.clases, SPINNER_HTML, true, true);

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

        mostrarMensaje("¡Verificacion realizada con exito, componentes compatibles!", "success");

        pedidoCreado = true;
        pedidoId = data.idPedido;

        // Flujo: el botón pasa a "Confirmar pedido" (verde, habilitado)
        cambiarEstadoBoton(
            ESTADO_BOTON_PRESUPUESTADO.clases,
            ESTADO_BOTON_PRESUPUESTADO.texto,
            false
        );

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

        // Error en la creación → volver al estado inicial (azul "Verificar componentes")
        cambiarEstadoBoton(
            ESTADO_BOTON_INICIAL.clases,
            ESTADO_BOTON_INICIAL.texto,
            false
        );

    }

}

async function confirmarPedido() {

    const token = localStorage.getItem("token");

    // Estado de carga (spinner) — mantiene el verde de la fase presupuestada
    cambiarEstadoBoton(ESTADO_BOTON_PRESUPUESTADO.clases, SPINNER_HTML, true, true);

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

        // Éxito: el backend devuelve el PedidosResponseDTO (nombreUsuario,
        // fechaPedido, estadoPedido, valorTotal, idPedido). Se captura el
        // body para renderizar el recuadro "Detalles del pedido".
        const data = await response.json();

        mostrarMensaje("¡Pedido confirmado correctamente!", "success");

        renderizarDetallesPedido(data);

        // Flujo: botón final "Confirmado!" (verde, deshabilitado)
        cambiarEstadoBoton(
            ESTADO_BOTON_CONFIRMADO.clases,
            ESTADO_BOTON_CONFIRMADO.texto,
            true
        );

    } catch (error) {

        console.error(error);

        mostrarMensaje(error.message, "error");

        // En error el recuadro no debe quedar visible (ni con datos viejos)
        limpiarDetallesPedido();

        // Error en la confirmación → volver al estado "Confirmar pedido" (verde)
        cambiarEstadoBoton(
            ESTADO_BOTON_PRESUPUESTADO.clases,
            ESTADO_BOTON_PRESUPUESTADO.texto,
            false
        );

    }

}

// ============================================================
// Detalles del pedido — recuadro bajo el botón de confirmación
// ------------------------------------------------------------
// Se renderiza SOLO cuando el PUT /pedidos/confirmar/{id} responde
// 200 OK, leyendo el PedidosResponseDTO que devuelve el backend:
//   - nombreUsuario → nombre del usuario
//   - fechaPedido   → LocalDateTime ISO-8601 → dd/MM/yyyy HH:mm
//   - idPedido      → identificador del pedido (#n)
//   - valorTotal    → BigDecimal → $ con 2 decimales
//   - estadoPedido  → enum "CONFIRMADO" → "Confirmado"
// ============================================================

// Convierte "2026-08-14T15:30:00" en "14/08/2026 15:30".
// El ISO sin zona horaria se interpreta como hora LOCAL del navegador
// (especificación ES2015+), que es lo correcto aquí: el backend guarda
// LocalDateTime, es decir "hora de pared" sin zona.
function formatearFecha(fechaIso) {
    if (!fechaIso) return "—";

    const fecha = new Date(fechaIso);

    if (isNaN(fecha.getTime())) return "—"; // defensivo: ISO inválido

    const dia = String(fecha.getDate()).padStart(2, "0");
    const mes = String(fecha.getMonth() + 1).padStart(2, "0");
    const anio = fecha.getFullYear();
    const horas = String(fecha.getHours()).padStart(2, "0");
    const minutos = String(fecha.getMinutes()).padStart(2, "0");

    return `${dia}/${mes}/${anio} ${horas}:${minutos}`;
}

// "CONFIRMADO" → "Confirmado" (solo la primera letra en mayúscula)
function capitalizar(texto) {
    if (!texto) return "";
    return texto.charAt(0).toUpperCase() + texto.slice(1).toLowerCase();
}

// Escapa caracteres HTML antes de insertar texto con innerHTML.
// El nombre de usuario viene del backend y podría contener < > & que
// se interpretarían como markup si no se escaparan (XSS básico).
function escaparHTML(texto) {
    const div = document.createElement("div");
    div.textContent = texto == null ? "" : String(texto);
    return div.innerHTML;
}

// Renderiza el recuadro "Detalles del pedido" dentro de #detalles-pedido.
// El contenedor ya existe en menu.html (oculto con d-none) y usa el mismo
// lenguaje visual que #pc-total (p-3 bg-secondary rounded text-white).
function renderizarDetallesPedido(data) {

    const contenedor = document.getElementById("detalles-pedido");
    if (!contenedor) return; // defensivo: si el div no está, no romper nada

    const fecha = formatearFecha(data.fechaPedido);
    const total = typeof formatearPrecio === "function"
        ? formatearPrecio(data.valorTotal)
        : `$${Number(data.valorTotal).toFixed(2)}`;
    const estado = capitalizar(data.estadoPedido);

    contenedor.innerHTML = `
        <div class="pedido-resumen-card">
            <h6 class="text-primary fw-bold mb-2 pb-2 border-bottom border-secondary">Detalles del pedido</h6>

            <div class="d-flex justify-content-between mb-1">
                <span class="text-white-50">Usuario:</span>
                <span class="fw-bold">${escaparHTML(data.nombreUsuario)}</span>
            </div>

            <div class="d-flex justify-content-between mb-1">
                <span class="text-white-50">Fecha:</span>
                <span class="fw-bold">${fecha}</span>
            </div>

            <div class="d-flex justify-content-between mb-1">
                <span class="text-white-50">ID del pedido:</span>
                <span class="fw-bold">#${data.idPedido}</span>
            </div>

            <div class="d-flex justify-content-between mb-1">
                <span class="text-white-50">Coste total:</span>
                <span class="text-success fw-bold">${total}</span>
            </div>

            <div class="d-flex justify-content-between mb-1">
                <span class="text-white-50">Estado:</span>
                <span class="text-success fw-bold">${estado}</span>
            </div>
        </div>
    `;

    contenedor.classList.remove("d-none");
}

// Oculta y vacía el recuadro. Se invoca desde el catch de confirmarPedido()
// para que ante un error no quede visible un recuadro con datos de un
// intento anterior.
function limpiarDetallesPedido() {

    const contenedor = document.getElementById("detalles-pedido");
    if (!contenedor) return; // defensivo

    contenedor.innerHTML = "";
    contenedor.classList.add("d-none");
}
