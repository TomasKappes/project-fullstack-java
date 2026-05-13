function actualizarResumen() {

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

    summary.innerHTML = `
        <ul class="list-group">

            <li class="list-group-item bg-dark text-white">
                CPU: ${pcBuild.cpu?.nombre || "No seleccionado"}
            </li>

            <li class="list-group-item bg-dark text-white">
                GPU: ${pcBuild.gpu?.nombre || "No seleccionado"}
            </li>

            <li class="list-group-item bg-dark text-white">
                Motherboard: ${pcBuild.motherboard?.nombre || "No seleccionado"}
            </li>

            <li class="list-group-item bg-dark text-white">
                RAM: ${pcBuild.ram?.nombre || "No seleccionado"}
            </li>

            <li class="list-group-item bg-dark text-white">
                Almacenamiento: ${pcBuild.storage?.nombre || "No seleccionado"}
            </li>

        </ul>
    `;

    btn.disabled = false;
}

actualizarResumen();

