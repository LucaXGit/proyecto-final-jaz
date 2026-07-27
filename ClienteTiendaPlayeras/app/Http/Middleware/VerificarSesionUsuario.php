<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class VerificarSesionUsuario
{
    /**
     * Verifica que exista un usuario autenticado en la sesión Laravel.
     */
    public function handle(
        Request $request,
        Closure $next
    ): Response|RedirectResponse {
        if (!$request->session()->has('usuario.id')) {
            return redirect()
                ->route('login')
                ->withErrors([
                    'general' => 'Debes iniciar sesión para realizar esta operación.',
                ]);
        }

        return $next($request);
    }
}