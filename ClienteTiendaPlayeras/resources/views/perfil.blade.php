<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Drop | Mi Perfil</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100">
                <span class="navbar-brand mb-0 h1">Metro Drop | Mi Perfil</span>
                <div class="d-flex flex-wrap align-items-center gap-2">
                    <a href="{{ route('tienda.index') }}" class="btn btn-outline-light">Regresar al catálogo</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container mb-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow-sm text-center">
                    <div class="card-body p-5">
                        
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3"
                             style="width: 90px; height: 90px; font-size: 32px; font-weight: 700;">
                            {{ strtoupper(substr($usuario['nombre'] ?? 'U', 0, 1)) }}{{ strtoupper(substr($usuario['apellido'] ?? '', 0, 1)) }}
                        </div>

                        <h3 class="fw-bold mb-1">{{ $usuario['nombre'] ?? '' }} {{ $usuario['apellido'] ?? '' }}</h3>
                        <div class="mb-4">
                            @if (strtolower($usuario['rol'] ?? 'usuario') === 'admin')
                                <span class="badge bg-danger fs-6 px-3 py-2">{{ $usuario['rol'] ?? 'Admin' }}</span>
                            @else
                                <span class="badge bg-success fs-6 px-3 py-2">{{ $usuario['rol'] ?? 'Usuario' }}</span>
                            @endif
                        </div>

                        <hr class="my-4">

                        <div class="text-start">
                            <div class="mb-3">
                                <small class="text-muted text-uppercase fw-bold">Identificador de Usuario</small>
                                <div class="fs-5">#{{ $usuario['id'] ?? '' }}</div>
                            </div>

                            <div class="mb-3">
                                <small class="text-muted text-uppercase fw-bold">Correo Electrónico</small>
                                <div class="fs-5">{{ $usuario['correo'] ?? '' }}</div>
                            </div>

                            <div class="mb-3">
                                <small class="text-muted text-uppercase fw-bold">Permisos del Sistema</small>
                                <div class="fs-6 mt-1">
                                    {{ strtolower($usuario['rol'] ?? 'usuario') === 'admin' 
                                        ? 'Acceso completo de administrador, gestión del catálogo e inventario y visualización de usuarios' 
                                        : 'Acceso estándar de cliente, compra de playeras en catálogo' }}
                                </div>
                            </div>
                        </div>

                        <hr class="my-4">

                        <div class="d-grid gap-2">
                            @if (strtolower($usuario['rol'] ?? 'usuario') === 'admin')
                                <a href="{{ route('admin.usuarios') }}" class="btn btn-primary py-2 fw-bold">Gestionar Usuarios</a>
                            @endif
                            <form action="{{ route('logout') }}" method="POST" class="m-0">
                                @csrf
                                <button type="submit" class="btn btn-outline-danger w-100 py-2 fw-bold">Cerrar Sesión</button>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer class="text-center py-4 mt-auto text-muted border-top">
        <div class="container">
            <small>&copy; {{ date('Y') }} Metro Drop. Todos los derechos reservados.</small>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
