// ============================================================
// Actualizar.js — Resumen del carrito con precios
// ------------------------------------------------------------
// Escribe en #pc-summary el detalle de componentes con su
// precio y el total parcial. Habilita #btn-submit solo cuando
// hay CPU + Motherboard + RAM (mínimo exigido por el backend).
// ============================================================

function formatearPrecio(precio) {
    return `$${Number(precio).toFixed(2)}`;
}

// ============================================================
// Badge del tab "Carrito" — contador de componentes elegidos
// ------------------------------------------------------------
// Cuenta cuántas categorías de pcBuild tienen un componente
// seleccionado (no null) y lo muestra en #carrito-badge.
// Se oculta (d-none) cuando el contador es 0.
// ============================================================
function actualizarBadgeCarrito() {
    const badge = document.getElementById("carrito-badge");
    if (!badge) return; // defensivo: si la página no tiene el badge, no romper nada

    const cantidad = Object.values(pcBuild).filter(v => v != null).length;

    badge.textContent = cantidad;
    badge.classList.toggle("d-none", cantidad === 0);
}

function actualizarResumen() {

    // El badge se actualiza SIEMPRE, incluso si el resumen no es válido
    actualizarBadgeCarrito();

    const summary = document.getElementById("pc-summary");
    const btn = document.getElementById("btn-submit");

    const aceptable =
        pcBuild.cpu &&
        pcBuild.motherboard &&
        pcBuild.ram;

    if (!aceptable) {

        summary.innerHTML = `
            <p class="text-warning">
                La PC debe contar como mínimo con:
                CPU, Motherboard y RAM.
            </p>
        `;

        btn.disabled = true;
        return;
    }

    const componentes = [
        { etiqueta: "CPU", valor: pcBuild.cpu },
        { etiqueta: "GPU", valor: pcBuild.gpu },
        { etiqueta: "Motherboard", valor: pcBuild.motherboard },
        { etiqueta: "RAM", valor: pcBuild.ram },
        { etiqueta: "Almacenamiento", valor: pcBuild.storage }
    ];

    // Total parcial = suma de precios de los componentes seleccionados
    const total = componentes.reduce(
        (suma, c) => suma + (c.valor?.precio || 0),
        0
    );

    const items = componentes.map(c => {
        const nombre = c.valor?.nombre || "No seleccionado";
        const precio = c.valor?.precio != null
            ? formatearPrecio(c.valor.precio)
            : "—";

        return `
            <li class="list-group-item bg-dark text-white d-flex justify-content-between align-items-center">
                <span>${c.etiqueta}: ${nombre}</span>
                <span class="text-info fw-bold">${precio}</span>
            </li>
        `;
    }).join("");

    summary.innerHTML = `
        <ul class="list-group">
            ${items}
            <li class="list-group-item bg-dark text-white d-flex justify-content-between align-items-center fw-bold">
                <span>Total parcial</span>
                <span class="text-success">${formatearPrecio(total)}</span>
            </li>
        </ul>
    `;

    btn.disabled = false;
}

actualizarResumen();