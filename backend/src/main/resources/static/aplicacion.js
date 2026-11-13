const estado = { correo: null, contrasena: null, csrf: null, categorias: [], usuario: null,
  correoPorVerificar: null, catalogosRegistro: null, temporizadorAviso: null };
const porId = (id) => document.getElementById(id);

function credencialBasica() {
  const bytes = new TextEncoder().encode(`${estado.correo}:${estado.contrasena}`);
  let binario = '';
  bytes.forEach((byte) => { binario += String.fromCharCode(byte); });
  return `Basic ${btoa(binario)}`;
}

async function peticion(url, opciones = {}) {
  const encabezados = new Headers(opciones.headers || {});
  if (estado.correo) encabezados.set('Authorization', credencialBasica());
  if (opciones.method && !['GET', 'HEAD'].includes(opciones.method.toUpperCase())) {
    // Cada escritura obtiene un token vigente; el servidor puede renovar la cookie CSRF entre solicitudes.
    const respuestaCsrf = await fetch('/api/csrf', { credentials: 'same-origin' });
    if (!respuestaCsrf.ok) throw new Error('No se pudo preparar la solicitud segura.');
    estado.csrf = await respuestaCsrf.json();
    encabezados.set(estado.csrf.nombreCabecera, estado.csrf.token);
  }
  const respuesta = await fetch(url, { ...opciones, headers: encabezados, credentials: 'same-origin' });
  if (respuesta.status === 401) {
    const metodo = (opciones.method || 'GET').toUpperCase();
    throw new Error(`El servidor rechazó la solicitud ${metodo} ${url} (401). Inicia sesión de nuevo.`);
  }
  const tipo = respuesta.headers.get('content-type') || '';
  const contenido = tipo.includes('application/json') ? await respuesta.json() : null;
  if (!respuesta.ok) throw new Error(contenido?.mensaje || 'No se pudo completar la solicitud. Intenta de nuevo.');
  return contenido;
}

function mostrarAviso(texto) {
  const aviso = porId('aviso');
  aviso.textContent = texto;
  aviso.classList.add('visible');
  clearTimeout(estado.temporizadorAviso);
  estado.temporizadorAviso = setTimeout(() => aviso.classList.remove('visible'), 3200);
}

function fechaAmigable(valor) {
  return new Intl.DateTimeFormat('es-CO', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(valor));
}

function renderizarKudos(elementos) {
  const lista = porId('lista-kudos');
  porId('cantidad-kudos').textContent = elementos.length;
  if (!elementos.length) {
    lista.innerHTML = '<div class="vacio"><span class="vacio-icono" aria-hidden="true">✿</span><strong>Tu buzón está esperando su primer Kudo</strong><span>Cuando alguien te reconozca, aparecerá aquí.</span></div>';
    return;
  }
  lista.replaceChildren(...elementos.map((kudo) => {
    const tarjeta = document.createElement('article');
    tarjeta.className = 'kudo';
    const cabecera = document.createElement('div');
    cabecera.className = 'kudo-cabecera';
    const avatar = document.createElement('span');
    avatar.className = 'avatar';
    avatar.setAttribute('aria-hidden', 'true');
    avatar.textContent = (kudo.nombreAutor || '?').trim().charAt(0).toLocaleUpperCase('es-CO');
    const datos = document.createElement('div');
    const autor = document.createElement('div');
    autor.className = 'kudo-autor';
    autor.textContent = kudo.nombreAutor;
    const fecha = document.createElement('div');
    fecha.className = 'kudo-fecha';
    fecha.textContent = fechaAmigable(kudo.fechaCreacion);
    datos.append(autor, fecha);
    const categoria = document.createElement('span');
    categoria.className = 'kudo-categoria';
    categoria.textContent = kudo.nombreCategoria;
    const mensaje = document.createElement('p');
    mensaje.className = 'kudo-mensaje';
    mensaje.textContent = kudo.mensaje;
    cabecera.append(avatar, datos, categoria);
    tarjeta.append(cabecera, mensaje);
    if (kudo.adjuntos?.length) {
      const adjuntos = document.createElement('div');
      adjuntos.className = 'adjuntos-kudo';
      kudo.adjuntos.forEach((adjunto) => {
        const boton = document.createElement('button');
        boton.type = 'button';
        boton.className = 'boton-enlace';
        boton.textContent = `Descargar ${adjunto.nombreArchivo} (${Math.ceil(adjunto.tamanoBytes / 1024)} KB)`;
        boton.addEventListener('click', () => descargarAdjunto(kudo.id, adjunto).catch((error) => mostrarAviso(error.message)));
        adjuntos.append(boton);
      });
      tarjeta.append(adjuntos);
    }
    return tarjeta;
  }));
}

