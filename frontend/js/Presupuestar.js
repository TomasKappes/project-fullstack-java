document.getElementById("contactForm").addEventListener("submit", async (e) => {

    e.preventDefault();

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

    } catch (error) {

        console.error(error);

        alert("Ocurrió un error");

    }

});