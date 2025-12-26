// Estado global de la PC armada
window.pcBuild = {
    cpu: null,
    gpu: null,
    motherboard: null,
    ram: [],
    storage: []
};

// Funciones para seleccionar componentes

window.seleccionarCPU = function(nombre) {
    pcBuild.cpu = nombre;
    console.log("CPU seleccionada:", nombre);
    actualizarResumen()
};

window.seleccionarGPU = function(nombre) {
    pcBuild.gpu = nombre;
    console.log("GPU seleccionada:", nombre);
    actualizarResumen()
};

window.seleccionarMotherboard = function(nombre) {
    pcBuild.motherboard = nombre;
    console.log("Motherboard seleccionada:", nombre);
    actualizarResumen()
};

window.seleccionarRAM = function(nombre) {
    pcBuild.ram = [nombre]; // una sola selección por ahora
    console.log("RAM seleccionada:", nombre);
    actualizarResumen()
};

window.seleccionarStorage = function(nombre) {
    pcBuild.storage = [nombre];
    console.log("Storage seleccionado:", nombre);
    actualizarResumen()
};
