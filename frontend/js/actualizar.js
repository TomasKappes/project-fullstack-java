function actualizarResumen() {
    const summary = document.getElementById("pc-summary");
    const btn = document.getElementById("btn-submit");

    const completo =
        pcBuild.cpu &&
        pcBuild.gpu &&
        pcBuild.motherboard
        pcBuild.ram.length > 0 &&
        pcBuild.storage.length > 0;

    if (!completo) {
        summary.innerHTML = `<p class="text-warning">Faltan componentes.</p>`;
        btn.disabled = true;
        return;
    }

    summary.innerHTML = `
        <ul class="list-group">
            <li class="list-group-item bg-dark text-white">CPU: ${pcBuild.cpu}</li>
            <li class="list-group-item bg-dark text-white">GPU: ${pcBuild.gpu}</li>
            <li class="list-group-item bg-dark text-white">Motherboard: ${pcBuild.motherboard}</li>
            <li class="list-group-item bg-dark text-white">RAM: ${pcBuild.ram.join(", ")}</li>
            <li class="list-group-item bg-dark text-white">Almacenamiento: ${pcBuild.storage.join(", ")}</li>
        </ul>
    `;

    btn.disabled = false;
}

actualizarResumen();



