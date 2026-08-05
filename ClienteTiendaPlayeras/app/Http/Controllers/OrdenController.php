<?php

namespace App\Http\Controllers;

use App\Services\PayaraClient;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\View;

class OrdenController extends Controller
{
    private string $ordenesApiUrl;

    public function __construct()
    {
        $this->ordenesApiUrl = rtrim(
            (string) config('services.ordenes.url'),
            '/'
        );
    }

    /**
     * Procesa el checkout: crea una orden a partir del carrito.
     */
    public function checkout(Request $request): View|RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('POST', $this->ordenesApiUrl, [], $token);

        if ($respuesta['success']) {
            $orden = $respuesta['orden'] ?? [];

            return view('ordenes.confirmacion', compact('orden'));
        }

        return redirect()
            ->route('carrito.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo procesar la compra.',
            ]);
    }

    /**
     * Muestra el historial de órdenes del usuario.
     * El admin ve todas las órdenes del sistema.
     */
    public function misOrdenes(Request $request): View|RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('GET', $this->ordenesApiUrl, [], $token);

        if ($respuesta['success']) {
            $ordenes = $respuesta['ordenes'] ?? [];

            return view('ordenes.index', compact('ordenes'));
        }

        return redirect()
            ->route('tienda.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo obtener el historial de órdenes.',
            ]);
    }

    /**
     * Muestra el detalle completo de una orden.
     */
    public function detalle(Request $request, $id): View|RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('GET', $this->ordenesApiUrl . '/' . $id, [], $token);

        if ($respuesta['success']) {
            $orden = $respuesta['orden'] ?? [];

            return view('ordenes.detalle', compact('orden'));
        }

        if (($respuesta['code'] ?? 0) === 403) {
            return redirect()
                ->route('ordenes.index')
                ->withErrors([
                    'general' => 'No tienes permiso para ver esta orden.',
                ]);
        }

        return redirect()
            ->route('ordenes.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo obtener el detalle de la orden.',
            ]);
    }
}
