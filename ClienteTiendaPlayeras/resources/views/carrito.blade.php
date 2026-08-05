<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Drop | Mi Carrito</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100">
                <span class="navbar-brand mb-0 h1">Metro Drop | Mi Carrito</span>
                <div class="d-flex flex-wrap align-items-center gap-2">
                    <a href="{{ route('tienda.index') }}" class="btn btn-outline-light">Catálogo</a>
                    <a href="{{ route('ordenes.index') }}" class="btn btn-outline-light">Mis Órdenes</a>
                    <a href="{{ route('perfil') }}" class="btn btn-outline-info">Mi Perfil</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container">

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
                <h2 class="fw-bold m-0">Mi Carrito</h2>
                <p class="text-muted m-0">{{ $totalItems ?? 0 }} {{ ($totalItems ?? 0) === 1 ? 'producto' : 'productos' }} en tu carrito</p>
            </div>
            @if (!empty($items))
                <form action="{{ route('carrito.vaciar') }}" method="POST"
                      onsubmit="return confirm('¿Seguro que deseas vaciar todo el carrito?')">
                    @csrf
                    @method('DELETE')
                    <button type="submit" class="btn btn-outline-danger btn-sm">Vaciar carrito</button>
                </form>
            @endif
        </div>

        @if (!empty($items))
            <div class="row">
                <div class="col-lg-8">
                    @foreach ($items as $item)
                        <div class="card shadow-sm mb-3">
                            <div class="card-body">
                                <div class="d-flex align-items-center gap-4">
                                    <div class="bg-primary text-white rounded d-flex align-items-center justify-content-center flex-shrink-0"
                                         style="width: 55px; height: 55px; font-size: 22px;">
                                        👕
                                    </div>

                                    <div class="flex-grow-1">
                                        <h5 class="fw-bold mb-1">{{ $item['nombreProducto'] ?? '' }}</h5>
                                        <div class="d-flex align-items-center gap-3 mb-2">
                                            <span class="badge bg-secondary">{{ $item['talla'] ?? '' }}</span>
                                            <span class="text-muted">
                                                ${{ number_format($item['precio'] ?? 0, 2) }} MXN c/u
                                            </span>
                                        </div>

                                        <div class="d-flex align-items-center justify-content-between mt-3">
                                            <form action="{{ route('carrito.actualizar', $item['id']) }}" method="POST"
                                                  class="d-flex align-items-center gap-2">
                                                @csrf
                                                @method('PUT')
                                                <button type="submit" name="cantidad"
                                                        value="{{ max(1, ($item['cantidad'] ?? 1) - 1) }}"
                                                        class="btn btn-outline-secondary btn-sm">−</button>
                                                <input type="number" name="cantidad" value="{{ $item['cantidad'] ?? 1 }}"
                                                       min="1" max="{{ $item['stock'] ?? 99 }}"
                                                       class="form-control form-control-sm text-center" style="width: 60px;" readonly>
                                                <button type="submit" name="cantidad"
                                                        value="{{ ($item['cantidad'] ?? 1) + 1 }}"
                                                        class="btn btn-outline-secondary btn-sm"
                                                        {{ ($item['cantidad'] ?? 1) >= ($item['stock'] ?? 99) ? 'disabled' : '' }}>+</button>
                                            </form>

                                            <div class="d-flex align-items-center gap-3">
                                                <span class="fw-bold text-primary fs-5">
                                                    ${{ number_format($item['subtotal'] ?? 0, 2) }}
                                                </span>

                                                <form action="{{ route('carrito.eliminar', $item['id']) }}" method="POST">
                                                    @csrf
                                                    @method('DELETE')
                                                    <button type="submit" class="btn btn-outline-danger btn-sm" title="Eliminar">✕</button>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    @endforeach
                </div>

                <div class="col-lg-4">
                    <div class="card shadow-sm sticky-top" style="top: 20px;">
                        <div class="card-body">
                            <h5 class="fw-bold mb-3">Resumen de Compra</h5>

                            <div class="d-flex justify-content-between py-2 border-bottom">
                                <span class="text-muted">Productos ({{ $totalItems ?? 0 }})</span>
                                <span>${{ number_format($total ?? 0, 2) }}</span>
                            </div>

                            <div class="d-flex justify-content-between py-2 border-bottom">
                                <span class="text-muted">Envío</span>
                                <span class="text-success fw-semibold">Gratis</span>
                            </div>

                            <div class="d-flex justify-content-between py-3">
                                <span class="fw-bold fs-5">Total</span>
                                <span class="fw-bold fs-5 text-success">${{ number_format($total ?? 0, 2) }}</span>
                            </div>

                            <form action="{{ route('ordenes.checkout') }}" method="POST">
                                @csrf
                                <button type="submit" class="btn btn-success w-100 fw-bold py-2">
                                    Confirmar Compra
                                </button>
                            </form>

                            <a href="{{ route('tienda.index') }}" class="btn btn-outline-dark w-100 mt-2">
                                Seguir comprando
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        @else
            <div class="card shadow-sm">
                <div class="card-body text-center py-5">
                    <div class="mb-3" style="font-size: 48px;">🛒</div>
                    <h3 class="fw-bold mb-2">Tu carrito está vacío</h3>
                    <p class="text-muted mb-4">¡Explora nuestro catálogo y encuentra las mejores playeras!</p>
                    <a href="{{ route('tienda.index') }}" class="btn btn-primary">Ir al Catálogo</a>
                </div>
            </div>
        @endif
    </div>

    <footer class="text-center py-4 mt-5 text-muted border-top">
        <div class="container">
            <small>&copy; {{ date('Y') }} Metro Drop. Todos los derechos reservados.</small>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
