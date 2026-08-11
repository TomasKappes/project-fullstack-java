// ============================================================
// Build.js — Selección de componentes para la PC
// ------------------------------------------------------------
// Estado global: window.pcBuild guarda {id, nombre, precio}
// por cada categoría. Cada función seleccionarX recibe
// (nombre, boton, precio) desde el onclick del HTML.
// ============================================================

// Estado global de la PC armada
window.pcBuild = {
    cpu: null,
    gpu: null,
    motherboard: null,
    ram: null,
    storage: null
};

// Helper compartido: evita repetir la misma lógica 5 veces.
// - clave:         propiedad de pcBuild a setear (cpu, gpu, ...)
// - selectorClase: clase CSS de los botones de esa categoría
// - nombre:        nombre del producto (viene del onclick)
// - boton:         botón clickeado (this)
// - precio:        precio del producto (viene del onclick)
function seleccionarComponente(clave, selectorClase, nombre, boton, precio) {

    // 1) Resetear todos los botones de la categoría a su estado inicial
    document.querySelectorAll(selectorClase).forEach(btn => {
        btn.classList.remove("btn-success");
        btn.classList.add("btn-primary");
        btn.innerHTML = `<i class="bi bi-cart-fill"></i> Elegir componente`;

        // Quitar el borde neón de la card de esa categoría
        const card = btn.closest(".card");
        if (card) {
            card.classList.remove("card-seleccionada");
        }
    });

    // 2) Guardar la selección en el estado global
    const id = boton.dataset.id;
    pcBuild[clave] = { id: id, nombre: nombre, precio: precio };

    console.log(`${clave} seleccionado:`, nombre);

    // 3) Marcar el botón clickeado como seleccionado
    boton.classList.remove("btn-primary");
    boton.classList.add("btn-success");
    boton.innerHTML = `<i class="bi bi-cart-fill"></i> Producto seleccionado`;

    // 3b) Marcar la card clickeada con el borde neón
    const cardSeleccionada = boton.closest(".card");
    if (cardSeleccionada) {
        cardSeleccionada.classList.add("card-seleccionada");
    }

    // 4) Refrescar el resumen del carrito
    actualizarResumen();
}

// Funciones públicas usadas por los onclick del HTML
window.seleccionarCPU = function (nombre, boton, precio) {
    seleccionarComponente("cpu", ".btn-cpu", nombre, boton, precio);
};

window.seleccionarGPU = function (nombre, boton, precio) {
    seleccionarComponente("gpu", ".btn-placas", nombre, boton, precio);
};

window.seleccionarMotherboard = function (nombre, boton, precio) {
    seleccionarComponente("motherboard", ".btn-mother", nombre, boton, precio);
};

window.seleccionarRAM = function (nombre, boton, precio) {
    seleccionarComponente("ram", ".btn-ram", nombre, boton, precio);
};

window.seleccionarStorage = function (nombre, boton, precio) {
    seleccionarComponente("storage", ".btn-alm", nombre, boton, precio);
};