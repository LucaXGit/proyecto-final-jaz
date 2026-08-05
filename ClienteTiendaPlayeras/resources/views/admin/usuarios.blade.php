<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Drop | Panel de Usuarios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100">
                <span class="navbar-brand mb-0 h1">Metro Drop | Panel de Usuarios</span>
                <div class="d-flex flex-wrap align-items-center gap-2">
                    <a href="{{ route('perfil') }}" class="btn btn-outline-info">Mi Perfil</a>
                    <a href="{{ route('tienda.index') }}" class="btn btn-outline-light">Volver al Catálogo</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container mb-5">
        
        @if (session('success'))
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                {{ session('success') }}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
            </div>
        @endif

        @if ($errors->has('general'))
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                {{ $errors->first('general') }}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
            </div>
        @endif

        <div class="d-flex align-items-center justify-content-between mb-4">
            <div>
                <h2 class="fw-bold m-0">Panel de Control de Usuarios</h2>
                <p class="text-muted m-0">Visualización de usuarios registrados en el sistema bajo arquitectura RBAC.</p>
            </div>
        </div>

        <div class="card shadow-sm overflow-hidden">
            <div class="table-responsive">
                <table class="table table-striped table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th scope="col" style="width: 80px;">Usuario</th>
                            <th scope="col">ID</th>
                            <th scope="col">Nombre</th>
                            <th scope="col">Apellido</th>
                            <th scope="col">Correo Electrónico</th>
                            <th scope="col" style="width: 220px;">Rol asignado</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse ($usuarios as $u)
                            <tr>
                                <td>
                                    <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center"
                                         style="width: 40px; height: 40px; font-weight: 600;">
                                        {{ strtoupper(substr($u['nombre'] ?? 'U', 0, 1)) }}{{ strtoupper(substr($u['apellido'] ?? '', 0, 1)) }}
                                    </div>
                                </td>
                                <td><code class="text-info">#{{ $u['id'] ?? '' }}</code></td>
                                <td class="fw-medium">{{ $u['nombre'] ?? '' }}</td>
                                <td>{{ $u['apellido'] ?? '' }}</td>
                                <td class="text-muted">{{ $u['correo'] ?? '' }}</td>
                                <td>
                                    <form action="{{ route('admin.usuarios.rol', $u['id']) }}" method="POST" class="d-flex align-items-center gap-2 m-0">
                                        @csrf
                                        @method('PUT')
                                        <select name="rol" class="form-select form-select-sm" style="width: auto;">
                                            <option value="Usuario" {{ strtolower($u['rol'] ?? '') === 'usuario' ? 'selected' : '' }}>Usuario</option>
                                            <option value="Admin" {{ strtolower($u['rol'] ?? '') === 'admin' ? 'selected' : '' }}>Admin</option>
                                        </select>
                                        <button type="submit" class="btn btn-sm btn-outline-primary py-1 px-2">Actualizar</button>
                                    </form>
                                </td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <p class="text-muted m-0 fs-5">No se encontraron usuarios registrados.</p>
                                </td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>

    </div>

    <footer class="text-center py-4 mt-5 text-muted border-top">
        <div class="container">
            <small>&copy; {{ date('Y') }} Metro Drop. Todos los derechos reservados.</small>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
