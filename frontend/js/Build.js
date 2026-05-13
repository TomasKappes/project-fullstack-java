// Estado global de la PC armada
window.pcBuild = {
    cpu: null,
    gpu: null,
    motherboard: null,
    ram: null,
    storage: null
};

// Funciones para seleccionar componentes



window.seleccionarCPU = function(nombre,boton) {

document.querySelectorAll(".btn-cpu").forEach(btn => {
        btn.classList.remove("btn-success");
        btn.classList.add("btn-primary");
        btn.innerHTML = ` <i class="bi bi-cart-fill"></i>
                Elegir componente`;
    });

    const id = boton.dataset.id

            pcBuild.cpu = {id:id,
            nombre:nombre}
    console.log("CPU seleccionada:", nombre);
     boton.classList.remove("btn-primary");
                  boton.classList.add("btn-success");
                  boton.innerHTML = `
                      <i class="bi bi-cart-fill"></i>
                      Producto seleccionado
                    `;



    actualizarResumen()
};

window.seleccionarGPU = function(nombre,boton) {

document.querySelectorAll(".btn-placas").forEach(btn => {
        btn.classList.remove("btn-success");
        btn.classList.add("btn-primary");
        btn.innerHTML = ` <i class="bi bi-cart-fill"></i>
                            Elegir componente`;
    });

    const id = boton.dataset.id

            pcBuild.gpu = {id:id,
            nombre:nombre}
    console.log("GPU seleccionada:", nombre);
     boton.classList.remove("btn-primary");
                  boton.classList.add("btn-success");
                  boton.innerHTML = `
                      <i class="bi bi-cart-fill"></i>
                      Producto seleccionado
                    `;



    actualizarResumen()
};

window.seleccionarMotherboard = function(nombre,boton) {

document.querySelectorAll(".btn-mother").forEach(btn => {
        btn.classList.remove("btn-success");
        btn.classList.add("btn-primary");
        btn.innerHTML = ` <i class="bi bi-cart-fill"></i>
                                Elegir componente`;
    });

    const id = boton.dataset.id

    pcBuild.motherboard = {id:id,
            nombre:nombre}
    console.log("Motherboard seleccionada:", nombre);
     boton.classList.remove("btn-primary");
                  boton.classList.add("btn-success");
                  boton.innerHTML = `
                      <i class="bi bi-cart-fill"></i>
                      Producto seleccionado
                    `;



    actualizarResumen()
};

window.seleccionarRAM = function(nombre,boton) {

document.querySelectorAll(".btn-ram").forEach(btn => {
        btn.classList.remove("btn-success");
        btn.classList.add("btn-primary");
        btn.innerHTML = ` <i class="bi bi-cart-fill"></i>
                                Elegir componente`;
    });

    const id = boton.dataset.id

        pcBuild.ram = {id:id,
        nombre:nombre} // una sola selección por ahora
    console.log("RAM seleccionada:", nombre);
    boton.classList.remove("btn-primary");
              boton.classList.add("btn-success");
              boton.innerHTML = `
                  <i class="bi bi-cart-fill"></i>
                  Producto seleccionado
                `;



    actualizarResumen()

};

window.seleccionarStorage = function(nombre,boton) {

document.querySelectorAll(".btn-alm").forEach(btn => {
        btn.classList.remove("btn-success");
        btn.classList.add("btn-primary");
        btn.innerHTML = ` <i class="bi bi-cart-fill"></i>
        Elegir componente`;
    });

    const id = boton.dataset.id

    pcBuild.storage = {id:id,
    nombre:nombre}
    ;
    console.log("Storage seleccionado:", nombre);
     boton.classList.remove("btn-primary");
                  boton.classList.add("btn-success");
                  boton.innerHTML = `
                      <i class="bi bi-cart-fill"></i>
                      Producto seleccionado
                    `;



    actualizarResumen()
};