async function descargarAdjunto(kudoId, adjunto) {
  const encabezados = new Headers();
  encabezados.set('Authorization', credencialBasica());
  const respuesta = await fetch(`/api/kudos/${kudoId}/adjuntos/${adjunto.id}/contenido`, { headers: encabezados });
  if (!respuesta.ok) throw new Error('No se pudo descargar el archivo.');
  const url = URL.createObjectURL(await respuesta.blob());
  const enlace = document.createElement('a');
  enlace.href = url;
  enlace.download = adjunto.nombreArchivo;
  enlace.click();
  URL.revokeObjectURL(url);
}

async function cargarBuzon() {
  const resultado = await peticion('/api/kudos?pagina=0&tamano=50');
  const elementos = await Promise.all((resultado.elementos || []).map(async (kudo) => ({
    ...kudo,
    adjuntos: await peticion(`/api/kudos/${kudo.id}/adjuntos`).catch(() => [])
  })));
  renderizarKudos(elementos);
}

async function cargarCategorias() {
  estado.categorias = await peticion('/api/categorias-kudos');
  const selector = porId('categoria');
  selector.replaceChildren(new Option('Elige una categoría', ''));
  estado.categorias.forEach((categoria) => selector.add(new Option(categoria.nombre, categoria.id)));
}

async function cargarCatalogosRegistro() {
  if (estado.catalogosRegistro) return;
  const [programas, facultades] = await Promise.all([
    peticion('/api/programas-academicos'),
    peticion('/api/facultades')
  ]);
  estado.catalogosRegistro = { programas, facultades };
  const selectorPrograma = porId('registro-programa');
  programas.forEach((programa) => selectorPrograma.add(new Option(`${programa.nombre} · ${programa.facultad}`, programa.id)));
  const selectorFacultad = porId('registro-facultad');
  facultades.forEach((facultad) => selectorFacultad.add(new Option(facultad.nombre, facultad.id)));
}

function mostrarAplicacion() {
  porId('vista-acceso').classList.add('oculto');
  porId('vista-principal').classList.remove('oculto');
  porId('sesion-activa').classList.remove('oculto');
  porId('correo-sesion').textContent = estado.correo;
  porId('nombre-sesion').textContent = estado.usuario?.nombreCompleto || estado.correo.split('@')[0].replace(/[._-]+/g, ' ');
  const rolesAprobadores = ['ADMINISTRADOR_SISTEMA', 'RECTOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_FACULTAD', 'COORDINADOR_PROGRAMA'];
  porId('abrir-aprobaciones').classList.toggle('oculto', !rolesAprobadores.includes(estado.usuario?.rol));
}

async function iniciarSesion(correo, contrasena) {
  estado.correo = correo.trim().toLowerCase();
  estado.contrasena = contrasena;
  try {
    // Valida credenciales y perfil antes de mostrar la aplicación.
    await peticion('/api/kudos?pagina=0&tamano=1');
    estado.usuario = await peticion('/api/mi-cuenta');
  } catch (error) {
    estado.correo = null;
    estado.contrasena = null;
    estado.usuario = null;
    throw error;
  }
  mostrarAplicacion();
  const cargas = await Promise.allSettled([cargarBuzon(), cargarCategorias()]);
  const fallo = cargas.find((resultado) => resultado.status === 'rejected');
  if (fallo) mostrarAviso(fallo.reason.message);
}

