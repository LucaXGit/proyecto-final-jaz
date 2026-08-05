<?php

namespace App\Http\Controllers;

use App\Services\PayaraClient;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\View;

class UserController extends Controller
{
    private string $usersApiUrl;

    public function __construct()
    {
        $this->usersApiUrl = rtrim(
            (string) config('services.users.url'),
            '/'
        );
    }

    /**
     * Muestra el perfil del usuario autenticado consumiendo el backend de Payara.
     */
    public function perfil(Request $request): View|RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('GET', $this->usersApiUrl . '/me', [], $token);

        if ($respuesta['success']) {
            $usuario = $respuesta['usuario'] ?? [];
            return view('perfil', compact('usuario'));
        }

        return redirect()
            ->route('tienda.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo obtener la información de perfil.',
            ]);
    }

    /**
     * Muestra el panel de administración con la lista de usuarios.
     * Solo accesible para administradores.
     */
    public function adminPanel(Request $request): View|RedirectResponse
    {
        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('GET', $this->usersApiUrl, [], $token);

        if ($respuesta['success']) {
            $usuarios = $respuesta['usuarios'] ?? [];
            return view('admin.usuarios', compact('usuarios'));
        }

        if ($respuesta['code'] === 403) {
            return redirect()
                ->route('tienda.index')
                ->withErrors([
                    'general' => 'Acceso denegado. Se requiere rol de Administrador.',
                ]);
        }

        return redirect()
            ->route('tienda.index')
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No se pudo obtener la lista de usuarios.',
            ]);
    }

    /**
     * Actualiza el rol de un usuario.
     */
    public function actualizarRol(Request $request, $id): RedirectResponse
    {
        $datosValidados = $request->validate([
            'rol' => ['required', 'string', 'in:Admin,Usuario'],
        ]);

        $token = $request->session()->get('usuario.token');

        $respuesta = PayaraClient::request('PUT', $this->usersApiUrl . '/' . $id . '/rol', [
            'rol' => $datosValidados['rol']
        ], $token);

        if ($respuesta['success']) {
            return redirect()->route('admin.usuarios')->with('success', 'Rol actualizado correctamente.');
        }

        return redirect()->route('admin.usuarios')->withErrors(['general' => $respuesta['message'] ?? 'Error al actualizar rol.']);
    }
}