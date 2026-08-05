<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class VerificarRolAdmin
{
    /**
     * Verifica que el usuario autenticado tenga el rol de Admin.
     */
    public function handle(Request $request, Closure $next): Response
    {
        if (!$request->session()->has('usuario.id')) {
            return redirect()
                ->route('login')
                ->withErrors([
                    'general' => 'Debes iniciar sesión para realizar esta operación.',
                ]);
        }

        $rol = $request->session()->get('usuario.rol');

        if (strtolower($rol) !== 'admin') {
            return redirect()
                ->route('tienda.index')
                ->withErrors([
                    'general' => 'Acceso denegado. Se requiere rol de Administrador.',
                ]);
        }

        return $next($request);
    }
}