function cerrarSesion() {
  estado.correo = null;
  estado.contrasena = null;
  estado.csrf = null;
  estado.usuario = null;
  porId('vista-principal').classList.add('oculto');
  porId('sesion-activa').classList.add('oculto');
  porId('vista-acceso').classList.remove('oculto');
  porId('formulario-acceso').reset();
}

function mostrarFormularioAcceso(formulario) {
  ['formulario-acceso', 'formulario-registro', 'formulario-verificacion'].forEach((id) => porId(id).classList.toggle('oculto', id !== formulario));
  if (formulario === 'formulario-acceso') porId('correo').focus();
}

async function cambiarAlRegistro() {
  try {
    await cargarCatalogosRegistro();
    mostrarFormularioAcceso('formulario-registro');
  } catch (error) {
    mostrarAviso(error.message);
  }
}

function ajustarAlcanceRegistro() {
  const rol = porId('registro-rol').value;
  const requierePrograma = ['ESTUDIANTE', 'DOCENTE', 'COORDINADOR_PROGRAMA'].includes(rol);
  const requiereFacultad = rol === 'COORDINADOR_FACULTAD';
  porId('campo-programa').classList.toggle('oculto', !requierePrograma);
  porId('campo-facultad').classList.toggle('oculto', !requiereFacultad);
  porId('registro-programa').required = requierePrograma;
  porId('registro-facultad').required = requiereFacultad;
}

async function registrarCuenta(evento) {
  evento.preventDefault();
  const formulario = evento.currentTarget;
  const boton = formulario.querySelector('button[type="submit"]');
  const error = porId('error-registro');
  error.classList.add('oculto');
  boton.disabled = true;
  try {
    const rol = porId('registro-rol').value;
    const solicitud = {
      nombreCompleto: porId('registro-nombre').value.trim(),
      correoInstitucional: porId('registro-correo').value.trim().toLowerCase(),
      numeroCelular: porId('registro-celular').value.trim(),
      contrasena: porId('registro-contrasena').value,
      rol,
      programaId: porId('registro-programa').value || null,
      facultadId: porId('registro-facultad').value || null
    };
    await peticion('/api/registros', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(solicitud) });
    estado.correoPorVerificar = solicitud.correoInstitucional;
    mostrarFormularioAcceso('formulario-verificacion');
  } catch (fallo) {
    error.textContent = fallo.message;
    error.classList.remove('oculto');
  } finally {
    boton.disabled = false;
  }
}

async function verificarRegistro(evento) {
  evento.preventDefault();
  const formulario = evento.currentTarget;
  const boton = formulario.querySelector('button[type="submit"]');
  const error = porId('error-verificacion');
  error.classList.add('oculto');
  boton.disabled = true;
  try {
    await peticion('/api/registros/verificar', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ correoInstitucional: estado.correoPorVerificar,
        codigoCorreo: porId('codigo-correo').value.trim(), codigoCelular: porId('codigo-celular').value.trim() })
    });
    porId('formulario-acceso').reset();
    porId('correo').value = estado.correoPorVerificar;
    porId('contrasena').value = '';
    estado.correoPorVerificar = null;
    formulario.reset();
    mostrarFormularioAcceso('formulario-acceso');
    mostrarAviso('Datos verificados. Tu solicitud queda pendiente de aprobación.');
  } catch (fallo) {
    error.textContent = fallo.message;
    error.classList.remove('oculto');
  } finally {
    boton.disabled = false;
  }
}

