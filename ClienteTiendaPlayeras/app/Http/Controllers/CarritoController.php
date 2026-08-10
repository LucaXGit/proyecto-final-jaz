<?php

namespace App\Http\Controllers;

use App\Services\PayaraClient;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\View;

class CarritoController extends Controller
{
    private string $carritoApiUrl;

    public function __construct()
    {
        $this->carritoApiUrl = rtrim(
            (string) config('services.carrito.url'),
            '/'
        );
    }

    /**
     * Muestra el carrito del usuario autenticado.
     */
    public function index(Request $request): View|RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('GET', $this->carritoApiUrl, [], $token);

        if ($respuesta['success']) {
            $items = $respuesta['items'] ?? [];
            $total = $respuesta['total'] ?? 0;
            $totalItems = $respuesta['totalItems'] ?? 0;

            return view('carrito', compact('items', 'total', 'totalItems'));
        }

        return redirect()
            ->route('tienda.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo obtener el carrito.',
            ]);
    }

    /**
     * Agrega un producto al carrito.
     */
    public function agregar(Request $request): RedirectResponse
    {
        $datosValidados = $request->validate([
            'producto_id' => ['required', 'string', 'size:24'],
            'cantidad' => ['required', 'integer', 'min:1'],
        ]);

        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('POST', $this->carritoApiUrl, [
            'productoId' => $datosValidados['producto_id'],
            'cantidad' => (int) $datosValidados['cantidad'],
        ], $token);

        if ($respuesta['success']) {
            return redirect()
                ->route('tienda.index')
                ->with(
                    'success',
                    $respuesta['message'] ?? '¡Producto agregado al carrito!'
                );
        }

        return redirect()
            ->route('tienda.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo agregar el producto al carrito.',
            ]);
    }

    /**
     * Actualiza la cantidad de un item en el carrito.
     */
    public function actualizar(Request $request, $id): RedirectResponse
    {
        $datosValidados = $request->validate([
            'cantidad' => ['required', 'integer', 'min:1'],
        ]);

        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('PUT', $this->carritoApiUrl . '/' . $id, [
            'cantidad' => (int) $datosValidados['cantidad'],
        ], $token);

        if ($respuesta['success']) {
            return redirect()
                ->route('carrito.index')
                ->with('success', 'Cantidad actualizada.');
        }

        return redirect()
            ->route('carrito.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo actualizar la cantidad.',
            ]);
    }

    /**
     * Elimina un item del carrito.
     */
    public function eliminar(Request $request, $id): RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('DELETE', $this->carritoApiUrl . '/' . $id, [], $token);

        if ($respuesta['success']) {
            return redirect()
                ->route('carrito.index')
                ->with('success', 'Producto eliminado del carrito.');
        }

        return redirect()
            ->route('carrito.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo eliminar el producto.',
            ]);
    }

    /**
     * Vacía todo el carrito.
     */
    public function vaciar(Request $request): RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('DELETE', $this->carritoApiUrl, [], $token);

        if ($respuesta['success']) {
            return redirect()
                ->route('carrito.index')
                ->with('success', 'Carrito vaciado correctamente.');
        }

        return redirect()
            ->route('carrito.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo vaciar el carrito.',
            ]);
    }
}
