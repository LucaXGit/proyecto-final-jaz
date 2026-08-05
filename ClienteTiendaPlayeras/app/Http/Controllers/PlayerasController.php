<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

class PlayerasController extends Controller
{
private string $apiUrl;

public function __construct()
{
    $this->apiUrl = rtrim(
        (string) config('services.productos.url'),
        '/'
    );
}
    // READ: Listar productos en la tabla/tarjetas
    public function index()
    {
        $response = Http::get($this->apiUrl);
        $playeras = $response->json() ?? [];
        return view('tienda', compact('playeras'));
    }

    // CREATE: Insertar un nuevo producto
    public function store(Request $request)
    {
        Http::asForm()->post($this->apiUrl, [
            'accion' => 'crear',
            'nombre'    => $request->nombre,
            'talla'     => $request->talla,
            'precio'    => $request->precio,
            'stock'     => $request->stock,
            'imagenUrl' => $request->imagenUrl,
            'activo'    => $request->has('activo') ? 'true' : 'false'
        ]);

        return redirect('/')->with('success', '¡Playera agregada con éxito!');
    }

    // UPDATE: Modificar datos de una playera existente
    public function update(Request $request, $id)
    {
        $activo = $request->has('activo') ? 'true' : 'false';
        $imagenUrl = urlencode($request->imagenUrl ?? '');
        Http::put($this->apiUrl . "?id={$id}&nombre=" . urlencode($request->nombre) . "&talla={$request->talla}&precio={$request->precio}&stock={$request->stock}&imagenUrl={$imagenUrl}&activo={$activo}");

        return redirect('/')->with('success', '¡Playera actualizada correctamente!');
    }

    // DELETE: Eliminar un registro permanentemente
    public function destroy($id)
    {
        Http::delete($this->apiUrl . "?id={$id}");

        return redirect('/')->with('success', '¡Playera eliminada del catálogo!');
    }

    // Acción modificada: Vender una cantidad variable asegurando el tipo de dato
    public function vender(Request $request, $id)
    {
        Http::asForm()->post($this->apiUrl, [
            'accion'   => 'vender',
            'id'       => $id,
            'cantidad' => (int)$request->cantidad // Forzamos a que sea un número entero
        ]);

        return redirect('/')->with('success', '¡Venta procesada con éxito!');
    }
}