async function cargarAprobaciones() {
  const panel = porId('panel-aprobaciones');
  const lista = porId('lista-aprobaciones');
  panel.classList.remove('oculto');
  lista.replaceChildren();
  try {
    const solicitudes = await peticion('/api/aprobaciones/pendientes');
    if (!solicitudes.length) {
      lista.innerHTML = '<div class="vacio"><strong>No tienes solicitudes pendientes</strong><span>Cuando haya solicitudes en tu ámbito, aparecerán aquí.</span></div>';
      return;
    }
    solicitudes.forEach((solicitud) => {
      const tarjeta = document.createElement('article');
      tarjeta.className = 'solicitud-aprobacion';
      const datos = document.createElement('div');
      const nombre = document.createElement('strong');
      nombre.textContent = solicitud.nombreCompleto;
      const detalle = document.createElement('p');
      const alcance = [solicitud.facultad, solicitud.programa].filter(Boolean).join(' · ');
      detalle.textContent = `${solicitud.correoInstitucional} · ${solicitud.rol.replaceAll('_', ' ').toLocaleLowerCase('es-CO')}${alcance ? ` · ${alcance}` : ''}`;
      datos.append(nombre, detalle);
      const acciones = document.createElement('div');
      acciones.className = 'acciones-aprobacion';
      [['APROBADA', 'Aprobar', 'boton-principal'], ['RECHAZADA', 'Rechazar', 'boton-rechazar']].forEach(([decision, etiqueta, clase]) => {
        const boton = document.createElement('button');
        boton.type = 'button'; boton.className = `boton ${clase}`; boton.textContent = etiqueta;
        boton.addEventListener('click', () => decidirAprobacion(solicitud.id, decision, boton));
        acciones.append(boton);
      });
      tarjeta.append(datos, acciones);
      lista.append(tarjeta);
    });
  } catch (error) {
    lista.innerHTML = '';
    const mensaje = document.createElement('p'); mensaje.className = 'mensaje error'; mensaje.textContent = error.message; lista.append(mensaje);
  }
}

async function decidirAprobacion(id, decision, boton) {
  boton.disabled = true;
  try {
    await peticion(`/api/aprobaciones/${id}`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ decision, comentario: null })
    });
    mostrarAviso(decision === 'APROBADA' ? 'Solicitud aprobada.' : 'Solicitud rechazada.');
    await cargarAprobaciones();
  } catch (error) {
    boton.disabled = false;
    mostrarAviso(error.message);
  }
}

async function buscarDestinatarios() {
  const busqueda = porId('busqueda-destinatario').value.trim();
  const resultados = porId('resultados-destinatario');
  porId('destinatario-id').value = '';
  porId('destinatario-elegido').classList.add('oculto');
  if (busqueda.length < 2) {
    mostrarAviso('Escribe al menos dos letras para buscar.');
    return;
  }
  const personas = await peticion(`/api/usuarios?busqueda=${encodeURIComponent(busqueda)}`);
  resultados.replaceChildren();
  resultados.classList.remove('oculto');
  if (!personas.length) {
    const vacio = document.createElement('div');
    vacio.className = 'resultado-usuario';
    vacio.textContent = 'No encontramos personas con ese nombre.';
    resultados.append(vacio);
    return;
  }
  personas.forEach((persona) => {
    const boton = document.createElement('button');
    boton.type = 'button';
    boton.className = 'resultado-usuario';
    boton.setAttribute('role', 'option');
    const nombre = document.createElement('span');
    nombre.textContent = persona.nombreCompleto;
    const rol = document.createElement('small');
    rol.textContent = persona.rol.replaceAll('_', ' ').toLocaleLowerCase('es-CO');
    boton.append(nombre, rol);
    boton.addEventListener('click', () => {
      porId('destinatario-id').value = persona.id;
      porId('destinatario-elegido').textContent = `Para: ${persona.nombreCompleto}`;
      porId('destinatario-elegido').classList.remove('oculto');
      resultados.classList.add('oculto');
    });
    resultados.append(boton);
  });
}

