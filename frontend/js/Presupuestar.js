
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

    try {

        const response = await fetch("http://localhost:8080/pedidos/crear", {

            method: "POST",

            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },

            body: JSON.stringify(pedidoDTO)

        });

        if (!response.ok) {
            throw new Error("Error al enviar presupuesto");
        }

        const data = await response.json();

        console.log("Respuesta backend:", data);

        alert("Presupuesto enviado correctamente");

        pedidoCreado = true;
        pedidoId = data.idPedido;

        const boton = document.getElementById("btn-submit");

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

        alert("Ocurrió un error");

    }

};

async function confirmarPedido() {

const token = localStorage.getItem("token");

    const response = await fetch(
        `http://localhost:8080/pedidos/confirmar/${pedidoId}`,
        {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    if(response.ok){

        alert("Pedido confirmado");

    }

    const boton = document.getElementById("btn-submit");

    boton.textContent = "Confirmado!"

    boton.style.backgroundColor = "red"

    boton.disabled = true;

}