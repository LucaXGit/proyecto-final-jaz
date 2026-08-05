<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Drop | Detalle de Orden #{{ $orden['id'] ?? '' }}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100">
                <span class="navbar-brand mb-0 h1">Metro Drop | Detalle de Orden</span>
                <div class="d-flex flex-wrap align-items-center gap-2">
                    <a href="{{ route('ordenes.index') }}" class="btn btn-outline-light">← Mis Órdenes</a>
                    <a href="{{ route('tienda.index') }}" class="btn btn-outline-light">Catálogo</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-9">

                @if ($errors->has('general'))
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        {{ $errors->first('general') }}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
                    </div>
                @endif

                <div class="card shadow-sm">
                    <div class="card-body p-4">

                        {{-- Área de factura para PDF --}}
                        <div id="factura-pdf">
                            <div class="d-flex align-items-center justify-content-between flex-wrap gap-3 mb-4">
                                <div>
                                    <h3 class="fw-bold text-primary mb-1">Orden #{{ $orden['id'] ?? '' }}</h3>
                                    <small class="text-muted">{{ $orden['fechaCreacion'] ?? '' }}</small>
                                </div>

                                @php
                                    $estado = strtolower($orden['estado'] ?? 'completada');
                                    $badgeClass = match($estado) {
                                        'completada' => 'bg-success',
                                        'pendiente' => 'bg-warning text-dark',
                                        'cancelada' => 'bg-danger',
                                        default => 'bg-success',
                                    };
                                @endphp
                                <span class="badge {{ $badgeClass }} fs-6 px-3 py-2">{{ $orden['estado'] ?? 'Completada' }}</span>
                            </div>

                            @if (strtolower(session('usuario.rol')) === 'admin' && !empty($orden['nombreUsuario']))
                                <div class="alert alert-info mb-4">
                                    <strong>Cliente:</strong> {{ $orden['nombreUsuario'] ?? '' }} {{ $orden['apellidoUsuario'] ?? '' }}
                                </div>
                            @endif

                            <hr>

                            <h5 class="fw-bold mb-3">Productos Comprados</h5>

                            @if (!empty($orden['detalles']))
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover align-middle">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>Producto</th>
                                                <th>Talla</th>
                                                <th class="text-center">Cantidad</th>
                                                <th class="text-end">Precio Unitario</th>
                                                <th class="text-end">Subtotal</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            @foreach ($orden['detalles'] as $detalle)
                                                <tr>
                                                    <td class="fw-medium">{{ $detalle['nombreProducto'] ?? '' }}</td>
                                                    <td><span class="badge bg-secondary">{{ $detalle['talla'] ?? '' }}</span></td>
                                                    <td class="text-center">{{ $detalle['cantidad'] ?? 0 }}</td>
                                                    <td class="text-end">${{ number_format($detalle['precioUnitario'] ?? 0, 2) }}</td>
                                                    <td class="text-end fw-bold">${{ number_format($detalle['subtotal'] ?? 0, 2) }}</td>
                                                </tr>
                                            @endforeach
                                            <tr class="table-success">
                                                <td colspan="4" class="text-end fw-bold fs-5">Total</td>
                                                <td class="text-end fw-bold fs-5">${{ number_format($orden['total'] ?? 0, 2) }} MXN</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            @else
                                <p class="text-muted">No se encontraron detalles para esta orden.</p>
                            @endif
                        </div>
                        {{-- Fin del área de factura PDF --}}

                        <div class="d-flex flex-wrap gap-2 mt-4">
                            <button type="button" class="btn btn-danger" onclick="descargarPDF()">
                                📄 Descargar Factura PDF
                            </button>
                            <a href="{{ route('ordenes.index') }}" class="btn btn-outline-dark">← Volver al Historial</a>
                            <a href="{{ route('tienda.index') }}" class="btn btn-primary">Seguir Comprando</a>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer class="text-center py-4 mt-5 text-muted border-top">
        <div class="container">
            <small>&copy; {{ date('Y') }} Metro Drop. Todos los derechos reservados.</small>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
    <script>
        function descargarPDF() {
            var elemento = document.getElementById('factura-pdf');
            var opciones = {
                margin:       0.5,
                filename:     'MetroDrop_Orden_{{ $orden["id"] ?? "0" }}.pdf',
                image:        { type: 'jpeg', quality: 0.98 },
                html2canvas:  { scale: 2 },
                jsPDF:        { unit: 'in', format: 'letter', orientation: 'portrait' }
            };
            html2pdf().set(opciones).from(elemento).save();
        }
    </script>
</body>
</html>