porId('formulario-acceso').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const boton = evento.currentTarget.querySelector('button[type="submit"]');
  const error = porId('error-acceso');
  error.classList.add('oculto');
  boton.disabled = true;
  boton.textContent = 'Ingresando…';
  try {
    await iniciarSesion(porId('correo').value, porId('contrasena').value);
  } catch (fallo) {
    estado.correo = null;
    estado.contrasena = null;
    error.textContent = fallo.message;
    error.classList.remove('oculto');
  } finally {
    boton.disabled = false;
    boton.innerHTML = 'Entrar <span aria-hidden="true">→</span>';
  }
});

porId('cerrar-sesion').addEventListener('click', cerrarSesion);
porId('abrir-registro').addEventListener('click', cambiarAlRegistro);
porId('volver-acceso-desde-registro').addEventListener('click', () => mostrarFormularioAcceso('formulario-acceso'));
porId('registro-rol').addEventListener('change', ajustarAlcanceRegistro);
porId('formulario-registro').addEventListener('submit', registrarCuenta);
porId('formulario-verificacion').addEventListener('submit', verificarRegistro);
porId('buscar-destinatario').addEventListener('click', () => buscarDestinatarios().catch((error) => mostrarAviso(error.message)));
porId('busqueda-destinatario').addEventListener('keydown', (evento) => {
  if (evento.key === 'Enter') { evento.preventDefault(); buscarDestinatarios().catch((error) => mostrarAviso(error.message)); }
});
porId('actualizar-buzon').addEventListener('click', () => cargarBuzon().catch((error) => mostrarAviso(error.message)));
porId('abrir-aprobaciones').addEventListener('click', cargarAprobaciones);
porId('cerrar-aprobaciones').addEventListener('click', () => porId('panel-aprobaciones').classList.add('oculto'));
porId('mensaje-kudo').addEventListener('input', (evento) => { porId('contador-mensaje').textContent = evento.target.value.length; });
porId('formulario-kudo').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const formulario = evento.currentTarget;
  const estadoPublicacion = porId('estado-publicacion');
  estadoPublicacion.classList.add('oculto');
  const boton = porId('publicar-kudo');
  boton.disabled = true;
  try {
    const archivos = [...porId('archivos-kudo').files];
    if (archivos.length > 3) throw new Error('Cada Kudo admite hasta tres archivos.');
    if (archivos.some((archivo) => archivo.size > 5 * 1024 * 1024)) throw new Error('Cada archivo puede pesar máximo 5 MB.');
    const kudo = await peticion('/api/kudos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ destinatarioId: porId('destinatario-id').value, categoriaId: porId('categoria').value, mensaje: porId('mensaje-kudo').value })
    });
    let errorAdjunto = null;
    for (const archivo of archivos) {
      const datos = new FormData();
      datos.append('archivo', archivo);
      try {
        await peticion(`/api/kudos/${kudo.id}/adjuntos`, { method: 'POST', body: datos });
      } catch (error) {
        errorAdjunto = error.message;
        break;
      }
    }
    formulario.reset();
    porId('contador-mensaje').textContent = '0';
    porId('destinatario-id').value = '';
    porId('destinatario-elegido').classList.add('oculto');
    porId('resultados-destinatario').classList.add('oculto');
    estadoPublicacion.textContent = errorAdjunto
      ? `El Kudo se envió, pero no se pudo guardar uno de los archivos: ${errorAdjunto}`
      : 'Tu reconocimiento fue enviado. ¡Gracias por compartirlo!';
    estadoPublicacion.className = errorAdjunto ? 'mensaje error' : 'mensaje';
    mostrarAviso(errorAdjunto ? 'El Kudo se envió con un problema en los adjuntos.' : 'Kudo enviado con éxito.');
  } catch (error) {
    estadoPublicacion.textContent = error.message;
    estadoPublicacion.className = 'mensaje error';
  } finally {
    estadoPublicacion.classList.remove('oculto');
    boton.disabled = false;
  }
});

