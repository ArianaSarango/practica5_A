async function cargarPruebas() {
    const contenedor = document.getElementById('tablas');
    contenedor.innerHTML = "<p style='text-align:center;'>Procesando autómatas... espere un momento.</p>";

    try {
        // Llamada al endpoint que creamos en el AutomataController
        const response = await fetch('http://localhost:8080/api/practica/ejecutar-todo');
        
        if (!response.ok) {
            throw new Error("Error al conectar con el servidor. ¿Está corriendo el Backend?");
        }

        const data = await response.json();
        contenedor.innerHTML = ""; // Limpiar mensaje de carga

        // Recorrer cada caso (Comprador, IDS, Bioinformatica)
        for (const [nombreCaso, filas] of Object.entries(data)) {
            let html = `
                <h2>Caso: ${nombreCaso.toUpperCase()}</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Cadena de Prueba</th>
                            <th>AFND (Original)</th>
                            <th>AFD (Subconjuntos)</th>
                            <th>AFD Minimizado</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            filas.forEach(fila => {
                html += `
                    <tr>
                        <td><code>"${fila.cadena}"</code></td>
                        <td class="${fila.nfa}">${fila.nfa ? 'ACEPTA' : 'RECHAZA'}</td>
                        <td class="${fila.dfa}">${fila.dfa ? 'ACEPTA' : 'RECHAZA'}</td>
                        <td class="${fila.min}">${fila.min ? 'ACEPTA' : 'RECHAZA'}</td>
                    </tr>
                `;
            });

            html += "</tbody></table>";
            contenedor.innerHTML += html;
        }
    } catch (error) {
        contenedor.innerHTML = `<p style="color:red; font-weight:bold; text-align:center;">
            ❌ ERROR: ${error.message}</p>`;
    }
    
}