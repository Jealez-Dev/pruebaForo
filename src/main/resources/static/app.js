const apiUrl = '/api/hilos';
const listaHilos = document.querySelector('#listaHilos');
const estado = document.querySelector('#estado');
const busqueda = document.querySelector('#busqueda');
const filtroCategoria = document.querySelector('#filtroCategoria');
const dialogo = document.querySelector('#dialogoHilo');
const formulario = document.querySelector('#formularioHilo');
const errorFormulario = document.querySelector('#errorFormulario');

let debounceId;

const escapeHtml = (value) => String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');

const formatearFecha = (fecha) => new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short'
}).format(new Date(fecha));

const mostrarEstado = (mensaje, esError = false) => {
    estado.textContent = mensaje;
    estado.style.color = esError ? '#b3261e' : '';
};

const pintarHilos = (hilos) => {
    if (!hilos.length) {
        listaHilos.innerHTML = '<p class="empty">No hay hilos que coincidan con la búsqueda.</p>';
        return;
    }

    listaHilos.innerHTML = hilos.map((hilo) => `
        <article class="thread">
            <div class="vote-rail" aria-label="Votos">
                <button class="vote-button" type="button" aria-label="Votar positivo">▲</button>
                <span>0</span>
                <button class="vote-button" type="button" aria-label="Votar negativo">▼</button>
            </div>
            <div>
                <p class="thread-meta">f/foro · ${escapeHtml(hilo.autor)} · ${formatearFecha(hilo.fechaCreacion)}</p>
                <h2>${escapeHtml(hilo.titulo)}</h2>
                <p>${escapeHtml(hilo.contenido)}</p>
                <p class="thread-meta">
                    <span class="badge">${escapeHtml(hilo.categoria)}</span>
                    ${escapeHtml(hilo.autor)} · ${formatearFecha(hilo.fechaCreacion)}
                </p>
            </div>
            <div class="thread-actions">
                <button class="button button-muted button-small" data-editar="${hilo.id}" type="button">Editar</button>
                <button class="button button-small" data-eliminar="${hilo.id}" type="button">Eliminar</button>
            </div>
        </article>
    `).join('');
};

const cargarHilos = async () => {
    const parametros = new URLSearchParams();
    const texto = busqueda.value.trim();
    if (texto) parametros.set('q', texto);
    if (filtroCategoria.value) parametros.set('categoria', filtroCategoria.value);

    mostrarEstado('Buscando...');
    try {
        const ruta = parametros.size ? `${apiUrl}/buscar?${parametros}` : apiUrl;
        const respuesta = await fetch(ruta);
        if (!respuesta.ok) throw new Error('No se pudieron cargar los hilos.');
        pintarHilos(await respuesta.json());
        mostrarEstado('');
    } catch (error) {
        mostrarEstado(error.message, true);
    }
};

const abrirFormulario = (hilo = null) => {
    formulario.reset();
    errorFormulario.textContent = '';
    document.querySelector('#hiloId').value = hilo?.id ?? '';
    document.querySelector('#tituloFormulario').textContent = hilo ? 'Editar hilo' : 'Nuevo hilo';
    if (hilo) {
        document.querySelector('#titulo').value = hilo.titulo;
        document.querySelector('#contenido').value = hilo.contenido;
        document.querySelector('#autor').value = hilo.autor;
        document.querySelector('#categoria').value = hilo.categoria;
    }
    dialogo.showModal();
};

const obtenerHilo = async (id) => {
    const respuesta = await fetch(`${apiUrl}/${id}`);
    if (!respuesta.ok) throw new Error('No se pudo obtener el hilo.');
    return respuesta.json();
};

formulario.addEventListener('submit', async (event) => {
    event.preventDefault();
    const id = document.querySelector('#hiloId').value;
    const datos = Object.fromEntries(new FormData(formulario));
    delete datos.method;

    try {
        const respuesta = await fetch(id ? `${apiUrl}/${id}` : apiUrl, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });
        if (!respuesta.ok) throw new Error('Revisa los datos del formulario.');
        dialogo.close();
        await cargarHilos();
    } catch (error) {
        errorFormulario.textContent = error.message;
    }
});

listaHilos.addEventListener('click', async (event) => {
    const editar = event.target.closest('[data-editar]');
    const eliminar = event.target.closest('[data-eliminar]');

    try {
        if (editar) abrirFormulario(await obtenerHilo(editar.dataset.editar));
        if (eliminar && window.confirm('¿Eliminar este hilo?')) {
            const respuesta = await fetch(`${apiUrl}/${eliminar.dataset.eliminar}`, { method: 'DELETE' });
            if (!respuesta.ok) throw new Error('No se pudo eliminar el hilo.');
            await cargarHilos();
        }
    } catch (error) {
        mostrarEstado(error.message, true);
    }
});

busqueda.addEventListener('input', () => {
    clearTimeout(debounceId);
    debounceId = setTimeout(cargarHilos, 300);
});
filtroCategoria.addEventListener('change', cargarHilos);
document.querySelector('#nuevoHilo').addEventListener('click', () => abrirFormulario());
document.querySelector('#cerrarDialogo').addEventListener('click', () => dialogo.close());
document.querySelector('#cancelarDialogo').addEventListener('click', () => dialogo.close());

cargarHilos();
