<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>Metro Drop | Catálogo de Playeras</title>

    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >
</head>

<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div
                class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100"
            >
                <span class="navbar-brand mb-0 h1">
                    Metro Drop | Tienda de Playeras
                </span>

                <div class="d-flex flex-wrap align-items-center gap-2">
                    @if (session()->has('usuario.id'))
                        <a href="{{ route('carrito.index') }}" class="btn btn-light position-relative">
                            🛒 Carrito
                        </a>
                        <a href="{{ route('ordenes.index') }}" class="btn btn-outline-light">
                            📦 Mis Órdenes
                        </a>

                        <div class="dropdown">
                            <button class="btn btn-dark dropdown-toggle" type="button" id="dropdownPerfil" data-bs-toggle="dropdown" aria-expanded="false">
                                👤 {{ session('usuario.nombre') }}
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownPerfil">
                                <li><h6 class="dropdown-header">{{ session('usuario.correo') }} ({{ session('usuario.rol') }})</h6></li>
                                <li><a class="dropdown-item" href="{{ route('perfil') }}">Mi Perfil</a></li>
                                @if (strtolower(session('usuario.rol')) === 'admin')
                                <li><a class="dropdown-item" href="{{ route('admin.usuarios') }}">Panel Admin</a></li>
                                <li>
                                    <button class="dropdown-item" data-bs-toggle="modal" data-bs-target="#modalCrear" type="button">
                                        Agregar nueva playera
                                    </button>
                                </li>
                                @endif
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="{{ route('logout') }}" method="POST" class="m-0">
                                        @csrf
                                        <button type="submit" class="dropdown-item text-danger">Cerrar sesión</button>
                                    </form>
                                </li>
                            </ul>
                        </div>
                    @else
                        <a
                            href="{{ route('login') }}"
                            class="btn btn-outline-light"
                        >
                            Iniciar sesión
                        </a>

                        <a
                            href="{{ route('registro') }}"
                            class="btn btn-primary"
                        >
                            Crear cuenta
                        </a>
                    @endif
                </div>
            </div>
        </div>
    </nav>

    <div class="container">
        @if (session('success'))
            <div
                class="alert alert-success alert-dismissible fade show"
                role="alert"
            >
                {{ session('success') }}

                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="alert"
                    aria-label="Cerrar"
                ></button>
            </div>
        @endif

        @if ($errors->has('general'))
            <div
                class="alert alert-danger alert-dismissible fade show"
                role="alert"
            >
                {{ $errors->first('general') }}

                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="alert"
                    aria-label="Cerrar"
                ></button>
            </div>
        @endif

        <h2 class="mb-4 text-center">
            Catálogo de Productos Activos
        </h2>

        <div class="row">
            @forelse ($playeras as $playera)
                <div class="col-md-4 mb-4">
                    <div class="card h-100 shadow-sm">
                        <img src="{{ $playera['imagenUrl'] ?? $playera['imagen_url'] ?? 'https://via.placeholder.com/300x300?text=Sin+Imagen' }}" class="card-img-top" alt="{{ $playera['nombre'] }}" style="object-fit: cover; height: 250px;">
                        <div class="card-body">
                            <h5 class="card-title text-primary">
                                {{ $playera['nombre'] }}
                            </h5>

                            <hr>

                            <p class="card-text">
                                <strong>ID Producto:</strong>
                                #{{ $playera['id'] }}
                            </p>

                            <p class="card-text">
                                <strong>Talla:</strong>

                                <span class="badge bg-secondary">
                                    {{ $playera['talla'] }}
                                </span>
                            </p>

                            <p class="card-text">
                                <strong>Disponibles:</strong>
                                {{ $playera['stock'] }} piezas
                            </p>

                            <h4 class="text-success">
                                ${{ number_format($playera['precio'], 2) }} MXN
                            </h4>
                        </div>

                        @if (session()->has('usuario.id'))
                            <div
                                class="card-footer bg-transparent border-top-0 d-flex flex-column gap-2"
                            >
                                <form
                                    action="{{ route('carrito.agregar') }}"
                                    method="POST"
                                    class="mt-2"
                                >
                                    @csrf

                                    <input
                                        type="hidden"
                                        name="producto_id"
                                        value="{{ $playera['id'] }}"
                                    >

                                    <div class="input-group mb-2">
                                        <span class="input-group-text bg-white text-muted">
                                            Cant:
                                        </span>

                                        <input
                                            type="number"
                                            name="cantidad"
                                            class="form-control text-center"
                                            value="1"
                                            min="1"
                                            max="{{ $playera['stock'] }}"
                                            {{ $playera['stock'] <= 0 ? 'disabled' : '' }}
                                            required
                                        >
                                    </div>

                                    <button
                                        type="submit"
                                        class="btn btn-primary w-100"
                                        {{ $playera['stock'] <= 0 ? 'disabled' : '' }}
                                    >
                                        {{ $playera['stock'] <= 0
                                            ? 'Agotado'
                                            : '🛒 Agregar al Carrito' }}
                                    </button>
                                </form>

                                @if (strtolower(session('usuario.rol')) === 'admin')
                                    <div class="d-flex gap-2">
                                        <button
                                            class="btn btn-warning w-50 btn-sm"
                                            data-bs-toggle="modal"
                                            data-bs-target="#modalEditar{{ $playera['id'] }}"
                                            type="button"
                                        >
                                            Editar
                                        </button>

                                        <form
                                            action="{{ route('playeras.destroy', $playera['id']) }}"
                                            method="POST"
                                            class="w-50"
                                            onsubmit="return confirm('¿Seguro que deseas eliminar esta playera permanentemente?')"
                                        >
                                            @csrf
                                            @method('DELETE')

                                            <button
                                                type="submit"
                                                class="btn btn-danger w-100 btn-sm"
                                            >
                                                Borrar
                                            </button>
                                        </form>
                                    </div>
                                @endif
                            </div>
                        @endif
                    </div>
                </div>

                @if (session()->has('usuario.id') && strtolower(session('usuario.rol')) === 'admin')
                    <div
                        class="modal fade"
                        id="modalEditar{{ $playera['id'] }}"
                        tabindex="-1"
                        aria-hidden="true"
                    >
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form
                                    action="{{ route('playeras.update', $playera['id']) }}"
                                    method="POST"
                                >
                                    @csrf
                                    @method('PUT')

                                    <div class="modal-header">
                                        <h5 class="modal-title">
                                            Modificar Playera #{{ $playera['id'] }}
                                        </h5>

                                        <button
                                            type="button"
                                            class="btn-close"
                                            data-bs-dismiss="modal"
                                            aria-label="Cerrar"
                                        ></button>
                                    </div>

                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label
                                                for="nombre-{{ $playera['id'] }}"
                                                class="form-label"
                                            >
                                                Nombre del producto
                                            </label>

                                            <input
                                                id="nombre-{{ $playera['id'] }}"
                                                type="text"
                                                name="nombre"
                                                class="form-control"
                                                value="{{ $playera['nombre'] }}"
                                                required
                                            >
                                        </div>

                                        <div class="mb-3">
                                            <label
                                                for="talla-{{ $playera['id'] }}"
                                                class="form-label"
                                            >
                                                Talla
                                            </label>

                                            <select
                                                id="talla-{{ $playera['id'] }}"
                                                name="talla"
                                                class="form-select"
                                            >
                                                <option
                                                    value="CH"
                                                    {{ $playera['talla'] === 'CH' ? 'selected' : '' }}
                                                >
                                                    Chica (CH)
                                                </option>

                                                <option
                                                    value="M"
                                                    {{ $playera['talla'] === 'M' ? 'selected' : '' }}
                                                >
                                                    Mediana (M)
                                                </option>

                                                <option
                                                    value="G"
                                                    {{ $playera['talla'] === 'G' ? 'selected' : '' }}
                                                >
                                                    Grande (G)
                                                </option>

                                                <option
                                                    value="XG"
                                                    {{ $playera['talla'] === 'XG' ? 'selected' : '' }}
                                                >
                                                    Extra Grande (XG)
                                                </option>
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label
                                                for="precio-{{ $playera['id'] }}"
                                                class="form-label"
                                            >
                                                Precio (MXN)
                                            </label>

                                            <input
                                                id="precio-{{ $playera['id'] }}"
                                                type="number"
                                                name="precio"
                                                step="0.01"
                                                class="form-control"
                                                value="{{ $playera['precio'] }}"
                                                required
                                            >
                                        </div>

                                        <div class="mb-3">
                                            <label
                                                for="stock-{{ $playera['id'] }}"
                                                class="form-label"
                                            >
                                                Inventario
                                            </label>

                                            <input
                                                id="stock-{{ $playera['id'] }}"
                                                type="number"
                                                name="stock"
                                                class="form-control"
                                                value="{{ $playera['stock'] }}"
                                                required
                                            >
                                        </div>

                                        <div class="mb-3">
                                            <label for="imagenUrl-{{ $playera['id'] }}" class="form-label">URL de Imagen</label>
                                            <input id="imagenUrl-{{ $playera['id'] }}" type="url" name="imagenUrl" class="form-control" value="{{ $playera['imagenUrl'] ?? $playera['imagen_url'] ?? '' }}">
                                        </div>

                                        <div class="form-check mb-3">
                                            <input class="form-check-input" type="checkbox" id="activo-{{ $playera['id'] }}" name="activo" value="true" {{ (isset($playera['activo']) ? $playera['activo'] : true) ? 'checked' : '' }}>
                                            <label class="form-check-label" for="activo-{{ $playera['id'] }}">Producto Activo</label>
                                        </div>
                                    </div>

                                    <div class="modal-footer">
                                        <button
                                            type="button"
                                            class="btn btn-secondary"
                                            data-bs-dismiss="modal"
                                        >
                                            Cancelar
                                        </button>

                                        <button
                                            type="submit"
                                            class="btn btn-primary"
                                        >
                                            Guardar cambios
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                @endif
            @empty
                <div class="col-12 text-center my-5">
                    <p class="text-muted fs-4">
                        No se encontraron productos registrados en la base de datos.
                    </p>
                </div>
            @endforelse
        </div>
    </div>

    @if (session()->has('usuario.id') && strtolower(session('usuario.rol')) === 'admin')
        <div
            class="modal fade"
            id="modalCrear"
            tabindex="-1"
            aria-hidden="true"
        >
            <div class="modal-dialog">
                <div class="modal-content">
                    <form
                        action="{{ route('playeras.store') }}"
                        method="POST"
                    >
                        @csrf

                        <div class="modal-header">
                            <h5 class="modal-title">
                                Registrar Nueva Playera
                            </h5>

                            <button
                                type="button"
                                class="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Cerrar"
                            ></button>
                        </div>

                        <div class="modal-body">
                            <div class="mb-3">
                                <label
                                    for="nombre"
                                    class="form-label"
                                >
                                    Nombre de la playera
                                </label>

                                <input
                                    id="nombre"
                                    type="text"
                                    name="nombre"
                                    class="form-control"
                                    placeholder="Ej. Playera Oversize Anime"
                                    required
                                >
                            </div>

                            <div class="mb-3">
                                <label
                                    for="talla"
                                    class="form-label"
                                >
                                    Talla
                                </label>

                                <select
                                    id="talla"
                                    name="talla"
                                    class="form-select"
                                >
                                    <option value="CH">
                                        Chica (CH)
                                    </option>

                                    <option value="M" selected>
                                        Mediana (M)
                                    </option>

                                    <option value="G">
                                        Grande (G)
                                    </option>

                                    <option value="XG">
                                        Extra Grande (XG)
                                    </option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label
                                    for="precio"
                                    class="form-label"
                                >
                                    Precio (MXN)
                                </label>

                                <input
                                    id="precio"
                                    type="number"
                                    name="precio"
                                    step="0.01"
                                    class="form-control"
                                    placeholder="299.90"
                                    required
                                >
                            </div>

                            <div class="mb-3">
                                <label
                                    for="stock"
                                    class="form-label"
                                >
                                    Stock disponible
                                </label>

                                <input
                                    id="stock"
                                    type="number"
                                    name="stock"
                                    class="form-control"
                                    placeholder="20"
                                    required
                                >
                            </div>

                            <div class="mb-3">
                                <label for="imagenUrl" class="form-label">URL de Imagen</label>
                                <input id="imagenUrl" type="url" name="imagenUrl" class="form-control" placeholder="https://ejemplo.com/img.png">
                            </div>

                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" id="activo" name="activo" value="true" checked>
                                <label class="form-check-label" for="activo">Producto Activo</label>
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button
                                type="button"
                                class="btn btn-secondary"
                                data-bs-dismiss="modal"
                            >
                                Cerrar
                            </button>

                            <button
                                type="submit"
                                class="btn btn-success"
                            >
                                Guardar producto
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    @endif

    <script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
    ></script>
</body>
</html>