const form = document.querySelector("form");

form.addEventListener("submit", function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // Validación básica
    if (!email || !password) {
        alert("Por favor completa todos los campos");
        return;
    }

    
    console.log("Login exitoso con:", email);

    // Redirigir a menu.html
    window.location.href = "menu.html";
});