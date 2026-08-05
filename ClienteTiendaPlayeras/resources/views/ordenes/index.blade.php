<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Drop | Mis Órdenes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100">
                <span class="navbar-brand mb-0 h1">
                    Metro Drop | {{ strtolower(session('usuario.rol')) === 'admin' ? 'Todas las Órdenes' : 'Mis Órdenes' }}
                </span>
                <div class="d-flex flex-wrap align-items-center gap-2">
                    <a href="{{ route('carrito.index') }}" class="btn btn-outline-light">🛒 Carrito</a>
                    <a href="{{ route('tienda.index') }}" class="btn btn-outline-light">Catálogo</a>
                    <a href="{{ route('perfil') }}" class="btn btn-outline-info">Mi Perfil</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container">

        @if ($errors->has('general'))
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                {{ $errors->first('general') }}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
            </div>
        @endif

        <h2 class="fw-bold mb-1">Historial de Compras</h2>
        <p class="text-muted mb-4">Consulta el detalle de cada pedido realizado.</p>

        @forelse ($ordenes as $orden)
            <a href="{{ route('ordenes.detalle', $orden['id']) }}"
               class="card shadow-sm mb-3 text-decoration-none text-dark">
                <div class="card-body">
                    <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                        <div>
                            <h5 class="fw-bold text-primary mb-1">Orden #{{ $orden['id'] ?? '' }}</h5>
                            <small class="text-muted">{{ $orden['fechaCreacion'] ?? '' }}</small>
                            @if (strtolower(session('usuario.rol')) === 'admin' && !empty($orden['nombreUsuario']))
                                <div class="mt-1">
                                    <small class="text-secondary">
                                        👤 {{ $orden['nombreUsuario'] }} {{ $orden['apellidoUsuario'] ?? '' }}
                                    </small>
                                </div>
                            @endif
                        </div>

                        <div class="d-flex align-items-center gap-3">
                            @php
                                $estado = strtolower($orden['estado'] ?? 'completada');
                                $badgeClass = match($estado) {
                                    'completada' => 'bg-success',
                                    'pendiente' => 'bg-warning text-dark',
                                    'cancelada' => 'bg-danger',
                                    default => 'bg-success',
                                };
                            @endphp
                            <span class="badge {{ $badgeClass }}">{{ $orden['estado'] ?? 'Completada' }}</span>
                            <span class="fw-bold text-success fs-5">${{ number_format($orden['total'] ?? 0, 2) }}</span>
                            <span class="text-muted">→</span>
                        </div>
                    </div>
                </div>
            </a>
        @empty
            <div class="card shadow-sm">
                <div class="card-body text-center py-5">
                    <div class="mb-3" style="font-size: 48px;">📦</div>
                    <h3 class="fw-bold mb-2">Sin órdenes aún</h3>
                    <p class="text-muted mb-4">Realiza tu primera compra desde nuestro catálogo.</p>
                    <a href="{{ route('tienda.index') }}" class="btn btn-primary">Ir al Catálogo</a>
                </div>
            </div>
        @endforelse

    </div>

    <footer class="text-center py-4 mt-5 text-muted border-top">
        <div class="container">
            <small>&copy; {{ date('Y') }} Metro Drop. Todos los derechos reservados.</small>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
