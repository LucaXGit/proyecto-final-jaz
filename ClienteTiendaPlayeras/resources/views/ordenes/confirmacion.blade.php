<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Metro Drop | Orden Confirmada</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 w-100">
                <span class="navbar-brand mb-0 h1">Metro Drop | Orden Confirmada</span>
                <div class="d-flex flex-wrap align-items-center gap-2">
                    <a href="{{ route('tienda.index') }}" class="btn btn-outline-light">Catálogo</a>
                    <a href="{{ route('ordenes.index') }}" class="btn btn-outline-warning">Mis Órdenes</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card shadow-sm text-center">
                    <div class="card-body p-5">

                        <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center mx-auto mb-4"
                             style="width: 80px; height: 80px; font-size: 36px;">
                            ✓
                        </div>

                        <h2 class="fw-bold mb-2">¡Compra Realizada!</h2>
                        <p class="text-muted mb-4">Tu pedido ha sido procesado exitosamente.</p>

                        <div class="mb-4">
                            <span class="text-muted">Número de Orden</span>
                            <div class="text-primary fw-bold" style="font-size: 28px;">#{{ $orden['id'] ?? '' }}</div>
                        </div>

                        {{-- Área de factura para PDF --}}
                        <div id="factura-pdf">
                            <div class="text-center mb-3">
                                <h4 class="fw-bold">Metro Drop — Comprobante de Compra</h4>
                                <p class="text-muted mb-0">Orden #{{ $orden['id'] ?? '' }}</p>
                            </div>

                            @if (!empty($orden['detalles']))
                                <div class="table-responsive mb-4">
                                    <table class="table table-striped table-hover align-middle">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>Producto</th>
                                                <th>Talla</th>
                                                <th class="text-center">Cant.</th>
                                                <th class="text-end">Precio</th>
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
                                        </tbody>
                                    </table>
                                </div>
                            @endif

                            <div class="mb-3">
                                <span class="text-muted">Total Pagado</span>
                                <div class="text-success fw-bold" style="font-size: 24px;">
                                    ${{ number_format($orden['total'] ?? 0, 2) }} MXN
                                </div>
                            </div>
                        </div>
                        {{-- Fin del área de factura PDF --}}

                        <hr class="my-4">

                        <div class="d-flex flex-column flex-sm-row gap-3 justify-content-center">
                            <button type="button" class="btn btn-danger" onclick="descargarPDF()">
                                📄 Descargar Factura PDF
                            </button>
                            <a href="{{ route('ordenes.index') }}" class="btn btn-primary">Ver Mis Órdenes</a>
                            <a href="{{ route('tienda.index') }}" class="btn btn-outline-dark">Seguir Comprando</a>
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
